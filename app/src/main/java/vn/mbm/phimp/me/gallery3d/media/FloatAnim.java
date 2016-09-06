/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.media;

import android.util.FloatMath;

public final class FloatAnim {
    private float mValue;
    private float mDelta;
    private float mDuration;
    private long mStartTime;

    public FloatAnim(float value) {
        mValue = value;
        mStartTime = 0;
    }

    public boolean isAnimating() {
        return mStartTime != 0;
    }

    public float getTimeRemaining(long currentTime) {
        float duration = (currentTime - mStartTime) * 0.001f;
        if (mDuration > duration) // CR: braces
            return mDuration - duration;
        else
            return 0.0f;
    }

    public float getValue(long currentTime) {
        if (mStartTime == 0) {
            return mValue;
        } else {
            return getInterpolatedValue(currentTime);
        }
    }

    public void animateValue(float value, float duration, long currentTime) {
        mDelta = getValue(currentTime) - value;
        mValue = value;
        mDuration = duration;
        mStartTime = currentTime;
    }

    public void setValue(float value) {
        mValue = value;
        mStartTime = 0;
    }

    public void skip() {
        mStartTime = 0;
    }

    private float getInterpolatedValue(long currentTime) {
        float ratio = (float) (currentTime - mStartTime) * 0.001f / mDuration;
        if (ratio >= 1f) { // CR: 1.0f
            mStartTime = 0;
            return mValue;
        } else {
            ratio = (float) (0.5f - 0.5f * Math.cos(ratio * 3.14159265f)); // CR:
                                                                      // (float)Math.PI
            return mValue + (1f - ratio) * mDelta;
        }
    }
}
