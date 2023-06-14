package com.kayzsystems.wemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

//This Activity is responsible for displaying the app splash screen
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //This is a delayed action. It runs a command after a specified time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //This is the action to be run. Launch the Main Activity
                Intent openStartingPoint = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(openStartingPoint);
            }
        } ,3000);   //This specifies the time in milliseconds after which above action will run


    }
}