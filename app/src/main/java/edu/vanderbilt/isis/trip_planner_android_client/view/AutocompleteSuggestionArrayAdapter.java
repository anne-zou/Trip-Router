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

import java.lang.reflect.Array;
import java.util.List;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;

/**
 * Created by Anne on 7/12/2017.
 */

public class AutocompleteSuggestionArrayAdapter extends ArrayAdapter<List<String>> {

    public static final String TAG = AutocompleteSuggestionArrayAdapter.class.getName();

    /**
     * Reference the the MainActivity
     */
    private MainActivity activity;

    public AutocompleteSuggestionArrayAdapter(MainActivity activity, int resource) {
        super(activity, resource);
        this.activity = activity;
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position the position of the item within the adapter's data set of the item whose
     *                 view we want
     * @param convertView the old view to reuse, if possible
     * @param parent  the parent that this view will eventually be attached to
     * @return
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // If there is no old view to be recycled, inflate a new list item view
        if (convertView == null)
            convertView = LayoutInflater.from(activity)
                    .inflate(R.layout.search_list_item, parent, false);

        // Find the views we want to modify in the list item view
        TextView nameTextView = (TextView)
                convertView.findViewById(R.id.history_list_item_name);
        TextView addressTextView = (TextView)
                convertView.findViewById(R.id.history_list_item_address);

        if (nameTextView == null || addressTextView == null) { // error in structure of recycled view
            Log.e(TAG, "List item view does not contain a name TextView and an address TextView.");
            convertView.setVisibility(View.GONE);
            return convertView;
        }

        // Get the attributes of the predicted place data item
        List<String> attributes = getItem(position);

        if (attributes == null) { // the data item does not exist
            Log.e(TAG, "String list autocomplete suggestion data item at index " + position +
                    " does not exist.");
            convertView.setVisibility(View.GONE);
            return convertView;
        }
        if (attributes.size() < 3) { // error in formatting of data item
            Log.e(TAG, "String list autocomplete suggestion data item does not contain a name " +
                    "String, an address String, and a placeId String.");
            convertView.setVisibility(View.GONE);
            return convertView;
        }

        //  Get the individual attributes of the predicted place data item
        String name = attributes.get(0);
        String address = attributes.get(1);
        final String placeId = attributes.get(2);

        // Update the TextViews with the attributes of the predicted place data item
        nameTextView.setText(name);
        addressTextView.setText(address);

        // Set the view's on click listener to get the location of the place and plan a trip
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controller.requestPlaceById(activity, placeId);
            }
        });


        return convertView;

    }


}
