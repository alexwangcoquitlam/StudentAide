/*
    Written by Alexander Wang

    User chooses to create an educator account, the app will ask for more information than a regular account.
    The data is stored in a separate collection labeled Educators
    For this type of account, the user is asked for more information in order for us to verify their identity
    Institution and faculty will be set by the developers and shown to the user in spinners.
 */

package com.group04.studentaide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistrationEducator extends AppCompatActivity {

    private EditText mFirstNameInput, mLastNameInput, mEmailInput, mPhoneNumberInput, mPasswordInput, mPasswordCheck;
    private Spinner mInstitution, mFaculty;
    private Button mRegister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String uid = null;
    private DocumentReference institutionID;
    private int institutionPosition, facultyPosition;
    private String email, password, passwordCheck, institutionName, facultyName, firstName, lastName, phoneNumber, name;

    //ArrayLists to hold the appropriate names and populate spinners
    private ArrayList<String> institutionList = new ArrayList<String>();
    private ArrayList<String> facultyList = new ArrayList<String>();

    //Basic Adapters for spinners
    private ArrayAdapter<String> institutionAdapter;
    private ArrayAdapter<String> facultyAdapter;

    //Hashmap structures to insert new data/update data into  Firestore database
    private Map<String, String> institutionsHM = new HashMap<String, String>();
    private Map<String, String> facultyHM = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_educator);

        mFirstNameInput = findViewById(R.id.firstNameInput2);
        mLastNameInput = findViewById(R.id.lastNameInput2);
        mEmailInput = findViewById(R.id.emailInput2);
        mPhoneNumberInput = findViewById(R.id.phoneInputEducator);
        mInstitution = findViewById(R.id.institutionSpinnerRegister);
        mFaculty = findViewById(R.id.facultySpinnerRegister);
        mPasswordInput = findViewById(R.id.inputPassword2);
        mPasswordCheck = findViewById(R.id.passwordConfirmation2);
        mRegister = findViewById(R.id.registerButton2);
        Log.d("buttonWorked", "Got into educator registration.");

        institutionList.add(0, "Choose an Institution");
        facultyList.add(0, "Choose a Faculty");

        facultyAdapter = new ArrayAdapter<String>(RegistrationEducator.this, android.R.layout.simple_spinner_dropdown_item, facultyList);
        mFaculty.setAdapter(facultyAdapter);

        getAllInstitutions(new JoinCourseActivity.Callback() {
            @Override
            public void call() {
                institutionAdapter = new ArrayAdapter<String>(RegistrationEducator.this, android.R.layout.simple_spinner_dropdown_item, institutionList);
                mInstitution.setAdapter(institutionAdapter);
            }
        });

        mInstitution.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String choice = mInstitution.getItemAtPosition(position).toString();

                if (!choice.equals("Choose an Institution")) {
                    institutionPosition = position;
                    getAllFaculties(choice);
                    //facultyAdapter = new ArrayAdapter<String>(RegistrationEducator.this, android.R.layout.simple_spinner_dropdown_item, facultyList);
                    mFaculty.setAdapter(facultyAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position2, long id) {
                String choice = mFaculty.getItemAtPosition(position2).toString();
                if (!choice.equals("Choose a Faculty")){
                    facultyPosition = position2;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationEducator();
            }
        });


    }


    /* Essentially the same function as in Registration, differences are:
     * 1. Checks more info
     * 2. Stores info into a different collection
     */
    private void registrationEducator(){
        email = mEmailInput.getText().toString().trim();
        password = mPasswordInput.getText().toString();
        passwordCheck = mPasswordCheck.getText().toString();
        institutionName = mInstitution.getItemAtPosition(institutionPosition).toString();
        facultyName = mFaculty.getItemAtPosition(facultyPosition).toString();
        firstName = mFirstNameInput.getText().toString().trim();
        lastName = mLastNameInput.getText().toString().trim();
        phoneNumber = mPhoneNumberInput.getText().toString().trim();
        name = firstName + " " + lastName;
        name = name.toLowerCase();
        Log.d("nameCheck", name);
        if (TextUtils.isEmpty(firstName) || firstName == null){
            mFirstNameInput.setError("Please enter a first name");
            mFirstNameInput.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(lastName) || lastName == null){
            mLastNameInput.setError("Please enter a last name");
            mLastNameInput.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(email) || email == null){
            mEmailInput.setError("Please enter your email");
            mEmailInput.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if(email.indexOf('@') == -1){
            mEmailInput.setError("Please enter a valid email");
            mEmailInput.requestFocus();
        }
        if(institutionName.equals("Choose an Institution") || institutionName == null){
            Toast.makeText(RegistrationEducator.this, "Please choose an institution.", Toast.LENGTH_SHORT).show();
            mInstitution.requestFocus();
        }
        if(facultyName.equals("Choose a Faculty") || facultyName == null){
            Toast.makeText(RegistrationEducator.this, "Please choose a faculty.", Toast.LENGTH_SHORT).show();
            mFaculty.requestFocus();
        }
        if(TextUtils.isEmpty(phoneNumber) || phoneNumber == null){
            mPhoneNumberInput.setError("Please enter a phone number");
            mPhoneNumberInput.requestFocus();
        }
        if (TextUtils.isEmpty(password) || password == null){
            mPasswordInput.setError("Please enter a password");
            password = "wrong"; // this is to make sure the values aren't null
            mPasswordInput.requestFocus();// .requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(passwordCheck) || passwordCheck == null){
            mPasswordCheck.setError("Please confirm your password");
            passwordCheck = "right"; // this is to make sure the values aren't null
            mPasswordCheck.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if(!passwordCheck.equals(password)){
            mPasswordCheck.setError("Passwords do not match");
            mPasswordCheck.requestFocus();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("hwa133", "createUserWithEmail:success");

                                user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();
                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("profileUpdated", "User profile updated.");
                                                }
                                            }
                                        });
                                uid = user.getUid();

                                Map<String, Object> Educators = new HashMap<>();
                                Educators.put("User_ID", uid);
                                Educators.put("Given_Names", firstName);
                                Educators.put("Last_Names", lastName);
                                Educators.put("Email", email);
                                Educators.put("Phone_Number", phoneNumber);
                                Educators.put("Institution", institutionName);
                                Educators.put("Faculty", facultyName);
                                Educators.put("Institution_ID", institutionID);

                                db.collection("Educators")
                                        .add(Educators).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("educatorAdded", "Educator added with ID: " + documentReference.getId());
                                        Intent returnLogin = new Intent(RegistrationEducator.this, LoginActivity.class);
                                        Toast.makeText(RegistrationEducator.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        startActivity(returnLogin);
                                    }
                                });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("hwa134", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegistrationEducator.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    public void getAllInstitutions(JoinCourseActivity.Callback callback){
        db.collection("Institutions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean occurs = false;
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String institution = document.getString("Name");
                                for (int i = 0; i < institutionList.size(); i++){
                                    if(institution.equals(institutionList.get(i))){
                                        occurs = true;
                                        break;
                                    }
                                    else{

                                    }
                                }
                                if(occurs){}
                                else {
                                    String institutionID = document.getId();
                                    institutionList.add(institution);

                                    //Insert key value pair to retreive document ID's
                                    institutionsHM.put(institution, institutionID);
                                }

                                //}
                            }
                            callback.call();
                        }else{
                            Log.d("documentRetrieveFail", "Error retrieving documents.");
                        }
                    }
                });
    }

    public void getAllFaculties(String institutionID){
        db.collection("Institutions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String faculty = document.getString("Faculty");
                                String institution = document.getString("Name");
                                if(institution.equals(institutionID)){
                                    facultyList.add(faculty);
                                    facultyHM.put(faculty, institutionID);
                                }
                                else{}
                            }
                        }
                        else{
                            Log.d("facultyAddFail", "Error retrieving documents.");
                        }
                    }
                });
    }

    public void getInstitutionID(){
        db.collection("Institutions")
                .whereEqualTo("Name", mInstitution.getItemAtPosition(institutionPosition))
                .whereEqualTo("Faculty", mFaculty.getItemAtPosition(facultyPosition))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                String institutionIDString = document.getId();
                                institutionID = db.collection("Institutions").document(institutionIDString);
                            }
                        }
                    }
                });

        }
    }

