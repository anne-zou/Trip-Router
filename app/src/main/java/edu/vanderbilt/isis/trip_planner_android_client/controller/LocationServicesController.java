package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 7/4/2017.
 */

public class LocationServicesController {

    private static final String TAG = LocationServicesController.class.getName();

    private static final int LOCATION_UPDATE_INTERVAL = 5000; // milliseconds

    private static LocationListener myLocationListener = null;

    private LocationServicesController() {} // private constructor to prevent instantiation

    /**
     * Get the current location
     * @param context context
     * @return LatLng representing the current location, or null if could not be obtained
     */
    static LatLng getCurrentLocation(Context context) {
        if (Controller.checkLocationPermission(context)
                && Controller.getGoogleApiClient() != null) {

            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(Controller.getGoogleApiClient());
            if (location != null)
                return new LatLng(location.getLatitude(), location.getLongitude());
            else
                return null;
        } else {
            return null;
        }
    }

    /**
     * Requests frequent, high accuracy location updates.
     * Stops any previous location updates.
     * @param initialize runnable to be run once after receiving the first location update to
     *                   initialize the UI
     * @param update runnable to be run every time a location update is received to update the UI
     */

    static void startHighAccuracyLocationUpdates(
            @NonNull Context context, @Nullable Runnable initialize, @Nullable Runnable update) {
        if (myLocationListener == null)
            myLocationListener = new MyLocationListener(initialize, update);

        // Remove previous location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(Controller.getGoogleApiClient(),
                myLocationListener);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL);
        if (Controller.checkLocationPermission(context)
                && Controller.getGoogleApiClient() != null)
            // Invokes onLocationChanged callback
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(Controller.getGoogleApiClient(), locationRequest,
                            myLocationListener);
    }

    /**
     * Requests not so frequent, low accuracy location updates.
     * Stops any previous location updates.
     * @param initialize runnable to be run once after receiving the first location update to
     *                   initialize the UI
     * @param update runnable to be run every time a location update is received to update the UI
     */
    static void startLowAccuracyLocationUpdates(
            @NonNull Context context, @Nullable Runnable initialize, @Nullable Runnable update) {
        if (myLocationListener == null)
            myLocationListener = new MyLocationListener(initialize, update);

        // Remove previous location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(Controller.getGoogleApiClient(),
                myLocationListener);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (Controller.checkLocationPermission(context)
                && Controller.getGoogleApiClient() != null)
            // Invokes onLocationChanged callback
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(Controller.getGoogleApiClient(), locationRequest,
                            myLocationListener);
    }

    /**
     * Stops any existing location updates
     */
    static void stopLocationUpdates() {
        if (myLocationListener != null) {
            // Remove previous location updates
            LocationServices.FusedLocationApi.removeLocationUpdates(Controller.getGoogleApiClient(),
                    myLocationListener);
        }
    }


    /**
     * Implementation of LocationListener to respond to location updates
     */
    private static class MyLocationListener implements LocationListener {

        private static boolean UIWasInitialized = false; // flag to indicate if the UI was initialized

        private static Runnable initializationRunnable;

        private static Runnable updateRunnable;


        /**
         * Constructor to save the initialization and update runnables
         * @param initialize runnable to be run once after receiving the first location update to
         *                   initialize the UI
         * @param update runnable to be run every time a location update is received to update the UI
         */
        private MyLocationListener(Runnable initialize, Runnable update) {
            initializationRunnable = initialize;
            updateRunnable = update;
        }

        /**
         * Callback invoked when location update is received
         * @param location the device's current location
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location changed");

            // Initialize the UI if it has not been initialized by running the initializationRunnable
            if (!UIWasInitialized) {
                if (initializationRunnable != null)
                    initializationRunnable.run();
                UIWasInitialized = true;
            }

            // Update the UI by running the updateRunnable
            if (updateRunnable != null)
                updateRunnable.run();
        }
    }

}
