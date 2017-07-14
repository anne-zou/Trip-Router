package edu.vanderbilt.isis.trip_planner_android_client.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anne on 6/29/2017.
 */

/**
 * Helper used by the content provider to interact with the trip planner database.
 */
public class TripPlannerDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TripPlanner.db";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + TripPlannerContract.SearchHistoryTable.TABLE_NAME + " " + "("
            + TripPlannerContract.SearchHistoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_NAME + " TEXT, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_NAME + " TEXT, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES + " TEXT NOT NULL, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES + " TEXT NOT NULL, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_FROM_ADDRESS + " TEXT, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TO_ADDRESS + " TEXT, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_MODES + " TEXT NOT NULL, "
            + TripPlannerContract.SearchHistoryTable.COLUMN_NAME_TIMESTAMP + " INTEGER"
            + ")";

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "
            + TripPlannerContract.SearchHistoryTable.TABLE_NAME;

    /**
     * Constructor for the db helper
     * @param context context
     */
    public TripPlannerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * The database is not created until getReadableDatabase() or getWritableDatabase() is called
     * @param db the newly created database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * @param db database
     * @param oldVersion old version #
     * @param newVersion new version #
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

}
