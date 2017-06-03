package com.example.anne.otp_android_client_v3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import static java.lang.StrictMath.max;


/**
 * Created by Anne on 6/1/2017.
 */


public class SlidingLayoutHeadIcon extends View {

    private final int DEFAULT_ROUTE_COLOR = Color.RED;

    private final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private final String DEFAULT_ROUTE_NAME = "0";

    private final int TEXT_SIZE = 40;

    private final int TEXT_PADDING = 15;

    private final int SPACE_BETWEEN_ICONS = 10;

    private final float ROUNDED_RECT_RADIUS = 15;

    // Mode icon

    private Drawable mIcon;

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

    public SlidingLayoutHeadIcon(Context context) {
        this(context, null);
    }

    public SlidingLayoutHeadIcon(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingLayoutHeadIcon(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Get the view's attributes
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SlidingLayoutHeadIcon, 0, defStyle);

        // Get the mode icon
        mIcon = a.getDrawable(R.styleable.SlidingLayoutHeadIcon_mode_icon);

        // Check if we need to show a public transit route number
        isShowRoute = a.getBoolean(R.styleable.SlidingLayoutHeadIcon_show_route_icon, false);

        if (isShowRoute) {
            // Create new Paints for drawing the route icon shape & text
            mRouteBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRouteNamePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

            // Get the route name
            mRouteName = a.getString(R.styleable.SlidingLayoutHeadIcon_route_name);
            if (mRouteName == null) mRouteName = DEFAULT_ROUTE_NAME;

            // Get the route icon shape & text colors
            mRouteBackgroundPaint.setColor(a.getColor(R.styleable.SlidingLayoutHeadIcon_route_icon_color,
                    DEFAULT_ROUTE_COLOR));
            mRouteNamePaint.setColor(a.getColor(R.styleable.SlidingLayoutHeadIcon_route_number_color,
                    DEFAULT_TEXT_COLOR));

            mRouteBackgroundPaint.setStyle(Paint.Style.FILL);
            mRouteNamePaint.setTextAlign(Paint.Align.CENTER);
            mRouteNamePaint.setTextSize(TEXT_SIZE);
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
            iconWidth = mIcon.getIntrinsicWidth();

        if (isShowRoute) {
            float textWidth = mRouteNamePaint.measureText(mRouteName, 0 , mRouteName.length());
            return paddingWidth
                    + iconWidth
                    + SPACE_BETWEEN_ICONS
                    + TEXT_PADDING * 2
                    + (int) textWidth;
        } else {
            return paddingWidth + iconWidth;
        }
    }

    private int getDesiredHeight() {

        int modeIconHeight = 0;
        int routeIconHeight = 0;

        if (mIcon != null)
            modeIconHeight = mIcon.getIntrinsicHeight();

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

        // Calculate and save bounds for the route icon
        if (!isShowRoute) {
            // Calculate and set bounds for the mode icon
            if (mIcon != null) {
                int leftBound = getWidth()/2 - mIcon.getIntrinsicWidth()/2;
                int rightBound = getWidth()/2 + mIcon.getIntrinsicWidth()/2;
                int topBound = getHeight()/2 - mIcon.getIntrinsicHeight()/2;
                int bottomBound = getHeight()/2 + mIcon.getIntrinsicHeight()/2;
                mIcon.setBounds(leftBound, topBound, rightBound, bottomBound);
            }
        } else {

            // Calculate and set bounds for the mode icon
            if (mIcon != null) {
                int leftBound = getPaddingLeft();
                int rightBound = getPaddingLeft() + mIcon.getIntrinsicWidth();
                int topBound = getHeight()/2 - mIcon.getIntrinsicHeight()/2;
                int bottomBound = getHeight()/2 + mIcon.getIntrinsicHeight()/2;
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

        if (mIcon != null)
            mIcon.draw(canvas);

        if (isShowRoute) {
            canvas.drawRoundRect(mRouteIconRect, ROUNDED_RECT_RADIUS,
                    ROUNDED_RECT_RADIUS, mRouteBackgroundPaint);
            canvas.drawText(mRouteName,mRouteTextCoordinates.x,
                    mRouteTextCoordinates.y, mRouteNamePaint);
        }

    }

}

