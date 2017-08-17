package edu.vanderbilt.isis.trip_planner_android_client.model.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Content provider class used for accessing & updating the TripPlanner database
 * through the query, insert, update, delete methods
 */
public class TripPlannerProvider extends ContentProvider {

    /** Log tag for log messages */
    private static final String TAG = TripPlannerProvider.class.getName();

    /** URI matcher code for the content URI for the trip plan history table */
    private static final int TRIP_PLAN_HISTORY_TABLE = 100;

    /** URI matcher code for the content URI for a single trip in the trip plan history table */
    private static final int TRIP_ID = 101;

    /** URI matcher code for the content URI for the schedule table */
    private static final int SCHEDULE_TABLE = 102;

    /** URI matcher code for the content URI for a trip plan schedule in the schedule table */
    private static final int SCHEDULE_ID = 103;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /** Static initializer. This is run the first time anything is called from this class. */
    static {
        // Add content URIs to URI matcher
        sUriMatcher.addURI(TripPlannerContract.CONTENT_AUTHORITY,
                TripPlannerContract.PATH_TRIP_PLAN_HISTORY, TRIP_PLAN_HISTORY_TABLE);
        sUriMatcher.addURI(TripPlannerContract.CONTENT_AUTHORITY,
                TripPlannerContract.PATH_TRIP_PLAN_HISTORY + "/#", TRIP_ID);
        sUriMatcher.addURI(TripPlannerContract.CONTENT_AUTHORITY,
                TripPlannerContract.PATH_SCHEDULE, SCHEDULE_TABLE);
        sUriMatcher.addURI(TripPlannerContract.CONTENT_AUTHORITY,
                TripPlannerContract.PATH_SCHEDULE + "/#", SCHEDULE_ID);
    }

