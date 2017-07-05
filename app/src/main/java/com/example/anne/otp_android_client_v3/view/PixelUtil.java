package com.example.anne.otp_android_client_v3.view;

import android.content.Context;

/**
 * Created by Anne on 7/5/2017.
 */

public class PixelUtil {

    public static int dpFromPx(final Context context, final int px) {
        return (int)(px / context.getResources().getDisplayMetrics().density);
    }

    public static int pxFromDp(final Context context, final int dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density);
    }
}
