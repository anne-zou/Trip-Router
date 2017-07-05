package com.example.anne.otp_android_client_v3.controller;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import static com.google.android.gms.internal.zzt.TAG;

/**
 * Created by Anne on 7/4/2017.
 */

public class LocationServicesService {

    private static final int LOCATION_UPDATE_INTERVAL = 5000; // milliseconds

    private static LocationListener myLocationListener = null;

    private LocationServicesService() {}

    static LatLng getCurrentLocation(MainActivity activity) {
        if (Controller.checkLocationPermission(activity)) {
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(GoogleAPIClientSetup.getGoogleApiClient());
            return new LatLng(location.getLatitude(), location.getLongitude());
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
         * Initializes map by repositioning camera
         * Stops the location updates
         *
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) { // todo
            Log.d(TAG, "Location changed");
            mainActivity.updateUIOnLocationChanged(location);
        }
    }

}
