package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TraverseMode;
import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Anne on 7/3/2017.
 */
// TODO change all the callbacks to runnables and parameterrunnables
public class Controller {

    // Set up Google Play Services

    /**
     * Set up the GoogleAPIClient.
     * Will request user permission for fine location access if not already granted, then wait in
     * the background until permission is granted or manually denied
     * Upon user response: if permission was granted,the GoogleAPIClient will be built with the
     * Location Services API as well as any other desired Google APIs; if permission was denied, the
     * client will be built without the Location Services API
     *
     * @param activity the activity the API client is to be associated with
     */
    public static void setUpGooglePlayServices(MainActivity activity) {
        GoogleAPIClientSetup.beginSetUp(activity);
    }

    // Communication with routing/transit info platform server

    /**
     * Send a trip plan request to the trip planner server
     * @param activity the activity to call the UI updating callback methods on
     * @param origin the location of the starting point of the trip
     * @param destination the location of the destination of the trip
     * @param intermediateStops list of intermediate stops for the trip; use null if there are none
     * @param time the time to depart after or arrive by (depends on the value of departBy)
     * @param arriveBy true to arrive by the specified time, false to depart by the specified time
     */
    public static void requestTripPlan(MainActivity activity,
                                           LatLng origin, LatLng destination,
                                           @Nullable List<LatLng> intermediateStops,
                                           @NonNull Date time, boolean arriveBy) {
        GetTripPlanService.planTrip(activity, origin, destination, intermediateStops, time,
                arriveBy);
    }

    public static void interruptOngoingTripPlanRequests() {
        GetTripPlanService.interruptOngoingTripPlanRequests();
    }

    public static void requestRoutesServicingTransitStop(MainActivity activity,
                                                         String stopId) {
        GetTransitRoutesService.requestRoutesServicingTransitStop(activity, stopId);
    }

    public static void interruptOngoingRoutesRequests() {
        GetTransitRoutesService.interruptOngoingRoutesRequests();
    }

    public static void requestTransitStopsWithinRadius(MainActivity activity, LatLng center,
                                                       double radius) {
        GetTransitStopsService.requestTransitStopsWithinRadius(activity, center, radius);
    }


    // Get Google Place by placeId

    /**
     * Request a Google Place object by its placeId. Updates the UI upon request response and upon
     * request failure via the given Runnable arguments.
     * @param placeId the id of the Place
     * @param responseRunnable the ParameterRunnable to execute upon result; passes the retrieved
     *                       Place object as the parameter
     * @param failureRunnable the Runnable to execute upon request failure
     */
    public static void requestPlaceById(String placeId, ParameterRunnable<Place> responseRunnable,
                                        Runnable failureRunnable) {
        GetPlaceByIdService.requestPlaceById(placeId, responseRunnable, failureRunnable);
    }

    // Get Google Places Autocomplete Predictions for a query

    /**
     * Get the GooglePlacesAutocompletePredictions for a given query and process the results
     * with a given ParameterRunnable.
     * Uses a bounds bias of a 20-mile-wide square centered at
     * the current location will for the prediction results.
     *
     * @param context used to get location permission to get the current location for the bounds
     *                bias; no bounds bias will be generated if this is null
     * @param query the query to get autocomplete predictions for
     * @param runnable the parameter runnable used to process the results; passes an
     *                 AutoCompletePredictionBuffer as the parameter -- THIS MUST BE RELEASED TO
     *                 PREVENT MEMORY LEAKS
     */
    public static void getGooglePlacesAutocompletePredictions(Context context, String query,
                                                              ParameterRunnable<AutocompletePredictionBuffer> runnable) {
        GetGooglePlacesAutocompletePredictionsService.getGooglePlacesAutocompletePredictions(
                context, query, runnable);
    }


    // Package-private method to check location permission

    /**
     * Check if permission for fine location access is granted
     * @param context app context
     * @return true if fine location access permission is granted
     */
    static boolean checkLocationPermission(Context context) {
        return LocationPermissionService.isLocationPermissionGranted(context);
    }


    // Get current location

    public static LatLng getCurrentLocation(Context context) {
        return LocationServicesService.getCurrentLocation(context);
    }


    // Location updates

    public static void startHighAccuracyLocationUpdates(MainActivity activity) {
        LocationServicesService.startHighAccuracyLocationUpdates(activity);
    }

    public static void startLowAccuracyLocationUpdates(MainActivity activity) {
        LocationServicesService.startLowAccuracyLocationUpdates(activity);
    }

