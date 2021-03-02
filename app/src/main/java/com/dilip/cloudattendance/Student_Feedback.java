package com.dilip.cloudattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.Functions;
import com.dilip.cloudattendance.helper.SessionManager;
import com.hsalf.smilerating.SmileRating;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Student_Feedback extends AppCompatActivity {

    private static final String TAG = Student_Feedback.class.getSimpleName();
    SmileRating Ques_Rating1,Ques_Rating2,Ques_Rating3,Ques_Rating4;
    TextView Ques1,Ques2,Ques3,Ques4,Lable;
    EditText Comments;
    String batch,sem,urn,email,Table_Name,status="Grant",batc,deptartment;
    String ques1,ques2,ques3,ques4,lec;
    String rating1,rating2,rating3,rating4;
    int question1,question2,question3,question4;
    Button Submit_Feedback;
    Spinner Lecture;
    RelativeLayout MyLayout;
    private DatabaseHandler db;
    private HashMap<String, String> user = new HashMap<>();
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student__feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Ques_Rating1 = findViewById(R.id.ques_rating1);
        Ques_Rating2 = findViewById(R.id.ques_rating2);
        Ques_Rating3 = findViewById(R.id.ques_rating3);
        Ques_Rating4 = findViewById(R.id.ques_rating4);
        Ques1 = findViewById(R.id.ques1);
        Ques2 = findViewById(R.id.ques2);
        Ques3 = findViewById(R.id.ques3);
        Ques4 = findViewById(R.id.ques4);
        Submit_Feedback = findViewById(R.id.submit_feedback);
        Lecture = findViewById(R.id.lecture);
        Comments = findViewById(R.id.comment);
        MyLayout = findViewById(R.id.myLayout);
        Lable = findViewById(R.id.lable);

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        email = user.get("email");
        urn = user.get("urn_no");
        batch = user.get("course");
        sem = user.get("semester");

        // Progress dialog
        pDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        //Questions here
        ques1="Lecture start on time?";
        ques2="Study material is provided by faculty?";
        ques3="Topics are explained using examples?";
        ques4="Assessment message is conveyed by faculty?";

        Ques1.setText(ques1);
        Ques2.setText(ques2);
        Ques3.setText(ques3);
        Ques4.setText(ques4);

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

        final List<String> lecture = new ArrayList<String>();
        lecture.add("Select Lecture");
        lecture.add("CyberForensic");
        lecture.add("CloudSecurity");
        lecture.add("CloudSolutionArchitect");
        lecture.add("AdvanceStorage");

        Lecture.setSelection(0);

        ArrayAdapter<String> LectureAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lecture);
        LectureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Lecture.setAdapter(LectureAdapter);

        Ques_Rating1.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {
                question1 = Ques_Rating1.getRating();
                switch (smiley) {
                    case SmileRating.TERRIBLE:
                        rating1="1.0";
                        break;
                    case SmileRating.BAD:
                        rating1="2.0";
                        break;
                    case SmileRating.OKAY:
                        rating1="3.0";
                        break;
                    case SmileRating.GOOD:
                        rating1="4.0";
                        break;
                    case SmileRating.GREAT:
                        rating1="5.0";
                        break;
                }
            }
        });

        Ques_Rating2.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {
                question2 = Ques_Rating2.getRating();
                switch (smiley) {
                    case SmileRating.TERRIBLE:
                        rating2="1.0";
                        break;
                    case SmileRating.BAD:
                        rating2="2.0";
                        break;
                    case SmileRating.OKAY:
                        rating2="3.0";
                        break;
                    case SmileRating.GOOD:
                        rating2="4.0";
                        break;
                    case SmileRating.GREAT:
                        rating2="5.0";
                        break;
                }
            }
        });

        Ques_Rating3.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {
                question3 = Ques_Rating3.getRating();
                switch (smiley) {
                    case SmileRating.TERRIBLE:
                        rating3="1.0";
                        break;
                    case SmileRating.BAD:
                        rating3="2.0";
                        break;
                    case SmileRating.OKAY:
                        rating3="3.0";
                        break;
                    case SmileRating.GOOD:
                        rating3="4.0";
                        break;
                    case SmileRating.GREAT:
                        rating3="5.0";
                        break;
                }
            }
        });

        Ques_Rating4.setOnSmileySelectionListener(new SmileRating.OnSmileySelectionListener() {
            @Override
            public void onSmileySelected(int smiley, boolean reselected) {
                question4 = Ques_Rating4.getRating();
                switch (smiley) {
                    case SmileRating.TERRIBLE:
                        rating4="1.0";
                        break;
                    case SmileRating.BAD:
                        rating4="2.0";
                        break;
                    case SmileRating.OKAY:
                        rating4="3.0";
                        break;
                    case SmileRating.GOOD:
                        rating4="4.0";
                        break;
                    case SmileRating.GREAT:
                        rating4="5.0";
                        break;
                }
            }
        });
        Table_Name="Feedback_"+batch+"_"+sem;
        check_Feedback(status);
        Submit_Feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments=Comments.getText().toString();
                if (comments.isEmpty()){
                    comments="N/A";
                }
                if (lec.contains("Select Lecture")){
                    Toast.makeText(getApplicationContext(), "Please select lecture !", Toast.LENGTH_SHORT).show();
                }
                else{
                    float r1,r2,r3,r4,Sum;
                    String Average;
                    r1=Float.parseFloat(rating1);
                    r2=Float.parseFloat(rating2);
                    r3=Float.parseFloat(rating3);
                    r4=Float.parseFloat(rating4);
                    Sum=r1+r2+r3+r4;
                    Sum=Sum/5;
                    Average=Float.toString(Sum);
                    submit_Feedback(Table_Name,urn,email,lec,batch,sem,rating1,rating2,rating3,rating4,comments,Average);
                }
            }
        });
    }

    private void check_Feedback(final String status) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.CHECK_FEEDBACK_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Check Response: " + response);
                pDialog.hide();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        MyLayout.setVisibility(View.VISIBLE);
                    } else {
                        // Error occurred during attendance. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Lable.setText(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Check Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("status", status);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void submit_Feedback(final String table_Name, final String urn, final String email, final String lecture, final String batch, final String sem,
                                 final String ques1, final String ques2, final String ques3, final String ques4, final String comment, final String average) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Please wait...");
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.FEEDBACK_URL, new Response.Listener<String>() {

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
                        Intent i =new Intent(getApplicationContext(),StudentHomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
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
                params.put("table_name", table_Name);
                params.put("urn_no", urn);
                params.put("email", email);
                params.put("lecture", lecture);
                params.put("batch", batch);
                params.put("sem", sem);
                params.put("question1", ques1);
                params.put("question2", ques2);
                params.put("question3", ques3);
                params.put("question4", ques4);
                params.put("comments", comment);
                params.put("average", average);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
