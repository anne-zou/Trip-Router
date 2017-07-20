package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import edu.vanderbilt.isis.trip_planner_android_client.R;

/**
 * Created by Anne on 7/19/2017.
 */

public class EditScheduledTripFragment extends Fragment {

    public static String SCHEDULE_ID = "id";

    public static String NEXT_TRIP_TIME = "next_trip_time";

    public static String FIRST_TRIP_TIME = "first_trip_time";

    public static String REMINDER_MINS = "rem_mins";

    public static String REPEAT_DAYS = "rep_days";

    public static String DESTINATION_NAME = "d_name";

    public static String DESTINATION_LAT = "d_lat";

    public static String DESTINATION_LON = "d_lon";

    public static String DESTINATION_ADDRESS = "d_addr";

    public static String ORIGIN_NAME = "o_name";

    public static String ORIGIN_LAT = "o_lat";

    public static String ORIGIN_LON = "o_lon";

    public static String ORIGIN_ADDRESS = "o_addr";

    private int mScheduleId;

    private Calendar mNextTripTime;

    private Calendar mFirstTripTime;

    private int mReminderMins;

    private Set<String> mRepeatDays;

    private String mDestName;

    private String mOrigName;

    private LatLng mDestCoords;

    private LatLng mOrigCoords;

    private String mDestAddr;

    private String mOrigAddr;

    private EditText mFromEditText;

    private EditText mToEditText;

    private EditText mDateEditText;

    private EditText mTimeEditText;


