package com.example.anne.otp_android_client_v3.view;

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

import com.example.anne.otp_android_client_v3.R;

import java.util.Date;

/**
 * Created by Anne on 6/16/2017.
 */

public class SetDepartOrArriveTimeDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.dialog_set_time, null);

        final MainActivity activity = (MainActivity) getActivity();
        final TimePicker timePicker = (TimePicker) ll.findViewById(R.id.time_picker);
        final Button departButton = (Button) ll.findViewById(R.id.depart_button);
        final Button arriveButton = (Button) ll.findViewById(R.id.arrive_button);
        select(departButton);
        unselect(arriveButton);


        departButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button) v;
                if (!button.isSelected()) {
                    select(button);
                    unselect(arriveButton);
                }
            }
        });
        arriveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Button button = (Button) v;
                if (!button.isSelected()) {
                    select(button);
                    unselect(departButton);
                }
            }
        });

        builder.setView(ll)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        // Signal activity to ignore the response of any ongoing request
                        ((MainActivity) getActivity()).interruptTransitRoutesRequest();

                        // Plan the trip
                        Date now = new Date();
                        activity.planTrip(
                                activity.getmOrigin(),
                                activity.getmDestination(),
                                new Date(now.getYear(), now.getMonth(), now.getDate(),
                                        timePicker.getHour(), timePicker.getMinute()),
                                arriveButton.isSelected()
                        );
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SetDepartOrArriveTimeDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    private void select(Button button) {
        button.setSelected(true);
        button.setTextColor(Color.WHITE);
        button.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
    }

    private void unselect(Button button) {
        button.setSelected(false);
        button.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        button.setBackgroundColor(Color.WHITE);
    }

}
