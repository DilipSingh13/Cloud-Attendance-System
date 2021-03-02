package com.dilip.cloudattendance;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.dilip.cloudattendance.fragment.AddFacultyFragment;
import com.dilip.cloudattendance.fragment.AddStudentFragment;
import com.dilip.cloudattendance.fragment.BlockStudentFragment;
import com.dilip.cloudattendance.fragment.UnblockStudentFragment;
import com.dilip.cloudattendance.helper.DatabaseHandler;
import com.dilip.cloudattendance.helper.SessionManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class BlockUnblockUserActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SessionManager session;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_unblock_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!NetConnectivityCheck.isNetworkAvailable(BlockUnblockUserActivity.this)) {
            ViewDialogNet alert = new BlockUnblockUserActivity.ViewDialogNet();
            alert.showDialog(BlockUnblockUserActivity.this, "Please, enable internet connection before using this app !!");
        }
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        db = new DatabaseHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

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
                    if (!NetConnectivityCheck.isNetworkAvailable(BlockUnblockUserActivity.this)) {
                        ViewDialogNet alert = new BlockUnblockUserActivity.ViewDialogNet();
                        alert.showDialog(BlockUnblockUserActivity.this, "Please, enable internet connection before using this app !!");
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
        tabOne.setText("Block User");
        // set icon
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_block_white, 0, 0);
        // set postion
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Unblock User");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_unblock_white, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new BlockUnblockUserActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new BlockStudentFragment(), "Block User");
        adapter.addFrag(new UnblockStudentFragment(), "Unblock User");
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
    private void logoutUser() {
        db.resetTables();
        session.setLogin(false);
        // Launching the login activity
        Intent intent = new Intent(BlockUnblockUserActivity.this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.scale,R.anim.scale);
        finish();
    }
}
