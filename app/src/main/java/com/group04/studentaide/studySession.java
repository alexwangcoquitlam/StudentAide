package com.group04.studentaide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;
import java.util.TreeMap;

import javax.security.auth.callback.Callback;

/*
Written by: Yufeng Luo, Jason Leung

-- Option to choose course if student wants to study for certain course, otherwise adds to general study time
Assume user inputs time in whole minutes because who sets a timer to study for 55 mins and 25 seconds
1. User input time in milliseconds
2. Update textview to display entered time in minutes
3. Student clicks start timer
4. When timer finishes -- update studyStatistics time // want to implement ability to choose certain course, may need a course class to store information first?
5. Send request and update data in server

//TODO: Study statistics added to total or certain course

 */

public class studySession extends AppCompatActivity {

    Spinner selectSession;
    Spinner courses;
    Button planSession;

    EditText userInputTime;
    Button setTime;
    Button startTime;
    Button pauseTime;
    Button resetTime;
    TextView textCountdownTimer;
    CourseSingleton courseList;

    private CountDownTimer mCountDownTimer;
    private Boolean mTimerRunning;
    private long mStartTimeMilli;
    private long mTimeLeftMilli;
    private long mEndTimeMilli;
    private long mEndTime;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String uid = null;
    ArrayList<String> sessions = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_session);

        if (user != null) {
            uid = user.getUid();
        } else {
            uid = "No associated user";
        }

        courseList = CourseSingleton.getInstance();

        courses = (Spinner) findViewById(R.id.courses);
        ArrayList<String> hashKeys = courseList.courseKeys;
        ArrayAdapter<String> coursesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hashKeys);
        courses.setAdapter(coursesAdapter);

        selectSession = (Spinner) findViewById(R.id.selectSession);
        grabStudySession(new Callback() {
            @Override
            public void call() {
                Log.v("StudySession", "Callback");
                ArrayAdapter<String> sessionsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, sessions);
                selectSession.setAdapter(sessionsAdapter);
            }
        });

        planSession = (Button) findViewById(R.id.planSession);

        userInputTime = (EditText) findViewById(R.id.timeInput);
        pauseTime = (Button) findViewById(R.id.pauseTime);
        setTime = (Button) findViewById(R.id.setTime);
        textCountdownTimer = (TextView) findViewById(R.id.timeLeft);
        startTime = (Button) findViewById(R.id.startTime);
        //resetTime = (Button) findViewById(R.id.resetTimer);

        pauseTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimer();
            }
        });

        planSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Not sure if it should be "getApplicationContext()", or "this"
                Intent intent = new Intent(getApplicationContext(), studySessionPlan.class);
                startActivity(intent);
            }
        });

        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String timeInput = userInputTime.getText().toString().trim();
                double duration = Double.parseDouble(timeInput);

                // Log.v("StudySession", String.valueOf(duration));

                long millisLeftToTime = Long.parseLong(timeInput) * 60000;

                if (courses.getSelectedItem() == null || millisLeftToTime == 0) {

                    if (millisLeftToTime == 0) {
                        Toast.makeText(getApplicationContext(), "Please enter a time.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select a course.", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    String courseInput = courses.getSelectedItem().toString();
                    courseList.setStudyTime(courseInput, duration);
                    setTimer(millisLeftToTime);
                    userInputTime.setText("");

                }

            }
        });

        /*resetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });*/

    }

    private studySession getActivity() {

        return this;

    }

    //Timer Functions
    private void startTimer(){
        mEndTime = System.currentTimeMillis() + mTimeLeftMilli;

        mCountDownTimer = new CountDownTimer(mTimeLeftMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftMilli = millisUntilFinished;
                updateCountDown();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                //Create an object
                //totalTimeStudying = totalTimeStudying + (startTimeMilli /60000) in minutes
            }

        }.start();
        mTimerRunning = true;
    }

    private void pauseTimer(){
        mCountDownTimer.cancel();
        mTimerRunning  = false;
    }

    private void resetTimer(){
        mTimeLeftMilli = mStartTimeMilli;
        updateCountDown();    }

    private void setTimer(long milliseconds){
        mStartTimeMilli = milliseconds;
        resetTimer();
        hideKeyboard();
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        InputMethodManager input = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        input.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Updates timer to reflect the time inputed by user in minutes
    private void updateCountDown(){

        int hours = (int) (mTimeLeftMilli / 1000) / 3600;
        int minutes = (int) ((mTimeLeftMilli / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftMilli / 1000) % 60;
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        textCountdownTimer.setText(timeLeftFormatted);
    }

    public void grabStudySession(Callback callback) {

        db.collection("PlannedSessions")
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // Create new TreeMap that will sort the months in order before storing into sessions
                            TreeMap<Integer, ArrayList<Integer>> sortMonth = new TreeMap<>();

                            // Store key and value pairs into TreeMap which will then be sorted by month (ascending order)
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                long monthInt = (long) document.get("month");
                                long dayDB = (long) document.get("day");
                                long yearDB = (long) document.get("year");

                                int month = (int) monthInt;
                                int day = (int) dayDB;
                                int year = (int) yearDB;

                                ArrayList<Integer> temp = new ArrayList<Integer>();
                                temp.add(day);
                                temp.add(year);

                                sortMonth.put(month, temp);
                            }

                            // Start storing the month, day, and year into sessions to be displayed in Spinner
                            for (TreeMap.Entry<Integer, ArrayList<Integer>> entry : sortMonth.entrySet()) {
                                int monthInt = entry.getKey();

                                ArrayList<Integer> temp = entry.getValue();
                                int dayDB = temp.get(0);
                                int yearDB = temp.get(1);

                                String month = "";

                                switch(monthInt) {
                                    case 1:
                                        month = "January";
                                        break;
                                    case 2:
                                        month = "February";
                                        break;
                                    case 3:
                                        month = "March";
                                        break;
                                    case 4:
                                        month = "April";
                                        break;
                                    case 5:
                                        month = "May";
                                        break;
                                    case 6:
                                        month = "June";
                                        break;
                                    case 7:
                                        month = "July";
                                        break;
                                    case 8:
                                        month = "August";
                                        break;
                                    case 9:
                                        month = "September";
                                        break;
                                    case 10:
                                        month = "October";
                                        break;
                                    case 11:
                                        month = "November";
                                        break;
                                    case 12:
                                        month = "December";
                                        break;
                                }

                                String day = Integer.toString(dayDB);
                                String year = Integer.toString(yearDB);
                                sessions.add(month + " " + day + ", " + year);
                                callback.call();
                            }

                        } else {
                            Log.v("StudySession", "Error occurred when getting data from database.");
                        }
                    }
                });

    }

    public interface Callback {
        void call();
    }

}
