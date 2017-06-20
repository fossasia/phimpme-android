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

import vn.mbm.phimp.me.gallery3d.app.App;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.SystemClock;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

public final class GridInputProcessor implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
        ScaleGestureDetector.OnScaleGestureListener {
    private int mCurrentFocusSlot;
    private boolean mCurrentFocusIsPressed;
    private int mCurrentSelectedSlot;

    private float mPrevTiltValueLowPass;
    private float mPrevShakeValueHighPass;
    private float mShakeValue;
    private int mTouchPosX;
    private int mTouchPosY;
    private int mActionCode;
    private long mPrevTouchTime;
    private float mFirstTouchPosX;
    private float mFirstTouchPosY;
    private float mPrevTouchPosX;
    private float mPrevTouchPosY;
    private float mTouchVelX;
    private float mTouchVelY;
    private boolean mProcessTouch;
    private boolean mTouchMoved;
    private float mDpadIgnoreTime = 0.0f;
    private GridCamera mCamera;
    private GridLayer mLayer;
    private Context mContext;
    private Pool<Vector3f> mPool;
    private DisplayItem[] mDisplayItems;
    private boolean mPrevHitEdge;
    private boolean mTouchFeedbackDelivered;
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mZoomGesture;
    private int mCurrentScaleSlot;
    private float mScale;

    public GridInputProcessor(Context context, GridCamera camera, GridLayer layer, RenderView view, Pool<Vector3f> pool,
            DisplayItem[] displayItems) {
        mPool = pool;
        mCamera = camera;
        mLayer = layer;
        mCurrentFocusSlot = Shared.INVALID;
        mCurrentSelectedSlot = Shared.INVALID;
        mCurrentScaleSlot = Shared.INVALID;
        mContext = context;
        mDisplayItems = displayItems;
        mGestureDetector = new GestureDetector(context, this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        mGestureDetector.setIsLongpressEnabled(true);
        mZoomGesture = false;
        mScale = 1.0f;
    }

    public int getCurrentFocusSlot() {
        return mCurrentFocusSlot;
    }

    public int getCurrentSelectedSlot() {
        return mCurrentSelectedSlot;
    }

    public int getCurrentScaledSlot() {
        return mCurrentScaleSlot;
    }

    public void setCurrentSelectedSlot(int slot) {
        mCurrentSelectedSlot = slot;
        GridLayer layer = mLayer;
        layer.setState(GridLayer.STATE_FULL_SCREEN);
        mCamera.mConvergenceSpeed = 2.0f;
        mCamera.mFriction = 0.0f;
        DisplayItem displayItem = layer.getDisplayItemForSlotId(slot);
        MediaItem item = null;
        if (displayItem != null)
            item = displayItem.mItemRef;
        layer.getHud().fullscreenSelectionChanged(item, mCurrentSelectedSlot + 1, layer.getCompleteRange().end + 1);
    }

    public void onSensorChanged(RenderView view, SensorEvent event, int state) {
        if (mZoomGesture)
            return;
        switch (event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            float[] values = event.values;
            float valueToUse = (mCamera.mWidth < mCamera.mHeight) ? values[0] : -values[1];
            float tiltValue = 0.8f * mPrevTiltValueLowPass + 0.2f * valueToUse;
            if (Math.abs(tiltValue) < 0.5f)
                tiltValue = 0.0f;
            if (state == GridLayer.STATE_FULL_SCREEN)
                tiltValue = 0.0f;
            if (tiltValue != 0.0f)
                view.requestRender();
            mCamera.mEyeOffsetX = -3.0f * tiltValue;
            float shakeValue = values[1] * values[1] + values[2] * values[2];
            mShakeValue = shakeValue - mPrevShakeValueHighPass;
            mPrevShakeValueHighPass = shakeValue;
            if (mShakeValue < 16.0f) {
                mShakeValue = 0;
            } else {
                mShakeValue = mShakeValue * 4.0f;
                if (mShakeValue > 200) {
                    mShakeValue = 200;
                }
            }
            break;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        mTouchPosX = (int) (event.getX());
        mTouchPosY = (int) (event.getY());
        mActionCode = event.getAction();
        long timestamp = SystemClock.elapsedRealtime();
        long delta = timestamp - mPrevTouchTime;
        mPrevTouchTime = timestamp;
        float timeElapsed = (float) delta;
        timeElapsed = timeElapsed * 0.001f; // division by 1000 for seconds
        switch (mActionCode) {
        case MotionEvent.ACTION_UP:
            touchEnded(mTouchPosX, mTouchPosY, timeElapsed);
            break;
        case MotionEvent.ACTION_DOWN:
            mPrevTouchTime = timestamp;
            touchBegan(mTouchPosX, mTouchPosY);
            break;
        case MotionEvent.ACTION_MOVE:
            touchMoved(mTouchPosX, mTouchPosY, timeElapsed);
            break;
        }
        if (!mZoomGesture)
            mGestureDetector.onTouchEvent(event);
        mScaleGestureDetector.onTouchEvent(event);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, int state) {
        GridLayer layer = mLayer;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layer.getViewIntent())
                return false;
            if (layer.getHud().getMode() == HudLayer.MODE_SELECT) {
                layer.deselectAll();
                return true;
            }
            if (layer.inSlideShowMode()) {
                layer.endSlideshow();
                layer.getHud().setAlpha(1.0f);
                return true;
            }
            float zoomValue = layer.getZoomValue();
            if (zoomValue != 1.0f) {
                layer.setZoomValue(1.0f);
                layer.centerCameraForSlot(mCurrentSelectedSlot, 1.0f);
                return true;
            }
            layer.goBack();
            if (state == GridLayer.STATE_MEDIA_SETS)
                return false;
            return true;
        }
        if (mDpadIgnoreTime < 0.1f)
            return true;
        mDpadIgnoreTime = 0.0f;
        IndexRange bufferedVisibleRange = layer.getBufferedVisibleRange();
        int firstBufferedVisibleSlot = bufferedVisibleRange.begin;
        int lastBufferedVisibleSlot = bufferedVisibleRange.end;
        int anchorSlot = layer.getAnchorSlotIndex(GridLayer.ANCHOR_CENTER);
        if (state == GridLayer.STATE_FULL_SCREEN) {
            if (keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                    && keyCode != KeyEvent.KEYCODE_MUTE && keyCode != KeyEvent.KEYCODE_HEADSETHOOK
                    && keyCode != KeyEvent.KEYCODE_NOTIFICATION) {
                layer.endSlideshow();
            }
            boolean needsVibrate = false;
            boolean directionalKeyPressed = false;
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                needsVibrate = !layer.changeFocusToNextSlot(1.0f);
                directionalKeyPressed = true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                needsVibrate = !layer.changeFocusToPreviousSlot(1.0f);
                directionalKeyPressed = true;
            }
            if (directionalKeyPressed) {
                if (layer.getHud().getMode() == HudLayer.MODE_SELECT) {
                    layer.deselectAll();
                    layer.enterSelectionMode();
                }
            }
            if (needsVibrate) {
                vibrateShort();
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && !mCamera.isAnimating()) {
                if (layer.getZoomValue() == 1.0f)
                    layer.zoomInToSelectedItem();
                else
                    layer.setZoomValue(1.0f);
            }
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                if (mLayer.getFeed() != null && mLayer.getFeed().isSingleImageMode()) {
                    return true;
                }
                if (layer.getHud().getMode() == HudLayer.MODE_NORMAL)
                    layer.enterSelectionMode();
                else
                    layer.deselectAll();
            }
        } else {
            mCurrentFocusIsPressed = false;
            int numRows = ((GridLayoutInterface) layer.getLayoutInterface()).mNumRows;
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && mCurrentFocusSlot != Shared.INVALID) {
                if (layer.getHud().getMode() != HudLayer.MODE_SELECT) {
                    boolean centerCamera = layer.tapGesture(mCurrentFocusSlot, false);
                    if (centerCamera) {
                        int slotId = mCurrentFocusSlot;
                        selectSlot(slotId);
                    }
                    mCurrentFocusSlot = Shared.INVALID;
                    return true;
                } else {
                    layer.addSlotToSelectedItems(mCurrentFocusSlot, true, true);
                }
                mCurrentFocusIsPressed = true;
            } else if (keyCode == KeyEvent.KEYCODE_MENU && mCurrentFocusSlot != Shared.INVALID) {
                if (layer.getHud().getMode() == HudLayer.MODE_NORMAL)
                    layer.enterSelectionMode();
                else
                    layer.deselectAll();
            } else if (mCurrentFocusSlot == Shared.INVALID) {
                mCurrentFocusSlot = anchorSlot;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                mCurrentFocusSlot += numRows;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                mCurrentFocusSlot -= numRows;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                --mCurrentFocusSlot;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                ++mCurrentFocusSlot;
            }
            if (mCurrentFocusSlot > lastBufferedVisibleSlot) {
                mCurrentFocusSlot = lastBufferedVisibleSlot;
            }
            if (mCurrentFocusSlot < firstBufferedVisibleSlot)
                mCurrentFocusSlot = firstBufferedVisibleSlot;
            if (mCurrentFocusSlot != Shared.INVALID) {
                layer.centerCameraForSlot(mCurrentFocusSlot, 1.0f);
            }
        }
        return false;
    }

    private void touchBegan(int posX, int posY) {
        mPrevTouchPosX = posX;
        mPrevTouchPosY = posY;
        mFirstTouchPosX = posX;
        mFirstTouchPosY = posY;
        mTouchVelX = 0;
        mTouchVelY = 0;
        mProcessTouch = true;
        mZoomGesture = false;
        mTouchMoved = false;
        mCamera.stopMovementInX();
        GridLayer layer = mLayer;
        mCurrentFocusSlot = layer.getSlotIndexForScreenPosition(posX, posY);
        mCurrentFocusIsPressed = true;
        mTouchFeedbackDelivered = false;
        HudLayer hud = layer.getHud();
        if (hud.getMode() == HudLayer.MODE_SELECT)
            hud.closeSelectionMenu();
        if (layer.getState() == GridLayer.STATE_FULL_SCREEN && hud.getMode() == HudLayer.MODE_SELECT) {
            layer.deselectAll();
            hud.setAlpha(1.0f);
        }
        int slotId = layer.getSlotIndexForScreenPosition(posX, posY);
        if (slotId != Shared.INVALID && layer.getState() != GridLayer.STATE_FULL_SCREEN) {
            vibrateShort();
        }
    }

    private void touchMoved(int posX, int posY, float timeElapsedx) {
        if (mProcessTouch && !mZoomGesture) {
            GridLayer layer = mLayer;
            GridCamera camera = mCamera;
            float deltaX = -(posX - mPrevTouchPosX); // negation since the wall
            // moves in a direction
            // opposite to that of
            // the touch
            float deltaY = -(posY - mPrevTouchPosY);
            if (Math.abs(deltaX) >= 10.0f || Math.abs(deltaY) >= 10.0f) {
                mTouchMoved = true;
            }
            Pool<Vector3f> pool = mPool;
            Vector3f firstPosition = pool.create();
            Vector3f lastPosition = pool.create();
            Vector3f deltaAnchorPosition = pool.create();
            Vector3f worldPosDelta = pool.create();
            try {
                deltaAnchorPosition.set(layer.getDeltaAnchorPosition());
                LayoutInterface layout = layer.getLayoutInterface();
                GridCameraManager.getSlotPositionForSlotIndex(0, camera, layout, deltaAnchorPosition, firstPosition);
                int lastSlotIndex = 0;
                IndexRange completeRange = layer.getCompleteRange();
                synchronized (completeRange) {
                    lastSlotIndex = completeRange.end;
                }
                GridCameraManager.getSlotPositionForSlotIndex(lastSlotIndex, camera, layout, deltaAnchorPosition, lastPosition);

                camera.convertToRelativeCameraSpace(deltaX, deltaY, 0, worldPosDelta);
                deltaX = worldPosDelta.x;
                deltaY = worldPosDelta.y;
                camera.moveBy(deltaX, (layer.getZoomValue() == 1.0f) ? 0 : deltaY, 0);
                deltaX *= camera.mScale;
                deltaY *= camera.mScale;
            } finally {
                pool.delete(firstPosition);
                pool.delete(lastPosition);
                pool.delete(deltaAnchorPosition);
                pool.delete(worldPosDelta);
            }
            if (layer.getZoomValue() == 1.0f) {
                if (camera
                        .computeConstraints(false, (layer.getState() != GridLayer.STATE_FULL_SCREEN), firstPosition, lastPosition)) {
                    deltaX = 0.0f;
                    // vibrate
                    if (!mTouchFeedbackDelivered) {
                        mTouchFeedbackDelivered = true;
                        vibrateLong();
                    }
                }
            }
            mTouchVelX = deltaX * timeElapsedx;
            mTouchVelY = deltaY * timeElapsedx;
            float maxVelXx = (mCamera.mWidth * 0.5f);
            float maxVelYx = (mCamera.mHeight);
            mTouchVelX = FloatUtils.clamp(mTouchVelX, -maxVelXx, maxVelXx);
            mTouchVelY = FloatUtils.clamp(mTouchVelY, -maxVelYx, maxVelYx);
            mPrevTouchPosX = posX;
            mPrevTouchPosY = posY;
            // you want the movement to track the finger immediately
            if (mTouchMoved == false)
                mCurrentFocusSlot = layer.getSlotIndexForScreenPosition(posX, posY);
            else
                mCurrentFocusSlot = Shared.INVALID;
            if (!mCamera.isZAnimating()) {
                mCamera.commitMoveInX();
                mCamera.commitMoveInY();
            }
            int anchorSlotIndex = layer.getAnchorSlotIndex(GridLayer.ANCHOR_LEFT);
            DisplayItem[] displayItems = mDisplayItems;
            IndexRange bufferedVisibleRange = layer.getBufferedVisibleRange();
            int firstBufferedVisibleSlot = bufferedVisibleRange.begin;
            int lastBufferedVisibleSlot = bufferedVisibleRange.end;
            synchronized (displayItems) {
                if (anchorSlotIndex >= firstBufferedVisibleSlot && anchorSlotIndex <= lastBufferedVisibleSlot) {
                    DisplayItem item = displayItems[(anchorSlotIndex - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                    if (item != null) {
                        layer.getHud().setTimeBarTime(item.mItemRef.mDateTakenInMs);
                    }
                }
            }
        }
    }

    private void touchEnded(int posX, int posY, float timeElapsedx) {
        if (mProcessTouch == false || mZoomGesture) {
            return;
        }
        int maxPixelsBeforeSwitch = mCamera.mWidth / 8;
        mCamera.mConvergenceSpeed = 2.0f;
        GridLayer layer = mLayer;
        if (layer.getExpandedSlot() == Shared.INVALID && !layer.feedAboutToChange() && !mZoomGesture) {
            if (mCurrentSelectedSlot != Shared.INVALID) {
                if (layer.getState() == GridLayer.STATE_FULL_SCREEN) {
                    if (!mTouchMoved) {
                        // tap gesture for fullscreen
                        if (layer.getZoomValue() == 1.0f)
                            layer.changeFocusToSlot(mCurrentSelectedSlot, 1.0f);
                    } else if (layer.getZoomValue() == 1.0f) {
                        // we want to snap to a new slotIndex based on where the
                        // current position is
                        if (layer.inSlideShowMode()) {
                            layer.endSlideshow();
                        }
                        float deltaX = posX - mFirstTouchPosX;
                        float deltaY = posY - mFirstTouchPosY;
                        if (deltaY != 0) {
                            // it has moved vertically
                        }
                        layer.changeFocusToSlot(mCurrentSelectedSlot, 1.0f);
                        HudLayer hud = layer.getHud();
                        if (deltaX > maxPixelsBeforeSwitch && hud.getMode() != HudLayer.MODE_SELECT) {
                            layer.changeFocusToPreviousSlot(1.0f);
                        } else if (deltaX < -maxPixelsBeforeSwitch && hud.getMode() != HudLayer.MODE_SELECT) {
                            layer.changeFocusToNextSlot(1.0f);
                        }
                    } else {
                        // in zoomed state
                        // we do nothing for now, but we should clamp to the
                        // image bounds
                        boolean hitEdge = layer.constrainCameraForSlot(mCurrentSelectedSlot);
                        // mPrevHitEdge = false;
                        if (hitEdge && mPrevHitEdge) {
                            float deltaX = posX - mFirstTouchPosX;
                            float deltaY = posY - mFirstTouchPosY;
                            maxPixelsBeforeSwitch *= 4;
                            if (deltaY != 0) {
                                // it has moved vertically
                            }
                            mPrevHitEdge = false;
                            HudLayer hud = layer.getHud();
                            if (deltaX > maxPixelsBeforeSwitch && hud.getMode() != HudLayer.MODE_SELECT) {
                                layer.changeFocusToPreviousSlot(1.0f);
                            } else if (deltaX < -maxPixelsBeforeSwitch && hud.getMode() != HudLayer.MODE_SELECT) {
                                layer.changeFocusToNextSlot(1.0f);
                            } else {
                                mPrevHitEdge = hitEdge;
                            }
                        } else {
                            mPrevHitEdge = hitEdge;
                        }
                    }
                }
            } else {
                if (!layer.feedAboutToChange() && layer.getZoomValue() == 1.0f && mTouchMoved) {
                    constrainCamera(true);
                }
            }
        }
        mCurrentFocusSlot = Shared.INVALID;
        mCurrentFocusIsPressed = false;
        mPrevTouchPosX = posX;
        mPrevTouchPosY = posY;
        mProcessTouch = false;
    }

    private void constrainCamera(boolean b) {
        Pool<Vector3f> pool = mPool;
        GridLayer layer = mLayer;
        Vector3f firstPosition = pool.create();
        Vector3f lastPosition = pool.create();
        Vector3f deltaAnchorPosition = pool.create();
        try {
            deltaAnchorPosition.set(layer.getDeltaAnchorPosition());
            GridCamera camera = mCamera;
            LayoutInterface layout = layer.getLayoutInterface();
            GridCameraManager.getSlotPositionForSlotIndex(0, camera, layout, deltaAnchorPosition, firstPosition);
            int lastSlotIndex = 0;
            IndexRange completeRange = layer.getCompleteRange();
            synchronized (completeRange) {
                lastSlotIndex = completeRange.end;
            }
            GridCameraManager.getSlotPositionForSlotIndex(lastSlotIndex, camera, layout, deltaAnchorPosition, lastPosition);
            camera.computeConstraints(true, (layer.getState() != GridLayer.STATE_FULL_SCREEN), firstPosition, lastPosition);
        } finally {
            pool.delete(firstPosition);
            pool.delete(lastPosition);
            pool.delete(deltaAnchorPosition);
        }
    }

    public void clearSelection() {
        mCurrentSelectedSlot = Shared.INVALID;
    }

    public void clearFocus() {
        mCurrentFocusSlot = Shared.INVALID;
    }

    public boolean isFocusItemPressed() {
        return mCurrentFocusIsPressed;
    }

    public void update(float timeElapsed) {
        mDpadIgnoreTime += timeElapsed;
        if (mCamera.mFriction != 0.0f) {
            constrainCamera(true);
        }
    }

    public void setCurrentFocusSlot(int slotId) {
        mCurrentSelectedSlot = slotId;
    }

    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (mCurrentSelectedSlot == Shared.INVALID) {
            mCamera.moveYTo(0);
            mCamera.moveZTo(0);
            mCamera.mConvergenceSpeed = 1.0f;
            mCamera.mFriction = 0.0f;
            float normalizedVelocity = velocityX * mCamera.mOneByScale;
            // mCamera.moveBy(-velocityX * mCamera.mOneByScale * 0.25f, 0, 0);
            // constrainCamera(true);
            IndexRange visibleRange = mLayer.getVisibleRange();
            int numVisibleSlots = visibleRange.end - visibleRange.begin;
            if (numVisibleSlots > 0) {
                float fastFlingVelocity = 10.0f;
                int slotsToSkip = (int) (numVisibleSlots * (-normalizedVelocity / fastFlingVelocity));
                int maxSlots = numVisibleSlots;
                if (slotsToSkip > maxSlots)
                    slotsToSkip = maxSlots;
                if (slotsToSkip < -maxSlots)
                    slotsToSkip = -maxSlots;
                if (Math.abs(slotsToSkip) <= 1) {
                    if (velocityX > 0)
                        slotsToSkip = -2;
                    else if (velocityX < 0)
                        slotsToSkip = 2;
                }
                int slotToGetTo = mLayer.getAnchorSlotIndex(GridLayer.ANCHOR_CENTER) + slotsToSkip;
                if (slotToGetTo < 0)
                    slotToGetTo = 0;
                int lastSlot = mLayer.getCompleteRange().end;
                if (slotToGetTo > lastSlot)
                    slotToGetTo = lastSlot;
                mLayer.centerCameraForSlot(slotToGetTo, 1.0f);
            }
            constrainCamera(true);
            return true;
        } else {
            return false;
        }
    }

    public void onLongPress(MotionEvent e) {
        if (mZoomGesture)
            return;
        if (mLayer.getFeed() != null && mLayer.getFeed().isSingleImageMode()) {
            HudLayer hud = mLayer.getHud();
            hud.getPathBar().setHidden(true);
            hud.getMenuBar().setHidden(true);
            if (hud.getMode() != HudLayer.MODE_NORMAL)
                hud.setMode(HudLayer.MODE_NORMAL);
        }
        if (mCurrentFocusSlot != Shared.INVALID) {
            vibrateLong();
            GridLayer layer = mLayer;
            if (layer.getState() == GridLayer.STATE_FULL_SCREEN) {
                layer.deselectAll();
            }
            HudLayer hud = layer.getHud();
            hud.enterSelectionMode();
            layer.addSlotToSelectedItems(mCurrentFocusSlot, true, true);
        }
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    public boolean onSingleTapUp(MotionEvent e) {
        GridLayer layer = mLayer;
        int posX = (int) e.getX();
        int posY = (int) e.getY();
        if (mCurrentSelectedSlot != Shared.INVALID) {
            // Fullscreen mode.
            mCamera.mConvergenceSpeed = 2.0f;
            mCamera.mFriction = 0.0f;
            int slotId = mCurrentSelectedSlot;
            if (layer.getZoomValue() == 1.0f) {
                layer.centerCameraForSlot(slotId, 1.0f);
            } else {
                layer.constrainCameraForSlot(slotId);
            }
            DisplayItem displayItem = layer.getDisplayItemForSlotId(slotId);
            if (displayItem != null) {
                final MediaItem item = displayItem.mItemRef;
                int heightBy2 = mCamera.mHeight / 2;
                boolean posYInBounds = (Math.abs(posY - heightBy2) < 64);
                if (posX < 32 && posYInBounds) {
                    layer.changeFocusToPreviousSlot(1.0f);
                } else if (posX > mCamera.mWidth - 32 && posYInBounds) {
                    layer.changeFocusToNextSlot(1.0f);
                } else if (item.getMediaType() == MediaItem.MEDIA_TYPE_VIDEO) {
                    Utils.playVideo(mContext, item);
                } else {
                    // We stop any slideshow.
                    HudLayer hud = layer.getHud();
                    if (layer.inSlideShowMode()) {
                        layer.endSlideshow();
                    } else {
                        hud.setAlpha(1.0f - hud.getAlpha());
                    }
                    if (hud.getMode() == HudLayer.MODE_SELECT) {
                        hud.setAlpha(1.0f);
                    }
                }
            }
        } else {
            int slotId = layer.getSlotIndexForScreenPosition(posX, posY);
            if (slotId != Shared.INVALID) {
                HudLayer hud = layer.getHud();
                if (hud.getMode() == HudLayer.MODE_SELECT) {
                    layer.addSlotToSelectedItems(slotId, true, true);
                } else {
                    boolean centerCamera = (mCurrentSelectedSlot == Shared.INVALID) ? layer.tapGesture(slotId, false) : true;
                    if (centerCamera) {
                        // We check if this item is a video or not.
                        selectSlot(slotId);
                    }
                }
            } else {
                int state = layer.getState();
                if (state != GridLayer.STATE_FULL_SCREEN && state != GridLayer.STATE_GRID_VIEW
                        && layer.getHud().getMode() != HudLayer.MODE_SELECT) {
                    slotId = layer.getMetadataSlotIndexForScreenPosition(posX, posY);
                    if (slotId != Shared.INVALID) {
                        layer.tapGesture(slotId, true);
                    }
                }
            }
        }
        return true;
    }

    private void selectSlot(int slotId) {
        GridLayer layer = mLayer;
        if (layer.getState() == GridLayer.STATE_GRID_VIEW) {
            DisplayItem displayItem = layer.getDisplayItemForSlotId(slotId);
            if (displayItem != null) {
                final MediaItem item = displayItem.mItemRef;
                if (layer.getPickIntent()) {
                    // we need to return this item
                    App.get(mContext).getHandler().post(new Runnable() {
                        public void run() {
                            CropImage.launchCropperOrFinish(mContext, item);
                        }
                    });
                    return;
                }
                if (item.getMediaType() == MediaItem.MEDIA_TYPE_VIDEO) {
                    Utils.playVideo(mContext, item);
                } else {
                    mCurrentSelectedSlot = slotId;
                    layer.endSlideshow();
                    layer.setState(GridLayer.STATE_FULL_SCREEN);
                    mCamera.mConvergenceSpeed = 2.0f;
                    mCamera.mFriction = 0.0f;
                    layer.getHud().fullscreenSelectionChanged(item, mCurrentSelectedSlot + 1, layer.getCompleteRange().end + 1);
                }
            }
        }
        constrainCamera(true);
    }

    public boolean onDoubleTap(MotionEvent e) {
        final GridLayer layer = mLayer;
        if (layer.getState() == GridLayer.STATE_FULL_SCREEN && !mCamera.isZAnimating()) {
            float posX = e.getX();
            float posY = e.getY();
            final Vector3f retVal = new Vector3f();
            posX -= (mCamera.mWidth / 2);
            posY -= (mCamera.mHeight / 2);
            mCamera.convertToRelativeCameraSpace(posX, posY, 0, retVal);
            if (layer.getZoomValue() == 1.0f) {
                layer.setZoomValue(3f);
                mCamera.update(0.001f);
                mCamera.moveBy(retVal.x, retVal.y, 0);
                layer.constrainCameraForSlot(mCurrentSelectedSlot);
            } else {
                layer.setZoomValue(1.0f);
            }
            mCamera.mConvergenceSpeed = 2.0f;
            mCamera.mFriction = 0.0f;
        } else {
            return onSingleTapConfirmed(e);
        }
        return true;
    }

    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    public boolean touchPressed() {
        return mProcessTouch;
    }

    private void vibrateShort() {
        // As per request by Google, this line disables vibration.
        // mView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
    }

    private void vibrateLong() {
        // mView.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    public boolean onScale(ScaleGestureDetector detector) {
        final GridLayer layer = mLayer;
        float scale = detector.getScaleFactor();
        if (Float.isInfinite(scale) || Float.isNaN(scale))
            return true;
        mScale = scale * mScale;
        boolean performTranslation = Math.abs(scale - 1.0f) < 0.001f ? false : true;
        if (layer.getState() == GridLayer.STATE_FULL_SCREEN) {
            float currentScale = layer.getZoomValue();
            if (currentScale <= 1.0f)
                performTranslation = false;
            final Vector3f retVal = new Vector3f();
            if (performTranslation) {
                float posX = detector.getFocusX();
                float posY = detector.getFocusY();
                posX -= (mCamera.mWidth / 2);
                posY -= (mCamera.mHeight / 2);
                mCamera.convertToRelativeCameraSpace(posX, posY, 0, retVal);
            }
            if (currentScale < 0.7f && scale < 1.0f) {
                scale = 1.0f;
            }
            if (currentScale > 8.0f && scale > 1.0f) {
                scale = 1.0f;
            }
            layer.setZoomValue(currentScale * scale);
            if (performTranslation) {
                mCamera.update(0.001f);
                mCamera.moveBy(retVal.x, retVal.y, 0);
                layer.constrainCameraForSlot(mCurrentSelectedSlot);
            }
        }
        if (mLayer.getState() == GridLayer.STATE_GRID_VIEW) {
            mCurrentScaleSlot = Shared.INVALID;
            mCurrentFocusSlot = Shared.INVALID;
        }
        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector detector) {
        mZoomGesture = true;
        mScale = 1.0f;
        mLayer.getHud().hideZoomButtons(true);
        int posX = (int) detector.getFocusX();
        int posY = (int) detector.getFocusY();
        int slotId = mLayer.getSlotIndexForScreenPosition(posX, posY);
        if (slotId == Shared.INVALID) {
            slotId = mLayer.getAnchorSlotIndex(GridLayer.ANCHOR_CENTER);
        }
        if (slotId != Shared.INVALID) {
            mCurrentScaleSlot = slotId;
            mCurrentFocusSlot = slotId;
        }
        if (mLayer.getState() == GridLayer.STATE_GRID_VIEW) {
            mCurrentScaleSlot = Shared.INVALID;
            mCurrentFocusSlot = Shared.INVALID;
        }
        constrainCamera(true);
        return true;
    }

    @SuppressWarnings("unused")
	public void onScaleEnd(ScaleGestureDetector detector, boolean cancel) {
        if (!cancel) {
            final GridLayer layer = mLayer;
            if (layer.getState() == GridLayer.STATE_FULL_SCREEN) {
                float currentScale = layer.getZoomValue();
                if (currentScale < 1.0f) {
                    currentScale = 1.0f;
                } else if (currentScale > 6.0f) {
                    currentScale = 6.0f;
                }
                if (currentScale != layer.getZoomValue()) {
                    layer.setZoomValue(currentScale);
                }
                layer.constrainCameraForSlot(mCurrentSelectedSlot);
                mLayer.getHud().hideZoomButtons(false);
                if (mScale < 0.7f && false)
					layer.goBack();
            } else {
                if (mScale > 3.0f && false) {
                    HudLayer hud = layer.getHud();
                    if (hud.getMode() == HudLayer.MODE_SELECT) {
                        layer.addSlotToSelectedItems(mCurrentScaleSlot, true, true);
                    } else {
                        boolean centerCamera = (mCurrentSelectedSlot == Shared.INVALID) ? layer
                                .tapGesture(mCurrentScaleSlot, false) : true;
                        if (centerCamera) {
                            // We check if this item is a video or not.
                            selectSlot(mCurrentScaleSlot);
                        }
                    }
                } else {
                    if (layer.getState() == GridLayer.STATE_GRID_VIEW) {
                        if (mScale < 0.7f && false) {
                            layer.goBack();
                        }
                    }
                }
            }
        } else {
            // The gesture was cancelled.
            final GridLayer layer = mLayer;
            if (layer.getState() == GridLayer.STATE_FULL_SCREEN) {
                layer.setZoomValue(1.0f);
                mLayer.getHud().hideZoomButtons(false);
            }
        }
        resetScale();
    }

    public float getScale() {
        return mScale;
    }

    public void resetScale() {
        mScale = 1.0f;
        mCurrentScaleSlot = Shared.INVALID;
    }

    public ScaleGestureDetector getScaleGestureDetector() {
        return mScaleGestureDetector;
    }
}
