package com.group04.studentaide;

/*
Written By Yufeng Luo
Tested functionality locally using activity_course_creation.xml, and coursesActivity.xml
Currently no implementation for institution field or Quizzes switch --> will be done in v2/v3
 */

/*
    Quick little temporary fix for me to test my functions. Not the final product since I believe Yufeng is working on this.
        - Jason
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CourseCreation extends AppCompatActivity {

    EditText mInputCourseName;
    EditText mInputInstitution;
    Switch mQuizzes;
    Button mCreateCourse;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();

    DocumentReference studentRef;
    String studentDocumentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_creation); // Set to study session XML

        grabDocumentReference();

        mInputCourseName = findViewById(R.id.inputCourseName);
        mCreateCourse = findViewById(R.id.createButton);

        //Create our LinkedHashMap object from singleton
        CourseSingleton courseList = CourseSingleton.getInstance();

        //After user enters details and clicks create course
        //They will be taken back to the main course activity page
        mCreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourseFire();
            }
        });

    }

    public void educatorCreateCourse(View view){
        Intent educator = new Intent(this, CourseCreationEducator.class);
        startActivity(educator);
    }


    public void grabDocumentReference() {

        studentDocumentId = infoRetrieve.getDocumentID();
        studentRef = db.collection("Students").document(studentDocumentId);

    }

    private void createCourseFire(){

        String name = mInputCourseName.getText().toString().trim();

        if (TextUtils.isEmpty(name)){
            mInputCourseName.setError("Please enter a course name");
            mInputCourseName.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        else {

            String placeholder = "0";

            Map<String, Object> Courses = new HashMap<>();
            Courses.put("CourseName", name);
            Courses.put("Student_SA_ID", studentRef);
            Courses.put("Course_SA_ID", placeholder);

            db.collection("StudentCourses")
                    .add(Courses)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("courseAdded", "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(CourseCreation.this, "Course created", Toast.LENGTH_LONG).show();
                            Intent returnCourses = new Intent(CourseCreation.this, CoursesActivity.class);
                            startActivity(returnCourses);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("courseAddFail", "Error adding document", e);
                        }
                    });
        }
    }
}