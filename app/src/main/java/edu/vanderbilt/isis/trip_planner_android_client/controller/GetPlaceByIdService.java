package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.support.annotation.NonNull;

import edu.vanderbilt.isis.trip_planner_android_client.view.MainActivity;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

/**
 * Created by Anne on 7/4/2017.
 */

public class GetPlaceByIdService {

    private GetPlaceByIdService() {} // private constructor to prevent instantiation

    /**
     * Request a Google Place object by its placeId. Updates the UI upon request response and upon
     * request failure via the given Runnable arguments.
     * @param placeId the id of the Place
     * @param responseRunnable the ParameterRunnable to execute upon result; passes the retrieved
     *                       Place object as the parameter
     * @param failureRunnable the Runnable to execute upon request failure
     */
    static void requestPlaceById(final String placeId,
                                 final ParameterRunnable<Place> responseRunnable,
                                 final Runnable failureRunnable) {
        if (Controller.getGoogleApiClient() != null)
            Places.GeoDataApi.getPlaceById(Controller.getGoogleApiClient(), placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {

                    /**
                     * Automatically invoked upon reception of result from the Google Places
                     * GeoDataApi server
                     * @param places buffer containing the Place object that corresponds to
                     *               the submitted placeId
                     */
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        // Update the UI via callbacks to methods defined in the view layer
                        if (places.getStatus().isSuccess() && places.getCount() > 0
                                && places.get(0) != null) {
                            // Successfully retrieved Place object
                            responseRunnable.run(places.get(0));
                        } else { // Did not successfully retrieve Place object
                            failureRunnable.run();
                        }
                        places.release(); // release buffer to prevent memory leak
                    }
                });
    }

}
