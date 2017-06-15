package google_places_place_search.model;

import com.google.android.gms.maps.model.LatLng;

import vanderbilt.thub.otp.model.OTPPlanModel.Coordinate;

/**
 * Created by Anne on 6/14/2017.
 */

public class Viewport {

    private Coordinate northeast;

    private Coordinate southwest;

    public Coordinate getNortheast() {
        return northeast;
    }

    public void setNortheast(Coordinate northeast) {
        this.northeast = northeast;
    }

    public Coordinate getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Coordinate southwest) {
        this.southwest = southwest;
    }

    public boolean contains(LatLng latLng) {
        return latLng.latitude <= northeast.getLat()
                && latLng.latitude >= southwest.getLat()
                && latLng.longitude <= northeast.getLng()
                && latLng.longitude >= southwest.getLng();
    }

}
