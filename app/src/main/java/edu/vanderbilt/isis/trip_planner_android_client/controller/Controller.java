package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TraverseMode;
import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;

import com.google.android.gms.common.api.GoogleApiClient;
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
     * @param activity the activity the API client is to be associated with
     */
    public static void setUpGooglePlayServices(MainActivity activity) {
        GoogleAPIClientSetup.beginSetUp(activity);
    }

    // Communication with routing/transit info platform server

    public static void requestTripPlan(MainActivity activity,
                                           LatLng origin, LatLng destination,
                                           @Nullable List<LatLng> intermediateStops,
                                           @NonNull Date time, boolean departBy) {
        GetTripPlanService.planTrip(activity, origin, destination, intermediateStops, time,
                departBy);
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

    public static void requestPlaceById(MainActivity activity, String placeId) {
        GetPlaceByIdService.requestPlaceById(activity, placeId);
    }

    // Package-private method to check location permission

    static boolean checkLocationPermission(MainActivity activity) {
        return LocationPermissionService.isLocationPermissionGranted(activity);
    }

    // Get current location

    public static LatLng getCurrentLocation(MainActivity activity) {
        return LocationServicesService.getCurrentLocation(activity);
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
    // TODO: add methods for new operations as needed (call on SearchHistoryDatabaseService)

    public static void addToSearchHistory(Context context,
                                                  String fromName, String toName,
                                                  LatLng fromCoords, LatLng toCoords,
                                                  String modes, long timeStamp) {
        SearchHistoryDatabaseService.insertIntoSearchHistoryTable(context, fromName, toName,
                fromCoords, toCoords, modes, timeStamp);
    }

    // Mode-select options

    public static boolean selectMode(TraverseMode mode) {
        return ModeSelectOptions.selectMode(mode);
    }

    public static boolean deselectMode(TraverseMode mode) {
        return ModeSelectOptions.deselectMode(mode);
    }

    public static boolean isModeSelected(TraverseMode mode) {
        return ModeSelectOptions.isSelected(mode);
    }

    public static Set<TraverseMode> getSelectedModes() {
        return ModeSelectOptions.getSelectedModes();
    }

    public static String getSelectedModesString() {
        return ModeSelectOptions.getSelectedModesString();
    }

    public static int getNumSelectedModes() {
        return ModeSelectOptions.getNumSelectedModes();
    }

    public static void setFirstMode(TraverseMode mode) {
        ModeSelectOptions.setFirstMode(mode);
    }

    public static void removeFirstMode() {
        ModeSelectOptions.removeFirstMode();
    }

    public static TraverseMode getFirstMode() {
        return ModeSelectOptions.getFirstMode();
    }

    public static boolean addDefaultMode(TraverseMode mode) {
        return ModeSelectOptions.addDefaultMode(mode);
    }

    public static boolean removeDefaultMode(TraverseMode mode) {
        return ModeSelectOptions.removeDefaultMode(mode);
    }

    public static void setDefaultModes(Set<TraverseMode> modes) { //todo
        ModeSelectOptions.setDefaultModes(modes);
    }

    // Package-private getter for the Google API client

    static GoogleApiClient getGoogleApiClient() {
        return GoogleAPIClientSetup.getGoogleApiClient();
    }

}
