package com.group04.studentaide;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class informationRetrieval {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String documentID;
    public String getDocumentID(FirebaseUser userInput){
        String uid;
        if (userInput != null){
            uid = userInput.getUid();
            db.collection("Students")
                    .whereEqualTo("User_ID", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("documentIDGet", "Document ID is: " + document.getId());
                                    documentID = document.getId();
                                }
                            } else {
                                Log.v("signedinwrong", "you're signed into the wrong account for testing");
                            }
                        }
                    });
        }
        else{
            Log.v("userNull", "No user signed in");
            documentID = "No user signed in.";
        }
        return documentID;
    }
}

