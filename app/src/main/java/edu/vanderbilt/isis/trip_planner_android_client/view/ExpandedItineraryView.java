package edu.vanderbilt.isis.trip_planner_android_client.view;

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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Itinerary;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Leg;
import edu.vanderbilt.isis.trip_planner_android_client.model.TripPlanner.TPPlanModel.Place;


/**
 * Created by Anne Zou
 * VUSERP (Vanderbilt University School of Engineering Summer Research) 2017
 * anne.zou@vanderbilt.edu
 */

// TODO: Implement text wrapping for the place names (via StaticLayout?)

/**
 * Custom view to depict in detail the information about an itinerary.
 *
 * The representations of the legs of the itinerary are stacked on top of one another in a vertical
 * chain, in the order that the legs appear in the itinerary, with the first one at the top.
 * The representation of the destination of the trip is at the bottom of the chain.
 *
 * There are two types of legs, each with different representations:
 *
 * 1. a leg whose mode does not have stops (i.e. WALK or BICYCLE or CAR).
 *
 *      This only has one possible representation, which comprises:
 *       - a MODE ICON representing the mode of the leg (or, if the mode is a transit mode, a
 *         COMPOUND ICON composed of a mode icon and a route icon side by side; the route icon
 *         depicts the name and color of the route)
 *       - a TIME TEXT to the left of the mode icon depicting the time at which the leg begins
 *       - a PLACE TEXT to the right of the mode icon depicting the name of the place of origin of the leg
 *       - a LEG SEGMENT, or a vertical line below the mode icon to chain the next mode icon (or, if
 *         it is the last leg, the destination icon)
 *
 * 2. a leg whose mode has stops (i.e. BUS or SUBWAY)
 *
 *      There are two possible representations of this type of leg: expanded or collapsed.
 *      Initially, all the legs whose modes have stops appear in the collapsed representation.
 *      The representation is toggled between expanded and collapsed when the user clicks the
 *      expand/collapse icon or the expand/collapse text.
 *
 *      The COLLAPSED representation comprises:
 *       - a MODE ICON representing the mode of the leg (or, if the mode is a transit mode, a
 *         COMPOUND ICON composed of a mode icon and a route icon side by side; the route icon
 *         depicts the name and color of the route)
 *       - a TIME TEXT to the left of the mode icon depicting the time at which the leg begins
 *       - a PLACE TEXT to the right of the mode icon depicting the name of the place of origin of the leg
 *       - an EXPAND ICON below the place text, aligned with its left border
 *       - an EXPAND/COLLAPSE TEXT, below the place text and to the right of the expand icon,
 *         depicting the # of stops in the leg and the time duration of the leg
 *       - a LEG SEGMENT, a vertical line below the mode icon to chain the next mode icon (or, if
 *         it is the last leg, the destination icon)
 *
 *      The EXPANDED representation comprises:
 *       - a MODE ICON representing the mode of the leg (or, if the mode is a transit mode, a
 *         COMPOUND ICON composed of a mode icon and a route icon side by side; the route icon
 *         depicts the name and color of the route)
 *       - a TIME TEXT to the left of the mode icon depicting the time at which the leg begins
 *       - a PLACE TEXT to the right of the mode icon depicting the name of the place of origin of the leg
 *       - a COLLAPSE ICON below the place text, aligned with its left border
 *       - an EXPAND/COLLAPSE TEXT, below the place text and to the right of the collapse icon,
 *         depicting the # of stops in the leg and the time duration of the leg
 *       - For each stop in the leg:
 *          -- a STOP CIRCLE to represent the stop in the leg, aligned with all the mode icons
 *          -- a STOP INFO TEXT to the right of the transit stop circle depicting the name of the transit stop
 *          -- a STOP SEGMENT, a vertical line (should be shorter than the leg segment) below each
 *             stop circle to chain the next stop circle, or the next mode icon, or the destination icon
 *       - a STOP SEGMENT directly below the mode icon to chain the mode icon to the first stop
 *         circle (or the next mode icon or the destination icon if there is only 1 stop)
 *
 * The representation of the destination goes at the very bottom of the view and comprises:
 *
 *       - a DESTINATION ICON horizontally aligned with all the mode & stop icons in the itinerary
 *         and is connected to the mode or stop icon directly above it by a mode or stop segment
 *       - a TIME TEXT to the left of the destination icon depicting the time of arrival for the trip
 *       - a PLACE TEXT to the right of the destination icon depicting the destination of the trip
 *
 */
