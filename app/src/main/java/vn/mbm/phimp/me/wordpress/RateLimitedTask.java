package vn.mbm.phimp.me.wordpress;

import org.wordpress.android.util.DateTimeUtils;

import java.util.Date;

/**
 * Created by rohanagarwal94 on 6/4/17.
 */

public abstract class RateLimitedTask {
    private Date mLastUpdate;
    private int mMinRateInSeconds;

    public RateLimitedTask(int minRateInSeconds) {
        mMinRateInSeconds = minRateInSeconds;
    }

    public synchronized boolean runIfNotLimited() {
        Date now = new Date();
        if (mLastUpdate == null || DateTimeUtils.secondsBetween(now, mLastUpdate) >= mMinRateInSeconds) {
            if (run()) {
                mLastUpdate = now;
                return true;
            }
        }
        return false;
    }

    protected abstract boolean run();
}
