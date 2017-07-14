package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Abstract implementation of Runnable that allows a parameter to be passed into the Runnable by
 * storing it in a private field before calling run().
 *
 * Created so that we can implement in the View layer the equivalent of a callback method for an
 * operation that needs to be performed in the Controller layer.
 *
 * run() must be overridden in subclasses, but it should never be called.
 * Use getParameterObject() in the implementation of run() to use the parameter object.
 * Call run(Object) to run the runnable with the specified parameter.
 *
 * Example:
 *
 * // Create a new ParameterRunnable
 * ParameterRunnable<MyClass> paramRunnable = new ParameterRunnable() {
 *      @Override
 *      public void run() {
 *          MyClass myObject = getParameterObject();
 *          // myObject blah blah blah...
 *      }
 * }
 *
 * // ...code blah code blah code blah blah...
 *
 * // Somewhere else, later on:
 * // Run the ParameterRunnable
 * paramRunnable.run(someInstanceofMyClass);
 *
 */
public abstract class ParameterRunnable<T> implements Runnable {

    private T mParameter;

    /**
     * Gets the parameter object. To be called when implementing the run() method with no
     * parameters in order to use the parameter.
     * @return the parameter
     */
    public T getParameterObject() {
        return mParameter;
    }

    /**
     * Run the runnable with the specified parameter.
     * CALL THIS INSTEAD OF run() WITH NO PARAMETERS.
     * run() with no parameters should never be called except from within this method
     * @param parameter the parameter
     */
    public void run(T parameter) {
        // Store the parameter object
        mParameter = parameter;
        // Run the runnable
        run();
    }

}
