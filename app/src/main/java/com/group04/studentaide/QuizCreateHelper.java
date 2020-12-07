package com.group04.studentaide;

/*
Written By: Yufeng Luo

Class that implements Parcelable to pass TimeStamp object
DocumentReference does not implement Parcelable therefore hvae to pass strings, then build
DocumentReference path inside of the next activity

*/

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class QuizCreateHelper implements Parcelable {

    private Timestamp quizDate;
    private String quizName;
    private String educatorDocRef;
    private String courseDocRef;

    public QuizCreateHelper(String courseDocRef, String educatorDocRef, String quizName, Timestamp releaseDate){
        this.courseDocRef = courseDocRef;
        this.educatorDocRef = educatorDocRef;
        this.quizName = quizName;

        this.quizDate = releaseDate;
    }

    protected QuizCreateHelper(Parcel in) {
        quizDate = in.readParcelable(Timestamp.class.getClassLoader());
        quizName = in.readString();
        educatorDocRef = in.readString();
        courseDocRef = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(quizDate, flags);
        dest.writeString(quizName);
        dest.writeString(educatorDocRef);
        dest.writeString(courseDocRef);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuizCreateHelper> CREATOR = new Creator<QuizCreateHelper>() {
        @Override
        public QuizCreateHelper createFromParcel(Parcel in) {
            return new QuizCreateHelper(in);
        }

        @Override
        public QuizCreateHelper[] newArray(int size) {
            return new QuizCreateHelper[size];
        }
    };

    public Timestamp getQuizDate() {
        return quizDate;
    }

    public void setQuizDate(Timestamp quizDate) {
        this.quizDate = quizDate;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public String getEducatorDocRef() {
        return educatorDocRef;
    }

    public void setEducatorDocRef(String educatorDocRef) {
        this.educatorDocRef = educatorDocRef;
    }

    public String getCourseDocRef() {
        return courseDocRef;
    }

    public void setCourseDocRef(String courseDocRef) {
        this.courseDocRef = courseDocRef;
    }
}