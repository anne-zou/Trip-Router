package edu.vanderbilt.isis.trip_planner_android_client.controller;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import edu.vanderbilt.isis.trip_planner_android_client.model.database.TripPlannerContract;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anne on 7/5/2017.
 */

public class SearchHistoryDatabaseService {

    private SearchHistoryDatabaseService() {}

    /**
     * Add a trip to the search history table in the trip planner database
     * @param context used for getting the content resolver
     * @param fromName name of the from location
     * @param toName name of the to location
     * @param fromCoords the from location
     * @param toCoords the to location
     * @param modes string representing the selected modes
     * @param timeStamp time that the search was made, in seconds since epoch
     * @return
     */
    static Uri addToSearchHistory(Context context, String fromName, String toName,
                                  LatLng fromCoords, LatLng toCoords,
                                  String modes, long timeStamp) {

        // Create a ContentValues object specifying the value of each column in the row that is
        // to be inserted into the table:
        ContentValues values = new ContentValues();
        values.put(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_NAME, fromName);
        values.put(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME, toName);
        values.put(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES,
                Double.toString(fromCoords.latitude) + "," + Double.toString(fromCoords.longitude));
        values.put(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES,
                Double.toString(toCoords.latitude) + "," + Double.toString(toCoords.longitude));
        values.put(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_MODES, modes);
        values.put(TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TIMESTAMP, timeStamp);

        // Insert the new row into the table.
        // The ContentResolver will use the URI parameter to find the correct content provider
        // (which in this case is the TripPlannerProvider) and call the insert() method on it,
        // inserting a new row into the table and returning the URI of the new row.
        return context.getContentResolver()
                .insert(TripPlannerContract.SearchHistoryTable.CONTENT_URI, values);

    }

}
