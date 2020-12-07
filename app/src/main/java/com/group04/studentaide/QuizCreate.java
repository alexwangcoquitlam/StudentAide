package com.group04.studentaide;

/*
Written By: Yufeng Luo

Activity is used by Educator to set the time, date, and name of their quiz

*/

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

public class QuizCreate extends AppCompatActivity {

    private Button quizQuestionsCreateOpen;
    private EditText quizName;
    private Spinner dueDay, dueMonth, dueYear, dueHour, dueMinute;

    InformationRetrievalEducator mInformationRetrievalEducator = InformationRetrievalEducator.getInstance();
    QuizCreateHelper partialQuiz;

    boolean quizDateOkay = false;

    private String educatorDocRef;
    private String courseDocRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_creation);

        dueDay = findViewById(R.id.dueDateDay);
        dueMonth = findViewById(R.id.dueDateMonth);
        dueYear = findViewById(R.id.dueDateYear);
        dueHour = findViewById(R.id.dueDateHour);
        dueMinute = findViewById(R.id.dueDateMinute);
        quizName = findViewById(R.id.quizName);
        quizQuestionsCreateOpen = findViewById(R.id.quizQuestionsActivityOpen);

        if(getIntent().getExtras() != null){
            courseDocRef = getIntent().getStringExtra("Course_SA_ID");
        }

        grabEducatorDocuments();

        // Populate month Spinner with the months
        String[] months = new String[]{"Choose a Month","January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);
        dueMonth.setAdapter(monthsAdapter);

        // Populate day Spinner with the days
        String[] days = new String[]{"Choose a day", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, days);
        dueDay.setAdapter(daysAdapter);

        // Populate year Spinner with the years
        String[] years = new String[]{"Choose a year", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030"};
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, years);
        dueYear.setAdapter(yearsAdapter);

        // Define the hours and minutes to be displayed in Spinners
        String[] hours = new String[]{"Choose a hour", "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12",
                "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"};
        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        dueHour.setAdapter(hoursAdapter);

        String[] minutes = new String[]{"Choose a minute", "00", "15", "30", "45"};
        ArrayAdapter<String> minutesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutes);
        dueMinute.setAdapter(minutesAdapter);

        /*
        When the educator fills in all fields available, quizDateOkay will be true and be given the
        go-ahead to start creating quiz questions

        If a field is not filled or the date has already been passed, a toast message will appear
        asking the educator to fix whatever field is wrong
        */
        quizQuestionsCreateOpen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                QuizCreateHelper intentQuiz = createQuiz();

                if(quizDateOkay) {
                    Intent quizQuestionsCreate = new Intent(QuizCreate.this, QuizQuestionsCreate.class);
                    quizQuestionsCreate.putExtra("partialQuiz", intentQuiz);

                    startActivity(quizQuestionsCreate);
                    Log.d("Yu", "Create quiz questions started");
                }else{
                    Toast.makeText(getApplicationContext(), "Please input a valid date.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void grabEducatorDocuments(){

        educatorDocRef = mInformationRetrievalEducator.getEducatorDocumentID();
        Log.d("Yu", educatorDocRef);

    }

    private QuizCreateHelper createQuiz(){
        
        QuizCreateHelper partialQuiz = null;
        String nameInput = quizName.getText().toString();
        String yearInput = null;
        String monthInput = null;
        String dayInput = null;
        String hourInput = null;
        String minuteInput = null;

        int monthInt = 0;
        //Check that user has inputted a quiz name
        if (TextUtils.isEmpty(nameInput)){
            quizName.setError("Please enter a quiz name.");
            quizName.requestFocus();
        }

        //Check validity of spinners
        if (dueYear.getSelectedItem().equals("Choose a year") || dueDay.getSelectedItem().equals("Choose a day") || dueHour.getSelectedItem().equals("Choose a hour")
            || dueMinute.getSelectedItem().equals("Choose a minute")) {
            Toast.makeText(this, "Please fill out all options", Toast.LENGTH_SHORT).show();
        }else{

                monthInput = dueMonth.getSelectedItem().toString();

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

            yearInput = dueYear.getSelectedItem().toString();
            dayInput = dueDay.getSelectedItem().toString();
            hourInput = dueHour.getSelectedItem().toString();
            minuteInput = dueMinute.getSelectedItem().toString();


            //Building Timestamp object
            int day = Integer.parseInt(dayInput);
            int year = Integer.parseInt(yearInput);
            int hour = Integer.parseInt(hourInput);
            int minute = Integer.parseInt(minuteInput);

            Month month = Month.of(monthInt);

            LocalDateTime currentDate = LocalDateTime.now();
            LocalDateTime quizDate = LocalDateTime.of(year, month, day, hour, minute);

            ZoneId zoneId = ZoneId.systemDefault();

            long releaseDate = quizDate.atZone(zoneId).toEpochSecond();
            Timestamp quizReleaseDate = new Timestamp(releaseDate, 0);

            if (quizDate.isBefore(currentDate)){
                Toast.makeText(this, "Date has already passed", Toast.LENGTH_SHORT).show();
            }else{
                //Everything okay, create partial Quiz object to be completed in QuizQuestionsCreate.java
                partialQuiz = new QuizCreateHelper(courseDocRef, educatorDocRef, nameInput, quizReleaseDate);
                quizDateOkay = true;
            }

        }
        return partialQuiz;

    }

}