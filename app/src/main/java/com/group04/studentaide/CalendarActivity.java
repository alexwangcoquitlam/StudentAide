/*
    Written by Alexander Wang
    1. Events are stored into both the phone's local calendar and the StudentAide database
    2. Events created through the app can be displayed on the app, events created through other apps will not be retrieved
    3. Events are stored with a date, userID and event name

 */

package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {

    private TextView eventDisplay, eventDisplay2, eventDisplay3;
    private EditText event;
    private CalendarView calendarDisplay;
    private Button addEvent;
    private long dateOccur;
    private String uid;
    private String date;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        event = findViewById(R.id.eventName);
        addEvent = findViewById(R.id.addEvent);
        calendarDisplay = findViewById(R.id.calendarView);
        eventDisplay = findViewById(R.id.eventLabel);
        eventDisplay2 = findViewById(R.id.eventLabel1);
        eventDisplay3 = findViewById(R.id.eventLabel2);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // Define a date format for storing the date into the database
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        date = sdf.format(calendarDisplay.getDate());
        dateOccur = calendarDisplay.getDate();
        if (user != null) {
            uid = user.getUid();
        }

        db.collection("Events")
                .whereEqualTo("User_ID", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> eventNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String documentDay = (String) document.get("Date");
                                if(documentDay.equals(date)) {
                                    String eventName = (String) document.get("Event_Name");
                                    eventNames.add(eventName);
                                }
                                else{}
                            }
                            Log.d("listCreated", eventNames.toString());
                            int listSize = eventNames.size();
                            if (listSize == 0){}
                            else if (listSize == 1){
                                eventDisplay.setText(eventNames.get(0));
                            }
                            else if (listSize == 2){
                                eventDisplay.setText(eventNames.get(0));
                                eventDisplay2.setText(eventNames.get(1));
                            }
                            else if (listSize >= 3){
                                eventDisplay.setText(eventNames.get(0));
                                eventDisplay2.setText(eventNames.get(1));
                                eventDisplay3.setText(eventNames.get(2));
                            }
                        } else {
                            Log.v("listCreateFailed", "Error occurred when getting data from Firebase.");
                        }
                    }
                });

        // The calendar displays the events that are pulled from the database, but only the top 3
        calendarDisplay.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dateOccur = c.getTimeInMillis(); //this is what you want to use later
                date = sdf.format(dateOccur);

                db.collection("Events")
                        .whereEqualTo("User_ID", uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    List<String> eventNames = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String documentDay = (String) document.get("Date");
                                        if(documentDay.equals(date)) {
                                            String eventName = (String) document.get("Event_Name");
                                            eventNames.add(eventName);
                                        }
                                        else{}
                                    }
                                    Log.d("listCreated", eventNames.toString());
                                    int listSize = eventNames.size();
                                    if (listSize == 0){}
                                    else if (listSize == 1){
                                        eventDisplay.setText(eventNames.get(0));
                                    }
                                    else if (listSize == 2){
                                        eventDisplay.setText(eventNames.get(0));
                                        eventDisplay2.setText(eventNames.get(1));
                                    }
                                    else if (listSize >= 3){
                                        eventDisplay.setText(eventNames.get(0));
                                        eventDisplay2.setText(eventNames.get(1));
                                        eventDisplay3.setText(eventNames.get(2));
                                    }
                                } else {
                                    Log.v("listCreateFailed", "Error occurred when getting data from Firebase.");
                                }
                            }
                        });


            }
        });

        addEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createEvent();
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch(item.getItemId()){
                        case R.id.nav_study:
                            Intent study = new Intent(CalendarActivity.this, StudySession.class);
                            startActivity(study);
                            break;
                        case R.id.nav_courses:
                            Intent courses = new Intent(CalendarActivity.this, CoursesActivity.class);
                            startActivity(courses);
                            break;
                        case R.id.nav_home:
                            Intent main = new Intent(CalendarActivity.this, MainActivity.class);
                            startActivity(main);
                    }
                    return true;
                }
            };


    // Putting the event into the database, if that is successful then the event is put into the local calendar
    private void createEvent(){
        if(!event.getText().toString().isEmpty()){
            Intent calendar = new Intent(Intent.ACTION_INSERT);
            calendar.setData(CalendarContract.Events.CONTENT_URI);
            calendar.putExtra(CalendarContract.Events.TITLE, event.getText().toString());
            calendar.putExtra(CalendarContract.Events.ALL_DAY, true);
            calendar.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dateOccur);
            calendar.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dateOccur);

            if(calendar.resolveActivity(getPackageManager()) != null){
                startActivity(calendar);
            }
            else{
                Toast.makeText(CalendarActivity.this, "There is no app that can support this action.", Toast.LENGTH_SHORT).show();
            }

            if(user != null) {
                uid = user.getUid();
                String eventName = event.getText().toString().trim();
                String name = user.getDisplayName().toLowerCase();
                Map<String, Object> Events = new HashMap<>();
                Events.put("User_Name", name);
                Events.put("User_ID", uid);
                Events.put("Event_Name", eventName);
                Events.put("Date", date);

                db.collection("Events")
                        .add(Events)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("eventAdded", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("eventAddFail", "Error adding document", e);
                            }
                        });
            }
            else{}
        }
        else{
            Toast.makeText(CalendarActivity.this, "Please specify an event.", Toast.LENGTH_SHORT).show();
        }
    }
}
