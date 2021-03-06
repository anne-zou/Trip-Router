package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Fragment;
import android.os.AsyncTask;
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

import java.util.ArrayList;
import java.util.List;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.TraverseMode;

/**
 * Created by Anne on 5/30/2017.
 */

/**
 * Detailed search bar that appears in the TRIP_PLAN screen of the MainActivity.
 * Displays the origin name, destination name, selected modes, first mode, & depart/arrive time of
 * the current trip plan. Can be used to change the origin, destination, selected modes, first mode,
 * or depart/arrive time for the next trip plan and then launch the next trip plan.
 * See detailed_search_bar_layout.xml for the layout details.
 */
public class DetailedSearchBarFragment extends Fragment {

    private static final String TAG = DetailedSearchBarFragment.class.getName();

    private EditText originEditText;

    private EditText destinationEditText;

    private TextView departArriveTimeTextView;

    private String departArriveText;

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
                .inflate(R.layout.detailed_search_bar_layout, container, false);

        final MainActivity activity = (MainActivity) getActivity();

        // Initialize Mode-ImageButton BiMap
        activity.addToModeButtonBiMap(TraverseMode.WALK, (ImageButton) ll.findViewById(R.id.walk_mode_button));
        activity.addToModeButtonBiMap(TraverseMode.CAR, (ImageButton) ll.findViewById(R.id.car_mode_button));
        activity.addToModeButtonBiMap(TraverseMode.BUS, (ImageButton) ll.findViewById(R.id.bus_mode_button));
        activity.addToModeButtonBiMap(TraverseMode.BICYCLE, (ImageButton) ll.findViewById(R.id.bike_mode_button));

        Log.d(TAG, "Added mode buttons in BiMap");

        // Initialize the mode buttons in the detailed search bar fragment
        activity.initializeModeButtons();

        // Set up the EditTexts
        originEditText = (EditText)
                ll.findViewById(R.id.detailed_search_bar_from_edittext);
        destinationEditText = (EditText)
                ll.findViewById(R.id.deatiled_search_bar_to_edittext);

        // Set scrolling
        originEditText.setHorizontallyScrolling(true);
        destinationEditText.setHorizontallyScrolling(true);

        // Set focusable
        originEditText.setFocusable(false);
        destinationEditText.setFocusable(false);

        // Set text
        if (activity.getmOrigin() != null)
            originEditText.setText(activity.getmOrigin().getName());
        if (activity.getmDestination() != null)
            destinationEditText.setText(activity.getmDestination().getName());

        // Set the onClickListeners for the EditTexts
        class EditTextOnClickListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {

                EditText et = (EditText) v;

                // If one of the 'From' or 'To' search fields is clicked, launch the
                // SearchViewFragment
                activity.launchSearchViewFragment(new SearchField(et,
                        et == originEditText ? SearchField.ORIGIN : SearchField.DESTINATION));

            }
        }
        originEditText.setOnClickListener(new EditTextOnClickListener());
        destinationEditText.setOnClickListener(new EditTextOnClickListener());

        // Set the listener for the swap button
        ImageButton swapButton = (ImageButton) ll.findViewById(R.id.swap_origin_destination_button);
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Controller.interruptOngoingTripPlanRequests();

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
                DepartOrArriveTimeDialogFragment dialog =
                        new DepartOrArriveTimeDialogFragment();
                dialog.show(getFragmentManager(),"Show set depart or arrive time dialog fragment");
            }
        });

        return ll;
    }

    /**
     * Set the contents of the "from" search box
     * @param text the text to show
     */
    public void setOriginText(String text) {
        if (originEditText != null)
            originEditText.setText(text);
    }

    /**
     * Set the contents of the "from" search box
     * @param text the text to show
     */
    public void setOriginText(CharSequence text) {
        setOriginText(text.toString());
    }


    /**
     * Set the contents of the "to" search box
     * @param text the text to show
     */
    public void setDestinationText(String text) {
        if (destinationEditText != null)
            destinationEditText.setText(text);
    }

    /**
     * Set the contents of the "to" search box
     * @param text the text to show
     */
    public void setDestinationText(CharSequence text) {
        setDestinationText(text.toString());
    }

    /**
     * Set the contents of the depart/arrive time bar at the bottom of the detailed search view
     * @param text the text to show
     */
    public void setDepartArriveTimeText(String text) {
        departArriveText = text;
        if (departArriveTimeTextView != null)
            departArriveTimeTextView.setText(text);
    }

    /**
     * Get the "from" search box
     * @return the "from" search box
     */
    public TextView getOriginSearchField() {
        return originEditText;
    }

    /**
     * Get the "to" search box
     * @return the "to" search box
     */
    public TextView getDestinationSearchField() {
        return destinationEditText;
    }


}
