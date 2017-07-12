package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;

/**
 * Created by Anne on 7/10/2017.
 */

public class SearchHistoryCursorAdapter extends CursorAdapter {

    /**
     * Reference to the MainActivity
     */
    private MainActivity activity;

    /**
     * Construct a new {@link SearchHistoryCursorAdapter}
     * @param cursor the cursor from which to get the data
     * @param activity the activity from which to plan a trip and close the search fragment
     */
    public SearchHistoryCursorAdapter(MainActivity activity, Cursor cursor) {
        this(activity.getApplicationContext(), cursor);
        this.activity = activity;
    }

    /**
     * Construct a new {@link SearchHistoryCursorAdapter}
     * @param context the context
     * @param cursor the cursor from which to get the data
     */
    private SearchHistoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    /**
     * Inflate a new view and return it
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already
     *               moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @return the newly create list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in search_list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.search_list_item, parent, false);
    }

    /**
     * This method binds data to the given list item layout
     * @param view existing list item view, returned earlier by newView()
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already
     *               moved to the correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find the views we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.history_list_item_name);
        TextView addressTextView = (TextView) view.findViewById(R.id.history_list_item_address);

        // Find the columns of attributes that we're interested in
        int destinationNameIndex = cursor
                .getColumnIndex(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME);
        int destinationAddressIndex = cursor
                .getColumnIndex(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_ADDRESS);
        int destinationLocationIndex = cursor
                .getColumnIndex(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES);

        // Read the attributes from the cursor for the current history entry
        final String placeName = cursor.getString(destinationNameIndex);
        final String placeAddress = cursor.getString(destinationAddressIndex);
        String placeLocation = cursor.getString(destinationLocationIndex);

        // Update the TextViews with the attributes of the current search history entry
        nameTextView.setText(placeName);
        addressTextView.setText(placeAddress);

        // Parse the location string
        String[] latLngStrArr = placeLocation.split(",");

        if (latLngStrArr.length != 2) { // error in formatting of location data field
            view.setVisibility(View.GONE);
            return;
        }

        final double lat = Double.parseDouble(latLngStrArr[0]);
        final double lon = Double.parseDouble(latLngStrArr[1]);

        // Set the on-click listener for the list item
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TripPlanPlace selectedPlace = new TripPlanPlace(placeName,
                        new LatLng(lat, lon), placeAddress);

                // Set the origin or destination for the trip plan depending on the search field
                // that the search view fragment was launched from
                MainActivity.SearchFieldId id = activity.getLastEditedSearchField();
                if (id == MainActivity.SearchFieldId.DETAILED_FROM)
                    activity.setmOrigin(selectedPlace);
                else
                    activity.setmDestination(selectedPlace);

                // Plan the trip
                activity.planTrip(activity.getmOrigin(), activity.getmDestination());

                // TODO close the search view fragment

            }
        });
    }
}
