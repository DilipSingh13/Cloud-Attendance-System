package com.dilip.cloudattendance;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    ImageView IMG;
    TextView Lable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        IMG=findViewById(R.id.logo);
        Lable=findViewById(R.id.lable);

        Animation scaleAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale);
        IMG.startAnimation(scaleAnim);

        Animation slide = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right);
        Lable.startAnimation(slide);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(i);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
