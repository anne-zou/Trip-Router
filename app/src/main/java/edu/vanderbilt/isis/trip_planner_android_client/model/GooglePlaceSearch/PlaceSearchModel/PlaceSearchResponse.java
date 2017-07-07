package edu.vanderbilt.isis.trip_planner_android_client.model.GooglePlaceSearch.PlaceSearchModel;

import java.util.List;

/**
 * Created by Anne on 6/14/2017.
 */

public class PlaceSearchResponse {

    List<PlaceResult> results;

    String status;

    PlaceSearchResponse(){}

    public List<PlaceResult> getResults() {
        return results;
    }

    public void setResults(List<PlaceResult> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
