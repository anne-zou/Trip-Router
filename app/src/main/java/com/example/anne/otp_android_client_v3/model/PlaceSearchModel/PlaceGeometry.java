package com.example.anne.otp_android_client_v3.model.PlaceSearchModel;

import com.example.anne.otp_android_client_v3.model.OTPPlanModel.Coordinate;

/**
 * Created by Anne on 6/14/2017.
 */

public class PlaceGeometry {

    private Coordinate location;

    private Viewport viewport;

    public Coordinate getLocation() {
        return location;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
