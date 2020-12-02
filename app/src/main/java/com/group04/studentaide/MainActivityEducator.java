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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;

public class MainActivityEducator extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();

    InformationRetrievalEducator infoRetrieve = InformationRetrievalEducator.getInstance();

    private Button loginScreen, coursesScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_educator);

        loginScreen = findViewById(R.id.loginOpen);
        coursesScreen = findViewById(R.id.coursesOpen);

        infoRetrieve.updateID();

        loginScreen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent loginGuest = new Intent(MainActivityEducator.this, LoginActivityGuest.class);
                startActivity(loginGuest);
            }
        });

        coursesScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent courses = new Intent(MainActivityEducator.this, CoursesActivityEducator.class);
                startActivity(courses);
            }
        });

    }


}