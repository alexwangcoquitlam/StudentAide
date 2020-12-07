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

    Button joinCourseClicked, getQuizzes;
    Spinner coursesDisplay, quizDisplay;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();

    DocumentReference studentRef;
    String studentDocumentId;
    String chosenCourseID;
    String choice;

    ArrayList<String> courses = new ArrayList<String>();
    Map<String, String> coursesHM = new HashMap<>();

    Map<String, String> quizHM = new HashMap<>();

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

            getQuizzes = findViewById(R.id.openQuizzes);
            coursesDisplay = findViewById(R.id.courseDropdown);
            quizDisplay = findViewById(R.id.quizDropDown);
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
                        getQuizzes.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            getQuizzes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(choice.equals("Choose a Course")){
                        Toast.makeText(getActivity(), "Please choose a valid course.", Toast.LENGTH_SHORT).show();
                    }else {
                        getQuizzes.setVisibility(View.INVISIBLE);
                        retrieveQuizzes(chosenCourseID, new QuizCallback() {
                            @Override
                            public void quizCall(ArrayList<String> quizNames) {
                                ArrayAdapter<String> quizAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, quizNames);
                                quizDisplay.setAdapter(quizAdapter);
                            }
                        });
                    }
                }
            });

            quizDisplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String choice = quizDisplay.getItemAtPosition(position).toString();

                    if(choice.equals("Select a Quiz")){
                        Toast.makeText(getActivity(),"Please select a quiz.", Toast.LENGTH_SHORT).show();
                    }else{
                        String chosenQuizID = quizHM.get(choice);

                        Intent intent = new Intent(getActivity(), QuizActivity.class);
                        intent.putExtra("quizID", chosenQuizID);
                        startActivity(intent);

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
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

    public interface Callback {
        void call();
    }

    public interface QuizCallback{
        void quizCall(ArrayList<String> quizNames);
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

    public void retrieveQuizzes(String chosenCourseID, QuizCallback quizCallback){

        DocumentReference chosenCourseDocRef = db.collection("Courses").document(chosenCourseID);

        db.collection("QuizDefs")
                .whereEqualTo("course_SA_ID", chosenCourseDocRef)
                .orderBy("releaseDate", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<String> quizList = new ArrayList<>();
                            quizList.add(0, "Select a Quiz");
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String name = document.getString("quiz_Name");
                                String quizID = document.getId();

                                quizList.add(name);
                                quizHM.put(name, quizID);
                            }
                            quizCallback.quizCall(quizList);
                        }else{
                            Log.d("Yu", "Error retrieving quiz documents");
                        }
                    }
                });
    }



    public void courseCreate(View view){
        Intent create = new Intent(this, CourseCreation.class);
        startActivity(create);
    }


}