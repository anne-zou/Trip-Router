package com.example.anne.otp_android_client_v3.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anne.otp_android_client_v3.MainActivity;
import com.example.anne.otp_android_client_v3.R;
import com.example.anne.otp_android_client_v3.custom_views.ItineraryLegIconView;

/**
 * Created by Anne on 6/23/2017.
 */

public class TransitStopInfoWindowFragment extends Fragment {

    private TextView mTransitStopNameTextView;

    private LinearLayout mRouteIconsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater
                .inflate(R.layout.transit_stop_info_window_layout, container, false);

        final MainActivity activity = (MainActivity) getActivity();

        mTransitStopNameTextView = (TextView) ll.findViewById(R.id.transit_stop_name_text_view);
        mRouteIconsLayout = (LinearLayout)
                ll.findViewById(R.id.associated_routes_for_transit_stop_layout);

        return ll;
    }

    public void getRoutesAndDisplayIcons(String stopId) {
        // TODO make request to find bus routes that pass through this stop
    }

    public void setTransitStopNameText(String name) {
        mTransitStopNameTextView.setText(name);
    }

    public void addRouteIcon(ItineraryLegIconView icon) {
        mRouteIconsLayout.addView(icon);
    }

    public String getTransitStopNameText() {
        return mTransitStopNameTextView.getText().toString();
    }

}
