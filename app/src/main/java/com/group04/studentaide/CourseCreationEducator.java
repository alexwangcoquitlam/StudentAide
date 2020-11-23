package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        getEducatorDocument(new Callback() {
            @Override
            public void call() {

            }
        });

        mInputCourseName = findViewById(R.id.inputCourseName2);
        mCreateCourse = findViewById(R.id.createButton2);

        mCreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourseEducator();
            }
        });


    }

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
        inputMap.put("allowQuizzes", quiz);

        db.collection("Courses")
                .add(inputMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), courseName + " created.", Toast.LENGTH_SHORT).show();
                        Log.d("WDF", courseName + " " + educatorDocumentID + " " + institutionID);

                        Intent intent = new Intent(getActivity(),CoursesActivity.class);
                        startActivity(intent);
                    }
                });


    }

    public CourseCreationEducator getActivity(){
        return this;
    }

    public interface Callback{
        void call();
    }

    public void getEducatorDocument(Callback callback){

        if (user != null) {
            String UID = user.getUid();

            db.collection("Educators")
                    .whereEqualTo("User_ID", UID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    educatorDocumentID = document.getId();
                                    institutionID = document.getString("Institution_ID");

                                    Log.d("WDF", "Ed ID: " + educatorDocumentID + " " + " Ins ID: " + institutionID);
                                }
                                callback.call();
                            } else {
                                Log.d("WDF", "Error retrieving educator document ID");
                            }
                        }
                    });
        }
    }

}