public class ExpandedItineraryView extends View {

    private static final String TAG = ExpandedItineraryView.class.getName();

    // Constants are in dp

    // horizontal center of all the mode & stop icons, mode & stop segments, & the destination icon
    private int ICON_CENTER_X = 83;

    // start position for the time texts, left of the leg's mode icon
    private final int TIME_TEXT_START_X = 15;

    // start position for the place texts, right of the leg's mode icon
    private int PLACE_NAME_TEXT_START_X = 115;

    // height of the mode icons & destination icon
    private final int MODE_ICON_HEIGHT = 21;

    // size of the stop circle icon
    private final int STOP_CIRCLE_SIZE = 10;

    // for a transit leg compound icon, the space between the mode and route icon
    private final int SPACE_BETWEEN_MODE_ICON_AND_ROUTE_ICON = 0;

    // size of the time text, left of the leg's mode icon or the destination icon
    private final int TIME_TEXT_SIZE = 10;

    // size of the place text, right of the leg's mode icon or the destination icon
    private final int PLACE_NAME_TEXT_SIZE = 12;

    // size of the stop info text, right of the stop circle icon
    private final int STOP_INFO_TEXT_SIZE = 10;

    // size of the text inside the route icon of a transit leg compound icon, depicting the route
    // name of the transit leg
    private final int ROUTE_ICON_TEXT_SIZE = 12;

    // height of the leg segment that goes below the mode icon of each collapsed or stopless leg
    private final int REGULAR_LEG_SEGMENT_HEIGHT = 60;

    // height of the stop segment that goes below the mode icon of an expanded leg and below each
    // stop circle icon in an expanded leg
    private final int STOP_SEGMENT_HEIGHT = 40;

    // the thickness of the line segments
    private final int LINE_STROKE_WIDTH = 4;

    // height of the expand and collapse icon
    private final int EXPAND_COLLAPSE_ICON_HEIGHT = 38;

    // width of the expand collapse icon
    private final int EXPAND_COLLAPSE_ICON_WIDTH = 25;

    // space between the center of the name-text for a leg that has stops and the center of the
    // expand/collapse icon below it
    private final int SPACE_BETWEEN_LEG_ORIGIN_PLACE_NAME_AND_EXPAND_COLLAPSE_TEXT = 25;

    // the space between the expand/collapse icon and the expand/collapse text to its right
    private final int SPACE_BETWEEN_EXPAND_COLLAPSE_ICON_AND_LABEL = 13;

    // margin of error allowed beyond the top, bottom, left, and right borders of the
    // expand/collapse icon and text that is clickable by the user to expand or collapse the leg
    private final int CLICKABLE_ERROR_PADDING = 5;


    // Context
    private Context mContext;

    // The itinerary upon which to base the contents of this custom view
    private Itinerary mItinerary;

    // The set of legs that have transit stops that are currently expanded in the view
    private Set<Leg> mExpandedStopsLegs;

    // HashMap mapping itinerary legs to the clickable bounds of their corresponding
    // "Expand"/"Collapse" icon and text in the view.
    // Only itinerary legs with stops should be inserted into this map.
    // If the user clicks within these bounds, the representation of the leg in the view is to be
    // expanded (or collapsed if already expanded).
    private HashMap<Leg, Rect> mExpandablesDictionary;

    // List of mode icon & destination icon objects to draw() in the view
    private List<Drawable> mIconDrawables;

    // List of text objects to draw() in the view
    private List<TextDrawable> mTextDrawables;

    // List of line segments objects to draw() in the view
    private List<LineDrawable> mLineDrawables;

    // List of stop icon objects to draw() in the view
    private List<StopCircleDrawable> mStopCircleDrawables;

