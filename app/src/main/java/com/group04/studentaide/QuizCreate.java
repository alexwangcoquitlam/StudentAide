package com.group04.studentaide;

/*
Written By: Yufeng Luo
*/

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    private Button quizQuestionsCreateOpen;
    private Spinner dueDay, dueMonth, dueYear, dueHour, dueMinute, courseName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_creation);

        quizQuestionsCreateOpen = findViewById(R.id.quizQuestionsActivityOpen);

        quizQuestionsCreateOpen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent quizQuestionsCreate = new Intent(QuizCreate.this, QuizQuestionsCreate.class);
                startActivity(quizQuestionsCreate);
            }
        });

    }

}