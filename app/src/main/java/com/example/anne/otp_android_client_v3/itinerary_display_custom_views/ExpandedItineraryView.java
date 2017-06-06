package com.example.anne.otp_android_client_v3.itinerary_display_custom_views;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;

import com.example.anne.otp_android_client_v3.ModeToIconDictionary;
import com.example.anne.otp_android_client_v3.StringToModeDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.http.Path;
import vanderbilt.thub.otp.model.OTPPlanModel.Itinerary;
import vanderbilt.thub.otp.model.OTPPlanModel.Leg;
import vanderbilt.thub.otp.model.OTPPlanModel.Place;
import vanderbilt.thub.otp.model.OTPPlanModel.TraverseMode;


/**
 * Created by Anne on 6/5/2017.
 */

public class ExpandedItineraryView extends View {

    private final int MODE_ICON_SIZE = 70;

    private final int BUS_STOP_CIRCLE_SIZE = 10;

    private final int REGULAR_LEG_SEGMENT_HEIGHT = 400;

    private final int BUS_STOP_SEGMENT_HEIGHT = 150;

    private Itinerary mItinerary;

    private Set<Leg> mExpandedTransitLegs;


    private List<Drawable> mVertexDrawables;

    private List<PlaceNameText> mVertexTexts;

    private List<Edge> mEdges;


    private TextPaint mVertexTextPaint;

    private Paint mEdgePaint;



    public ExpandedItineraryView(Context context) {
        this(context, null);
    }

    public ExpandedItineraryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandedItineraryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mExpandedTransitLegs = new HashSet<>();
        mVertexDrawables = new ArrayList<>();
        mVertexTexts = new ArrayList<>();
        mEdges = new ArrayList<>();
        mVertexTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = 0;
        if (mItinerary != null)
            heightSize = resolveSize(getDesiredHeight() + getPaddingTop() + getPaddingBottom(),
                    heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightSize);
    }

    private int getDesiredHeight() {
        int height = 0;
        for (Leg leg : mItinerary.getLegs()) {
            if (mExpandedTransitLegs.contains(leg)) {
                height += (MODE_ICON_SIZE
                        + BUS_STOP_SEGMENT_HEIGHT
                        + (BUS_STOP_CIRCLE_SIZE + BUS_STOP_SEGMENT_HEIGHT)
                        * leg.getIntermediateStops().size());
            } else {
                height += (MODE_ICON_SIZE + REGULAR_LEG_SEGMENT_HEIGHT);
            }
        }
        return height;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        updateContentBounds();
        invalidate();
    }

    public Itinerary getItinerary() {
        return mItinerary;
    }

    public void setItinerary(Itinerary itinerary) {
        mItinerary = itinerary;
        requestLayout();
        updateContentBounds();
        invalidate();
    }

    public void expand(Leg leg) {
        mExpandedTransitLegs.add(leg);
        requestLayout();
        updateContentBounds();
        invalidate();
    }

    private void updateContentBounds() {

        int y = 0;

        List<Leg> legs = mItinerary.getLegs();

        for (Leg leg : legs) {

            if (mExpandedTransitLegs.contains(leg)) {


            } else {


                // Edge
                if (leg != legs.get(legs.size() - 1)) {
                    mEdges.add(new Edge(y, y + REGULAR_LEG_SEGMENT_HEIGHT, getPathEffect(leg))
                            .setColor(getColor(leg)));
                }
            }

        }

    }

    public PathEffect getPathEffect(Leg leg) {
        // TODO
        return new PathEffect();
    }

    public int getColor(Leg leg) {
        // TODO
        return 0;
    }

    private class PlaceNameText {

        protected String name;

        protected RectF bounds;

        public PlaceNameText(String name, RectF bounds) {
            this.name = name;
            this.bounds = bounds;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public RectF getBounds() {
            return bounds;
        }

        public void setBounds(RectF bounds) {
            this.bounds = bounds;
        }

    }

    private class ExpandablePlaceNameText extends PlaceNameText {

        private List<Place> intermediateStops;

        public ExpandablePlaceNameText(String name, RectF bounds, ArrayList<Place> stops) {
            super(name, bounds);
            this.intermediateStops = stops;
        }

        public boolean isInBounds(float x, float y) {
            return super.bounds.contains(x,y);
        }

        public String getStopName(int i) {
            return intermediateStops.get(i).getName();
        }

        public int getNumStops() {
            return intermediateStops.size();
        }
    }

    private class Edge {

        public float top;

        public float bottom;

        private Paint paint;

        public Edge(float top, float bottom, PathEffect pe) {
            this.top = top;
            this.bottom = bottom;
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(pe);
        }

        public PathEffect getPathEffect() {
            return paint.getPathEffect();
        }

        public Edge setPathEffect(PathEffect pe) {
            paint.setPathEffect(pe);
            return this;
        }

        public int getColor(int color) {
            return paint.getColor();
        }

        public Edge setColor(int color) {
            paint.setColor(color);
            return this;
        }

    }

}
