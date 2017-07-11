package edu.vanderbilt.isis.trip_planner_android_client.model.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import edu.vanderbilt.isis.trip_planner_android_client.R;

/**
 * Created by Anne on 7/10/2017.
 */

public class SearchHistoryCursorAdapter extends CursorAdapter {

    public SearchHistoryCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }
}
