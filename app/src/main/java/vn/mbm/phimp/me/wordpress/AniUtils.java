package vn.mbm.phimp.me.wordpress;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class AniUtils {

    public enum Duration {
        SHORT,
        MEDIUM,
        LONG;

        public long toMillis(Context context) {
            switch (this) {
                case LONG:
                    return context.getResources().getInteger(android.R.integer.config_longAnimTime);
                case MEDIUM:
                    return context.getResources().getInteger(android.R.integer.config_mediumAnimTime);
                default:
                    return context.getResources().getInteger(android.R.integer.config_shortAnimTime);
            }
        }
    }

    private AniUtils() {
        throw new AssertionError();
    }

    public static void startAnimation(View target, int aniResId) {
        startAnimation(target, aniResId, null);
    }

    public static void startAnimation(View target, int aniResId, AnimationListener listener) {
        if (target == null) return;

        Animation animation = AnimationUtils.loadAnimation(target.getContext(), aniResId);
        if (animation != null) {
            if (listener != null) {
                animation.setAnimationListener(listener);
            }
            target.startAnimation(animation);
        }
    }

    public static void scale(final View target, float scaleStart, float scaleEnd, Duration duration) {
        if (target == null || duration == null) {
            return;
        }

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, scaleStart, scaleEnd);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, scaleStart, scaleEnd);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, scaleX, scaleY);
        animator.setDuration(duration.toMillis(target.getContext()));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.start();
    }
}
