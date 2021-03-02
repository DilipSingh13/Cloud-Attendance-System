package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.dilip.cloudattendance.Navigation.DrawerAdapter;
import com.dilip.cloudattendance.Navigation.DrawerItem;
import com.dilip.cloudattendance.Navigation.SimpleItem;
import com.dilip.cloudattendance.Navigation.SpaceItem;
import com.dilip.cloudattendance.fragment.AddFacultyFragment;
import com.dilip.cloudattendance.fragment.AddStudentFragment;
import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.SessionManager;
import com.google.android.material.tabs.TabLayout;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminHomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, DrawerAdapter.OnItemSelectedListener {

    private static final String TAG = AdminHomeActivity.class.getSimpleName();
    private SlidingRootNav slidingRootNav;
    private static final int Home = 0;
    private static final int My_Account = 1;
    private static final int Add_User = 2;
    private static final int Delete_User = 3;
    private static final int Block_User = 4;
    private static final int View_Attendance = 5;
    private static final int Upload_Timetable = 6;
    private static final int Upload_Fee = 7;
    private static final int Feedback = 8;
    private static final int About = 10;
    private static final int Share = 11;
    private static final int Contact_Dev = 12;
    private static final int Exit = 14;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private ImageView Logout_btn;
    private SessionManager session;
    private DatabaseHandler db;

    private Toast toast;
    private long lastBackPressTime = 0;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Logout_btn = findViewById(R.id.logout_icon);

        if (!NetConnectivityCheck.isNetworkAvailable(AdminHomeActivity.this)) {
            ViewDialogNet alert = new ViewDialogNet();
            alert.showDialog(AdminHomeActivity.this, "Please, enable internet connection before using this app !!");
        }
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

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

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();


        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter drawadapter = new DrawerAdapter(Arrays.asList(
                createItemFor(Home).setChecked(true),
                createItemFor(My_Account),
                createItemFor(Add_User),
                createItemFor(Delete_User),
                createItemFor(Block_User),
                createItemFor(View_Attendance),
                createItemFor(Upload_Timetable),
                createItemFor(Upload_Fee),
                createItemFor(Feedback),
                new SpaceItem(18),
                createItemFor(About),
                createItemFor(Share),
                createItemFor(Contact_Dev),
                new SpaceItem(48),
                createItemFor(Exit)));

        drawadapter.setListener(this);
        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(drawadapter);

        drawadapter.setSelected(Home);

        // Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                    if (!NetConnectivityCheck.isNetworkAvailable(AdminHomeActivity.this)) {
                        ViewDialogNet alert = new ViewDialogNet();
                        alert.showDialog(AdminHomeActivity.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });

            dialog.show();
        }
    }
    private void setupTabIcons() {

        // create tab with text and icon

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        // set text
        tabOne.setText("Student");
        // set icon
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_student_24dp, 0, 0);
        // set postion
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Faculty");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_faculty_purple_24dp, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }
    private void setupViewPager(ViewPager viewPager) {
        AdminHomeActivity.ViewPagerAdapter adapter = new AdminHomeActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new AddStudentFragment(), "Student");
        adapter.addFrag(new AddFacultyFragment(), "Faculty");
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onItemSelected(int position) {
        if (position== My_Account) {
            startActivity(new Intent(this, AdminAccount.class));
        } if (position== Feedback) {
            startActivity(new Intent(this, AdminFeedback.class));
        }if (position== Block_User) {
            startActivity(new Intent(this, BlockUnblockUserActivity.class));
        }if (position== Delete_User) {
            startActivity(new Intent(this, AdminDeleteUser.class));
        }if (position== View_Attendance) {
            startActivity(new Intent(this, View_Attendance.class));
        }if (position== Upload_Timetable) {
            startActivity(new Intent(this, UploadTimetableActivity.class));
        }if (position== Upload_Fee) {
            startActivity(new Intent(this, UploadFeeStructureActivity.class));
        }if (position== About) {
            startActivity(new Intent(this, AboutUs_Admin.class));
        }if (position == Share) {
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
        return getResources().getStringArray(R.array.Admin_Titles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.Admin_Icons);
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

    private void logoutUser() {
        db.resetTables();
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.scale,R.anim.scale);
        finish();
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
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.MyDialogTheme);

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
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(AdminHomeActivity.this, R.style.MyDialogTheme);

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
}