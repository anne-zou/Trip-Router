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

    private GetTransitRoutesService() {}

    static void requestRoutesServicingTransitStop(
            final MainActivity activity, String stopId) {

        final long timeOfTransitRouteRequest = System.currentTimeMillis();

        Call<ArrayList<Route>> call = TPService.getOtpService().getRoutesByStop(
                TPService.ROUTER_ID,
                stopId,
                "true", "true"
        );

        call.enqueue(new Callback<ArrayList<Route>>() {
            @Override
            public void onResponse(Call<ArrayList<Route>> call,
                                   Response<ArrayList<Route>> response) {

                // Abort if request was cancelled
                if (timeOfLastTransitRoutesInterrupt > timeOfTransitRouteRequest)
                    return;

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

                activity.updateUIonRoutesRequestFailure();

            }
        });
    }

    static void interruptOngoingRoutesRequests() {
        timeOfLastTransitRoutesInterrupt = System.currentTimeMillis();
    }
}