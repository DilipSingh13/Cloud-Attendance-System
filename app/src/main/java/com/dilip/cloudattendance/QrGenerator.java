package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.helper.Functions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class QrGenerator extends AppCompatActivity {

    Button Stop_Attn;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    int min = 0;
    int max = 999999999;
    int random=0;
    String rand,email;

    private TextView Name, Lecture, Semester, Id , Email, Batch;
    private Button Save;
    private ImageView QrImage;

    private static final long MAIN_START_TIME_IN_MILLIS = 10000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis;
    private long mEndTime;
    private ProgressDialog pDialog;
    private static final String TAG = StudentHomeActivity.class.getSimpleName();
    private String lect,result,grant_no="no",lecture,id,semi,bat,fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_generator);
        QrImage = findViewById(R.id.qr_image);
        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Batch = findViewById(R.id.batch);
        Semester = findViewById(R.id.sem);
        Lecture=findViewById(R.id.lec);
        Id=findViewById(R.id.id_no);
        Stop_Attn=findViewById(R.id.stop);

        if (!NetConnectivityCheck.isNetworkAvailable(QrGenerator.this)) {
            ViewDialogNet alert = new QrGenerator.ViewDialogNet();
            alert.showDialog(QrGenerator.this, "Please, enable internet connection before using this app !!");
            Intent intent=new Intent(getApplicationContext(),QrGenerator.class);
            stopService(intent);
        }

        Intent in= getIntent();
        Bundle b = in.getExtras();

        if(b!=null)
        {
            result =(String) b.get("response");
            email =(String) b.get("email");
            lect =(String) b.get("lect");
            lecture =(String) b.get("lecture");
            fullname =(String) b.get("fullname");
            id =(String) b.get("id");
            bat =(String) b.get("batch");
            semi =(String) b.get("sem");

            Name.setText(fullname);
            Id.setText(id);
            Lecture.setText(lect);
            Email.setText(email);
            Batch.setText(bat);
            Semester.setText(semi);

            if (result.equals("Attendance already granted")){
                QrGen();
                //Toast.makeText(QrGenerator.this, lecture, Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
            else if (result.equals("Attendance grant sucessfully!")){
                QrGen();
                //Toast.makeText(QrGenerator.this, lecture, Toast.LENGTH_SHORT).show();
                //Toast.makeText(QrGenerator.this, email, Toast.LENGTH_SHORT).show();
            }
        }

        // Progress dialog
        pDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        Stop_Attn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStop();
                Stop_Password();
            }
        });

    }

    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                pushQRcode(lecture,rand,email);
            }
        }.start();

        mTimerRunning = true;
    }

    void QrGen(){
        try {
            random = new Random().nextInt((max - min) - 1) + min;
            rand = Integer.toString(random);
            bitmap = TextToImageEncode(rand);
            //Toast.makeText(QrGenerator.this, rand, Toast.LENGTH_SHORT).show();
            QrImage.setImageBitmap(bitmap);
            startTimer();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void pushQRcode(final String lecture, final String rand,final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_reset_pass";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.UPDATE_QR_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Reset Password Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        String res=jObj.getString("message");
                        if ("Updated".equals(res)) {
                            QrGen();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Password Response Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("lecture", lecture);
                params.put("code", rand);
                params.put("email", email);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

        };
        // Adding request to volley request queue
        strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
        strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void resetTimer() {
        mTimeLeftInMillis = MAIN_START_TIME_IN_MILLIS;
    }
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mTimeLeftInMillis = prefs.getLong("millisLeft", MAIN_START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                resetTimer();
            } else {
                startTimer();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void Stop_Password() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.stop_qr_password, null);

        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Enter Your Password");
        dialogBuilder.setCancelable(false);

        final TextInputLayout EditPassword = dialogView.findViewById(R.id.editPassword);

        dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // empty
            }
        });

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog alertDialog = dialogBuilder.create();

        EditPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (EditPassword.getEditText().getText().length() > 0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setEnabled(false);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String password = EditPassword.getEditText().getText().toString();

                        if (!password.isEmpty()) {
                                //Toast.makeText(QrGenerator.this, password, Toast.LENGTH_SHORT).show();
                                checkPassword(email,password,lecture,grant_no);
                                dialog.dismiss();
                        }

                    }
                });
            }
        });

        alertDialog.show();
    }
        private void checkPassword(final String email,final String password, final String result, final  String grant_no) {
            // Tag used to cancel the request
            String tag_string_req = "req_reset_pass";

            pDialog.setMessage("Please wait...");
            showDialog();

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    Functions.Stop_Atten_URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Reset Password Response: " + response);
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                        boolean error = jObj.getBoolean("error");

                        if (!error) {
                            Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                            String res=jObj.getString("message");
                            switch (res) {
                                case "Attendance grant over!": {
                                    Intent ii = new Intent(getApplicationContext(), FacultyHomeActivity.class);
                                    startActivity(ii);
                                    finish();
                                    break;
                                }
                                case "Attendance revoke error!": {
                                    Intent ii = new Intent(getApplicationContext(), FacultyHomeActivity.class);
                                    startActivity(ii);
                                    finish();
                                    break;
                                }
                                case "Attendance already stopped!": {
                                    Intent ii = new Intent(getApplicationContext(), FacultyHomeActivity.class);
                                    startActivity(ii);
                                    finish();
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), jObj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Password Response Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);
                    params.put("result", result);
                    params.put("grant_no", grant_no);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }

            };
            // Adding request to volley request queue
            strReq.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
            strReq.setRetryPolicy(new DefaultRetryPolicy(0, 0, 0));
            MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public class ViewDialogNet {

        void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_box);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!NetConnectivityCheck.isNetworkAvailable(QrGenerator.this)) {
                        ViewDialogNet alert = new QrGenerator.ViewDialogNet();
                        alert.showDialog(QrGenerator.this, "Please, enable internet connection before using this app !!");
                        Intent intent=new Intent(getApplicationContext(),QrGenerator.class);
                        stopService(intent);
                    }
                }
            });

            dialog.show();

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),QrGenerator.class);
        stopService(intent);
        finish();
    }
}