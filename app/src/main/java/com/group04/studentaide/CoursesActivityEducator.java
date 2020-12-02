package com.group04.studentaide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CoursesActivityEducator extends AppCompatActivity {

    private Button quizCreateOpen, courseCreateOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_educator);

        quizCreateOpen = findViewById(R.id.createQuizOpen);
        courseCreateOpen = findViewById(R.id.courseCreate);

        quizCreateOpen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent quizCreate = new Intent(CoursesActivityEducator.this, QuizCreate.class);
                startActivity(quizCreate);
            }
        });
        courseCreateOpen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent courseCreate = new Intent(CoursesActivityEducator.this, CourseCreationEducator.class);
                startActivity(courseCreate);
            }
        });
    }
}
