package com.group04.studentaide;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

/*  File Name: studyStatistics.java
    Team: ProjectTeam04
    Written By: Jason Leung
    Description:
    	This class implements the STATISTICS page in StudentAide. This class will grab data from the Cloud Firestore in order to display the users
    	stats on the page. There will be a Spinner at the top populated with the users courses, along with a Total option. When total is selected,
    	display a pie chart that will show the time studied in each individual course. When a course is selected, display a pie chart that will
    	show the time studied in that course compared to every other course displayed as one whole.
    Changes:
        November 7th - Draft 1 of Version 1
        November 8th - Draft 2 of Version 1
        November 9th - Finalized Version 1
        November 18th - Draft 1 of Version 2
        November 20th - Finalized Version 2
    External Libraries Used:
    	MPAndroidChart by PhilJay
    		https://weeklycoding.com/mpandroidchart/
    Bugs:
    	Haven't tested.
 */

public class StudyStatistics extends AppCompatActivity {

	PieChart chart;
	TextView timeCount;
	Spinner courseSpinner;
	double totalTime;

	FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
	FirebaseFirestore db = FirebaseFirestore.getInstance();
	InformationRetrieval infoRetrieve = InformationRetrieval.getInstance();
	private static DecimalFormat df = new DecimalFormat("0.00");

	ArrayList<String> courses = new ArrayList<String>();
	ArrayList<String> courseName = new ArrayList<String>();
	ArrayList<Double> duration = new ArrayList<Double>();

	DocumentReference studentRef;
	String studentDocumentId;
	private int counter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		if (user == null) {
			Toast.makeText(getApplicationContext(), "Please sign in.", Toast.LENGTH_SHORT).show();
			Intent main = new Intent(this, MainActivity.class);
			startActivity(main);
		} else {

			grabDocumentReference();

			if (counter == 0) {
				courses.add("Total");
				counter++;
			}

			courseSpinner = (Spinner) findViewById(R.id.chooseCourseStats);
			timeCount = (TextView) findViewById(R.id.totalTimeCount);
			chart = (PieChart) findViewById(R.id.chart);

			BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
			bottomNav.setOnNavigationItemSelectedListener(navListener);

			chart.setNoDataText("No Study Sessions Found.");
			chart.setRotationEnabled(true);
			chart.setHoleRadius(25f);
			chart.setTransparentCircleAlpha(0);

			// Populate courseSpinner with users courses
			grabCourses(new Callback() {
				@Override
				public void call() {
					ArrayAdapter<String> courseAdapter= new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, courses);
					courseSpinner.setAdapter(courseAdapter);
				}
			});

