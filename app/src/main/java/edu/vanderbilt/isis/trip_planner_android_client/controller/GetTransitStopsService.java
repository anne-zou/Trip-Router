package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPService;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Stop;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Anne on 7/4/2017.
 */

class GetTransitStopsService {

    private GetTransitStopsService() {} // private constructor to prevent instantiation

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
    static void requestTransitStopsWithinRadius(
            @NonNull LatLng center, double radius,
            @Nullable final ParameterRunnable<List<Stop>> successRunnable,
            @Nullable final Runnable failureRunnable) {

        // Make the request to the server
        Call<ArrayList<Stop>> call = TPService.getOtpService().getStopsByRadius(
                TPService.ROUTER_ID,
                Double.toString(center.latitude),
                Double.toString(center.longitude),
                Double.toString(radius),
                "true", "true"
        );

        // Set the callback to be executed upon response
        call.enqueue(new Callback<ArrayList<Stop>>() {
            @Override
            public void onResponse(Call<ArrayList<Stop>> call,
                                   retrofit2.Response<ArrayList<Stop>> response) {
                // Update the UI
                if (response.isSuccessful())
                    if (successRunnable != null)
                        successRunnable.run(response.body());
                else
                    if (failureRunnable != null)
                        failureRunnable.run();
            }

            @Override
            public void onFailure(Call<ArrayList<Stop>> call, Throwable throwable) {
                // Update the UI
                if (failureRunnable != null)
                    failureRunnable.run();
            }
        });
    }


}
