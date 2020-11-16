package com.group04.studentaide;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/*  File Name: studyStatistics.java
    Team: ProjectTeam04
    Written By: Jason Leung
    Changes:
        November 7th - Draft 1 of Version 1
        November 8th - Draft 2 of Version 1
        November 9th - Finalized Version 1
    Bugs:

 */

public class studyStatistics extends AppCompatActivity {

	TextView totalTimeStudied;
	TextView userName;
	String currentName;
	FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stats);

		if (user != null) {
			currentName = user.getDisplayName().toUpperCase();
		}
		else{
			currentName = "Signed Out";
		}
		Log.d("userNull", currentName);
		userName = findViewById(R.id.userNameDisplay);
		userName.setText(currentName);
		double totalTime = getTimeTotalNoServer();
		String displayTotal = Double.toString(totalTime);
		totalTimeStudied = (TextView) findViewById(R.id.totalTimeCount);
		totalTimeStudied.setText(displayTotal);

	}

	private double getTimeTotalNoServer() {

		CourseSingleton courseList = CourseSingleton.getInstance();
		LinkedHashMap<String, ArrayList<Double>> courseHM = courseList.getLinkedHM();

		double totalTime = 0;

		ArrayList<String> courseNames = new ArrayList<String>();
		courseNames = courseList.getKeys(courseHM, courseNames);
		ArrayList<Double> allTimeStudied;

		for (int i = 0; i < courseNames.size(); i++) {

			allTimeStudied = courseList.getStudyTime(courseNames.get(i));

			for (int j = 0; j < allTimeStudied.size(); j++) {

				totalTime += allTimeStudied.get(j);

			}

		}

		return totalTime;

	}

	/* GET request to retrieve all study session data and returns sum of all duration (totalTimeStudied).
       Currently requires database to function, will change once we get internal storage working.
     */

	/*
	private double getTimeTotal(String GUID) throws Exception {

		String getUrl = ROOT_URL + "study_schedule/" + GUID;
		ArrayList<Double> durationList = new ArrayList<>();

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {

				try {

					JSONArray jsonArray = response.getJSONArray("study_schedule");

					for (int i = 0; i < jsonArray.length(); i++) {

						JSONArray jsonArr2 = jsonArray.getJSONArray(i);

						for (int j = 0; j < jsonArr2.length(); j++) {

							JSONObject jsonObject = jsonArr2.getJSONObject(j);
							double duration = jsonObject.getDouble("duration");

							String start = jsonObject.getString("start");
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
							LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
							LocalDateTime currentDateTime = LocalDateTime.now();

							if (currentDateTime.isAfter(startDateTime)) {

								durationList.add(duration);

							}

						}

					}

				} catch (JSONException e) {

					e.printStackTrace();

				}

			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				error.printStackTrace();

			}

		});

		double totalTime = 0;

		for (int i = 0; i < durationList.size(); i++) {

			totalTime += durationList.get(i);

		}

		StudentAideSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
		return totalTime;

	}
	*/
	/* GET request to retrieve all study session data and only returns sum of duration from specific courses
       (courseTime).
       Currently requires database to function, I don't think we'll be using this in v1 so I won't be
       editing it for now.
     */
	/*
	private double getTimeCourse(String GUID, int courseCode) throws Exception {

		String getUrl = ROOT_URL + "study_schedule/" + GUID;
		ArrayList<Double> courseDurationList = new ArrayList<>();

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {

				try {

					JSONArray jsonArray = response.getJSONArray("study_schedule");

					for (int i = 0; i < jsonArray.length(); i++) {

						JSONArray jsonArr2 = jsonArray.getJSONArray(i);

						for (int j = 0; j < jsonArr2.length(); j++) {

							JSONObject jsonObject = jsonArr2.getJSONObject(j);
							double duration = jsonObject.getDouble("duration");
							int courseID = jsonObject.getInt("course_id");

							String start = jsonObject.getString("start");
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
							LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
							LocalDateTime currentDateTime = LocalDateTime.now();

							if (currentDateTime.isAfter(startDateTime) && courseID == courseCode) {

								courseDurationList.add(duration);

							}

						}

					}

				} catch (JSONException e) {

					e.printStackTrace();

				}

			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				error.printStackTrace();

			}

		});

		double courseTime = 0;

		for (int i = 0; i < courseDurationList.size(); i++) {

			courseTime += courseDurationList.get(i);

		}

		StudentAideSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
		return courseTime;

	}
	*/
}