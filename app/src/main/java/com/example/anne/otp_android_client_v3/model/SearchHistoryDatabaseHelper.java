package com.example.anne.otp_android_client_v3.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anne on 6/29/2017.
 */

public class SearchHistoryDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SearchHistory.db";

    private static final String SQL_CREATE_TABLE_PARAMS = "("
            + SearchHistoryDatabaseContract.SearchHistoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SearchHistoryDatabaseContract.SearchHistoryTable.COLUMN_NAME_FROM_NAME + " TEXT, "
            + SearchHistoryDatabaseContract.SearchHistoryTable.COLUMN_NAME_TO_NAME + " TEXT, "
            + SearchHistoryDatabaseContract.SearchHistoryTable.COLUMN_NAME_FROM_COORDINATES + " TEXT NOT NULL, "
            + SearchHistoryDatabaseContract.SearchHistoryTable.COLUMN_NAME_TO_COORDINATES + " TEXT NOT NULL, "
            + SearchHistoryDatabaseContract.SearchHistoryTable.COLUMN_NAME_MODES + " TEXT NOT NULL, "
            + SearchHistoryDatabaseContract.SearchHistoryTable.COLUMN_NAME_TIMESTAMP + " INTEGER"
            + ")";

    private static final String SQL_CREATE_TABLE = "CREATE TABLE "
            + SearchHistoryDatabaseContract.SearchHistoryTable.TABLE_NAME + " " + SQL_CREATE_TABLE_PARAMS;

    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS "
            + SearchHistoryDatabaseContract.SearchHistoryTable.TABLE_NAME;


    public SearchHistoryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        db.execSQL(SQL_CREATE_TABLE);
    }

}
