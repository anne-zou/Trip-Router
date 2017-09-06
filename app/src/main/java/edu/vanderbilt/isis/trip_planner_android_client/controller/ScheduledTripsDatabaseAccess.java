package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Set;

import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;

/**
 * Created by Anne on 7/20/2017.
 */

public class ScheduledTripsDatabaseAccess {

    private ScheduledTripsDatabaseAccess() {} // private constructor to prevent instantiation

    /**
     * Get a row by its id from the scheduled trips table in the database.
     * Use this to get the info to plan the trip when the user selects "Go" on a scheduled trip.
     * @param rowId the id of the trip schedule in the table
     * @return the cursor containing the row
     */
    static Cursor queryRowInSchedulesTable(int rowId) {
        // TODO query the table and return a cursor
        return null;
    }

    /**
     * Update an existing schedule by its id, or insert a new schedule into the scheduled trips
     * table in the database
     * @param rowId id of the schedule to update; insert new row if null
     * @param timeFirstTrip the time of the first trip in milliseconds sine epoch
     * @param timeNextTrip the calculated time of the next upcoming trip in milliseconds since epoch
     * @param reminderTime the number of minutes before each trip the user is to be given a reminder
     * @param repeatDays String of space-separated abbreviations representing the days of the week
     *                   the trip is to repeat on, occurring in the order that the days occur in
     *                   the week, Monday being the 1st day. The abbreviations are M T W Th F Sa Su.
     * @param fromCoords the coordinates of the trip origin
     * @param toCoords the coordinates of the trip destination
     * @param fromName the name of the trip origin
     * @param toName the name of the trip destination
     * @param fromAddr the address of the trip origin
     * @param toAddr the address of the trip destination
     * @return the Uri of the newly inserted row
     */
    static void updateOrInsertRowInSchedulesTable(Context context, Long rowId,
                                                 @NonNull Long timeFirstTrip, Long timeNextTrip,
                                                 Integer reminderTime, String repeatDays,
                                                 @NonNull LatLng fromCoords, @NonNull LatLng toCoords,
                                                 @NonNull String fromName, @NonNull String toName,
                                                 String fromAddr, String toAddr) {

        // Create ContentValues object to update the row in the schedules table
        ContentValues values = new ContentValues();
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_FIRST_TRIP, timeFirstTrip);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_NEXT_TRIP, timeNextTrip);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_REMINDER_TIME, reminderTime);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_REPEAT_DAYS, repeatDays);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_COORDINATES,
                fromCoords.latitude + "," + fromCoords.longitude);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_COORDINATES,
                toCoords.latitude + "," + toCoords.longitude);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_NAME, fromName);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_NAME, toName);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_ADDRESS, fromAddr);
        values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_ADDRESS, toAddr);

        // Was an existing row id passed in, or do we insert a new row into the schedules table?
        if (rowId == null) {
            // Insert new schedule into database

            // The ContentResolver will use the URI parameter to location the correct content provider
            // (which in this case is the TripPlannerProvider) and call the insert() method on it,
            // inserting a new row into the table and returning the URI of the new row.
            context.getContentResolver()
                    .insert(TripPlannerContract.ScheduleTable.CONTENT_URI, values);
        } else {
            // Update an existing schedule in the database

            // Add the row id to the content values
            values.put(TripPlannerContract.ScheduleTable.COLUMN_NAME_ID, rowId);

            // Get the uri for the row in the schedule table we want to update
            Uri rowUri = Uri.withAppendedPath(TripPlannerContract.ScheduleTable.CONTENT_URI,
                    "/" + rowId);
            // Update the schedule
            context.getContentResolver().update(rowUri, values, null, null);
        }

    }


}
