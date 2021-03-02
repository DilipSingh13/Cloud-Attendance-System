package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dilip.cloudattendance.Navigation.DrawerAdapter;
import com.dilip.cloudattendance.Navigation.DrawerItem;
import com.dilip.cloudattendance.Navigation.SimpleItem;
import com.dilip.cloudattendance.Navigation.SpaceItem;
import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.Functions;
import com.dilip.cloudattendance.helper.SessionManager;
import com.dilip.cloudattendance.imageSliderFragment.CustomViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacultyHomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener{

    private static final String TAG = StudentHomeActivity.class.getSimpleName();

    private SlidingRootNav slidingRootNav;
    private static final int Home = 0;
    private static final int MyAccount = 1;
    private static final int Mrk_Attn = 2;
    private static final int View_today = 3;
    private static final int TimeTable = 4;
    private static final int ExamDuty = 5;
    private static final int About = 7;
    private static final int SHARE = 8;
    private static final int Contact_Dev = 9;
    private static final int Exit = 11;
    ProgressBar progressBar;
    private String[] screenTitles;
    private Drawable[] screenIcons;

    private ImageView Logout_btn;
    private SessionManager session;
    private DatabaseHandler db;
    private Toast toast;
    private long lastBackPressTime = 0;

    TextView txtName, txtEmail, txtID;
    private Button MRK_ATTN;
    private HashMap<String, String> user = new HashMap<>();
    Spinner Lecture,Course,Sem;
    String lect,lec,bat,semi,name,id,email,status="yes",last,fullname;

    private static final long SLIDER_TIMER = 4000;
    private int currentPage = 0;
    private boolean isCountDownTimerActive = false;
    private Handler handler;
    private ViewPager viewPager;
    CardView card;
    private LinearLayout dotsLayout;
    private TextView[] dots;

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (!isCountDownTimerActive) {
                automateSlider();
            }
            handler.postDelayed(runnable, 1000);

        }
    };

    private void automateSlider() {
        isCountDownTimerActive = true;
        new CountDownTimer(SLIDER_TIMER, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                int nextSlider = currentPage + 1;
                if (nextSlider == 4) {
                    nextSlider = 0;
                }

                viewPager.setCurrentItem(nextSlider);
                isCountDownTimerActive = false;
            }
        }.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Logout_btn = findViewById(R.id.logout_icon);
        txtName = findViewById(R.id.Name);
        txtEmail = findViewById(R.id.email);
        txtID = findViewById(R.id.id);
        Lecture = findViewById(R.id.sub);
        Course = findViewById(R.id.batch);
        Sem = findViewById(R.id.sem);
        MRK_ATTN=findViewById(R.id.mrk_att);
        progressBar=findViewById(R.id.progressBar);
        card=findViewById(R.id.card);

        viewPager = findViewById(R.id.view_pager_slider);
        dotsLayout = findViewById(R.id.layoutDots);

        db = new DatabaseHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        Logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_Conformation();
            }
        });

        handler = new Handler();

        handler.postDelayed(runnable, 4000);
        runnable.run();

        CustomViewPagerAdapter viewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if (position == 0) {
                    currentPage = 0;
                } else if (position == 1) {
                    currentPage = 1;
                } else if (position == 2) {
                    currentPage = 2;
                }else {
                    currentPage = 3;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.faculty_menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();


        DrawerAdapter drawadapter = new DrawerAdapter(Arrays.asList(
                createItemFor(Home).setChecked(true),
                createItemFor(MyAccount),
                createItemFor(Mrk_Attn),
                createItemFor(View_today),
                createItemFor(TimeTable),
                createItemFor(ExamDuty),
                new SpaceItem(18),
                createItemFor(About),
                createItemFor(SHARE),
                createItemFor(Contact_Dev),
                new SpaceItem(48),
                createItemFor(Exit)));

        drawadapter.setListener(this);
        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawadapter);

        drawadapter.setSelected(Home);

        if (!NetConnectivityCheck.isNetworkAvailable(FacultyHomeActivity.this)) {
            ViewDialogNet alert = new ViewDialogNet();
            alert.showDialog(FacultyHomeActivity.this, "Please, enable internet connection before using this app !!");
        }

        MRK_ATTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lect=lec+"_"+bat+"_"+semi;
                if (lec.contains("Select Lecture")){
                    Toast.makeText(getApplicationContext(), "Please select lecture !", Toast.LENGTH_SHORT).show();
                }
                else if (bat.contains("Select Course")){
                    Toast.makeText(getApplicationContext(), "Please select batch !", Toast.LENGTH_SHORT).show();
                }
                else if (semi.contains("Select Semester")){
                    Toast.makeText(getApplicationContext(), "Please select semester !", Toast.LENGTH_SHORT).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    startAttendance(lect, fullname, id, email, status);
                }
            }
        });

        // Spinner click listener
        Lecture.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                lec = parent.getItemAtPosition(position).toString();

                //Toast.makeText(parent.getContext(), "Selected: " + lec, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bat = parent.getItemAtPosition(position).toString();

                //Toast.makeText(parent.getContext(), "Selected: " + bat, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Sem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semi = parent.getItemAtPosition(position).toString();

                //Toast.makeText(parent.getContext(), "Selected: " + semi, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        // Fetching user details from database
        name = user.get("name");
        last = user.get("surname");
        email = user.get("email");
        id = user.get("id_no");

        fullname=name+" "+last;

        // Displaying the user details on the screen
        txtName.setText(fullname);
        txtEmail.setText(email);
        txtID.setText(id);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void logoutUser() {
        db.resetTables();
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(FacultyHomeActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.scale,R.anim.scale);
        finish();
    }

    public class ViewDialogNet {

        public void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_box);

            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (!NetConnectivityCheck.isNetworkAvailable(FacultyHomeActivity.this)) {
                        ViewDialogNet alert = new ViewDialogNet();
                        alert.showDialog(FacultyHomeActivity.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });
            dialog.show();
        }
    }

    private void startAttendance(final String lecture, final String name, final String id_no, final String email, final String status) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                Functions.Start_Attendance_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response);

                try {
                    JSONObject jObj = new JSONObject(response.substring(response.indexOf("{"), response.lastIndexOf("}") + 1));
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        progressBar.setVisibility(View.INVISIBLE);
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg, Toast.LENGTH_LONG).show();
                        if (errorMsg.equals("Attendance grant sucessfully!")){
                            Intent ii=new Intent(getApplicationContext(), QrGenerator.class);
                            ii.putExtra("response", errorMsg);
                            ii.putExtra("email", email);
                            ii.putExtra("lect", lec);
                            ii.putExtra("lecture", lecture);
                            ii.putExtra("fullname", fullname);
                            ii.putExtra("id", id);
                            ii.putExtra("batch", bat);
                            ii.putExtra("sem", semi);
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(ii);
                            finish();
                        }
                        else if (errorMsg.equals("Attendance already granted sucessfully!")){
                            Intent ii=new Intent(getApplicationContext(), QrGenerator.class);
                            ii.putExtra("response", errorMsg);
                            ii.putExtra("email", email);
                            ii.putExtra("lect", lec);
                            ii.putExtra("lecture", lecture);
                            ii.putExtra("fullname", fullname);
                            ii.putExtra("id", id);
                            ii.putExtra("batch", bat);
                            ii.putExtra("sem", semi);
                            progressBar.setVisibility(View.INVISIBLE);
                            startActivity(ii);
                            finish();
                        }
                    } else {
                        Toast.makeText(FacultyHomeActivity.this, "Server error!", Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<>();
                params.put("lecture", lecture);
                params.put("name", name);
                params.put("id_no", id_no);
                params.put("email", email);
                params.put("status", status);
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq, tag_string_req);
    }



    @Override
    public void onItemSelected(int position) {
        if (position== MyAccount) {
            startActivity(new Intent(this, FacultyAccount.class));
        }if (position== TimeTable) {
            Intent intent = new Intent(getBaseContext(), FacultyTimeTable.class);
            startActivity(intent);
        }if (position== Mrk_Attn) {
            Intent intent = new Intent(getBaseContext(), Faculty_Mark_Attendance.class);
            startActivity(intent);
        }if (position== View_today) {
            Intent intent = new Intent(getBaseContext(), Faculty_View_Attendance.class);
            startActivity(intent);
        }if (position== ExamDuty) {
            Intent intent = new Intent(getBaseContext(), FacultyExamPlan.class);
            startActivity(intent);
        }if (position== About) {
            startActivity(new Intent(this, AboutUs_Faculty.class));
        }if (position == SHARE) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT," Hey! guys I just found an app on play store name "+"\"Cloud Attendance\""+" Click the link below to download this application. ");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT,"");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            startActivity(sendIntent);
        }if (position == Contact_Dev) {
            Intent mailIntent =new Intent();
            mailIntent.setAction(Intent.ACTION_SEND);
            mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "dilipsingh.d98@gmail.com" });
            mailIntent.setData(Uri.parse("dilipsingh.d98@gmail.com"));
            mailIntent.setPackage("com.google.android.gm");
            mailIntent.setType("text/plain");
            mailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            startActivity(mailIntent);
        }if (position== Exit) {
            exit_Conformation();
        }
        slidingRootNav.closeMenu();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.white))
                .withTextTint(color(R.color.white))
                .withSelectedIconTint(color(R.color.navfun))
                .withSelectedTextTint(color(R.color.navfun));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.Titles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.Icons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }
    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast=Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
            toast.show();
            this.lastBackPressTime = System.currentTimeMillis();
        }
        else {
            if (toast != null) {
                toast.cancel();
                finish();
            }
            super.onBackPressed();
        }
    }
    private void logout_Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(FacultyHomeActivity.this, R.style.MyDialogTheme);

        dialogBuilder.setIcon(R.drawable.logo);
        dialogBuilder.setTitle("Logout");
        dialogBuilder.setMessage("Do you want to logout?");
        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
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

        final androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Conform Delete Action Method
                        logoutUser();
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void exit_Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(FacultyHomeActivity.this, R.style.MyDialogTheme);

        dialogBuilder.setIcon(R.drawable.logo);
        dialogBuilder.setTitle("Exit");
        dialogBuilder.setMessage("Do you want to exit application?");

        dialogBuilder.setCancelable(false);


        dialogBuilder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
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

        final androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Conform Delete Action Method
                        System.exit(0);
                        dialog.dismiss();
                    }
                });
            }
        });

        alertDialog.show();
    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[4];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }
}