package edu.vanderbilt.isis.trip_planner_android_client.controller;

import java.util.Objects;

/**
 * Abstract class that allows a parameter to be passed into a runnable by storing it
 * in a private field.
 */
public abstract class ParameterRunnable implements Runnable {

    private Object mObject;

    public Object getParameterObject() {
        return mObject;
    }

    public void run(Object object) {
        mObject = object;
        run();
    }

}
