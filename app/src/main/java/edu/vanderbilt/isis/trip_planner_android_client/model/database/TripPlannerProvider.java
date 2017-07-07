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

    /** URI matcher code for the content URI for the search history table */
    private static final int SEARCHES_TABLE = 100;

    /** URI matcher code for the content URI for a single trip plan in the search history table */
    private static final int SEARCH_ID = 101;

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
                TripPlannerContract.PATH_SEARCH_HISTORY, SEARCHES_TABLE);
        sUriMatcher.addURI(TripPlannerContract.CONTENT_AUTHORITY,
                TripPlannerContract.PATH_SEARCH_HISTORY + "/#", SEARCH_ID);
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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // See if the URI matcher can match the URI to one of the defined codes
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCHES_TABLE:

                // URI matches the SEARCHES_TABLE code: query the search history table
                // directly with the given projection, selection, selection arguments, and sort order.
                // The returned cursor may contain multiple rows of the search history table.

                // Query the database using the db helper & return the cursor object.
                return database.query(TripPlannerContract.SearchHistoryTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

            case SEARCH_ID:

                // URI matches the SEARCH_ID code: extract out the ID from the URI and
                // set the selection & selectionArgs arguments to query the table for a specific row
                //
                // For example, for a URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3.

                selection = TripPlannerContract.SearchHistoryTable._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Query the database using the db helper & return the cursor object.
                return database.query(TripPlannerContract.SearchHistoryTable.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

            default:
                // If the URI does not match any of the defined codes, we cannot query
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

    }


    /**
     * Insert new row into the database at the given uri with the given content values
     * @param uri the uri of the table or row on which we are operating
     *
     * @param contentValues map holding the column values for the row to be inserted,
     *                      MUST have non-null values for the following keys:
     *                      TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES
     *                      TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES
     *                      TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_NAME
     *                      TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME
     *                      TripPlannerContract.SearchHistoryTable.COLUMN_NAME_MODES
     *                      TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TIMESTAMP
     *
     *                      See TripPlannerContract.java for details on the formatting of each
     *                      column value.
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
            case (SEARCHES_TABLE):

                // Perform sanity check for the content values before inserting row
                // Throws IllegalArgumentException if one of the required content values is null
                sanityCheckSearchHistoryTableInsertion(contentValues);

                // Insert the new row into the search history table in the trip planner database
                // using the db helper. Save the row ID of the newly inserted row.
                newRowId = database.insert(TripPlannerContract.SearchHistoryTable.TABLE_NAME, null,
                        contentValues);

                // If the ID is -1, then the insertion failed. Log an error and return null.
                if (newRowId == -1) {
                    Log.e(TAG, "Failed to insert row for " + uri);
                    return null;
                }

                break;

            // Since it does not make sense to try to insert at the URI for a specific row,
            // the SEARCH_ID code is not valid here

            default:
                // If the URI does not match any valid codes, we cannot insert into the database
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }

        // Return the URI of the newly inserted row
        return ContentUris.withAppendedId(uri, newRowId);

    }


    /**
     * Performs a sanity check on each of the required content values for an insertion into
     * the search history table.
     * Should be called before inserting row into the search history table witht db helper,
     * will throw an IllegalArgumentException if the value at one of the following keys
     * in the given ContentValues is null:
     *
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_NAME
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_MODES
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TIMESTAMP
     *
     * @param contentValues
     */
    private void sanityCheckSearchHistoryTableInsertion(ContentValues contentValues)
            throws IllegalArgumentException {

        // Check that the "from coordinates" column value is not null
        String fromCoords = contentValues.getAsString(TripPlannerContract
                .SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES);
        if (fromCoords == null)
            throw new IllegalArgumentException("Entry requires starting point " +
                    "coordinates");

        // Check that the "to coordinates" column value is not null
        String toCoords = contentValues.getAsString(TripPlannerContract
                .SearchHistoryTable.COLUMN_NAME_TO_COORDINATES);
        if (toCoords == null)
            throw new IllegalArgumentException("Entry requires destination " +
                    "coordinates");

        // Check that the "from name" column value is not null
        String fromName = contentValues.getAsString(TripPlannerContract
                .SearchHistoryTable.COLUMN_NAME_FROM_NAME);
        if (fromName == null)
            throw new IllegalArgumentException("Entry requires starting point name");

        // Check that the "to name" column value is not null
        String toName = contentValues.getAsString(TripPlannerContract
                .SearchHistoryTable.COLUMN_NAME_TO_NAME);
        if (toName == null)
            throw new IllegalArgumentException("Entry requires destination name");

        // Check the the "modes" column value is not null
        String modes = contentValues.getAsString(TripPlannerContract
                .SearchHistoryTable.COLUMN_NAME_MODES);
        if (modes == null)
            throw new IllegalArgumentException("Entry requires modes");

        // Check that the "timestamp" column value is not null
        Long timestamp = contentValues.getAsLong(TripPlannerContract
                .SearchHistoryTable.COLUMN_NAME_TIMESTAMP);
        if (timestamp == null)
            throw new IllegalArgumentException("Entry requires a timestamp");

    }


    /**
     * Updates the database at the given selection with the new ContentValues
     * @param uri the uri of the table or row on which we are operating
     * @param contentValues map holding the column values for the row to be updated
     * @param selection condition for the selection, using ? where the values should go
     * @param selectionArgs the arguments to replace the ?s in the selection string
     * @return the number of rows that were affected
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // See if the URI matcher can match the URI to one of the defined codes
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCHES_TABLE:

                // The URI matches the SEARCHES_TABLE code: update the given selection of the
                // search history table directly.
                // May affect multiple rows in the search history table.

                // Sanity check before writing update to the database
                // Throws IllegalArgumentException if one of the content values is invalid
                sanityCheckSearchHistoryTableUpdate(contentValues);

                // Update the search history table through the db helper
                // Return the number of rows affected
                return database.update(TripPlannerContract.SearchHistoryTable.TABLE_NAME,
                        contentValues, selection, selectionArgs);

            case SEARCH_ID:

                // The URI matches the SEARCH_ID code: extract out the ID from the URI and
                // set the selection & selectionArgs to update a specific row
                selection = TripPlannerContract.SearchHistoryTable._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Sanity check before writing update to the database
                // Throws IllegalArgumentException if one of the content values is invalid
                sanityCheckSearchHistoryTableUpdate(contentValues);

                // Update the search history table through the db helper
                // Return the number of rows affected (should be 1 in this case)
                return database.update(TripPlannerContract.SearchHistoryTable.TABLE_NAME,
                        contentValues, selection, selectionArgs);

            default:
                // If the URI does not match any valid codes, we cannot update the database
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    /**
     * Performs a sanity check on each of the provided content values for an update to the
     * search history table.
     * Should be called before updating the search history table through the db helper,
     * will throw an IllegalArgumentException if one of the following keys both *exists* in the
     * given ContentValues AND is mapped to the value null
     *
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_NAME
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_MODES
     * TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TIMESTAMP
     *
     * @param contentValues
     */
    private void sanityCheckSearchHistoryTableUpdate(ContentValues contentValues)
            throws IllegalArgumentException {

        // Check that the "from coordinates" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.SearchHistoryTable
                .COLUMN_NAME_FROM_COORDINATES)) {
            String name = contentValues.getAsString(TripPlannerContract.SearchHistoryTable
                    .COLUMN_NAME_FROM_COORDINATES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires starting point coordinates");
            }
        }

        // Check that the "to coordinates" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.SearchHistoryTable
                .COLUMN_NAME_TO_COORDINATES)) {
            String name = contentValues.getAsString(TripPlannerContract.SearchHistoryTable
                    .COLUMN_NAME_TO_COORDINATES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires destination coordinates");
            }
        }

        // Check that the "from name" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.SearchHistoryTable
                .COLUMN_NAME_FROM_NAME)) {
            String name = contentValues.getAsString(TripPlannerContract.SearchHistoryTable
                    .COLUMN_NAME_FROM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires starting point name");
            }
        }

        // Check that the "to name" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.SearchHistoryTable
                .COLUMN_NAME_TO_NAME)) {
            String name = contentValues.getAsString(TripPlannerContract.SearchHistoryTable
                    .COLUMN_NAME_TO_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires destination name");
            }
        }

        // Check that the "modes" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.SearchHistoryTable
                .COLUMN_NAME_MODES)) {
            String name = contentValues.getAsString(TripPlannerContract.SearchHistoryTable
                    .COLUMN_NAME_MODES);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires modes");
            }
        }

        // Check that the "timestamp" column value is not being updated to null
        if (contentValues.containsKey(TripPlannerContract.SearchHistoryTable
                .COLUMN_NAME_TIMESTAMP)) {
            String name = contentValues.getAsString(TripPlannerContract.SearchHistoryTable
                    .COLUMN_NAME_TIMESTAMP);
            if (name == null) {
                throw new IllegalArgumentException("Entry requires timestamp");
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

        // See if the URI matcher can match the URI to one of the defined codes
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCHES_TABLE:

                // The URI matches the SEARCHES_TABLE code: directly delete the given selection
                // in the search history table and return the number of rows deleted
                return database.delete(TripPlannerContract.SearchHistoryTable.TABLE_NAME,
                        selection, selectionArgs);

            case SEARCH_ID:

                // The URI matches the SEARCH_ID code: extract out the ID from the URI and
                // set the selection & selectionArgs to update a specific row
                selection = TripPlannerContract.SearchHistoryTable._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Delete the row from the search history table, should return 1
                return database.delete(TripPlannerContract.SearchHistoryTable.TABLE_NAME,
                        selection, selectionArgs);

            default:
                // If the URI does not match any valid codes, we cannot delete from the database
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }

    }

    /**
     * Returns the MIME type of data for the content URI
     */
    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCHES_TABLE:
                return TripPlannerContract.SearchHistoryTable.CONTENT_LIST_TYPE;
            case SEARCH_ID:
                return TripPlannerContract.SearchHistoryTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
