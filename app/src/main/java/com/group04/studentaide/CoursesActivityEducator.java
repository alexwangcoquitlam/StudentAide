package com.group04.studentaide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*

*/

public class CoursesActivityEducator extends AppCompatActivity {

    private Button quizCreateOpen, courseCreateOpen;
    Spinner coursesDisplay;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    InformationRetrievalEducator infoRetrieve = InformationRetrievalEducator.getInstance();
    String educatorDocumentID, choice, chosenCourseID;
    boolean courseChosen = false;
    DocumentReference educatorDocRef;

    Map<String, String> coursesHM = new HashMap<>();
    ArrayList<String> courses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_educator);

        quizCreateOpen = findViewById(R.id.createQuizOpen);
        courseCreateOpen = findViewById(R.id.courseCreate);
        coursesDisplay = findViewById(R.id.courseDropdown);

        courses.add(0, "Choose a Course");

        grabDocumentReference();

        /*
        Quiz Creation clicked, ensures that the user has selected a course first
        If no course is selected, a toast will appear prompting the user to do so
        */
        quizCreateOpen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                if (courseChosen){
                    //If course is chosen, the new activity will be created
                    //The chosen Course ID is passed to the new activity
                    Intent quizCreate = new Intent(CoursesActivityEducator.this, QuizCreate.class);
                    quizCreate.putExtra("Course_SA_ID", chosenCourseID);
                    startActivity(quizCreate);
                }else{
                    Toast.makeText(getActivity(), "Please select a course.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //Takes educator to new course creation page
        courseCreateOpen.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent courseCreate = new Intent(CoursesActivityEducator.this, CourseCreationEducator.class);
                startActivity(courseCreate);
            }
        });

        grabCourses(new Callback() {
            @Override
            public void call() {
                ArrayAdapter<String> courseAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
                coursesDisplay.setAdapter(courseAdapter);
                Log.d("Yu", "Courses populated");
                Log.d("Yu", "Course: " + courses.get(1));
            }
        });

        coursesDisplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choice = coursesDisplay.getItemAtPosition(position).toString();

                if(!choice.equals("Choose a Course")){
                    chosenCourseID = coursesHM.get(choice);
                    courseChosen = true;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public interface Callback{
        void call();
    }

    public CoursesActivityEducator getActivity(){
        return this;
    }

    /*
    This method is used to grab the current users document ID which would then be used to query for their associated Courses.
    */
    public void grabDocumentReference() {

        educatorDocumentID = infoRetrieve.getEducatorDocumentID();
        educatorDocRef = db.collection("Educators").document(educatorDocumentID);
        Log.d("Yu", "Educator ref grabbed.");

    }


    /*
      Method uses Firestore API
      Grabs all courses from Database associated with the current logged-in educators unique Educator_SA_ID
    */
    public void grabCourses(Callback callback) {

        Log.d("Yu", "Now grabbing courses");

        db.collection("Courses")
                .whereEqualTo("Educator_SA_ID", educatorDocRef)
                .orderBy("Course_Name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //courses.add(0, "Choose a Course");
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String path;
                                String courseName = document.getString("Course_Name");

                                path = document.getId();
                                Log.d("Yu", "Course_SA_ID: " + path);
                                //The string path for the Document of each course is placed into a hashmap for retrival
                                coursesHM.put(courseName, path);
                                courses.add(courseName);

                            }
                            callback.call();
                        } else {
                            Log.v("CoursesActivity", "Error occurred when getting data from Firebase.");
                        }
                    }
                });

    }
}
