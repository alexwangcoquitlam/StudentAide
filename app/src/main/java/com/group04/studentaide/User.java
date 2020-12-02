package com.group04.studentaide;

public class User {

    private static boolean isEducator = false;

    public static boolean getEducator(){
        return isEducator;
    }
    public static void setIsEducator(Boolean data){
        User.isEducator = data;
    }

}
