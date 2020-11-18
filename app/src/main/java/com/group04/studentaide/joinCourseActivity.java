package com.group04.studentaide;

/*

1. User selects Institution
2. Then chooses Educator
3. Then displays all courses that the Educator currently owns

Use 3 dependent spinners -> Institution -> Educator -> Course -> join
Upon clicking join -> (Add student into the Course collection?)

Use ArrayAdapters to hold names

 */

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class joinCourseActivity extends AppCompatActivity {

    Spinner mInstitutionSpinner;
    Spinner mEducatorSpinner;
    Spinner mCourseSpinner;

    private final static String TAG = "institutionList";

    String educatorNames;

    //Collection names
    final String courseDB = "Courses";
    final String educatorDB = "Educators";
    final String institutionDb = "Institutions";

    FirebaseFirestore db;
    ArrayList<String> institutionList = new ArrayList<String>();
    ArrayList<String> educatorList = new ArrayList<String>();
    ArrayList<String> courseList = new ArrayList<String>();

    ArrayAdapter<String> institutionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_join);

        //getAllInstitutions();

        db = FirebaseFirestore.getInstance();
        //CollectionReference institutionsRef = db.collection(institutionDb);

        getAllInstitutions();

        mInstitutionSpinner = (Spinner)findViewById(R.id.institution_spinner);
        mEducatorSpinner = (Spinner)findViewById(R.id.educator_spinner);
        mCourseSpinner = (Spinner)findViewById(R.id.course_spinner);

        institutionAdapter= new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, institutionList);
        institutionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mInstitutionSpinner.setAdapter(institutionAdapter);

        /*
        institutionsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String institution = document.getString("Name");
                                institutionList.add(institution);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }else{
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

         */
        mInstitutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Get institution to display educators associated with chosen institution
                if(mInstitutionSpinner.getSelectedItem() == null){
                    Log.d(TAG, "ERROR ON SELECTION.");
                }

                educatorNames = mInstitutionSpinner.getSelectedItem().toString();
                Log.d(TAG, educatorNames + " YOLO");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //getInstitutionEducators(educatorNames); //Call method to populate educator list

        ArrayAdapter<String> educatorAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, educatorList);
        educatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEducatorSpinner.setAdapter(educatorAdapter);

        /*---------------------------------------------------------------------------*/
        mEducatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String educatorName = mEducatorSpinner.getSelectedItem().toString().trim();
                getEducatorCourses(educatorName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set spinner for course list corresponding to chosen educator
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, courseList);
        mCourseSpinner.setAdapter(courseAdapter);


    }

    /*

    private joinCourseActivity getActivity(){
        return this;
    }

    public interface Callback{
        void call();
    }

    //Grab list of all institutions
    getAllInstitutions(new Callback() {
        @Override
        public void call() {
            ArrayAdapter<String> institutionAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, institutionList);
            mInstitutionSpinner.setAdapter(institutionAdapter);
        }
    });


     */


    //Take in data from Firestore and populate appropriate lists -> lists will then populate spinner
    public void getAllInstitutions(){

        //Looking through all documents in collection "Institutions"
        db.collection(institutionDb)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            //For all documents available;, place into ArrayList
                            for (QueryDocumentSnapshot document : task.getResult()){
                                //If there are multiple institutions with the same name -> only want to display a single instance
                                String institution = document.getString("Name");
                                institutionList.add(institution);
                                Log.d(TAG, institution + " added successfully.");
                                //callback.call();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Error retrieving Institutions.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }); //Can add onFailure listener here
    }

    //Take in data from Firestore for all educators that are in the institutions associated in the institutionList
    //InstitutionNme will be used in query against the database to narrow down options
    //This function to be called on item selected from institution spinner to ensure proper educator list is populated into secondary spinner

    //Update this parameter to take in INSTITUTION_ID string to compare against

    //When user chooses a educator, get there EDUCATOR_SA_ID
    public void getInstitutionEducators(String institutionID){
        db.collection(educatorDB)
                //Check that the course is associated to the institution
                .whereEqualTo("Institution_ID", institutionID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()){
                                String educator = document.getString("Last_Names");
                                if (!educatorList.contains(educator)){
                                    educatorList.add(educator);
                                }
                                Log.d(TAG, educator + " added.");
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Error retreiving Educator and/or courses.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }); //Can add onFailure listener
    }

    public void getEducatorCourses(String educatorID){

        db.collection(courseDB)
                .whereEqualTo("Educator_SA_ID", educatorID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot document : task.getResult()){
                                String course = document.getString("Course_Name");
                                courseList.add(course);
                            }
                        }
                    }
                }); //Can add OnFailure here

    }



}
