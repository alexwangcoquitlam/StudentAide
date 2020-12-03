/*
 * Written by: Yufeng Luo, Alexander Wang
 * UNTESTED
 * This page is presented to the user on startup, and if the user is already signed in, then they are redirected to the main page.
 * The user has the option to continue as a guest account, but will have limited features
 * There is also the possibility to register for an account
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

// Change current UID when logging in

public class LoginActivity extends AppCompatActivity {

    Button logInButton;
    EditText inputEmail;
    EditText inputPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String greeting;
    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();
    InformationRetrievalEducator infoRetrieveEd = InformationRetrievalEducator.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.v("LoginActivityy", String.valueOf((user == null)));

        if (user != null) {
            if (user.getDisplayName() == null) {
                if (infoRetrieveEd.getEducatorDocumentID() != null){
                    infoRetrieveEd.updateID();
                    Intent returnMain = new Intent(LoginActivity.this, MainActivityEducator.class);
                    startActivity(returnMain);
                }
                else {
                    infoRetrieve.updateID();
                    Intent returnMain = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(returnMain);
                }
            } else {
                if (infoRetrieveEd.getEducatorDocumentID() != null){
                    infoRetrieveEd.updateID();
                    Intent returnMain = new Intent(LoginActivity.this, MainActivityEducator.class);
                    Toast.makeText(LoginActivity.this, createGreeting(), Toast.LENGTH_LONG).show();
                    startActivity(returnMain);
                }
                else {
                    infoRetrieve.updateID();
                    Intent returnMain = new Intent(LoginActivity.this, MainActivity.class);
                    Toast.makeText(LoginActivity.this, createGreeting(), Toast.LENGTH_LONG).show();
                    startActivity(returnMain);
                }
            }
        }

        logInButton = (Button) findViewById(R.id.login);
        inputEmail = (EditText) findViewById(R.id.emailInputLogin);
        inputPassword = (EditText) findViewById(R.id.password);

        //If the response from server is success, allow login

        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginFire();
            }
        });

    }

    //Register button clicked, open register activity
    public void register(View view){
        Intent register = new Intent(this, com.group04.studentaide.Registration.class);
        startActivity(register);
    }

    // Checks if the textboxes are empty, then checks Firebase authentication to log the user in.
    private void loginFire(){
        inputEmail = findViewById(R.id.emailInputLogin);
        inputPassword = findViewById(R.id.password);
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            inputEmail.setError("Please enter your email");
            inputEmail.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Please enter your password");
            inputPassword.requestFocus(); // requestFocus will make the focus go to this box that is empty
        }
        if (email.indexOf('@') == -1){
            inputEmail.setError("Please enter a valid email");
            inputEmail.requestFocus();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("hwa135", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (infoRetrieveEd.getEducatorDocumentID() != null){
                                    infoRetrieveEd.updateID();
                                    Intent returnMain = new Intent(LoginActivity.this, MainActivityEducator.class);
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    startActivity(returnMain);
                                }
                                else {
                                    infoRetrieve.updateID();
                                    Intent returnMain = new Intent(LoginActivity.this, MainActivity.class);
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    startActivity(returnMain);
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("hwa136", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Please check your email and password.",
                                        Toast.LENGTH_LONG).show();
                                // ...
                            }

                            // ...
                        }
                    });
        }


    }

    public void continueAsGuest(View view){
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
    }

    private String createGreeting(){
        String fullName = user.getDisplayName();
        String arr[] = fullName.split(" ", 2);

        String firstName = arr[0];
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
        greeting = "Hello, " + firstName + ".";
        return greeting;
    }

}