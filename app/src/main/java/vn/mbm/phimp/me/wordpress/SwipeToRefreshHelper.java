package vn.mbm.phimp.me.wordpress;

/**
 * Created by rohanagarwal94 on 13/4/17.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.TypedValue;

import org.wordpress.android.util.R;

public class SwipeToRefreshHelper implements OnRefreshListener {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RefreshListener mRefreshListener;
    private boolean mRefreshing;

    public interface RefreshListener {
        public void onRefreshStarted();
    }

    public SwipeToRefreshHelper(Context context, SwipeRefreshLayout swipeRefreshLayout, RefreshListener listener) {
        init(context, swipeRefreshLayout, listener);
    }

    public void init(Context context, SwipeRefreshLayout swipeRefreshLayout, RefreshListener listener) {
        mRefreshListener = listener;
        mSwipeRefreshLayout = swipeRefreshLayout;
        mSwipeRefreshLayout.setOnRefreshListener(this);
        final TypedArray styleAttrs = obtainStyledAttrsFromThemeAttr(context, R.attr.swipeToRefreshStyle,
                R.styleable.RefreshIndicator);
        int color = styleAttrs.getColor(R.styleable.RefreshIndicator_refreshIndicatorColor, context.getResources()
                .getColor(android.R.color.holo_blue_dark));
        mSwipeRefreshLayout.setColorSchemeColors(color, color, color, color);
    }

    public void setRefreshing(boolean refreshing) {
        mRefreshing = refreshing;
        // Delayed refresh, it fixes https://code.google.com/p/android/issues/detail?id=77712
        // 50ms seems a good compromise (always worked during tests) and fast enough so user can't notice the delay
        if (refreshing) {
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // use mRefreshing so if the refresh takes less than 50ms, loading indicator won't show up.
                    mSwipeRefreshLayout.setRefreshing(mRefreshing);
                }
            }, 50);
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public boolean isRefreshing() {
        return mSwipeRefreshLayout.isRefreshing();
    }

    @Override
    public void onRefresh() {
        mRefreshListener.onRefreshStarted();
    }

    public void setEnabled(boolean enabled) {
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    public static TypedArray obtainStyledAttrsFromThemeAttr(Context context, int themeAttr, int[] styleAttrs) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(themeAttr, outValue, true);
        int styleResId = outValue.resourceId;
        return context.obtainStyledAttributes(styleResId, styleAttrs);
    }
}
