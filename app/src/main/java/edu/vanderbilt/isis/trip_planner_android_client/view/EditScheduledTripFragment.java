package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import edu.vanderbilt.isis.trip_planner_android_client.R;

/**
 * Created by Anne on 7/19/2017.
 */

public class EditScheduledTripFragment extends Fragment {

    public static String IS_EXISTING_SCHEDULE = "is_new";

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

    private SparseArray<String> dayOfTheWeekStringMap;

    private boolean mIsExistingSchedule;

    private int mScheduleId;

    private Calendar mNextTripTime;

    private static Calendar mFirstTripTime; // static so can be updated from date & time picker fragments

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
     * Extracts arguments from the bundle & saves them. The bundle should contain
     * arguments iff this is editing an existing scheduled trip already in the database.
     * @param savedInstanceState bundle of arguments passed into the constructor
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Extract arguments from bundle if they exist
        if (savedInstanceState != null) {

            // mScheduleId will be null if not in bundle
            mScheduleId = savedInstanceState.getInt(SCHEDULE_ID);

            // mReminderMins will be 0 if not in bundle
            mReminderMins = savedInstanceState.getInt(REMINDER_MINS);

            // mIsExistingSchedule will be false if not in bundle (default is new schedule)
            mIsExistingSchedule = savedInstanceState.getBoolean(IS_EXISTING_SCHEDULE);

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

        // Initialize day of the week string map
        dayOfTheWeekStringMap = new SparseArray<>();
        dayOfTheWeekStringMap.put(Calendar.MONDAY, getResources().getString(R.string.monday));
        dayOfTheWeekStringMap.put(Calendar.TUESDAY, getResources().getString(R.string.tuesday));
        dayOfTheWeekStringMap.put(Calendar.WEDNESDAY, getResources().getString(R.string.wednesday));
        dayOfTheWeekStringMap.put(Calendar.THURSDAY, getResources().getString(R.string.thursday));
        dayOfTheWeekStringMap.put(Calendar.FRIDAY, getResources().getString(R.string.friday));
        dayOfTheWeekStringMap.put(Calendar.SATURDAY, getResources().getString(R.string.saturday));
        dayOfTheWeekStringMap.put(Calendar.SUNDAY, getResources().getString(R.string.sunday));

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
        if (mRepeatDays.contains(dayOfTheWeekStringMap.get(Calendar.MONDAY)))
            markButtonSelected(mondayButton);
        if (mRepeatDays.contains(dayOfTheWeekStringMap.get(Calendar.TUESDAY)))
            markButtonSelected(tuesdayButton);
        if (mRepeatDays.contains(dayOfTheWeekStringMap.get(Calendar.WEDNESDAY)))
            markButtonSelected(wednesdayButton);
        if (mRepeatDays.contains(dayOfTheWeekStringMap.get(Calendar.THURSDAY)))
            markButtonSelected(thursdayButton);
        if (mRepeatDays.contains(dayOfTheWeekStringMap.get(Calendar.FRIDAY)))
            markButtonSelected(fridayButton);
        if (mRepeatDays.contains(dayOfTheWeekStringMap.get(Calendar.SATURDAY)))
            markButtonSelected(saturdayButton);
        if (mRepeatDays.contains(dayOfTheWeekStringMap.get(Calendar.SUNDAY)))
            markButtonSelected(sundayButton);

        // Set the adapter of dropdown list items for the spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.reminder_times_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reminderSpinner.setAdapter(spinnerAdapter);

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


        // Set the on click listeners for the Date and Time EditTexts to launch date and time
        // pickers to allow the user to select a date and time for the first trip
        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePicker = new DatePickerFragment();
                //todo insert bundle params to set default date (use previously selected date if available)
                datePicker.show(getFragmentManager(), "datePicker");
            }
        });
        mTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePicker = new TimePickerFragment();
                //todo insert bundle params to set default time (use previously selected time if available)
                timePicker.show(getFragmentManager(), "timePicker");
            }
        });


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

        // Set the on click listeners for the day buttons
        mondayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        tuesdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        wednesdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        thursdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        fridayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        saturdayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());
        sundayButton.setOnClickListener(new DayOfWeekButtonOnClickListener());


        // Set the on click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check that the origin, destination, and first trip time are not null
                if (mOrigCoords == null) {
                    Toast.makeText(getContext(), "Please select a place of origin for the " +
                            "scheduled trip", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mDestCoords == null) {
                    Toast.makeText(getContext(), "Please select a destination for the " +
                            "scheduled trip", Toast.LENGTH_LONG).show();
                    return;
                }
                if (mFirstTripTime == null) {
                    Toast.makeText(getContext(), "Please select a valid date and time for the " +
                            "scheduled trip", Toast.LENGTH_LONG).show();
                    return;
                }

                // Calculate time of next trip & update mNextTripTime
                Long nextTripTime = calculateNextTripTime(mFirstTripTime, mRepeatDays);
                if (nextTripTime == null) // if no next trip, set mNextTripTime to null
                    mNextTripTime = null;
                else { // else initialize mNextTripTime if null and and update it
                    if (mNextTripTime == null)
                        mNextTripTime = Calendar.getInstance();
                    mNextTripTime.setTimeInMillis(nextTripTime);
                }

                // TODO: update/insert row in schedules table in database



                // Close EditScheduledTripFragment
                ((MainActivity) getActivity()).removeEditScheduledTripsFragment();
            }
        });

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

    /**
     * Helper method to calculate the time of the next trip in a schedule, based on the time
     * of the first trip in the schedule and the days of the week that the trip repeats on.
     * @param firstTripTime Calendar set to the date and time of the first trip in the schedule
     * @param repeatDays set of strings representing
     * @return the time of the next trip in the schedule, in milliseconds since epoch, or null
     *         if there is no next trip
     */
    private Long calculateNextTripTime(Calendar firstTripTime, Set<String> repeatDays) {

        Calendar now = Calendar.getInstance();

        if (now.before(firstTripTime)) {
            // If we haven't passed the time of the 1st trip yet, return the time of the 1st trip
            return now.getTimeInMillis();
        } else if (repeatDays.isEmpty()) {
            // If we HAVE passed the time of the 1st trip and the trip is not scheduled to repeat,
            // then there is no next trip
            return null;
        } else {
            // Start from the first trip time, and keep incrementing to the next day until we reach
            // the first repeat day
            Calendar nextTripTime = Calendar.getInstance();
            nextTripTime.setTime(firstTripTime.getTime());

            // Add 1 day at a time to the nextTripTime calendar until its day of the week is in
            // the set of repeat days for the trip schedule
            while (!repeatDays
                    .contains(dayOfTheWeekStringMap.get(nextTripTime.get(Calendar.DAY_OF_WEEK))))
                nextTripTime.add(Calendar.DATE, 1);

            // Return the time of the next trip
            return nextTripTime.getTimeInMillis();
        }

    }


    /**
     * Set the year, month, and day of  the first scheduled trip
     * @param year
     * @param month
     * @param day
     * @pre mFirstTripTime and mDateEditText have already been initialized in the OnCreateView()
     * method of this fragment
     */
    public void setDateAndText(int year, int month, int day) {

        // Save the date chosen by the user
        mFirstTripTime.set(Calendar.YEAR, year);
        mFirstTripTime.set(Calendar.MONTH, month);
        mFirstTripTime.set(Calendar.DAY_OF_MONTH, day);

        // Show the selected date in the edit screen
        mDateEditText.setText(new SimpleDateFormat("MM/dd/yy").format(mFirstTripTime));
    }

    /**
     * Set the hour of day and minute of the first scheduled trip
     * @param hourOfDay
     * @param minute
     * @pre mFirstTripTime and mTimeEditText have already been initialized in the OnCreateView()
     * method of this fragment
     */
    public void setTimeAndText(int hourOfDay, int minute) {

        // Save the time chosen by the user
        mFirstTripTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mFirstTripTime.set(Calendar.MINUTE, minute);

        // Show the selected time in the edit screen
        mTimeEditText.setText(new SimpleDateFormat("hh:mm").format(mFirstTripTime));
    }


}
