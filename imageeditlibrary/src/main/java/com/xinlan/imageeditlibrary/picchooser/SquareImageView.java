package com.xinlan.imageeditlibrary.picchooser;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 正方形的ImageView  强制 宽度 = 高度
 *
 */
public class SquareImageView extends ImageView {

    public SquareImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}//end class
