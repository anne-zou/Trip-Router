package com.example.anne.otp_android_client_v3;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

/**
 * Created by Anne on 5/25/2017.
 */

public class ItinerarySummaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create the layout for this fragment

        LinearLayout itinerarySummaryLayout = new LinearLayout(getActivity());
        itinerarySummaryLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout tabAndItinerarySummaryLayout = new LinearLayout(getActivity());
        tabAndItinerarySummaryLayout.setOrientation(LinearLayout.VERTICAL);
        tabAndItinerarySummaryLayout.addView(new TabHost(getActivity()));
        tabAndItinerarySummaryLayout.addView(itinerarySummaryLayout);

        return tabAndItinerarySummaryLayout;
    }
}
