package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.view.View;

import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;

/**
 * OnClickListener for a search suggestion item View in the SearchViewFragment
 */
public class PlanTripSearchSuggestionOnClickListener implements View.OnClickListener {

    // Reference to the main activity
    private MainActivity activity;

    // Place that was selected in the search view fragment
    private TripPlanPlace tripPlanPlace;

    /**
     * Constructor
     * @param activity reference to the main activity
     * @param selectedPlace place that was selected in the search view fragment
     */
    public PlanTripSearchSuggestionOnClickListener(MainActivity activity,
                                                   TripPlanPlace selectedPlace) {
        super();
        this.activity = activity;
        this.tripPlanPlace = selectedPlace;
    }

    @Override
    public void onClick(View v) {

        // Interrupt any ongoing trip plan request, since we are about to make a new one
        Controller.interruptOngoingTripPlanRequests();

        // Get the last edited search field in the activity
        SearchField searchField = activity.getLastEditedSearchField();

        // Close the search view fragment
        activity.closeSearchViewFragment();

        // Set the selected place as the origin or destination or an intermediate top for the trip
        // plan depending on the last edited search field
        if (searchField.isWhichSearchField() == SearchField.ORIGIN)
            activity.setmOrigin(tripPlanPlace);
        else if (searchField.isWhichSearchField() == SearchField.DESTINATION)
            activity.setmDestination(tripPlanPlace);
        else
            activity.getmIntermediateStops().add(searchField.isWhichSearchField(), tripPlanPlace);

        // Set origin or destination to default TripPlanPlace if null
        if (activity.getmOrigin() == null)
            activity.setmOrigin(new TripPlanPlace());
        if (activity.getmDestination() == null)
            activity.setmDestination(new TripPlanPlace());

        // Signal that we need to close the sliding drawer after displaying the itinerary
        activity.needToCloseSlidingDrawerAfterDisplayItinerary = true;

        // Request the trip plan
        activity.planTrip(activity.getmOrigin(), activity.getmDestination(),
                activity.getmIntermediateStops().isEmpty() ? null : activity.getmIntermediateStops());

    }
}
