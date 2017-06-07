package com.example.anne.otp_android_client_v3.itinerary_display_custom_views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.anne.otp_android_client_v3.R;

import static java.lang.StrictMath.max;


/**
 * Created by Anne on 6/1/2017.
 */

public class ItineraryLegIconView extends View {

    private static final String TAG = "SummarizedItineraryIcon";

    private final int DEFAULT_ROUTE_COLOR = Color.RED;

    private final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private final int DURATION_TEXT_COLOR = Color.BLACK;

    private final String DEFAULT_ROUTE_NAME = "0";

    private final int TEXT_SIZE = 40;

    private final int TEXT_PADDING = 15;

    // TODO: Adjust this paddig according the to mode
    private final int PADDING_BETWEEN_MODE_AND_DURATION = 5;

    private final int SPACE_BETWEEN_ICONS = 10;

    private final float ROUNDED_RECT_RADIUS = 15;

    private final int DURATION_TEXT_SIZE = 30;

    // Width/height of icon; set to -1 for intrinsic dimensions

    private final int ICON_WIDTH = 50;

    // Mode icon

    private Drawable mIcon;

    // Leg duration text

    private Paint mLegDurationPaint;

    private String mLegDurationText = "";

    private PointF mLegDurationTextCoordinates;

    // Whether to show route icon

    private boolean isShowRoute;

    // Route icon

    private Paint mRouteBackgroundPaint;

    private RectF mRouteIconRect;

    // Route name

    private Paint mRouteNamePaint;

    private String mRouteName = DEFAULT_ROUTE_NAME;

    private PointF mRouteTextCoordinates;

    // Constructors

    public ItineraryLegIconView(Context context) {
        this(context, null);
    }

