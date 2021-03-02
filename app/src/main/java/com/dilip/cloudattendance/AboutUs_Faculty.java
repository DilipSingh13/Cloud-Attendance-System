package com.dilip.cloudattendance;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dilip.cloudattendance.helper.DatabaseHandler;

import java.util.HashMap;

public class AboutUs_Faculty extends AppCompatActivity {

    ImageView Instagram,Facebook,Linkedin;
    DatabaseHandler db;
    private HashMap<String, String> user = new HashMap<>();
    String Name,Surname,ID,Fullname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Instagram=findViewById(R.id.insta);
        Facebook=findViewById(R.id.fb);
        Linkedin=findViewById(R.id.link);

        db = new DatabaseHandler(getApplicationContext());
        user = db.getUserDetails();

        Name = user.get("name");
        Surname = user.get("surname");
        ID = user.get("id_no");

        Fullname= Name+" "+Surname;

        Instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/dilip_singh01");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/dilip_singh01")));
                }
            }
        });
        Facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(AboutUs_Faculty.this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
            }
        });
        Linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profile_url = "https://www.linkedin.com/in/dilip-singh-334064140";
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(profile_url));
                    intent.setPackage("com.linkedin.android");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(profile_url)));
                }
            }
        });
    }

    public static String FACEBOOK_URL = "https://www.facebook.com/dilip.singh2001";
    public static String FACEBOOK_PAGE_ID = "https://www.facebook.com/dilip.singh2001";

    //method to get the right URL to use in the intent
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
}
