package com.group04.studentaide;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class calendarActivity extends AppCompatActivity {

    private TextView dateDisplay;
    private EditText event;
    private CalendarView calendarDisplay;
    private Button addEvent;
    private long dateOccur;

    //https://medium.com/@Patel_Prashant_/android-custom-calendar-with-events-fa48dfca8257

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        event = findViewById(R.id.eventName);
        addEvent = findViewById(R.id.addEvent);
        calendarDisplay = findViewById(R.id.calendarView);
        dateDisplay = findViewById(R.id.dateTestLabel);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(calendarDisplay.getDate());
        dateDisplay.setText(date);

        calendarDisplay.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            //show the selected date as a toast
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                dateDisplay.setText(day+"/"+(month+1)+"/"+year);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dateOccur = c.getTimeInMillis(); //this is what you want to use later
            }
        });
        addEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createEvent();
            }
        });
    }

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
                Toast.makeText(calendarActivity.this, "There is no app that can support this action.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(calendarActivity.this, "Please specify an event.", Toast.LENGTH_SHORT).show();
        }
    }
}
