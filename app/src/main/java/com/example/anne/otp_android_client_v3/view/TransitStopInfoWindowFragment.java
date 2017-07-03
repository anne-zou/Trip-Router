package com.example.anne.otp_android_client_v3.view;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anne.otp_android_client_v3.view.MainActivity;
import com.example.anne.otp_android_client_v3.R;
import com.example.anne.otp_android_client_v3.view.ItineraryLegIconView;

import java.util.ArrayList;

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
     * @pre the OTPService retrofit has already been built
     * @param stopId
     */
    public void showStopInfo(String stopName, String stopId) {

        final long timeOfTransitRouteRequest = System.currentTimeMillis();

        setTransitStopNameText(stopName);
        if (mRouteIconsLayout != null) mRouteIconsLayout.removeAllViews();

        Call<ArrayList<Route>> call = OTPService.getOtpService().getRoutesByStop(
                OTPService.ROUTER_ID,
                stopId,
                "true","true"
        );

        call.enqueue(new Callback<ArrayList<Route>>() {
            @Override
            public void onResponse(Call<ArrayList<Route>> call, Response<ArrayList<Route>> response) {

                if (((MainActivity) getActivity()).timeOfLastTransitRoutesInterrupt
                        > timeOfTransitRouteRequest)
                    return;

                ArrayList<Route> routeList = response.body();
                for (Route route : routeList) {
                    while (getActivity() == null) // wait until onAttach has been called
                        try {Thread.sleep(100);} catch (InterruptedException ie) {}
                    ItineraryLegIconView view = new ItineraryLegIconView(getActivity());
                    view.setRouteName(route.getShortName());
                    view.setRouteColor(Color.parseColor("#" + route.getColor()));
                    view.setShowRoute(true);
                    mRouteIconsLayout.addView(view);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Route>> call, Throwable throwable) {}
        });

    }

    public void setTransitStopNameText(String name) {
        mTransitStopName = name;
        if (mTransitStopNameTextView != null)
            mTransitStopNameTextView.setText(name);
    }

    public void addRouteIcon(ItineraryLegIconView icon) {
        mRouteIconsLayout.addView(icon);
    }

    public String getTransitStopNameText() {
        return mTransitStopNameTextView.getText().toString();
    }

}
