package com.example.anne.otp_android_client_v3;

import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.concurrent.ConcurrentMap;

import static android.content.ContentValues.TAG;

/**
 * Helper class to update the current search results when a suggestion is clicked
 */
public class MyOnSearchListener implements FloatingSearchView.OnSearchListener {

    private MainActivity mActivity;

    // Constructor passes in the fsv to be updated
    public MyOnSearchListener(MainActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onSearchAction(String currentQuery) {}

    // If a suggestion is clicked, update the current selected destination
    @Override
    public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
        PlaceSearchSuggestion destination = (PlaceSearchSuggestion) searchSuggestion;
        mActivity.setDestination(destination);
        Log.d(TAG, "Search suggestion was selected: " + searchSuggestion.getBody() +
                "\nCoordinates: " + destination.getLatLng().toString());
    }

}
