package com.example.anne.otp_android_client_v3.controller;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.anne.otp_android_client_v3.view.MainActivity;

/**
 * Created by Anne on 7/3/2017.
 */

public class LocationPermissionService {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    volatile static boolean permissionDenied = false;

    private LocationPermissionService() {}

    /**
     * Returns true if permission for fine location access is granted
     * Otherwise, requests permission from the user and returns false
     * @param activity
     * @return
     */
    static boolean checkAndObtainPermission(MainActivity activity) {
        if (isLocationPermissionGranted(activity)) {
            return true;
        } else {
            requestPermission(activity);
            return false;
        }
    }

    /**
     * Check if permission for fine location access is granted
     * @param activity
     * @return
     */
    static boolean isLocationPermissionGranted(MainActivity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Handles result of user response to the location permission request
     * Call from onRequestPermissionsResult() in the MainActivity
     * @param activity
     * @param grantResults
     */
    public static void handleLocationRequestPermissionsResult(MainActivity activity,
                                                              @NonNull int[] grantResults) {

        if (grantResults.length == 0) { // request cancelled
            Toast.makeText(activity, "Could not get access to location services",
                    Toast.LENGTH_SHORT).show();

        } else if  (grantResults[0] == PackageManager.PERMISSION_DENIED) { // permission denied
            permissionDenied = true;
        }

    }

    /**
     * Requests permission for fine location access from the user
     * @param activity
     */
    private static void requestPermission(MainActivity activity) {
        // Invokes OnRequestPermissionsResult callback
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }



}
