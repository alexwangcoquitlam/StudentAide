package com.group04.studentaide;

/*
Written By: Yufeng Luo
Helper class for grabbing object from Firestore and turning from document to class object
*/

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Document;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;

public class QuizDocument{


    private Timestamp quizDate;
    private String quizName;
    private DocumentReference educatorDocRef;
    private DocumentReference courseDocRef;
    public ArrayList<QuizQuestions> quiz;

    public QuizDocument(){
    }

    public QuizDocument(DocumentReference courseDocRef, DocumentReference educatorDocRef, String quizName, ArrayList<QuizQuestions> quizQuestionsArrayList, Timestamp releaseDate){
        this.courseDocRef = courseDocRef;
        this.educatorDocRef = educatorDocRef;
        this.quizName = quizName;
        this.quiz = quizQuestionsArrayList;
        this.quizDate = releaseDate;
    }


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

    public DocumentReference getEducatorDocRef() {
        return educatorDocRef;
    }

    public void setEducatorDocRef(DocumentReference educatorDocRef) {
        this.educatorDocRef = educatorDocRef;
    }

    public DocumentReference getCourseDocRef() {
        return courseDocRef;
    }

    public void setCourseDocRef(DocumentReference courseDocRef) {
        this.courseDocRef = courseDocRef;
    }

    public ArrayList<QuizQuestions> getQuiz() {
        return quiz;
    }

    public void setQuiz(ArrayList<QuizQuestions> quiz) {
        this.quiz = quiz;
    }
}