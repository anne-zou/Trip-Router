package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
 * Search view fragment that pops up whenever user clicks a search field: i.e. SIMPLE,
 * DETAILED_FROM, or DETAILED_TO. Contains a search bar visually similar to the SIMPLE search bar
 * and a CardView containing a ListView to display search suggestions in.
 *
 * A SearchHistoryCursorAdapter and AutocompleteSuggestionArrayAdapter are interchangeably set as
 * the adapter for the ListView (an adapter automatically fills up its ListView with list-item
 * views that it generates based on its own list of data items, which can be updated through a
 * CursorLoader or manually through calls to add() or addAll())
 *
 * When the EditText is empty, the ListView should show suggestions from the app's search history,
 * so the SearchHistoryCursorAdapter will be set as its adapter.
 * When the EditText is not empty, the ListView should show autocomplete suggestions based on the
 * query in the EditText, so then the AutocompleteSuggestionArrayAdapter will be set as its adapter.
 *
 */
public class SearchViewFragment extends Fragment {

    // Define code for the CursorLoader
    private static final int SEARCH_HISTORY_LOADER = 1;
    // TODO: When the platform can support queries for autocomplete suggestions, make a cursor
    // adapter, a cursor loader, and a loader callbacks class for populating the search suggestions
    // ListView with autocomplete suggestions. This would replace the existing
    // AutocompleteSuggestionsArrayAdapter.

    // CursorAdapter to supply the search suggestions list with search history items when needed
    private SearchHistoryCursorAdapter mSearchHistoryAdapter;

    // ArrayAdapter to supply the search suggestions list with autocomplete suggestion items when needed
    private AutocompleteSuggestionArrayAdapter mAutocompleteSuggestionAdapter;

    // The search suggestions ListView
    private ListView mSearchSuggestionsList;

    // Header for the search suggestions list (displays "No results" or "Loading results" when appropriate)
    private TextView mSearchSuggestionsHeader;

    // LoaderCallbacks object for the search history suggestions cursor loader
    private SearchHistoryCursorLoaderCallbacks mSearchHistoryCursorLoaderCallbacksObject;


