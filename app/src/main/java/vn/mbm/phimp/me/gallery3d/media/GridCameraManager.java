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


public final class GridCameraManager {
    private final GridCamera mCamera;
    private static final Pool<Vector3f> sPool;
    static {
        final Vector3f[] vectorPool = new Vector3f[128];
        int length = vectorPool.length;
        for (int i = 0; i < length; ++i) {
            vectorPool[i] = new Vector3f();
        }
        sPool = new Pool<Vector3f>(vectorPool);
    }

    public GridCameraManager(final GridCamera camera) {
        mCamera = camera;
    }

    public void centerCameraForSlot(LayoutInterface layout, int slotIndex, float baseConvergence, Vector3f deltaAnchorPositionIn,
            int selectedSlotIndex, float zoomValue, float imageTheta, int state) {
        final GridCamera camera = mCamera;
        final Pool<Vector3f> pool = sPool;
        synchronized (camera) {
            final boolean zoomin = (selectedSlotIndex != Shared.INVALID);
            final int theta = (int) imageTheta;
            final int portrait = (theta / 90) % 2;
            if (slotIndex == selectedSlotIndex) {
                camera.mConvergenceSpeed = baseConvergence * (zoomin ? 2.0f : 2.0f);
                camera.mFriction = 0.0f;
            }
            final float oneByZoom = 1.0f / zoomValue;
            if (slotIndex >= 0) {
                final Vector3f position = pool.create();
                final Vector3f deltaAnchorPosition = pool.create();
                try {
                    deltaAnchorPosition.set(deltaAnchorPositionIn);
                    GridCameraManager.getSlotPositionForSlotIndex(slotIndex, camera, layout, deltaAnchorPosition, position);
                    position.x = (zoomValue == 1.0f) ? ((position.x) * camera.mOneByScale) : camera.mLookAtX;
                    position.y = (zoomValue == 1.0f) ? 0 : camera.mLookAtY;
                    if (state == GridLayer.STATE_MEDIA_SETS || state == GridLayer.STATE_TIMELINE) {
                        position.y = -0.1f;
                    }
                    float width = camera.mItemWidth;
                    float height = camera.mItemHeight;
                    if (portrait != 0) {
                        float temp = width;
                        width = height;
                        height = temp;
                    }
                    camera.moveTo(position.x, position.y, zoomin ? camera.getDistanceToFitRect(width * oneByZoom, height
                            * oneByZoom) : 0);
                } finally {
                    pool.delete(position);
                    pool.delete(deltaAnchorPosition);
                }
            } else {
                camera.moveYTo(0);
                camera.moveZTo(0);
            }
        }
    }

    // CR: line too long. Documentation--what are the semantics of the return
    // value?
    /**
     */
    public boolean constrainCameraForSlot(LayoutInterface layout, int slotIndex, Vector3f deltaAnchorPositionIn,
            float currentFocusItemWidth, float currentFocusItemHeight) {
        final GridCamera camera = mCamera;
        final Pool<Vector3f> pool = sPool;
        boolean retVal = false;
        synchronized (camera) {
            final Vector3f position = pool.create();
            final Vector3f deltaAnchorPosition = pool.create();
            final Vector3f topLeft = pool.create();
            final Vector3f bottomRight = pool.create();
            final Vector3f imgTopLeft = pool.create();
            final Vector3f imgBottomRight = pool.create();

            try {
                if (slotIndex >= 0) {
                    deltaAnchorPosition.set(deltaAnchorPositionIn);
                    GridCameraManager.getSlotPositionForSlotIndex(slotIndex, camera, layout, deltaAnchorPosition, position);
                    position.x *= camera.mOneByScale;
                    position.y = 0.0f;
                    float width = (currentFocusItemWidth / 2);
                    float height = (currentFocusItemHeight / 2);
                    imgTopLeft.set(position.x - width, position.y - height, 0);
                    imgBottomRight.set(position.x + width, position.y + height, 0);
                    camera.convertToCameraSpace(0, 0, 0, topLeft);
                    camera.convertToCameraSpace(camera.mWidth, camera.mHeight, 0, bottomRight);
                    float leftExtent = topLeft.x - imgTopLeft.x;
                    float rightExtent = bottomRight.x - imgBottomRight.x;
                    camera.mConvergenceSpeed = 2.0f;
                    camera.mFriction = 0.0f;
                    if (leftExtent < 0) {
                        retVal = true;
                        camera.moveBy(-leftExtent, 0, 0);
                    }
                    if (rightExtent > 0) {
                        retVal = true;
                        camera.moveBy(-rightExtent, 0, 0);
                    }
                    float topExtent = topLeft.y - imgTopLeft.y;
                    float bottomExtent = bottomRight.y - imgBottomRight.y;
                    if (topExtent < 0) {
                        camera.moveBy(0, -topExtent, 0);
                    }
                    if (bottomExtent > 0) {
                        camera.moveBy(0, -bottomExtent, 0);
                    }
                }
            } finally {
                pool.delete(position);
                pool.delete(deltaAnchorPosition);
                pool.delete(topLeft);
                pool.delete(bottomRight);
                pool.delete(imgTopLeft);
                pool.delete(imgBottomRight);
            }
        }
        return retVal;
    }

