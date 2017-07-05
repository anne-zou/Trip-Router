package com.example.anne.otp_android_client_v3.controller;

import android.support.annotation.NonNull;

import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by Anne on 7/4/2017.
 */

public class GetPlaceByIdService {

    private GetPlaceByIdService() {}

    static void requestPlaceById(final MainActivity activity, final String placeId) {
        if (Controller.getGoogleApiClient() != null)
            Places.GeoDataApi.getPlaceById(Controller.getGoogleApiClient(), placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0)
                            activity.updateUIonGetPlaceByIdRequestResponse(places.get(0));
                        else
                            activity.updateUIonGetPlaceByIdRequestFailure();
                        places.release(); // release buffer to prevent memory leak
                    }
                });
    }

}
