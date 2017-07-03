package com.example.anne.otp_android_client_v3.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.anne.otp_android_client_v3.view.MainActivity;

/**
 * Created by Anne on 6/8/2017.
 */




public class SlidingPanelHeadOnSwipeTouchListener implements View.OnTouchListener {

    private final String TAG = "SlidingPanelListener";

    private final GestureDetector gestureDetector;

    private MainActivity activity;

    public SlidingPanelHeadOnSwipeTouchListener(Context ctx, MainActivity activity) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.activity = activity;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (activity.getState() == MainActivity.ActivityState.HOME_PLACE_SELECTED)
            return true;
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "Fling detected");
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();
            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0)
                    onSwipeRight();
                else
                    onSwipeLeft();
                return true;
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            activity.toggleSlidingPanel();
            return super.onSingleTapUp(e);
        }

    }

    public void onSwipeRight() {
        Log.d(TAG, "Swiped right");
        activity.onSwipeSlidingPanelRight();
    }

    public void onSwipeLeft() {
        Log.d(TAG, "Swiped left");
        activity.onSwipeSlidingPanelLeft();
    }

}


