package com.example.anne.otp_android_client_v3;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.location.places.Place;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import vanderbilt.thub.otp.model.TraverseMode;

import static android.content.ContentValues.TAG;
import static vanderbilt.thub.otp.model.TraverseMode.BICYCLE;
import static vanderbilt.thub.otp.model.TraverseMode.BUS;
import static vanderbilt.thub.otp.model.TraverseMode.CAR;
import static vanderbilt.thub.otp.model.TraverseMode.SUBWAY;
import static vanderbilt.thub.otp.model.TraverseMode.WALK;

/**
 * Created by Anne on 5/30/2017.
 */

public class DetailedSearchBarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inllate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater
                .inflate(R.layout.detailed_search_bar, container, false);

        final MainActivity activity = (MainActivity) getActivity();

        // Initialize Mode-ImageButton BiMap
        activity.addToModeButtonBiMap(WALK, (ImageButton) ll.findViewById(R.id.walk_mode_button));
        activity.addToModeButtonBiMap(CAR, (ImageButton) ll.findViewById(R.id.car_mode_button));
        activity.addToModeButtonBiMap(BUS, (ImageButton) ll.findViewById(R.id.bus_mode_button));
        activity.addToModeButtonBiMap(BICYCLE, (ImageButton) ll.findViewById(R.id.bike_mode_button));
        activity.addToModeButtonBiMap(SUBWAY, (ImageButton) ll.findViewById(R.id.subway_mode_button));

        Log.d(TAG, "Added mode buttons in BiMap");

        // Initialize the mode buttons in the detailed search bar fragment
        activity.setUpModeButtons();

        // Set up the EditTexts
        final EditText sourceEditText = (EditText)
                ll.findViewById(R.id.detailed_search_bar_from_edittext);
        final EditText destinationEditText = (EditText)
                ll.findViewById(R.id.deatiled_search_bar_to_edittext);
        sourceEditText.setHorizontallyScrolling(true);
        destinationEditText.setHorizontallyScrolling(true);

        // Initialize the text in the EditTexts
        if (activity.getCurrentSelectedSourcePlace() == null) sourceEditText.setText("My Location");
        else sourceEditText.setText(activity.getCurrentSelectedSourcePlace().getName());
        destinationEditText.setText(activity.getCurrentSelectedDestinationPlace().getName());

        // Set the listener for the swap button
        ImageButton swapButton = (ImageButton) ll.findViewById(R.id.swap_source_destination_button);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Swap the contents of the EditTexts
                Editable tempEditable = sourceEditText.getText();
                sourceEditText.setText(destinationEditText.getText());
                destinationEditText.setText(tempEditable);

                // Swap the source and destination
                Place tempPlace = activity.getCurrentSelectedSourcePlace();
                activity.setCurrentSelectedSourcePlace(activity.getCurrentSelectedDestinationPlace());
                activity.setCurrentSelectedDestinationPlace(tempPlace);

                // Refresh the trip plan
                activity.planAndDisplayTrip(activity.getCurrentSelectedSourcePlace(),
                        activity.getCurrentSelectedDestinationPlace());
            }
        });

        // Set the listener for the back button
        ImageButton backButton = (ImageButton) ll.findViewById(R.id.detailed_search_bar_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        return ll;
    }
}
