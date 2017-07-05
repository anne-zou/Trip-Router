package com.example.anne.otp_android_client_v3.view;

import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anne.otp_android_client_v3.controller.Controller;
import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.example.anne.otp_android_client_v3.R;
import com.example.anne.otp_android_client_v3.view.ItineraryLegIconView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vanderbilt.thub.otp.model.OTPStopsModel.Route;
import vanderbilt.thub.otp.service.OTPService;

/**
 * Created by Anne on 6/23/2017.
 */

public class TransitStopInfoWindowFragment extends Fragment {

    private TextView mTransitStopNameTextView;

    private String mTransitStopName;

    private LinearLayout mRouteIconsLayout;


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
     * Shows info about a stop in this fragment
     * @param stopId
     */
    public void requestStopInfo(final String stopId) {

        // Start AsyncTask to wait until the fragment has been attached, then show route
        // icons in the fragment
        new AsyncTask<Boolean, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... params) {
                // Block until onAttach has been called
                while (getActivity() == null)
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ie) {
                        return false;
                    }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean verifiedAttached) {
                super.onPostExecute(verifiedAttached);
                if (verifiedAttached)
                    Controller.requestRoutesServicingTransitStop((MainActivity) getActivity(),
                            stopId);
            }
        }.execute();
    }

    public void clear() {
        if (mRouteIconsLayout != null) mRouteIconsLayout.removeAllViews();
    }

    public void addRouteIcon(ItineraryLegIconView icon) {
        mRouteIconsLayout.addView(icon);
    }

    public void setTransitStopNameText(String name) {
        mTransitStopName = name;
        if (mTransitStopNameTextView != null)
            mTransitStopNameTextView.setText(name);
    }

    public String getTransitStopNameText() {
        return mTransitStopNameTextView.getText().toString();
    }

}
