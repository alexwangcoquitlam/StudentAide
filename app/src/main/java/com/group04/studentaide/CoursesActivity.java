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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.TreeMap;


public class CoursesActivity extends AppCompatActivity {

    Button createCourseClicked;
    Button joinCourseClicked;
    Spinner coursesDisplay;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String uid = null;
    ArrayList<String> courses = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        joinCourseClicked = findViewById(R.id.courseJoin);


        if (user != null) {
            uid = user.getUid();

        } else {
            uid = "No associated user";
        }


        joinCourseClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), JoinCourseActivity.class);
                startActivity(intent);
            }
        });

        /*createCourseClicked.findViewById(R.id.courseCreate);

        createCourseClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseCreate(v);
            }
        });*/

        CourseSingleton courseList = CourseSingleton.getInstance();
        ArrayList<String> hashKeys = courseList.courseKeys;

        coursesDisplay = findViewById(R.id.courseDropdown);
        grabCourses(new Callback() {
            @Override
            public void call() {
                ArrayAdapter<String> courseAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
                coursesDisplay.setAdapter(courseAdapter);
            }
        });
        ArrayAdapter<String> courseAdapter= new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, courses);
        coursesDisplay.setAdapter(courseAdapter);

        /*

        ArrayAdapter<String> arrAdapt = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, hashKeys);
        coursesDisplay.setAdapter(arrAdapt);
        coursesDisplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getItemAtPosition(position).toString();
                //random data inserted to see what appears
                //Double choiceTime = courseList.getStudyTime(choice);
                //Toast.makeText(getApplicationContext(), choice + ": " + choiceTime, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        */

    }

    private CoursesActivity getActivity() {

        return this;

    }

    private void grabCourses(Callback callback) {

        db.collection("Courses")
                .whereEqualTo("owner uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String courseName = (String) document.get("name");

                                courses.add(courseName);
                                callback.call();
                            }
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