package com.dilip.cloudattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.Functions;
import com.dilip.cloudattendance.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CompleteAttendance extends AppCompatActivity {

    private static final String TAG = CompleteAttendance.class.getSimpleName();
    public String Name,Surname,Fullname,Email,Urn,Batch,Sem,Status,secret_code,table_name,verify="1";
    private SessionManager session;
    private DatabaseHandler db;
    private ProgressDialog pDialog;
    private HashMap<String, String> user = new HashMap<>();
    private Drawable myDrawable;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_attendance);

        img=findViewById(R.id.image);

        Intent in= getIntent();
        Bundle b = in.getExtras();

        if(b!=null)
        {
            table_name =(String) b.get("table_name");
            secret_code =(String) b.get("code");
        }

        // Progress dialog
        pDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();
        myDrawable= getResources().getDrawable(R.drawable.failed);
        // Fetching user details from database
        Name = user.get("name");
        Surname = user.get("surname");
        Email = user.get("email");
        Urn = user.get("urn_no");
        Batch = user.get("batch");
        Sem = user.get("semister");

        Fullname=Name+" "+Surname;

        Status = "Present";

        //Toast.makeText(this, Urn, Toast.LENGTH_SHORT).show();
        validateUser(Email,verify);
    }

    private void validateUser(final String email, final String verify) {
        String tag_string_req = "req_register";

        pDialog.setMessage("Marking Attendance ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.VALIDATE_STUDNETS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String Msg = jObj.getString("verify");
                        if (Msg.equals("0")){
                            img.setVisibility(View.VISIBLE);
                            img.setImageDrawable(myDrawable);
                            Toast.makeText(CompleteAttendance.this, "Your attendance is blocked by admin", Toast.LENGTH_SHORT).show();
                            hideDialog();
                        }
                        else{
                            MarkAttendance(secret_code, Fullname, Urn, Email, table_name, Status);
                        }
                    } else {
                        img.setVisibility(View.VISIBLE);
                        img.setImageDrawable(myDrawable);
                        String errorMsg = jObj.getString("verify");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    img.setVisibility(View.VISIBLE);
                    img.setImageDrawable(myDrawable);
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                img.setImageDrawable(myDrawable);
                Log.e(TAG, "Script Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("verify", verify);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void MarkAttendance(final String flag, final String name, final String urn, final String email, final String table_name, final String status) {

        // Tag used to cancel the request
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.Student_Attendance_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Attendance Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    final boolean error = jObj.getBoolean("error");
                    if (!error) {
                        img.setVisibility(View.VISIBLE);
                        final String Msg = jObj.getString("message");
                                Toast.makeText(getApplicationContext(), Msg, Toast.LENGTH_SHORT).show();
                    } else {
                        // Error occurred during attendance. Get the error
                        // message
                        img.setVisibility(View.VISIBLE);
                        img.setImageDrawable(myDrawable);
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Script Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("table_name", table_name);
                params.put("flag", flag);
                params.put("name", name);
                params.put("urn", urn);
                params.put("email", email);
                params.put("status", status);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, StudentHomeActivity.class));
        finish();
    }
}
