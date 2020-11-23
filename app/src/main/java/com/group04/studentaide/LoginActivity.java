/*
 *Written by: Yufeng Luo
 * UNTESTED
 *
 * User login will check information entered serverside and use StringRequest response to determine whether or not credentials are known in database
 * On successful login, new Intent will be created taking users to MainActivity
 *
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    Button logInButton;
    EditText inputEmail;
    EditText inputPassword;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.v("LoginActivityy", String.valueOf((user == null)));

        if (user != null) {
            if (user.getDisplayName() == null) {
                Intent mainAc = new Intent (LoginActivity.this, MainActivity.class);
                startActivity(mainAc);
            } else {
                Intent mainAc = new Intent (LoginActivity.this, MainActivity.class);
                Toast.makeText(LoginActivity.this, "Welcome back, " + user.getDisplayName().toUpperCase(), Toast.LENGTH_LONG).show();
                startActivity(mainAc);
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
                                Intent returnMain = new Intent(LoginActivity.this, MainActivity.class);
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                startActivity(returnMain);
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

}

