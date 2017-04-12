package vn.mbm.phimp.me.wordpress;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by rohanagarwal94 on 7/4/17.
 */

public class FadeInNetworkImageView extends NetworkImageView {
    public FadeInNetworkImageView(Context context) {
        super(context);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FadeInNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        if (getContext() == null)
            return;
        int duration = getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

        // use faster property animation if device supports it
        ObjectAnimator alpha = ObjectAnimator.ofFloat(this, View.ALPHA, 0.25f, 1f);
        alpha.setDuration(duration);
        alpha.start();
    }
}
