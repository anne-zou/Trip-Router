package edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 5/26/2017.
 */

public class Coordinate {

    private Double lng;
    private Double lat;


    public Coordinate(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }


    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public LatLng buildLatLng() {
        return new LatLng(lat, lng);
    }
}
