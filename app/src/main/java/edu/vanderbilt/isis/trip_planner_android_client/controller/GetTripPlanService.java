package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Response;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPService;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Anne on 7/4/2017.
 */

class GetTripPlanService {

    private static final String TAG = GetTripPlanService.class.getName();

    private volatile static long timeOfLastTripPlanInterrupt = 0;

    private GetTripPlanService() {}

    static void planTrip(final MainActivity activity, LatLng origin, LatLng destination,
                         @Nullable List<LatLng> intermediateStops,
                         @NonNull Date time, boolean departBy) {

        final long timeBeginPlanTrip = System.currentTimeMillis();

        String startLocationString = Double.toString(origin.latitude) +
                "," + Double.toString(origin.longitude);
        String endLocationString = Double.toString(destination.latitude) +
                "," + Double.toString(destination.longitude);

        String intermediateLocationsString = "";
        if (intermediateStops                                                                           != null && !intermediateStops.isEmpty()) {
            for (LatLng latLng : intermediateStops)
                intermediateLocationsString += ";" + Double.toString(latLng.latitude) +
                        "," + Double.toString(latLng.longitude);
            intermediateLocationsString = intermediateLocationsString.substring(1); // handle fencepost
        }

        String modesString = ModeSelectOptions.getSelectedModesString();
        String dateString = new SimpleDateFormat("MM-dd-yyyy", Locale.US).format(time);
        String timeString = new SimpleDateFormat("hh:mma", Locale.US).format(time);


        Call<Response> response;
        if (intermediateStops == null || intermediateStops.isEmpty()) {
            response = TPService.getOtpService().getTripPlan(
                    TPService.ROUTER_ID,
                    startLocationString,
                    endLocationString,
                    modesString,
                    true,
                    "TRANSFERS",
                    dateString,
                    timeString,
                    departBy
            );
        } else {
            response = TPService.getOtpService().getTripPlan(
                    TPService.ROUTER_ID,
                    startLocationString,
                    endLocationString,
                    intermediateLocationsString,
                    true,
                    modesString,
                    "TRANSFERS",
                    dateString,
                    timeString,
                    departBy
            );
        }

        response.enqueue(new Callback<Response>() {

            // Handle the request response
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {

                // Abort if request was cancelled
                if (timeOfLastTripPlanInterrupt > timeBeginPlanTrip)
                    return;

                if (response.isSuccessful())
                    activity.updateUIonTripPlanResponse(response.body().getPlan());
                else
                    activity.updateUIonTripPlanFailure();
            }

            @Override
            public void onFailure(Call<Response> call, Throwable throwable) {

                // Abort if request was cancelled
                if (timeOfLastTripPlanInterrupt > timeBeginPlanTrip)
                    return;

                activity.updateUIonTripPlanFailure();
            }

        });

        Log.d(TAG, "Made request to OTP server");
        Log.d(TAG, "Starting point coordinates: " + origin.toString());
        Log.d(TAG, "Destination coordinates: " + destination.toString());
    }

    static void interruptOngoingTripPlanRequests() {
        timeOfLastTripPlanInterrupt = System.currentTimeMillis();
    }

}