    /**
     * Invoked upon creation of the Fragment.
     * Extracts arguments from the bundle & saves them.
     * @param savedInstanceState bundle of arguments passed into the constructor
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Extract arguments from bundle if they exist
        if (savedInstanceState != null) {

            // mScheduleId & mReminderMins will be 0 if not in bundle
            mScheduleId = savedInstanceState.getInt(SCHEDULE_ID);
            mReminderMins = savedInstanceState.getInt(REMINDER_MINS);


            // mRepeatDays will be empty if not in bundle
            mRepeatDays = new HashSet<>();
            String repeatDaysString = savedInstanceState.getString(REPEAT_DAYS);
            // If in bundle, it will contain the Strings representing the days of the week
            // the trip was repeated
            if (repeatDaysString != null)
                mRepeatDays.addAll(Arrays.asList(repeatDaysString.split(" ")));

            // mNextTripTime & mFirstTripTime will be null if not in bundle
            long timeNextTripMillis = savedInstanceState.getLong(NEXT_TRIP_TIME);
            if (timeNextTripMillis != 0L) {
                mNextTripTime = Calendar.getInstance();
                mNextTripTime.setTimeInMillis(timeNextTripMillis);
            }
            long timeFirstTripMillis = savedInstanceState.getLong(FIRST_TRIP_TIME);
            if (timeFirstTripMillis != 0L) {
                mFirstTripTime = Calendar.getInstance();
                mFirstTripTime.setTimeInMillis(timeFirstTripMillis);
            }

            // mDestName, mOrigName, mDestAddr, and mOrigAddr will be null if not in bundle
            mDestName = savedInstanceState.getString(DESTINATION_NAME);
            mOrigName = savedInstanceState.getString(ORIGIN_NAME);
            mDestAddr = savedInstanceState.getString(DESTINATION_ADDRESS);
            mOrigAddr = savedInstanceState.getString(ORIGIN_ADDRESS);

            // mDestCoords and mOrigCoords will be null if not in bundle
            double dLat = savedInstanceState.getDouble(DESTINATION_LAT);
            double dLon = savedInstanceState.getDouble(DESTINATION_LON);
            if (dLat != 0.0 && dLon != 0.0)
                mDestCoords = new LatLng(dLat, dLon);

            double oLat = savedInstanceState.getDouble(ORIGIN_LAT);
            double oLon = savedInstanceState.getDouble(ORIGIN_LON);
            if (oLat != 0.0 && oLon != 0.0)
                mOrigCoords = new LatLng(oLat, oLon);
        }
    }

    /**
     * Inflate the view, initialize the children of the fragment, and return the inflated view
     * @param inflater inflater used to inflate the view
     * @param container container to inflate the view into
     * @param savedInstanceState bundle of arguments passed into the constructor for this Fragment
     * @return the newly inflated view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        RelativeLayout rl = (RelativeLayout) inflater
                .inflate(R.layout.edit_scheduled_trip_layout, container, false);

        // Get all the child views we want to modify/set up
        mFromEditText = (EditText) rl.findViewById(R.id.scheduled_trip_from_edittext);
        mToEditText = (EditText) rl.findViewById(R.id.scheduled_trip_to_edittext);
        mDateEditText = (EditText) rl.findViewById(R.id.scheduled_trip_date_edittext);
        mTimeEditText = (EditText) rl.findViewById(R.id.scheduled_trip_time_edittext);
        final Spinner reminderSpinner = (Spinner) rl.findViewById(R.id.scheduled_trip_reminder_spinner);

        TextView mondayButton = (TextView) rl.findViewById(R.id.scheduled_trip_monday_button);
        TextView tuesdayButton = (TextView) rl.findViewById(R.id.schedule_trip_tuesday_button);
        TextView wednesdayButton = (TextView) rl.findViewById(R.id.schedule_trip_wednesday_button);
        TextView thursdayButton = (TextView) rl.findViewById(R.id.schedule_trip_thursday_button);
        TextView fridayButton = (TextView) rl.findViewById(R.id.schedule_trip_friday_button);
        TextView saturdayButton = (TextView) rl.findViewById(R.id.schedule_trip_saturday_button);
        TextView sundayButton = (TextView) rl.findViewById(R.id.schedule_trip_sunday_button);
        TextView saveButton = (TextView) rl.findViewById(R.id.schedule_trip_save_button);


        // Fill in the EditTexts to match any existing information for this schedule
        if (mOrigName != null) // 'from'
            mFromEditText.setText(mOrigName);
        if (mDestName != null) // 'to'
            mToEditText.setText(mDestName);
        if (mNextTripTime != null) { // 'date' and 'time'
            mDateEditText.setText(new SimpleDateFormat("MM/dd/yy").format(mFirstTripTime));
            mTimeEditText.setText(new SimpleDateFormat("hh:mm").format(mFirstTripTime));
        }

        // Select the buttons for any existing repeat days set for this schedule
        if (mRepeatDays.contains("M"))
            markButtonSelected(mondayButton);
        if (mRepeatDays.contains("T"))
            markButtonSelected(tuesdayButton);
        if (mRepeatDays.contains("W"))
            markButtonSelected(wednesdayButton);
        if (mRepeatDays.contains("Th"))
            markButtonSelected(thursdayButton);
        if (mRepeatDays.contains("F"))
            markButtonSelected(fridayButton);
        if (mRepeatDays.contains("Sa"))
            markButtonSelected(saturdayButton);
        if (mRepeatDays.contains("Su"))
            markButtonSelected(sundayButton);

        // Set the adapter of dropdown list items for the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.reminder_times_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(adapter);

        // Set the spinner selection if there was an existing reminder set for this schedule
        if (mReminderMins != 0) {
            switch (mReminderMins) {
                case 10:
                    reminderSpinner.setSelection(0);
                    break;
                case 20:
                    reminderSpinner.setSelection(1);
                    break;
                case 30:
                    reminderSpinner.setSelection(2);
                    break;
                case 60:
                    reminderSpinner.setSelection(3);
                    break;
            }
        }


        // Set the on item click listener for the spinner
        reminderSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Modify mReminderMins based on what was selected
                switch (position) {
                    case 0:
                        mReminderMins = 10;
                        break;
                    case 1:
                        mReminderMins = 20;
                        break;
                    case 2:
                        mReminderMins = 30;
                        break;
                    case 3:
                        mReminderMins = 60;
                        break;
                }
            }
        });


        // Set the onClick listeners for the from and to EditTexts
        mFromEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the search view fragment to select a place; will call setOrigin on
                // this fragment if a place is selected
                ((MainActivity) getActivity()).launchSearchViewFragment(
                        new SearchField(mFromEditText, SearchField.ORIGIN));
            }
        });
        mToEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the search view fragment to select a place; will call setDestination on
                // this fragment if a place is selected
                ((MainActivity) getActivity()).launchSearchViewFragment(
                        new SearchField(mToEditText, SearchField.DESTINATION));
            }
        });

        // Set the on click listeners for the day buttons
        mondayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        tuesdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        wednesdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        thursdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        fridayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        saturdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        sundayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());



        // TODO: set the on click listener for the save button:
        // make sure origin and destination are selected
        // make sure time of first trip is selected
        // calculate time of next trip
        // update/insert row in schedules table in trip plan database
        // close EditScheduledTripFragment

        return rl;
    }

    private class DayOfWeekButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;

            // Get the string of the text in the TextView, representing the day of the week that the
            // button is for ("M", "T", "W", "Th", "F", "Sa", or "Su")
            String dayString = tv.getText().toString();

            if (mRepeatDays.contains(dayString)) {
                // If day was already selected, remove day String from set of repeat day Strings
                // & deselect the view
                mRepeatDays.remove(dayString);
                markButtonDeselected(tv);
            } else {
                // If day was not already selected, add day String to the set of repeat day Strings
                // & select the view
                mRepeatDays.add(dayString);
                markButtonSelected(tv);
            }

        }
    }

    /**
     * Set the origin for the scheduled trip
     * @param tripPlanPlace the place to set
     */
    public void setOrigin(TripPlanPlace tripPlanPlace) {
        // Save the name, address, and location, to be written to the database upon "save"
        mOrigName = tripPlanPlace.getName();
        mOrigAddr = tripPlanPlace.getAddress();
        mOrigCoords = tripPlanPlace.getLocation();
        // Show the place name in the search field EditText
        mFromEditText.setText(mOrigName);
    }

    /**
     * Set the destination for the scheduled trip
     * @param tripPlanPlace the place to set
     */
    public void setDestination(TripPlanPlace tripPlanPlace) {
        // Save the name, address, and location, to be written to the database upon "save"
        mDestName = tripPlanPlace.getName();
        mDestAddr = tripPlanPlace.getAddress();
        mDestCoords = tripPlanPlace.getLocation();
        // Show the place name in the search field EditText
        mToEditText.setText(mDestName);
    }

    /**
     * Change a textview's text color and background image to depict it as selected
     * @param dayButton the textview to mark as selected
     */
    private void markButtonSelected(TextView dayButton) {
        dayButton.setTextColor(Color.WHITE);
        dayButton.setBackground(getResources()
                .getDrawable(R.drawable.rounded_rectangle_primary, null));
    }

    /**
     * Change a textview's text color and background image to depict it as deselected
     * @param dayButton the textview to mark as deselected
     */
    private void markButtonDeselected(TextView dayButton) {
        dayButton.setTextColor(getResources().getColor(R.color.colorPrimary, null));
        dayButton.setBackground(getResources()
                .getDrawable(R.drawable.rounded_rectangle_white, null));
    }




}
