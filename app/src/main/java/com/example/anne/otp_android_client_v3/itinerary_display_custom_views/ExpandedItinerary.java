package com.example.anne.otp_android_client_v3.itinerary_display_custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;

import vanderbilt.thub.otp.model.OTPPlanModel.Itinerary;
import vanderbilt.thub.otp.model.OTPPlanModel.Leg;
import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;

/**
 * Created by Anne on 6/5/2017.
 */

public class ExpandedItinerary extends View {

    private final int MODE_ICON_SIZE = 70;

    private final int BUS_STOP_CIRCLE_SIZE = 10;

    private final int NON_TRANSIT_LEG_SEGMENT_HEIGHT = 400;

    private final int BUS_STOP_SEGMENT_HEIGHT = 150;
    

    private Itinerary mItinerary;

    private HashMap<Leg,Boolean> isLegExpandedMap;


    // CONSTRUCTORS

    public ExpandedItinerary(Context context) {
        this(context, null);
    }

    public ExpandedItinerary(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandedItinerary(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    // CALCULATE DIMENSIONS
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Get the height measurement
        int heightSize = 0;
        if (mItinerary != null)
            heightSize = resolveSize(getDesiredHeight() + getPaddingTop() + getPaddingBottom(),
                    heightMeasureSpec);

        // Store the measurements
        setMeasuredDimension(widthMeasureSpec, heightSize);
    }

    private int getDesiredHeight() {
        int height = 0;

        for (Leg leg : mItinerary.getLegs()) {
            if (isLegExpandedMap.get(leg)) {
                int i = leg.getIntermediateStops().size() + 1;
            }
        }
        return 0;
    }

    // GETTERS AND SETTERS

    public Itinerary getItinerary() {
        return mItinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        mItinerary = itinerary;

        // Fill out HashMap
        for (Leg leg : itinerary.getLegs())
            isLegExpandedMap.put(leg, false);

        // Resize the view
        requestLayout();

        // Update bounds
        updateContentBounds();
    }

    public void updateContentBounds() {

    }

}