    public static void stopLocationUpdates(MainActivity activity) {
        LocationServicesService.stopLocationUpdates(activity);
    }

    // Insert, update, delete from trip planner database

    // TODO: add methods for new database operations as needed

    /**
     * Add a trip to the search history table in the trip planner database
     * @param context app context, used for getting the content resolver
     * @param fromName name of the from location
     * @param toName name of the to location
     * @param fromCoords the from location
     * @param toCoords the to location
     * @param modes string representing the selected modes
     * @param timeStamp time that the search was made, in seconds since epoch
     */
    public static void addToSearchHistory(Context context,
                                          String fromName, String toName,
                                          LatLng fromCoords, LatLng toCoords,
                                          String fromAddress, String toAddress,
                                          String modes, long timeStamp) {
        SearchHistoryDatabaseService.insertIntoSearchHistoryTable(context, fromName, toName,
                fromCoords, toCoords, fromAddress, toAddress, modes, timeStamp);
    }


    // Access & update the currently selected traverse modes

    /**
     * Add a mode to the set of selected modes
     * @param mode the mode to select
     * @return true if the set did not already contain the mode
     */
    public static boolean selectMode(TraverseMode mode) {
        return ModeSelectOptions.selectMode(mode);
    }

    /**
     * Remove a mode from the set of selected modes
     * @param mode the mode to deselect
     * @return true if the set contained the specified mode
     */
    public static boolean deselectMode(TraverseMode mode) {
        return ModeSelectOptions.deselectMode(mode);
    }

    /**
     * Check if a mode is currently selected
     * @param mode the mode to examine
     * @return true if the specified mode is in the set of selected modes
     */
    public static boolean isModeSelected(TraverseMode mode) {
        return ModeSelectOptions.isSelected(mode);
    }

    /**
     * Get the currently selected modes
     * @return the set of selected modes
     */
    public static Set<TraverseMode> getSelectedModes() {
        return ModeSelectOptions.getSelectedModes();
    }

    /**
     * Get a string representation of the currently selected modes
     * @return a comma separated list of strings representing the currently selected modes
     *         e.g. "BUS", "CAR,WALK", "BICYCLE,BUS"
     */
    public static String getSelectedModesString() {
        return ModeSelectOptions.getSelectedModesString();
    }

    /**
     * Get the number of currently selected modes
     * @return the number of currently selected modes
     */
    public static int getNumSelectedModes() {
        return ModeSelectOptions.getNumSelectedModes();
    }

    /**
     * Get the currently selected first mode for the trip plan
     * @return the currently selected first mode, or null if there is no first mode selected
     */
    public static TraverseMode getFirstMode() {
        return ModeSelectOptions.getFirstMode();
    }

    /**
     * Set the specified mode as the first mode for the trip plan
     * @param mode the mode to set as the first mode
     */
    public static void setFirstMode(TraverseMode mode) {
        ModeSelectOptions.setFirstMode(mode);
    }

    /**
     * Remove the currently selected first mode for the trip plan
     */
    public static void removeFirstMode() {
        ModeSelectOptions.removeFirstMode();
    }


    // Access & update the default traverse modes for the app

    public static boolean addDefaultMode(TraverseMode mode) {
        return ModeSelectOptions.addDefaultMode(mode);
    }

    public static boolean removeDefaultMode(TraverseMode mode) {
        return ModeSelectOptions.removeDefaultMode(mode);
    }

    public static void setDefaultModes(Set<TraverseMode> modes) {
        ModeSelectOptions.setDefaultModes(modes);
    }


    // Callback to update LocationPermissionService upon requestPermissionsResult in the activity

    /**
     * To be called from the MainActivity from when the response to a location permissions request
     * is received in onRequestPermissionsResult.
     *
     * MUST be called for Google Play Services to be properly set up.
     *
     * @param activity the main activity
     * @param grantResults the permission request results
     */
    public static void handleLocationRequestPermissionsResult(MainActivity activity,
                                           @NonNull int[] grantResults) {
        LocationPermissionService.handleLocationRequestPermissionsResult(activity, grantResults);
    }


    // Package-private getter for the Google API client

    /**
     * Retrieve the GoogleApiClient
     * @return the GoogleApiClient, or null if the client is not yet connected
     */
    static GoogleApiClient getGoogleApiClient() {
        return GoogleAPIClientSetup.getGoogleApiClient();
    }

}
