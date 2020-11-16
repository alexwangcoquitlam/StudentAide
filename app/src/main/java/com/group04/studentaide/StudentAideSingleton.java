package com.group04.studentaide;


//Singleton is used to create a single instance of our RequestQueue for the lifetime of our app
//RequestQueue is used for our REST services such as GET and PUT and others
//It will queue our requests and send them off one by one

// REQUESTQUEUE MUS BE INSTANTIATED WITH THE APPLICATION CONTEXT ***NOT ACTIVIITY***

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class StudentAideSingleton {

    private static StudentAideSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private StudentAideSingleton(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized StudentAideSingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new StudentAideSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null){
            //Using getApplicationContext prevents from leaking the Activity if something passes one in
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request <T> request){
        getRequestQueue().add(request);
    }
}
