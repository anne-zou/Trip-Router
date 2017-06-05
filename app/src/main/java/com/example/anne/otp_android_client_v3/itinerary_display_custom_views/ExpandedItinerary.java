package com.example.anne.otp_android_client_v3.itinerary_display_custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import vanderbilt.thub.otp.model.OTPPlanModel.Itinerary;

/**
 * Created by Anne on 6/5/2017.
 */

public class ExpandedItinerary extends View {

    private Itinerary mItinerary;


    public ExpandedItinerary(Context context) {
        this(context, null);
    }

    public ExpandedItinerary(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandedItinerary(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ExpandedItinerary(Context context, AttributeSet attrs, int defStyle,
                             Itinerary itinerary) {
        super(context, attrs, defStyle);
        mItinerary = itinerary;
        updateContentBounds();
    }

    public Itinerary getItinerary() {
        return mItinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        mItinerary = itinerary;
        updateContentBounds();
    }

    public void updateContentBounds() {

    }

}
