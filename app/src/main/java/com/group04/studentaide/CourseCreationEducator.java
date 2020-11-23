package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
Use information Retrieval
Educator created account -> inside of Educator Field will need UID field
*/

public class CourseCreationEducator extends AppCompatActivity {

    EditText mInputCourseName;
    EditText mInputInstitutionName;
    Switch mQuizzes;
    Button mCreateCourse;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InformationRetrievalEducator infoRetrieve = InformationRetrievalEducator.getInstance();

    DocumentReference educatorRef;
    String educatorDocumentID;
    String institutionID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_creation_educator);

        grabDocumentReference();

        mInputCourseName = findViewById(R.id.inputCourseName);
        mInputInstitutionName = findViewById(R.id.institutionInput);
        mQuizzes = findViewById(R.id.allowQuiz);
        mCreateCourse = findViewById(R.id.createButton);

        mCreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourseEducator();
            }
        });


    }


    public void grabDocumentReference() {

        educatorDocumentID = infoRetrieve.getEducatorDocumentID();
        educatorRef = db.collection("Educators").document(educatorDocumentID);
        institutionID = infoRetrieve.getInstitutionID();

    }

    /*
    Educator Fields
    -
    Create new doucment in Courses collection with:
     Course fields
    -Course_Name -> Taken from user
    -Educator_SA_ID -> query for UID and get documentID
    -Institution_ID -> Query for UID and get institutionID
     */

    public void createCourseEducator(){

        String quiz;
        String courseName = mInputCourseName.getText().toString().trim();


        //Make institution choice a spinner because the options will be statically set
        String institution = mInputInstitutionName.getText().toString().trim();

        if (TextUtils.isEmpty(courseName)){
            mInputCourseName.setError("Please enter a course name");
            mInputCourseName.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }

        if (TextUtils.isEmpty(institution)){
            mInputInstitutionName.setError("Please enter an institution name");
            mInputCourseName.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }

        if (mQuizzes.isChecked()) {
            quiz = "true";
        }else {
            quiz = "false";
        }

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("Course_Name", courseName);
        inputMap.put("Educator_SA_ID", educatorDocumentID);
        inputMap.put("Institution_SA_ID", institutionID);

        db.collection("Educators")
                .add(inputMap)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(getActivity(), courseName + " created.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(),CoursesActivity.class);
                        startActivity(intent);
                    }
                });

    }

    public CourseCreationEducator getActivity(){
        return this;
    }

}