    public ItineraryLegIconView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItineraryLegIconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Get the view's attributes
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ItineraryLegIconView, 0, defStyle);

        // Get the mode icon
        mIcon = a.getDrawable(R.styleable.ItineraryLegIconView_mode_icon);

        // Check if we need to show the public transit route number
        isShowRoute = a.getBoolean(R.styleable.ItineraryLegIconView_show_route_icon, false);
        if (isShowRoute) {
            // Create new Paints for drawing the route icon shape & text
            mRouteBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRouteNamePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

            // Set up the route icon background paint
            mRouteBackgroundPaint.setColor(a
                    .getColor(R.styleable.ItineraryLegIconView_route_icon_color,
                    DEFAULT_ROUTE_COLOR));
            mRouteBackgroundPaint.setStyle(Paint.Style.FILL);

            // Set up the route icon text paint
            mRouteNamePaint.setColor(a
                    .getColor(R.styleable.ItineraryLegIconView_route_number_color,
                    DEFAULT_TEXT_COLOR));
            mRouteNamePaint.setTextAlign(Paint.Align.CENTER);
            mRouteNamePaint.setTextSize(TEXT_SIZE);

            // Get the route name
            mRouteName = a.getString(R.styleable.ItineraryLegIconView_route_name);
            if (mRouteName == null) mRouteName = DEFAULT_ROUTE_NAME;

        } else {
            // Create & set up new TextPaint for drawing the duration text
            mLegDurationPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mLegDurationPaint.setColor(DURATION_TEXT_COLOR);
            mLegDurationPaint.setTextAlign(Paint.Align.CENTER);
            mLegDurationPaint.setTextSize(DURATION_TEXT_SIZE);
        }

        updateContentBounds();

        a.recycle();
    }

    // Overridden callbacks & setter methods

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Get the width measurement
        int widthSize = View.resolveSize(getDesiredWidth(), widthMeasureSpec);

        // Get the height measurement
        int heightSize = View.resolveSize(getDesiredHeight(), heightMeasureSpec);

        // Store the measurements
        setMeasuredDimension(widthSize, heightSize);
    }

    private int getDesiredWidth() {

        int paddingWidth = getPaddingLeft() + getPaddingRight();

        int iconWidth = 0;
        if (mIcon != null)
            iconWidth = getModeIconWidth();

        if (isShowRoute) {
            float textWidth = mRouteNamePaint.measureText(mRouteName, 0 , mRouteName.length());
            return paddingWidth
                    + iconWidth
                    + SPACE_BETWEEN_ICONS
                    + TEXT_PADDING * 2
                    + (int) textWidth;
        } else {
            float durationTextWidth = 0;
            if (mLegDurationPaint != null)
                durationTextWidth = mLegDurationPaint
                        .measureText(mLegDurationText, 0, mLegDurationText.length());
            return paddingWidth
                    + iconWidth
                    + (int) durationTextWidth;
        }
    }

    private int getDesiredHeight() {

        int modeIconHeight = 0;
        int routeIconHeight = 0;

        if (mIcon != null)
            modeIconHeight = getModeIconHeight();

        if (isShowRoute) {
            Rect textBounds = new Rect();
            mRouteNamePaint.getTextBounds(mRouteName, 0, mRouteName.length(), textBounds);
            routeIconHeight = textBounds.height() + TEXT_PADDING * 2;
        }

        return max(modeIconHeight, routeIconHeight) + getPaddingTop() + getPaddingBottom();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh)
            updateContentBounds();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        updateContentBounds();
        invalidate();
    }


    // Modifiers & Accessors

    public Drawable getIcon() {
        return  mIcon;
    }

    public void setIcon(Drawable d) {
        Drawable oldIcon = mIcon;
        mIcon = d;

        if (oldIcon != d) {
            updateContentBounds();
            invalidate();
        }
    }

    public String getLegDuration() {
        return mLegDurationText;
    }

    public void setLegDuration(int duration) {
        Log.d(TAG, "Leg duration set: " + duration + "m");
        mLegDurationText = duration + "";
        updateContentBounds();
        invalidate();
    }

    public boolean isShowRoute() {
        return isShowRoute;
    }

    public void setShowRoute(boolean b) {
        boolean oldIsShowRoute = isShowRoute;

        if (oldIsShowRoute != b) {
            isShowRoute = b;
            updateContentBounds();
            invalidate();
        }
    }

    public String getRouteName() {
        return mRouteName;
    }

    public void setRouteName(String s) {
        String oldRouteName = mRouteName;
        if (oldRouteName != s) {
            mRouteName = s;
            if (isShowRoute) {
                updateContentBounds();
                invalidate();
            }
        }
    }

    public int getRouteColor() {
        if (mRouteBackgroundPaint != null)
            return mRouteBackgroundPaint.getColor();
        else return DEFAULT_ROUTE_COLOR;
    }

    public void setRouteColor(int c) {
        if (mRouteBackgroundPaint == null) {
            mRouteBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRouteBackgroundPaint.setStyle(Paint.Style.FILL);
        }

        int oldColor = mRouteBackgroundPaint.getColor();
        if (oldColor != c) {
            mRouteBackgroundPaint.setColor(c);
            if (isShowRoute)
                invalidate();
        }
    }

    public int getRouteNameColor() {
        if (mRouteNamePaint != null)
            return mRouteNamePaint.getColor();
        else return DEFAULT_TEXT_COLOR;
    }

    public void setRouteNameColor(int c) {
        if (mRouteNamePaint == null) {
            mRouteNamePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            mRouteNamePaint.setTextAlign(Paint.Align.CENTER);
            mRouteNamePaint.setTextSize(TEXT_SIZE);
        }

        int oldColor = mRouteNamePaint.getColor();

        if (oldColor != c) {
            mRouteNamePaint.setColor(c);
            if (isShowRoute)
                invalidate();
        }
    }

    // Helper method to reconfigure draw parameters on state change

    private void updateContentBounds() {

        if (!isShowRoute) {

            // Create new TextPaint for the leg duration text if necessary
            if (mLegDurationPaint == null) {
                mLegDurationPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
                mLegDurationPaint.setTextAlign(Paint.Align.CENTER);
                mLegDurationPaint.setTextSize(DURATION_TEXT_SIZE);
                mLegDurationPaint.setColor(DURATION_TEXT_COLOR);
            }

            Rect durationTextBounds = new Rect();
            mLegDurationPaint.getTextBounds(mLegDurationText, 0 ,mLegDurationText.length(),
                    durationTextBounds);

            // Calculate and set bounds for just the mode icon and the duration
            if (mIcon != null) {

                int leftBound = getWidth()/2 - durationTextBounds.width()/2 - getModeIconWidth()/2;
                int rightBound = getWidth()/2 - durationTextBounds.width()/2 + getModeIconWidth()/2;
                int topBound = getHeight()/2 - getModeIconHeight()/2;
                int bottomBound = getHeight()/2 + getModeIconHeight()/2;
                mIcon.setBounds(leftBound, topBound, rightBound, bottomBound);

                float edgeAnchorX = getWidth() - getPaddingRight() - durationTextBounds.width()/2;
                float modeIconAnchorX = rightBound + PADDING_BETWEEN_MODE_AND_DURATION
                        + durationTextBounds.width()/2;
                mLegDurationTextCoordinates = new PointF(
                        edgeAnchorX < modeIconAnchorX ? edgeAnchorX : modeIconAnchorX,
                        bottomBound);
            }

        } else {

            // Scale and resize the mode drawable if not using the intrinsic dimensions
            if (ICON_WIDTH != -1) {
                Bitmap bitmapResized = Bitmap.createScaledBitmap(
                        ((BitmapDrawable) mIcon).getBitmap(),
                        getModeIconWidth(),
                        getModeIconHeight(),
                        false);
                int opacity = mIcon.getOpacity();
                mIcon = new BitmapDrawable(getResources(), bitmapResized);
                mIcon.setAlpha(opacity);

            }

            // Calculate and set bounds for the mode icon
            if (mIcon != null) {
                int leftBound = getPaddingLeft();
                int rightBound = getPaddingLeft() + getModeIconWidth();
                int topBound = getHeight()/2 - getModeIconHeight()/2;
                int bottomBound = getHeight()/2 + getModeIconHeight()/2;
                mIcon.setBounds(leftBound, topBound, rightBound, bottomBound);
            }

            // Create and initialize paints if necessary
            if (mRouteBackgroundPaint == null) {
                mRouteBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mRouteBackgroundPaint.setColor(DEFAULT_ROUTE_COLOR);
                mRouteBackgroundPaint.setStyle(Paint.Style.FILL);
            }
            if (mRouteNamePaint == null) {
                mRouteNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mRouteNamePaint.setColor(DEFAULT_TEXT_COLOR);
                mRouteNamePaint.setTextAlign(Paint.Align.CENTER);
                mRouteNamePaint.setTextSize(TEXT_SIZE);
            }

            // Calculate bounds for the route icon
            Rect textBounds = new Rect();
            mRouteNamePaint.getTextBounds(mRouteName,0,mRouteName.length(),textBounds);

            float leftBound = getWidth() - getPaddingRight() - TEXT_PADDING * 2 -
                    textBounds.width();
            float rightBound = getWidth() - getPaddingRight();
            float topBound = getHeight()/2 - textBounds.height()/2 - TEXT_PADDING;
            float bottomBound = getHeight()/2 + textBounds.height()/2 + TEXT_PADDING;

            // Save bounds for the route icon
            mRouteIconRect = new RectF(leftBound, topBound, rightBound, bottomBound);

            // Save bounds for route text
            mRouteTextCoordinates = new PointF(mRouteIconRect.centerX(),
                    mRouteIconRect.centerY() + textBounds.height()/2);
        }
    }

    // onDraw

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mIcon != null){
            mIcon.draw(canvas);
            canvas.drawText(mLegDurationText, mLegDurationTextCoordinates.x,
                    mLegDurationTextCoordinates.y, mLegDurationPaint);
        }

        if (isShowRoute) {
            canvas.drawRoundRect(mRouteIconRect, ROUNDED_RECT_RADIUS,
                    ROUNDED_RECT_RADIUS, mRouteBackgroundPaint);
            canvas.drawText(mRouteName,mRouteTextCoordinates.x,
                    mRouteTextCoordinates.y, mRouteNamePaint);
        }

    }

    public int getModeIconWidth() {
        if (ICON_WIDTH == -1 && mIcon != null)
            return mIcon.getIntrinsicWidth();
        else return ICON_WIDTH;
    }

    public int getModeIconHeight() {
        if (ICON_WIDTH == -1 && mIcon != null)
            return mIcon.getIntrinsicHeight();
        else return ICON_WIDTH;
    }

}

