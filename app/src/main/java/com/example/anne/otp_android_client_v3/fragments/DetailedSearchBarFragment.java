package com.example.anne.otp_android_client_v3.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.anne.otp_android_client_v3.MainActivity;
import com.example.anne.otp_android_client_v3.R;
import com.example.anne.otp_android_client_v3.TripPlanPlace;
import com.google.android.gms.location.places.Place;

import static android.content.ContentValues.TAG;
import static vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode.BICYCLE;
import static vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode.BUS;
import static vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode.CAR;
import static vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode.WALK;

/**
 * Created by Anne on 5/30/2017.
 */

public class DetailedSearchBarFragment extends Fragment {

    private EditText originEditText;

    private EditText destinationEditText;

    private TextView departArriveTimeTextView;

    private String departArriveText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        LinearLayout ll = (LinearLayout) inflater
                .inflate(R.layout.detailed_search_bar_layout, container, false);

        final MainActivity activity = (MainActivity) getActivity();

        // Initialize Mode-ImageButton BiMap
        activity.addToModeButtonBiMap(WALK, (ImageButton) ll.findViewById(R.id.walk_mode_button));
        activity.addToModeButtonBiMap(CAR, (ImageButton) ll.findViewById(R.id.car_mode_button));
        activity.addToModeButtonBiMap(BUS, (ImageButton) ll.findViewById(R.id.bus_mode_button));
        activity.addToModeButtonBiMap(BICYCLE, (ImageButton) ll.findViewById(R.id.bike_mode_button));

        Log.d(TAG, "Added mode buttons in BiMap");

        // Initialize the mode buttons in the detailed search bar fragment
        activity.setUpModeButtons();

        // Set up the EditTexts
        originEditText = (EditText)
                ll.findViewById(R.id.detailed_search_bar_from_edittext);
        destinationEditText = (EditText)
                ll.findViewById(R.id.deatiled_search_bar_to_edittext);

        originEditText.setHorizontallyScrolling(true);
        destinationEditText.setHorizontallyScrolling(true);

        originEditText.setFocusable(false);
        destinationEditText.setFocusable(false);

        // Initialize the text in the EditTexts
        if (activity.getmOrigin() == null) setOriginText("My Location");
        else setOriginText(activity.getmOrigin().getName());

        setDestinationText(activity.getmDestination().getName());

        // Set the onClickListeners for the EditTexts
        class EditTextOnClickListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {

                EditText et = (EditText) v;

                if (et == originEditText)
                    activity.launchGooglePlacesSearchWidget(MainActivity.SearchBarId.DETAILED_FROM);
                if (et == destinationEditText)
                    activity.launchGooglePlacesSearchWidget(MainActivity.SearchBarId.DETAILED_TO);

            }
        }
        originEditText.setOnClickListener(new EditTextOnClickListener());
        destinationEditText.setOnClickListener(new EditTextOnClickListener());

        // Set the listener for the swap button
        ImageButton swapButton = (ImageButton) ll.findViewById(R.id.swap_origin_destination_button);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Swap the contents of the EditTexts
                Editable tempEditable = originEditText.getText();
                setOriginText(destinationEditText.getText());
                setDestinationText(tempEditable);

                // Refresh the trip plan
                activity.planTrip(activity.getmDestination(),
                        activity.getmOrigin());

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

        // Initialize the depart/arrive time TextView
        departArriveTimeTextView = (TextView) ll.findViewById(R.id.depart_arrive);

        if (departArriveText != null)
            departArriveTimeTextView.setText(departArriveText);
        else departArriveTimeTextView.setText("Depart by/arrive by...");

        departArriveTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetDepartOrArriveTimeDialogFragment dialog =
                        new SetDepartOrArriveTimeDialogFragment();
                dialog.show(getFragmentManager(),"Show set depart or arrive time dialog fragment");
            }
        });

        return ll;
    }

    public void setOriginText(String text) {
        originEditText.setText(text);
    }

    public void setOriginText(CharSequence text) {
        setOriginText(text.toString());
    }

    public void setDestinationText(String text) {
        destinationEditText.setText(text);
    }

    public void setDestinationText(CharSequence text) {
        setDestinationText(text.toString());
    }

    public void setDepartArriveTimeText(String text) {
        departArriveText = text;
        if (departArriveTimeTextView != null)
            departArriveTimeTextView.setText(text);
    }

    public String getOriginText() {
        return originEditText.getText().toString();
    }

    public String getDestinationText() {
        return destinationEditText.getText().toString();
    }

    public String getDepartArriveTimeText() {
        return departArriveTimeTextView.getText().toString();
    }


}
