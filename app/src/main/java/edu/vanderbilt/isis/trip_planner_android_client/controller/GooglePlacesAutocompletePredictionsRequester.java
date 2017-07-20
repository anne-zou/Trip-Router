package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Anne on 7/12/2017.
 */

public class GooglePlacesAutocompletePredictionsRequester {

    private static String TAG = GooglePlacesAutocompletePredictionsRequester.class.getName();

    private GooglePlacesAutocompletePredictionsRequester() {} // private constructor to prevent instantiation

    /**
     * Get the GooglePlacesAutocompletePredictions for a given query and process the results
     * with a given ParameterRunnable.
     * Uses a bounds bias of a 20-mile-wide square centered at
     * the current location will for the prediction results.
     *
     * @pre the GoogleApiClient has been setup, has the Places GEO_DATA_API, and is connected
     * @param context used to get location permission to get the current location for the bounds
     *                bias; no bounds bias will be generated if this is null
     * @param query the query to get autocomplete predictions for
     * @param runnable the parameter runnable used to process the results; passes an
     *                 AutoCompletePredictionBuffer as the parameter -- THIS MUST BE RELEASED TO
     *                 PREVENT MEMORY LEAKS
     */
    static void getGooglePlacesAutocompletePredictions(
            Context context, String query,
            final ParameterRunnable<AutocompletePredictionBuffer> runnable) {

        // Get the GoogleApiClient
        GoogleApiClient client = Controller.getGoogleApiClient();

        // GoogleApiClient must be connected and have the places geo data api connected
        if (client != null && client.isConnected()
                && client.hasConnectedApi(Places.GEO_DATA_API)) {

            // If the GoogleApiClient has the location services api connected, get a bounds
            // bias for the autocomplete search results centered on the device's current location
            LatLngBounds bounds = null;
            if (client.hasConnectedApi(LocationServices.API) && context != null)
                bounds = getBoundsBias(context);

            // Get the Autocomplete Predictions in a PendingResult
            PendingResult<AutocompletePredictionBuffer> pendingResult =
                    Places.GeoDataApi.getAutocompletePredictions(
                            Controller.getGoogleApiClient(), query,
                            bounds, null);

            pendingResult.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                @Override
                public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                    // Run the ParameterRunnable to process the autocomplete prediction results,
                    // passing in the predictions buffer as the parameter

                    int size = autocompletePredictions.getCount();
                    Log.d(TAG, "Result buffer size: " + size);

                    runnable.run(autocompletePredictions);
                }
            });

        } else {
            Log.e(TAG, "Could not get Google Places Autocomplete Predictions; GoogleApiClient " +
                    "not ready.");
        }
    }

    /**
     *  Helper function to generate latitude and longitude bounds to bias the results of a Google
     *  Places autocomplete prediction to a 20-mile-wide square centered at the current location
     *  If the current location is unavailable, returns bounds encompassing the whole globe
     */
    private static LatLngBounds getBoundsBias(Context context) {

        // Get current location
        LatLng location = Controller.getCurrentLocation(context);

        if (location != null) {
            // Return bounds for a 20-mile-wide square centered at the current location
            double latitude = location.latitude;
            double longitude = location.longitude;
            return new LatLngBounds(new LatLng(latitude - .145, longitude - .145),
                    new LatLng(latitude + .145, longitude - .145));

        } else {
            // If we cannot access location, return bounds for the whole globe
            return new LatLngBounds(new LatLng(-90,-180), new LatLng(90, 180));
        }

    }
}
