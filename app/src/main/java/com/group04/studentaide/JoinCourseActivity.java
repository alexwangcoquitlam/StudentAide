package com.group04.studentaide;

/*
Written By: Yufeng Luo
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
AM I JUST GONNA USE A DAMN GLOBAL VARIABLE AND PASS THE COURSE DOCUMENT ID?????
 */

import android.content.Intent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class JoinCourseActivity extends AppCompatActivity {

    Spinner mInstitutionSpinner;
    Spinner mEducatorSpinner;
    Spinner mCourseSpinner;
    Button mJoinCourseButton;

    private final static String TAG = "institutionList";

    String courseChosen;
    String courseChosenID;
    String studentDocumentID;

    //Collection names
    final String courseDB = "Courses";
    final String educatorDB = "Educators";
    final String institutionDb = "Institutions";
    final String studentsDB = "Students";
    final String statisticsDB = "Statistics";
    final String studentCoursesDB = "StudentCourses";

    //Create an instance of our Firestore database
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //Current user's UID, used to add them into course
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    //ArrayLists to hold the appropriate names and populate spinners
    ArrayList<String> institutionList = new ArrayList<String>();
    //ArrayList<String> educatorList = new ArrayList<String>();
    //ArrayList<String> courseList = new ArrayList<String>();

    //Basic Adapters for spinners
    ArrayAdapter<String> institutionAdapter;
    ArrayAdapter<String> educatorAdapter;
    ArrayAdapter<String> courseAdapter;

    //Hashmap structures to insert new data/update data into  Firestore database
    Map<String, String> institutionsHM = new HashMap<String, String>();
    Map<String, String> educatorsHM = new HashMap<String, String>();
    Map<String, String> coursesHM = new HashMap<String, String>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_join);


        //Sets up Spinners so that the first option is not an item that can be chosen
        institutionList.add(0, "Choose an Institution");
        //educatorList.add(0, "Choose an Educator");
        //courseList.add(0, "Choose a Course");


        //Setup view ID's for respective widgets
        mInstitutionSpinner = findViewById(R.id.institution_spinner);
        mEducatorSpinner = findViewById(R.id.educator_spinner);
        mCourseSpinner = findViewById(R.id.joinCourseSpinner);
        mJoinCourseButton = findViewById(R.id.join_course_button);

        Log.d(TAG, "Current UserID: " + UID);

        //Institution Spinner set
        getAllInstitutions(new Callback() {
            @Override
            public void call() {
                institutionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, institutionList);
                mInstitutionSpinner.setAdapter(institutionAdapter);
            }
        });

        //Educator Spinner set
        mInstitutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = mInstitutionSpinner.getItemAtPosition(position).toString();

                if (!choice.equals("Choose an Institution")) {

                    String institutionChoiceID = institutionsHM.get(choice);

                    Log.d(TAG, "InstitutionID: " + institutionChoiceID);

                    getAllEducators(institutionChoiceID, new educatorCallback() {
                        @Override
                        public void onEducatorCallback(ArrayList<String> educatorList) {

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

        //Course spinner set
        mEducatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = mEducatorSpinner.getItemAtPosition(position).toString();

                if (!choice.equals("Choose an Educator")){

                    String choiceID = educatorsHM.get(choice);
                    Log.d(TAG, "EducatorDocumentID: " + choiceID);

                    //Passed in doucmentID of chosen educator
                    getAllCourses(choiceID, new courseCallback() {
                        @Override
                        public void onCourseCallback(ArrayList<String> courseList) {

                            courseAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courseList);
                            //courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        //Get the course selected from course spinner
        mCourseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Get course name selected
                courseChosen = mCourseSpinner.getItemAtPosition(position).toString();

                courseChosenID = coursesHM.get(courseChosen);

                Log.d(TAG, "Made it here.");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*
        Join Course Button clicked
        New course will be added into StudentCourses with Course name, Course_SA_ID, Student_SA_ID
         */
        mJoinCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (courseChosen.equals("Choose a Course")) {
                    Toast.makeText(getActivity(),"Please choose a course.", Toast.LENGTH_SHORT).show();
                } else {

                    getCurrentStudentDocument(UID, new StudentDocumentCallback() {
                        @Override
                        public void onDocumentCallback(String StudentDocumentID) {
                            Log.d(TAG, "student document ID: " + StudentDocumentID);
                            //Pass in courseChosenID here
                            joinSelectedCourse(courseChosen, courseChosenID, StudentDocumentID);
                            Log.d(TAG, "Join course called, course: " + courseChosen + " added");

                            //Log.d(TAG, "Starting new activity");
                            Intent intent = new Intent(getApplicationContext(), CoursesActivity.class);
                            startActivity(intent);

                        }
                    });
                }
            }
        });

    }

    //Create callback methods
    //Callback methods used because Firestore API is asynchronous
    //Need to wait for method to grab data before moving on in application
    public interface Callback{
        void call();
    }

    public interface StudentDocumentCallback{
        void onDocumentCallback(String StudentDocumentID);
    }

    public interface educatorCallback{
        void onEducatorCallback(ArrayList<String> educatorList);
    }

    public interface courseCallback{
        void onCourseCallback(ArrayList<String> courseList);
    }

    public JoinCourseActivity getActivity(){
        return this;
    }


    //Queries against the current documents inside of "Students" collection
    //Finds the document where the UID matches
    //And gets the students unique Document ID to be added to the course
    public void getCurrentStudentDocument(String userID, StudentDocumentCallback callback){

        db.collection(studentsDB)
                .whereEqualTo("User_ID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                studentDocumentID = document.getId();
                                Log.d(TAG, "StudentdocumentID inside of method: " + studentDocumentID);
                            }
                            callback.onDocumentCallback(studentDocumentID);
                        }else{
                            Log.d(TAG, "Error retrieving document ID");
                        }
                    }
                });
    }

    //Queries against the institution name chosen
    //When chosen, the name of the institution is put into an arrayList to be populated in spinner
    //A hashmap will be populated with the respective institution name and document ID
    //Then the document ID and be easily retreived

    /*
    Queries the institution database upon opening app to prompt user with list of institutions
    Hashmap is used to associate the name chosen with its documentID quickly
     */
    public void getAllInstitutions(Callback callback){
        db.collection(institutionDb)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String institution = document.getString("Name") + " " + document.getString("Faculty");
                                String institutionID = document.getId();
                                //Log.d(TAG, "Institution from Firestore: " + institution);
                                //if (!institutionList.contains(institution)){
                                institutionList.add(institution);

                                //Insert key value pair to retreive document ID's
                                institutionsHM.put(institution, institutionID);

                                //}
                            }
                            callback.call();
                        }else{
                            Log.d(TAG, "Error retrieving documents.");
                        }
                    }
                });
    }


    /*
    - Perform a query for the chosen institutionID
    - Will list all educators associated with the institutionID
    - Name of the educator is then stored in a hashmap along with the respective documentID for quick lookup
     */
    public void getAllEducators(String institutionID, educatorCallback callback){

        //String institutionID = institutionsHM.get(institutionName);
        String institutionSearch = "Institutions/" + institutionID;

        Log.d(TAG, "INSIDE OF getAllEducators");

        db.collection(educatorDB)
                .whereEqualTo("Institution_ID", institutionSearch)
                .orderBy("Last_Names", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            ArrayList<String> educatorList = new ArrayList<String>();
                            educatorList.add(0, "Choose an Educator");

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String firstName = document.getString("Given_Names");
                                String lastName = document.getString("Last_Names");
                                String educator = firstName + " " + lastName;
                                String educatorID = document.getId();
                                //if (!educatorList.contains(educator)) {
                                educatorList.add(educator);

                                educatorsHM.put(educator, educatorID);

                                Log.d(TAG, educator + " added.");
                                //}

                            }
                            callback.onEducatorCallback(educatorList);
                        } else {
                            Log.d(TAG, "Error retrieving documents.");
                        }
                    }
                });
    }


    /*
    Queries into Course database and looks for all courses associated with the chosen
    Educators documentID, then all courses are populated into an ArrayList
    Hashmap is used to quickly look up respective documentID's when choosing a string in spinner
     */
    public void getAllCourses(String educatorID, courseCallback callback){
        Log.d(TAG, "Inside of getAllCourses");

        String educatorSearch = "Educators/" + educatorID;

        db.collection(courseDB)
                .whereEqualTo("Educator_SA_ID", educatorSearch)
                .orderBy("Course_Name", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<String> courseList = new ArrayList<>();
                            courseList.add(0, "Choose a Course");
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String course = document.getString("Course_Name");
                                String courseID = document.getId();
                                courseList.add(course);

                                coursesHM.put(course, courseID);

                                Log.d(TAG, course + " added.");
                            }
                            Log.d(TAG, "CourseList callback");
                            callback.onCourseCallback(courseList);
                        }else{
                            Log.d(TAG,"Error retrieving documents.");
                        }
                    }
                });
    }

    /*
    - New document is created inside of StudentCourses with fields
    course name, course_SA_ID, and student_SA_ID filled
     */
    public void joinSelectedCourse(String courseName, String courseChosenSAID, String studentDocumentID){

        CollectionReference studentDocRef = db.collection(studentCoursesDB);

        //Store information that is passed in
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("CourseName", courseName);
        inputMap.put("Course_SA_ID", "Courses/" + courseChosenSAID);
        inputMap.put("STUDENT_SA_ID", "Students/" + studentDocumentID);

        studentDocRef.document().set(inputMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), courseName + " joined.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error joining course", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
