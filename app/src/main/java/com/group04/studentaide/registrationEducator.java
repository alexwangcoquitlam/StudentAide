package com.group04.studentaide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registrationEducator extends AppCompatActivity {

    private EditText mFirstNameInput, mLastNameInput, mEmailInput, mInstitutionInput, mFacultyInput, mPhoneNumberInput, mPasswordInput, mPasswordCheck;
    private Button mRegister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String uid = null;
    private String institutionID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_educator);

        mFirstNameInput = findViewById(R.id.firstNameInput2);
        mLastNameInput = findViewById(R.id.lastNameInput2);
        mEmailInput = findViewById(R.id.emailInput2);
        mInstitutionInput = findViewById(R.id.institutionInputRegister);
        mFacultyInput = findViewById(R.id.facultyInput);
        mPhoneNumberInput = findViewById(R.id.phoneInputEducator);
        mPasswordInput = findViewById(R.id.inputPassword2);
        mPasswordCheck = findViewById(R.id.passwordConfirmation2);
        mRegister = findViewById(R.id.registerButton2);
        Log.d("buttonWorked", "Got into educator registration.");

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationEducator();
            }
        });


    }

    private void registrationEducator(){
        String email = mEmailInput.getText().toString().trim();
        String password = mPasswordInput.getText().toString();
        String passwordCheck = mPasswordCheck.getText().toString();
        String firstName = mFirstNameInput.getText().toString().trim();
        String lastName = mLastNameInput.getText().toString().trim();
        String institution = mInstitutionInput.getText().toString().trim();
        String faculty = mFacultyInput.getText().toString().trim();
        String phoneNumber = mPhoneNumberInput.getText().toString().trim();
        String name = firstName + " " + lastName;
        institution.toLowerCase();
        faculty.toLowerCase();
        name = name.toLowerCase();
        Log.d("nameCheck", name);
        if (TextUtils.isEmpty(firstName)){
            mFirstNameInput.setError("Please enter a first name");
            mFirstNameInput.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(lastName)){
            mLastNameInput.setError("Please enter a last name");
            mLastNameInput.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(email)){
            mEmailInput.setError("Please enter your email");
            mEmailInput.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if(email.indexOf('@') == -1){
            mEmailInput.setError("Please enter a valid email");
            mEmailInput.requestFocus();
        }
        if(TextUtils.isEmpty(institution)) {
            mInstitutionInput.setError("Please enter an institution");
            mInstitutionInput.requestFocus();
        }
        if(TextUtils.isEmpty(faculty)){
            mFacultyInput.setError("Please enter a faculty");
            mFacultyInput.requestFocus();
        }
        if(TextUtils.isEmpty(phoneNumber)){
            mPhoneNumberInput.setError("Please enter a phone number");
            mPhoneNumberInput.requestFocus();
        }
        if (TextUtils.isEmpty(password)){
            mPasswordInput.setError("Please enter a password");
            password = "wrong"; // this is to make sure the values aren't null
            mPasswordInput.requestFocus();// .requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(passwordCheck)){
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
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("hwa134", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(registrationEducator.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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

            Map<String, Object> Institutions = new HashMap<>();
            Institutions.put("Name", institution);
            Institutions.put("Faculty", faculty);
            db.collection("Institutions")
                    .add(Institutions).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d("institutionAdded", "Institution added with ID: " + documentReference.getId());
                    institutionID = documentReference.getId();
                    if (institutionID == null){
                        Log.d("idFail", "Institution ID was null.");
                    }
                    Map<String, Object> Educators = new HashMap<>();
                    Educators.put("User_ID", uid);
                    Educators.put("Given_Names", firstName);
                    Educators.put("Last_Names", lastName);
                    Educators.put("Email", email);
                    Educators.put("Phone_Number", phoneNumber);
                    Educators.put("Institution_ID", institutionID);

                    db.collection("Educators")
                            .add(Educators)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d("educatorAdded", "DocumentSnapshot added with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("educatorAddFail", "Error adding document", e);
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("institutionAddFail", "Error adding institution.");
                }
            });
            Intent returnLogin = new Intent(registrationEducator.this, loginActivity.class);
            Toast.makeText(registrationEducator.this, "Registration successful", Toast.LENGTH_SHORT).show();
            startActivity(returnLogin);
        }
    }

}