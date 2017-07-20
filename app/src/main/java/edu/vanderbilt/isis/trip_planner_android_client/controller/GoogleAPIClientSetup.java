package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

/**
 * Created by Anne on 7/3/2017.
 */

public class GoogleAPIClientSetup {

    private static final int WAIT_TIME_INTERVAL = 250; // milliseconds

    private static GoogleApiClient googleApiClient = null;

    private static GoogleAPIClientConnectionListener listener = null;

    private static AsyncTask<Activity, Void, Boolean> setUpTask = null;

    static boolean locationServicesEnabled = false;

    private static ParameterRunnable<Boolean> connectedRunnable;

    private static Runnable failedRunnable;


    private GoogleAPIClientSetup() {} // private constructor to prevent instantiation of class

    /**
     * Runs the AsyncTask that builds the API client
     * @throws IllegalStateException if try to execute AsyncTask while it is RUNNING or FINISHED
     * @param activity the Activity from which to request location access permission
     * @param connectedRunnable runnable to run when client successfully connected,
     *                          pass true if LocationServicesAPI was added
     * @param failedRunnable runnable to run if client fails
     */
    static void beginSetUp(@NonNull Activity activity,
                           @Nullable ParameterRunnable<Boolean> connectedRunnable,
                           @Nullable Runnable failedRunnable) {

        // Make sure we only run setup once
        if (setUpTask == null)
            setUpTask = new GoogleAPIClientSetUpTask();

        GoogleAPIClientSetup.connectedRunnable = connectedRunnable;
        GoogleAPIClientSetup.failedRunnable = failedRunnable;
        setUpTask.execute(activity);
    }

    /**
     * Getter for the GoogleApiClient
     * @return the GoogleApiClient, or null if it is null or not connectedRunnable
     */
    static @Nullable GoogleApiClient getGoogleApiClient() {
        if (googleApiClient.isConnected())
            return googleApiClient;
        else
            return null;
    }

    /**
     * AsyncTask that checks/requests location permission, blocks until permission is granted or
     * denied, and builds the API client. Will build the client without LocationServices API if
     * permission was denied.
     */
    private static class GoogleAPIClientSetUpTask extends AsyncTask<Activity, Void, Boolean> {

        /**
         * Invoked after execute() is called on the AsyncTask
         * Executed on a background thread in order to avoid blocking the UI thread
         * @param params reference to the MainActivity
         * @return true if the client was built with the Location Services API included
         */
        @Override
        protected Boolean doInBackground(Activity... params) {
            Activity activity = params[0];

            // If location permission is granted, go ahead and build API Client with location
            // services; if not granted, request permission from user
            if (LocationPermissionController.checkAndObtainLocationPermission(activity)) {

                buildGoogleApiClientWithLocationServices(activity);
                return true;

            } else { // Permission requested in checkAndObtainLocationPermission(), waiting on result

                // Block while the permission has neither been granted nor denied by the user
                while (!Controller.checkLocationPermission(activity) &&
                        !LocationPermissionController.permissionDenied)
                    try {
                        // Wait for the specified time interval before checking again
                        Thread.sleep(WAIT_TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                // If permission was granted, build API Client with location services
                if (Controller.checkLocationPermission(activity)) {
                    buildGoogleApiClientWithLocationServices(activity);
                    return true;

                } else { // If permission was denied, build API Client without location services
                    buildGoogleApiClientWithoutLocationServices(activity);
                    return false;
                }
            }
        }
    }


    /**
     * Helper method that builds & connects the GoogleApiClient
     * Will set the locationServicesEnabled flag to true
     */
    private static void buildGoogleApiClientWithLocationServices(Context context) {

        // Get the API client builder for a client without the LocationServices API
        GoogleApiClient.Builder builder = prepareToBuildGoogleApiClient(context);

        // Add the Location Services API, and build the API client
        googleApiClient = builder.addApi(LocationServices.API).build();

        // Connect the API client (should invoke the onConnected() method in the connection listener)
        googleApiClient.connect();

        // Set flag indicating that the location services api is built into the client
        locationServicesEnabled = true;
    }


    /**
     * Helper method that builds & connects the GoogleApiClient without the
     * Location Services API
     * The locationServicesEnabled flag will continue to be false
     * @param context the context
     */
    private static void buildGoogleApiClientWithoutLocationServices(Context context) {

        // Get the API client builder for a client without the LocationServices API
        GoogleApiClient.Builder builder = prepareToBuildGoogleApiClient(context);

        // Build the API client
        googleApiClient = builder.build();

        // Connect the API client (should invoke the onConnected() method in the connection listener)
        googleApiClient.connect();

        // Set flag indicating that the location services api has not been built into the client
        locationServicesEnabled = false;
    }


    /**
     * Returns an API client builder for an API client that will have all the desired APIs that do
     * not require permissions.
     * Any desired APIs that do require permissions, if they are granted, should be subsequently
     * added to the returned builder before calling build().
     * @param context the context
     * @return the API client builder
     */
    private static GoogleApiClient.Builder prepareToBuildGoogleApiClient(Context context) {

        // Initialize connection listener for the google api client
        if (listener == null)
            listener = new GoogleAPIClientConnectionListener();

        // Create and return a google api client builder with permission-independent APIs added
        return new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(listener)
                .addApi(Places.GEO_DATA_API);
        // Add any desired additional permission-independent APIs to the api client builder here
    }


    /**
     * Custom connection listener for the Google API Client; updates the UI thread upon connection
     * of the API client
     */
    private static class GoogleAPIClientConnectionListener implements
            GoogleApiClient.OnConnectionFailedListener,
            GoogleApiClient.ConnectionCallbacks {

        /**
         * Invoked when the Google API client is connectedRunnable
         * @param bundle nah
         */
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            // Update the UI
            if (connectedRunnable != null)
                connectedRunnable.run(locationServicesEnabled);
        }

        /**
         * Invoked when the Google API client connection is suspended
         * @param i nah
         */
        @Override
        public void onConnectionSuspended(int i) {}

        /**
         * Invoked when the Google API client connection fails
         * @param connectionResult nah
         */
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            // Update the UI
            if (failedRunnable != null)
                failedRunnable.run();
        }
    }


}
