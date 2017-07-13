package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;


import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;
import edu.vanderbilt.isis.trip_planner_android_client.controller.ParameterRunnable;


/**
 * ArrayAdapter customized to display Google Places Autocomplete search suggestions in the
 * search suggestions ListView in the SearchViewFragment.
 *
 * Use add() or addAll() to update the contents of the adapter.
 * Each data item added must a be String array of length 3, holding the name, the address, and
 * the placeId of a Google Places place, in that order.
 *
 * The data item does NOT include the latitude and longitude of the place because this info is not
 * provided in the results returned by the GooglePlacesAutocompletePredictions framework.
 * A separate request has to be made through the Google Places GeoData API to get the Google Place
 * object by its placeId to get its latitude and longitude. Rather than doing this for every search
 * suggestion we display, this request is only made for a search suggestion when clicked by the user.
 *
 * TODO: REPLACE THIS ADAPTER WITH A CURSOR ADAPTER WHEN WE HAVE OUR OWN DATABASE FROM WHICH TO
 * QUERY FOR PLACE AUTOCOMPLETE SUGGESTIONS (MAKE ONE SIMILAR TO SearchHistoryCursorAdapter)
 */
public class AutocompleteSuggestionArrayAdapter extends ArrayAdapter<String[]> {

    public static final String TAG = AutocompleteSuggestionArrayAdapter.class.getName();

    /**
     * Reference to the the MainActivity
     */
    private MainActivity activity;

    /**
     * Constructor to acquire a reference to the MainActivity
     * @param activity the main activity
     * @param resource (not used)
     */
    public AutocompleteSuggestionArrayAdapter(MainActivity activity, int resource) {
        super(activity, resource);
        this.activity = activity;
    }

    /**
     * Get a View that displays the data for the item at the specified position in the data list.
     * Called automatically by the ListView framework to update itself if this ArrayAdapter has
     * been set as the adapter for the ListView.
     * @param position the position of the data item within the adapter's data list
     * @param convertView the old view to reuse, if possible
     * @param parent  the parent that this view will eventually be attached to
     * @return the updated list-item view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // If there is no old view to be recycled, inflate a new list item view
        if (convertView == null)
            convertView = LayoutInflater.from(activity)
                    .inflate(R.layout.search_list_item, parent, false);

        // Get the TextViews in the list item layout we want to update
        TextView nameTextView = (TextView)
                convertView.findViewById(R.id.history_list_item_name);
        TextView addressTextView = (TextView)
                convertView.findViewById(R.id.history_list_item_address);

        if (nameTextView == null || addressTextView == null) { // error in structure of view
            Log.e(TAG, "List item view does not contain a name TextView and an address TextView.");
            convertView.setVisibility(View.GONE);
            return convertView;
        }

        // Get the data item at the specified position; it specifies the attributes of the
        // corresponding Place search suggestion
        String[] attributes = getItem(position);

        if (attributes == null) { // the data item does not exist
            Log.e(TAG, "String list autocomplete suggestion data item at index " + position +
                    " does not exist.");
            convertView.setVisibility(View.GONE);
            return convertView;
        }
        // the data item should be a String array of length 3
        if (attributes.length != 3) { // error in formatting of data item
            Log.e(TAG, "String list autocomplete suggestion data item does not contain a name " +
                    "String, an address String, and a placeId String.");
            convertView.setVisibility(View.GONE);
            return convertView;
        }

        //  Get the individual attributes of the place
        final String name = attributes[0];
        final String address = attributes[1];
        final String placeId = attributes[2];

        // Update the TextViews in the list-item view with the attributes of the place
        nameTextView.setText(name);
        addressTextView.setText(address);

        // Set the view's on click listener: request the Place object of the selected place by
        // its id, and plan a trip
        convertView.setOnClickListener(new View.OnClickListener() {
            /**
             * Respond to when the user selects an autocomplete search suggestion
             * @param v the selected view
             */
            @Override
            public void onClick(final View v) {

                // Interrupt any ongoing trip plan request, since we are about to make a new one
                Controller.interruptOngoingTripPlanRequests();

                // Close the search view fragment, go to the trip plan screen, & display a loading
                // message so we don't get stuck on the search screen while waiting to get the
                // selected Place by id
                // TODO close the search view fragment
                activity.goToNextScreen(MainActivity.ActivityState.TRIP_PLAN);
                activity.showOnSlidingPanelHead(MainActivity.LOADING_MESSAGE);

                // Request the Place by id
                Controller.requestPlaceById(placeId,
                        new ParameterRunnable<Place>() {
                            /**
                             * Make a trip plan request upon receipt of the Place object
                             */
                            @Override
                            public void run() {

                                // Get the Google Place object (hooray! we finally can get its LatLng)
                                Place place = getParameterObject();

                                // Create the TripPlanPlace
                                TripPlanPlace tripPlanPlace = new TripPlanPlace(
                                        place.getName(), place.getLatLng(), place.getAddress());

                                // Process the click of the search suggestion
                                new SearchSuggestionOnClickListener(activity, tripPlanPlace)
                                        .onClick(v);

                            }
                        },
                        new Runnable() {
                            /**
                             * Display failure message on sliding panel head upon notification of
                             * request failure
                             */
                            @Override
                            public void run() {
                                activity.showOnSlidingPanelHead(MainActivity.TRIP_PLAN_FAILURE_MESSAGE);
                            }
                        });
            }
        });

        return convertView;

    }

}
