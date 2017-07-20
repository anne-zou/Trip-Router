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

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TripPlanner.db";

    public static final String TRIP_PLAN_HISTORY_TABLE_NAME =
            TripPlannerContract.TripPlanHistoryTable.TABLE_NAME;
    public static final String SCHEDULE_TABLE_NAME =
            TripPlannerContract.ScheduleTable.TABLE_NAME;

    private static final String SQL_CREATE_TRIP_PLAN_HISTORY_TABLE = "CREATE TABLE "
            + TRIP_PLAN_HISTORY_TABLE_NAME + " " + "("
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_NAME + " TEXT NOT NULL, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_NAME + " TEXT NOT NULL, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_COORDINATES + " TEXT NOT NULL, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_COORDINATES + " TEXT NOT NULL, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_FROM_ADDRESS + " TEXT, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TO_ADDRESS + " TEXT, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_MODES + " TEXT NOT NULL, "
            + TripPlannerContract.TripPlanHistoryTable.COLUMN_NAME_TIMESTAMP + " INTEGER NOT NULL"
            + ")";

    private static final String SQL_CREATE_SCHEDULE_TABLE = "CREATE TABLE "
            + SCHEDULE_TABLE_NAME + " " + "("
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_FIRST_TRIP + " INTEGER NOT NULL, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_TIME_NEXT_TRIP + " INTEGER, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_REMINDER_TIME + " INTEGER, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_REPEAT_DAYS + " TEXT, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_NAME + " TEXT NOT NULL, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_NAME + " TEXT NOT NULL, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_COORDINATES + " TEXT NOT NULL, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_COORDINATES + " TEXT NOT NULL, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_FROM_ADDRESS + " TEXT, "
            + TripPlannerContract.ScheduleTable.COLUMN_NAME_TO_ADDRESS + " TEXT"
            + ")";

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS ";

    /**
     * Constructor for the db helper
     * @param context context
     */
    public TripPlannerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create the tables in the database.
     * This is not called until after getReadableDatabase() or getWritableDatabase() is called
     * @param db the newly created database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRIP_PLAN_HISTORY_TABLE);
        db.execSQL(SQL_CREATE_SCHEDULE_TABLE);
    }

    /**
     * @param db database
     * @param oldVersion old version #
     * @param newVersion new version #
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE + TRIP_PLAN_HISTORY_TABLE_NAME);
        db.execSQL(SQL_CREATE_TRIP_PLAN_HISTORY_TABLE);

        db.execSQL(SQL_DELETE_TABLE + SCHEDULE_TABLE_NAME);
        db.execSQL(SQL_CREATE_SCHEDULE_TABLE);
    }

}
