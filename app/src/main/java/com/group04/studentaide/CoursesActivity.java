/*
Written by: Yufeng Luo, Jason Leung
 */
package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CoursesActivity extends AppCompatActivity {

    Button createCourseClicked;
    Button joinCourseClicked;
    Spinner coursesDisplay;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();

    DocumentReference studentRef;
    String studentDocumentId;

    ArrayList<String> courses = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        joinCourseClicked = findViewById(R.id.courseJoin);

        if (user == null) {

            Log.v("Hareye", "Test");

        } else {

            grabDocumentReference();

            joinCourseClicked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), JoinCourseActivity.class);
                    startActivity(intent);
                }
            });

            coursesDisplay = findViewById(R.id.courseDropdown);
            grabCourses(new Callback() {
                @Override
                public void call() {
                    ArrayAdapter<String> courseAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
                    coursesDisplay.setAdapter(courseAdapter);
                }
            });

        }

    }

    private CoursesActivity getActivity() {

        return this;

    }

    // Return current users document ID and document reference path
    public void grabDocumentReference() {

        studentDocumentId = infoRetrieve.getDocumentID();
        studentRef = db.collection("Students").document(studentDocumentId);

    }

    private void grabCourses(Callback callback) {

        db.collection("StudentCourses")
                .whereEqualTo("Student_SA_ID", studentRef)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String courseName = (String) document.get("CourseName");

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


    public void educatorRegister(View view){
        Intent educator = new Intent(this, CourseCreationEducator.class);
        startActivity(educator);
    }

}