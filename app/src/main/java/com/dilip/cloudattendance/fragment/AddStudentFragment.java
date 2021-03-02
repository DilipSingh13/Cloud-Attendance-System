package com.dilip.cloudattendance.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.MyApplication;
import com.dilip.cloudattendance.R;
import com.dilip.cloudattendance.helper.Functions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStudentFragment extends Fragment {

    EditText txtName, txtLast,txtUrn, txtMob,txtEmail, txtPassword,txtImei;
    private Button AddUser;
    Spinner Course,Year,Semester;
    public String item,Name,Last,ID="0",Urn,Mobile,Email,Password,course,yr,sem,imei,role="Student",verify="1";
    ProgressDialog pDialog;


    public AddStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_studnet, container, false);

        txtName = view.findViewById(R.id.lEdit_First);
        txtLast = view.findViewById(R.id.lLast);
        txtUrn = view.findViewById(R.id.lURN);
        txtMob = view.findViewById(R.id.lMob);
        txtEmail = view.findViewById(R.id.lEmail);
        txtPassword = view.findViewById(R.id.lPassword);
        txtImei = view.findViewById(R.id.l_imei);
        Course = view.findViewById(R.id.course);
        Year = view.findViewById(R.id.year);
        Semester = view.findViewById(R.id.semester);
        AddUser = view.findViewById(R.id.add_user);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        pDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);
        // Spinner Drop down elements
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

        Course.setSelection(0);
        Year.setSelection(0);
        Semester.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> CourseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Cour);
        ArrayAdapter<String> YearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, year);
        ArrayAdapter<String> SemesterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, seme);

        // Drop down layout style - list view with radio button
        CourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SemesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Course.setAdapter(CourseAdapter);
        Year.setAdapter(YearAdapter);
        Semester.setAdapter(SemesterAdapter);

        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name=txtName.getText().toString();
                Last=txtLast.getText().toString();
                Urn=txtUrn.getText().toString();
                Mobile=txtMob.getText().toString();
                Email=txtEmail.getText().toString();
                Password=txtPassword.getText().toString();
                imei=txtImei.getText().toString();
                checkDetails();
                if (!Name.isEmpty()&&!Last.isEmpty()&&!Urn.isEmpty()&&Mobile.length()==10&&!Email.isEmpty()&&Password.length()>=7&&imei.length()==15) {
                    if (Functions.isValidEmailAddress(Email)) {
                        AddStudent(ID, Urn, Name, Last, course, yr, sem, Mobile, Email, Password, imei, role, verify);
                    }
                    else{
                        txtEmail.setError("Enter correct email id!");
                    }
                }
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
        Semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void checkDetails() {
        if (Name.isEmpty()){
            txtName.setError("Enter first name !");
        }if (Last.isEmpty()){
            txtLast.setError("Enter last name !");
        }if (ID.isEmpty()){
            txtUrn.setError("Enter Urn !");
        }if (Mobile.isEmpty()) {
            txtMob.setError("Enter mobile number !");
        }if (Mobile.length()!=10) {
            txtMob.setError("Enter 10 digit number !");
        }if (Email.isEmpty()) {
            txtEmail.setError("Enter email address !");
        }if (Password.length()<7) {
            txtPassword.setError("Password should be greater than 6 digits !");
        }if (imei.length()!=15) {
            txtImei.setError("Enter 15 imei digits !");
        }if (imei.isEmpty()) {
            txtImei.setError("Enter 15 digits imei !");
        }if (course.equals("Select Course")) {
            Toast.makeText(getActivity(), "Please select course !", Toast.LENGTH_SHORT).show();
        } else if (yr.equals("Select Year")) {
            Toast.makeText(getActivity(), "Please select year !", Toast.LENGTH_SHORT).show();
        } else if (sem.equals("Select Semester")) {
            Toast.makeText(getActivity(), "Please select semester !", Toast.LENGTH_SHORT).show();
        }
    }


    private void AddStudent(final String id, final  String URN, final String name, final String surname, final String course, final String year, final String sem, final String mobile, final String email, final String password, final String imei, final String role, final  String verify) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Adding Studnet ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.ADD_USER_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    final boolean error = jObj.getBoolean("error");
                    if (!error) {
                        final String Msg = jObj.getString("message");


                        ///////looper error
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), Msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        txtName.setText("");
                        txtLast.setText("");
                        txtUrn.setText("");
                        txtMob.setText("");
                        txtEmail.setText("");
                        txtPassword.setText("");
                        txtImei.setText("");
                        Course.setSelection(0);
                        Year.setSelection(0);
                        Semester.setSelection(0);
                    } else {
                        // Error occurred during attendance. Get the error
                        // message
                        final String errorMsg = jObj.getString("message");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(final VolleyError error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("id", id);
                params.put("urn", URN);
                params.put("name", name);
                params.put("surname", surname);
                params.put("course", course);
                params.put("year", year);
                params.put("semester", sem);
                params.put("mobile", mobile);
                params.put("email", email);
                params.put("password", password);
                params.put("imei", imei);
                params.put("role", role);
                params.put("verify", verify);
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
}