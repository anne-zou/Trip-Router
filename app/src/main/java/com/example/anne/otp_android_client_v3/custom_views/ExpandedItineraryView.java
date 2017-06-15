package com.example.anne.otp_android_client_v3.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.example.anne.otp_android_client_v3.MainActivity;
import com.example.anne.otp_android_client_v3.R;
import com.example.anne.otp_android_client_v3.dictionary.ModeToDrawableDictionary;
import com.example.anne.otp_android_client_v3.dictionary.StringToModeDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vanderbilt.thub.otp.model.OTPPlanModel.Itinerary;
import vanderbilt.thub.otp.model.OTPPlanModel.Leg;
import vanderbilt.thub.otp.model.OTPPlanModel.Place;

import static android.content.ContentValues.TAG;


/**
 * Created by Anne on 6/5/2017.
 */

// TODO: Show the start times of each leg
// TODO: Implement text wrap via StaticLayout

public class ExpandedItineraryView extends View {

    private final int MODE_ICON_HEIGHT = 70;

    private final int SPACE_BETWEEN_MODE_ICON_AND_ROUTE_ICON = 3;

    private final int ROUTE_ICON_TEXT_SIZE = 40;

    private final int PLACE_NAME_TEXT_SIZE = 40;

    private final int STOPS_INFO_TEXT_SIZE = 35;

    private final int TRANSIT_STOP_CIRCLE_SIZE = 30;

    private final int REGULAR_LEG_SEGMENT_HEIGHT = 200;

    private final int TRANSIT_STOP_SEGMENT_HEIGHT = 120;

    private final int LINE_STROKE_WIDTH = 10;

    private final int EXPAND_COLLAPSE_ICON_HEIGHT = 100;

    private final int EXPAND_COLLAPSE_ICON_WIDTH = 40;

    private final int SPACE_BETWEEN_TRANSIT_LEG_NAME_AND_EXPANDABLE_INFO = 70;

    private final int SPACE_BETWEEN_EXPAND_COLLAPSE_ICON_AND_LABEL = 20;

    private final int CLICKABLE_ERROR_PADDING = 30;

    private int CENTER_X = 150;

    private int PLACE_NAME_START_X = 250;

    private Context mContext;

    private Itinerary mItinerary;

    private Set<Leg> mExpandedTransitLegs;

    private HashMap<Rect,Leg> mExpandablesDictionary;

    private List<Drawable> mVertexDrawables;

    private List<PlaceNameText> mVertexTexts;

    private List<Edge> mEdges;

    private List<TransitStopCircle> mTransitStopCircles;

    private Paint mBusStopCirclePaint;

    public ExpandedItineraryView(Context context) {
        this(context, null);
    }

    public ExpandedItineraryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandedItineraryView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
        mContext = context;
        mExpandedTransitLegs = new HashSet<>();
        mExpandablesDictionary = new HashMap<>();
        mVertexDrawables = new ArrayList<>();
        mVertexTexts = new ArrayList<>();
        mEdges = new ArrayList<>();
        mTransitStopCircles = new ArrayList<>();
        mBusStopCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBusStopCirclePaint.setColor(Color.BLACK);
        mBusStopCirclePaint.setAlpha(MainActivity.DARK_OPACITY);
        mBusStopCirclePaint.setStyle(Paint.Style.FILL);

