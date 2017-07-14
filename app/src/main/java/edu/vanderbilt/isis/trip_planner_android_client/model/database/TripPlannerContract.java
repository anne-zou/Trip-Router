package edu.vanderbilt.isis.trip_planner_android_client.model.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Anne on 6/29/2017.
 */

/**
 * Contract for the content provider of the trip planner database.
 */
public final class TripPlannerContract {

    /** Content authority (identifier for the content provider) */
    public static final String CONTENT_AUTHORITY =
            "edu.vanderbilt.isis.trip_planner_android_client";

    /** The base of all URIs which apps will use to contact the content provider */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path to the search_history table (appended to base content URI for possible URI's)
     * For instance, content://edu.vanderbilt.isis.trip_planner_android_client/search_history
     */
    public static final String PATH_SEARCH_HISTORY = "search_history";

    private TripPlannerContract() {} // private constructor to prevent instantiation of class

    /**
     * Inner class that defines constant values for the search history database table.
     * Each entry in the table represents a time the user requested a trip plan.
     */
    public static class SearchHistoryTable implements BaseColumns {

        /** The content URI to access the search history data in the provider */
        public static final Uri CONTENT_URI = Uri
                .withAppendedPath(BASE_CONTENT_URI, PATH_SEARCH_HISTORY);

        /** Name of database table for search history */
        public final static String TABLE_NAME = "search_history";

        /** The MIME type of the {@link #CONTENT_URI} for a list of search entries. */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_HISTORY;

        /** The MIME type of the {@link #CONTENT_URI} for a single search entry. */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_HISTORY;

        /**
         * Unique ID number for the trip plan request (only for use in the database table)
         *
         * Required to be non-null in table entry: YES
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Time at which the request was made, in seconds since epoch
         *
         * Required to be non-null in table entry: YES
         * Type: INTEGER
         */
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";

        /**
         * Coordinates of the starting point for the trip
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: two floating point values between -180 and 180 separated by a single comma with
         * no spaces or parentheses, e.g. "36.16589,-86.78444"
         */
        public static final String COLUMN_NAME_FROM_COORDINATES = "from_coords";

        /**
         * Coordinates of the destination for the trip
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: two floating point values between -180 and 180 separated by a single comma with
         * no spaces or parentheses, e.g. "36.16589,-86.78444"
         */
        public static final String COLUMN_NAME_TO_COORDINATES = "to_coords";

        /**
         * Name of the starting point for the trip (e.g. "Vanderbilt University")
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_FROM_NAME = "from_name";

        /**
         * Name of the destination for the trip
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_TO_NAME = "to_name";

        /**
         * Address of the starting point for the trip
         *
         * Required to be non-null in table entry: NO
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_FROM_ADDRESS = "from_address";

        /**
         * Address of the destination for the trip
         *
         * Required to be non-null in table entry: NO
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_TO_ADDRESS = "to_address";

        /**
         * List of modes specified for the trip
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: a comma-separated list of strings representing TraverseModes with no spaces,
         * e.g. "BUS", "BUS,BICYCLE" , "WALK,CAR,BUS"
         * (see the static initializer in ModeUtil.java for a complete list of valid strings)
         */
        public static final String COLUMN_NAME_MODES = "modes";

    }

}
