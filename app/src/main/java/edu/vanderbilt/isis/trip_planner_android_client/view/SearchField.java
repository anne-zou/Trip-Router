package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.widget.TextView;

/**
 * Created by Anne on 7/19/2017.
 */

public class SearchField {

    // Use SearchField.ORIGIN, SearchField.DESTINATION, or an integer i greater than or equal to 0
    // for the i_th intermediate stop of the trip plan.

    public static int ORIGIN = -1;

    public static int DESTINATION = -2;

    private TextView mTextView;

    private int mWhichSearchField;

    /**
     * Construct a SearchField object
     * @param textView the search field TextView
     * @param whichSearchField The position in the trip plan that a place entered into this search
     *                         field should have.
     *                         Use SearchField.ORIGIN, SearchField.DESTINATION, or an integer i
     *                         greater than or equal to 0 for the i_th intermediate stop of the
     *                         trip plan.
     */
    public SearchField(TextView textView, int whichSearchField) {
        mTextView = textView;
        mWhichSearchField = whichSearchField;
    }

    /**
     * Set the text in the TextView
     * @param text the text
     */
    public void setText(String text) {
        mTextView.setText(text);
    }

    /**
     * Get the point in the trip plan that this search field represents
     * @return SearchField.ORIGIN, SearchField.DESTINATION, or any integer i greater than -1
     *         indicating the i_th intermediate stop for the trip plan
     */
    public int isWhichSearchField() {
        return mWhichSearchField;
    }

}
