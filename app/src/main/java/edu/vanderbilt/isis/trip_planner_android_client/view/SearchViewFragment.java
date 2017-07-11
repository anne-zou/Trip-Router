package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.model.database.SearchHistoryCursorAdapter;
import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;

/**
 * Created by Anne on 7/11/2017.
 */

public class SearchViewFragment extends Fragment implements LoaderManager.LoaderCallbacks {

    SearchHistoryCursorAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        RelativeLayout rl = (RelativeLayout) inflater
                .inflate(R.layout.search_view_layout, container, false);

        // Create an empty adapter we will use to display the loaded data
        

        return rl;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
