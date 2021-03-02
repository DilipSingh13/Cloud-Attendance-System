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

public class CommentsFragment extends Fragment {

    private ListView DetailsListView;
    private TextView Result_Lable;
    private static final String TAG = CommentsFragment.class.getSimpleName();
    private ArrayList<HashMap<String, String>> contactJsonList;
    private String batch,sem,lecture,comments,email,urn;
    Spinner Batch,Sem;
    private ProgressDialog progressDialog;
    Button Search;

    public CommentsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_ratings, container, false);

        DetailsListView = view.findViewById(R.id.listView_search_result);
        Search= view.findViewById(R.id.search);
        Result_Lable= view.findViewById(R.id.res_lable);
        Batch = view.findViewById(R.id.batch);
        Sem = view.findViewById(R.id.sem);

        contactJsonList = new ArrayList<>();

        progressDialog = new ProgressDialog(getActivity(),R.style.MyAlertDialogStyle);
        progressDialog.setCancelable(false);

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        Batch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                batch = parent.getItemAtPosition(position).toString();
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
                if (batch.contains("Select Batch")) {
                    Toast.makeText(getActivity(), "Please select batch !", Toast.LENGTH_SHORT).show();
                }
                else if (sem.contains("Select Semester")){
                    Toast.makeText(getActivity(), "Please select semester !", Toast.LENGTH_SHORT).show();
                }
                else {
                    contactJsonList.clear();
                    viewFeedbackRatings(batch,sem);
                }
            }
        });

        // Spinner Drop down elements
        final List<String> Batchh = new ArrayList<String>();
        Batchh.add("Select Course");
        Batchh.add("B.Tech(CTIS)");
        Batchh.add("B.Tech(MACT)");
        Batchh.add("B.Tech(ITDS)");
        Batchh.add("B.C.A(CTIS)");
        Batchh.add("B.C.A(MACT)");
        Batchh.add("B.C.A(ITDS)");

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

        Batch.setSelection(0);
        Sem.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> BatchAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Batchh);
        ArrayAdapter<String> SemAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, sem);

        // Drop down layout style - list view with radio button
        BatchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        Batch.setAdapter(BatchAdapter);
        Sem.setAdapter(SemAdapter);

        return view;
    }

    public void viewFeedbackRatings(final String batch, final String sem) {

        Result_Lable.setVisibility(View.GONE);

        progressDialog.setMessage("Searching please wait...");
        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.VIEW_FEEDBACK_COMMENTS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Feedback Response: " + response);
                hideDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONArray jsonObject = new JSONArray(response);

                    if(response !=null) {
                        for (int i = 0; i < jsonArray.length(); i++) {


                            JSONObject c = jsonArray.getJSONObject(i);
                            urn = c.getString("urn_no");
                            email = c.getString("email");
                            lecture = c.getString("lecture");
                            comments = c.getString("comments");

                            HashMap<String, String> fetchOrders = new HashMap<>();

                            // adding each child node to HashMap key => value
                            fetchOrders.put("urn_no", urn);
                            fetchOrders.put("email", email);
                            fetchOrders.put("lecture", lecture);
                            fetchOrders.put("comments", comments);

                            contactJsonList.add(fetchOrders);
                            Batch.setSelection(0);
                            Sem.setSelection(0);
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
                        R.layout.view_comments, new String[]{"urn_no", "email", "lecture", "comments"}, new int[]{R.id.urn,R.id.email,R.id.lecture,R.id.comment});

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
                Log.e(TAG, "Feedback Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("batch", batch);
                params.put("semester", sem);

                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
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