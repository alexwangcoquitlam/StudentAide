/*
Written by: Yufeng Luo, Jason Leung
 */
package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity {

    Button createCourseClicked;
    Button joinCourseClicked;
    Button openQuizzes;
    Spinner coursesDisplay;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();

    DocumentReference studentRef;
    String studentDocumentId;
    String chosenCourseID;
    String choice;

    ArrayList<String> courses = new ArrayList<String>();
    Map<String, String> coursesHM = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        joinCourseClicked = findViewById(R.id.courseJoin);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //user == null here
        if (user == null) {

            Toast.makeText(getActivity(), "Please sign in.", Toast.LENGTH_SHORT).show();
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);
        } else {

            grabDocumentReference();

            joinCourseClicked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), JoinCourseActivity.class);
                    startActivity(intent);
                }
            });

            openQuizzes = findViewById(R.id.openQuizzes);
            coursesDisplay = findViewById(R.id.courseDropdown);
            courses.add(0, "Choose a Course");

            grabCourses(new Callback() {
                @Override
                public void call() {
                    ArrayAdapter<String> courseAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
                    coursesDisplay.setAdapter(courseAdapter);
                }
            });

            coursesDisplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    choice = coursesDisplay.getItemAtPosition(position).toString();

                    if(!choice.equals("Choose a Course")){
                        chosenCourseID = coursesHM.get(choice);

                        //Show buttons
                        openQuizzes.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            openQuizzes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(choice.equals("Choose a Course")){
                        Toast.makeText(getActivity(), "Please choose a course.", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(getActivity(), QuizActivity.class);
                        intent.putExtra("Course_SA_ID", chosenCourseID);
                        startActivity(intent);
                    }
                }
            });

        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.nav_study:
                            Intent study = new Intent(CoursesActivity.this, StudySession.class);
                            startActivity(study);
                            break;
                        case R.id.nav_courses:
                            Intent courses = new Intent(CoursesActivity.this, CoursesActivity.class);
                            startActivity(courses);
                            break;
                        case R.id.nav_home:
                            Intent main = new Intent(CoursesActivity.this, MainActivity.class);
                            startActivity(main);
                    }
                    return true;
                }
            };

    private CoursesActivity getActivity() {

        return this;

    }

    // Return current users document ID and document reference path
    public void grabDocumentReference() {

        studentDocumentId = infoRetrieve.getDocumentID();
        studentRef = db.collection("Students").document(studentDocumentId);
        Log.d("Yu", "Student ref grabbed.");

    }

    private void grabCourses(Callback callback) {

        Log.d("Yu", "Now grabbing courses");

        db.collection("StudentCourses")
                .whereEqualTo("Student_SA_ID", studentRef)
                .orderBy("CourseName", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //courses.add(0, "Choose a Course");
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String path;
                                String courseName = document.getString("CourseName");

                                DocumentReference course_SA_ID;
                                if(document.getDocumentReference("Course_SA_ID") != null){
                                    course_SA_ID = document.getDocumentReference("Course_SA_ID");

                                    if(course_SA_ID.getId() != null){
                                        path = course_SA_ID.getId();
                                        Log.d("Yu", "Course_SA_ID = " + path);
                                        coursesHM.put(courseName, path);
                                    }

                                }

                                courses.add(courseName);
                            }
                            callback.call();
                        } else {
                            Log.v("CoursesActivity", "Error occurred when getting data from Firebase.");
                        }
                    }
                });

    }


    public interface Callback {
        void call();
    }

    public void courseCreate(View view){
        Intent create = new Intent(this, CourseCreation.class);
        startActivity(create);
    }


}