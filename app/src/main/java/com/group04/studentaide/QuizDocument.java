package com.group04.studentaide;

/*
Written By: Yufeng Luo
Helper class for grabbing object from Firestore and turning from document to class object
*/

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class QuizDocument implements Parcelable {


    private Timestamp quizDate;
    private String quizName;
    private String educatorDocRef;
    private String courseDocRef;

    public ArrayList<QuizQuestions> Quiz;

    public QuizDocument(){
    }

    public QuizDocument(String courseDocRef, String educatorDocRef, String quizName, ArrayList<QuizQuestions> quizQuestionsArrayList, Timestamp releaseDate){
        this.courseDocRef = courseDocRef;
        this.educatorDocRef = educatorDocRef;
        this.quizName = quizName;
        this.Quiz = quizQuestionsArrayList;
        this.quizDate = releaseDate;
    }

    protected QuizDocument(Parcel in) {
        quizDate = in.readParcelable(Timestamp.class.getClassLoader());
        quizName = in.readString();
        educatorDocRef = in.readString();
        courseDocRef = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuizDocument> CREATOR = new Creator<QuizDocument>() {
        @Override
        public QuizDocument createFromParcel(Parcel in) {
            return new QuizDocument(in);
        }

        @Override
        public QuizDocument[] newArray(int size) {
            return new QuizDocument[size];
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

    public ArrayList<QuizQuestions> getQuiz() {
        return Quiz;
    }

    public void setQuiz(ArrayList<QuizQuestions> quiz) {
        Quiz = quiz;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeParcelable(quizDate, flags);
        dest.writeString(quizName);
        dest.writeString(educatorDocRef);
        dest.writeString(courseDocRef);
    }
}