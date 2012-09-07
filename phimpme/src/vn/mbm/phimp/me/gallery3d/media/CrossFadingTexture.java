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

import javax.microedition.khronos.opengles.GL11;

import android.util.Log;

public class CrossFadingTexture {
    private static final String TAG = "CrossFadingTexture";

    private Texture mTexture;
    private Texture mFadingTexture;
    private float mMixRatio = 0.0f;
    private float mAnimatedMixRatio = 0.0f;
    private boolean mBindUsingMixed = false;
    private boolean mBind = false;
    private boolean mFadeNecessary = false;

    public CrossFadingTexture() {
    }

    public CrossFadingTexture(Texture initialTexture) {
        mMixRatio = 1.0f;
        mAnimatedMixRatio = 1.0f;
        mFadeNecessary = false;
        mTexture = initialTexture;
        mFadingTexture = initialTexture;
    }

    public void clear() {
        mTexture = null;
        mFadingTexture = null;
    }

    public CrossFadingTexture(Texture source, Texture destination) {
        mFadingTexture = source;
        mTexture = destination;
        mMixRatio = 1.0f;
        mAnimatedMixRatio = 0.0f;
        Log.i(TAG, "Creating crossfading texture");
    }

    public Texture getTexture() {
        return mTexture;
    }

    public void setTexture(Texture texture) {
        if (mTexture == texture || texture == null || mAnimatedMixRatio < 1.0f) {
            return;
        }
        mFadeNecessary = false;
        if (mFadingTexture == null) {
            mFadeNecessary = true;
        }
        if (mTexture != null) {
            mFadingTexture = mTexture;
        } else {
            mFadingTexture = texture;
        }
        mTexture = texture;
        mAnimatedMixRatio = 0.0f;
        mMixRatio = 1.0f;
    }

    public void setTextureImmediate(Texture texture) {
        if (texture == null || texture.isLoaded() == false || mTexture == texture) {
            return;
        }
        if (mTexture != null) {
            mFadingTexture = mTexture;
        }
        mTexture = texture;
        mMixRatio = 1.0f;
    }

    public boolean update(float timeElapsed) {
        if (mTexture != null && mFadingTexture != null && mTexture.isLoaded() && mFadingTexture.isLoaded()) {
            mAnimatedMixRatio = FloatUtils.animate(mAnimatedMixRatio, mMixRatio, timeElapsed * 0.5f);
            return (mMixRatio != mAnimatedMixRatio);
        } else {
            mAnimatedMixRatio = 0.0f;
            return false;
        }
    }

    public boolean bind(RenderView view, GL11 gl) {
        if (mBind) {
            return true; // Already bound.
        }
        if (mFadingTexture != null && mFadingTexture.mState == Texture.STATE_ERROR) {
            mFadingTexture = null;
        }
        if (mTexture != null && mTexture.mState == Texture.STATE_ERROR) {
            mTexture = null;
        }
        mBindUsingMixed = false;
        boolean fadingTextureLoaded = false;
        boolean textureLoaded = false;
        if (mFadingTexture != null) {
            fadingTextureLoaded = view.bind(mFadingTexture);
        }
        if (mTexture != null) {
            view.bind(mTexture);
            textureLoaded = mTexture.isLoaded();
        }
        if (mFadeNecessary) {
            if (view.getAlpha() > mAnimatedMixRatio) {
                view.setAlpha(mAnimatedMixRatio);
            }
            if (mAnimatedMixRatio == 1.0f) {
                mFadeNecessary = false;
            }
        }
        if (textureLoaded == false && fadingTextureLoaded == false) {
            return false;
        }
        mBind = true;
        if (mAnimatedMixRatio <= 0.0f && fadingTextureLoaded) {
            view.bind(mFadingTexture);
        } else if (mAnimatedMixRatio >= 1.0f || !fadingTextureLoaded || view.getAlpha() < mAnimatedMixRatio
                || mFadingTexture == mTexture) {
            view.bind(mTexture);
        } else {
            mBindUsingMixed = true;
            view.bindMixed(mFadingTexture, mTexture, mAnimatedMixRatio);
        }
        return true;
    }

    public void unbind(RenderView view, GL11 gl) {
        if (mBindUsingMixed && mBind) {
            view.unbindMixed();
            mBindUsingMixed = false;
        }
        mBind = false;
    }
}
