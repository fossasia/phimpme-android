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

import android.os.Bundle;

public final class GridCamera {
    public static final float MAX_CAMERA_SPEED = 12.0f;
    public static final float EYE_CONVERGENCE_SPEED = 3.0f;
    public static final float EYE_X = 0;
    public static final float EYE_Y = 0;
    public static final float EYE_Z = 8.0f; // Initial z distance.
    private static final float DEFAULT_PORTRAIT_ASPECT = 320.0f / 480.0f;
    private static final float DEFAULT_LANDSCAPE_ASPECT = 1.0f / DEFAULT_PORTRAIT_ASPECT;

    public float mEyeX;
    public float mEyeY;
    public float mEyeZ;
    public float mLookAtX;
    public float mLookAtY;
    public float mLookAtZ;
    public float mUpX;
    public float mUpY;
    public float mUpZ;

    // To tilt the wall.
    public float mEyeOffsetX;
    public float mEyeOffsetY;
    private float mEyeEdgeOffsetX;
    private float mEyeEdgeOffsetXAnim;
    private float mAmountExceeding;

    // Animation speed, 1.0f is normal speed.
    public float mConvergenceSpeed;

    // Camera field of view and its relation to the grid item width.
    public float mFov;
    public float mScale;
    public float mOneByScale;
    public int mWidth;
    public int mHeight;
    public int mItemHeight;
    public int mItemWidth;
    public float mAspectRatio;
    public float mDefaultAspectRatio;

    // Camera positional information.
    private float mPosX;
    private float mPosY;
    private float mPosZ;
    private float mTargetPosX;
    private float mTargetPosY;
    private float mTargetPosZ;
    private float mEyeOffsetAnimX;
    private float mEyeOffsetAnimY;
    private float mTargetEyeX;

    // Screen width and height.
    private int mWidthBy2;
    private int mHeightBy2;
    private float mTanFovBy2;
    public float mFriction;

    public GridCamera(int width, int height, int itemWidth, int itemHeight) {
        reset();
        viewportChanged(width, height, itemWidth, itemHeight);
        mConvergenceSpeed = 1.0f;
        mFriction = 0.0f;
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mEyeX = savedInstanceState.getInt(new String("Camera.mEyeX")) + EYE_X;
        mTargetPosX = savedInstanceState.getFloat(new String("Camera.mTargetPosX"));
        mTargetPosY = savedInstanceState.getFloat(new String("Camera.mTargetPosY"));
        mTargetPosZ = savedInstanceState.getFloat(new String("Camera.mTargetPosZ"));
        commitMove();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putFloat(new String("Camera.mEyeX"), mEyeX - EYE_X);
        outState.putFloat(new String("Camera.mTargetPosX"), mTargetPosX);
        outState.putFloat(new String("Camera.mTargetPosY"), mTargetPosY);
        outState.putFloat(new String("Camera.mTargetPosZ"), mTargetPosZ);
    }

    public void reset() {
        mTargetEyeX = 0;
        mEyeX = EYE_X;
        mEyeY = EYE_Y;
        mEyeZ = EYE_Z;
        mLookAtX = EYE_X;
        mLookAtY = EYE_Y;
        mLookAtZ = 0;
        mUpX = 0;
        mUpY = 1.0f;
        mUpZ = 0;
        mPosX = 0;
        mPosY = 0;
        mPosZ = 0;
        mTargetPosX = 0;
        mTargetPosY = 0;
        mTargetPosZ = 0;
    }

    public void viewportChanged(int w, int h, float itemWidth, float itemHeight) {
        // For pixel precision we need to use this formula.
        /* fov = 2tan-1(qFactor/2*defaultZ) where qFactor = height/ItemHeight */
        float qFactor = h / (float) itemHeight;
        float fov = 2.0f * (float) Math.toDegrees(Math.atan2(qFactor / 2, GridCamera.EYE_Z));
        mWidth = w;
        mHeight = h;
        mWidthBy2 = w >> 1;
        mHeightBy2 = h >> 1;
        mAspectRatio = (h == 0) ? 1.0f : (float) w / (float) h;
        mDefaultAspectRatio = (w > h) ? DEFAULT_LANDSCAPE_ASPECT : DEFAULT_PORTRAIT_ASPECT;
        mTanFovBy2 = (float) Math.tan(Math.toRadians(fov * 0.5f));
        mItemHeight = (int) itemHeight;
        mItemWidth = (int) itemWidth;
        mScale = itemHeight;
        mOneByScale = 1.0f / (float) itemHeight;
        mFov = fov;
    }

