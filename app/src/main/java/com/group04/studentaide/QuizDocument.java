package com.group04.studentaide;

/*
Written By: Yufeng Luo

Getters and Setters are case sensitive to what is in firestore when retrieving

Helper class for grabbing object from Firestore and turning from document to class object
*/

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;

public class QuizDocument{


    private DocumentReference Educator_SA_ID;
    private DocumentReference Course_SA_ID;
    public ArrayList<QuizQuestions> quiz;
    private String Quiz_Name;
    private Timestamp ReleaseDate;

    public QuizDocument(){
    }

    public QuizDocument(DocumentReference courseDocRef, DocumentReference educatorDocRef, String Quiz_Name, ArrayList<QuizQuestions> quizQuestionsArrayList, Timestamp releaseDate){
        this.Course_SA_ID = courseDocRef;
        this.Educator_SA_ID = educatorDocRef;
        this.Quiz_Name = Quiz_Name;
        this.quiz = quizQuestionsArrayList;
        this.ReleaseDate = releaseDate;
    }

    public DocumentReference getEducator_SA_ID() {
        return Educator_SA_ID;
    }

    public void setEducator_SA_ID(DocumentReference Educator_SA_ID) {
        this.Educator_SA_ID = Educator_SA_ID;
    }

    public DocumentReference getCourse_SA_ID() {
        return Course_SA_ID;
    }

    public void setCourse_SA_ID(DocumentReference Course_SA_ID) {
        this.Course_SA_ID = Course_SA_ID;
    }

    public ArrayList<QuizQuestions> getQuiz() {
        return quiz;
    }

    public void setQuiz(ArrayList<QuizQuestions> Quiz) {
        this.quiz = Quiz;
    }

    public String getQuiz_Name() {
        return Quiz_Name;
    }

    public void setQuiz_Name(String Quiz_Name) {
        this.Quiz_Name = Quiz_Name;
    }

    public Timestamp getReleaseDate() {
        return ReleaseDate;
    }

    public void ReleaseDate(Timestamp ReleaseDate) {
        this.ReleaseDate = ReleaseDate;
    }
}