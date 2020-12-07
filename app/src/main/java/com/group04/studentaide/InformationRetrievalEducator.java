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

import javax.security.auth.callback.Callback;

public class InformationRetrievalEducator {

    private static InformationRetrievalEducator ourInstance = null;
    String educatorDocumentID = null;
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
                                    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();
                                    infoRetrieve.studentDocumentID = null;
                                    educatorDocRef = document.getReference();
                                    institutionID = document.getDocumentReference("Institution_ID");

                                    Log.v("Hareye", "Grabbing educatorDocumentID");

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
            db.collection("Educators")
                    .whereEqualTo("User_ID", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("documentIDGet", "Document ID is: " + document.getId());
                                    Log.v("Hareye", "Updating Educator ID");
                                    educatorDocumentID = document.getId();
                                    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();
                                    infoRetrieve.studentDocumentID = null;
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

    public void updateID(CallbackEd callback) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {

            String uid = user.getUid();
            db.collection("Educators")
                    .whereEqualTo("User_ID", uid)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("documentIDGet", "Document ID is: " + document.getId());
                                    Log.v("Hareye", "Updating Educator ID");
                                    educatorDocumentID = document.getId();
                                    InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();
                                    infoRetrieve.studentDocumentID = null;
                                    callback.callEd();
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

    // Callback function
    public interface CallbackEd {
        void callEd();
    }

}
