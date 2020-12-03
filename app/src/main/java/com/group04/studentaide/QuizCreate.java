package com.group04.studentaide;

/*
Written By: Yufeng Luo
*/

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class QuizCreate extends AppCompatActivity {

    private Button quizQuestionsCreateOpen;
    private EditText quizName;
    private Spinner dueDay, dueMonth, dueYear, dueHour, dueMinute;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_creation);

        dueDay = findViewById(R.id.dueDateDay3);
        dueMonth = findViewById(R.id.dueDateMonth);
        dueYear = findViewById(R.id.dueDateYear);
        dueHour = findViewById(R.id.dueDateHour);
        dueMinute = findViewById(R.id.dueDateMinute);
        quizName = findViewById(R.id.quizName);

        quizQuestionsCreateOpen = findViewById(R.id.quizQuestionsActivityOpen);

        QuizQuestions newQuiz = new QuizQuestions();

        quizQuestionsCreateOpen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent quizQuestionsCreate = new Intent(QuizCreate.this, QuizQuestionsCreate.class);
                quizQuestionsCreate.putExtra("quiz", newQuiz);
                startActivity(quizQuestionsCreate);
            }
        });

    }

}