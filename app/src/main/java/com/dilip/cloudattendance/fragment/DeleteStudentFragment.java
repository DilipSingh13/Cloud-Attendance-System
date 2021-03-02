package com.dilip.cloudattendance.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.MyApplication;
import com.dilip.cloudattendance.R;
import com.dilip.cloudattendance.helper.Functions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteStudentFragment extends Fragment {

    private ListView DetailsListView;
    private TextView Result_Lable;
    private static final String TAG = DeleteStudentFragment.class.getSimpleName();
    private ArrayList<HashMap<String, String>> contactJsonList;
    private String name,email,urn,surname,yr,course,sem,Fullname;
    Spinner Year,Course,Sem;
    private ProgressDialog progressDialog;
    Button Search;

    public DeleteStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_studnet, container, false);

        DetailsListView = view.findViewById(R.id.listView_search_result);
        Search= view.findViewById(R.id.search);
        Result_Lable= view.findViewById(R.id.res_lable);
        Year = view.findViewById(R.id.year);
        Course = view.findViewById(R.id.batch);
        Sem = view.findViewById(R.id.sem);

        contactJsonList = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yr = parent.getItemAtPosition(position).toString();
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
                if (course.contains("Select Course")){
                    Toast.makeText(getActivity(), "Please select course !", Toast.LENGTH_SHORT).show();
                }
                else if (yr.contains("Select Year")) {
                    Toast.makeText(getActivity(), "Please select year !", Toast.LENGTH_SHORT).show();
                }
                else if (sem.contains("Select Semester")){
                    Toast.makeText(getActivity(), "Please select semester !", Toast.LENGTH_SHORT).show();
                }
                else {
                    contactJsonList.clear();
                    searchStudents(course,yr,sem);
                }
            }
        });

        // Spinner Drop down elements
        final List<String> Cours = new ArrayList<String>();
        Cours.add("Select Course");
        Cours.add("B.Tech(CTIS)");
        Cours.add("B.Tech(MACT)");
        Cours.add("B.Tech(ITDS)");
        Cours.add("B.C.A(CTIS)");
        Cours.add("B.C.A(MACT)");
        Cours.add("B.C.A(ITDS)");

        final List<String> year = new ArrayList<String>();
        year.add("Select Year");
        year.add("1st Year");
        year.add("2nd Year");
        year.add("3rd Year");
        year.add("4th Year");

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

        Year.setSelection(0);
        Course.setSelection(0);
        Sem.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> CourseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Cours);
        ArrayAdapter<String> YearAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, year);
        ArrayAdapter<String> SemAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sem);

        // Drop down layout style - list view with radio button
        CourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        YearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Course.setAdapter(CourseAdapter);
        Year.setAdapter(YearAdapter);
        Sem.setAdapter(SemAdapter);

        return view;
    }

    public void searchStudents(final String course, final String year, final String sem) {

        Result_Lable.setVisibility(View.GONE);

        progressDialog.setMessage("Searching please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.DELETE_STUDNET_VIEW_URL, new Response.Listener<String>() {

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
                            surname=c.getString("surname");
                            urn = c.getString("urn_no");
                            email = c.getString("email");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("surname", surname);
                            fetchOrders.put("urn_no", urn);
                            fetchOrders.put("email",email);


                            contactJsonList.add(fetchOrders);
                        }
                    }
                } catch (final Exception e ) {
                    contactJsonList.clear();
                    progressDialog.hide();
                    Result_Lable.setVisibility(View.VISIBLE);
                    Result_Lable.setText("No data found try again !");
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        getActivity(), contactJsonList,
                        R.layout.search_students_list, new String[]{"name", "surname", "urn_no", "email"}, new int[]{R.id.name,R.id.surname,R.id.urn,R.id.email});

                DetailsListView.setAdapter(adapter);

                DetailsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final TextView Email = (TextView) view.findViewById(R.id.email);
                        final TextView Name = (TextView) view.findViewById(R.id.name);
                        final TextView Surname = (TextView) view.findViewById(R.id.surname);
                        name = Name.getText().toString();
                        surname = Surname.getText().toString();
                        Fullname=name+" "+surname;
                        email = Email.getText().toString();
                        delete_Conformation();
                    }
                });
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("course", course);
                params.put("year", year);
                params.put("semester", sem);

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

    private void delete_Conformation() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle("Delete User?");
        dialogBuilder.setMessage("Name: "+Fullname+"\n"+"Email: "+email);
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
                        delete_User(email);
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void delete_User(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        progressDialog.setMessage("Deleting user please wait ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.DELETE_USERS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete user response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String Msg = jObj.getString("message");
                        Toast.makeText(getActivity(),Msg, Toast.LENGTH_LONG).show();
                        contactJsonList.clear();
                        searchStudents(course,yr,sem);
                    } else {
                        // Error occurred during attendance. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getActivity(),errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Delete user error: " + error.getMessage(), error);
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}