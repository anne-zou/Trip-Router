package edu.vanderbilt.isis.trip_planner_android_client.view;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 6/20/2017.
 */

/**
 * Class that encapsulates the information needed about a place for it to be used as the origin,
 * destination, or an intermediate stop of a trip plan.
 */
public class TripPlanPlace {

    public static final String DEFAULT_TRIP_PLAN_PLACE_NAME = "My Location";

    private boolean useCurrentLocation;

    private String name;

    private LatLng location;

    private String address;

    /**
     * Default constructor.
     * Use the default name and initialize location and address to null.
     * Since a location is not specified, set the flag that says: when about to plan a trip
     * using this TripPlanPlace, get and use the device's current location for this place.
     */
    TripPlanPlace() {
        this.name = DEFAULT_TRIP_PLAN_PLACE_NAME;
        this.location = null;
        this.address = null;
        useCurrentLocation = true;
    }

    /**
     * Constructor specifying the name and location of this place.
     * Initialize address to null.
     * @param name name
     * @param location location
     */
    TripPlanPlace(String name, LatLng location) {
        this.name = name;
        this.location = location;
        this.address = null;
        useCurrentLocation = false;
    }

    /**
     * Constructor specifying the name, location, and address of this place
     * @param name name
     * @param location location
     * @param address address
     */
    TripPlanPlace(CharSequence name, LatLng location, CharSequence address) {
        this.name = name.toString();
        this.location = location;
        this.address = address.toString();
        useCurrentLocation = false;
    }

    /**
     * Set the name of this place
     * @param name new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the location of the place
     * Will no longer indicate that the trip plan should "use current location" for this place
     * @param location new location
     */
    public void setLocation(LatLng location) {
        // Initialize name if null
        if (name == null)
            name = DEFAULT_TRIP_PLAN_PLACE_NAME;

        this.location = location;
        useCurrentLocation = false;
    }

    /**
     * Set the address of this place
     * @param address new address
     */
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

    // Getters for class fields

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
