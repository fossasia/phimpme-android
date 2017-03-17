package vn.mbm.phimp.me.Views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

/**
 * Created by dynamitechetan on 14/03/2017.
 */
public class AutoImageView extends ImageView {
    private boolean adjustViewBounds;

    public AutoImageView(Context context) {
        super(context);
    }

    public AutoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        this.adjustViewBounds = adjustViewBounds;
        super.setAdjustViewBounds(adjustViewBounds);
    }

    private boolean isInScrollingView() {
        ViewParent viewParent = getParent();
        while (viewParent != null && viewParent instanceof ViewGroup) {
            if (((ViewGroup) viewParent).shouldDelayChildPressedState()) {
                return true;
            }
            viewParent = viewParent.getParent();
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (adjustViewBounds) {
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);

            if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
                // Fixed height but adjustable width
                int height = heightSize;
                int width = height * drawableWidth / drawableHeight;
                if (isInScrollingView())
                    setMeasuredDimension(width, height);
                else
                    setMeasuredDimension(Math.min(width, widthSize), Math.min(height, heightSize));
            } else if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
                // Fixed width but adjustable height
                int width = widthSize;
                int height = width * drawableHeight / drawableWidth;
                if (isInScrollingView())
                    setMeasuredDimension(width, height);
                else
                    setMeasuredDimension(Math.min(width, widthSize), Math.min(height, heightSize));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
