package com.example.anne.otp_android_client_v3;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by Anne on 5/24/2017.
 */

public class MyFabOnClickListener implements View.OnClickListener {

    public enum FabState {DIRECTIONS, START_NAVIGATION, EXIT_NAVIGATION, ADD_TO_CALENDAR}
    private MainActivity mActivity;

    public MyFabOnClickListener(MainActivity act) {
        mActivity = act;
    }

    @Override
    public void onClick(View view) {
        MainActivity.ActivityState state = mActivity.getState();

        switch (state) {
            case ONE: mActivity.tryStateChangeONEtoTWO(view);
        }

    }
}
