package com.example.anne.otp_android_client_v3.itinerary_display_custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;


import com.example.anne.otp_android_client_v3.dictionary.ModeToIconDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vanderbilt.thub.otp.model.OTPPlanModel.Itinerary;
import vanderbilt.thub.otp.model.OTPPlanModel.Leg;


/**
 * Created by Anne on 6/5/2017.
 */

public class ExpandedItineraryView extends View {

    private final int MODE_ICON_SIZE = 70;

    private final int BUS_STOP_CIRCLE_SIZE = 10;

    private final int REGULAR_LEG_SEGMENT_HEIGHT = 400;

    private final int BUS_STOP_SEGMENT_HEIGHT = 150;

    private final int SPACE_BETWEEN_DOTS = 10;

    private final int SPACE_BETWEEN_TRANSIT_ICON_AND_ROUTE_NUMBER = 5;

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

            // Add leg mode icon
            Drawable drawable = ModeToIconDictionary.getDrawable(leg.getMode());
            drawable.setBounds(0, y, MODE_ICON_SIZE, MODE_ICON_SIZE);
            mVertexDrawables.add(drawable);

            // TODO: Add leg text

            // TODO: y += max(icon_height, text_height)

            if (mExpandedTransitLegs.contains(leg)) {
                // TODO: Add bus stop edge
                // TODO: Add intermediate stop icons and edges

            } else {

                // Add regular edge
                if (leg != legs.get(legs.size() - 1)) {
                    mEdges.add(new Edge()
                            .setTop(y)
                            .setBottom(y + REGULAR_LEG_SEGMENT_HEIGHT)
                            .setPathEffect(getPathEffect(leg))
                            .setColor(getColor(leg))
                    );
                    y += REGULAR_LEG_SEGMENT_HEIGHT;
                }
            }

        }

    }

    public PathEffect getPathEffect(Leg leg) {

        float[] walk = new float[] {5,10};
        float[] bike = new float[] {10,10};

        switch (leg.getMode()) {
            case ("WALK"):
                return new DashPathEffect(walk, 0);
            case ("BICYCLE"):
                return new DashPathEffect(bike, 0);
        }

        return null;
    }

    public int getColor(Leg leg) {

        if (leg.getMode() == "BUS" || leg.getMode() == "SUBWAY")
            return Color.parseColor(leg.getRouteColor());
        else
            return Color.BLUE;
    }

    private class PlaceNameText {

        private String name;

        private TextPaint paint;

        private int x;

        private int y;

        private Rect bounds;

        public PlaceNameText(){
            paint = new TextPaint();
            paint.setTextAlign(Paint.Align.CENTER);
        }

        public PlaceNameText setName(String name) {
            this.name = name;
            return this;
        }

        public PlaceNameText setX(int x) {
            this.x = x;
            return this;
        }

        public PlaceNameText setY(int y) {
            this.y = y;
            return this;
        }

        public PlaceNameText setTextSize(float size) {
            paint.setTextSize(size);
            return this;
        }

        public PlaceNameText setTextColor(int color) {
            paint.setColor(color);
            return this;
        }

        public String getName() { return name; }

        public TextPaint getPaint() { return paint; }

        public float getX() { return x; }

        public float getY() {
            return y;
        }

        public float getTextSize() { return paint.getTextSize(); }

        public int getTextColor() { return paint.getColor(); }

        public boolean isInBounds(int x, int y) {
            if (paint == null)
                throw new RuntimeException("Need to set TextPaint to calculate bounds");
            if (bounds == null) bounds = new Rect();
            paint.getTextBounds(name, this.x, this.y, bounds);
            return bounds.contains(x, y);
        }

        public void draw(Canvas canvas) {
            canvas.drawText(name, x, y, paint);
        }

    }

    private class Edge {

        public float top;

        public float bottom;

        private Paint paint;

        public Edge() {this(0,0); }

        public Edge(float top, float bottom) {
            this.top = top;
            this.bottom = bottom;
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
        }

        public Edge setTop(float top) {
            this.top = top;
            return this;
        }

        public Edge setBottom(float bottom) {
            this.bottom = bottom;
            return this;
        }

        public Edge setPathEffect(PathEffect pe) {
            paint.setPathEffect(pe);
            return this;
        }

        public Edge setColor(int color) {
            paint.setColor(color);
            return this;
        }

        public float getTop() { return top; }

        public float getBottom() { return bottom; }

        public PathEffect getPathEffect() { return paint.getPathEffect(); }

        public int getColor(int color) { return paint.getColor(); }

        public Paint getPaint() { return paint; }

        public void draw(Canvas canvas, float centerX) {
            canvas.drawLine(centerX, top, centerX, bottom, paint);
        }

    }

    private class CombinedDrawable extends Drawable {

        private List<Drawable> drawables;

        private Rect bounds;

        private int opacity;

        public CombinedDrawable() { this(null); }

        public CombinedDrawable(List<Drawable> drawables) {
            this.drawables = drawables;
            updateDrawablesBounds();
        }

        public CombinedDrawable setDrawables(List<Drawable> drawables) {
            this.drawables = drawables;
            updateDrawablesBounds();
            return this;
        }

        @Override
        public void setBounds(Rect bounds) {
            super.setBounds(bounds);
            this.bounds = bounds;
            updateDrawablesBounds();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            for (Drawable drawable : drawables) { drawable.draw(canvas); }
        }

        public List<Drawable> getDrawables() {
            return drawables;
        }

        private void updateDrawablesBounds() {
            // TODO

        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            for (Drawable drawable : drawables) { drawable.setAlpha(alpha); }
        }

        @Override
        public void setColorFilter(@ColorInt int color, @NonNull PorterDuff.Mode mode) {
            for (Drawable drawable : drawables) { drawable.setColorFilter(color, mode); }
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            for (Drawable drawable : drawables) { drawable.setColorFilter(colorFilter); }
        }

        @Override
        public int getOpacity() {
            return opacity;
        }

        public CombinedDrawable setOpacity(int opacity) {
            this.opacity = opacity;
            for (Drawable drawable : drawables) { drawable.setAlpha(opacity); }
            return this;
        }


    }

}
