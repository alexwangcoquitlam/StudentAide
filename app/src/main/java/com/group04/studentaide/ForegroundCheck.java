package com.group04.studentaide;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

public class ForegroundCheck extends Application implements LifecycleObserver {

    private static ForegroundCheck ourInstance = null;
    boolean paused = false;
    Timer timer = Timer.getInstance();

    public ForegroundCheck() {

        // Nothing happens

    }

    public static ForegroundCheck getInstance() {
        if (ourInstance == null) {
            ourInstance = new ForegroundCheck();
        }
        return ourInstance;
    }

    public void startForegroundCheck() {

        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }

    public void stopForegroundCheck() {

        ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void resumed() {

        paused = false;

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void paused() {

        paused = true;

    }

}