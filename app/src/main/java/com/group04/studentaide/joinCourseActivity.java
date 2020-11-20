package com.group04.studentaide;

/*

1. User selects Institution
2. Then chooses Educator
3. Then displays all courses that the Educator currently owns

Use 3 dependent spinners -> Institution -> Educator -> Course -> join
Upon clicking join -> (Add student into the Course collection?)

Use ArrayAdapters to hold names

BUGS: Last spinner is not populating, although the ArrayList is being populated

1. Retrieve educators institution ID from Institution_ID -> List will then be all educators from associated institution
2. After choosing educator -> take documentID of matched queries and create a query in Courses
3. Query Courses for Educator_SA_ID hit and retreive all Course_Names associated with educator
4. Stored in hashmaps

Example
1. Choose name to search for in institutions
2. Get name (SFU) - Get documentID (GPxJ3nn6oD4AmvTILN1f)
3. Query for all educators with (GPxJ3nn6oD4AmvTILN1f) in there Institution_SA_ID field
4. And retreive the names associated with the documents to display, retreive documentId of educator selected
5. Query for all courses with educator document ID and retreive names of courses to display in Spinner
6. Upon user selection -> and a field to respective current Users statistics

Data structure to use: Hashmap

InstitutionList: <Name, documentID>
EducatorList: <Name, documentID>
CourseList: <Name, DocumentID>

 */

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.callback.Callback;

public class joinCourseActivity extends AppCompatActivity {

    Spinner mInstitutionSpinner;
    Spinner mEducatorSpinner;
    Spinner mCourseSpinner;
    Button mJoinCourseButton;

    private final static String TAG = "institutionList";

    String courseChosen;
    String studentDocumentID;

    //Collection names
    final String courseDB = "Courses";
    final String educatorDB = "Educators";
    final String institutionDb = "Institutions";
    final String studentsDB = "Students";
    final String statisticsDB = "Statistics";
    final String studentCoursesDB = "StudentCourses";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Current user's UID, used to add them into course
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    ArrayList<String> institutionList = new ArrayList<String>();
    ArrayList<String> educatorList = new ArrayList<String>();
    ArrayList<String> courseList = new ArrayList<String>();

    ArrayAdapter<String> institutionAdapter;
    ArrayAdapter<String> educatorAdapter;
    ArrayAdapter<String> courseAdapter;

    Map<String, String> institutionsHM = new HashMap<String, String>();
    Map<String, String> educatorsHM = new HashMap<String, String>();
    Map<String, String> coursesHM = new HashMap<String, String>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_join);

        institutionList.add(0, "Choose an Institution");
        educatorList.add(0, "Choose an Educator");
        courseList.add(0, "Choose a Course");

        mInstitutionSpinner = (Spinner) findViewById(R.id.institution_spinner);
        mEducatorSpinner = (Spinner) findViewById(R.id.educator_spinner);
        mCourseSpinner = (Spinner) findViewById(R.id.course_spinner);
        mJoinCourseButton = (Button) findViewById(R.id.join_course_button);

        //Might not need this method as a callback as the student UID variable will not be used until later
        getCurrentStudentDocument(UID, new Callback() {
            @Override
            public void call() {

            }
        });

        getAllInstitutions(new Callback() {
            @Override
            public void call() {
                institutionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, institutionList);
                mInstitutionSpinner.setAdapter(institutionAdapter);
            }
        });

        mInstitutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = mInstitutionSpinner.getItemAtPosition(position).toString();

                if (!choice.equals("Choose an Institution")) {

                    getAllEducators(choice, new Callback() {
                        @Override
                        public void call() {

                            educatorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, educatorList);
                            mEducatorSpinner.setAdapter(educatorAdapter);

                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mEducatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = mEducatorSpinner.getItemAtPosition(position).toString();

                if (!choice.equals("Choose an Educator")){

                    getAllCourses(choice, new Callback() {
                        @Override
                        public void call() {

                            courseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courseList);
                            courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            mCourseSpinner.setAdapter(courseAdapter);

                            Log.d(TAG, "Course Spinner set");
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCourseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Get course name selected
                courseChosen = mCourseSpinner.getItemAtPosition(position).toString();


                Log.d(TAG, "First item in courseList: " + courseList.get(0));
                Log.d(TAG, "Made it here.");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Clicked join course button
        //db.collection("")
        mJoinCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    //Create callback methods
    public interface Callback{
        void call();
    }

    public joinCourseActivity getActivity(){
        return this;
    }

    public void getCurrentStudentDocument(String userID,Callback callback){
        db.collection(studentsDB)
                .whereEqualTo("User_ID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                studentDocumentID = document.getId();
                            }
                        }else{
                            Log.d(TAG, "Error retrieving document ID");
                        }
                    }
                });
    }

    public void getAllInstitutions(Callback callback){
        db.collection(institutionDb)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String institution = document.getString("Name");
                                String institutionID = document.getId();
                                Log.d(TAG, "Institution from Firestore: " + institution);
                                if (!institutionList.contains(institution)){
                                    institutionList.add(institution);

                                    //Insert key value pair to retreive document ID's
                                    institutionsHM.put(institution, institutionID);

                                }
                            }
                            callback.call();
                        }else{
                            Log.d(TAG, "Error retrieving documents.");
                        }
                    }
                });
    }

    public void getAllEducators(String institutionName, Callback callback){
        db.collection(educatorDB)
                .whereEqualTo("institution" ,institutionName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String educator = document.getString("Last_Names");
                                String educatorID = document.getId();
                                if (!educatorList.contains(educator)) {
                                    educatorList.add(educator);

                                    educatorsHM.put(educator, educatorID);

                                    Log.d(TAG, educator + " added.");
                                }
                            }
                            callback.call();
                        } else {
                            Log.d(TAG, "Error retrieving documents.");
                        }
                    }
                });
    }

    public void getAllCourses(String educatorName, Callback callback){
        db.collection(courseDB)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String course = document.getString("name");
                                String courseID = document.getId();
                                courseList.add(course);

                                coursesHM.put(course, courseID);

                                Log.d(TAG, course + " added.");
                            }
                            callback.call();
                        }else{
                            Log.d(TAG,"Error retrieving documents.");
                        }
                    }
                });
    }

    //When joining selected course -> need to retreive their student document ID first
    public void joinSelectedCourse(String courseName){

        CollectionReference studentDocRef = db.collection(studentCoursesDB);
        studentDocRef.whereEqualTo("STUDENT_SA_ID", "/Students/" + studentDocumentID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String courseID = coursesHM.get(courseName);
                                Map<String, Object> inputMap = new HashMap<>();
                                inputMap.put("Course_SA_ID", courseID);
                                studentDocRef.document(document.getId()).update(inputMap);
                            }
                        }else{
                            Log.d(TAG, "Error updating courseID field.");
                        }
                    }
                });
    }

}
