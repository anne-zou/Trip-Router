package edu.vanderbilt.isis.trip_planner_android_client.model.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import edu.vanderbilt.isis.trip_planner_android_client.R;

/**
 * Created by Anne on 7/10/2017.
 */

public class SearchHistoryCursorAdapter extends CursorAdapter {

    /**
     * Construct a new {@link SearchHistoryCursorAdapter}
     * @param context the context
     * @param cursor the cursor from which to get the data
     */
    public SearchHistoryCursorAdapter(Context context, Cursor cursor) {
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

        // Read the attributes from the cursor for the current history entry
        String placeName = cursor.getString(destinationNameIndex);
        String placeAddress = cursor.getString(destinationAddressIndex);

        // Update the TextViews with the attribute for the current history entry
        nameTextView.setText(placeName);
        addressTextView.setText(placeAddress);

    }
}
