package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.location.Location;
import android.util.Log;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 7/4/2017.
 */

public class LocationServicesService {

    private static final String TAG = LocationServicesService.class.getName();

    private static final int LOCATION_UPDATE_INTERVAL = 5000; // milliseconds

    private static boolean UIWasInitialized = false; // flag to indicate if the UI was initialized

    private static LocationListener myLocationListener = null;

    private LocationServicesService() {}

    static LatLng getCurrentLocation(MainActivity activity) {
        if (Controller.checkLocationPermission(activity)) {
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(GoogleAPIClientSetup.getGoogleApiClient());
            if (location != null)
                return new LatLng(location.getLatitude(), location.getLongitude());
            else
                return null;
        } else {
            return null;
        }
    }

    static void startHighAccuracyLocationUpdates(MainActivity mainActivity) {
        if (myLocationListener == null)
            myLocationListener = new MyLocationListener(mainActivity);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_UPDATE_INTERVAL);
        if (Controller.checkLocationPermission(mainActivity))
            // Invokes onLocationChanged callback
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(Controller.getGoogleApiClient(), locationRequest,
                            myLocationListener);
    }

    static void startLowAccuracyLocationUpdates(MainActivity mainActivity) {
        if (myLocationListener == null)
            myLocationListener = new MyLocationListener(mainActivity);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (Controller.checkLocationPermission(mainActivity))
            // Invokes onLocationChanged callback
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(Controller.getGoogleApiClient(), locationRequest,
                            myLocationListener);
    }

    static void stopLocationUpdates(MainActivity mainActivity) {
        if (myLocationListener == null)
            myLocationListener = new MyLocationListener(mainActivity);

        LocationServices.FusedLocationApi.removeLocationUpdates(Controller.getGoogleApiClient(),
                myLocationListener);
    }


    private static class MyLocationListener implements LocationListener {

        private MainActivity mainActivity;

        private MyLocationListener(MainActivity activity) {
            mainActivity = activity;
        }

        /**
         * Callback invoked when location update is received
         * @param location the device's current location
         */
        @Override
        public void onLocationChanged(Location location) { // todo
            Log.d(TAG, "Location changed");

            // Initialize the UI if it has not been initialized
            if (!UIWasInitialized) {
                mainActivity.initializeUIOnFirstLocationUpdate();
                UIWasInitialized = true;
            }

            // Update UI
            mainActivity.updateUIOnLocationChanged();
        }
    }

}
