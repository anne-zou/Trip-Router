package com.example.anne.otp_android_client_v3;

/**
 * Created by Anne on 5/19/2017.
 */

import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;

import static android.content.ContentValues.TAG;

/**
 * Listener class that updates the search suggestions and the current selected location
 * of a FloatingSearchView when its query changes
 */
public class MyOnQueryChangeListener implements FloatingSearchView.OnQueryChangeListener {

    private MainActivity mActivity;
    private FloatingSearchView mFloatingSearchView;
    private GoogleApiClient mGoogleAPIClient;

    private static volatile long timeOfLastQueryChange;

    /**
     * Constructor passes in the data to be referenced/updated
     */
    public MyOnQueryChangeListener(
            MainActivity activity,
            FloatingSearchView fsv,
            GoogleApiClient gpc) {

        // The activity
        mActivity = activity;

        // The FloatingSearchView object this listener will update
        mFloatingSearchView = fsv;

        // The API Client
        mGoogleAPIClient = gpc;
    }

    /**
     * Callback method invoked when the query changes
     * @param oldQuery
     * @param newQuery
     */
    @Override
    public void onSearchTextChanged(String oldQuery, String newQuery) {

        Log.d(TAG, "Query change detected");

        // Get the current time
        final long timeOfThisCall = System.currentTimeMillis();

        // Update the time of the most recent query change
        timeOfLastQueryChange = timeOfThisCall;

        // Block for a quarter second, then if timeOfLastQueryChange has been updated
        // by another call to onSearchTextChanged, don't do any more work!
        try {
            Thread.sleep((long) 250);
            if (timeOfLastQueryChange > timeOfThisCall) {
                Log.d(TAG, "Still typing. Query: " + newQuery);
                return;
            }
        } catch (InterruptedException ie) {
            Log.d(TAG, "Thread interrupted while waiting to see if user was done typing");
        }

        // Remove the current selected Place
        mActivity.updateMyFsvDestination(null);

        // If the query has been cleared, clear the search suggestions and return
        if (newQuery.equals("")) {
            mFloatingSearchView.clearSuggestions();
            Log.d(TAG, "Query was cleared");
            return;
        }

        // Otherwise, get the Google Places autocomplete predictions for the query
        PendingResult<AutocompletePredictionBuffer> pendingResult =
                Places.GeoDataApi.getAutocompletePredictions(
                        mGoogleAPIClient, newQuery, getBounds(),
                        new AutocompleteFilter.Builder()
                                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                                .build()
                );

        Log.d(TAG, "Pending result of search autocomplete predictions");


        // Set the callback method for when the autocomplete predictions are ready
        pendingResult.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {

            @Override
            public void onResult(@NonNull final AutocompletePredictionBuffer buffer) {
                Log.d(TAG, "Buffer of autocomplete predictions received");

                // Return if the request for autocomplete predictions failed
                if (!buffer.getStatus().isSuccess()) {
                    Log.d(TAG, "Buffer status: failed");
                    return;
                }

                Log.d(TAG, "Buffer status: success");

                // Clear search suggestions & current selected location if there are no results
                if (buffer.getCount() == 0) {
                    Log.d(TAG, "Buffer was empty");
                    mFloatingSearchView.clearSuggestions();
                }

                // Loop through the auto-prediction results, get the corresponding Places, and
                // create a list of the corresponding PlaceSearchSuggestions to swap the
                // current suggestions with:

                // Create a new list of PlaceSearchSuggestions
                final List<PlaceSearchSuggestion> placeSearchSuggestions = new ArrayList<>();

                // Create a CountDownLatch for threads updating the search suggestions list
                final CountDownLatch bufferReleaseLatch =
                        new CountDownLatch(buffer.getCount());

                // For each autocomplete prediction in the buffer,
                // construct a PlaceSearchSuggestion and add it to the list
                for (final AutocompletePrediction prediction : buffer) {
//                    Log.d(TAG, "Looping through buffer");
//                    Log.d(TAG, "Adding place: " + prediction.getPrimaryText(null));

                    String placeId = prediction.getPlaceId();

                    // Get the Place by its Google Place ID
                    Places.GeoDataApi.getPlaceById(mGoogleAPIClient, placeId)
                            .setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(PlaceBuffer places) {
                                    Place resultPlace = places.get(0);

                                    // Add a new PlaceSearchSuggestion to the list
                                    placeSearchSuggestions.add(new PlaceSearchSuggestion(
                                            prediction.getPrimaryText(null),
                                            prediction.getSecondaryText(null),
                                            resultPlace.getLatLng()
                                    ));

                                    places.release();

                                    long count = bufferReleaseLatch.getCount() - 1;
                                    bufferReleaseLatch.countDown();
//                            Log.d(TAG, "Buffer release latch was counted down. " +
//                                    "The count is now: " + count);

                                    // If all the AutoCompletePrediction items in the buffer have been
                                    // added to the search suggestions list, swap the SearchSuggestions
                                    // and release the AutocompletePredictionBuffer
                                    if (count == 0) {
                                        mFloatingSearchView.swapSuggestions(placeSearchSuggestions);
                                        buffer.release();
                                        Log.d(TAG, "Buffer was released");
                                    }

                                } // end definition of onResult callback for the getPlaceById() method
                            }); // end define & set ResultCallback object for getPlaceById()

                } // end loop through the buffer of AutoCompletePredictions

            } // end definition of onResult callback for the PendingResult
        }); // end define & set ResultCallback object for the PendingResult

    } // end OnSearchTextChanged() callback method


    /**
     *  Helper function to generate latitude and longitude bounds to bias the results of a Google
     *  Places autocomplete prediction to a 20-mile-wide square centered at the current location
     *  If the current location is unavailable, returns bounds encompassing the whole globe
     */
    private LatLngBounds getBounds() {

        try {
            // Get current location
            Location location = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleAPIClient);

            if (location != null) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                // Return bounds for a 20-mile-wide square centered at the current location
                return new LatLngBounds(new LatLng(latitude - .145, longitude - .145),
                        new LatLng(latitude + .145, longitude - .145));
            } else {

                // If location is null, return bounds for the whole globe
                return new LatLngBounds(new LatLng(-90,-180), new LatLng(90, 180));
            }

        } catch (SecurityException se) {

            // If we cannot access the current location, return bounds for the whole globe
            return new LatLngBounds(new LatLng(-90,-180), new LatLng(90, 180));
        }
    }

}

