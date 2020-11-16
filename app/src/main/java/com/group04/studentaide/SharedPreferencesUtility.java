package com.group04.studentaide;

/*  This class is used to save key-data of the user, allowing them to not have to login everytime they open the app
* Data is lost when: application is uninstalled or clearing application data through the settings
*
*
* */


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SharedPreferencesUtility {

    //Shared preferences uses key's to retreive data that is stored in key-pair values
    public static final String KEY_preferenceName = "userPrefrence";
    public static final String KEY_firstName = "firstnamekey";
    public static final String KEY_lastName = "lastnamekey";
    public static final String KEY_email = "emailkey";

    //Boolean -- needs to be set further down
    public static final String KEY_educator ="educatorkey";

    public static SharedPreferencesUtility mInstance;
    public static Context mContext;

    //Pass context of application
    private SharedPreferencesUtility(Context context){
        mContext = context;
    }

    //Synchronized makes it so that only one instance can execute at time -- preventing multiple user login's (?)
    public static synchronized SharedPreferencesUtility getInstance(Context context){
        if (mInstance == null){
            mInstance = new SharedPreferencesUtility(context);
        }
        return mInstance;
    }

    /* Methods*/

    //Save user credentials into shared preferences
    public void userLogin(User user){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(KEY_preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_firstName, user.getFirstName());
        editor.putString(KEY_lastName, user.getLastName());
        editor.putString(KEY_email, user.getEmail());
        editor.apply();
    }

    //Check if user is already logged in when opening application
    //App uses email as username when logging in
    //Checking against username that is stored in sharedPreferences
    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(KEY_preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_email, null) != null;
    }

    //Retrieve logged in user
    //For v2, will need to figure out how to save boolean for users to determine whether or not is student or educator
    public User getUser(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(KEY_preferenceName, Context.MODE_PRIVATE);

        return new User (
                sharedPreferences.getString(KEY_firstName, null),
                sharedPreferences.getString(KEY_lastName, null),
                sharedPreferences.getString(KEY_email, null)
        );

    }

    public void logout(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(KEY_preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Clear all saved details from our application context
        editor.clear();
        editor.apply();
        mContext.startActivity(new Intent(mContext, loginActivity.class));
    }

}
