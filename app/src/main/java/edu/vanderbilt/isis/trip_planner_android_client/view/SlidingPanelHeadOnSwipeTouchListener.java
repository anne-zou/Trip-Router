package edu.vanderbilt.isis.trip_planner_android_client.view;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Anne on 6/8/2017.
 */


/**
 * OnSwipeTouchListener for the sliding panel head
 */
public class SlidingPanelHeadOnSwipeTouchListener implements View.OnTouchListener {

    private final String TAG = SlidingPanelHeadOnSwipeTouchListener.class.getName();

    private final GestureDetector gestureDetector;

    private MainActivity activity;

    /**
     * Constructor to create the gesture detector and save a reference to the main activity
     * @param activity reference to the main activity
     */
    public SlidingPanelHeadOnSwipeTouchListener(MainActivity activity) {
        gestureDetector = new GestureDetector(activity, new GestureListener());
        this.activity = activity;
    }

    /**
     * Process the touch event with the gesture detector
     * @param v the view touched
     * @param event the motion event
     * @return true if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    /**
     * Determines the type of the touch event, then handles it accordingly via the overridden
     * callback methods
     */
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // Handle if the sliding panel was swiped left or right
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "Fling detected");
            float distanceX = e2.getX() - e1.getX();
            float distanceY = e2.getY() - e1.getY();

            // If the swipe was long & fast enough
            if (Math.abs(distanceX) > Math.abs(distanceY)
                    && Math.abs(distanceX) > SWIPE_THRESHOLD
                    && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (distanceX > 0) // swiped right
                    onSwipeRight();
                else // swiped left
                    onSwipeLeft();
                return true;
            }
            return false;
        }

        // Toggle the sliding panel if it was tapped
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            activity.toggleSlidingPanel();
            return super.onSingleTapUp(e);
        }

    }

    /**
     * Handle if panel was swiped right
     */
    private void onSwipeRight() {
        Log.d(TAG, "Swiped right");
        activity.onSwipeSlidingPanelRight();
    }

    /**
     * Handle if panel was swiped left
     */
    private void onSwipeLeft() {
        Log.d(TAG, "Swiped left");
        activity.onSwipeSlidingPanelLeft();
    }

}


