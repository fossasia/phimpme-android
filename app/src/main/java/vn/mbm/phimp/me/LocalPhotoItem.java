package vn.mbm.phimp.me;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Padmal on 3/2/17.
 */

public class LocalPhotoItem extends ImageView {

    public LocalPhotoItem(Context context) {
        super(context);
    }

    public LocalPhotoItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LocalPhotoItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}