    /**
     * Inflates the layout for this fragment, gets the views in the layout, initializes
     * the adapters to be used with the search suggestions ListView, sets the listener for the
     * search bar EditText, and simulates clicking the search bar.
     *
     * @param inflater the inflater for the layout
     * @param container the container to inflate the layout into
     * @param savedInstanceState (not used)
     * @return the newly inflated layout
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        RelativeLayout rl = (RelativeLayout) inflater
                .inflate(R.layout.search_view_layout, container, false);

        // Get the search bar EditText
        EditText searchField = (EditText) rl.findViewById(R.id.custom_search_bar_edit_text);

        // Get the back button in the search bar
        ImageView backButton = (ImageView) rl.findViewById(R.id.custom_search_bar_back_button);

        // Get the search suggestions header TextView
        mSearchSuggestionsHeader = (TextView) rl.findViewById(R.id.search_suggestions_header);

        // Get the search suggestions ListView
        mSearchSuggestionsList = (ListView) rl.findViewById(R.id.search_suggestions_list);


        // Create the adapters we will use to display search suggestion views in the ListView
        mSearchHistoryAdapter = new SearchHistoryCursorAdapter((MainActivity) getActivity(), null);
        mAutocompleteSuggestionAdapter = new AutocompleteSuggestionArrayAdapter(
                (MainActivity) getActivity(), 0);

        // Initialize the cursor loader callbacks object for the search history cursor loader
        mSearchHistoryCursorLoaderCallbacksObject =  new SearchHistoryCursorLoaderCallbacks();

        // Initialize the search history cursor loader using the corresponding ID and loader
        // callbacks object
        getLoaderManager().initLoader(SEARCH_HISTORY_LOADER, null,
                mSearchHistoryCursorLoaderCallbacksObject);

        // Start the ListView off with the search history adapter
        mSearchSuggestionsList.setAdapter(mSearchHistoryAdapter);


        // Set the click listener for the layout
        rl.setOnClickListener(new View.OnClickListener() {
            // Press the back button, closing this fragment
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Set the click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            // Press the back button, closing this fragment
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Set the text change listener for the EditText
        searchField.addTextChangedListener(new SearchFieldTextWatcher());

        // Focus on the the EditText
        searchField.requestFocus(); // focus cursor
        InputMethodManager keyboard = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(searchField, InputMethodManager.SHOW_IMPLICIT); // open soft keyboard

        return rl;
    }

    /**
     * Implementation of LoaderCallbacks for the search history CursorLoader.
     * TODO: Define a similar implementation for an autocomplete suggestions CursorLoader, when the
     * platform supports queries for autocomplete suggestions
     *
     * Loads search suggestions based on the app's search history: queries the search_history
     * table of the app database for recent trip plan destinations, and swaps the resulting cursor
     * into the SearchHistoryCursorAdapter.
     *
     */
    private class SearchHistoryCursorLoaderCallbacks
            implements LoaderManager.LoaderCallbacks<Cursor> {

        /**
         * Creates the cursor loader to load search suggestions based on the app's search history.
         * Specifies the parameters for the CursorLoader's queries.
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
                    TripPlannerContract.SearchHistoryTable._ID,
                    TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME,
                    TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_ADDRESS,
                    TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES
            };

            // Exclude entries with the default trip plan place name when loading a cursor
            String selection = TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME + "!=?";
            String[] selectionArgs = {TripPlanPlace.DEFAULT_TRIP_PLAN_PLACE_NAME};

            // Sort by _id in descending order when loading a cursor
            String sortOrder = TripPlannerContract.SearchHistoryTable._ID + " DESC";

            // Create & return a new CursorLoader that loads the specified selection
            return new CursorLoader(
                    getActivity(),
                    TripPlannerContract.SearchHistoryTable.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );

            /*
             * SELECT DISTINCT _id, to_name, to_address, to_coords
             * FROM search_history
             * WHERE to_name != My Location
             * ORDER BY _id DESC
             * LIMIT 10
             */

            // The DISTINCT and LIMIT 10 are hard-coded into the TripPlannerProvider, so all calls
            // to query() through the content provider are distinct and have a result limit of 10.
            // TODO: Find a better, more flexible way to implement this, possibly using customized
            // URIs.

            // TODO: The distinct keyword does NOT prevent duplicate results in the returned cursor
            // because the _id column of each entry in the table is unique, even if the to_name,
            // to_address, and to_coords columns may not be.
            // We need to include the _id column (or the timestamp column) in the selection in order
            // to sort the search history entries in reverse order.
            // Find a way to prevent duplicate results in the resulting cursor (preferably without
            // needing to abandoning the CursorLoader altogether and using a raw SQL query to nest
            // selection statements).

        }

