package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.helper.Functions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class View_Attendance extends AppCompatActivity {

    ListView DetailsListView;
    ProgressBar progressBar;
    TextView Result_Lable;
    private static final String TAG = Faculty_Mark_Attendance.class.getSimpleName();
    ArrayList<HashMap<String, String>> contactJsonList;
    String name,email,urn,dte,dt;
    Spinner Lecture,Course,Sem;
    EditText PickDate;
    private ProgressDialog pDialog;
    String lec,bat,semi,Table_name;
    Button Search;
    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DetailsListView = findViewById(R.id.listView_search_result);
        progressBar = findViewById(R.id.progressbar);
        Search=findViewById(R.id.search);
        Result_Lable=findViewById(R.id.res_lable);
        Lecture = findViewById(R.id.sub);
        Course = findViewById(R.id.course);
        Sem = findViewById(R.id.sem);
        PickDate = findViewById(R.id.date);

        contactJsonList = new ArrayList<>();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        PickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(View_Attendance.this, R.style.Datetheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


            // Progress dialog
        pDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (!NetConnectivityCheck.isNetworkAvailable(View_Attendance.this)) {
            ViewDialogNet alert = new View_Attendance.ViewDialogNet();
            alert.showDialog(View_Attendance.this, "Please, enable internet connection before using this app !!");
        }

        Lecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                lec = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                bat = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                semi = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Table_name=lec+"_"+bat+"_"+semi;
                dte=PickDate.getText().toString();
                if (lec.contains("Select Lecture")){
                    Toast.makeText(getApplicationContext(), "Please select lecture !", Toast.LENGTH_SHORT).show();
                }
                else if (bat.contains("Select Course")){
                    Toast.makeText(getApplicationContext(), "Please select course !", Toast.LENGTH_SHORT).show();
                }
                else if (semi.contains("Select Semester")){
                    Toast.makeText(getApplicationContext(), "Please select semester !", Toast.LENGTH_SHORT).show();
                }
                else if(dte.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please select date !", Toast.LENGTH_SHORT).show();
                }
                else {
                    contactJsonList.clear();
                    progressBar.setVisibility(View.GONE);
                    ViewAttendance(Table_name,dte);
                }
            }
        });

        // Spinner Drop down elements
        final List<String> lecture = new ArrayList<String>();
        lecture.add("Select Lecture");
        lecture.add("CyberForensic");
        lecture.add("CloudSecurity");
        lecture.add("CloudSolutionArchitect");
        lecture.add("AdvanceStorage");

        final List<String> Cours = new ArrayList<String>();
        Cours.add("Select Course");
        Cours.add("B.Tech(CTIS)");
        Cours.add("B.Tech(MACT)");
        Cours.add("B.Tech(ITDS)");
        Cours.add("B.C.A(CTIS)");
        Cours.add("B.C.A(MACT)");
        Cours.add("B.C.A(ITDS)");

        final List<String> sem = new ArrayList<String>();
        sem.add("Select Semester");
        sem.add("I");
        sem.add("II");
        sem.add("III");
        sem.add("IV");
        sem.add("V");
        sem.add("VI");
        sem.add("VII");
        sem.add("VIII");

        Lecture.setSelection(0);
        Course.setSelection(0);
        Sem.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> LectureAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, lecture);
        ArrayAdapter<String> CourseAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, Cours);
        ArrayAdapter<String> SemAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, sem);

        // Drop down layout style - list view with radio button
        LectureAdapter.setDropDownViewResource(R.layout.spinner_list_view);
        CourseAdapter.setDropDownViewResource(R.layout.spinner_list_view);
        SemAdapter.setDropDownViewResource(R.layout.spinner_list_view);

        // attaching data adapter to spinner
        Lecture.setAdapter(LectureAdapter);
        Course.setAdapter(CourseAdapter);
        Sem.setAdapter(SemAdapter);
    }
    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        PickDate.setText(sdf.format(myCalendar.getTime()));
    }

    public void ViewAttendance(final String table_name,final  String date) {

        pDialog.setMessage("Searching please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.VIEW_TODAY_ATTENDANCE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Search Response: " + response);
                hideDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONArray jsonObject = new JSONArray(response);

                    if(response !=null) {
                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject c = jsonArray.getJSONObject(i);

                            name = c.getString("name");
                            urn = c.getString("urn");
                            email = c.getString("email");
                            dt = c.getString("date");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("urn", urn);
                            fetchOrders.put("email",email);
                            fetchOrders.put("date",dt);

                            contactJsonList.add(fetchOrders);
                        }
                        progressBar.setVisibility(View.GONE);
                        Result_Lable.setVisibility(View.GONE);
                    }
                } catch (final Exception e ) {
                    contactJsonList.clear();
                    progressBar.setVisibility(View.GONE);
                    Result_Lable.setVisibility(View.VISIBLE);
                    Result_Lable.setText("No data found try again !");
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        View_Attendance.this, contactJsonList,
                        R.layout.view_today_attendance_list, new String[]{"name", "urn", "email", "date"}, new int[]{R.id.name,R.id.urn,R.id.email,R.id.date});

                DetailsListView.setAdapter(adapter);

                DetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("table_name", table_name);
                params.put("date", date);

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public class ViewDialogNet {

        public void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_box);

            TextView text = dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!NetConnectivityCheck.isNetworkAvailable(View_Attendance.this)) {
                        ViewDialogNet alert = new View_Attendance.ViewDialogNet();
                        alert.showDialog(View_Attendance.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });
            dialog.show();
        }
    }
}
