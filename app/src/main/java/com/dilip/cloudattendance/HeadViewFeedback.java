package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dilip.cloudattendance.fragment.CommentsFragment;
import com.dilip.cloudattendance.fragment.RatingsFragment;
import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class HeadViewFeedback extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView Logout_btn;
    private SessionManager session;
    private DatabaseHandler db;
    private Toast toast;
    private long lastBackPressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_view_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Logout_btn = findViewById(R.id.logout_icon);

        db = new DatabaseHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        if (!NetConnectivityCheck.isNetworkAvailable(HeadViewFeedback.this)) {
            ViewDialogNet alert = new ViewDialogNet();
            alert.showDialog(HeadViewFeedback.this, "Please, enable internet connection before using this app !!");
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout_Conformation();
            }
        });
    }
    private void logoutUser() {
        db.resetTables();
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(HeadViewFeedback.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.scale,R.anim.scale);
        finish();
    }
    private void logout_Conformation() {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(HeadViewFeedback.this, R.style.MyDialogTheme);

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
                    if (!NetConnectivityCheck.isNetworkAvailable(HeadViewFeedback.this)) {
                        HeadViewFeedback.ViewDialogNet alert = new HeadViewFeedback.ViewDialogNet();
                        alert.showDialog(HeadViewFeedback.this, "Please, enable internet connection before using this app !!");
                    }
                }
            });

            dialog.show();
        }
    }
    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        // set text
        tabOne.setText("Ratings");
        // set icon
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_ratings, 0, 0);
        // set postion
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Comments");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_fedback_white, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }
    private void setupViewPager(ViewPager viewPager) {
        HeadViewFeedback.ViewPagerAdapter adapter = new HeadViewFeedback.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new RatingsFragment(), "Ratings");
        adapter.addFrag(new CommentsFragment(), "Comments");
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
    public void onBackPressed() {
        if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
            toast= Toast.makeText(this, "Press back again to close this app", Toast.LENGTH_LONG);
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
}
