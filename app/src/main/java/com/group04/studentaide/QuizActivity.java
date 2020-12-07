package com.group04.studentaide;

/*
Written By: Yufeng Luo

Quiz Activity that allows students to attempt quizzes that are created by an educator
The quizzes are associated with the Course_SA_ID therefore all students that are enrolled
in the class will have access.

** NOT IMPLEMENTED **
- Correct answers information sent to database for educator to see
- Timer to determine how long a student spends on each question
- Release Date which would only allow students to start quiz when it is the appropriate date

*/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;


public class QuizActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView mQuestionText,mQuestionNumber, mQuizLabel;
    RadioGroup mAllChoices;
    RadioButton mSelectedChoice, mChoice1, mChoice2, mChoice3;
    Button mNextButton;

    private final String QUIZDB = "QuizDefs";
    private final static String TAG = "QuizActivity";

    private ArrayList<QuizQuestions> quizList;
    private int currentQuestionIndex;
    private int correctAnswers;
    private String quizDocumentID;

    private double start;
    private double finish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if(getIntent().getExtras() != null){
            quizDocumentID = getIntent().getStringExtra("quizID");
        }

        mQuestionText = findViewById(R.id.questionLabel);
        mQuestionNumber = findViewById(R.id.questionNumberLabel);
        mQuizLabel = findViewById(R.id.quizLabel);
        mAllChoices = findViewById(R.id.radioGroup);
        mChoice1 = findViewById(R.id.questionAnswer1);
        mChoice2 = findViewById(R.id.questionAnswer2);
        mChoice3 = findViewById(R.id.questionAnswer3);
        mNextButton = findViewById(R.id.nextButton);

        currentQuestionIndex = 0;
        correctAnswers = 0;

        //Retrieve questions from firestore database for the corresponding quiz chosen
        getAllQuestions(quizDocumentID, new QuizCallback() {
            @Override
            public void onQuizCallback(ArrayList<QuizQuestions> quizQuestions) {
                //Then use quizList outside
                quizList = quizQuestions;
                Log.d("Yu", "ArrayList is not populated?");
                setQuizQuestion(currentQuestionIndex);
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answerIsCorrect()){

                    correctAnswers++;
                    nextQuestion();
                    Log.d("Yu", "Correct answer: " + correctAnswers);

                }else{
                    //Incorrect answer
                    nextQuestion();
                    Log.d("Yu", "Correct answer: " + correctAnswers);
                }
            }
            private boolean answerIsCorrect(){
                int answer = -1;
                int buttonID = mAllChoices.getCheckedRadioButtonId();
                mSelectedChoice = findViewById(buttonID);

                if(mSelectedChoice == mChoice1){
                    answer = 1;
                }
                if (mSelectedChoice == mChoice2){
                    answer = 2;
                }
                if (mSelectedChoice == mChoice3){
                    answer = 3;
                }
                return quizList.get(currentQuestionIndex).isCorrect(answer);
            }
        });

    }

    /*
    Method to build questions that are then set inside of radio button choices
    */
    public void setQuizQuestion(int index){
        mQuestionText.setText(quizList.get(currentQuestionIndex).getQuestion());
        mChoice1.setText(quizList.get(currentQuestionIndex).getChoice1());
        mChoice2.setText(quizList.get(currentQuestionIndex).getChoice2());
        mChoice3.setText(quizList.get(currentQuestionIndex).getChoice3());
        String questionNumber =  String.valueOf(currentQuestionIndex + 1);
        mQuestionNumber.setText("Question " + (currentQuestionIndex+1));

        clearAllCheck();

        start = System.nanoTime();
    }

    /*
    Advances to next question in arraylist
    Ensures that User cannot go past the total amount of questions, prevents user from advancing past last index
    */
    public void nextQuestion(){
        finish = System.nanoTime();
        double timeElapsed = (finish - start) / 1000000000;
        quizList.get(currentQuestionIndex).setTimeElapsed(timeElapsed);

        if (currentQuestionIndex < quizList.size() - 1){
            currentQuestionIndex++;
            setQuizQuestion(currentQuestionIndex);
            clearAllCheck();
        }else{
            Toast.makeText(this, "Quiz Finished.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), CoursesActivity.class);
            startActivity(intent);
        }
    }


    public interface QuizCallback{
        void onQuizCallback(ArrayList<QuizQuestions> quizQuestions);
    }

    //Clears checked radio button when advancing to next question
    public void clearAllCheck(){
        mChoice1.setChecked(false);
        mChoice2.setChecked(false);
        mChoice3.setChecked(false);

    }

    /*
    Retrieves the quiz associated with quiz documentID that was selected from previous activity
    */
    public void getAllQuestions(String documentID, QuizCallback callback){
        Log.d("Yu", "Getting all questions");
        DocumentReference quizDocRef = db.collection(QUIZDB).document(documentID);

        quizDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //This will only look at one document

                            DocumentSnapshot document = task.getResult();
                            ArrayList<QuizQuestions> quizQuestions = null;
                            if (document.exists()){
                                Log.d("Yu" ,"Building quiz questions object");
                                quizQuestions = document.toObject(QuizDocument.class).quiz;

                                QuizDocument quizDocument = document.toObject(QuizDocument.class);
                                String quizName = quizDocument.getQuiz_Name();

                                mQuizLabel.setText(quizName);

                                Log.d("Yu", "Quiz name: " + quizName);
                                Log.d("Yu", quizQuestions.toString());

                            }
                            callback.onQuizCallback(quizQuestions);
                        }else{
                            Log.d(TAG, "Error retrieving Quiz questions");
                        }
                    }
                });


    }
}