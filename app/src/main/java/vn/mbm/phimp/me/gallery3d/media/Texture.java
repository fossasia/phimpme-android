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

import android.graphics.Bitmap;

public abstract class Texture {

    public static final int STATE_UNLOADED = 0;
    public static final int STATE_QUEUED = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_LOADED = 3;
    public static final int STATE_ERROR = 4;

    int mState = STATE_UNLOADED;
    int mId;
    int mWidth;
    int mHeight;
    float mNormalizedWidth;
    float mNormalizedHeight;
    Bitmap mBitmap;

    public boolean isCached() {
        return false;
    }

    public final void clear() {
        mId = 0;
        mState = STATE_UNLOADED;
        mWidth = 0;
        mHeight = 0;
        mNormalizedWidth = 0;
        mNormalizedHeight = 0;
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public final boolean isLoaded() {
        return mState == STATE_LOADED;
    }

    public final int getState() {
        return mState;
    }

    public final int getWidth() {
        return mWidth;
    }

    public final int getHeight() {
        return mHeight;
    }

    public final float getNormalizedWidth() {
        return mNormalizedWidth;
    }

    public final float getNormalizedHeight() {
        return mNormalizedHeight;
    }

    /** If this returns true, the texture will be enqueued. */
    protected boolean shouldQueue() {
        return true;
    }

    /** Returns a bitmap, or null if an error occurs. */
    protected abstract Bitmap load(RenderView view);

    public boolean isUncachedVideo() {
        return false;
    }
}
