package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.view.View;

import java.util.ArrayList;

import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;

/**
 * OnClickListener implementation for a search suggestion item in the search view fragment
 */
public class SearchSuggestionOnClickListener implements View.OnClickListener {

    // Reference to the main activity
    private MainActivity activity;

    // Place that was selected in the search view fragment
    private TripPlanPlace tripPlanPlace;

    /**
     * Constructor
     * @param activity reference to the main activity
     * @param selectedPlace place that was selected in the search view fragment
     */
    public SearchSuggestionOnClickListener(MainActivity activity, TripPlanPlace selectedPlace) {
        super();
        this.activity = activity;
        this.tripPlanPlace = selectedPlace;
    }

    @Override
    public void onClick(View v) {

        // Interrupt any ongoing trip plan request, since we are about to make a new one
        Controller.interruptOngoingTripPlanRequests();

        // Get the last edited search field in the activity
        MainActivity.SearchFieldId id = activity.getLastEditedSearchField();

        // Set the selected place as the origin or destination for the trip
        // plan depending on the last edited search field
        if (id == MainActivity.SearchFieldId.DETAILED_FROM)
            activity.setmOrigin(tripPlanPlace); // set the text in the from search field
        else
            activity.setmDestination(tripPlanPlace); // set the text in the to search field

        // Set origin or destination to default TripPlanPlace if null
        if (activity.getmOrigin() == null)
            activity.setmOrigin(new TripPlanPlace());
        if (activity.getmDestination() == null)
            activity.setmDestination(new TripPlanPlace());

        // If in state HOME_STOP_SELECTED:
        ArrayList<TripPlanPlace> intermediateStops = null; // store intermediate stop
        if (activity.getState() == MainActivity.ActivityState.HOME_STOP_SELECTED) {

            // Add the selected transit stop as an intermediate stop
            intermediateStops = new ArrayList<>();
            intermediateStops.add(0,
                    new TripPlanPlace(activity.getmPlaceSelectedMarker().getTitle(),
                            activity.getmPlaceSelectedMarker().getPosition()));
        }

        // Request the trip plan
        activity.planTrip(activity.getmOrigin(), activity.getmDestination(),
                intermediateStops);


        // Close the search view fragment
        activity.closeSearchViewFragment();
    }
}
