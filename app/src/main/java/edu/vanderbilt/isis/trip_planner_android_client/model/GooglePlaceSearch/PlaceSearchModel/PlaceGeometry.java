package edu.vanderbilt.isis.trip_planner_android_client.model.GooglePlaceSearch.PlaceSearchModel;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Coordinate;

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
