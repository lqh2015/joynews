package com.hzpd.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

/**
 * 快速右滑关闭Activity
 */
public class SwipeCloseLayout extends FrameLayout {
    public SwipeCloseLayout(Context context) {
        super(context);
    }

    public SwipeCloseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeCloseLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
        mTouchSlop = ViewConfiguration.get(activity).getScaledTouchSlop();
        mMaxVelocity = ViewConfiguration.get(activity).getScaledMaximumFlingVelocity();
    }

    private int mTouchSlop;
    private int downX;
    private int downY;
    private int tempX;
    private VelocityTracker velocityTracker;
    private int mPointerId;
    private int mMaxVelocity;
    boolean isFinish = false;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (activity == null) {
            return false;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = tempX = (int) ev.getRawX();
                downY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getRawX();
                // 满足此条件屏蔽SildingFinishLayout里面子类的touch事件
                if (moveX - downX > mTouchSlop
                        && Math.abs((int) ev.getRawY() - downY) < mTouchSlop) {
                    return true;
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPointerId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getRawX();
                int deltaX = tempX - moveX;
                tempX = moveX;
                if (moveX - downX > mTouchSlop
                        && Math.abs((int) event.getRawY() - downY) < mTouchSlop) {
                }
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker tracker = velocityTracker;
                tracker.computeCurrentVelocity(1000, mMaxVelocity);
                float velocityX = tracker.getXVelocity(mPointerId);
                float velocityY = tracker.getYVelocity(mPointerId);
                if (event.getX() - downX >= getWidth() / 2
                        || (velocityX > velocityY && velocityX > getWidth() / 3 * 2)) {
                    isFinish = true;
                }
                if (null != velocityTracker) {
                    velocityTracker.clear();
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (null != velocityTracker) {
                    velocityTracker.clear();
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
        }

        if (activity != null && isFinish) {
            activity.finish();
            activity = null;
            isFinish = false;
        }
        return true;
    }
}