			// When clicking on one of the values on the chart, alert the user and display the course name
			chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
				@Override
				public void onValueSelected(Entry e, Highlight h) {
					String entry = e.toString();
					int pos = entry.indexOf("y: ");

					String time = e.toString().substring(pos + 3);
					String name = "Other Courses";

					for (int i = 0; i < courseName.size(); i++) {
						if (duration.get(i) == Double.parseDouble(time)) {
							name = courseName.get(i);
							break;
						}
					}

					Toast.makeText(getApplicationContext(), "Course: " + name, Toast.LENGTH_SHORT).show();
				}
				@Override
				public void onNothingSelected() {

				}
			});

			// When user selects one of their courses in the courseSpinner, display the stats of that course
			courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					String choice = parent.getItemAtPosition(position).toString();

					courseName.clear();
					duration.clear();

					grabTotalTime(new Callback() {
						@Override
						public void call() {

							// If Total is selected
							if (choice.equals(courses.get(0))) {

								// Populate grabTotalTime with users totalTimeStudied
								String displayTotal = df.format(totalTime);
								timeCount.setText(displayTotal);

								chart.setCenterText("Total Time Studied");
								addTotalChart();

							} else {

								// If a course is selected
								for (int i = 0; i < courseName.size(); i++) {
									if (courseName.get(i).equals(choice)) {
										String displayCourseTotal = df.format(duration.get(i));
										timeCount.setText(displayCourseTotal);
									}
								}

								chart.setCenterText("Course Time Studied");
								addCourseChart(choice);

							}

						}
					});

				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

		}

	}

	private BottomNavigationView.OnNavigationItemSelectedListener navListener =
			new BottomNavigationView.OnNavigationItemSelectedListener() {
				@Override
				public boolean onNavigationItemSelected(@NonNull MenuItem item) {
					switch(item.getItemId()){
						case R.id.nav_study:
							Intent study = new Intent(StudyStatistics.this, StudySession.class);
							startActivity(study);
							break;
						case R.id.nav_courses:
							Intent courses = new Intent(StudyStatistics.this, CoursesActivity.class);
							startActivity(courses);
							break;
						case R.id.nav_home:
							Intent main = new Intent(StudyStatistics.this, MainActivity.class);
							startActivity(main);
					}
					return true;
				}
			};

	// Return current activity
	private StudyStatistics getActivity() {

		return this;

	}

	// Changes pie chart to display the time studied in each course when Total is selected
	public void addTotalChart() {

		ArrayList<PieEntry> yEntrys = new ArrayList<>();
		ArrayList<String> xEntrys = new ArrayList<>();

		for (int i = 0; i < duration.size(); i++) {
			yEntrys.add(new PieEntry(duration.get(i).floatValue(), i));
		}

		for (int i = 0; i < courseName.size(); i++) {
			xEntrys.add(courseName.get(i));
		}

		PieDataSet pieDataSet = new PieDataSet(yEntrys, "Total");
		pieDataSet.setSliceSpace(2);
		pieDataSet.setValueTextSize(12);


		ArrayList<Integer> colors = new ArrayList<>();

		// Blue
		colors.add(Color.rgb(0, 0, 255)); // Pure Blue
		colors.add(Color.rgb(73, 73, 255)); // Ultramarine Blue
		colors.add(Color.rgb(135, 206, 235)); // Sky Blue
		colors.add(Color.rgb(181, 126, 220)); // Lavender
		colors.add(Color.rgb(0, 191, 188)); // Yellow

		// Green
		colors.add(Color.rgb(64, 224, 208)); // Turquoise
		colors.add(Color.rgb(63, 122, 77)); // Green
		colors.add(Color.rgb(57, 255, 20)); // Neon Green
		colors.add(Color.rgb(227, 255, 0)); // Lemon Lime
		colors.add(Color.rgb(0, 232, 107)); // Jade

		// Red
		colors.add(Color.rgb(255, 192, 203)); // Pink
		colors.add(Color.rgb(222, 23, 56)); // Red
		colors.add(Color.rgb(255, 131, 0)); // Orange
		colors.add(Color.rgb(255, 203, 164)); // Peach
		colors.add(Color.rgb(181, 101, 29)); // Light Brown

		pieDataSet.setColors(colors);

		PieData pieData = new PieData(pieDataSet);
		chart.setData(pieData);
		chart.invalidate();

	}

	// Changes pie chart to display the specific courses stats when a course is selected
	public void addCourseChart(String choice) {

		ArrayList<PieEntry> yEntrys = new ArrayList<>();
		ArrayList<String> xEntrys = new ArrayList<>();
		float toSubtract = 0;

		for (int i = 0; i < duration.size(); i++) {
			if (courseName.get(i).equals(choice)) {
				toSubtract = duration.get(i).floatValue();
				yEntrys.add(new PieEntry(duration.get(i).floatValue(), i + 1));
			}
		}
		yEntrys.add(new PieEntry((float) (totalTime - toSubtract), 0));

		for (int i = 0; i < courseName.size(); i++) {
			xEntrys.add(courseName.get(i));
		}

		PieDataSet pieDataSet = new PieDataSet(yEntrys, "Courses");
		pieDataSet.setSliceSpace(2);
		pieDataSet.setValueTextSize(12);

		ArrayList<Integer> colors = new ArrayList<>();

		// Red
		colors.add(Color.rgb(255, 192, 203)); // Pink
		colors.add(Color.rgb(222, 23, 56)); // Red
		colors.add(Color.rgb(255, 131, 0)); // Orange
		colors.add(Color.rgb(255, 203, 164)); // Peach
		colors.add(Color.rgb(181, 101, 29)); // Light Brown

		// Blue
		colors.add(Color.rgb(192, 5, 248)); // Neon Purple
		colors.add(Color.rgb(56, 123, 225)); // Royal Blue
		colors.add(Color.rgb(135, 206, 235)); // Sky Blue
		colors.add(Color.rgb(181, 126, 220)); // Lavender
		colors.add(Color.rgb(255, 255, 0)); // Yellow

		// Green
		colors.add(Color.rgb(64, 224, 208)); // Turquoise
		colors.add(Color.rgb(63, 122, 77)); // Green
		colors.add(Color.rgb(57, 255, 20)); // Neon Green
		colors.add(Color.rgb(227, 255, 0)); // Lemon Lime
		colors.add(Color.rgb(0, 168, 107)); // Jade

		pieDataSet.setColors(colors);

		PieData pieData = new PieData(pieDataSet);
		chart.setData(pieData);
		chart.invalidate();

	}

	// Will be used to return current users Document ID
	public void grabDocumentReference() {

		studentDocumentId = infoRetrieve.getDocumentID();
		studentRef = db.collection("Students").document(studentDocumentId);

	}

	// Grabs the stats from database to be displayed
	public void grabTotalTime(Callback callback) {

		db.collection("Statistics")
				.whereEqualTo("Student_SA_ID", studentRef)
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							for (QueryDocumentSnapshot document : task.getResult()) {
								Map<String, Double> coursesTimeStudied = (Map<String, Double>) document.get("coursesTimeStudied");

								for (Map.Entry<String, Double> entry : coursesTimeStudied.entrySet()) {
									String k = entry.getKey();
									Double v = entry.getValue();

									courseName.add(k);
									duration.add(Double.parseDouble(df.format(v / 60)));
								}

								Double total = (Double) document.get("totalTimeStudied");
								totalTime = Double.parseDouble(df.format(total / 60));
								callback.call();
							}
						} else {
							Log.v("StudyStatistics", "Error occurred when getting data from Firebase.");
						}
					}
				});

	}

	// Grabs the users courses to populate Spinner
	public void grabCourses(Callback callback) {

		db.collection("StudentCourses")
				.whereEqualTo("Student_SA_ID", studentRef)
				.get()
				.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
					@Override
					public void onComplete(@NonNull Task<QuerySnapshot> task) {
						if (task.isSuccessful()) {
							for (QueryDocumentSnapshot document : task.getResult()) {
								String courseName = (String) document.get("CourseName");

								courses.add(courseName);
							}
							callback.call();
						} else {
							Log.v("StudyStatistics", "Error occurred when getting data from Firebase.");
						}
					}
				});

	}

	// Callback function
	public interface Callback {
		void call();
	}

}