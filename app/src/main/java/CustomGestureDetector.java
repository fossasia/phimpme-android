import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by manuja on 6/5/17.
 */

public class CustomGestureDetector implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {
    private TextView mGestureText;
    private GestureDetector mGestureDetector;
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        mGestureText.setText("onSingleTapConfirmed");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        mGestureText.setText("onDoubleTap");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        mGestureText.setText("onDoubleTapEvent");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        mGestureText.setText("onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        mGestureText.setText("onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        mGestureText.setText("onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        mGestureText.setText("onScroll");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        mGestureText.setText("onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        mGestureText.setText("onFling");
        return true;
    }
}