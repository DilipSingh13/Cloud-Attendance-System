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
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
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
import java.util.Map;

public class DeleteFacultyFragment extends Fragment {

    private ListView DetailsListView;
    private TextView Result_Lable;
    private static final String TAG = DeleteFacultyFragment.class.getSimpleName();
    private ArrayList<HashMap<String, String>> JsonList;
    private String name,email,id,surname,role="Faculty",Fullname;
    private ProgressDialog pDialog;

    public DeleteFacultyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_faculty, container, false);
        DetailsListView = view.findViewById(R.id.listView_search_result);
        Result_Lable= view.findViewById(R.id.res_lable);

        JsonList = new ArrayList<>();

        pDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        pDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        searchFaculty(role);

        return view;
    }

    public void searchFaculty(final String role) {

        Result_Lable.setVisibility(View.GONE);
        JsonList.clear();
        pDialog.setMessage("Please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.VIEW_FACULTIES_URL, new Response.Listener<String>() {

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
                            id = c.getString("id_no");
                            email = c.getString("email");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("name", name);
                            fetchOrders.put("surname", surname);
                            fetchOrders.put("id_no", id);
                            fetchOrders.put("email",email);


                            JsonList.add(fetchOrders);
                        }
                    }
                } catch (final Exception e ) {
                    JsonList.clear();
                    hideDialog();
                    Result_Lable.setVisibility(View.VISIBLE);
                    Result_Lable.setText("No data found try again !");
                    e.printStackTrace();
                }
                ListAdapter adapter = new SimpleAdapter(
                        getActivity(), JsonList,
                        R.layout.search_faculties_list, new String[]{"name", "surname", "id_no", "email"}, new int[]{R.id.name,R.id.surname,R.id.id_no,R.id.email});

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
                params.put("role", role);

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

        pDialog.setMessage("Deleting user please wait ...");
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
                        JsonList.clear();
                        searchFaculty(role);
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
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}