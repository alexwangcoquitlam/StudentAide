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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*  File Name: studySessionPlan.java
    Team: ProjectTeam04
    Written By: Jason Leung

    Description:
        This class implements the PLAN STUDY SESSION page inside StudentAide. It is accessed by clicking the PLAN button on the STUDY page. This class will
        display a page that will allow the user to select a course to plan the study session for, the start date/time and end date/time. If end date is before
        the start date, or the planned study session is on a date that has already passed, it will alert the user to enter a valid date. If the user enters a
        valid date, it will then be stored into Cloud Firestore to be accessed.

    Changes:
        November 14th - Draft 1 of Version 1/2
        November 18th - Draft 2 of Version 1/2
        November 20th - Finalized Version 1/2

    Bugs:
        Haven't tested.
 */

public class StudySessionPlan extends AppCompatActivity {

    Spinner courseDisplayPlan;
    Spinner editSession;
    Spinner month;
    Spinner day;
    Spinner year;

    Spinner timeStartHour;
    Spinner timeStartMinute;

    Spinner timeEndHour;
    Spinner timeEndMinute;

    Button planSession;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();

    ArrayList<String> courses = new ArrayList<String>();
    ArrayList<String> sessions = new ArrayList<String>();
    ArrayList<String> documentId = new ArrayList<String>();

    ArrayAdapter<String> courseAdapter;

