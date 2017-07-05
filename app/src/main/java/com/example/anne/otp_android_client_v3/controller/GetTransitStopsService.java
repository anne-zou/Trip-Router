package com.example.anne.otp_android_client_v3.controller;

import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import com.example.anne.otp_android_client_v3.model.OTPStopsModel.Stop;
import com.example.anne.otp_android_client_v3.model.OTPService;

/**
 * Created by Anne on 7/4/2017.
 */

class GetTransitStopsService {

    private GetTransitStopsService() {}

    static void requestTransitStopsWithinRadius(final MainActivity activity,
                                                LatLng center, double radius) {

        Call<ArrayList<Stop>> call = OTPService.getOtpService().getStopsByRadius(
                OTPService.ROUTER_ID,
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
