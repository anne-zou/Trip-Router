package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.view.View;

/**
 * Click listener for a search suggestion in the SearchViewFragment launched when a search field
 * in an EditScheduledTripFragment was clicked
 */
public class UpdateScheduledTripSearchSuggestionOnClickListener implements View.OnClickListener {

    // Reference to the main activity
    private MainActivity activity;

    // Place that was selected in the search view fragment
    private TripPlanPlace tripPlanPlace;

    /**
     * Constructor
     * @param activity reference to the main activity
     * @param selectedPlace place that was selected in the search view fragment
     */
    public UpdateScheduledTripSearchSuggestionOnClickListener(MainActivity activity,
                                                   TripPlanPlace selectedPlace) {
        super();
        this.activity = activity;
        this.tripPlanPlace = selectedPlace;
    }

    /**
     * Update the EditScheduledTripFragment.
     * Invoked if a search suggestion in the SearchViewFragment is clicked and the
     * SearchViewFragment was launched from a EditScheduledTripFragment.
     * @param v the search suggestion view that was clicked
     */
    @Override
    public void onClick(View v) {

        // Get the last clicked search field in the activity from which caused the launch of
        // the SearchViewFragment in order to select a place
        // (should be a search field in the EditScheduleTripFragment)
        SearchField searchField = activity.getLastEditedSearchField();

        // Update the EditScheduledTripFragment depending on which search field was clicked
        if (searchField.isWhichSearchField() == SearchField.ORIGIN)
            activity.getEditScheduledTripFragment().setOrigin(tripPlanPlace);
        else if (searchField.isWhichSearchField() == SearchField.DESTINATION)
            activity.getEditScheduledTripFragment().setDestination(tripPlanPlace);

        // Close the SearchViewFragment
        activity.closeSearchViewFragment();
    }
}
