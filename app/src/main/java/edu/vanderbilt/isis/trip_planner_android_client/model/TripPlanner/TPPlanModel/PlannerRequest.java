package edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel;

import java.util.List;

/**
 * Created by chinmaya on 5/16/2017.
 */
public class PlannerRequest {

    private GenericLocation from;
    private GenericLocation to;
    private List<GenericLocation> intermediatePlaces;
    private String modes;

    public PlannerRequest() {}

    public GenericLocation getFrom() {
        return from;
    }

    public void setFrom(GenericLocation from) {
        this.from = from;
    }

    public GenericLocation getTo() {
        return to;
    }

    public void setTo(GenericLocation to) {
        this.to = to;
    }

    public String getModes() {
        return modes;
    }

    public void setModes(String modes) {
        this.modes = modes;
    }

    public List<GenericLocation> getIntermediatePlaces() {
        return intermediatePlaces;
    }

    public void setIntermediatePlaces(List<GenericLocation> intermediatePlaces) {
        this.intermediatePlaces = intermediatePlaces;
    }
}
