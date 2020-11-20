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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.Timestamp;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*  File Name: studySessionPlan.java
    Team: ProjectTeam04
    Written By: Jason Leung
    Changes:
        November 14th - Draft 1 of Version 1
        November 18th - Draft 2 of Version 1
    Bugs:
        Haven't tested.
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

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    informationRetrieval infoRetrieve = informationRetrieval.getInstance();

    ArrayList<String> courses = new ArrayList<String>();

    // Need to find a way to grab current users document id
    DocumentReference studentRef;
    String studentDocumentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        grabDocumentReference();

        month = (Spinner) findViewById(R.id.month);
        day = (Spinner) findViewById(R.id.day);
        year = (Spinner) findViewById(R.id.year);
        courseDisplayPlan = (Spinner) findViewById(R.id.coursesSpinner);
        timeStartHour = (Spinner) findViewById(R.id.timeStartHour);
        timeStartMinute = (Spinner) findViewById(R.id.timeStartMinute);
        timeEndHour = (Spinner) findViewById(R.id.timeEndHour);
        timeEndMinute = (Spinner) findViewById(R.id.timeEndMinute);
        planSession = (Button) findViewById(R.id.planSessionButton);

        // Populate month Spinner with the months
        String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        month.setAdapter(monthsAdapter);

        // Populate day Spinner with the days
        String[] days = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        day.setAdapter(daysAdapter);

        // Populate year Spinner with the years
        String[] years = new String[]{"2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"};
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        year.setAdapter(yearsAdapter);

        // Populate grabUsercourses Spinner with the users courses
        grabUserCourses(new Callback() {
            @Override
            public void call() {
                ArrayAdapter<String> courseAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
                courseDisplayPlan.setAdapter(courseAdapter);
            }
        });

        // Define the hours and minutes to be displayed in Spinners
        String[] hours = new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        String[] minutes = new String[]{"00", "15", "30", "45"};

        // Populate timeStartHour Spinner with the hours
        ArrayAdapter<String> timeStartHourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        timeStartHour.setAdapter(timeStartHourAdapter);

        // Populate timeStartMinute Spinner with the minutes
        ArrayAdapter<String> timeStartMinuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutes);
        timeStartMinute.setAdapter(timeStartMinuteAdapter);

        // Populate timeEndHour Spinner with the hours
        ArrayAdapter<String> timeEndHourAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        timeEndHour.setAdapter(timeEndHourAdapter);

        // Populate timeEndMinute Spinner with the minutes
        ArrayAdapter<String> timeEndMinuteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutes);
        timeEndMinute.setAdapter(timeEndMinuteAdapter);

        // When the user clicks planSession Button, then store the planned session into the database
        planSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeSession();
            }
        });

    }

    // Returns current activity
    private studySessionPlan getActivity() {

        return this;

    }

    // Will be used to return current users Document ID
    public void grabDocumentReference() {

        studentDocumentId = infoRetrieve.getDocumentID();
        studentRef = db.collection("Students").document(studentDocumentId);

    }

    // Stores the planned session into database
    private void storeSession() {

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

        // If user has not selected a course, display error, else proceed with storing
        if (courseDisplayPlan.getSelectedItem() == null) {

            Toast.makeText(getApplicationContext(), "Please select a course.", Toast.LENGTH_SHORT).show();

        } else {

            // Grab the course that the user wants to plan the study session for
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

            // Create LocalDateTime Objects
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime startDate = LocalDateTime.of(year, month, day, timeStartHour, timeStartMinute);
            LocalDateTime endDate = LocalDateTime.of(year, month, day, timeEndHour, timeEndMinute);

            // Get users current time zone id
            ZoneId zoneId = ZoneId.systemDefault();

            // Turn LocalDateTime Objects into seconds
            long startSecond = startDate.atZone(zoneId).toEpochSecond();
            long endSecond = endDate.atZone(zoneId).toEpochSecond();

            // Create Firebase timestamp with seconds
            Timestamp start = new Timestamp(startSecond, 0);
            Timestamp end = new Timestamp(endSecond, 0);

            // If user input study date has already passed or if month does not contain that date, give an error message
            if (startDate.isBefore(currentDate) == true || day > monthLength) {

                Toast.makeText(getApplicationContext(), "Please enter a valid date.", Toast.LENGTH_SHORT).show();

            } else {

                int placeholder = 0;

                // Store the planned study session into the database
                Map<String, Object> sessions = new HashMap<>();
                sessions.put("Course_Name", courseInput);
                sessions.put("Course_SA_ID", placeholder);
                sessions.put("Student_SA_ID", studentRef);
                sessions.put("Start", start);
                sessions.put("End", end);

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

    // Grabs users current courses to populate Spinner
    private void grabUserCourses(Callback callback) {

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
                                callback.call();
                            }
                        } else {
                            Log.v("StudySessionPlan", "Error occurred when getting data from Firebase.");
                        }
                    }
                });

    }

    // Callback function
    public interface Callback {
        void call();
    }

}
