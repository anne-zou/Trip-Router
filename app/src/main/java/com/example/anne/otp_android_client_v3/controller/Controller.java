package com.example.anne.otp_android_client_v3.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.anne.otp_android_client_v3.model.OTPPlanModel.TraverseMode;
import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.example.anne.otp_android_client_v3.view.TransitStopInfoWindowFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by Anne on 7/3/2017.
 */

public class Controller {

    // Set up Google Play Services

    public static void setUpGooglePlayServices(MainActivity activity) {
        GoogleAPIClientSetup.beginSetUp(activity);
    }

    // Routing/map platform services

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

    public static void addToSearchHistoryDatabase(MainActivity activity,
                                                  String fromName, String toName,
                                                  LatLng fromCoords, LatLng toCoords,
                                                  String modes, long timeStamp) {
        SearchHistoryDatabaseService.addToSearchHistory(activity, fromName, toName,
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

    public static void setDefaultModes(Set<TraverseMode> modes) {
        ModeSelectOptions.setDefaultModes(modes);
    }

    // Package-private getter for the Google API client

    static GoogleApiClient getGoogleApiClient() {
        return GoogleAPIClientSetup.getGoogleApiClient();
    }

}