        /**
         * Called when a previously created cursor loader has finished loading new data.
         * @param loader the cursor loader that loaded the data
         * @param data the cursor containing the new data
         */
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            // Swap the new cursor into the search history cursor adapter
            mSearchHistoryAdapter.swapCursor(data);
        }

        /**
         * Called when a previously created loader is being reset, thus
         * making its data unavailable. The application should at this point
         * remove any references it has to the Loader's data.
         * @param loader the cursor loader being reset
         */
        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            // Clear the adapter's reference to the Cursor to prevent memory leaks
            mSearchHistoryAdapter.swapCursor(null);
        }

    }

    /**
     * TextWatcher implementation to respond to changes in the contents of the EditText.
     * Update search suggestions based on the new contents of the EditText.
     *
     * If the query in the EditText is newly empty, stop showing Place Autocomplete
     * suggestions & start showing search history suggestions
     *
     * If the query in the EditText is newly not empty, stop showing search history
     * suggestions & prepare to show Place Autocomplete suggestions
     *
     * If the query is not empty, request the autocomplete predictions and update the
     * Place Autocomplete suggestions adapter
     *
     */
    private class SearchFieldTextWatcher implements TextWatcher {

        private String mLastQuery = "";

        private long mLastChanged = 0;

        /**
         * Record the contents of the EditText
         */
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Record the time as soon as we know the text is going to change
            mLastChanged = System.currentTimeMillis();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        /**
         * Update search suggestions based on the new contents of the EditText
         *
         * If the query in the EditText is newly empty, swap out the autocomplete suggestions
         * adapter for the search history adapter, and reinitialize the search history cursor loader
         *
         * If the query in the EditText is newly not empty, swap out the search history adapter for
         * the autocomplete suggestions adapter, and destroy the search history cursor loader
         *
         * If the query is not empty, request autocomplete predictions for the query and add the
         * results to the autocomplete suggestions adapter, to be shown in the search suggestions
         * ListView
         *
         *
         * @param s the Editable in the EditText
         */
        @Override
        public void afterTextChanged(Editable s) {

            // Record when we began to process this query
            final long timeBeginProcessQuery = System.currentTimeMillis();

            // Get the contents of the EditText
            String query = s.toString();

            if (query.isEmpty() && !mLastQuery.isEmpty()) {

                // The EditText has been newly cleared

                // Reinitialize the search history cursor loader
                getLoaderManager().initLoader(SEARCH_HISTORY_LOADER, null,
                        mSearchHistoryCursorLoaderCallbacksObject);

                // Set the search history adapter as the adapter for the search suggestions ListView
                mSearchSuggestionsList.setAdapter(mSearchHistoryAdapter);

                // Hide the search suggestions header
                mSearchSuggestionsHeader.setVisibility(View.GONE);

            } else if (!query.isEmpty() && mLastQuery.isEmpty()) {

                // The EditText is newly not empty

                // Destroy the search history cursor loader
                getLoaderManager().destroyLoader(SEARCH_HISTORY_LOADER);

                // Remove the search history adapter from the search suggestions ListView
                // (to be swapped with the autocomplete suggestions adapter when the autocomplete
                // prediction results have been received)
                mSearchSuggestionsList.setAdapter(null);

            }

            // Update mLastQuery
            mLastQuery = query;

            // If the query is not empty, show "Loading results" on the search suggestions header
            if (!query.isEmpty()) {
                mSearchSuggestionsHeader.setText(getResources().getText(R.string.loading_results));
                mSearchSuggestionsHeader.setVisibility(View.VISIBLE);
            }

            // If the query is at least size 2 & has not since been changed,
            // request the autocomplete predictions
            if (query.length() >= 2 && mLastChanged < timeBeginProcessQuery) {

                // Get the AutocompletePredictions
                Controller.getGooglePlacesAutocompletePredictions(getActivity(), query,
                        new ParameterRunnable<AutocompletePredictionBuffer>() {
                            /**
                             * Process the results of the AutocompletePredictions
                             */
                            @Override
                            public void run() {

                                // Abort if the query has since changed
                                if (mLastChanged >= timeBeginProcessQuery)
                                    return;

                                // Get the buffer of autocomplete predictions
                                AutocompletePredictionBuffer buffer = getParameterObject();

                                // Clear the AutocompletePredictions buffer
                                mAutocompleteSuggestionAdapter.clear();

                                // For each prediction in the buffer, construct a String array to
                                // hold its name, address, and placeId, and add it to the
                                // AutocompleteSuggestionArrayAdapter
                                for (AutocompletePrediction prediction : buffer) {
                                    // Data item for the array adapter
                                    String[] predictionData = new String[3];
                                    // Place name
                                    predictionData[0] = prediction.getPrimaryText(null).toString();
                                    // Place address
                                    predictionData[1] = prediction.getSecondaryText(null).toString();
                                    // Place id
                                    predictionData[2] = prediction.getPlaceId();
                                    // Insert into autocomplete suggestions adapter
                                    mAutocompleteSuggestionAdapter.add(predictionData);
                                }

                                // Show "No results" if the result buffer was empty, else hide the
                                // header saying "Loading results" upon result received
                                if (buffer.getCount() == 0) {
                                    mSearchSuggestionsHeader.setText(getResources()
                                            .getText(R.string.no_results));
                                    mSearchSuggestionsHeader.setVisibility(View.VISIBLE);
                                } else {
                                    mSearchSuggestionsHeader.setVisibility(View.GONE);
                                }

                                // Release the buffer
                                buffer.release();

                                // Set the ListView's adapter to be the autocomplete suggestion adapter
                                mSearchSuggestionsList.setAdapter(mAutocompleteSuggestionAdapter);
                            }
                        });
            }
        }
    }
}
