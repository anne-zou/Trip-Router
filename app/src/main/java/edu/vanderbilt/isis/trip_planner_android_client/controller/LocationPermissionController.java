package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;

/**
 * Created by Anne on 7/3/2017.
 */

public class LocationPermissionController {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    volatile static boolean permissionDenied = false;

    private LocationPermissionController() {} // private constructor to prevent instantiation

    /**
     * Returns true if permission for fine location access is granted
     * Otherwise, requests permission from the user and returns false
     * @param activity
     * @return
     */
    static boolean checkAndObtainLocationPermission(Activity activity) {
        if (isLocationPermissionGranted(activity)) {
            return true;
        } else {
            requestPermission(activity);
            return false;
        }
    }

    /**
     * Check if permission for fine location access is granted
     * @param context app context
     * @return true if fine location access permission is granted
     */
    static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Handles result of user response to the location permission request
     * @param activity the main activity
     * @param grantResults the results
     */
    public static void handleLocationPermissionRequestResult(MainActivity activity,
                                                             @NonNull int[] grantResults) {

        if (grantResults.length == 0) { // request cancelled
            Toast.makeText(activity, "Could not get access to location services",
                    Toast.LENGTH_SHORT).show();
            permissionDenied = true; // set permission denied flag to true

        } else if  (grantResults[0] == PackageManager.PERMISSION_DENIED) { // permission denied
            permissionDenied = true; // set permission denied flag to true
        }

    }

    /**
     * Requests permission for fine location access from the user
     * @param activity
     */
    private static void requestPermission(Activity activity) {
        // Invokes OnRequestPermissionsResult callback
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_LOCATION);
    }



}
