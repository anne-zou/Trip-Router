package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 7/5/2017.
 */

public class TripPlanHistoryDatabaseAccess {

    private TripPlanHistoryDatabaseAccess() {} // private constructor to prevent instantiation


    // ADD FUNCTIONS FOR DATABASE OPERATIONS IN THIS CLASS AS NEEDED

    /**
     * Insert a new entry into the trip plan history table in the trip planner database
     * @param context app context, used for getting the content resolver
     * @param fromName name of the from location
     * @param toName name of the to location
     * @param fromCoords the from location
     * @param toCoords the to location
     * @param modes string representing the selected modes
     * @param timeStamp time that the search was made, in seconds since epoch
     * @return the Uri of the newly inserted row
     */
    static Uri insertIntoTripPlanHistoryTable(Context context, String fromName, String toName,
                                              LatLng fromCoords, LatLng toCoords,
                                              String fromAddress, String toAddress,
                                              String modes, long timeStamp) {

        // Create a ContentValues object and insert the fromName, toName, fromCoords, toCoords,
        // modes, and timeStamp in their respective appropriate formats for insertion into the
        // search history table in the trip planner database
        ContentValues values = new ContentValues();
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_NAME, fromName);
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_NAME, toName);
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_COORDINATES,
                Double.toString(fromCoords.latitude) + "," + Double.toString(fromCoords.longitude));
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_COORDINATES,
                Double.toString(toCoords.latitude) + "," + Double.toString(toCoords.longitude));
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_ADDRESS, fromAddress);
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_ADDRESS, toAddress);
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_MODES, modes);
        values.put(TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TIMESTAMP, timeStamp);

        // Insert the new row into the table.
        // The ContentResolver will use the URI parameter to location the correct content provider
        // (which in this case is the TripPlannerProvider) and call the insert() method on it,
        // inserting a new row into the table and returning the URI of the new row.
        return context.getContentResolver()
                .insert(TripPlannerContract.TripPlanHistoryTable.CONTENT_URI, values);

    }

}
