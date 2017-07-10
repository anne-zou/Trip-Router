package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;
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

    private static AsyncTask<MainActivity, Boolean, Boolean> setUpTask = null;

    static boolean locationServicesEnabled = false;


    private GoogleAPIClientSetup() {} // private constructor to prevent instantiation of class

    /**
     * Runs the AsyncTask that builds the API client
     * @throws IllegalStateException if AsyncTask is RUNNING or FINISHED
     */
    static void beginSetUp(MainActivity activity) {
        // Make sure we only run setup once
        if (setUpTask == null)
            setUpTask = new GoogleAPIClientSetUpTask();
        setUpTask.execute(activity);
    }

    /**
     * AsyncTask that checks/requests location permission and builds the API client
     */
    private static class GoogleAPIClientSetUpTask extends AsyncTask<MainActivity, Boolean, Boolean> {

        /**
         * Invoked after execute() is called on the AsyncTask
         * Executed on a background thread in order to avoid blocking the UI thread
         * @param params
         * @return
         */
        @Override
        protected Boolean doInBackground(MainActivity... params) {
            MainActivity activity = params[0];

            // If location permission is granted, go ahead and build API Client with location
            // services; if not granted, request permission from user

            if (LocationPermissionService.checkAndObtainPermission(activity)) {

                buildGoogleApiClientWithLocationServices(activity);
                return true;

            } else { // Permission requested in checkAndObtainPermission(), waiting on result

                // Wait until the permission is either granted or denied by the user
                while (!LocationPermissionService.isLocationPermissionGranted(activity) &&
                        !LocationPermissionService.permissionDenied)
                    try {
                        // Block for a period of time before checking again
                        Thread.sleep(WAIT_TIME_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                // If permission was granted, build API Client with location services
                if (LocationPermissionService.isLocationPermissionGranted(activity)) {
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
    private static void buildGoogleApiClientWithLocationServices(MainActivity activity) {

        // Get the API client builder
        GoogleApiClient.Builder builder = prepareToBuildGoogleApiClient(activity);

        // Add the Location Services API and build the API client
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
     * @param activity the activity the API client is to be associated with
     */
    private static void buildGoogleApiClientWithoutLocationServices(MainActivity activity) {

        // Get the API client builder
        GoogleApiClient.Builder builder = prepareToBuildGoogleApiClient(activity);

        // Build the API client
        googleApiClient = builder.build();

        // Connect the API client (should invoke the onConnected() method in the connection listener)
        googleApiClient.connect();

        // Set flag indicating that the location services api has not been built into the client
        locationServicesEnabled = false;
    }


    /**
     * Helper method to setup the API client builder before adding any Google APIs that require
     * permissions
     * @param activity the activity the API client is to be associated with
     * @return the API client builder
     */
    private static GoogleApiClient.Builder prepareToBuildGoogleApiClient(MainActivity activity) {

        // Initialize connection listener for the google api client
        if (listener == null)
            listener = new GoogleAPIClientConnectionListener(activity);

        // Create and return a google api client builder with permission-independent APIs added
        return new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(listener)
                .addApi(Places.GEO_DATA_API);
        // Add any desired additional permission-independent APIs to the api client builder here
    }


    /**
     * @return the GoogleApiClient
     */
    static @Nullable GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }


    /**
     * Custom connection listener for the Google API Client; updates the UI thread upon connection
     * of the API client
     */
    private static class GoogleAPIClientConnectionListener implements
            GoogleApiClient.OnConnectionFailedListener,
            GoogleApiClient.ConnectionCallbacks {

        private MainActivity mainActivity;


        private GoogleAPIClientConnectionListener(MainActivity activity) {
            mainActivity = activity;
        }

        /**
         * Invoked when the Google API client is connected
         * @param bundle
         */
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            // Invoke callback defined in the view layer to update the UI
            mainActivity.updateUIOnGoogleAPIClientConnected(locationServicesEnabled);
        }

        /**
         * Invoked when the Google API client connection is suspended
         * @param i
         */
        @Override
        public void onConnectionSuspended(int i) {}

        /**
         * Invoked when the Google API client connection fails
         * @param connectionResult
         */
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    }


}
