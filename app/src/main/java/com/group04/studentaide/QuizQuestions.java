package com.group04.studentaide;

/*

Written By: Yufeng Luo

This class object will be used to take in and hold Quiz Questions in format ArrayList<QuizQuestions> quizList

*/

public class QuizQuestions {

    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private int answerNumber;
    private double timeElapsed;

    public QuizQuestions(){
    }

    public QuizQuestions(String question, String choice1, String choice2, String choice3, int answerNumber){
        this.question = question;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.answerNumber = answerNumber;
        this.timeElapsed = 0;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public int getAnswerNumber() {
        return answerNumber;
    }

    public void setAnswerNumber(int answerNumber) {
        this.answerNumber = answerNumber;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public boolean isCorrect(int answer){
        return (answer == answerNumber);
    }
}
