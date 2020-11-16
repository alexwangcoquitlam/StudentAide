package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*  File Name: studySessionPlan.java
    Team: ProjectTeam04
    Written By: Jason Leung
    Changes:
        November 14th - Draft 1 of Version 1
    Bugs:
        Haven't tested.
    Notes:
        Maybe add a start date and end date?
 */

public class studySessionPlan extends AppCompatActivity {

    Spinner courseDisplayPlan;
    Spinner month;
    Spinner day;
    Spinner year;

    Spinner timeStartHour;
    Spinner timeStartMinute;

    Spinner timeEndHour;
    Spinner timeEndMinute;

    Button planSession;

    CourseSingleton courseList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String uid = null;
    ArrayList<String> courses = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        month = (Spinner) findViewById(R.id.month);
        day = (Spinner) findViewById(R.id.day);
        year = (Spinner) findViewById(R.id.year);
        courseDisplayPlan = (Spinner) findViewById(R.id.coursesSpinner);
        timeStartHour = (Spinner) findViewById(R.id.timeStartHour);
        timeStartMinute = (Spinner) findViewById(R.id.timeStartMinute);
        timeEndHour = (Spinner) findViewById(R.id.timeEndHour);
        timeEndMinute = (Spinner) findViewById(R.id.timeEndMinute);
        planSession = (Button) findViewById(R.id.planSessionButton);

        if (user != null) {
            uid = user.getUid();
        } else {
            uid = "No associated user";
        }

        courseList = CourseSingleton.getInstance();

        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        month.setAdapter(monthsAdapter);

        String[] days = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        day.setAdapter(daysAdapter);

        String[] years = new String[]{"2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"};
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        year.setAdapter(yearsAdapter);

        // When Firebase collection that stores a users uid and all the courses they're enrolled in is created, change this to grab the data from database

        grabCourses(new Callback() {
            @Override
            public void call() {
                ArrayAdapter<String> courseAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
                courseDisplayPlan.setAdapter(courseAdapter);
            }
        });

        String[] hours = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String[] minutes = new String[]{"00", "15", "30", "45"};

        ArrayAdapter<String> timeStartHourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        timeStartHour.setAdapter(timeStartHourAdapter);

        ArrayAdapter<String> timeStartMinuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutes);
        timeStartMinute.setAdapter(timeStartMinuteAdapter);

        ArrayAdapter<String> timeEndHourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        timeEndHour.setAdapter(timeEndHourAdapter);

        ArrayAdapter<String> timeEndMinuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutes);
        timeEndMinute.setAdapter(timeEndMinuteAdapter);

        planSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If Spinner inputs are not empty, assign selected Spinner item to variables.
                String monthInput = month.getSelectedItem().toString();
                String dayInput = day.getSelectedItem().toString();
                String yearInput = year.getSelectedItem().toString();

                int day = Integer.parseInt(dayInput);
                int year = Integer.parseInt(yearInput);

                String timeStartHourInput = timeStartHour.getSelectedItem().toString();
                String timeStartMinuteInput = timeStartMinute.getSelectedItem().toString();

                int timeStartHour = Integer.parseInt(timeStartHourInput);
                int timeStartMinute = Integer.parseInt(timeStartMinuteInput);

                String timeEndHourInput = timeEndHour.getSelectedItem().toString();
                String timeEndMinuteInput = timeEndMinute.getSelectedItem().toString();

                int timeEndHour = Integer.parseInt(timeEndHourInput);
                int timeEndMinute = Integer.parseInt(timeEndMinuteInput);

                if (courseDisplayPlan.getSelectedItem() == null) {

                    Toast.makeText(getApplicationContext(), "Please select a course.", Toast.LENGTH_SHORT).show();

                } else {

                    String courseInput = courseDisplayPlan.getSelectedItem().toString();

                    // Assign monthInput an integer value in order to compare dates
                    int monthInt = 0;

                    switch (monthInput) {
                        case "January":
                            monthInt = 1;
                            break;
                        case "February":
                            monthInt = 2;
                            break;
                        case "March":
                            monthInt = 3;
                            break;
                        case "April":
                            monthInt = 4;
                            break;
                        case "May":
                            monthInt = 5;
                            break;
                        case "June":
                            monthInt = 6;
                            break;
                        case "July":
                            monthInt = 7;
                            break;
                        case "August":
                            monthInt = 8;
                            break;
                        case "September":
                            monthInt = 9;
                            break;
                        case "October":
                            monthInt = 10;
                            break;
                        case "November":
                            monthInt = 11;
                            break;
                        case "December":
                            monthInt = 12;
                            break;
                    }

                    // Get the actual month from the integer value, and the length of that month
                    Month month = Month.of(monthInt);
                    int monthLength = month.length(false);

                    LocalDateTime currentDate = LocalDateTime.now();
                    LocalDateTime studyDate = LocalDateTime.of(year, month, day, timeStartHour, timeStartMinute);

                    // If user input study date has already passed or if month does not contain that date, give an error message
                    if (studyDate.isBefore(currentDate) == true || day > monthLength) {

                        Toast.makeText(getApplicationContext(), "Please enter a valid date.", Toast.LENGTH_SHORT).show();

                    } else {

                        // Store the study session into database, maybe create a Singleton for study sessions?
                        Map<String, Object> sessions = new HashMap<>();
                        sessions.put("uid", uid);
                        sessions.put("course", courseInput);
                        sessions.put("month", monthInt);
                        sessions.put("day", day);
                        sessions.put("year", year);
                        sessions.put("timeStartHour", timeStartHour);
                        sessions.put("timeStartMinute", timeStartMinute);
                        sessions.put("timeEndHour", timeEndHour);
                        sessions.put("timeEndMinute", timeEndMinute);

                        db.collection("PlannedSessions")
                                .add(sessions)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(studySessionPlan.this, "New Session Planned", Toast.LENGTH_SHORT).show();
                                        Intent returnStudy = new Intent(studySessionPlan.this, studySession.class);
                                        startActivity(returnStudy);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("StudySessionPlan", "Error occurred when adding session to Firebase.", e);
                                    }
                                });

                    }
                }
            }
        });

    }

    private studySessionPlan getActivity() {

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

}
