package com.group04.studentaide;

/*
    File Name: Timer.java
    Team: ProjectTeam04
    Written By: Jason Leung

    Description:
        This class is a Singleton meant for the timer utilized in StudySession.java. This class consists of getter and setter methods in order to send
        data to StudySession for it to display and store depending on the timer. The timer itself is implemented inside of this class, and StudySession
        only grabs information from this timer. Implementing it this way prevents a new timer from being initialized every time a new StudySession activity
        is launched from the MainActivity.

    Changes:
        December 2nd - Draft 1 of Version 3

    Bugs:
        Have not tested.

 */

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class Timer {

    private static Timer ourInstance = null;
    private static StudySession study = null;

    private CountDownTimer mCountDownTimer;
    private long mStartTimeMilli;
    private long mEndTime;
    private long mTimeLeftMilli;
    private boolean mTimerRunning;

    float timeDistracted;
    float timeStudied;
    String timeLeftFormatted;
    String selectedCourse;

    float accelX;
    float accelY;
    float accelZ;
    boolean distracted;

    ForegroundCheck foreground = ForegroundCheck.getInstance();

    public Timer(StudySession activity) {
        study = activity;
    }

    public void setInstance(StudySession activity) {
        study = activity;
    }

    public static Timer getInstance() {
        return ourInstance;
    }

    public static Timer getInstance(StudySession activity) {
        if (ourInstance == null) {
            ourInstance = new Timer(activity);
        }
        return ourInstance;
    }

    public void setCourse(String course) {

        selectedCourse = course;

    }

    public String getCourse() {

        return selectedCourse;

    }

    public void setTimer(long time) {

        mStartTimeMilli = time;
        resetTimer();

    }

    public void resetTimer(){

        mTimeLeftMilli = mStartTimeMilli;
        updateCountDown();

    }

    public long getStartTime() {

        return mStartTimeMilli;

    }

    public boolean getRunning() {

        return mTimerRunning;

    }

    public void startTimer() {

        Log.v("Hareye", "Start timer");
        mEndTime = System.currentTimeMillis() + mTimeLeftMilli;

        mCountDownTimer = new CountDownTimer(mTimeLeftMilli, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                isDistracted();

                // Time left in millis
                mTimeLeftMilli = millisUntilFinished;

                if (distracted) {

                    timeDistracted++;
                    Log.v("Hareye", "Time Distracted: " + timeDistracted);

                } else if (!distracted) {

                    timeStudied++;
                    Log.v("Hareye", "Time Studied: " + timeStudied);

                }

                updateCountDown();

            }

            @Override
            public void onFinish() {

                study.finishStore();
                study.finishTimer();

            }

        }.start();
        mTimerRunning = true;

    }

    public void pauseTimer() {

        if (mTimerRunning == true) {
            mCountDownTimer.cancel();
        }
        mTimerRunning = false;

    }

    public void updateCountDown() {

        int hours = (int) (mTimeLeftMilli / 1000) / 3600;
        int minutes = (int) ((mTimeLeftMilli / 1000) % 3600) / 60;
        int seconds = (int) (mTimeLeftMilli / 1000) % 60;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }

        study.textCountdownTimer.setText(timeLeftFormatted);

    }

    public void setAccelX(float x) {

        accelX = x;

    }

    public void setAccelY(float y) {

        accelY = y;

    }

    public void setAccelZ(float z) {

        accelZ = z;

    }

    public void isDistracted() {

        if (accelX > 0.1 || accelX < -0.1 || accelY > 0.1 || accelY < -0.1 || accelZ > 0.1 || accelZ < -0.1 || foreground.paused == true) {
            if (distracted == false) {
                Log.v("Hareye", "Is Distracted");
                distracted = true;
            }
        } else {
            if (distracted == true) {
                Log.v("Hareye", "Not Distracted");
                distracted = false;
            }
        }

    }

}
