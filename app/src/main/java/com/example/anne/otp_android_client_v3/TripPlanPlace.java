package com.example.anne.otp_android_client_v3;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 6/20/2017.
 */

public class TripPlanPlace {

    private boolean useCurrentLocation = true;

    private String name;

    private LatLng location;

    private String address;

    TripPlanPlace() {
        this.name = "My Location";
        this.location = null;
    }

    TripPlanPlace(String name, LatLng location) {
        this.name = name;
        this.location = location;
        useCurrentLocation = false;
    }

    TripPlanPlace(CharSequence name, LatLng location) {
        this.name = name.toString();
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
            this.name = "My Location";
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

    double getLatitude() {
        return location.latitude;
    }

    double getLongitude() {
        return location.longitude;
    }

    boolean isCurrentLocation() { return useCurrentLocation; }
}
