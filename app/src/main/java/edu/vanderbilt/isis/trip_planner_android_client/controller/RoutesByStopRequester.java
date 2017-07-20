package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPService;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Anne on 7/4/2017.
 */

class RoutesByStopRequester {

    private static volatile long timeOfLastTransitRoutesInterrupt = 0;

    private RoutesByStopRequester() {} // private constructor to prevent instantiation

    /**
     * Request the transit routes that service a particular transit stop
     * @param stopId id of the transit stop
     * @param successRunnable runnable to run upon successful response; pass the list of routes
     *                        as the parameter
     * @param failureRunnable runnable to run upon failure of request
     */
    static void requestRoutesServicingTransitStop(
            @NonNull String stopId,
            @Nullable final ParameterRunnable<List<Route>> successRunnable,
            @Nullable final Runnable failureRunnable) {

        // Record the time we began processing this request
        final long timeOfTransitRouteRequest = System.currentTimeMillis();

        // Make the request to the server
        Call<ArrayList<Route>> call = TPService.getOtpService().getRoutesByStop(
                TPService.ROUTER_ID,
                stopId,
                "true", "true"
        );

        // Set the callback to be executed upon response
        call.enqueue(new Callback<ArrayList<Route>>() {
            @Override
            public void onResponse(Call<ArrayList<Route>> call,
                                   Response<ArrayList<Route>> response) {

                // Abort if request was interrupted
                if (timeOfLastTransitRoutesInterrupt > timeOfTransitRouteRequest)
                    return;

                // Update UI
                if (response.isSuccessful())
                    if (successRunnable != null)
                        successRunnable.run(response.body());
                else
                    if (failureRunnable != null)
                        failureRunnable.run();
            }

            @Override
            public void onFailure(Call<ArrayList<Route>> call, Throwable throwable) {
                // Abort if request was cancelled
                if (timeOfLastTransitRoutesInterrupt > timeOfTransitRouteRequest)
                    return;

                // Update UI
                if (failureRunnable != null)
                    failureRunnable.run();

            }
        });
    }

    /**
     * Invalidates the response to any previously made transit routes requests.
     * To be called when it is known that a new transit routes request is about to be made.
     */
    static void interruptOngoingRoutesRequests() {
        timeOfLastTransitRoutesInterrupt = System.currentTimeMillis();
    }
}
