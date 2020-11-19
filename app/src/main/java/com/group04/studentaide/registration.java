/*
 * Written by: Yufeng Luo
 *
 * UNTESTED
 *
 * Registration Class POSTS user entered information using Volley to Server URL in JSONObject format
 * Using Volleys built-in functionality getParams() that utilizes hashmaps to store key-pair values
 *
 * Makes use of StudentAideSingleton class to create RequestQueues and add Volley requests to be sent to server
 *
 * */

package com.group04.studentaide;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

import static com.group04.studentaide.SharedPreferencesUtility.KEY_email;
import static com.group04.studentaide.SharedPreferencesUtility.KEY_firstName;
import static com.group04.studentaide.SharedPreferencesUtility.KEY_lastName;
import static com.group04.studentaide.serverURL.REGISTER_URL;

public class registration extends AppCompatActivity {

    //Initialize buttons globally
    Button registerButton;
    EditText inputFirstName;
    EditText inputLastName;
    EditText inputEmail; // This will be the username
    EditText inputPassword;
    EditText passwordConfirmation;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String name;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // CHANGE TO NAME OF PROPER XML FILE

        //Check if user is already logged in, if so go straight to app homepage
        if (SharedPreferencesUtility.getInstance(this).isLoggedIn()){
            finish();
            //
            startActivity(new Intent(this, MainActivity.class));
        }

        registerButton = (Button) findViewById(R.id.registerButton);
        inputFirstName = (EditText) findViewById(R.id.firstNameInput);
        inputLastName = (EditText) findViewById(R.id.lastNameInput);
        inputEmail = (EditText) findViewById(R.id.emailInput);

        //Maybe make it so that password needs to be entered the same twice -- ensure user is typing in correct password
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        passwordConfirmation = findViewById(R.id.passwordConfirmation);

        //Want to error check for valid email
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrationFireAuth();
            }
        });

    }

    public void educatorRegister(View view){
        Intent educator = new Intent(registration.this, registrationEducator.class);
        startActivity(educator);
    }

    private void registrationFireAuth(){
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString();
        String passwordCheck = passwordConfirmation.getText().toString();
        String firstName = inputFirstName.getText().toString().trim();
        String lastName = inputLastName.getText().toString().trim();
        name = firstName + " " + lastName;
        name = name.toLowerCase();
        Log.d("nameCheck", name);
        if (TextUtils.isEmpty(firstName)){
            inputFirstName.setError("Please enter a first name");
            inputFirstName.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(lastName)){
            inputLastName.setError("Please enter a last name");
            inputLastName.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(email)){
            inputEmail.setError("Please enter your email");
            inputEmail.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if(email.indexOf('@') == -1){
            inputEmail.setError("Please enter a valid email");
            inputEmail.requestFocus();
        }
        if (TextUtils.isEmpty(password)){
            inputPassword.setError("Please enter a password");
            password = "wrong"; // this is to make sure the values aren't null
            inputPassword.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (TextUtils.isEmpty(passwordCheck)){
            passwordConfirmation.setError("Please confirm your password");
            passwordCheck = "right"; // this is to make sure the values aren't null
            passwordConfirmation.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if(!passwordCheck.equals(password)){
            passwordConfirmation.setError("Passwords do not match");
            passwordConfirmation.requestFocus();
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
                                Map<String, Object> Students = new HashMap<>();
                                Students.put("Email", email);
                                Students.put("Given_Names", firstName);
                                Students.put("Last_Names", lastName);
                                Students.put("Email", email);
                                Students.put("User_ID", user.getUid());
                                //Students.put("Institution_ID", institutionID);

                                db.collection("Students")
                                        .add(Students)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("studentAdded", "DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("studentAddFail", "Error adding document", e);
                                            }
                                        });
                                Intent returnLogin = new Intent(registration.this, loginActivity.class);
                                Toast.makeText(registration.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(returnLogin);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("hwa134", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(registration.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}