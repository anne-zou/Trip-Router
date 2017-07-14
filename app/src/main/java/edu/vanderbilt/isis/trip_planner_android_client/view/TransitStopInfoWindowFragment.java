package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;
import edu.vanderbilt.isis.trip_planner_android_client.R;

/**
 * Created by Anne on 6/23/2017.
 */

/**
 * Info window that shows the name and the servicing transit routes for a transit stop in the
 * HOME_STOP_SELECTED state of the MainActivity. Makes a request to obtain the transit routes that
 * serve the stop and displays the route names in ItineraryLegIconViews upon result.
 */
public class TransitStopInfoWindowFragment extends Fragment {

    private TextView mTransitStopNameTextView;

    private String mTransitStopName;

    private LinearLayout mRouteIconsLayout;


    /**
     * Inflate the layout and implement the functionality of each of its views
     * @param inflater the view inflater
     * @param container the container for the fragment
     * @param savedInstanceState nah
     * @return the newly created view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater
                .inflate(R.layout.transit_stop_info_window_layout, container, false);

        mTransitStopNameTextView = (TextView)
                ll.findViewById(R.id.transit_stop_name_text_view);
        mRouteIconsLayout = (LinearLayout)
                ll.findViewById(R.id.associated_routes_for_transit_stop_layout);

        if (mTransitStopName != null)
            mTransitStopNameTextView.setText(mTransitStopName);

        return ll;
    }

    /**
     * Runs in a background thread to wait until the fragment has been attached, then requests
     * the transit routes servicing the transit stop
     * @param stopId the id of the transit stop to request info about
     */
    public void requestStopRoutes(final String stopId) {

        // Start AsyncTask to wait until the fragment has been attached, then request the routes
        // that service the transit stop
        new AsyncTask<Void, Void, Boolean>() {

            /**
             * Code to be executed in the background: wait until fragment is attached
             * @param params dummy parameter
             * @return true if the fragment has been attached, false if the thread was
             *         interrupted
             */
            @Override
            protected Boolean doInBackground(Void... params) {
                // Block until onAttach has been called
                while (getActivity() == null)
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ie) {
                        return false;
                    }
                return true;
            }

            /**
             * Code to be executed on the UI thread after doInBackground() is finished executing
             * Requests the routes that are servicing the transit stop
             * @param verifiedAttached return value of doInBackground()
             */
            @Override
            protected void onPostExecute(Boolean verifiedAttached) {
                super.onPostExecute(verifiedAttached);
                // If the fragment is attached, request the routes that are servicing the
                // transit stop
                if (verifiedAttached || getActivity() != null)
                    Controller.requestRoutesServicingTransitStop((MainActivity) getActivity(),
                            stopId); // invokes a callback defined in the main activity
            }
        }.execute();
    }

    /**
     * Remove all child views of the route icons layout in the info window fragment
     */
    public void clear() {
        if (mRouteIconsLayout != null) mRouteIconsLayout.removeAllViews();
    }

    /**
     * Add another route icon view to the route icons layout in the info window fragment
     * @param icon the view to be added
     */
    public void addRouteIcon(ItineraryLegIconView icon) {
        mRouteIconsLayout.addView(icon);
    }

    /**
     * Show the name of the transit stop in the info window fragment
     * @param name the name to be shown
     */
    public void setTransitStopNameText(String name) {
        mTransitStopName = name;
        if (mTransitStopNameTextView != null)
            mTransitStopNameTextView.setText(name);
    }

    /**
     * Get the name of the transit stop
     * @return the name of the transit stop
     */
    public String getTransitStopNameText() {
        return mTransitStopNameTextView.getText().toString();
    }

}
