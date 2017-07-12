package edu.vanderbilt.isis.trip_planner_android_client.view;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 6/20/2017.
 */

public class TripPlanPlace {

    public static final String DEFAULT_TRIP_PLAN_PLACE_NAME = "My Location";

    private boolean useCurrentLocation = true;

    private String name;

    private LatLng location;

    private String address;

    TripPlanPlace() {
        this.name = DEFAULT_TRIP_PLAN_PLACE_NAME;
        this.location = null;
    }

    TripPlanPlace(String name, LatLng location) {
        this.name = name;
        this.location = location;
        useCurrentLocation = false;
    }

    TripPlanPlace(CharSequence name, LatLng location, CharSequence address) {
        this.name = name.toString();
        this.location = location;
        this.address = address.toString();
        useCurrentLocation = false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUseCurrentLocation(boolean useCurrentLocation) {
        this.useCurrentLocation = useCurrentLocation;
        if (useCurrentLocation) {
            this.name = DEFAULT_TRIP_PLAN_PLACE_NAME;
            this.location = null;
        }
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return location.latitude;
    }

    public double getLongitude() {
        return location.longitude;
    }

    public boolean shouldUseCurrentLocation() { return useCurrentLocation; }
}
