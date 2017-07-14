package edu.vanderbilt.isis.trip_planner_android_client.controller;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;

import java.util.ArrayList;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPService;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Route;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Anne on 7/4/2017.
 */

class GetTransitRoutesService {

    private static volatile long timeOfLastTransitRoutesInterrupt = 0;

    private GetTransitRoutesService() {} // private constructor to prevent instantiation

    /**
     * Request the transit routes that service a particular transit stop
     * @param activity reference to the main activity
     * @param stopId id of the transit stop
     */
    static void requestRoutesServicingTransitStop(
            final MainActivity activity, String stopId) {

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
                    activity.updateUIonRoutesRequestResponse(response.body());
                else
                    activity.updateUIonRoutesRequestFailure();
            }

            @Override
            public void onFailure(Call<ArrayList<Route>> call, Throwable throwable) {
                // Abort if request was cancelled
                if (timeOfLastTransitRoutesInterrupt > timeOfTransitRouteRequest)
                    return;

                // Update UI
                activity.updateUIonRoutesRequestFailure();

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