    // Paint object to draw transit stop icons in the view
    private Paint mStopCirclePaint;


    /** Constructors */

    public ExpandedItineraryView(Context context) {
        this(context, null);
    }

    public ExpandedItineraryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initializes class fields
     * @param context the context
     * @param attrs not used (no xml attributes)
     * @param defStyle not used (no xml styles)
     */
    public ExpandedItineraryView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle); // construct superclass
        mContext = context; // save reference to context

        // Create data structures
        mExpandedStopsLegs = new HashSet<>();
        mExpandablesDictionary = new HashMap<>();
        mIconDrawables = new ArrayList<>();
        mTextDrawables = new ArrayList<>();
        mLineDrawables = new ArrayList<>();
        mStopCircleDrawables = new ArrayList<>();

        // Initialize paint object that will be used to draw all the transit stop icons in the view
        mStopCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStopCirclePaint.setColor(Color.BLACK);
        mStopCirclePaint.setAlpha(MainActivity.DARK_OPACITY);
        mStopCirclePaint.setStyle(Paint.Style.FILL);

        // Add any padding specified in the xml to the x-positions for the icons and place name texts
        ICON_CENTER_X += PixelUtil.dpFromPx(mContext, getPaddingLeft());
        PLACE_NAME_TEXT_START_X += PixelUtil.dpFromPx(mContext, getPaddingLeft());

    }

    /**
     * Resolve the dimensions of the view based on the desired height & width and the constraints
     * imposed by the parent, then setMeasuredDimensions() with the resolved dimensions.
     * @param widthMeasureSpec width constraint imposed by parent
     * @param heightMeasureSpec height constraint imposed by parent
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // Get resolved height
        int heightSize = 0;
        if (mItinerary != null)
            heightSize = resolveSize(getDesiredHeight() + getPaddingTop() + getPaddingBottom(),
                    heightMeasureSpec);

        // Set the resolved dimensions (for width, just use widthMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightSize);
    }

    /**
     * Helper method to calculate the desired height for the view, not including padding.
     * @return the desired height
     */
    private int getDesiredHeight() {
        int height = 0; // initialize height to 0

        // add the height of each leg
        for (Leg leg : mItinerary.getLegs()) {
            if (mExpandedStopsLegs.contains(leg)) {
                // if the leg is expanded, add the height of the mode icon, the stop icons,
                // and the stop segments
                height += PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)
                        + PixelUtil.pxFromDp(mContext, STOP_SEGMENT_HEIGHT)
                        + leg.getIntermediateStops().size() *
                        (PixelUtil.pxFromDp(mContext, STOP_CIRCLE_SIZE)
                                + PixelUtil.pxFromDp(mContext, STOP_SEGMENT_HEIGHT));
            } else {
                // if the leg is not expanded, add the height of the mode icon and the leg segment
                height += PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)
                        + PixelUtil.pxFromDp(mContext, REGULAR_LEG_SEGMENT_HEIGHT);
            }
        }

        // add the height of the destination icon
        height += PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT);

        // return the sum
        return height;
    }

    /**
     * Method to set the padding of the view.
     * Updates the bounds of the elements inside the view, updates the view's layout, and redraws
     * the view.
     * @param left left padding
     * @param top top padding
     * @param right right padding
     * @param bottom bottom padding
     */
    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        updateContentBounds();
        requestLayout();
    }

    /**
     * Getter for the itinerary of this ExpandedItineraryView.
     * @return the itinerary
     */
    public Itinerary getItinerary() {
        return mItinerary;
    }

    /**
     * Setter for the itinerary of this ExpandedItineraryView.
     * Updates the bounds of the elements inside the view, updates the view's layout, and redraws
     * the view.
     * @param itinerary the new itinerary
     */
    public void setItinerary(Itinerary itinerary) {
        mItinerary = itinerary;
        updateContentBounds();
        requestLayout();
    }

    /**
     * Handle a touch event for the view
     * @param event the touch event
     * @return true if the event was handled by the view, false otherwise
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        // On ACTION_DOWN, invoke onClick
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            onClick((int)event.getX(), (int)event.getY());

        // Dispatch the touch event through the superclass (i.e. scrolling)
        return super.dispatchTouchEvent(event);
    }

    /**
     * Handle a click event for the view
     * @param x the x coordinate of the point clicked
     * @param y the y coordinate of the point clicked
     */
    private void onClick(int x, int y) {

        // Loop through the expanded legs
        for (Map.Entry<Leg,Rect> entry : mExpandablesDictionary.entrySet()) {
            Leg leg = entry.getKey();
            Rect bounds = entry.getValue();

            // If the clicked point is within the expand/collapse click bounds for any of
            // the expanded legs, expand or collapse the leg.
            if (bounds.contains(x, y)) {
                if (mExpandedStopsLegs.contains(leg))
                    collapse(leg);
                else
                    expand(leg);
                break;
            }
        }
    }

    /**
     * Expands a leg by adding it to the list of expanded legs.
     * Updates the bounds of the elements inside the view, updates the view's layout, and redraws
     * the view.
     * @param leg the leg to expand
     */
    public void expand(Leg leg) {
        mExpandedStopsLegs.add(leg);
        updateContentBounds();
        requestLayout();
    }

    /**
     * Removes a leg by removing it from the list of expanded legs.
     * Updates the bounds of the elements inside the view, updates the view's layout, and redraws
     * the view.
     * @param leg the leg to expand
     */
    public void collapse(Leg leg) {
        mExpandedStopsLegs.remove(leg);
        updateContentBounds();
        requestLayout();
    }

    /**
     * Helper method to create & specify the new bounds of the objects to be drawn in the view.
     * Each of the objects will be drawn in the draw() method of the ExpandedItineraryView after
     * the view is invalidated.
     */
    private void updateContentBounds() {

        // Nothing to add to the view if the itinerary is null
        if (mItinerary == null)
            return;

        // Clear previous icons, texts, and lines
        mIconDrawables.clear();
        mTextDrawables.clear();
        mLineDrawables.clear();
        mStopCircleDrawables.clear();

        // Initialize y to the padding at the top. This will be incremented as we add items to the
        // view, going from top to bottom.
        int y = getPaddingTop();

        List<Leg> legs = mItinerary.getLegs(); // get the legs of the itinerary

        // Loop through the legs of the itinerary, adding the icons, texts, and segments of
        // each leg to their corresponding lists as we go.
        // The objects we add to the lists encapsulate the positions of the items, and their
        // classes all implement the draw() method.
        // In the draw() method of the ExpandedItineraryView, we will loop through the lists and
        // invoke draw() on each of the objects.
        for (Leg leg : legs) {

            // Add time text for the leg
            mTextDrawables.add(new TextDrawable(
                    getTimeString(leg.getStartTime()),
                    PixelUtil.pxFromDp(mContext, TIME_TEXT_START_X),
                    y + PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2,
                    PixelUtil.pxFromDp(mContext, TIME_TEXT_SIZE),
                    Color.BLACK)
            );

            // Add icon for the leg
            Drawable modeIcon = ModeUtil.getDrawableFromString(leg.getMode()); // get mode icon

            if (ModeUtil.hasStops(leg.getMode())) {

                // If transit, use compound icon
                ModeAndRouteDrawable compoundIcon = new ModeAndRouteDrawable(
                        modeIcon,
                        PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT),
                        PixelUtil.pxFromDp(mContext, ICON_CENTER_X), y + PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2,
                        leg.getRoute(), Color.parseColor("#" + leg.getRouteColor()),
                        PixelUtil.pxFromDp(mContext, ROUTE_ICON_TEXT_SIZE), Color.WHITE);

                mIconDrawables.add(compoundIcon); // add icon to list

            } else {

                // If non-transit, use the regular mode icon
                modeIcon.setBounds(PixelUtil.pxFromDp(mContext, ICON_CENTER_X)
                                - PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2, y,
                        PixelUtil.pxFromDp(mContext, ICON_CENTER_X) +
                                PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2, y
                                + PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT));

                mIconDrawables.add(modeIcon); // add icon to list
            }

            y += PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2; // move y down to the center of the icon


            // Add name text for the leg
            TextDrawable placeName = new TextDrawable(leg.getFrom().getName().toUpperCase(),
                    PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_START_X), y,
                    PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_SIZE), Color.BLACK);

            mTextDrawables.add(placeName); // add text to list

            y += PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2; // move y down to the bottom of the icon


            // If mode has stops, add expand/collapse icon and expand/collapse text
            if (ModeUtil.hasStops(leg.getMode())) {

                // Vertical center for the expand/collapse text
                int expandMessageCenterY = y +
                        PixelUtil.pxFromDp(mContext, SPACE_BETWEEN_LEG_ORIGIN_PLACE_NAME_AND_EXPAND_COLLAPSE_TEXT) / 2;

                // Initialize expand/collapse text (# stops + duration of transit leg)

                // total number of stops
                int numIntermediateStops = ((leg.getIntermediateStops() == null)
                        ? 1 : leg.getIntermediateStops().size() + 1);
                // "stop" vs "stops" (depending on number of stops)
                String sigularOrPluralStops = numIntermediateStops == 1 ? "stop" : "stops";

                // The string to be used in the text
                String expandCollapseTextString = numIntermediateStops + " " + sigularOrPluralStops
                        +" (" + MainActivity.getDurationString(leg.getDuration()) + ")";

                // Create the expand/collapse text
                TextDrawable transitModeInfo = new TextDrawable(expandCollapseTextString,
                        PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_START_X) + EXPAND_COLLAPSE_ICON_WIDTH
                                + PixelUtil.pxFromDp(mContext, SPACE_BETWEEN_EXPAND_COLLAPSE_ICON_AND_LABEL),
                        expandMessageCenterY,
                        PixelUtil.pxFromDp(mContext, STOP_INFO_TEXT_SIZE), Color.BLACK
                );

                mTextDrawables.add(transitModeInfo); // add text to list


                // Add expand/collapse icon if there is more than one stop

                // Check if there is more than 1 stop
                if (leg.getIntermediateStops() != null && !leg.getIntermediateStops().isEmpty()) {

                    // Figure out whether to use the collapsed or expanded drawable for the icon
                    int expandOrCollapseDrawable = mExpandedStopsLegs.contains(leg) ?
                            edu.vanderbilt.isis.trip_planner_android_client.R.drawable.collapse
                            : edu.vanderbilt.isis.trip_planner_android_client.R.drawable.expand;

                    // Get the drawable
                    Drawable expandCollapseIcon = ContextCompat.getDrawable(
                            mContext, expandOrCollapseDrawable)
                            .getConstantState().newDrawable();

                    // Set the opacity for the drawable
                    expandCollapseIcon.setAlpha(MainActivity.DARK_OPACITY);

                    // Set the bounds for the drawable
                    expandCollapseIcon.setBounds(PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_START_X),
                            expandMessageCenterY - PixelUtil.pxFromDp(mContext, EXPAND_COLLAPSE_ICON_HEIGHT) / 2,
                            PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_START_X) + EXPAND_COLLAPSE_ICON_WIDTH,
                            expandMessageCenterY + PixelUtil.pxFromDp(mContext, EXPAND_COLLAPSE_ICON_HEIGHT) / 2);

                    mIconDrawables.add(expandCollapseIcon); // add icon to list


                    // Store clickable bounds for expanding/collapsing the leg's transit stop info
                    Rect clickableBounds = new Rect(expandCollapseIcon.getBounds());
                    clickableBounds.union(transitModeInfo.getBounds());
                    clickableBounds.set(clickableBounds.left - PixelUtil.pxFromDp(mContext, CLICKABLE_ERROR_PADDING),
                            clickableBounds.top - PixelUtil.pxFromDp(mContext, CLICKABLE_ERROR_PADDING),
                            clickableBounds.right + PixelUtil.pxFromDp(mContext, CLICKABLE_ERROR_PADDING),
                            clickableBounds.bottom + PixelUtil.pxFromDp(mContext, CLICKABLE_ERROR_PADDING));

                    // Map the leg to the expand/collapse icon & text clickable bounds
                    mExpandablesDictionary.put(leg, clickableBounds);
                }

            }

            // Add leg segment or stop icons and stop segments
            if (mExpandedStopsLegs.contains(leg)) {

                // If the leg is expanded, add stop icons & line segments

                int routeColor = Color.parseColor("#" + leg.getRouteColor()); // get the route color

                // Add stop segment directly below the mode icon
                mLineDrawables.add(
                        new LineDrawable(
                            y, y + PixelUtil.pxFromDp(mContext, STOP_SEGMENT_HEIGHT),
                            PixelUtil.pxFromDp(mContext, ICON_CENTER_X))
                            .setColor(routeColor)
                );

                y += PixelUtil.pxFromDp(mContext, STOP_SEGMENT_HEIGHT); // move y to the bottom of the segment


                // Add stop icons, stop name texts, and the rest of the stop segments
                for (Place stop : leg.getIntermediateStops()) {

                    // Add stop icon
                    mStopCircleDrawables.add(
                            new StopCircleDrawable(PixelUtil.pxFromDp(mContext, ICON_CENTER_X),
                                y + PixelUtil.pxFromDp(mContext, STOP_CIRCLE_SIZE)/2,
                                PixelUtil.pxFromDp(mContext, STOP_CIRCLE_SIZE)/2,
                                Color.parseColor("#" + leg.getRouteColor()))
                    );
                    y += PixelUtil.pxFromDp(mContext, STOP_CIRCLE_SIZE)/2; // move y to the middle of the icon

                    // Add stop name text
                    mTextDrawables.add(
                            new TextDrawable(stop.getName().toUpperCase(),
                                PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_START_X),
                                y, PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_SIZE), Color.BLACK)
                    );

                    y += PixelUtil.pxFromDp(mContext, STOP_CIRCLE_SIZE)/2; // move y to the bottom of the icon

                    // Add stop segment
                    mLineDrawables.add(
                            new LineDrawable(
                                y, y + PixelUtil.pxFromDp(mContext, STOP_SEGMENT_HEIGHT),
                                PixelUtil.pxFromDp(mContext, ICON_CENTER_X))
                                .setColor(routeColor)
                    );

                    y += PixelUtil.pxFromDp(mContext, STOP_SEGMENT_HEIGHT); // move y to the bottom of the segment
                }

            } else {

                // If leg is not expanded, add a regular leg segment

                // Add leg segment
                mLineDrawables.add(
                        new LineDrawable(
                                y, y + PixelUtil.pxFromDp(mContext, REGULAR_LEG_SEGMENT_HEIGHT),
                                PixelUtil.pxFromDp(mContext, ICON_CENTER_X))
                                .setPathEffect(getPathEffect(leg))
                                .setColor(getColor(leg))
                );

                y += PixelUtil.pxFromDp(mContext, REGULAR_LEG_SEGMENT_HEIGHT); // move y to the bottom of the segment

            }
        }


        // Add destination icon

        // Get drawable
        Drawable destinationIcon = ContextCompat
                .getDrawable(mContext, edu.vanderbilt.isis.trip_planner_android_client
                        .R.drawable.ic_location_on_black_24dp);
        // Set opacity of drawable
        destinationIcon.setAlpha(MainActivity.DARK_OPACITY);
        // Set bounds of drawable
        destinationIcon.setBounds(
                PixelUtil.pxFromDp(mContext, ICON_CENTER_X)
                        - PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2, y,
                PixelUtil.pxFromDp(mContext, ICON_CENTER_X)
                        + PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2,
                y + PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)
        );

        mIconDrawables.add(destinationIcon); // add icon to list

        y += PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2; // Move y to center of icon

        // Add destination time text
        TextDrawable destinationTime = new TextDrawable(
                getTimeString(mItinerary.getEndTime()),
                PixelUtil.pxFromDp(mContext, TIME_TEXT_START_X), y,
                PixelUtil.pxFromDp(mContext, TIME_TEXT_SIZE),
                Color.BLACK);
        mTextDrawables.add(destinationTime); // add text to list

        // Add destination name text
        TextDrawable destinationName = new TextDrawable(
                legs.get(legs.size() - 1).getTo().getName().toUpperCase(),
                PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_START_X), y,
                PixelUtil.pxFromDp(mContext, PLACE_NAME_TEXT_SIZE), Color.BLACK);

        mTextDrawables.add(destinationName); // add text to list

        y += PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2; // Move y to bottom of icon

    }

    /**
     * Invoked after invalidate() or after requestLayout().
     * Redraws the view.
     * @param canvas the canvas to draw onto
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Draw all the segments
        for (LineDrawable LineDrawable : mLineDrawables)
            LineDrawable.draw(canvas);

        // Draw all the texts
        for (TextDrawable text : mTextDrawables)
            text.draw(canvas);

        // Draw all the stop circles
        for (StopCircleDrawable circle : mStopCircleDrawables)
            circle.draw(canvas);

        // Draw all the icons
        for (Drawable drawable : mIconDrawables)
            drawable.draw(canvas);
    }

    /**
     * Helper method to get the path effect for an itinerary leg depending on its mode
     * @param leg the itinerary leg
     * @return the corresponding PathEffect, or null for no path effect
     */
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

    /**
     * Helper method to get the color for an itinerary leg depending on whether it is a transit mode
     * @param leg the itinerary leg
     * @return the corresponding color
     */
    public int getColor(Leg leg) {

        if (ModeUtil.hasStops(leg.getMode()))
            return Color.parseColor("#" + leg.getRouteColor());
        else // if not a transit leg, just use the default colorPrimary color
            return getResources()
                    .getColor(edu.vanderbilt.isis.trip_planner_android_client.R.color.colorPrimary, null);
    }

    /**
     * Convert a timestamp from seconds since epoch to a time string in hh:mm XM format.
     * @param timestamp seconds since epoch
     * @return the time string
     */
    public String getTimeString(long timestamp) {

        Date date = new Date(timestamp); // initialize date object
        int hour = date.getHours(); // get the hour (0-24)
        int minute = date.getMinutes(); // get the minute (0-59)

        String timeString = ""; // start with empty string

        // Get the (1-12 hour)
        int AM_PM_hour = hour % 12;
        if (AM_PM_hour == 0)
            AM_PM_hour = 12;

        timeString += AM_PM_hour; // Append the hour
        timeString += ":"; // Append the :

        // Get the minute in 2-digits
        if (minute < 10)
            timeString += "0";
        timeString +=  minute; // Append the minute
        timeString += " "; // Append a space

        // Append AM or PM
        if (hour < 12)
            timeString += "AM";
        else
            timeString += "PM";

        // Return the string
        return timeString;
    }



    // Below are classes that implement the "draw(Canvas)" method and encapsulate the data needed
    // to draw the item that they represent.
    //
    // In updateContentBounds(), we create keep a list of each of these (as well as a list of
    // Drawables for the regular mode icons) so that we can draw() them later in the draw() method
    // of our ExpandedItineraryView after the view is invalidated.


    /**
     * Texts
     */
    private class TextDrawable {

        private String text;

        private TextPaint paint;

        private int startX;

        private int centerY;

        private Rect dimensions;

        private Rect bounds;

        public TextDrawable(String text, int startX, int centerY, float textSize, int textColor){
            this.text = text;
            this.startX = startX;
            this.centerY = centerY;
            this.paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
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

        /**
         * Draw text to canvas
         * @param canvas the canvas
         */
        public void draw(Canvas canvas) {
            canvas.drawText(text, startX + getWidth()/2, centerY + getHeight()/2, paint);
        }


        // Setters and getters

        public TextDrawable setText(String text) {
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

        public TextDrawable setStartX(int x) {
            this.startX = x;
            return this;
        }

        public TextDrawable setCenterY(int y) {
            this.centerY = y;
            return this;
        }

        public TextDrawable setTextSize(float size) {
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

        public TextDrawable setTextColor(int color) {
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

    }

    /**
     * Segments
     */
    private class LineDrawable {

        public float top;

        public float bottom;

        public float centerX;

        private Paint paint;

        private Path path;

        public LineDrawable(float top, float bottom, float centerX) {
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.path = new Path();

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(PixelUtil.pxFromDp(mContext, LINE_STROKE_WIDTH));
            path.moveTo(centerX, top);
            path.lineTo(centerX, bottom);

        }

        public LineDrawable setPathEffect(PathEffect pe) {
            paint.setPathEffect(pe);
            return this;
        }

        public LineDrawable setColor(int color) {
            paint.setColor(color);
            return this;
        }

        /**
         * Draw line segment to canvas
         * @param canvas the canvas
         */
        public void draw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        public LineDrawable setOpacity(int opacity) {
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

    }

    /**
     * Compound mode & route icons
     */
    private class ModeAndRouteDrawable extends Drawable {

        // Constants are in dp

        private int SPACE_BETWEEN_ICONS = SPACE_BETWEEN_MODE_ICON_AND_ROUTE_ICON;

        private int TEXT_PADDING = 5;

        private final float ROUNDED_RECT_RADIUS = 4;

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

        /**
         * Draw drawable to canvas
         * @param canvas the canvas
         */
        @Override
        public void draw(@NonNull Canvas canvas) {

            // Draw the mode icon
            modeIcon.draw(canvas);

            // Draw the background of the route icon
            canvas.drawRoundRect(routeIconPositionBounds,
                    PixelUtil.pxFromDp(mContext, (int) ROUNDED_RECT_RADIUS),
                    PixelUtil.pxFromDp(mContext, (int) ROUNDED_RECT_RADIUS), paint);
            // Draw the text of the route icon
            canvas.drawText(routeName, routeTextPostion.x, routeTextPostion.y,
                    textPaint);
        }

        private void updateDrawablesBounds() {

            textPaint.getTextBounds(routeName, 0, routeName.length(), routeTextDimensions);
            routeIconDimensions = new Rect(0, 0,
                    routeTextDimensions.width() + 2 * PixelUtil.pxFromDp(mContext, TEXT_PADDING),
                    routeTextDimensions.height() + 2 * PixelUtil.pxFromDp(mContext, TEXT_PADDING));

            int modeIconCenterX =
                    centerX - (int)(.5 * (PixelUtil.pxFromDp(mContext, SPACE_BETWEEN_ICONS) + routeIconDimensions.width()));
            int routeIconCenterX =
                    centerX + (int)(.5 * (PixelUtil.pxFromDp(mContext, SPACE_BETWEEN_ICONS) + modeIconHeight));
            int routeTextBottomY =
                    centerY + (int)(.5 * routeTextDimensions.height());

            this.modeIcon.setBounds(modeIconCenterX - PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2,
                    centerY - PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2,
                    modeIconCenterX + PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2,
                    centerY + PixelUtil.pxFromDp(mContext, MODE_ICON_HEIGHT)/2);

            this.routeIconPositionBounds = new RectF(
                    routeIconCenterX  - routeIconDimensions.width()/2,
                    centerY - routeIconDimensions.height()/2,
                    routeIconCenterX  + routeIconDimensions.width()/2,
                    centerY + routeIconDimensions.height()/2
            );

            this.routeTextPostion = new Point(routeIconCenterX, routeTextBottomY);

            this.width = modeIconHeight
                    + PixelUtil.pxFromDp(mContext, SPACE_BETWEEN_ICONS)
                    + routeIconDimensions.width();
            this.height = Math.max(modeIconHeight, routeIconDimensions.height());
        }

    }

    /**
     * Stop circles
     */
    private class StopCircleDrawable {

        private float centerX;

        private float centerY;

        private float radius;

        private Paint paint;

        public StopCircleDrawable(float centerX, float centerY, float radius, int routeColor) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            this.paint = new Paint();
            this.paint.setColor(routeColor);
        }

        /**
         * Draw stop circle to canvas
         * @param canvas the canvas
         */
        public void draw(Canvas canvas) {
            canvas.drawCircle(centerX, centerY, radius, paint);
        }
    }

}
