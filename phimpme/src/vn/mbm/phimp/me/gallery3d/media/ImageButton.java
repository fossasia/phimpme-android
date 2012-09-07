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

import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.gallery3d.media.RenderView.Lists;
import android.os.SystemClock;
import android.view.MotionEvent;

public final class ImageButton extends Layer {
    private static final float TRACKING_MARGIN = 30.0f;

    // Images for the normal and pressed states.
    private int mImage = 0;
    private int mPressedImage = 0;

    // The action to be invoked when a single tap occurs on the button.
    private Runnable mAction = null;

    // Animation state for button crossfades.
    private final FloatAnim mFade = new FloatAnim(1f);
    private int mCurrentImage = 0;
    private int mPreviousImage = 0;
    private boolean mPressed = false;

    @SuppressWarnings("static-access")
	private final int mTransparent = Res.drawable.transparent;

    public void setImages(int image, int pressedImage) {
        mImage = image;
        mPressedImage = pressedImage;
        if (!mPressed) {
            setImage(image, true);
        }
    }

    public final void setAction(Runnable action) {
        mAction = action;
    }

    private boolean containsPoint(float x, float y, boolean addTrackingMargin) {
        if (mImage != 0) {
            float minX = mX;
            float minY = mY;
            float maxX = minX + mWidth;
            float maxY = minY + mHeight;
            if (addTrackingMargin) {
                minX -= TRACKING_MARGIN;
                minY -= TRACKING_MARGIN;
                maxX += TRACKING_MARGIN;
                maxY += TRACKING_MARGIN;
            }
            return x >= minX && y >= minY && x < maxX && y < maxY;
        }
        return false;
    }

    @Override
    public void generate(RenderView view, Lists lists) {
        lists.updateList.add(this);
        lists.blendedList.add(this);
        lists.hitTestList.add(this);
    }

    @Override
    public void renderBlended(RenderView view, GL11 gl) {
        // Get the value of the animation.
        final float ratio = mFade.getValue(view.getFrameTime());
        Texture currentImage = view.getResource(mCurrentImage);
        Texture previousImage = view.getResource(mPreviousImage);
        Texture transparent = view.getResource(mTransparent);
        if (currentImage == null) {
            currentImage = transparent;
        }
        if (previousImage == null) {
            previousImage = transparent;
        }
        if (ratio >= 0.99f) {
            view.draw2D(currentImage, mX, mY);
        } else {
            view.drawMixed2D(previousImage, currentImage, ratio, mX, mY, 0f, currentImage.getWidth(), currentImage.getHeight());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = e.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            final boolean hit = containsPoint(e.getX(), e.getY(), true);
            mPressed = hit;
            if (hit) {
                setImage(mPressedImage, false);
            } else {
                setImage(mImage, true);
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mPressed) {
                if (mAction != null)
                    mAction.run();
            }
        case MotionEvent.ACTION_CANCEL:
            mPressed = false;
            setImage(mImage, true);
            break;
        default:
            break;
        }
        return true;
    }

    private void setImage(int image, boolean animate) {
        if (mCurrentImage != image) {
            if (animate) {
                mFade.setValue(0f);
                mFade.animateValue(1f, 0.25f, SystemClock.uptimeMillis()); // TODO:
                                                                           // use
                                                                           // frame
                                                                           // clock.
                mPreviousImage = mCurrentImage;
            } else {
                mFade.setValue(1f);
            }
            mCurrentImage = image;
        }
    }
}
