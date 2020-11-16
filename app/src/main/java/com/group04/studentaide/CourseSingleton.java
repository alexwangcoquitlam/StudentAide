package com.group04.studentaide;

/*
Written By: Yufeng Luo
Edited By: Jason Leung
Temporary fix to no database?
Currently don't know how to split data for when a user wants to display study time from a certain period of time --> will need more research and adjust implementation
This class is used to create a singleton object of our LHM and ArrayList,
that way data can be shared throughout activities as the Instance is saved
 */

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;



//whenever you create a new Courses courseList = Courses.getInstance(); --> call this, if there is no instance yet it will be created

public class CourseSingleton{
    private static CourseSingleton ourInstance = null;

    LinkedHashMap<String, ArrayList<Double>> courseList;

    ArrayList<String> courseKeys;

    public CourseSingleton(){
        courseList = new LinkedHashMap<String,ArrayList<Double>>();
        //Dont know if can do this with arraylist and keys
        courseKeys = new ArrayList<String>();
    }

    public static CourseSingleton getInstance(){
        if (ourInstance == null){
            ourInstance = new CourseSingleton();
        }
        return ourInstance;
    }


    //When creating object
    //Courses courseList = new Courses();
    //Then to use Hashmap<String, Double> courseHM = courseList.getLinkedHM();
    public LinkedHashMap<String, ArrayList<Double>> getLinkedHM(){

        return courseList;

    }


    //Methods

    //User enters a course name to create -- new k-v pair will be created in hashmap
    public void setCourseName(String courseName){

        ArrayList<Double> temp = new ArrayList<Double>();
        temp.add(0.0);
        courseList.put(courseName, temp);

    }

    //Use this before adding new time studied to ensure that total time is updated correctly
    public ArrayList<Double> getStudyTime(String courseName) {

        return courseList.get(courseName);

    }

    //After a timer has run down and time has been calculated can add onto total
    public void setStudyTime(String courseName, double timeStudied){

        if (courseList.containsKey(courseName)) {

            ArrayList<Double> temp = courseList.get(courseName);
            temp.add(timeStudied);
            courseList.put(courseName, temp);

        } else {

            ArrayList<Double> temp = new ArrayList<Double>();
            temp.add(timeStudied);
            courseList.put(courseName, temp);

        }

    }

    public Boolean isEmpty(){

        return courseList.size() == 0;

    }

    public void setCourseKeys(String courseName){

        courseKeys.add(courseName);

    }

    //NO CLUE IF THIS WORKS -- there has to be a better way
    public ArrayList<String> getKeys(LinkedHashMap<String, ArrayList<Double>> LinkedHM, ArrayList<String> ArrayKeys){

        Set<String> keySet = LinkedHM.keySet();
        ArrayKeys  = new ArrayList<String>(keySet);

        return ArrayKeys;
    }

}