    public void convertToScreenSpace(int posX, int posY, int posZ, Vector3f retVal) {
        // TODO
    }

    public void convertToCameraSpace(float posX, float posY, float posZ, Vector3f retVal) {
        float posXx = posX - mWidthBy2;
        float posYx = posY - mHeightBy2;
        convertToRelativeCameraSpace(posXx, posYx, posZ, retVal);
        retVal.x += (EYE_X + mTargetPosX);
        retVal.y += (mTargetPosY);
    }

    public void convertToRelativeCameraSpace(float posX, float posY, float posZ, Vector3f retVal) {
        float posXx = posX;
        float posYx = posY;
        posXx = posXx / mWidth;
        posYx = posYx / mHeight;
        float posZx = posZ;
        float zDiscriminant = (mTanFovBy2 * (mTargetPosZ + EYE_Z + posZx));
        zDiscriminant *= 2.0f;
        float yRange = zDiscriminant;
        float xRange = zDiscriminant * mAspectRatio;
        posXx = ((posXx * xRange));
        posYx = ((posYx * yRange));
        retVal.x = posXx;
        retVal.y = posYx;
    }

    public float getDistanceToFitRect(float f, float g) {
        final float thisAspectRatio = (float) f / (float) g;
        float h = g;
        if (thisAspectRatio > mAspectRatio) {
            // The width will hit the screen.
            h = (f * mHeight) / mWidth;
        }
        // To fit ITEM_HEIGHT pixels perfectly, the targetZ value must be the
        // 1.0f for the given fov
        // Thus to fit h pixels,
        h = h / mItemHeight;
        float targetZ = h / mTanFovBy2;
        targetZ = targetZ * 0.5f;
        return -(EYE_Z - targetZ);
    }

    public void moveXTo(float posX) {
        mTargetPosX = posX;
    }

    public void moveYTo(float posY) {
        mTargetPosY = posY;
    }

    public void moveZTo(float posZ) {
        mTargetPosZ = posZ;
    }

    public void moveTo(float posX, float posY, float posZ) {
        float delta = posX - mTargetPosX;
        float maxDelta = mWidth * 2.0f * mOneByScale;
        delta = FloatUtils.clamp(delta, -maxDelta, maxDelta);
        mTargetPosX += delta;
        mTargetPosY = posY;
        mTargetPosZ = posZ;
    }

    public void moveBy(float posX, float posY, float posZ) {
        moveTo(posX + mTargetPosX, posY + mTargetPosY, posZ + mTargetPosZ);
    }

    public void commitMove() {
        mPosX = mTargetPosX;
        mPosY = mTargetPosY;
        mPosZ = mTargetPosZ;
    }

    public void commitMoveInX() {
        mPosX = mTargetPosX;
    }

    public void commitMoveInY() {
        mPosY = mTargetPosY;
    }

    public void commitMoveInZ() {
        mPosZ = mTargetPosZ;
    }

