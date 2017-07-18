package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.controller.Controller;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Anne on 6/16/2017.
 */

/**
 * The dialog fragment that pops up when the depart/arrive time bar in the DetailedSearchBarFragment
 * is clicked. Allows the user to select a time of day, and whether they want to DEPART or ARRIVE at
 * that time for the next trip plan, then launches the next trip plan.
 */
public class DepartOrArriveTimeDialogFragment extends DialogFragment {


    /**
     * Inflate the layout for this Fragment, implement the functionality of each of its child views,
     * and build and return the Dialog
     * @param savedInstanceState nah
     * @return the newly created Dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Initialize the builder for the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Inflate the layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.dialog_set_time, null);

        final MainActivity activity = (MainActivity) getActivity();
        final TimePicker timePicker = (TimePicker) ll.findViewById(R.id.time_picker);
        final Button departButton = (Button) ll.findViewById(R.id.depart_button);
        final Button arriveButton = (Button) ll.findViewById(R.id.arrive_button);

        // Initialize the time picker to the last trip plan time
        if (MainActivity.getmTripPlanTime() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(MainActivity.getmTripPlanTime());
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        } 

        // Initialize the buttons
        if (((MainActivity) getActivity()).getmArriveBy()) {
            select(arriveButton);
            deselect(departButton);
        } else {
            select(departButton);
            deselect(arriveButton);
        }

        // Set the click listener for the buttons
        // If it is selected, do nothing
        // If it is not selected, selected it and deselect the other button
        departButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (!button.isSelected()) {
                    select(button);
                    deselect(arriveButton);
                }
            }
        });
        arriveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button button = (Button) v;
                if (!button.isSelected()) {
                    select(button);
                    deselect(departButton);
                }
            }
        });

        // Set the view for the Dialog builder
        builder.setView(ll)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    /**
                     * If the "OK" button is clicked, plan a new trip with the additional
                     * depart/arrive by time parameter
                     * @param dialog the dialog that was clicked
                     * @param id id of the button clicked
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // Signal activity to ignore the response of any ongoing request
                        Controller.interruptOngoingRoutesRequests();

                        // Configure the date/time to plan the trip
                        Calendar now = Calendar.getInstance(); // now time

                        // Use today
                        Calendar timeInPicker = Calendar.getInstance();
                        timeInPicker.set(now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH), now.get(Calendar.DATE),
                                timePicker.getHour(), timePicker.getMinute());

                        // If already passed that time today, use the same time the next day
                        if (now.getTime().after(timeInPicker.getTime()))
                            timeInPicker.add(Calendar.DATE, 1); // add a day

                        // Plan the trip
                        activity.planTrip(
                                activity.getmOrigin(),
                                activity.getmDestination(),
                                timeInPicker.getTime(),
                                arriveButton.isSelected()
                        );
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    /**
                     * If the "Cancel" button is clicked, close the dialog.
                     * @param dialog the dialog that was clicked
                     * @param id id of the button clicked
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DepartOrArriveTimeDialogFragment.this.getDialog().cancel();
                    }
                });

        // Build the Dialog and return it
        return builder.create();
    }

    /**
     * Set a button as selected and change its appearance accordingly
     * @param button the button to select
     */
    private void select(Button button) {
        button.setSelected(true);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
    }

    /**
     * Set a button as deselected and change its appearance accordingly
     * @param button the button to deselect
     */
    private void deselect(Button button) {
        button.setSelected(false);
        button.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        button.setBackgroundColor(Color.WHITE);
    }

}
