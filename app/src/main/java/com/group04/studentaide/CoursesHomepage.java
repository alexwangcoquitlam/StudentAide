package com.group04.studentaide;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class CoursesHomepage extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    Spinner mCoursesSpinner;

    private Map<String, DocumentReference> coursesHM;
    ArrayAdapter<String> courseAdapter;

    private static final String STUDENT_DB = "Students";
    private static final String STUDENT_COURSES_DB = "StudentCourses";
    private String studentDocumentID;
    private DocumentReference studentDocRef;
    private DocumentReference chosenDocRef;

    InformationRetrieval studentRetrieval = InformationRetrieval.getInstance();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_courses_homepage); //fill in

        //mCoursesSpinner = findViewById(R.id.courseSpinner); //Fill in

        //Get chosen options documentID to be used in new activities
        mCoursesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Set buttons here for user to use
                String courseChosen = mCoursesSpinner.getItemAtPosition(position).toString();
                chosenDocRef = coursesHM.get(courseChosen);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public interface CourseCallback{
        void call(ArrayList<String> courseList);
    }

    public CoursesHomepage getActivity(){
        return this;
    }

    public void grabDocumentReference(){
        studentDocumentID = studentRetrieval.getDocumentID();
        studentDocRef = db.collection(STUDENT_DB).document(studentDocumentID);
    }

    public void getAllUserCourses(){

        db.collection(STUDENT_COURSES_DB)
                .whereEqualTo("Student_SA_ID", studentDocRef)
                .orderBy("CourseName", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<String> courseList = new ArrayList<String>();
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String courseName = document.getString("CourseName");
                                DocumentReference courseDocRef = document.getReference();
                                coursesHM.put(courseName, courseDocRef);

                                courseList.add(courseName);
                            }

                            courseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courseList);
                            mCoursesSpinner.setAdapter(courseAdapter);
                        }else{
                            Log.d("Yu", "Error retrieving Student Courses");
                        }
                    }
                });

    }


}