    public void computeVisibleRange(MediaFeed feed, LayoutInterface layout, Vector3f deltaAnchorPositionIn,
            IndexRange outVisibleRange, IndexRange outBufferedVisibleRange, IndexRange outCompleteRange, int state) {
        GridCamera camera = mCamera;
        Pool<Vector3f> pool = sPool;
        float offset = (camera.mLookAtX * camera.mScale);
        int itemWidth = camera.mItemWidth;
        float maxIncrement = camera.mWidth * 0.5f + itemWidth;
        float left = -maxIncrement + offset;
        float right = left + 2.0f * maxIncrement;
        if (state == GridLayer.STATE_MEDIA_SETS || state == GridLayer.STATE_TIMELINE) {
            right += (itemWidth * 0.5f);
        }
        float top = -maxIncrement;
        float bottom = camera.mHeight + maxIncrement;
        // the hint to compute the visible display items
        int numSlots = 0;
        if (feed != null) {
            numSlots = feed.getNumSlots();
        }
        synchronized (outCompleteRange) {
            outCompleteRange.set(0, numSlots - 1);
        }

        Vector3f position = pool.create();
        Vector3f deltaAnchorPosition = pool.create();
        try {
            int firstVisibleSlotIndex = 0;
            int lastVisibleSlotIndex = numSlots - 1;
            int leftEdge = firstVisibleSlotIndex;
            int rightEdge = lastVisibleSlotIndex;
            int index = (leftEdge + rightEdge) / 2;
            lastVisibleSlotIndex = firstVisibleSlotIndex;
            deltaAnchorPosition.set(deltaAnchorPositionIn);
            while (index != leftEdge) {
                GridCameraManager.getSlotPositionForSlotIndex(index, camera, layout, deltaAnchorPosition, position);
                if (FloatUtils.boundsContainsPoint(left, right, top, bottom, position.x, position.y)) {
                    // this index is visible
                    firstVisibleSlotIndex = index;
                    lastVisibleSlotIndex = index;
                    break;
                } else {
                    if (position.x > left) {
                        rightEdge = index;
                    } else {
                        leftEdge = index;
                    }
                    index = (leftEdge + rightEdge) / 2;
                }
            }
            // CR: comments would make me a happy panda.
            while (firstVisibleSlotIndex >= 0 && firstVisibleSlotIndex < numSlots) {
                GridCameraManager.getSlotPositionForSlotIndex(firstVisibleSlotIndex, camera, layout, deltaAnchorPosition, position);
                // CR: !fubar instead of fubar == false.
                if (FloatUtils.boundsContainsPoint(left, right, top, bottom, position.x, position.y) == false) {
                    ++firstVisibleSlotIndex;
                    break;
                } else {
                    --firstVisibleSlotIndex;
                }
            }
            while (lastVisibleSlotIndex >= 0 && lastVisibleSlotIndex < numSlots) {
                GridCameraManager.getSlotPositionForSlotIndex(lastVisibleSlotIndex, camera, layout, deltaAnchorPosition, position);
                if (FloatUtils.boundsContainsPoint(left, right, top, bottom, position.x, position.y) == false) {
                    --lastVisibleSlotIndex;
                    break;
                } else {
                    ++lastVisibleSlotIndex;
                }
            }
            if (firstVisibleSlotIndex < 0)
                firstVisibleSlotIndex = 0;
            if (lastVisibleSlotIndex >= numSlots)
                lastVisibleSlotIndex = numSlots - 1;
            synchronized (outVisibleRange) {
                outVisibleRange.set(firstVisibleSlotIndex, lastVisibleSlotIndex);
            }
            if (feed != null) {
                feed.setVisibleRange(firstVisibleSlotIndex, lastVisibleSlotIndex);
            }
            final int buffer = 24;
            firstVisibleSlotIndex = ((firstVisibleSlotIndex - buffer) / buffer) * buffer;
            lastVisibleSlotIndex += buffer;
            lastVisibleSlotIndex = (lastVisibleSlotIndex / buffer) * buffer;
            if (firstVisibleSlotIndex < 0) {
                firstVisibleSlotIndex = 0;
            }
            if (lastVisibleSlotIndex >= numSlots) {
                lastVisibleSlotIndex = numSlots - 1;
            }
            synchronized (outBufferedVisibleRange) {
                outBufferedVisibleRange.set(firstVisibleSlotIndex, lastVisibleSlotIndex);
            }
        } finally {
            pool.delete(position);
            pool.delete(deltaAnchorPosition);
        }
    }

    public static final void getSlotPositionForSlotIndex(int slotIndex, GridCamera camera, LayoutInterface layout,
            Vector3f deltaAnchorPosition, Vector3f outVal) {
        layout.getPositionForSlotIndex(slotIndex, camera.mItemWidth, camera.mItemHeight, outVal);
        outVal.subtract(deltaAnchorPosition);
    }

    public static final float getFillScreenZoomValue(GridCamera camera, Pool<Vector3f> pool, float currentFocusItemWidth,
            float currentFocusItemHeight) {
        final Vector3f topLeft = pool.create();
        final Vector3f bottomRight = pool.create();
        float potentialZoomValue = 1.0f;
        try {
            camera.convertToCameraSpace(0, 0, 0, topLeft);
            camera.convertToCameraSpace(camera.mWidth, camera.mHeight, 0, bottomRight);
            float xExtent = Math.abs(topLeft.x - bottomRight.x) / currentFocusItemWidth;
            float yExtent = Math.abs(topLeft.y - bottomRight.y) / currentFocusItemHeight;
            potentialZoomValue = Math.max(xExtent, yExtent);
        } finally {
            pool.delete(topLeft);
            pool.delete(bottomRight);
        }
        return potentialZoomValue;
    }
}