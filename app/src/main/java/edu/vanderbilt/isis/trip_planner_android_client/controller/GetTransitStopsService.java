package edu.vanderbilt.isis.trip_planner_android_client.controller;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPService;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPStopsModel.Stop;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by Anne on 7/4/2017.
 */

class GetTransitStopsService {

    private GetTransitStopsService() {}

    static void requestTransitStopsWithinRadius(final MainActivity activity,
                                                LatLng center, double radius) {

        Call<ArrayList<Stop>> call = TPService.getOtpService().getStopsByRadius(
                TPService.ROUTER_ID,
                Double.toString(center.latitude),
                Double.toString(center.longitude),
                Double.toString(radius),
                "true", "true"
        );

        call.enqueue(new Callback<ArrayList<Stop>>() {
            @Override
            public void onResponse(Call<ArrayList<Stop>> call,
                                   retrofit2.Response<ArrayList<Stop>> response) {
                if (response.isSuccessful())
                    activity.updateUIonTransitStopsRequestResponse(response.body());
                else
                    activity.updateUIonTransitStopsRequestFailure();
            }

            @Override
            public void onFailure(Call<ArrayList<Stop>> call, Throwable throwable) {
                activity.updateUIonTransitStopsRequestFailure();
            }
        });
    }


}
