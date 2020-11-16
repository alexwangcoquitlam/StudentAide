package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

  
  
    //Yufeng: I'm not sure if View view needs to be passed into these functions because when the functions are called
    //and the new Intent is created, startActivity will open the class that is associated with the Intent
  
    public void loginScreen(View view){
        Intent login = new Intent(this, loginActivity.class);
        startActivity(login);
    }

    public void calendarScreen(View view){
        Intent calendar = new Intent(this, calendarActivity.class);
        startActivity(calendar);
    }

    public void statsScreen(View view){
        Intent stats = new Intent(this, studyStatistics.class);
        startActivity(stats);
    }

    public void coursesScreen(View view){
        Intent courses = new Intent(this, coursesActivity.class);
        startActivity(courses);
    }

    public void studyScreen(View view){
        Intent study = new Intent(this, studySession.class);
        startActivity(study);
    }
}