    /**
     * Database helper for accessing the trip plan database
     */
    private TripPlannerDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a TripPlannerDbHelper object to gain access to the database
        mDbHelper = new TripPlannerDbHelper(getContext());
        return true;
    }


    /**
     * Perform the query for the given URI using the given projection, selection, selection
     * arguments, and sort order
     * @param uri the uri of the table or row on which we are operating
     * @param projection the columns to select from
     * @param selection condition for the selection, using ? where the values should go
     * @param selectionArgs the arguments to replace the ?s in the selection string
     * @param sortOrder the desired sort order for the rows returned in the cursor
     * @return a cursor object containing the results of the query
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Cursor to store the query result
        Cursor cursor;

        // See if the URI matcher can match the URI to one of the defined codes
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIP_PLAN_HISTORY_TABLE:

                // URI matches the TRIP_PLAN_HISTORY_TABLE code: query the trip plan history table
                // directly with the given projection, selection, selection arguments, and sort order.
                // The returned cursor may contain multiple rows of the trip plan history table.

                // Query the database using the db helper & get the cursor object.
                cursor = database.query(TripPlannerContract.TripPlanHistoryTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

                break;

            case TRIP_ID:

                // URI matches the TRIP_ID code: extract out the ID from the URI and set the
                // selection & selectionArgs arguments to query the table for a specific row.
                // The returned cursor will contain only one row of the trip plan history table.
                //
                // For example, for a URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3.

                selection = TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Query the database using the db helper & get the cursor object.
                cursor = database.query(TripPlannerContract.TripPlanHistoryTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

                break;

            case SCHEDULE_TABLE:

                // URI matches the SCHEDULE_TABLE code: query the schedule table directly with the
                // given projection, selection, selection arguments, and sort order.

                // Query the database using the db helper & get the cursor object.
                cursor = database.query(TripPlannerContract.ScheduleTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

                break;

            case SCHEDULE_ID:

                // URI matches the SCHEDULE_ID code: extract out the ID from the URI and set the
                // selection & selectionArgs arguments to query the table for a specific row.

                selection = TripPlannerContract.ScheduleTable.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Query the database using the db helper & get the cursor object.
                cursor = database.query(TripPlannerContract.ScheduleTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

                break;

            default:
                // If the URI does not match any of the defined codes, we cannot query
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    /**
     * Insert new row into the database at the given uri with the given content values
     * @param uri the uri of the table or row on which we are operating
     *
     * @param contentValues map holding the column values for the row to be inserted
     *                      (see TripPlannerContract for the values that need to be included and
     *                      non-null for each table)
     *
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // This will hold the id of the newly inserted row in the table
        long newRowId;

        // See if the URI matcher can match the URI to one of the defined codes
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIP_PLAN_HISTORY_TABLE:

                // Perform sanity check for the content values before inserting row
                // Throws IllegalArgumentException if one of the required content values is null
                sanityCheckTripPlanHistoryTableInsertion(contentValues);

                // Insert the new row into the trip plan history table in the trip planner database
                // using the db helper. Save the row ID of the newly inserted row.
                newRowId = database.insert(TripPlannerContract.TripPlanHistoryTable.TABLE_NAME, null,
                        contentValues);

                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (newRowId == -1) {
                    Log.e(TAG, "Failed to insert row for " + uri);
                    return null;
                }

                break;

            // Since it does not make sense to try to insert at the URI for a specific row,
            // the TRIP_ID code is not valid here

            case SCHEDULE_TABLE:

                // Perform sanity check for the content values before inserting row
                // Throws IllegalArgumentException if one of the required content values is null
                sanityCheckScheduleTableInsertion(contentValues);

                // Insert the new row into the schedule table in the trip planner database
                // using the db helper. Save the row ID of the newly inserted row.
                newRowId = database.insert(TripPlannerContract.ScheduleTable.TABLE_NAME, null,
                        contentValues);

                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (newRowId == -1) {
                    Log.e(TAG, "Failed to insert row for " + uri);
                    return null;
                }

                break;

            default:
                // If the URI does not match any valid codes, we cannot insert into the database
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }

        // Notify listeners that the data has been changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the URI of the newly inserted row
        return ContentUris.withAppendedId(uri, newRowId);

    }


    /**
     * Performs a sanity check on each of the required content values for an insertion into
     * the trip plan history table.
     * Should be called before inserting row. Will throw an IllegalArgumentException if the value
     * at one of the following keys in the given ContentValues is null:
     *
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_COORDINATES
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_COORDINATES
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_NAME
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_NAME
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_MODES
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TIMESTAMP
     *
     * @param contentValues the content values to check
     */
    private void sanityCheckTripPlanHistoryTableInsertion(ContentValues contentValues)
            throws IllegalArgumentException {

        // Check that the "from coordinates" column value is not null
        String fromCoords = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_FROM_COORDINATES);
        if (fromCoords == null)
            throw new IllegalArgumentException("Entry requires starting point coordinates");

        // Check that the "to coordinates" column value is not null
        String toCoords = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_TO_COORDINATES);
        if (toCoords == null)
            throw new IllegalArgumentException("Entry requires destination coordinates");

        // Check that the "from name" column value is not null
        String fromName = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_FROM_NAME);
        if (fromName == null)
            throw new IllegalArgumentException("Entry requires starting point name");

        // Check that the "to name" column value is not null
        String toName = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_TO_NAME);
        if (toName == null)
            throw new IllegalArgumentException("Entry requires destination name");

        // Check the the "modes" column value is not null
        String modes = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_MODES);
        if (modes == null)
            throw new IllegalArgumentException("Entry requires modes");

        // Check that the "timestamp" column value is not null
        Long timestamp = contentValues.getAsLong(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_TIMESTAMP);
        if (timestamp == null)
            throw new IllegalArgumentException("Entry requires a timestamp");

    }

    /**
     * Performs a sanity check on each of the required content values for an insertion into
     * the schedule table.
     * Should be called before inserting row. Will throw an IllegalArgumentException if the value
     * at one of the following keys in the given ContentValues is null:
     *
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_ID
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_NEXT_TRIP
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_REPEAT_DAYS
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_COORDINATES
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_COORDINATES
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_NAME
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_NAME
     *
     * @param contentValues the content values to check
     */
    private void sanityCheckScheduleTableInsertion(ContentValues contentValues)
            throws IllegalArgumentException {

        // Check that the "schedule id" column value is not null
        String scheduleId = contentValues.getAsString(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_ID);
        if (scheduleId == null)
            throw new IllegalArgumentException("Entry requires schedule id");

        // Check that the "time first trip" column value is not null
        String timeNextTrip = contentValues.getAsString(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_TIME_FIRST_TRIP);
        if (timeNextTrip == null)
            throw new IllegalArgumentException("Entry requires time of next trip");

        // Check that the "from coordinates" column value is not null
        String fromCoords = contentValues.getAsString(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_FROM_COORDINATES);
        if (fromCoords == null)
            throw new IllegalArgumentException("Entry requires starting point coordinates");

        // Check that the "to coordinates" column value is not null
        String toCoords = contentValues.getAsString(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_TO_COORDINATES);
        if (toCoords == null)
            throw new IllegalArgumentException("Entry requires destination coordinates");

        // Check that the "from name" column value is not null
        String fromName = contentValues.getAsString(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_FROM_NAME);
        if (fromName == null)
            throw new IllegalArgumentException("Entry requires starting point name");

        // Check that the "to name" column value is not null
        String toName = contentValues.getAsString(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_TO_NAME);
        if (toName == null)
            throw new IllegalArgumentException("Entry requires destination name");
    }


    /**
     * Updates the database at the given selection with the new ContentValues
     * @param uri the uri of the table or row on which we are operating
     * @param contentValues map holding the column values for the row to be updated
     *                      (see TripPlannerContract for the values that need to be non-null for
     *                      each table)
     * @param selection condition for the selection, using ? where the values should go
     * @param selectionArgs the arguments to replace the ?s in the selection string
     * @return the number of rows that were affected
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // To be used to store the number of rows affected
        int rows;

        // See if the URI matcher can match the URI to one of the defined codes
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIP_PLAN_HISTORY_TABLE:

                // The URI matches the TRIP_PLAN_HISTORY_TABLE code: update the given selection of
                // the trip plan history table directly.
                // May affect multiple rows in the trip plan history table.

                // Sanity check before writing update to the database
                // Throws IllegalArgumentException if one of the content values is invalid
                sanityCheckSearchHistoryTableUpdate(contentValues);

                // Update the trip plan history table through the db helper
                // Return the number of rows affected
                rows = database.update(TripPlannerContract.TripPlanHistoryTable.TABLE_NAME,
                        contentValues, selection, selectionArgs);

                break;

            case TRIP_ID:

                // The URI matches the TRIP_ID code: extract out the ID from the URI and
                // set the selection & selectionArgs to update a specific row
                selection = TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Sanity check before writing update to the database
                // Throws IllegalArgumentException if one of the content values is invalid
                sanityCheckSearchHistoryTableUpdate(contentValues);

                // Update the trip plan history table through the db helper
                // Return the number of rows affected (should be 1 in this case)
                rows =  database.update(TripPlannerContract.TripPlanHistoryTable.TABLE_NAME,
                        contentValues, selection, selectionArgs);

                break;

            case SCHEDULE_TABLE:

                // The URI matches the SCHEDULE_TABLE code: update the given selection of the
                // schedule table directly. May affect multiple rows in the schedule table.

                // Sanity check before writing update to the database
                // Throws IllegalArgumentException if one of the content values is invalid
                sanityCheckScheduleTableUpdate(contentValues);

                // Update the schedule table through the db helper
                // Return the number of rows affected
                rows = database.update(TripPlannerContract.ScheduleTable.TABLE_NAME,
                        contentValues, selection, selectionArgs);

                break;

            case SCHEDULE_ID:

                // The URI matches the SCHEDULE_ID code: extract out the ID from the URI and
                // set the selection & selectionArgs to update a specific row
                selection = TripPlannerContract.ScheduleTable.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Sanity check before writing update to the database
                // Throws IllegalArgumentException if one of the content values is invalid
                sanityCheckSearchHistoryTableUpdate(contentValues);

                // Update the schedule table through the db helper
                // Return the number of rows affected (should be 1 in this case)
                rows = database.update(TripPlannerContract.ScheduleTable.TABLE_NAME,
                        contentValues, selection, selectionArgs);

                break;

            default:
                // If the URI does not match any valid codes, we cannot update the database
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

        // Notify listeners that the data has been changed
        getContext().getContentResolver().notifyChange(uri, null);

        return rows;
    }


    /**
     * Performs a sanity check on each of the provided content values for an update to the
     * trip plan history table.
     * Should be called before updating the table. Will throw an IllegalArgumentException if one of
     * the following keys both *exists* in the given ContentValues AND is mapped to the value null.
     *
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_COORDINATES
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_COORDINATES
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_NAME
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_NAME
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_MODES
     * TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TIMESTAMP
     *
     * @param contentValues
     */
    private void sanityCheckSearchHistoryTableUpdate(ContentValues contentValues)
            throws IllegalArgumentException {

        // Check that the "from coordinates" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_FROM_COORDINATES)) {
            String name = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                    .COLUMN_NAME_FROM_COORDINATES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires starting point coordinates");
            }
        }

        // Check that the "to coordinates" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_TO_COORDINATES)) {
            String name = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                    .COLUMN_NAME_TO_COORDINATES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires destination coordinates");
            }
        }

        // Check that the "from name" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_FROM_NAME)) {
            String name = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                    .COLUMN_NAME_FROM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires starting point name");
            }
        }

        // Check that the "to name" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_TO_NAME)) {
            String name = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                    .COLUMN_NAME_TO_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires destination name");
            }
        }

        // Check that the "modes" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_MODES)) {
            String name = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                    .COLUMN_NAME_MODES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires modes");
            }
        }

        // Check that the "timestamp" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.TripPlanHistoryTable
                .COLUMN_NAME_TIMESTAMP)) {
            String name = contentValues.getAsString(TripPlannerContract.TripPlanHistoryTable
                    .COLUMN_NAME_TIMESTAMP);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires timestamp");
            }
        }

    }

    /**
     * Performs a sanity check on each of the provided content values for an update to the
     * schedule table.
     * Should be called before updating the table. Will throw an IllegalArgumentException if one of
     * the following keys both *exists* in the given ContentValues AND is mapped to the value null.
     *
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_ID
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_NEXT_TRIP
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_REPEAT_DAYS
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_COORDINATES
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_COORDINATES
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_NAME
     * TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_NAME
     *
     * @param contentValues
     */
    private void sanityCheckScheduleTableUpdate(ContentValues contentValues)
            throws IllegalArgumentException {

        // Check that the "schedule id" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.ScheduleTable.COLUMN_NAME_ID)) {
            String name = contentValues.getAsString(TripPlannerContract.ScheduleTable
                    .COLUMN_NAME_ID);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires schedule id");
            }
        }

        // Check that the "time first trip" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_TIME_FIRST_TRIP)) {
            String name = contentValues.getAsString(TripPlannerContract.ScheduleTable
                    .COLUMN_NAME_TIME_FIRST_TRIP);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires time of first trip");
            }
        }

        // Check that the "from coordinates" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_FROM_COORDINATES)) {
            String name = contentValues.getAsString(TripPlannerContract.ScheduleTable
                    .COLUMN_NAME_FROM_COORDINATES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires starting point coordinates");
            }
        }

        // Check that the "to coordinates" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_TO_COORDINATES)) {
            String name = contentValues.getAsString(TripPlannerContract.ScheduleTable
                    .COLUMN_NAME_TO_COORDINATES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires destination coordinates");
            }
        }

        // Check that the "from name" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_FROM_NAME)) {
            String name = contentValues.getAsString(TripPlannerContract.ScheduleTable
                    .COLUMN_NAME_FROM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires starting point name");
            }
        }

        // Check that the "to name" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.ScheduleTable
                .COLUMN_NAME_TO_NAME)) {
            String name = contentValues.getAsString(TripPlannerContract.ScheduleTable
                    .COLUMN_NAME_TO_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires destination name");
            }
        }

    }


    /**
     * Delete the data at the give selection in the database
     * @param uri the uri of the row or table we are deleting or deleting from
     * @param selection condition for the selection, using ? where the values should go
     * @param selectionArgs the arguments to replace the ?s in the selection string
     * @return the number of rows deleted if the last segment of the uri is a row ID or if
     * selection and selectionArgs are non-null, 0 otherwise
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // To be used to store the number of rows deleted
        int rows;

        // See if the URI matcher can match the URI to one of the defined codes
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIP_PLAN_HISTORY_TABLE:

                // The URI matches the TRIP_PLAN_HISTORY_TABLE code: directly delete the given selection
                // in the trip plan history table and return the number of rows deleted
                rows = database.delete(TripPlannerContract.TripPlanHistoryTable.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case TRIP_ID:

                // The URI matches the TRIP_ID code: extract out the ID from the URI and
                // set the selection & selectionArgs to update a specific row
                selection = TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Delete the row from the trip plan history table, should return 1
                rows = database.delete(TripPlannerContract.TripPlanHistoryTable.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case SCHEDULE_TABLE:

                // The URI matches the SCHEDULE_TABLE code: directly delete the given selection
                // in the schedule table and return the number of rows deleted
                rows = database.delete(TripPlannerContract.ScheduleTable.TABLE_NAME,
                        selection, selectionArgs);
                break;

            case SCHEDULE_ID:

                // The URI matches the SCHEDULE_ID code: extract out the ID from the URI and
                // set the selection & selectionArgs to update a specific row
                selection = TripPlannerContract.ScheduleTable.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Delete the row from the schedule table, should return 1
                rows = database.delete(TripPlannerContract.ScheduleTable.TABLE_NAME,
                        selection, selectionArgs);
                break;

            default:
                // If the URI does not match any valid codes, we cannot delete from the database
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

        // Notify listeners that the data has been changed
        getContext().getContentResolver().notifyChange(uri, null);

        return rows;
    }

    /**
     * Returns the MIME type of data for the content URI
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIP_PLAN_HISTORY_TABLE:
                return TripPlannerContract.TripPlanHistoryTable.CONTENT_LIST_TYPE;
            case TRIP_ID:
                return TripPlannerContract.TripPlanHistoryTable.CONTENT_ITEM_TYPE;
            case SCHEDULE_TABLE:
                return TripPlannerContract.ScheduleTable.CONTENT_LIST_TYPE;
            case SCHEDULE_ID:
                return TripPlannerContract.ScheduleTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
