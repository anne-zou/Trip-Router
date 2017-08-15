package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Anne on 8/10/2017.
 */

public class TimePickerFragment extends DialogFragment implements
        TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current time as the default time in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
        // boolean parameter indicates whether it is a 24 hour view or AM/PM
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Call setTimeAndText() to update the time of first trip and the time EditText in the
        // EditScheduledTripFragment
        ((MainActivity) getActivity()).getEditScheduledTripFragment()
                .setTimeAndText(hourOfDay, minute);
    }
}
