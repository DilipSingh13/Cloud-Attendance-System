package com.dilip.cloudattendance.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class AddFacultyFragment extends Fragment {

    private EditText txtName, txtLast,txtID, txtMob,txtEmail, txtPassword,txtImei;
    private Button AddUser;
    public String item,Name,Last,ID,Urn="0",Mobile,Email,Password,course="0",yr="0",sem="0",imei,role="Faculty",verify="1";
    ProgressDialog pDialog;


    public AddFacultyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_faculty, container, false);

        txtName = view.findViewById(R.id.lEdit_First);
        txtLast = view.findViewById(R.id.lLast);
        txtID = view.findViewById(R.id.lID);
        txtMob = view.findViewById(R.id.lMob);
        txtEmail = view.findViewById(R.id.lEmail);
        txtPassword = view.findViewById(R.id.lPassword);
        txtImei = view.findViewById(R.id.l_imei);
        AddUser=view.findViewById(R.id.add_user);

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        pDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        AddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Name=txtName.getText().toString();
                Last=txtLast.getText().toString();
                ID=txtID.getText().toString();
                Mobile=txtMob.getText().toString();
                Email=txtEmail.getText().toString();
                Password=txtPassword.getText().toString();
                imei=txtImei.getText().toString();
                checkDetails();
                if (!Name.isEmpty()&&!Last.isEmpty()&&!ID.isEmpty()&&Mobile.length()==10&&!Email.isEmpty()&&Password.length()>7&&imei.length()==15) {
                    if (Functions.isValidEmailAddress(Email)) {
                        AddFaculty(ID,Urn,Name,Last,course,yr,sem,Mobile,Email,Password,imei,role,verify);
                    }
                    else{
                        txtEmail.setError("Enter correct email id!");
                    }
                }
                else{
                    checkDetails();
                }

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
            txtID.setError("Enter Id !");
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
        }
    }
    private void AddFaculty(final String id, final  String URN, final String name, final String surname, final String course, final String year, final String sem, final String mobile, final String email, final String password, final String imei, final String role, final  String verify) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Adding Faculty ...");
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

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), Msg, Toast.LENGTH_SHORT).show();
                            }
                        });
                        txtName.setText("");
                        txtLast.setText("");
                        txtID.setText("");
                        txtMob.setText("");
                        txtEmail.setText("");
                        txtPassword.setText("");
                        txtImei.setText("");
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