    DocumentReference studentRef;
    String studentDocumentId;
    String sessionCourse;
    boolean setCourse = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_plan);

        grabDocumentReference();

        month = findViewById(R.id.month);
        day = findViewById(R.id.day);
        year = findViewById(R.id.year);
        courseDisplayPlan = findViewById(R.id.coursesSpinner);
        editSession = findViewById(R.id.editSpinner);
        timeStartHour = findViewById(R.id.timeStartHour);
        timeStartMinute = findViewById(R.id.timeStartMinute);
        timeEndHour = findViewById(R.id.timeEndHour);
        timeEndMinute = findViewById(R.id.timeEndMinute);
        planSession = findViewById(R.id.planSessionButton);

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

        // Populate editSession Spinner with the users study sessions
        grabStudySession(new Callback() {
            @Override
            public void call() {
                ArrayAdapter<String> sessionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sessions);
                editSession.setAdapter(sessionAdapter);
            }
        });

        // Populate courseDisplayPlan Spinner with the users courses
        grabUserCourses(new Callback() {
            @Override
            public void call() {
                courseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
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
                if (editSession.getSelectedItem().equals("No Session Selected")) {
                    storeSession();
                } else {
                    editStudySession(courseDisplayPlan.getSelectedItem().toString());
                }
            }
        });

        courseDisplayPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getItemAtPosition(position).toString();
                if (choice.equals("No Course Selected")) {
                    editSession.setSelection(0);
                    setCourse = false;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = parent.getItemAtPosition(position).toString();

                getSessionCourse(new Callback() {
                    @Override
                    public void call() {
                        int sessionPosition = courseAdapter.getPosition(sessionCourse);
                        courseDisplayPlan.setSelection(sessionPosition);
                        setCourse = true;
                    }
                });

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // Returns current activity
    private StudySessionPlan getActivity() {

        return this;

    }

    // Returns the current users document ID and reference to their document
    public void grabDocumentReference() {

        studentDocumentId = infoRetrieve.getDocumentID();
        studentRef = db.collection("Students").document(studentDocumentId);

    }

    // Stores the planned session into Cloud Firestore
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
        if (courseDisplayPlan.getSelectedItem().equals("No Course Selected")) {

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

            if (day > monthLength) {

                Toast.makeText(getApplicationContext(), "Please enter a valid date.", Toast.LENGTH_SHORT).show();

            } else {

                // Create LocalDateTime Objects
                LocalDateTime currentDate = LocalDateTime.now();
                LocalDateTime startDate = LocalDateTime.of(year, month, day, timeStartHour, timeStartMinute);
                LocalDateTime endDate = LocalDateTime.of(year, month, day, timeEndHour, timeEndMinute);

                // Get users current time zone ID
                ZoneId zoneId = ZoneId.systemDefault();

                // Turn LocalDateTime Objects into seconds
                long startSecond = startDate.atZone(zoneId).toEpochSecond();
                long endSecond = endDate.atZone(zoneId).toEpochSecond();

                // Create Firebase timestamp with seconds
                Timestamp start = new Timestamp(startSecond, 0);
                Timestamp end = new Timestamp(endSecond, 0);

                // If user input study date has already passed or if month does not contain that date, give an error message
                if (startDate.isBefore(currentDate) == true) {

                    Toast.makeText(getApplicationContext(), "Date has already passed.", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(StudySessionPlan.this, "New Session Planned", Toast.LENGTH_SHORT).show();
                                    Intent returnStudy = new Intent(StudySessionPlan.this, StudySession.class);
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

    }

    // Grabs users current courses to populate Spinner
    private void grabUserCourses(Callback callback) {

        courses.clear();
        courses.add("No Course Selected");

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

    // Grabs all planned study sessions that the user has to display in Spinner
    private void grabStudySession(Callback callback) {

        sessions.clear();
        sessions.add("No Session Selected");

        db.collection("PlannedSessions")
                .whereEqualTo("Student_SA_ID", studentRef)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // Store the Date of the start Timestamp into sessions spinner to be displayed
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timestamp start = (Timestamp) document.get("Start");
                                Date date = start.toDate();

                                sessions.add(String.valueOf(date));
                            }
                            callback.call();

                        } else {
                            Log.v("StudySession", "Error occurred when getting data from database.");
                        }
                    }
                });

    }

    // Edits the selected planned study session to the new time
    private void editStudySession(String course) {

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

        if (day > monthLength) {

            Toast.makeText(getApplicationContext(), "Please enter a valid date.", Toast.LENGTH_SHORT).show();

        } else {

            // Create LocalDateTime Objects
            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime startDate = LocalDateTime.of(year, month, day, timeStartHour, timeStartMinute);
            LocalDateTime endDate = LocalDateTime.of(year, month, day, timeEndHour, timeEndMinute);

            // Get users current time zone ID
            ZoneId zoneId = ZoneId.systemDefault();

            // Turn LocalDateTime Objects into seconds
            long startSecond = startDate.atZone(zoneId).toEpochSecond();
            long endSecond = endDate.atZone(zoneId).toEpochSecond();

            // Create Firebase timestamp with seconds
            Timestamp start = new Timestamp(startSecond, 0);
            Timestamp end = new Timestamp(endSecond, 0);

            // If user input study date has already passed or if month does not contain that date, give an error message
            if (startDate.isBefore(currentDate)) {

                Toast.makeText(getApplicationContext(), "Date has already passed.", Toast.LENGTH_SHORT).show();

            } else {

                DocumentReference sessionRef = db.collection("PlannedSessions").document(documentId.get(0));
                sessionRef
                        .update("End", end, "Start", start, "Course_Name", course)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(StudySessionPlan.this, "Updated Session", Toast.LENGTH_SHORT).show();
                                Log.v("StudySession", "Updated Start, End, and Course_Name field");
                                Intent returnStudy = new Intent(StudySessionPlan.this, StudySession.class);
                                startActivity(returnStudy);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.v("StudySession", "Error updating document", e);
                            }
                        });

            }

        }

    }

    // Grab the course from the current selected planned study session
    public void getSessionCourse(Callback callback) {

        String session = editSession.getSelectedItem().toString();
        documentId.clear();

        db.collection("PlannedSessions")
                .whereEqualTo("Student_SA_ID", studentRef)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timestamp start = (Timestamp) document.get("Start");
                                Date date = start.toDate();
                                String dateString = date.toString();

                                String currentCourse = (String) document.get("Course_Name");

                                if (session.equals(dateString)) {

                                    sessionCourse = currentCourse;
                                    documentId.add(document.getId());
                                    callback.call();
                                    break;

                                }

                            }
                        } else {
                            Log.v("CoursesActivity", "Error occurred when getting data from Firebase.");
                        }
                    }
                });

    }

    // Callback function
    public interface Callback {
        void call();
    }

}