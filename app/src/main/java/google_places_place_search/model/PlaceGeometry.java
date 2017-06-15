package google_places_place_search.model;

import com.google.android.gms.maps.model.LatLng;

import vanderbilt.thub.otp.model.OTPPlanModel.Coordinate;

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
