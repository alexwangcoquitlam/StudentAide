package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Loadingscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadingscreen);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent loadingScreen = new Intent(Loadingscreen.this, LoginActivity.class);
                startActivity(loadingScreen);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 2000);
    }
}