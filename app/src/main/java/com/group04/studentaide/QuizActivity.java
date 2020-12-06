package com.group04.studentaide;

/*
** Will need to pass in whatever quiz they clicked on as an intent extra **
Written By: Yufeng Luo
*/

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;


public class QuizActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView mQuestionText;
    RadioGroup mAllChoices;
    RadioButton mSelectedChoice;
    RadioButton mChoice1;
    RadioButton mChoice2;
    RadioButton mChoice3;
    Button mNextButton;

    private final String QUIZDB = "QuizDef";
    private final static String TAG = "QuizActivity";

    private ArrayList<QuizQuestions> quizList;
    private int currentQuestionIndex;
    private int correctAnswers;

    private double start;
    private double finish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mQuestionText = findViewById(R.id.questionLabel);
        mAllChoices = findViewById(R.id.radioGroup);
        mNextButton = findViewById(R.id.nextButton);

        currentQuestionIndex = 0;
        correctAnswers = 0;

        //put quizDocID
        getAllQuestions("SEE0TEnnRsmhc9QFg7ay", new QuizCallback() {
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

                }else{
                    //Incorrect answer
                    nextQuestion();
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

    public void setQuizQuestion(int index){
        mQuestionText.setText(quizList.get(currentQuestionIndex).getQuestion());
        mChoice1.setText(quizList.get(currentQuestionIndex).getChoice1());
        mChoice2.setText(quizList.get(currentQuestionIndex).getChoice2());
        mChoice3.setText(quizList.get(currentQuestionIndex).getChoice3());
        mAllChoices.clearCheck();

        start = System.nanoTime();


    }

    public void nextQuestion(){

        finish = System.nanoTime();
        double timeElapsed = (finish - start) / 1000000000;
        quizList.get(currentQuestionIndex).setTimeElapsed(timeElapsed);

        if (currentQuestionIndex < quizList.size()){
            currentQuestionIndex++;
            setQuizQuestion(currentQuestionIndex);
        }

        //currentQuestionIndex = (currentQuestionIndex + 1) % quizList.size()
    }

    public void checkAnswer(){

    }

    public interface QuizCallback{
        void onQuizCallback(ArrayList<QuizQuestions> quizQuestions);
    }

    public void getAllQuestions(String documentID, QuizCallback callback){
        Log.d("Yu", "Getting all questions");
        DocumentReference quizDocRef = db.collection(QUIZDB).document(documentID);

        quizDocRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            //This will only look at one document
                            QuizDocument quizDocument = new QuizDocument();
                            ArrayList<QuizQuestions> quizQuestions = new ArrayList<>();
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                //Hopefully this populates arrayList with only the questions
                                quizDocument = document.toObject(QuizDocument.class);
                                quizQuestions = quizDocument.getQuiz();
                                Log.d("Yu" ,"Building quiz questions object");
                                //Call method that builds quiz
                            }
                            callback.onQuizCallback(quizQuestions);
                        }else{
                            Log.d(TAG, "Error retrieving Quiz questions");
                        }
                    }
                });


    }


}