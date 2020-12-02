package com.group04.studentaide;

/*

Written By: Yufeng Luo

*/

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QuizCreate extends AppCompatActivity {

    private static final String QUIZ_DB = "QuizDefs";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<QuizQuestions> quizList;

    EditText mQuestionText;
    EditText mCourseName;
    EditText mOption1;
    EditText mOption2;
    EditText mOption3;
    EditText mAnswerText;
    Button mAddButton;

    Button mSubmitButton;

    private int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_quiz_creation); //Needs to be set

        quizList = new ArrayList<QuizQuestions>();

        //Onclicklisteners
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuestion();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addQuizDefinition(quizList);
            }
        });
    }

    /*
    Method that takes input from respective text fields and creates a new quiz question object
    Object is then added to ArrayList holding QuizQuestion objects, this ArrayList will then be stored into Firestore
    After question is added to the list, EditText fields will be cleared.
    */
    public void addQuestion(){

        String inputQuestion = mQuestionText.getText().toString().trim();
        String inputOption1 = mOption1.getText().toString().trim();
        String inputOption2 = mOption2.getText().toString().trim();
        String inputOption3 = mOption3.getText().toString().trim();
        //Add android:inputType=numberSigned to xml file
        String inputAnswer = mAnswerText.getText().toString().trim();

        int value = Integer.parseInt(inputAnswer);

        if (TextUtils.isEmpty(inputQuestion)){
            mQuestionText.setError("Please enter a question.");
            mQuestionText.requestFocus();
        }

        if (TextUtils.isEmpty(inputOption1)){
            mOption1.setError("Please enter an option.");
            mOption1.requestFocus();
        }

        if (TextUtils.isEmpty(inputOption2)){
            mOption2.setError("Please enter an option.");
            mOption2.requestFocus();
        }

        if (TextUtils.isEmpty(inputOption3)){
            mOption3.setError("Please enter an option.");
            mOption3.requestFocus();
        }

        if (TextUtils.isEmpty(inputAnswer)){
            mAnswerText.setError("Please enter an answer.");
            mAnswerText.requestFocus();
        }

        QuizQuestions question = new QuizQuestions(inputQuestion, inputOption1, inputOption2, inputOption3, value);

        quizList.add(question);

        count++;

        mQuestionText.setText("");
        mOption1.setText("");
        mOption2.setText("");
        mOption3.setText("");
        mAnswerText.setText("");

        Toast.makeText(getActivity(), "Question " + count + " added.", Toast.LENGTH_SHORT).show();
    }

    public QuizCreate getActivity(){
        return this;
    }

    //Need to pass in course name as intent and search
    public void addQuizDefinition(ArrayList<QuizQuestions> quizList){

        String name = mCourseName.getText().toString().trim();

        Map<String, Object> dataMap = new HashMap<>();

        //Fill these in
        /*
        dataMap.put("Course_SA_ID",);
        dataMap.put("Educator_SA_ID", );
        dataMap.put("Name", name);
        dataMap.put("ReleaseDate",);
        dataMap.put("Sequence",);

        dataMap.put("Quiz", quizList);

         */

        db.collection(QUIZ_DB)
                .add(dataMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                    }
                });


    }

}