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

    private static final int WAIT_TIME_INTERVAL = 250;

    private static GoogleApiClient googleApiClient = null;

    private static MyConnectionListener listener = null;

    private static boolean locationServicesEnabled = false;

    private static AsyncTask<MainActivity, Boolean, Boolean> setUpTask = null;

    private GoogleAPIClientSetup() {}

    /**
     * Creates single instance of class if not already existing &
     * runs the AsyncTask that builds the API client
     */
    static void beginSetUp(MainActivity activity) {
        if (setUpTask == null)
            setUpTask = new GoogleAPIClientSetUpTask();
        setUpTask.execute(activity);
    }

    /**
     * AsyncTask that checks/requests location permission and builds the API client
     */
    private static class GoogleAPIClientSetUpTask extends AsyncTask<MainActivity, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(MainActivity... params) {
            MainActivity activity = params[0];

            // If location permission is granted, build API Client with location services;
            // if not grated, request permission from user

            if (LocationPermissionService.checkAndObtainPermission(activity)) {
                buildGoogleApiClientWithLocationServices(activity);
                return true;

            } else { // Permission requested

                // Wait until user grants or denies permission
                while (!LocationPermissionService.isLocationPermissionGranted(activity) &&
                        !LocationPermissionService.permissionDenied)
                    try {
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
     */
    private static void buildGoogleApiClientWithLocationServices(MainActivity activity) {
        if (listener == null)
            listener = new MyConnectionListener(activity);
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(listener)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
        googleApiClient.connect();
        locationServicesEnabled = true;
    }

    /**
     * Helper method that builds & connects the GoogleApiClient without the
     * Location Services API
     */
    private static void buildGoogleApiClientWithoutLocationServices(MainActivity activity) {
        if (listener == null)
            listener = new MyConnectionListener(activity);
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(listener)
                .addOnConnectionFailedListener(listener)
                .addApi(Places.GEO_DATA_API)
                .build();
        googleApiClient.connect();
        locationServicesEnabled = false;
    }

    /**
     * @return the GoogleApiClient
     */
    static @Nullable GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }


    private static class MyConnectionListener implements
            GoogleApiClient.OnConnectionFailedListener,
            GoogleApiClient.ConnectionCallbacks {

        private MainActivity mainActivity;

        private MyConnectionListener(MainActivity activity) {
            mainActivity = activity;
        }

        /**
         * Google API client connected
         * @param bundle
         */
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            mainActivity.updateUIOnGoogleAPIClientConnected(locationServicesEnabled);
        }

        /**
         * Google API client connection suspended
         * @param i
         */
        @Override
        public void onConnectionSuspended(int i) {}

        /**
         * Google API client connected failed
         * @param connectionResult
         */
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
    }


}
