package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import edu.vanderbilt.isis.trip_planner_android_client.R;
import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;

/**
 * CursorAdapter to populate the list of scheduled trips in the SCHEDULES screen of the Activity
 * with CardViews. Each view shows info about the trip schedule that it represents.
 */
public class ScheduledTripsCursorAdapter extends CursorAdapter {

    // Reference to the main activity
    MainActivity activity;

    /**
     * Construct a new {@link ScheduledTripsCursorAdapter}
     * @param activity the MainActivity
     * @param cursor the cursor from which to get the data
     */
    public ScheduledTripsCursorAdapter(MainActivity activity, Cursor cursor) {
        this(activity.getApplicationContext(), cursor);
        this.activity = activity;
    }

    /**
     * Construct a new {@link ScheduledTripsCursorAdapter}
     * @param context the context
     * @param cursor the cursor from which to get the data
     */
    private ScheduledTripsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }


    /**
     * Inflate a new view and return it
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already
     *               moved to the correct position.
     * @param parent The parent to which the new view is attached to
     * @return the newly create list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a schedule list item using the layout specified in search_list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.scheduled_trip_list_item, parent, false);
    }

    /**
     * Binds data to the given list item view
     * @param view existing list item view, returned earlier by newView()
     * @param context app context
     * @param cursor The cursor from which to get the data. The cursor is already
     *               moved to the correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Find the views we want to modify in the list item layout
        TextView destinationTextView = (TextView) view.findViewById(R.id.destination);
        TextView originTextView = (TextView) view.findViewById(R.id.origin);
        TextView nextTripTextView = (TextView) view.findViewById(R.id.next_trip);
        TextView reminderTextView = (TextView) view.findViewById(R.id.reminder);
        TextView repeatTextView = (TextView) view.findViewById(R.id.repeats);
        ImageView goButton = (ImageView) view.findViewById(R.id.go_button);
        ImageView editButton = (ImageView) view.findViewById(R.id.edit_button);

        // Find the indices of the columns in the cursor that we're interested in
        int idIndex = cursor.getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_ID);
        int destinationNameIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_NAME);
        final int destinationAddressIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_ADDRESS);
        int destinationLocationIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_COORDINATES);
        int originNameIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_NAME);
        int originAddressIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_ADDRESS);
        int originLocationIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_COORDINATES);
        int nextTripIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_NEXT_TRIP);
        int firstTripIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_FIRST_TRIP);
        int reminderIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_REMINDER_TIME);
        int repeatIndex = cursor
                .getColumnIndex(TripPlannerContract.ScheduleTable.COLUMN_NAME_REPEAT_DAYS);

        // Read the column values from the cursor, checking for nullity if necessary
        final int tripScheduleId = cursor.getInt(idIndex);
        final long timeOfFirstTrip = cursor.getLong(firstTripIndex);

        final String destinationName = cursor.getString(destinationNameIndex);
        final String originName = cursor.getString(originNameIndex);

        String destinationLocation = cursor.getString(destinationLocationIndex);
        String[] destinationLatAndLon = destinationLocation.split(",");
        final double destinationLat = Double.parseDouble(destinationLatAndLon[0]);
        final double destinationLon = Double.parseDouble(destinationLatAndLon[1]);
        String originLocation = cursor.getString(originLocationIndex);
        String[] originLatAndLon = originLocation.split(",");
        final double originLat = Double.parseDouble(originLatAndLon[0]);
        final double originLon = Double.parseDouble(originLatAndLon[1]);

        String destinationAddress = null;
        if (!cursor.isNull(destinationAddressIndex))
            destinationAddress= cursor.getString(destinationAddressIndex);
        final String finalDestinationAddress = destinationAddress;

        String originAddress = null;
        if (!cursor.isNull(originAddressIndex))
            originAddress = cursor.getString(originAddressIndex);
        final String finalOriginAddress = originAddress;

        Long timeOfNextTrip = null;
        if (!cursor.isNull(nextTripIndex))
            timeOfNextTrip = cursor.getLong(nextTripIndex);
        final Long finalTimeOfNextTrip = timeOfNextTrip;

        Integer reminderMinutes = null;
        if (!cursor.isNull(reminderIndex))
            reminderMinutes = cursor.getInt(reminderIndex);
        final Integer finalReminderMinutes = reminderMinutes;

        String repeatDays = null;
        if (!cursor.isNull(repeatIndex))
            repeatDays = cursor.getString(repeatIndex);
        final String finalRepeatDays = repeatDays;


        // Fill in the TextViews

        // Fill in the destination text view
        destinationTextView.setText(destinationName);

        // Fill in the origin text view
        originTextView.setText(originName);

        // Fill in the reminder text view
        if (reminderMinutes != null)
            reminderTextView.setText("Reminder: " + reminderMinutes + " minutes before");

        // Fill in the repeat days text view
        if (repeatDays != null) {
            String repeatText = "Repeats " + repeatDays;
            repeatTextView.setText(repeatText);
        }

        // Fill in the date/time of next trip text view
        if (timeOfNextTrip != null) {

            // Calendar object to represent the time of next trip
            Calendar nextTrip = Calendar.getInstance();
            nextTrip.setTimeInMillis(timeOfNextTrip);

            // Calendar object to represent today
            Calendar today = Calendar.getInstance();

            // Calendar object to represent tomorrow
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DATE, 1);

            // String to represent the date
            String dateString;

            if (isSameDay(nextTrip, today)) // use "Today" if time next trip is today
                dateString = "Today";
            else if (isSameDay(nextTrip, tomorrow)) // use "Tomorrow" if time next trip is tomorrow
                dateString = "Tomorrow";
            else { // use MM/dd/yy otherwise
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                dateString = dateFormat.format(nextTrip.getTime());
            }

            // String to represent the time
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm aa");
            String timeString = timeFormat.format(nextTrip.getTime());

            // Fill in the TextView showing the date and time of the next trip
            nextTripTextView.setText("Next trip: " + dateString + " " + timeString);
        }

        // Set the on click listener for the "edit" button in the list item view
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create bundle to pass in arguments to the fragment
                Bundle bundle = new Bundle();
                bundle.putBoolean(EditScheduledTripFragment.IS_EXISTING_SCHEDULE, true);
                bundle.putInt(EditScheduledTripFragment.SCHEDULE_ID, tripScheduleId);
                bundle.putString(EditScheduledTripFragment.DESTINATION_NAME, destinationName);
                bundle.putString(EditScheduledTripFragment.ORIGIN_NAME, originName);
                bundle.putDouble(EditScheduledTripFragment.DESTINATION_LAT, destinationLat);
                bundle.putDouble(EditScheduledTripFragment.DESTINATION_LON, destinationLon);
                bundle.putDouble(EditScheduledTripFragment.ORIGIN_LAT, originLat);
                bundle.putDouble(EditScheduledTripFragment.ORIGIN_LON, originLon);
                bundle.putDouble(EditScheduledTripFragment.FIRST_TRIP_TIME, timeOfFirstTrip);
                if (finalOriginAddress != null)
                    bundle.putString(EditScheduledTripFragment.ORIGIN_ADDRESS, finalOriginAddress);
                if (finalDestinationAddress != null)
                    bundle.putString(EditScheduledTripFragment.DESTINATION_ADDRESS, finalDestinationAddress);
                if (finalTimeOfNextTrip != null)
                    bundle.putLong(EditScheduledTripFragment.NEXT_TRIP_TIME, finalTimeOfNextTrip);
                if (finalReminderMinutes != null)
                    bundle.putInt(EditScheduledTripFragment.REMINDER_MINS, finalReminderMinutes);
                if (finalRepeatDays != null)
                    bundle.putString(EditScheduledTripFragment.REPEAT_DAYS, finalRepeatDays);

                // Launch the EditScheduledTripFragment to edit the scheduled trip whose
                // CardView was clicked
                activity.launchEditScheduledTripFragment(bundle);
            }
        });

        // TODO: Set the on click listener for the "go" button in the list item view
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the trip plan, figure out how to transition to the TRIP_PLAN screen

            }
        });

    }

    /**
     * Helper method to determine if two calendar dates are on the same day
     * @param a one of the dates
     * @param b the other date
     * @return if a and b are the same day
     */
    private boolean isSameDay(Calendar a, Calendar b) {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
                b.get(Calendar.MONTH) == b.get(Calendar.MONTH) &&
                a.get(Calendar.DAY_OF_MONTH) == b.get(Calendar.DAY_OF_MONTH);
    }
}
