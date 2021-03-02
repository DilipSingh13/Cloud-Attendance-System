package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.helper.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Faculty_Mark_Attendance extends AppCompatActivity {

    ListView DetailsListView;
    ProgressBar progressBar;
    TextView Result_Lable;
    private static final String TAG = Faculty_Mark_Attendance.class.getSimpleName();
    ArrayList<HashMap<String, String>> JsonList;
    Spinner Lecture,Course,Year,Sem;
    private ProgressDialog pDialog;
    String course,yr,sem,status="Present",surname,name,email,urn,Fullname,lec,Table_name;
    Button Search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_mark_attendance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DetailsListView = findViewById(R.id.listView_search_result);
        progressBar = findViewById(R.id.progressbar);
        Search=findViewById(R.id.search);
        Result_Lable=findViewById(R.id.res_lable);
        Lecture = findViewById(R.id.lecture);
        Course = findViewById(R.id.course);
        Year = findViewById(R.id.year);
        Sem = findViewById(R.id.sem);

        JsonList = new ArrayList<>();

        // Progress dialog
        pDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        if (!NetConnectivityCheck.isNetworkAvailable(Faculty_Mark_Attendance.this)) {
            ViewDialogNet alert = new Faculty_Mark_Attendance.ViewDialogNet();
            alert.showDialog(Faculty_Mark_Attendance.this, "Please, enable internet connection before using this app !!");
        }

        Lecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lec = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                course = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Table_name=lec+"_"+bat+"_"+semi;
                if (course.contains("Select Lecture")){
                    Toast.makeText(getApplicationContext(), "Please select lecture !", Toast.LENGTH_SHORT).show();
                }
                else if (course.contains("Select Course")){
                    Toast.makeText(getApplicationContext(), "Please select course !", Toast.LENGTH_SHORT).show();
                }
                else if (yr.contains("Select Batch")){
                    Toast.makeText(getApplicationContext(), "Please select batch !", Toast.LENGTH_SHORT).show();
                }
                else if (sem.contains("Select Semester")){
                    Toast.makeText(getApplicationContext(), "Please select semester !", Toast.LENGTH_SHORT).show();
                }
                else {
                    JsonList.clear();
                    progressBar.setVisibility(View.VISIBLE);
                    ViewStudnet(course,yr,sem);
                }
            }
        });

        List<String> Lect = new ArrayList<String>();
        Lect.add("Select Lecture");
        Lect.add("CyberForensic");
        Lect.add("CloudSecurity");
        Lect.add("CloudSolutionArchitect");
        Lect.add("AdvanceStorage");

        List<String> Cour = new ArrayList<String>();
        Cour.add("Select Course");
        Cour.add("B.Tech(CTIS)");
        Cour.add("B.Tech(MACT)");
        Cour.add("B.Tech(ITDS)");
        Cour.add("B.C.A(CTIS)");
        Cour.add("B.C.A(MACT)");
        Cour.add("B.C.A(ITDS)");

        List<String> year = new ArrayList<String>();
        year.add("Select Year");
        year.add("1st Year");
        year.add("2nd Year");
        year.add("3rd Year");
        year.add("4th Year");

        List<String> seme = new ArrayList<String>();
        seme.add("Select Semester");
        seme.add("I");
        seme.add("II");
        seme.add("III");
        seme.add("IV");
        seme.add("V");
        seme.add("VI");
        seme.add("VII");
        seme.add("VIII");

        Lecture.setSelection(0);
        Course.setSelection(0);
        Year.setSelection(0);
        Sem.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> LectureAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, Lect);
        ArrayAdapter<String> CourseAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, Cour);
        ArrayAdapter<String> YearAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, year);
        ArrayAdapter<String> SemAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, seme);

        // Drop down layout style - list view with radio button
        CourseAdapter.setDropDownViewResource(R.layout.spinner_list_view);
        LectureAdapter.setDropDownViewResource(R.layout.spinner_list_view);
        YearAdapter.setDropDownViewResource(R.layout.spinner_list_view);
        SemAdapter.setDropDownViewResource(R.layout.spinner_list_view);

        // attaching data adapter to spinner
        Lecture.setAdapter(LectureAdapter);
        Course.setAdapter(CourseAdapter);
        Year.setAdapter(YearAdapter);
        Sem.setAdapter(SemAdapter);
    }

    private void ViewStudnet(final String course, final String yr, final String sem) {
        progressBar.setVisibility(View.GONE);
        Result_Lable.setVisibility(View.GONE);
        pDialog.setMessage("Searching student...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.VIEW_STUDNETS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                Log.d(TAG, "Search student Response: " + response);
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONArray jsonObject = new JSONArray(response);

                    if(response !=null) {
                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject c = jsonArray.getJSONObject(i);

                            name = c.getString("name");
                            surname = c.getString("surname");
                            urn = c.getString("urn_no");
                            email = c.getString("email");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("surname", surname);
                            fetchOrders.put("urn_no", urn);
                            fetchOrders.put("email",email);

                            JsonList.add(fetchOrders);
                        }
                    }

                } catch (final Exception e) {
                    JsonList.clear();
                    progressBar.setVisibility(View.GONE);
                    Result_Lable.setVisibility(View.VISIBLE);
                    Result_Lable.setText("No data found try again !");
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        Faculty_Mark_Attendance.this, JsonList,
                        R.layout.search_students_list, new String[]{"name", "surname", "urn_no", "email"}, new int[]{R.id.name,R.id.surname,R.id.urn,R.id.email});

                DetailsListView.setAdapter(adapter);

                DetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final TextView Email = (TextView) view.findViewById(R.id.email);
                        final TextView Name = (TextView) view.findViewById(R.id.name);
                        final TextView Surname = (TextView) view.findViewById(R.id.surname);
                        final TextView Urn = (TextView) view.findViewById(R.id.urn);
                        name = Name.getText().toString();
                        surname = Surname.getText().toString();
                        Fullname=name+" "+surname;
                        urn = Urn.getText().toString();
                        email = Email.getText().toString();
                        Table_name=lec+"_"+course+"_"+sem;
                        attendance_Conformation();
                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Search student Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("course", course);
                params.put("year", yr);
                params.put("semester", sem);
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }
    private void attendance_Conformation() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Faculty_Mark_Attendance.this);

        dialogBuilder.setTitle("Mark Attendance");
        dialogBuilder.setMessage("Name: "+Fullname+"\n"+"Urn: "+urn+"\n"+"Email: "+email);
        dialogBuilder.setCancelable(false);


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

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Conform Delete Action Method
                        insertAttendance(Table_name,Fullname,urn,email,status);
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void insertAttendance(final String table_name, final String name, final String urn, final String email, final String status) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Starting Attendance ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.INSERT_STUDENTS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Attendance Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String Msg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),Msg, Toast.LENGTH_LONG).show();
                    } else {
                        // Error occurred during attendance. Get the error
                        // message
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
                Log.e(TAG, "Attendance Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("table_name", table_name);
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
                    if (!NetConnectivityCheck.isNetworkAvailable(Faculty_Mark_Attendance.this)) {
                        ViewDialogNet alert = new Faculty_Mark_Attendance.ViewDialogNet();
                        alert.showDialog(Faculty_Mark_Attendance.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });
            dialog.show();
        }
    }
}