        CENTER_X += getPaddingLeft();
        PLACE_NAME_START_X += getPaddingLeft();

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
                height += (MODE_ICON_HEIGHT
                        + TRANSIT_STOP_SEGMENT_HEIGHT
                        + (TRANSIT_STOP_CIRCLE_SIZE + TRANSIT_STOP_SEGMENT_HEIGHT)
                        * leg.getIntermediateStops().size());
            } else {
                height += (MODE_ICON_HEIGHT + REGULAR_LEG_SEGMENT_HEIGHT);
            }
        }
        height += MODE_ICON_HEIGHT;
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

    public void collapse(Leg leg) {
        mExpandedTransitLegs.remove(leg);
        requestLayout();
        updateContentBounds();
        invalidate();
    }

    private void updateContentBounds() {

        if (mItinerary == null)
            return;

        mVertexDrawables.clear();
        mVertexTexts.clear();
        mEdges.clear();
        mTransitStopCircles.clear();

        int y = getPaddingTop();

        List<Leg> legs = mItinerary.getLegs();

        for (Leg leg : legs) {

            // Add icon for the leg
            Drawable modeIcon = ModeToDrawableDictionary.getDrawable(leg.getMode());

            if (StringToModeDictionary.isTransit(leg.getMode())) {
                // If transit, use custom drawable
                ModeAndRouteDrawable compoundIcon = new ModeAndRouteDrawable(
                        modeIcon, MODE_ICON_HEIGHT,
                        CENTER_X, y + MODE_ICON_HEIGHT/2,
                        leg.getRoute(), Color.parseColor("#" + leg.getRouteColor()),
                        ROUTE_ICON_TEXT_SIZE, Color.WHITE);

                mVertexDrawables.add(compoundIcon);
                Log.d(TAG, "Compound drawable added to list.");

            } else {
                // If non-transit, use the regular drawable
                modeIcon.setBounds(CENTER_X - MODE_ICON_HEIGHT/2, y,
                        CENTER_X + MODE_ICON_HEIGHT/2, y + MODE_ICON_HEIGHT);

                mVertexDrawables.add(modeIcon);
                Log.d(TAG, "Regular drawable added to list.");
            }

            y += MODE_ICON_HEIGHT/2; // Move y to center of icon

            // Add name of the origin of the leg
            PlaceNameText placeName = new PlaceNameText(leg.getFrom().getName().toUpperCase(),
                    PLACE_NAME_START_X, y, PLACE_NAME_TEXT_SIZE, Color.BLACK);
            mVertexTexts.add(placeName);

            y += MODE_ICON_HEIGHT/2; // Move y to bottom of the icon

            // If transit, add expand/collapse button, # stops, and duration of leg
            if (StringToModeDictionary.isTransit(leg.getMode())) {
                int expandMessageCenterY = y +
                        SPACE_BETWEEN_TRANSIT_LEG_NAME_AND_EXPANDABLE_INFO / 2;

                // Add expand/collapse text (# stops and duration of transit leg)
                String sigularOrPluralStops = leg.getIntermediateStops().isEmpty() ?
                        "stop" : "stops";
                PlaceNameText transitModeInfo = new PlaceNameText(
                        (leg.getIntermediateStops().size() + 1)
                                + " " + sigularOrPluralStops
                                +" (" + MainActivity.getDurationString(leg.getDuration())
                                + ")",
                        PLACE_NAME_START_X + EXPAND_COLLAPSE_ICON_WIDTH
                                + SPACE_BETWEEN_EXPAND_COLLAPSE_ICON_AND_LABEL,
                        expandMessageCenterY,
                        STOPS_INFO_TEXT_SIZE, Color.BLACK
                );
                mVertexTexts.add(transitModeInfo);

                // Add expand/collapse icon if there is more than one stop
                if (!leg.getIntermediateStops().isEmpty()) {
                    int expandOrCollapseDrawable = mExpandedTransitLegs.contains(leg) ?
                            R.drawable.collapse : R.drawable.expand;

                    Drawable expandCollapseIcon = ContextCompat.getDrawable(
                            mContext, expandOrCollapseDrawable)
                            .getConstantState().newDrawable();
                    expandCollapseIcon.setAlpha(MainActivity.DARK_OPACITY);

                    expandCollapseIcon.setBounds(PLACE_NAME_START_X,
                            expandMessageCenterY - EXPAND_COLLAPSE_ICON_HEIGHT / 2,
                            PLACE_NAME_START_X + EXPAND_COLLAPSE_ICON_WIDTH,
                            expandMessageCenterY + EXPAND_COLLAPSE_ICON_HEIGHT / 2);
                    mVertexDrawables.add(expandCollapseIcon);


                    // Store clickable bounds for expanding/collapsing the leg's transit stop info
                    Rect clickableBounds = new Rect(expandCollapseIcon.getBounds());
                    clickableBounds.union(transitModeInfo.getBounds());
                    clickableBounds.set(clickableBounds.left - CLICKABLE_ERROR_PADDING,
                            clickableBounds.top - CLICKABLE_ERROR_PADDING,
                            clickableBounds.right + CLICKABLE_ERROR_PADDING,
                            clickableBounds.bottom + CLICKABLE_ERROR_PADDING);
                    mExpandablesDictionary.put(clickableBounds, leg);
                }

            }

            // Add tail of the leg depiction
            if (mExpandedTransitLegs.contains(leg)) { // If expanded transit, add stops & edges

                int routeColor = Color.parseColor("#" + leg.getRouteColor());

                // Add fencepost transit stop edge
                mEdges.add(new Edge(y, y + TRANSIT_STOP_SEGMENT_HEIGHT, CENTER_X)
                        .setColor(routeColor)
                );

                y += TRANSIT_STOP_SEGMENT_HEIGHT;


                // Add remaining stop icons and edges
                for (Place stop : leg.getIntermediateStops()) {

                    // Add stop icon
                    mTransitStopCircles.add(new TransitStopCircle(CENTER_X,
                            y + TRANSIT_STOP_CIRCLE_SIZE/2,
                            TRANSIT_STOP_CIRCLE_SIZE/2,
                            Color.parseColor("#" + leg.getRouteColor()))
                    );
                    y += TRANSIT_STOP_CIRCLE_SIZE/2;

                    // Add stop name
                    mVertexTexts.add(new PlaceNameText(stop.getName().toUpperCase(),
                            PLACE_NAME_START_X, y, PLACE_NAME_TEXT_SIZE, Color.BLACK));
                    y += TRANSIT_STOP_CIRCLE_SIZE/2;

                    // Add edge
                    mEdges.add(new Edge(y, y + TRANSIT_STOP_SEGMENT_HEIGHT, CENTER_X)
                            .setColor(routeColor)
                    );
                    y += TRANSIT_STOP_SEGMENT_HEIGHT;
                }

            } else { // If not transit, add a regular edge

                // Add regular edge
                mEdges.add(new Edge(y, y + REGULAR_LEG_SEGMENT_HEIGHT, CENTER_X)
                        .setPathEffect(getPathEffect(leg))
                        .setColor(getColor(leg))
                );
                y += REGULAR_LEG_SEGMENT_HEIGHT;

            }
        }

        // Add destination icon
        Drawable destinationIcon = ContextCompat.getDrawable(mContext,
                R.drawable.ic_location_on_black_24dp);
        destinationIcon.setAlpha(MainActivity.DARK_OPACITY);
        destinationIcon.setBounds(CENTER_X - MODE_ICON_HEIGHT/2, y,
                CENTER_X + MODE_ICON_HEIGHT/2, y + MODE_ICON_HEIGHT);
        mVertexDrawables.add(destinationIcon);

        y += MODE_ICON_HEIGHT/2; // Move y to center of icon

        // Add destination name
        PlaceNameText destinationName = new PlaceNameText(
                legs.get(legs.size() - 1).getTo().getName().toUpperCase(),
                PLACE_NAME_START_X, y, PLACE_NAME_TEXT_SIZE, Color.BLACK);
        mVertexTexts.add(destinationName);

        y += MODE_ICON_HEIGHT/2; // Move y to bottom of icon

    }

    public PathEffect getPathEffect(Leg leg) {

        float[] walk = new float[] {10,10};
        float[] bike = new float[] {30,10};

        switch (leg.getMode()) {
            case ("WALK"):
                return new DashPathEffect(walk, 0);
            case ("BICYCLE"):
                return new DashPathEffect(bike, 0);
        }

        return null;
    }

    public int getColor(Leg leg) {

        if (StringToModeDictionary.isTransit(leg.getMode()))
            return Color.parseColor("#" + leg.getRouteColor());
        else
            return getResources().getColor(R.color.colorPrimary, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "Touched: " + event.getX() + ", " + event.getY());
            double x = event.getX();
            double y = event.getY();
            onClick((int)event.getX(), (int)event.getY());
        }
        return super.dispatchTouchEvent(event);
    }

    private void onClick(int x, int y) {
        for (Map.Entry<Rect, Leg> entry : mExpandablesDictionary.entrySet()) {
            Rect bounds = entry.getKey();
            Leg leg = entry.getValue();
            if (bounds.contains(x, y)) {
                if (mExpandedTransitLegs.contains(leg)) collapse(leg);
                else expand(leg);
                break;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (Edge edge : mEdges)
            edge.draw(canvas);
        for (PlaceNameText text : mVertexTexts)
            text.draw(canvas);
        for (TransitStopCircle circle : mTransitStopCircles)
            circle.draw(canvas);
        for (Drawable drawable : mVertexDrawables)
            drawable.draw(canvas);
    }









    // Inner helper classes; all implement the "void draw(Canvas canvas)" method

    private class PlaceNameText {

        private String text;

        private TextPaint paint;

        private int startX;

        private int centerY;

        private Rect dimensions;

        private Rect bounds;

        public PlaceNameText(String text, int startX, int centerY, float textSize, int textColor){
            this.text = text;
            this.paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            this.startX = startX;
            this.centerY = centerY;
            paint.setTextSize(textSize);
            paint.setColor(textColor);
            paint.setTextAlign(Paint.Align.CENTER);

            dimensions = new Rect();
            paint.getTextBounds(text, 0, text.length(), dimensions);
            bounds = new Rect(
                    startX + dimensions.left,
                    centerY - dimensions.height()/2,
                    startX + dimensions.right,
                    centerY + dimensions.height()/2
            );
        }

        public PlaceNameText setText(String text) {
            this.text = text;
            paint.getTextBounds(text, 0, text.length(), dimensions);
            bounds.set(
                    startX + dimensions.left,
                    centerY - dimensions.height()/2,
                    startX + dimensions.right,
                    centerY + dimensions.height()/2
            );
            return this;
        }

        public PlaceNameText setStartX(int x) {
            this.startX = x;
            return this;
        }

        public PlaceNameText setCenterY(int y) {
            this.centerY = y;
            return this;
        }

        public PlaceNameText setTextSize(float size) {
            paint.setTextSize(size);
            paint.getTextBounds(text, 0, text.length(), dimensions);
            bounds.set(
                    startX + dimensions.left,
                    centerY - dimensions.height()/2,
                    startX + dimensions.right,
                    centerY + dimensions.height()/2
            );
            return this;
        }

        public PlaceNameText setTextColor(int color) {
            paint.setColor(color);
            return this;
        }

        public String getText() { return text; }

        public TextPaint getPaint() { return paint; }

        public float startX() { return startX; }

        public float getY() {
            return centerY;
        }

        public float getTextSize() { return paint.getTextSize(); }

        public int getTextColor() { return paint.getColor(); }

        public int getHeight() {
            return dimensions.height();
        }

        public int getWidth() {
            return dimensions.width();
        }

        public Rect getBounds() {
           return bounds;
        }

        public boolean isInBounds(int x, int y) {
            return bounds.contains(x, y);
        }

        public void draw(Canvas canvas) {
            canvas.drawText(text, startX + getWidth()/2, centerY + getHeight()/2, paint);
        }

    }

    private class Edge {

        public float top;

        public float bottom;

        public float centerX;

        private Paint paint;

        private Path path;

        public Edge(float top, float bottom, float centerX) {
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.path = new Path();

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(LINE_STROKE_WIDTH);
            path.moveTo(centerX, top);
            path.lineTo(centerX, bottom);

        }

        public Edge setPathEffect(PathEffect pe) {
            paint.setPathEffect(pe);
            return this;
        }

        public Edge setColor(int color) {
            paint.setColor(color);
            return this;
        }

        public Edge setOpacity(int opacity) {
            paint.setAlpha(opacity);
            return this;
        }

        public float getTop() { return top; }

        public float getBottom() { return bottom; }

        public float getCenterX() { return centerX; }

        public PathEffect getPathEffect() { return paint.getPathEffect(); }

        public int getColor(int color) { return paint.getColor(); }

        public int getOpacity(int opacity) { return paint.getAlpha(); }

        public Paint getPaint() { return paint; }

        public void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

    }

    private class ModeAndRouteDrawable extends Drawable {

        private int SPACE_BETWEEN_ICONS = SPACE_BETWEEN_MODE_ICON_AND_ROUTE_ICON;

        private int TEXT_PADDING = 15;

        private final float ROUNDED_RECT_RADIUS = 15;

        private Drawable modeIcon;

        private int modeIconHeight;

        private String routeName;

        private int centerX = 0;

        private int centerY = 0;

        private Rect routeIconDimensions;

        private RectF routeIconPositionBounds;

        private Rect routeTextDimensions; // need to initialize in ctor

        private Point routeTextPostion;

        private TextPaint textPaint;

        private Paint paint;

        private int width;

        private int height;

        public ModeAndRouteDrawable(@NonNull Drawable modeIcon, int modeIconHeight,
                                    int centerX, int centerY,
                                    @NonNull String routeName, int routeColor,
                                    float textSize, int textColor) {

            this.modeIcon = modeIcon;
            this.modeIconHeight = modeIconHeight;
            this.centerX = centerX;
            this.centerY = centerY;
            this.routeName = routeName;

            this.textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            paint.setColor(routeColor);
            textPaint.setTextSize(textSize);
            textPaint.setColor(textColor);
            textPaint.setTextAlign(Paint.Align.CENTER);

            routeTextDimensions = new Rect();

            updateDrawablesBounds();
        }

        public void setCenter(int x, int y) {
            this.centerX = x;
            this.centerY = y;
            updateDrawablesBounds();
        }

        public void setModeIcon(@NonNull Drawable modeIcon) {
            this.modeIcon = modeIcon;
            updateDrawablesBounds();
        }

        public void setRouteName(@NonNull String routeName) {
            this.routeName = routeName;
            updateDrawablesBounds();
        }

        public void setRouteColor(int routeColor) {
            paint.setColor(routeColor);
        }

        public void setTextSize(int textSize) {
            textPaint.setTextSize(textSize);
            updateDrawablesBounds();
        }

        public void setTextColor(int color) {
            textPaint.setColor(color);
        }

        @Override
        public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
            modeIcon.setAlpha(alpha);
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@ColorInt int color, @NonNull PorterDuff.Mode mode) {
            ColorFilter colorFilter = new PorterDuffColorFilter(color, mode);
            setColorFilter(colorFilter);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            modeIcon.setColorFilter(colorFilter);
            paint.setColorFilter(colorFilter);
            textPaint.setColorFilter(colorFilter);
        }

        public int getCenterX() {
            return centerX;
        }

        public int getCenterY() {
            return centerY;
        }

        public Drawable getModeIcon() {
            return modeIcon;
        }

        public int getModeIconHeight() {
            return modeIconHeight;
        }

        public String getRouteName() {
            return routeName;
        }

        public int getRouteColor() {
            return paint.getColor();
        }

        public float getTextSize() { return textPaint.getTextSize(); }

        public int getWidth() { return width; }

        public int getHeight() { return height; }

        @Override
        public int getOpacity() {
            return modeIcon.getOpacity();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            modeIcon.draw(canvas);
            canvas.drawRoundRect(routeIconPositionBounds,
                    ROUNDED_RECT_RADIUS, ROUNDED_RECT_RADIUS, paint);
            canvas.drawText(routeName, routeTextPostion.x, routeTextPostion.y,
                    textPaint);
        }

        private void updateDrawablesBounds() {

            textPaint.getTextBounds(routeName, 0, routeName.length(), routeTextDimensions);
            routeIconDimensions = new Rect(0, 0,
                    routeTextDimensions.width() + 2 * TEXT_PADDING,
                    routeTextDimensions.height() + 2 * TEXT_PADDING);

            int modeIconCenterX =
                    centerX - (int)(.5 * (SPACE_BETWEEN_ICONS + routeIconDimensions.width()));
            int routeIconCenterX =
                    centerX + (int)(.5 * (SPACE_BETWEEN_ICONS + modeIconHeight));
            int routeTextBottomY =
                    centerY + (int)(.5 * routeTextDimensions.height());

            this.modeIcon.setBounds(modeIconCenterX - MODE_ICON_HEIGHT/2,
                    centerY - MODE_ICON_HEIGHT/2,
                    modeIconCenterX + MODE_ICON_HEIGHT/2,
                    centerY + MODE_ICON_HEIGHT/2);

            this.routeIconPositionBounds = new RectF(
                    routeIconCenterX  - routeIconDimensions.width()/2,
                    centerY - routeIconDimensions.height()/2,
                    routeIconCenterX  + routeIconDimensions.width()/2,
                    centerY + routeIconDimensions.height()/2
            );

            this.routeTextPostion = new Point(routeIconCenterX, routeTextBottomY);

            this.width = modeIconHeight
                    + SPACE_BETWEEN_ICONS
                    + routeIconDimensions.width();
            this.height = Math.max(modeIconHeight, routeIconDimensions.height());
        }

    }

    private class TransitStopCircle {

        private float centerX;

        private float centerY;

        private float radius;

        private Paint paint;

        public TransitStopCircle(float centerX, float centerY, float radius, int routeColor) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            this.paint = new Paint();
            this.paint.setColor(routeColor);
        }

        public void draw(Canvas canvas) {
            canvas.drawCircle(centerX, centerY, radius, paint);
        }
    }

}
