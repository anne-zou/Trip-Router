package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;
import edu.vanderbilt.isis.trip_planner_android_client.controller.ParameterRunnable;
import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;

/**
 * Created by Anne on 7/11/2017.
 */

public class SearchViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // Define code for the CursorLoader
    private static final int SEARCH_HISTORY_LOADER = 1;

    // CursorAdapter to supply the search suggestions list with search history items
    private SearchHistoryCursorAdapter mSearchHistoryAdapter;

    // ArrayAdapter to supply the search suggestions list with Place Autocomplete items
    private AutocompleteSuggestionArrayAdapter mAutocompleteSuggestionAdapter;

    // Search suggestions ListView
    private ListView mSearchSuggestionsList;

    // Header to the search suggestions list
    private TextView mSearchSuggestionsHeader;

    // Search field EditText
    private EditText mSearchField;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        RelativeLayout rl = (RelativeLayout) inflater
                .inflate(R.layout.search_view_layout, container, false);

        // Get the search field EditText
        mSearchField = (EditText) rl.findViewById(R.id.custom_search_bar_edit_text);

        // Set the listener for the EditText
        mSearchField.addTextChangedListener(new SearchFieldTextWatcher());

        // Get the search suggestions ListView
        mSearchSuggestionsList = (ListView) rl.findViewById(R.id.search_suggestions_list);

        // Disable scrolling in the ListView
        // We will need to limit the number of search results we get at a time to
        // prevent poor performance
        mSearchSuggestionsList.setOnTouchListener(new View.OnTouchListener() {
            /**
             * Swallow the touch event if it is ACTION_MOVE
             * The ListView will react to clicks, but will not change scroll position
             * @param v the view that was touched
             * @param event the motion event
             * @return true if the touch event has been handled
             */
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_MOVE)
                    return true;
                return false;
            }
        });

        // Get the search suggestions header TextView
        mSearchSuggestionsHeader = (TextView) rl.findViewById(R.id.search_suggestions_header);

        // Show "Previous searches" on the search suggestions header
        mSearchSuggestionsHeader.setText(getResources().getText(R.string.previous_searches));
        mSearchSuggestionsHeader.setVisibility(View.VISIBLE);


        // Create the adapters we will use to display search suggestion views in the ListView
        mSearchHistoryAdapter = new SearchHistoryCursorAdapter((MainActivity) getActivity(), null);
        mAutocompleteSuggestionAdapter = new AutocompleteSuggestionArrayAdapter(
                (MainActivity) getActivity(), 0);

        // Start the ListView off with the search history adapter
        mSearchSuggestionsList.setAdapter(mSearchHistoryAdapter);

        // Initialize the cursor loader
        getLoaderManager().initLoader(SEARCH_HISTORY_LOADER, null, this);

        return rl;
    }


    /**
     * Creates the cursor loader to load search suggestions based on the app's search history.
     * Invoked after initLoader() is called.
     * @param loaderId defined id for the loader
     * @param args args
     * @return the new CursorLoader
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        // Set up parameters to create the CursorLoader:

        // Get the "to_name" and "to_address" columns when loading a cursor
        String[] projection = {
                TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME,
                TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_ADDRESS
        };

        // Exclude entries with the default trip plan place name when loading a cursor
        String selection = TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME + "!=?";
        String[] selectionArgs = {TripPlanPlace.DEFAULT_TRIP_PLAN_PLACE_NAME};

        // Sort by _id in descending order when loading a cursor
        String sortOrder = TripPlannerContract.SearchHistoryTable._ID + " DESC";
        // IMPORTANT: To prevent poor performance, need to either limit the number of results in
        // the Cursor or restructure search_view_layout.xml to not have a ListView nested inside a
        // ScrollView

        /**
         * SELECT DISTINCT to_name, to_address
         * FROM search_history
         * WHERE to_name != My Location
         * ORDER BY _id DESC
         * LIMIT 10
         */

        // Create & return a new CursorLoader that loads the specified selection
        return new CursorLoader(
                getActivity(),
                TripPlannerContract.SearchHistoryTable.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor into the search history cursor adapter
        mSearchHistoryAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Clear the search history cursor adapter's reference to the Cursor to prevent memory leaks
        mSearchHistoryAdapter.swapCursor(null);
    }

    /**
     * TextWatcher implementation to respond to changes in the contents of the EditText
     */
    private class SearchFieldTextWatcher implements TextWatcher {

        private String mLastQuery = "";

        /**
         * Record the contents of the EditText
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            mLastQuery = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        /**
         * Update search suggestions based on the new contents of the EditText
         * @param s the Editable in the EditText
         */
        @Override
        public void afterTextChanged(Editable s) {

            // TODO: IMPOSE TIME THRESHOLD

            // Get the contents of the EditText
            String query = s.toString();

            if (query.isEmpty() && !mLastQuery.isEmpty()) {

                // If the EditText has been newly cleared, stop showing Place Autocomplete
                // suggestions & start showing search history suggestions

                // Clear the search suggestions ListView
                mSearchSuggestionsList.removeAllViews();

                // Reinitialize the search history cursor loader
                getLoaderManager().initLoader(SEARCH_HISTORY_LOADER, null, SearchViewFragment.this);

                // Set the search history adapter as the adapter for the search suggestions ListView
                mSearchSuggestionsList.setAdapter(mSearchHistoryAdapter);

                // Show "Previous searches" on the search suggestions header
                mSearchSuggestionsHeader.setText(getResources().getText(R.string.previous_searches));
                mSearchSuggestionsHeader.setVisibility(View.VISIBLE);

            } else if (!query.isEmpty() && mLastQuery.isEmpty()) {

                // If the EditText is newly no longer empty, stop showing search history
                // suggestions & prepare to show Place Autocomplete suggestions

                // Destroy the search history cursor loader
                getLoaderManager().destroyLoader(SEARCH_HISTORY_LOADER);

                // Remove the search history adapter from the search suggestions ListView
                mSearchSuggestionsList.setAdapter(null);

                // Clear the search suggestions ListView
                mSearchSuggestionsList.removeAllViews();

                // Show "Loading results" on the search suggestions header
                mSearchSuggestionsHeader.setText(getResources().getText(R.string.loading_results));
                mSearchSuggestionsHeader.setVisibility(View.VISIBLE);

            }

            // If the EditText is not empty, request the autocomplete predictions
            if (!query.isEmpty()) {

                // Show "Loading results" on the search suggestions header
                mSearchSuggestionsHeader.setText(getResources().getText(R.string.loading_results));
                mSearchSuggestionsHeader.setVisibility(View.VISIBLE);

                // Get the AutocompletePredictions
                Controller.getGooglePlacesAutocompletePredictions(getActivity(), query,
                        new ParameterRunnable<AutocompletePredictionBuffer>() {
                            /**
                             * Process the results of the AutocompletePredictions
                             */
                            @Override
                            public void run() {
                                // Get the buffer of autocomplete predictions
                                AutocompletePredictionBuffer buffer = getParameterObject();

                                // For each prediction in the buffer, construct a String array to
                                // hold its name, address, and placeId, and add it to the
                                // AutocompleteSuggestionArrayAdapter
                                String[] predictionData = new String[3];
                                for (AutocompletePrediction prediction : buffer) {
                                    // Place name
                                    predictionData[0] = prediction.getPrimaryText(null).toString();
                                    // Place address
                                    predictionData[1] = prediction.getSecondaryText(null).toString();
                                    // Place id
                                    predictionData[2] = prediction.getPlaceId();
                                    // Insert into autocomplete suggestions adapter
                                    mAutocompleteSuggestionAdapter.add(predictionData);
                                }

                                // Release the buffer
                                buffer.release();

                                // Hide the header
                                mSearchSuggestionsHeader.setVisibility(View.GONE);
                            }
                        });


            }

        }
    }
}