    public boolean computeConstraints(boolean applyConstraints, boolean applyOverflowFeedback, Vector3f firstSlotPosition,
            Vector3f lastSlotPosition) {
        boolean retVal = false;
        float minX = (firstSlotPosition.x) * (1.0f / mItemHeight);
        float maxX = (lastSlotPosition.x) * (1.0f / mItemHeight);
        if (mTargetPosX < minX) {
            mAmountExceeding += mTargetPosX - minX;
            mTargetPosX = minX;
            mPosX = minX;
            if (applyConstraints) {
                mTargetPosX = minX;
                mFriction = 0.0f;
            }
            retVal = true;
        }
        if (mTargetPosX > maxX) {
            mAmountExceeding += mTargetPosX - maxX;
            mTargetPosX = maxX;
            mPosX = maxX;
            if (applyConstraints) {
                mTargetPosX = maxX;
                mFriction = 0.0f;
            }
            retVal = true;
        }
        if (!retVal) {
            float scrollingFromEdgeX = 0.0f;
            if (mAmountExceeding < 0.0f) {
                scrollingFromEdgeX = mTargetPosX - minX;
            } else {
                scrollingFromEdgeX = maxX - mTargetPosX;
            }
            if (scrollingFromEdgeX > 0.1f) {
                mAmountExceeding = 0.0f;
            }
        }
        if (applyConstraints) {
            mEyeEdgeOffsetX = 0.0f;
            // We look at amount exceeding and calculate target position in the
            // reverse direction.
            final float maxBounceBack = 0.8f;
            if (mAmountExceeding < -maxBounceBack)
                mAmountExceeding = -maxBounceBack;
            if (mAmountExceeding > maxBounceBack)
                mAmountExceeding = maxBounceBack;
            //mTargetPosX -= mAmountExceeding * 0.8f;
            if (mTargetPosX > maxX)
                mTargetPosX = maxX;
            if (mTargetPosX < minX)
                mTargetPosX = minX;
            mAmountExceeding = 0.0f;
        } else {
            float amountExceedingToUse = mAmountExceeding;
            final float maxThreshold = 0.6f;
            if (amountExceedingToUse > maxThreshold)
                amountExceedingToUse = maxThreshold;
            if (amountExceedingToUse < -maxThreshold)
                amountExceedingToUse = -maxThreshold;
            if (applyOverflowFeedback)
                mEyeEdgeOffsetX = -10.0f * amountExceedingToUse;
            else
                mEyeEdgeOffsetX = 0.0f;
        }
        return retVal;
    }

    public void stopMovement() {
        mTargetPosX = mPosX;
        mTargetPosY = mPosY;
        mTargetPosZ = mPosZ;
    }

    public void stopMovementInX() {
        mTargetPosX = mPosX;
    }

    public void stopMovementInY() {
        mTargetPosY = mPosY;
    }

    public void stopMovementInZ() {
        mTargetPosZ = mPosZ;
    }

    public boolean isAnimating() {
        return (mPosX != mTargetPosX || mPosY != mTargetPosY || mPosZ != mTargetPosZ || mEyeOffsetAnimX != mEyeOffsetX || mEyeEdgeOffsetXAnim != mEyeEdgeOffsetX);
    }

    public boolean isZAnimating() {
        return mPosZ != mTargetPosZ;
    }

    public void update(float timeElapsed) {
        float factor = mConvergenceSpeed;
        timeElapsed = (timeElapsed * factor);
        float oldPosX = mPosX;
        mPosX = FloatUtils.animate(mPosX, mTargetPosX, timeElapsed);
        float diff = mPosX - oldPosX;
        if (diff == 0)
            mFriction = 0.0f;
        mTargetPosX += (diff * mFriction);
        mPosY = FloatUtils.animate(mPosY, mTargetPosY, timeElapsed);
        mPosZ = FloatUtils.animate(mPosZ, mTargetPosZ, timeElapsed);
        if (mEyeZ != EYE_Z) {
            mEyeOffsetX = 0;
            mEyeOffsetY = 0;
        }
        mEyeOffsetAnimX = FloatUtils.animate(mEyeOffsetAnimX, mEyeOffsetX, timeElapsed);
        mEyeOffsetAnimY = FloatUtils.animate(mEyeOffsetAnimY, mEyeOffsetY, timeElapsed);
        mEyeEdgeOffsetXAnim = FloatUtils.animate(mEyeEdgeOffsetXAnim, mEyeEdgeOffsetX, timeElapsed);
        mTargetEyeX = EYE_X + mPosX;
        if (mEyeZ == EYE_Z) {
            mEyeX = mTargetEyeX;
            // Enable the line below for achieving tilt while you scroll the
            // wall.
            // FloatUtils.animate(eyeX_, targetEyeX_, timeElapsedx -
            // (timeElapsedx * 0.35f));
        } else {
            mEyeX = mTargetEyeX;
        }
        mEyeX += (mEyeOffsetAnimX + mEyeEdgeOffsetXAnim);
        mLookAtX = EYE_X + mPosX;
        mEyeY = EYE_Y + mPosY;
        mLookAtY = EYE_Y + mPosY;
        mEyeZ = EYE_Z + mPosZ;
        mLookAtZ = mPosZ;
    }
}
