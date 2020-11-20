package com.group04.studentaide;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/*  File Name: studyStatistics.java
    Team: ProjectTeam04
    Written By: Jason Leung
    Changes:
        November 7th - Draft 1 of Version 1
        November 8th - Draft 2 of Version 1
        November 9th - Finalized Version 1
        November 18th - Draft 1 of Version 2
    Bugs:

 */

public class studyStatistics extends AppCompatActivity {

	PieChart chart;
	TextView timeCount;
	Spinner courseSpinner;
	double totalTime;

	FirebaseFirestore db = FirebaseFirestore.getInstance();
	informationRetrieval infoRetrieve = informationRetrieval.getInstance();
	private static DecimalFormat df = new DecimalFormat("0.00");

	ArrayList<String> courses = new ArrayList<String>();
	ArrayList<String> courseName = new ArrayList<String>();
	ArrayList<Double> duration = new ArrayList<Double>();

	// Need to find a way to grab current users document id
	DocumentReference studentRef;
	String studentDocumentId;
	private int counter = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		grabDocumentReference();

		if (counter == 0) {
			courses.add("Total");
			counter++;
		}

		courseSpinner = (Spinner) findViewById(R.id.chooseCourseStats);
		timeCount = (TextView) findViewById(R.id.totalTimeCount);
		chart = (PieChart) findViewById(R.id.chart);

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

		// When user selects one of their courses in the courseSpinner, display the total amount of time they've spent studying that course
		courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String choice = parent.getItemAtPosition(position).toString();

				courseName.clear();
				duration.clear();

				grabTotalTime(new Callback() {
					@Override
					public void call() {

						if (choice.equals(courses.get(0))) {
							// Populate grabTotalTime with users totalTimeStudied
							String displayTotal = df.format(totalTime);
							timeCount.setText(displayTotal);

							chart.setCenterText("Total Time Studied");
							addTotalChart();

						} else {

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

	// Return current activity
	private studyStatistics getActivity() {

		return this;

	}

	public void addTotalChart() {

		ArrayList<PieEntry> yEntrys = new ArrayList<>();
		ArrayList<String> xEntrys = new ArrayList<>();

		for (int i = 0; i < duration.size(); i++) {
			yEntrys.add(new PieEntry(duration.get(i).floatValue(), i));
		}

		for (int i = 0; i < courseName.size(); i++) {
			xEntrys.add(courseName.get(i));
		}

		PieDataSet pieDataSet = new PieDataSet(yEntrys, "Test");
		pieDataSet.setSliceSpace(2);
		pieDataSet.setValueTextSize(12);

		ArrayList<Integer> colors = new ArrayList<>();
		colors.add(Color.GRAY);
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		pieDataSet.setColors(colors);

		PieData pieData = new PieData(pieDataSet);
		chart.setData(pieData);
		chart.invalidate();

	}

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

		PieDataSet pieDataSet = new PieDataSet(yEntrys, "Test");
		pieDataSet.setSliceSpace(2);
		pieDataSet.setValueTextSize(12);

		ArrayList<Integer> colors = new ArrayList<>();
		colors.add(Color.GRAY);
		colors.add(Color.RED);
		colors.add(Color.GREEN);
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

	// Grabs the totalTimeStudied from database to be displayed
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
