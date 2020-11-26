/*
 * Written by Alexander Wang
 *
 * The main screen has no real functions, just some buttons that lead to other functional pages.
 */

package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();

    private Button loginScreen;
    private TextView greeting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginScreen = findViewById(R.id.loginOpen);
        greeting = findViewById(R.id.greetingLabelMain);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        infoRetrieve.updateID();

        if(user != null){
            createGreeting();
        }
        else{
            greeting.setText("Welcome back.");
        }
        loginScreen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent loginGuest = new Intent(MainActivity.this, LoginActivityGuest.class);
                startActivity(loginGuest);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.nav_study:
                            Intent study = new Intent(MainActivity.this, StudySession.class);
                            startActivity(study);
                            break;
                        case R.id.nav_courses:
                            Intent courses = new Intent(MainActivity.this, CoursesActivity.class);
                            startActivity(courses);
                            break;
                    }
                    return true;
                }
            };

    //Yufeng: I'm not sure if View view needs to be passed into these functions because when the functions are called
    //and the new Intent is created, startActivity will open the class that is associated with the Intent

    public void calendarScreen(View view){
        Intent calendar = new Intent(this, CalendarActivity.class);
        startActivity(calendar);
    }

    public void statsScreen(View view){
        Intent stats = new Intent(this, StudyStatistics.class);
        startActivity(stats);
    }

    private void createGreeting(){
        String fullName = user.getDisplayName();
        String arr[] = fullName.split(" ", 2);

        String firstName = arr[0];
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        greeting.setText("Hello, " + firstName + ".");
    }

}