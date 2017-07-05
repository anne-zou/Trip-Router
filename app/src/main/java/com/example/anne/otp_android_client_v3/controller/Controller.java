package com.example.anne.otp_android_client_v3.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.example.anne.otp_android_client_v3.view.TransitStopInfoWindowFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

/**
 * Created by Anne on 7/3/2017.
 */

public class Controller {

    public static void setUpGooglePlayServices(MainActivity activity) {
        GoogleAPIClientSetup.beginSetUp(activity);
    }

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

    public static void requestPlaceById(MainActivity activity, String placeId) {
        GetPlaceByIdService.requestPlaceById(activity, placeId);
    }

    static boolean checkLocationPermission(MainActivity activity) {
        return LocationPermissionService.isLocationPermissionGranted(activity);
    }

    public static LatLng getCurrentLocation(MainActivity activity) {
        return LocationServicesService.getCurrentLocation(activity);
    }

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

    static GoogleApiClient getGoogleApiClient() {
        return GoogleAPIClientSetup.getGoogleApiClient();
    }

}
