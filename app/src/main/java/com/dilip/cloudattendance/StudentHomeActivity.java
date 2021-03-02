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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.dilip.cloudattendance.Navigation.DrawerAdapter;
import com.dilip.cloudattendance.Navigation.DrawerItem;
import com.dilip.cloudattendance.Navigation.SimpleItem;
import com.dilip.cloudattendance.Navigation.SpaceItem;
import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.SessionManager;
import com.dilip.cloudattendance.imageSliderFragment.CustomViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StudentHomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = StudentHomeActivity.class.getSimpleName();
    private SlidingRootNav slidingRootNav;
    private static final int Home = 0;
    private static final int My_Account = 1;
    private static final int Time_table = 2;
    private static final int Feedback = 3;
    private static final int Exam_Seating = 4;
    private static final int Fees_Structure = 5;
    private static final int About = 7;
    private static final int share = 8;
    private static final int Contact_Dev = 9;
    private static final int Exit = 11;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private ImageView Logout_btn;
    private Toast toast;
    private long lastBackPressTime = 0;

    TextView txtName,txtEmail,txtUrn,txtCourse,txtYear,txtSem;
    private Button Mrk_Att;
    private SessionManager session;
    private DatabaseHandler db;
    private HashMap<String, String> user = new HashMap<>();
    Spinner spinner;
    public String item="Select Lecture";
    public String Name,Surname,Fullname,Email,Urn,Course,Year,Sem,Table_name,Status,secret_code;

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
        setContentView(R.layout.activity_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Logout_btn = findViewById(R.id.logout_icon);
        txtName = findViewById(R.id.Name);
        txtEmail = findViewById(R.id.email);
        txtUrn = findViewById(R.id.urn_no);
        txtCourse = findViewById(R.id.course);
        txtYear = findViewById(R.id.year);
        txtSem = findViewById(R.id.sem);
        spinner = findViewById(R.id.sub);
        Mrk_Att = findViewById(R.id.mrk_att);
        card=findViewById(R.id.card);

        viewPager = findViewById(R.id.view_pager_slider);
        dotsLayout = findViewById(R.id.layoutDots);

        Intent in= getIntent();
        Bundle b = in.getExtras();

        if(b!=null)
        {
            secret_code =(String) b.get("code");
            Toast.makeText(this, secret_code, Toast.LENGTH_SHORT).show();
        }

        if (!NetConnectivityCheck.isNetworkAvailable(StudentHomeActivity.this)) {
            ViewDialogNet alert = new ViewDialogNet();
            alert.showDialog(StudentHomeActivity.this, "Please, enable internet connection before using this app !!");
        }

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        // session manager
        session = new SessionManager(getApplicationContext());

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

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        Logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_Conformation();
            }
        });

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.student_menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter drawadapter = new DrawerAdapter(Arrays.asList(
                createItemFor(Home).setChecked(true),
                createItemFor(My_Account),
                createItemFor(Time_table),
                createItemFor(Feedback),
                createItemFor(Exam_Seating),
                createItemFor(Fees_Structure),
                new SpaceItem(18),
                createItemFor(About),
                createItemFor(share),
                createItemFor(Contact_Dev),
                new SpaceItem(48),
                createItemFor(Exit)));

        drawadapter.setListener(this);
        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawadapter);

        drawadapter.setSelected(Home);


        // Fetching user details from database
        Name = user.get("name");
        Surname = user.get("surname");
        Email = user.get("email");
        Urn = user.get("urn_no");
        Course = user.get("course");
        Year = user.get("year");
        Sem = user.get("semester");

        // Displaying the user details on the screen
        Fullname = Name + " " + Surname;
        txtName.setText(Fullname);
        txtEmail.setText(Email);
        txtUrn.setText(Urn);
        txtCourse.setText(Course);
        txtYear.setText(Year);
        txtSem.setText(Sem);

        Status = "Present";

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> lecture = new ArrayList<String>();
        lecture.add("Select Lecture");
        lecture.add("CyberForensic");
        lecture.add("CloudSecurity");
        lecture.add("CloudSolutionArchitect");
        lecture.add("AdvanceStorage");

        spinner.setSelection(0);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_view, lecture);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_view);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        Mrk_Att.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.equals("Select Lecture")) {
                    Toast.makeText(getApplicationContext(), "Please select your lecture !", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i=new Intent(getApplicationContext(),Qr_Scanner.class);
                    i.putExtra("table_name", Table_name);
                    startActivity(i);
                    finish();
                    //Toast.makeText(getApplicationContext(), "Attendance marked", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class ViewDialogNet {

        void showDialog(Activity activity, String msg) {
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
                    if (!NetConnectivityCheck.isNetworkAvailable(StudentHomeActivity.this)) {
                        ViewDialogNet alert = new ViewDialogNet();
                        alert.showDialog(StudentHomeActivity.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });

            dialog.show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
        Table_name=item.trim()+"_"+Course.trim()+"_"+Sem;
        //Toast.makeText(parent.getContext(), Table_name, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void logoutUser() {
        db.resetTables();
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(StudentHomeActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.scale,R.anim.scale);
        finish();
    }
    @Override
    public void onItemSelected(int position) {
        if (position== My_Account) {
            startActivity(new Intent(this, StudentAccount.class));
        }if (position== Feedback) {
            startActivity(new Intent(this, Student_Feedback.class));
        }if (position== Exam_Seating) {
            startActivity(new Intent(this, ExamSeatingArrangement.class));
        }if (position== Time_table) {
            startActivity(new Intent(this, StudentTimeTable.class));
        }if (position== Fees_Structure) {
            startActivity(new Intent(this, StudentFeesStructure.class));
        }if (position== About) {
            startActivity(new Intent(this, AboutUs_Student.class));
        }if (position == share) {
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
        return getResources().getStringArray(R.array.StudentTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.StudentIcons);
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
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(StudentHomeActivity.this, R.style.MyDialogTheme);

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
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(StudentHomeActivity.this, R.style.MyDialogTheme);

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


