package com.dilip.cloudattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.helper.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminFeedback extends AppCompatActivity {
    private static final String TAG = AdminFeedback.class.getSimpleName();
    Spinner Batchh,Sem,Status;
    String semi,bat,status,Table_name;
    Button Submit;
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_fedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Submit=findViewById(R.id.submit);
        Batchh=findViewById(R.id.batch);
        Sem=findViewById(R.id.sem);
        Status=findViewById(R.id.status);

        pDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        Batchh.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        Status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                status = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final List<String> Batch = new ArrayList<String>();
        Batch.add("Select Batch");
        Batch.add("B.Tech(CTIS)");
        Batch.add("B.Tech(MACT)");
        Batch.add("B.Tech(ITDS)");
        Batch.add("B.C.A(CTIS)");
        Batch.add("B.C.A(MACT)");
        Batch.add("B.C.A(ITDS)");

        final List<String> semester = new ArrayList<String>();
        semester.add("Select Semester");
        semester.add("I");
        semester.add("II");
        semester.add("III");
        semester.add("IV");
        semester.add("V");
        semester.add("VI");
        semester.add("VII");
        semester.add("VIII");

        final List<String> Statuss = new ArrayList<String>();
        Statuss.add("Select Status");
        Statuss.add("Grant");
        Statuss.add("Revoke");

        Batchh.setSelection(0);
        Sem.setSelection(0);
        Status.setSelection(0);

        ArrayAdapter<String> LectureAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Batch);
        LectureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> SemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, semester);
        SemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> TypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Statuss);
        TypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Drop down layout style - list view with radio button
        LectureAdapter.setDropDownViewResource(R.layout.spinner_list_view);
        SemAdapter.setDropDownViewResource(R.layout.spinner_list_view);
        TypeAdapter.setDropDownViewResource(R.layout.spinner_list_view);

        // attaching data adapter to spinner
        Batchh.setAdapter(LectureAdapter);
        Sem.setAdapter(SemAdapter);
        Status.setAdapter(TypeAdapter);

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Table_name="Feedback_"+bat+"_"+semi;
                if (bat.contains("Select Batch")){
                    Toast.makeText(getApplicationContext(), "Please select batch !", Toast.LENGTH_SHORT).show();
                }
                else if (semi.contains("Select Semester")){
                    Toast.makeText(getApplicationContext(), "Please select semester !", Toast.LENGTH_SHORT).show();
                }
                else if (status.contains("Select Status")){
                    Toast.makeText(getApplicationContext(), "Please select status !", Toast.LENGTH_SHORT).show();
                }
                else {
                    sumbit_Query(Table_name,status);
                }
            }
        });
    }

    private void sumbit_Query(final String table_name, final String status) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.START_FEEDBACK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Feedback Response: " + response);
                pDialog.hide();

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
                Log.e(TAG, "Feedback Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("table_name", table_name);
                params.put("status", status);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
