package vn.mbm.phimp.me.utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by manuja on 6/5/17.
 */

public class OnSwipeTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    public final GestureDetector gestureDetector;
    private boolean down = false;
    private boolean isEnabled = true;

    public OnSwipeTouchListener(Context ctx, boolean down) {
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        this.down = down;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isEnabled) {
            gestureDetector.onTouchEvent(event);
        }
        return false;
    }

    public void onSwipeRight() {
        Log.d("setOnTouchListener", "onSwipeRight");
    }

    public void onSwipeLeft() {
        Log.d("setOnTouchListener", "onSwipeLeft");
    }

    public void onSwipeTop() {
        Log.d("setOnTouchListener", "onSwipeTop");
    }

    public void onSwipeBottom() {
        Log.d("setOnTouchListener", "onSwipeBottom");
    }

    public void onSingleTap() {
        Log.d("setOnTouchListener", "onSingleTap");
    }

    public void onDoubleTapView() {
        Log.d("setOnTouchListener", "onDoubleTap");
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            onSingleTap();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            onDoubleTapView();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return down || super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = super.onFling(e1, e2, velocityX, velocityY);
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}