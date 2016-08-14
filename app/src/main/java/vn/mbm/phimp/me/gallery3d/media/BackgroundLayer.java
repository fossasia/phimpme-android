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

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.gallery3d.media.RenderView.Lists;
import android.util.Log;

public class BackgroundLayer extends Layer {
    private static final String TAG = "AdaptiveBackground";
    private final GridLayer mGridLayer;
    private CrossFadingTexture mBackground;
    private static final int MAX_ADAPTIVES_TO_KEEP_IN_MEMORY = 16;

    private final HashMap<Texture, AdaptiveBackgroundTexture> mCacheAdaptiveTexture = new HashMap<Texture, AdaptiveBackgroundTexture>();
    private int mCount;
    private int mBackgroundBlitWidth;
    private int mBackgroundOverlap;
    private Texture mFallbackBackground = null;
    private static final float Z_FAR_PLANE = 0.9999f;
    private static final float PARALLAX = 0.5f;
    private static final int ADAPTIVE_BACKGROUND_WIDTH = 256;
    private static final int ADAPTIVE_BACKGROUND_HEIGHT = 128;

    public BackgroundLayer(GridLayer layer) {
        mGridLayer = layer;
    }

    @Override
    public void generate(RenderView view, Lists lists) {
        // lists.blendedList.add(this);
        // lists.updateList.add(this);
        lists.opaqueList.add(this);
    }

    @Override
    public boolean update(RenderView view, float frameInterval) {
        Texture fallback = mFallbackBackground;
        if (fallback == null || !fallback.isLoaded())
            return false;
        CrossFadingTexture background = mBackground;
        if (background == null) {
            background = new CrossFadingTexture(fallback);
            mBackground = background;
        }
        final boolean retVal = background.update(frameInterval);
        int cameraPosition = (int) mGridLayer.getScrollPosition();
        final int backgroundSpacing = mBackgroundBlitWidth - mBackgroundOverlap;
        cameraPosition = (int) ((cameraPosition / backgroundSpacing) * backgroundSpacing);
        final DisplayItem displayItem = mGridLayer.getRepresentativeDisplayItem();
        if (displayItem != null) {
            background.setTexture(getAdaptive(view, displayItem));
        }
        return retVal;
    }

    private Texture getAdaptive(RenderView view, DisplayItem item) {
        if (item == null) {
            return mFallbackBackground;
        }
        Texture itemThumbnail = item.getThumbnailImage(view.getContext(), null);
        if (item == null || itemThumbnail == null || !itemThumbnail.isLoaded()) {
            return mFallbackBackground;
        }
        HashMap<Texture, AdaptiveBackgroundTexture> adaptives = mCacheAdaptiveTexture;
        AdaptiveBackgroundTexture retVal = adaptives.get(itemThumbnail);
        if (retVal == null) {
            retVal = new AdaptiveBackgroundTexture(itemThumbnail, ADAPTIVE_BACKGROUND_WIDTH, ADAPTIVE_BACKGROUND_HEIGHT);
            if (mCount == MAX_ADAPTIVES_TO_KEEP_IN_MEMORY) {
                mCount = 0;
                adaptives.clear();
                Log.i(TAG, "Clearing unused adaptive backgrounds.");
            }
            ++mCount;
            adaptives.put(itemThumbnail, retVal);
        }
        return retVal;
    }

    @SuppressWarnings("static-access")
	@Override
    public void renderOpaque(RenderView view, GL11 gl) {
        gl.glClear(GL11.GL_COLOR_BUFFER_BIT);
        if (mFallbackBackground == null) {
            mFallbackBackground = view.getResource(Res.drawable.default_background, false);
            view.loadTexture(mFallbackBackground);
        }
    }

    @Override
    public void renderBlended(RenderView view, GL11 gl) {
        CrossFadingTexture anchorTexture = mBackground;
        if (mBackground == null || mFallbackBackground == null)
            return;
        gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
        boolean bind = anchorTexture.bind(view, gl);
        if (!bind) {
            view.bind(mFallbackBackground);
        } else {
            Texture texture = anchorTexture.getTexture();
            if (texture != null && texture.isLoaded()) {
                mFallbackBackground = texture;
            }
        }

        // We stitch this crossfading texture, and to cover all cases of overlap
        // we need to perform 3 draws.
        int cameraPosition = (int) (mGridLayer.getScrollPosition() * PARALLAX);
        int backgroundSpacing = mBackgroundBlitWidth - mBackgroundOverlap;
        int anchorEdge = -cameraPosition % (backgroundSpacing);
        int rightEdge = anchorEdge + backgroundSpacing;

        view.draw2D(rightEdge, 0, Z_FAR_PLANE, mBackgroundBlitWidth, mHeight);

        view.draw2D(anchorEdge, 0, Z_FAR_PLANE, mBackgroundBlitWidth, mHeight);

        int leftEdge = anchorEdge - backgroundSpacing;
        view.draw2D(leftEdge, 0, Z_FAR_PLANE, mBackgroundBlitWidth, mHeight);

        if (bind) {
            anchorTexture.unbind(view, gl);
        }

        gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    protected void onSizeChanged() {
        mBackgroundBlitWidth = (int) (mWidth * 1.5f);
        mBackgroundOverlap = (int) (mBackgroundBlitWidth * 0.25f);
    }

    public void clear() {
        clearCache();
        mBackground = null;
        mFallbackBackground = null;
    }

    public void clearCache() {
        mCacheAdaptiveTexture.clear();
    }
}
