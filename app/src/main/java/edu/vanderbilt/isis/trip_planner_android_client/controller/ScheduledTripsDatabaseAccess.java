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
        // todo query the table and return a cursor
        return null;
    }

    /**
     * Update an existing schedule by its id, or insert a new schedule into the scheduled trips
     * table in the database
     * @param rowId id of the schedule to update; insert new row if null
     * @param timeFirstTrip the time of the first trip in milliseconds sine epoch
     * @param timeNextTrip the calculated time of the next upcoming trip in milliseconds since epoch
     * @param reminderTime the number of minutes before each trip the user is to be given a reminder
     * @param repeatDays the days of the week the trip is to repeat on, represented by the Strings
     *                   M T W Th F Sa Su
     * @param fromCoords the coordinates of the trip origin
     * @param toCoords the coordinates of the trip destination
     * @param fromName the name of the trip origin
     * @param toName the name of the trip destination
     * @param fromAddr the address of the trip origin
     * @param toAddr the address of the trip destination
     * @return the Uri of the newly inserted row
     */
    static Uri updateOrInsertRowInSchedulesTable(Context context, Integer rowId,
                                                 @NonNull Long timeFirstTrip, Long timeNextTrip,
                                                 Integer reminderTime, Set<String> repeatDays,
                                                 @NonNull LatLng fromCoords, @NonNull LatLng toCoords,
                                                 @NonNull String fromName, @NonNull String toName,
                                                 String fromAddr, String toAddr) {
        // todo gotta do
        ContentValues values = new ContentValues();

        // Insert the new row into the table.
        // The ContentResolver will use the URI parameter to location the correct content provider
        // (which in this case is the TripPlannerProvider) and call the insert() method on it,
        // inserting a new row into the table and returning the URI of the new row.
        return context.getContentResolver()
                .insert(TripPlannerContract.TripPlanHistoryTable.CONTENT_URI, values);
    }


}
