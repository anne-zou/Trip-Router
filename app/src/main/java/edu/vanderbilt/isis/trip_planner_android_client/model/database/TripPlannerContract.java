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
     * Path to the trip plan history table (appended to base content URI for possible URI's)
     * For instance, content://edu.vanderbilt.isis.trip_planner_android_client/trip_plan_history
     */
    public static final String PATH_TRIP_PLAN_HISTORY = "trip_plan_history";

    /**
     * Path to the schedule table (appended to base content URI for possible URI's)
     * For instance, content://edu.vanderbilt.isis.trip_planner_android_client/schedule
     */
    public static final String PATH_SCHEDULE = "schedule";

    private TripPlannerContract() {} // private constructor to prevent instantiation of class

    /**
     * Inner class that defines constant values for the trip plan history table.
     * Each entry in the table represents a time the user requested a trip plan.
     */
    public static class TripPlanHistoryTable implements BaseColumns {

        /** The content URI to access the trip plan history data in the provider */
        public static final Uri CONTENT_URI = Uri
                .withAppendedPath(BASE_CONTENT_URI, PATH_TRIP_PLAN_HISTORY);

        /** Name of database table for trip plan history */
        public final static String TABLE_NAME = "trip_plan_history";

        /** The MIME type of the {@link #CONTENT_URI} for a list of trip plan entries. */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIP_PLAN_HISTORY;

        /** The MIME type of the {@link #CONTENT_URI} for a single trip plan entry. */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_TRIP_PLAN_HISTORY;

        /**
         * Unique ID number for the trip plan request
         *
         * Required to be non-null in table entry: YES
         * Type: INTEGER
         */
        public final static String COLUMN_NAME_ID = BaseColumns._ID;

        /**
         * Time at which the request was made, in milliseconds since epoch
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

    /**
     * Inner class that defines constant values for the schedule table.
     * Each entry in the table represents a time the user requested a trip plan.
     */
    public static class ScheduleTable {

        /** The content URI to access the trip pln schedule data in the provider */
        public static final Uri CONTENT_URI = Uri
                .withAppendedPath(BASE_CONTENT_URI, PATH_SCHEDULE);

        /** Name of database table for schedule */
        public final static String TABLE_NAME = "schedule";

        /** The MIME type of the {@link #CONTENT_URI} for a list of schedule entries. */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;

        /** The MIME type of the {@link #CONTENT_URI} for a single schedule entry. */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_SCHEDULE;

        /**
         * Unique ID number of the scheduled trip plan
         *
         * Required to be non-null in table entry: YES
         * Type: INTEGER
         */
        public static final String COLUMN_NAME_ID = BaseColumns._ID;

        /**
         * Time of the first trip set for the scheduled trip plan, in milliseconds since epoch
         *
         * Required to be non-null in table entry: YES
         * Type: INTEGER
         */
        public static final String COLUMN_NAME_TIME_FIRST_TRIP = "time_first_trip";

        /**
         * Time of the next trip for the scheduled trip plan (may be a recurring trip plan), in
         * milliseconds since epoch
         *
         * Required to be non-null in table entry: NO
         * Type: INTEGER
         */
        public static final String COLUMN_NAME_TIME_NEXT_TRIP = "time_next_trip";

        /**
         * Number of minutes before the next trip at which the user is to be notified by a reminder
         *
         * Required to be non-null in table entry: NO
         * Type: INTEGER
         */
        public static final String COLUMN_NAME_REMINDER_TIME = "reminder_time";

        /**
         * Days of the week that the trip is to recur on
         *
         * Required to be non-null in table entry: NO
         * Type: STRING
         * Format: String of space-separated abbreviations representing the selected days of the
         * week, occurring in the order that the days occur in the week, Monday being the 1st day.
         * The abbreviations are M T W Th F Sa Su.
         * e.g. "M W F" or "T Th" or "M W Sa"
         */
        public static final String COLUMN_NAME_REPEAT_DAYS = "repeat_days";

        /**
         * Coordinates of the starting point for the scheduled trip
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: two floating point values between -180 and 180 separated by a single comma (with
         * no spaces or parentheses), e.g. "36.16589,-86.78444"
         */
        public static final String COLUMN_NAME_FROM_COORDINATES = "from_coords";

        /**
         * Coordinates of the destination for the scheduled trip
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: two floating point values between -180 and 180 separated by a single comma with
         * no spaces or parentheses, e.g. "36.16589,-86.78444"
         */
        public static final String COLUMN_NAME_TO_COORDINATES = "to_coords";

        /**
         * Name of the starting point for the scheduled trip (e.g. "Vanderbilt University")
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_FROM_NAME = "from_name";

        /**
         * Name of the destination for the scheduled trip
         *
         * Required to be non-null in table entry: YES
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_TO_NAME = "to_name";

        /**
         * Address of the starting point for the scheduled trip
         *
         * Required to be non-null in table entry: NO
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_FROM_ADDRESS = "from_address";

        /**
         * Address of the destination for the scheduled trip
         *
         * Required to be non-null in table entry: NO
         * Type: TEXT
         * Format: can be any string
         */
        public static final String COLUMN_NAME_TO_ADDRESS = "to_address";



    }

}
