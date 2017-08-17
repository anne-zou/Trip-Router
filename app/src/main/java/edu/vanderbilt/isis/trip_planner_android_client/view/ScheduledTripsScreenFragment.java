package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;

/**
 * Fragment comprising the screen that displays a list of CardViews, each representing a scheduled
 * trip in the calendar.
 */
public class ScheduledTripsScreenFragment extends Fragment {

    private ScheduledTripsCursorAdapter mCursorAdapter;

    /**
     * Called when the View for the Fragment is created. Inflate the view and initialize the
     * fragment.
     * @param inflater the inflater to inflate the view with
     * @param container the parent to inflate the view into
     * @param savedInstanceState nah
     * @return the inflated view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        RelativeLayout rl = (RelativeLayout) inflater
                .inflate(R.layout.scheduled_trips_layout, container, false);

        // Get the ListView to load the scheduled trips in
        ListView scheduledTripsList = (ListView) rl.findViewById(R.id.scheduled_trips_list);

        // Initialize the cursor adapter to fill the ListView of scheduled trips
        mCursorAdapter = new ScheduledTripsCursorAdapter((MainActivity) getActivity(), null);

        // Set the adapter of the scheduled trips ListView
        scheduledTripsList.setAdapter(mCursorAdapter);

        // Initialize the scheduled trips cursor loader
        getLoaderManager().initLoader(CursorLoaderID.SCHEDULED_TRIPS_LOADER, null,
                new ScheduledTripsCursorLoaderCallbacks());

        // Set the on click listener for the back button
        View backButton = rl.findViewById(R.id.scheduled_trips_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the scheduled trips screen
                ((MainActivity) getActivity()).removeScheduledTripsScreenFragment();
            }
        });

        // Set the on click listener for the add new trip schedule button
        View addButton = rl.findViewById(R.id.scheduled_trips_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the add/edit trip schedule screen
                Bundle bundle = new Bundle();
                bundle.putBoolean(EditScheduledTripFragment.IS_EXISTING_SCHEDULE, false);
                ((MainActivity) getActivity()).launchEditScheduledTripFragment(bundle);
            }
        });

        return rl;
    }

    /**
     * Called when the View for the Fragment is destroyed.
     * Destroy the scheduled trips cursor loader.
     */
    @Override
    public void onDestroy() {
        getLoaderManager().destroyLoader(CursorLoaderID.SCHEDULED_TRIPS_LOADER);
        super.onDestroy();
    }


    private class ScheduledTripsCursorLoaderCallbacks
            implements LoaderManager.LoaderCallbacks<Cursor> {

        /**
         * Creates the cursor loader to load search suggestions based on the app's search history.
         * Specifies the parameters for the CursorLoader's queries.
         * Invoked after initLoader() is called.
         * @param id defined id for the loader
         * @param args args
         * @return the new CursorLoader
         */
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            // Set up parameters to create the CursorLoader:

            // Sort schedules by time of next trip, where the schedule with the soonest next trip
            // comes first
            String sortOrder = TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_NEXT_TRIP
                    + " ASC";

            // Create a new CursorLoader that loads the table data in the specified sort order
            return new CursorLoader(getActivity(), TripPlannerContract.ScheduleTable.CONTENT_URI,
                    null, null, null, sortOrder);
        }

        /**
         * Called when a previously created loader is being reset, thus
         * making its data unavailable. The application should at this point
         * remove any references it has to the Loader's data.
         * @param loader the cursor loader being reset
         */
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
        }

        /**
         * Called when a previously created cursor loader has finished loading new data.
         * @param loader the cursor loader that loaded the data
         * @param data the cursor containing the new data
         */
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mCursorAdapter.swapCursor(data);
        }
    }


}
