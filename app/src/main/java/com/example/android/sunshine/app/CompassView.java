package com.example.android.sunshine.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

public class CompassView extends View {

    private int mMyHeight;
    private int mMyWidth;
    private float mDirection;

    private Paint mOuterRing;
    private Paint mInnerRing;
    private Paint mArrow;

    public CompassView(Context context) {
        super(context);
        init();
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompassView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        init();
    }

    private void init() {
        mOuterRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOuterRing.setStyle(Paint.Style.FILL);
        mOuterRing.setColor(getResources().getColor(R.color.sunshine_dark_blue));

        mInnerRing = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerRing.setStyle(Paint.Style.FILL);
        mInnerRing.setColor(getResources().getColor(R.color.sunshine_blue));

        mArrow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArrow.setStyle(Paint.Style.FILL_AND_STROKE);
        mArrow.setColor(getResources().getColor(android.R.color.white));
        mArrow.setStrokeWidth(5f);

        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        mMyHeight = hSpecSize;

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        mMyWidth = wSpecSize;

        if (hSpecMode == MeasureSpec.EXACTLY) {
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
        } else if (hSpecMode == MeasureSpec.UNSPECIFIED) {
        }

        if (wSpecMode == MeasureSpec.EXACTLY) {
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
        } else if (wSpecMode == MeasureSpec.UNSPECIFIED) {
        }

        setMeasuredDimension(mMyWidth, mMyHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cx = mMyHeight/2;
        float cy = mMyWidth/2;
        float outerRadius = mMyHeight/2;
        float innerRadius = mMyHeight/2 * 0.9f;

        canvas.drawCircle(cx, cy, outerRadius, mOuterRing);
        canvas.drawCircle(cx, cy, innerRadius, mInnerRing);

        canvas.drawLine(
                cx,
                cy,
                (float)(cx + innerRadius * Math.sin(Math.toRadians(mDirection))),
                (float)(cy - innerRadius * Math.cos(Math.toRadians(mDirection))),
                mArrow);

    }

    public void setDirection(float dir) {
        mDirection = dir;
        invalidate();
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(String.valueOf(mDirection));
        return true;
    }
}
