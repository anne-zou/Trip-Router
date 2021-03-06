package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TraverseMode;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TripPlan;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Route;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Stop;
import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;
import edu.vanderbilt.isis.trip_planner_android_client.view.ScheduledTripsCursorAdapter;

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
     * @param activity the activity from which to request location access permission
     * @param connectedRunnable runnable to run when client successfully connected,
     *                          pass true if LocationServicesAPI was added
     * @param failedRunnable runnable to run if client fails
     */
    public static void setUpGooglePlayServices(@NonNull Activity activity,
                                               @Nullable ParameterRunnable<Boolean> connectedRunnable,
                                               @Nullable Runnable failedRunnable) {
        GoogleAPIClientSetup.beginSetUp(activity, connectedRunnable, failedRunnable);
    }


    // Communicate with routing/transit info platform server

    /**
     * Send a trip plan request to the trip planner server
     * @param origin the location of the starting point of the trip
     * @param destination the location of the destination of the trip
     * @param intermediateStops list of intermediate stops for the trip; use null if there are none
     * @param time the time to depart after or arrive by (depends on the value of departBy)
     * @param arriveBy true to arrive by the specified time, false to depart by the specified time
     * @param successRunnable runnable to run upon trip plan response; pass the TripPlan as the
     *                        parameter
     * @param failureRunnable runnable to run upon request failure
     */
    public static void requestTripPlan(@NonNull LatLng origin, @NonNull LatLng destination,
                                       @Nullable List<LatLng> intermediateStops,
                                       @NonNull Date time, boolean arriveBy,
                                       @Nullable final ParameterRunnable<TripPlan> successRunnable,
                                       @Nullable final Runnable failureRunnable) {
        TripPlanRequester.planTrip(origin, destination, intermediateStops, time,
                arriveBy, successRunnable, failureRunnable);
    }

    /**
     * Invalidates the response to any previously made trip plan requests.
     * To be called when it is known that a new trip plan request is about to be made.
     */
    public static void interruptOngoingTripPlanRequests() {
        TripPlanRequester.interruptOngoingTripPlanRequests();
    }

    /**
     * Request the transit routes that service a particular transit stop
     * @param stopId id of the transit stop
     * @param successRunnable runnable to run upon successful response; pass the list of routes
     *                        as the parameter
     * @param failureRunnable runnable to run upon failure of request
     */
    public static void requestRoutesServicingTransitStop(
            @NonNull String stopId,
            @Nullable final ParameterRunnable<List<Route>> successRunnable,
            @Nullable final Runnable failureRunnable) {
        RoutesByStopRequester.requestRoutesServicingTransitStop(stopId, successRunnable,
                failureRunnable);
    }

    /**
     * Invalidates the response to any previously made transit routes requests.
     * To be called when it is known that a new transit routes request is about to be made.
     */
    public static void interruptOngoingRoutesRequests() {
        RoutesByStopRequester.interruptOngoingRoutesRequests();
    }

    /**
     * Requests a list of all the transit stops within a certain radius of a given location
     * Should only need to be called once, during setup of the activity, to get all the transit
     * stops for the city.
     * @param center the center of the area to look for transit stops in
     * @param radius the radius of the area to look for transit stops in
     * @param successRunnable runnable to run upon successful response; the list of stops will be
     *                        passed as the parameter
     * @param failureRunnable runnable to run upon request failure
     */
    public static void requestTransitStopsWithinRadius(
            @NonNull LatLng center, double radius,
            @Nullable final ParameterRunnable<List<Stop>> successRunnable,
            @Nullable final Runnable failureRunnable
    ) {
        StopsByRadiusRequester.requestTransitStopsWithinRadius(center, radius, successRunnable,
                failureRunnable);
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
    public static void requestPlaceById(@NonNull String placeId,
                                        @Nullable ParameterRunnable<Place> responseRunnable,
                                        @Nullable Runnable failureRunnable) {
        PlaceByIdRequester.requestPlaceById(placeId, responseRunnable, failureRunnable);
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
    public static void getGooglePlacesAutocompletePredictions(
            Context context, String query,
            ParameterRunnable<AutocompletePredictionBuffer> runnable) {
        GooglePlacesAutocompletePredictionsRequester.getGooglePlacesAutocompletePredictions(
                context, query, runnable);
    }


    // Package-private method to check location permission

    /**
     * Check if permission for fine location access is granted
     * @param context app context
     * @return true if fine location access permission is granted
     */
    static boolean checkLocationPermission(Context context) {
        return LocationPermissionController.isLocationPermissionGranted(context);
    }


    // Get current location

    /**
     * Get the current location
     * @param context context
     * @return LatLng representing the current location, or null if could not be obtained
     */
    public static LatLng getCurrentLocation(Context context) {
        return LocationServicesController.getCurrentLocation(context);
    }


    // Location updates

    /**
     * Requests frequent, high accuracy location updates
     * Stops any previous location updates
     * @param initialize runnable to be run once after receiving the first location update to
     *                   initialize the UI
     * @param update runnable to be run every time a location update is received to update the UI
     */
    public static void startHighAccuracyLocationUpdates(
            @NonNull Context context, @Nullable Runnable initialize, @Nullable Runnable update) {
        LocationServicesController.startHighAccuracyLocationUpdates(context, initialize, update);
    }

    /**
     * Requests not so frequent, low accuracy location updates
     * Stops any previous location updates
     * @param initialize runnable to be run once after receiving the first location update to
     *                   initialize the UI
     * @param update runnable to be run every time a location update is received to update the UI
     */
    public static void startLowAccuracyLocationUpdates(
            @NonNull Context context, @Nullable Runnable initialize, @Nullable Runnable update) {
        LocationServicesController.startLowAccuracyLocationUpdates(context, initialize, update);
    }

    /**
     * Stops any existing location updates
     */
    public static void stopLocationUpdates() {
        LocationServicesController.stopLocationUpdates();
    }


    // Insert, update, delete from tables in trip planner database

    // TODO: add methods for other database operations as needed


    /**
     * Add a trip to the trip plan history table in the trip planner database
     * @param context app context, used for getting the content resolver
     * @param fromName name of the from location
     * @param toName name of the to location
     * @param fromCoords the from location
     * @param toCoords the to location
     * @param modes string representing the selected modes
     * @param timeStamp time that the search was made, in seconds since epoch
     */
    public static void addToTripPlanHistory(Context context,
                                            String fromName, String toName,
                                            LatLng fromCoords, LatLng toCoords,
                                            String fromAddress, String toAddress,
                                            String modes, long timeStamp) {
        TripPlanHistoryDatabaseAccess.insertIntoTripPlanHistoryTable(context, fromName, toName,
                fromCoords, toCoords, fromAddress, toAddress, modes, timeStamp);
    }


    /**
     * Get a row by its id from the scheduled trips table in the database.
     * Use this to get the info to plan the trip when the user selects "Go" on a scheduled trip.
     * @param scheduleId the id of the trip schedule in the table
     * @return the cursor containing the row
     */
    public static Cursor getTripSchedule(int scheduleId) {
        return ScheduledTripsDatabaseAccess.queryRowInSchedulesTable(scheduleId);
    }


    /**
     * Update an existing schedule by its id, or insert a new schedule into the scheduled trips
     * table in the database
     * @param rowId id of the schedule to update; insert new row if null
     * @param timeFirstTrip the time of the first trip in milliseconds sine epoch
     * @param timeNextTrip the calculated time of the next upcoming trip in milliseconds since epoch
     * @param reminderTime the number of minutes before each trip the user is to be given a reminder
     * @param repeatDays String of space-separated abbreviations representing the days of the week
     *                   the trip is to repeat on, occurring in the order that the days occur in
     *                   the week, Monday being the 1st day. The abbreviations are M T W Th F Sa Su.
     * @param fromCoords the coordinates of the trip origin
     * @param toCoords the coordinates of the trip destination
     * @param fromName the name of the trip origin
     * @param toName the name of the trip destination
     * @param fromAddr the address of the trip origin
     * @param toAddr the address of the trip destination
     */
    public static void addOrUpdateTripSchedule(Context context, Long rowId,
                                                @NonNull Long timeFirstTrip, Long timeNextTrip,
                                                Integer reminderTime, String repeatDays,
                                                @NonNull LatLng fromCoords, @NonNull LatLng toCoords,
                                                @NonNull String fromName, @NonNull String toName,
                                                String fromAddr, String toAddr) {

        ScheduledTripsDatabaseAccess.updateOrInsertRowInSchedulesTable(context, rowId,
                timeFirstTrip, timeNextTrip, reminderTime, repeatDays,
                fromCoords, toCoords, fromName, toName, fromAddr, toAddr);
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


    // Callback to update LocationPermissionController upon requestPermissionsResult in the activity

    /**
     * To be called from the MainActivity from when the response to a location permissions request
     * is received in onRequestPermissionsResult.
     *
     * MUST be called for Google Play Services to be properly set up.
     *
     * @param activity the main activity
     * @param grantResults the permission request results
     */
    public static void handleLocationPermissionRequestResult(
            MainActivity activity, @NonNull int[] grantResults) {
        LocationPermissionController.handleLocationPermissionRequestResult(activity, grantResults);
    }


    // Package-private getter for the Google API client

    /**
     * Retrieve the GoogleApiClient
     * @return the GoogleApiClient, or null if the client is null or not connected
     */
    static GoogleApiClient getGoogleApiClient() {
        return GoogleAPIClientSetup.getGoogleApiClient();
    }

}
