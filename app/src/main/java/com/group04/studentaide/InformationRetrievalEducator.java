package com.group04.studentaide;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class InformationRetrievalEducator {

    private static InformationRetrievalEducator ourInstance = null;
    private String educatorDocumentID;
    private DocumentReference institutionID;
    private DocumentReference educatorDocRef;
    private FirebaseUser user;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public InformationRetrievalEducator() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String UID = user.getUid();

            db.collection("Educators")
                    .whereEqualTo("User_ID", UID)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    educatorDocumentID = document.getId();
                                    educatorDocRef = document.getReference();
                                    institutionID = document.getDocumentReference("Institution_ID");

                                    Log.d("WDF", "Ed ID: " + educatorDocumentID + " " + " Ins ID: " + institutionID);
                                }
                            } else {
                                Log.d("WDF", "Error retrieving educator document ID");
                            }
                        }
                    });
        }

    }

    public static InformationRetrievalEducator getInstance(){
        if (ourInstance == null){
            ourInstance = new InformationRetrievalEducator();
        }

        return ourInstance;
    }

    public String getEducatorDocumentID(){
        return educatorDocumentID;
    }

    public DocumentReference getInstitutionID(){
        return institutionID;
    }

    public DocumentReference getEducatorDocRef(){
        return educatorDocRef;
    }

    public void updateID() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            String uid = user.getUid();
            db.collection("Students")
                    .whereEqualTo("User_ID", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("documentIDGet", "Document ID is: " + document.getId());
                                    educatorDocumentID = document.getId();
                                }
                            } else {
                                Log.v("signedinwrong", "you're signed into the wrong account for testing");
                            }
                        }
                    });

        } else {

            educatorDocumentID = null;

        }

    }

}
