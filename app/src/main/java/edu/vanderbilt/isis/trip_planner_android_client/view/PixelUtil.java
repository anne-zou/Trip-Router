package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.content.Context;

/**
 * Created by Anne on 7/5/2017.
 */

public class PixelUtil {

    private PixelUtil() {} // private constructor to prevent instantiation of class

    /**
     * Utility function that converts px to dp
     * @param context used to get the pixel density of the screen
     * @param px the amount in pixels to be converted
     * @return the converted amount in dp
     */
    public static int dpFromPx(final Context context, final int px) {
        return (int)(px / context.getResources().getDisplayMetrics().density);
    }

    /**
     * Utility function that converts dp to px
     * @param context used to get the pixel density of the screen
     * @param dp the amount in dp to be converted
     * @return the converted amount in px
     */
    public static int pxFromDp(final Context context, final int dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }
}
