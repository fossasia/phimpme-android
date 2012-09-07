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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import android.content.Context;

public final class GridDrawManager {
    public static final int PASS_THUMBNAIL_CONTENT = 0;
    public static final int PASS_FOCUS_CONTENT = 1;
    public static final int PASS_FRAME = 2;
    public static final int PASS_PLACEHOLDER = 3;
    public static final int PASS_FRAME_PLACEHOLDER = 4;
    public static final int PASS_TEXT_LABEL = 5;
    public static final int PASS_SELECTION_LABEL = 6;
    public static final int PASS_VIDEO_LABEL = 7;
    public static final int PASS_LOCATION_LABEL = 8;
    public static final int PASS_MEDIASET_SOURCE_LABEL = 9;

    private static final MediaItemTexture.Config sThumbnailConfig = new MediaItemTexture.Config();
    private final DisplayItem[] mDisplayItems;
    private final DisplaySlot[] mDisplaySlots;
    private final DisplayList mDisplayList;
    private final GridCamera mCamera;
    private final GridDrawables mDrawables;
    private IndexRange mBufferedVisibleRange;
    private IndexRange mVisibleRange;
    private int mSelectedSlot;
    private int mCurrentFocusSlot;
    private DisplayItem[] mItemsDrawn;
    private int mDrawnCounter;
    private float mTargetFocusMixRatio = 0.0f;
    private float mFocusMixRatio = 0.0f;
    private final FloatAnim mSelectedMixRatio = new FloatAnim(0f);
    private float mCurrentFocusItemWidth;
    private float mCurrentFocusItemHeight;
    private boolean mCurrentFocusIsPressed;
    private final Texture mNoItemsTexture;
    private int mCurrentScaleSlot;
    private float mSpreadValue;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mHoldPosition;

    private static final Comparator<DisplayItem> sDisplayItemComparator = new Comparator<DisplayItem>() {
        public int compare(DisplayItem a, DisplayItem b) {
            if (a == null || b == null) {
                return 0;
            }
            float delta = (a.mAnimatedPosition.z - b.mAnimatedPosition.z);
            if (delta > 0) {
                return 1;
            } else if (delta < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    @SuppressWarnings("static-access")
	public GridDrawManager(Context context, GridCamera camera, GridDrawables drawables, DisplayList displayList, DisplayItem[] displayItems, DisplaySlot[] displaySlots) {
        sThumbnailConfig.thumbnailWidth = 128;
        sThumbnailConfig.thumbnailHeight = 96;
        mDisplayItems = displayItems;
        mDisplaySlots = displaySlots;
        mDisplayList = displayList;
        mDrawables = drawables;
        mCamera = camera;
        mItemsDrawn = new DisplayItem[GridLayer.MAX_ITEMS_DRAWABLE];

        StringTexture.Config stc = new StringTexture.Config();
        stc.bold = true;
        stc.fontSize = 16 * App.PIXEL_DENSITY;
        stc.sizeMode = StringTexture.Config.SIZE_EXACT;
        stc.overflowMode = StringTexture.Config.OVERFLOW_FADE;
        mNoItemsTexture = new StringTexture(context.getResources().getString(Res.string.no_items), stc);

    }

    public void prepareDraw(IndexRange bufferedVisibleRange, IndexRange visibleRange, int selectedSlot, int currentFocusSlot,
            int currentScaleSlot, boolean currentFocusIsPressed, float spreadValue, ScaleGestureDetector scaleGestureDetector,
            boolean holdPosition) {
        mBufferedVisibleRange = bufferedVisibleRange;
        mVisibleRange = visibleRange;
        mSelectedSlot = selectedSlot;
        mCurrentFocusSlot = currentFocusSlot;
        mCurrentFocusIsPressed = currentFocusIsPressed;
        mCurrentScaleSlot = currentScaleSlot;
        mScaleGestureDetector = scaleGestureDetector;
        mSpreadValue = spreadValue;
        mHoldPosition = holdPosition;
    }

    public boolean update(float timeElapsed) {
        mFocusMixRatio = FloatUtils.animate(mFocusMixRatio, mTargetFocusMixRatio, timeElapsed);
        mTargetFocusMixRatio = 0.0f;
        if (mFocusMixRatio != mTargetFocusMixRatio || mSelectedMixRatio.isAnimating()) {
            return true;
        }
        return false;
    }

    public void drawThumbnails(RenderView view, GL11 gl, int state) {
        final GridDrawables drawables = mDrawables;
        final DisplayList displayList = mDisplayList;
        final DisplayItem[] displayItems = mDisplayItems;
        final int firstBufferedVisibleSlot = mBufferedVisibleRange.begin;
        final int lastBufferedVisibleSlot = mBufferedVisibleRange.end;
        final int firstVisibleSlot = mVisibleRange.begin;
        final int lastVisibleSlot = mVisibleRange.end;
        final int selectedSlotIndex = mSelectedSlot;
        final int currentFocusSlot = mCurrentFocusSlot;
        final int currentScaleSlot = mCurrentScaleSlot;
        final DisplayItem[] itemsDrawn = mItemsDrawn;
        itemsDrawn[0] = null; // No items drawn yet.
        int drawnCounter = 0;
        final GridQuad grid = GridDrawables.sGrid;
        grid.bindArrays(gl);
        int numTexturesQueued = 0;
        Context context = view.getContext();
        for (int itrSlotIndex = firstBufferedVisibleSlot; itrSlotIndex <= lastBufferedVisibleSlot; ++itrSlotIndex) {
            int index = itrSlotIndex;
            boolean priority = !(index < firstVisibleSlot || index > lastVisibleSlot);
            int startSlotIndex = 0;
            final int maxDisplayedItemsPerSlot = (index == mCurrentScaleSlot) ? GridLayer.MAX_DISPLAYED_ITEMS_PER_FOCUSED_SLOT
                    : GridLayer.MAX_DISPLAYED_ITEMS_PER_SLOT;
            if (index != mCurrentScaleSlot) {
                for (int j = maxDisplayedItemsPerSlot - 1; j >= 0; --j) {
                    DisplayItem displayItem = displayItems[(index - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT + j];
                    if (displayItem == null) {
                        continue;
                    } else {
                        Texture texture = displayItem.getThumbnailImage(context, sThumbnailConfig);
                        if (texture != null && texture.isLoaded() == false) {
                            startSlotIndex = j;
                            break;
                        }
                    }
                }
            }
            // Prime the textures in the reverse order.
            for (int j = 0; j < maxDisplayedItemsPerSlot; ++j) {
                int stackIndex = (index == mCurrentScaleSlot) ? maxDisplayedItemsPerSlot - j - 1 : j;
                DisplayItem displayItem = displayItems[(index - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT
                        + stackIndex];
                if (displayItem == null) {
                    continue;
                } else {
                    displayItem.mCurrentSlotIndex = index;
                    if (selectedSlotIndex != Shared.INVALID && (index <= selectedSlotIndex - 2 || index >= selectedSlotIndex + 2)) {
                        displayItem.clearScreennailImage();
                    }
                    Texture texture = displayItem.getThumbnailImage(context, sThumbnailConfig);
                    if (index == mCurrentScaleSlot && texture != null && !texture.isLoaded()) {
                        view.prime(texture, true);
                        view.bind(texture);
                    } else if (texture != null && !texture.isLoaded() && numTexturesQueued <= 6) {
                        boolean isCached = texture.isCached();
                        view.prime(texture, priority);
                        view.bind(texture);
                        if (priority && isCached && texture.mState != Texture.STATE_ERROR)
                            ++numTexturesQueued;
                    }
                }
            }
            if (itrSlotIndex == selectedSlotIndex) {
                continue;
            }
            view.prime(drawables.mTexturePlaceholder, true);
            Texture placeholder = (state == GridLayer.STATE_GRID_VIEW) ? drawables.mTexturePlaceholder : null;
            final boolean pushDown = (state == GridLayer.STATE_GRID_VIEW || state == GridLayer.STATE_FULL_SCREEN) ? false : true;
            for (int j = 0; j < GridLayer.MAX_ITEMS_PER_SLOT; ++j) {
                DisplayItem displayItem = displayItems[(index - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT + j];
                if (displayItem == null)
                    continue;
                Texture texture = displayItem.getThumbnailImage(context, sThumbnailConfig);
                if (texture == null || !texture.isLoaded()) {
                    if (currentScaleSlot != index) {
                        if (j == 0) {
                            final MediaSet parentSet = displayItem.mItemRef.mParentMediaSet;
                            if (parentSet != null && parentSet.getNumItems() <= 1) {
                                displayList.setAlive(displayItem, false);
                            }
                        } else {
                            displayList.setAlive(displayItem, false);
                        }
                    }
                }
                final float dx1 = mScaleGestureDetector.getTopFingerDeltaX();
                final float dy1 = mScaleGestureDetector.getTopFingerDeltaY();
                final float dx2 = mScaleGestureDetector.getBottomFingerDeltaX();
                final float dy2 = mScaleGestureDetector.getBottomFingerDeltaY();
                final float span = mScaleGestureDetector.getCurrentSpan();
                if (state == GridLayer.STATE_FULL_SCREEN) {
                    displayList.setOffset(displayItem, false, true, span, dx1, dy1, dx2, dy2);
                } else {
                    if (!mHoldPosition) {
                        if (state != GridLayer.STATE_GRID_VIEW) {
                            if (currentScaleSlot == index) {
                                displayList.setOffset(displayItem, true, false, span, dx1, dy1, dx2, dy2);
                            } else if (currentScaleSlot != Shared.INVALID) {
                                displayList.setOffset(displayItem, true, true, span, dx1, dy1, dx2, dy2);
                            } else {
                                displayList.setOffset(displayItem, false, false, span, dx1, dy1, dx2, dy2);
                            }
                        } else {
                            float minVal = -1.0f;
                            float maxVal = GridCamera.EYE_Z * 0.5f;
                            float zVal = minVal + mSpreadValue;
                            zVal = FloatUtils.clamp(zVal, minVal, maxVal);
                            if (Float.isInfinite(zVal) || Float.isNaN(zVal)) {
                                mCamera.moveZTo(0);
                            } else {
                                mCamera.moveZTo(-zVal);
                            }
                        }
                    }
                }
            }
            for (int j = startSlotIndex; j < GridLayer.MAX_ITEMS_PER_SLOT; ++j) {
                DisplayItem displayItem = displayItems[(index - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT + j];
                if (displayItem == null) {
                    break;
                } else {
                    if (currentFocusSlot == index) {
                        displayList.setHasFocus(displayItem, true, pushDown);
                        mTargetFocusMixRatio = 1.0f;
                    } else {
                        displayList.setHasFocus(displayItem, false, pushDown);
                    }
                    if (j >= maxDisplayedItemsPerSlot)
                        continue;
                    Texture texture = displayItem.getThumbnailImage(view.getContext(), sThumbnailConfig);
                    if (texture != null) {
                        if (index == mCurrentScaleSlot)
                            displayItem.mAlive = true;
                        if ((!displayItem.isAnimating() || !texture.isLoaded())
                                && displayItem.getStackIndex() > GridLayer.MAX_ITEMS_PER_SLOT) {
                            displayList.setAlive(displayItem, true);
                            continue;
                        }
                        if (index < firstVisibleSlot || index > lastVisibleSlot) {
                            if (view.bind(texture)) {
                                displayList.setAlive(displayItem, true);
                            }
                            continue;
                        }
                        drawDisplayItem(view, gl, displayItem, texture, PASS_THUMBNAIL_CONTENT, placeholder,
                                displayItem.mAnimatedPlaceholderFade);
                    } else {
                        // Move on to the next stack.
                        break;
                    }
                    if (drawnCounter >= GridLayer.MAX_ITEMS_DRAWABLE - 1 || drawnCounter < 0) {
                        break;
                    }
                    // Insert in order of z.
                    itemsDrawn[drawnCounter++] = displayItem;
                    itemsDrawn[drawnCounter] = null;
                }
            }
        }
        mDrawnCounter = drawnCounter;
        grid.unbindArrays(gl);
    }

    public float getFocusQuadWidth() {
        return mCurrentFocusItemWidth;
    }

    public float getFocusQuadHeight() {
        return mCurrentFocusItemHeight;
    }

    public void drawFocusItems(RenderView view, GL11 gl, float zoomValue, boolean slideshowMode, float timeElapsedSinceView) 
    {
    	//Log.d("thong", "GridDrawManager - drawFocusItems");
        int selectedSlotIndex = mSelectedSlot;
        GridDrawables drawables = mDrawables;
        GridCamera camera = mCamera;
        DisplayItem[] displayItems = mDisplayItems;
        int firstBufferedVisibleSlot = mBufferedVisibleRange.begin;
        int lastBufferedVisibleSlot = mBufferedVisibleRange.end;
        boolean isCameraZAnimating = mCamera.isZAnimating();
        for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
            if (selectedSlotIndex != Shared.INVALID && (i >= selectedSlotIndex - 2 && i <= selectedSlotIndex + 2)) {
                continue;
            }
            DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
            if (displayItem != null) {
                displayItem.clearScreennailImage();
            }
        }
        if (selectedSlotIndex != Shared.INVALID) {
            float camX = camera.mLookAtX * camera.mScale;
            int centerIndexInDrawnArray = (selectedSlotIndex - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT;
            if (centerIndexInDrawnArray < 0 || centerIndexInDrawnArray >= displayItems.length) {
                return;
            }
            DisplayItem centerDisplayItem = displayItems[centerIndexInDrawnArray];
            if (centerDisplayItem == null || centerDisplayItem.mItemRef.mId == Shared.INVALID) {
                return;
            }
            boolean focusItemTextureLoaded = false;
            Texture centerTexture = centerDisplayItem.getScreennailImage(view.getContext());
            if (centerTexture != null && centerTexture.isLoaded()) {
                focusItemTextureLoaded = true;
            }
            float centerTranslateX = centerDisplayItem.mAnimatedPosition.x;
            final boolean skipPrevious = centerTranslateX < camX;
            view.setAlpha(1.0f);
            gl.glEnable(GL11.GL_BLEND);
            gl.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            float backupImageTheta = 0.0f;
            for (int i = -1; i <= 1; ++i) {
                if (slideshowMode && timeElapsedSinceView > 1.0f && i != 0)
                    continue;
                float viewAspect = camera.mAspectRatio;
                int selectedSlotToUse = selectedSlotIndex + i;
                if (selectedSlotToUse >= 0 && selectedSlotToUse <= lastBufferedVisibleSlot) {
                    int indexInDrawnArray = (selectedSlotToUse - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT;
                    if (indexInDrawnArray < 0 || indexInDrawnArray >= displayItems.length) {
                        return;
                    }
                    DisplayItem displayItem = displayItems[indexInDrawnArray];
                    MediaItem item = displayItem.mItemRef;
                    final Texture thumbnailTexture = displayItem.getThumbnailImage(view.getContext(), sThumbnailConfig);
                    Texture texture = displayItem.getScreennailImage(view.getContext());
                    if (isCameraZAnimating && (texture == null || !texture.isLoaded())) {
                        texture = thumbnailTexture;
                        mSelectedMixRatio.setValue(0f);
                        mSelectedMixRatio.animateValue(1f, 0.75f, view.getFrameTime());
                    }
                    Texture hiRes = (zoomValue != 1.0f && i == 0 && item.getMediaType() != MediaItem.MEDIA_TYPE_VIDEO) ? displayItem
                            .getHiResImage(view.getContext())
                            : null;
                    if (App.PIXEL_DENSITY > 1.0f) {
                        hiRes = texture;
                    }
                    if (i != 0) {
                        displayItem.clearHiResImage();
                    }
                    if (hiRes != null) {
                        if (!hiRes.isLoaded()) {
                            view.bind(hiRes);
                            view.prime(hiRes, true);
                        } else {
                            texture = hiRes;
                        }
                    }
                    final Texture fsTexture = texture;
                    if (texture == null || !texture.isLoaded()) {
                        if (Math.abs(centerTranslateX - camX) < 0.1f) {
                            if (focusItemTextureLoaded && i != 0) {
                                view.bind(texture);
                            }
                            if (i == 0) {
                                view.bind(texture);
                                view.prime(texture, true);
                            }
                        }
                        texture = thumbnailTexture;
                        if (i == 0) {
                            mSelectedMixRatio.setValue(0f);
                            mSelectedMixRatio.animateValue(1f, 0.75f, view.getFrameTime());
                        }
                    }
                    if (mCamera.isAnimating() || slideshowMode) {
                        if (!slideshowMode && skipPrevious && i == -1) {
                            continue;
                        }
                        if (!skipPrevious && i == 1) {
                            continue;
                        }
                    }
                    int theta = (int) displayItem.getImageTheta();
                    // If it is in slideshow mode, we draw the previous item in
                    // the next item's position.
                    if (slideshowMode && timeElapsedSinceView < 1.0f && timeElapsedSinceView != 0) {
                        if (i == -1) {
                            int nextSlotToUse = selectedSlotToUse + 1;
                            if (nextSlotToUse >= 0 && nextSlotToUse <= lastBufferedVisibleSlot) {
                                int nextIndexInDrawnArray = (nextSlotToUse - firstBufferedVisibleSlot)
                                        * GridLayer.MAX_ITEMS_PER_SLOT;
                                if (nextIndexInDrawnArray >= 0 && nextIndexInDrawnArray < displayItems.length) {
                                    float currentImageTheta = displayItem.mAnimatedImageTheta;
                                    displayItem = displayItems[nextIndexInDrawnArray];
                                    backupImageTheta = displayItem.mAnimatedImageTheta;
                                    displayItem.mAnimatedImageTheta = currentImageTheta;
                                    view.setAlpha(1.0f - timeElapsedSinceView);
                                }
                            }
                        } else if (i == 0) {
                            displayItem.mAnimatedImageTheta = backupImageTheta;
                            view.setAlpha(timeElapsedSinceView);
                        }
                    }
                    if (texture != null) {
                        int vboIndex = i + 1;
                        float alpha = view.getAlpha();
                        float selectedMixRatio = mSelectedMixRatio.getValue(view.getFrameTime());
                        if (selectedMixRatio != 1f) {
                            texture = thumbnailTexture;
                            view.setAlpha(alpha * (1.0f - selectedMixRatio));
                        }
                        GridQuad quad = GridDrawables.sFullscreenGrid[vboIndex];
                        float u = texture.getNormalizedWidth();
                        float v = texture.getNormalizedHeight();
                        float imageWidth = texture.getWidth();
                        float imageHeight = texture.getHeight();
                        boolean portrait = ((theta / 90) % 2 == 1);
                        if (portrait) {
                            viewAspect = 1.0f / viewAspect;
                        }
                        quad.resizeQuad(viewAspect, u, v, imageWidth, imageHeight);
                        quad.bindArrays(gl);
                        drawDisplayItem(view, gl, displayItem, texture, PASS_FOCUS_CONTENT, null, 0.0f);
                        quad.unbindArrays(gl);
                        if (selectedMixRatio != 0.0f && selectedMixRatio != 1.0f) {
                            texture = fsTexture;
                            if (texture != null) {
                                float drawAlpha = selectedMixRatio;
                                view.setAlpha(alpha * drawAlpha);
                                u = texture.getNormalizedWidth();
                                v = texture.getNormalizedHeight();
                                imageWidth = texture.getWidth();
                                imageHeight = texture.getHeight();
                                quad.resizeQuad(viewAspect, u, v, imageWidth, imageHeight);
                                quad.bindArrays(gl);
                                drawDisplayItem(view, gl, displayItem, fsTexture, PASS_FOCUS_CONTENT, null, 1.0f);
                                quad.unbindArrays(gl);
                            }
                        }
                        if (i == 0 || slideshowMode) {
                            mCurrentFocusItemWidth = quad.getWidth();
                            mCurrentFocusItemHeight = quad.getHeight();
                            if (portrait) {
                                // Swap these values.
                                float itemWidth = mCurrentFocusItemWidth;
                                mCurrentFocusItemWidth = mCurrentFocusItemHeight;
                                mCurrentFocusItemHeight = itemWidth;
                            }
                        }
                        view.setAlpha(alpha);
                        if (item.getMediaType() == MediaItem.MEDIA_TYPE_VIDEO) {
                            // The play graphic overlay.
                            GridDrawables.sVideoGrid.bindArrays(gl);
                            drawDisplayItem(view, gl, displayItem, drawables.mTextureVideo, PASS_VIDEO_LABEL, null, 0);
                            GridDrawables.sVideoGrid.unbindArrays(gl);
                        }
                    }
                }
            }
        }
    }

    public void drawBlendedComponents(RenderView view, GL11 gl, float alpha, int state, int hudMode, float stackMixRatio,
            float gridMixRatio, MediaBucketList selectedBucketList, MediaBucketList markedBucketList, boolean isFeedLoading) {
        int firstBufferedVisibleSlot = mBufferedVisibleRange.begin;
        int lastBufferedVisibleSlot = mBufferedVisibleRange.end;
        int firstVisibleSlot = mVisibleRange.begin;
        int lastVisibleSlot = mVisibleRange.end;
        DisplayItem[] displayItems = mDisplayItems;
        GridDrawables drawables = mDrawables;

        // We draw the frames around the drawn items.
        boolean currentFocusIsPressed = mCurrentFocusIsPressed;
        if (state != GridLayer.STATE_FULL_SCREEN) {
            GridDrawables.sFrame.bindArrays(gl);
            Texture texturePlaceHolder = (state == GridLayer.STATE_GRID_VIEW) ? drawables.mTextureGridFrame
                    : drawables.mTextureFrame;
            for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
                if (i < firstVisibleSlot || i > lastVisibleSlot) {
                    continue;
                }
                boolean slotIsAlive = false;
                final int maxDisplayedItemsPerSlot = (i == mCurrentScaleSlot) ? GridLayer.MAX_DISPLAYED_ITEMS_PER_FOCUSED_SLOT
                        : GridLayer.MAX_DISPLAYED_ITEMS_PER_SLOT;
                if (state != GridLayer.STATE_MEDIA_SETS && state != GridLayer.STATE_TIMELINE) {
                    for (int j = 0; j < maxDisplayedItemsPerSlot; ++j) {
                        DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT + j];
                        if (displayItem != null) {
                            slotIsAlive |= displayItem.mAlive;
                        }
                    }
                    if (!slotIsAlive) {
                        DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                        if (displayItem != null) {
                            drawDisplayItem(view, gl, displayItem, texturePlaceHolder, PASS_FRAME_PLACEHOLDER, null, 0);
                        }
                    }
                }
            }
            Texture texturePressed = drawables.mTextureFramePressed;
            Texture textureFocus = drawables.mTextureFrameFocus;
            Texture textureGrid = drawables.mTextureGridFrame;
            Texture texture = drawables.mTextureFrame;

            int drawnCounter = mDrawnCounter;
            DisplayItem[] itemsDrawn = mItemsDrawn;
            if (texture != null) {
                if (drawnCounter > 0) {
                    Arrays.sort(itemsDrawn, 0, drawnCounter, sDisplayItemComparator);
                    float timeElapsedSinceGridView = gridMixRatio;
                    float timeElapsedSinceStackView = stackMixRatio;
                    for (int i = drawnCounter - 1; i >= 0; --i) {
                        DisplayItem itemDrawn = itemsDrawn[i];
                        if (itemDrawn == null) {
                            continue;
                        }
                        boolean displayItemPresentInSelectedItems = selectedBucketList.find(itemDrawn.mItemRef);
                        boolean displayItemPresentInMarkedItems = markedBucketList.find(itemDrawn.mItemRef);
                        Texture previousTexture = (displayItemPresentInSelectedItems) ? texturePressed : texture;
                        Texture textureToUse = (itemDrawn.getHasFocus()) ? (currentFocusIsPressed ? texturePressed : textureFocus)
                                : ((displayItemPresentInSelectedItems) ? texturePressed
                                        : (displayItemPresentInMarkedItems) ? texture : textureGrid);
                        float ratio = timeElapsedSinceGridView;
                        if (itemDrawn.mAlive) {
                            if (state != GridLayer.STATE_GRID_VIEW) {
                                previousTexture = (displayItemPresentInSelectedItems) ? texturePressed : texture;
                                textureToUse = (itemDrawn.getHasFocus()) ? (currentFocusIsPressed ? texturePressed : textureFocus)
                                        : previousTexture;
                                if (timeElapsedSinceStackView == 1.0f) {
                                    ratio = mFocusMixRatio;
                                } else {
                                    ratio = timeElapsedSinceStackView;
                                    previousTexture = textureGrid;
                                }
                            }
                            drawDisplayItem(view, gl, itemDrawn, textureToUse, PASS_FRAME, previousTexture, ratio);
                        }
                    }
                }
            }
            GridDrawables.sFrame.unbindArrays(gl);
            if (mSpreadValue <= 1.0f)
                gl.glDepthFunc(GL10.GL_ALWAYS);
            if (state == GridLayer.STATE_MEDIA_SETS || state == GridLayer.STATE_TIMELINE) {
                DisplaySlot[] displaySlots = mDisplaySlots;
                GridDrawables.sTextGrid.bindArrays(gl);
                final float textOffsetY = 0.82f;
                gl.glTranslatef(0.0f, -textOffsetY, 0.0f);
                HashMap<String, StringTexture> stringTextureTable = GridDrawables.sStringTextureTable;
                ReverseGeocoder reverseGeocoder = App.get(view.getContext()).getReverseGeocoder();

                boolean itemsPresent = false;

                for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
                    itemsPresent = true;
                    if (mSpreadValue > 1.0f)
                        continue;
                    DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                    if (displayItem != null) {
                        DisplaySlot displaySlot = displaySlots[i - firstBufferedVisibleSlot];
                        Texture textureString = displaySlot.getTitleImage(stringTextureTable);
                        view.loadTexture(textureString);
                        if (textureString != null) {
                            if (i < firstVisibleSlot || i > lastVisibleSlot) {
                                continue;
                            }
                            drawDisplayItem(view, gl, displayItem, textureString, PASS_TEXT_LABEL, null, 0);
                        }
                    }
                }

                if (!itemsPresent && !isFeedLoading) {
                    // Draw the no items texture.
                    int wWidth = view.getWidth();
                    int wHeight = view.getHeight();

                    // Size this to be 40 pxls less than screen width
                    mNoItemsTexture.mWidth = wWidth - 40;

                    int x = (int) Math.floor((wWidth / 2) - (mNoItemsTexture.getWidth() / 2));
                    int y = (int) Math.floor((wHeight / 2) - (mNoItemsTexture.getHeight() / 2));
                    view.draw2D(mNoItemsTexture, x, y);
                }

                float yLocOffset = 0.2f;
                gl.glTranslatef(0.0f, -yLocOffset, 0.0f);
                for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
                    if (mSpreadValue > 1.0f)
                        continue;
                    DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                    if (displayItem != null) {
                        DisplaySlot displaySlot = displaySlots[i - firstBufferedVisibleSlot];
                        StringTexture textureString = displaySlot.getLocationImage(reverseGeocoder, stringTextureTable);
                        if (textureString != null) {
                            view.loadTexture(textureString);
                            drawDisplayItem(view, gl, displayItem, textureString, PASS_TEXT_LABEL, null, 0);
                        }
                    }
                }
                if (state == GridLayer.STATE_TIMELINE) {
                    GridDrawables.sLocationGrid.bindArrays(gl);
                    Texture locationTexture = drawables.mTextureLocation;
                    final float yLocationLabelOffset = 0.19f;
                    for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
                        if (mCurrentScaleSlot != Shared.INVALID)
                            continue;
                        DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                        if (displayItem != null) {
                            if (displayItem.mAlive == true) {
                                DisplaySlot displaySlot = displaySlots[i - firstBufferedVisibleSlot];
                                if (displaySlot.hasValidLocation()) {
                                    StringTexture textureString = displaySlot.getLocationImage(reverseGeocoder, stringTextureTable);
                                    float textWidth = (textureString != null) ? textureString.computeTextWidth() : 0;
                                    textWidth *= (mCamera.mOneByScale * 0.5f);
                                    if (textWidth == 0.0f) {
                                        textWidth -= 0.18f;
                                    }
                                    textWidth += 0.1f;
                                    gl.glTranslatef(textWidth, -yLocationLabelOffset, 0.0f);
                                    drawDisplayItem(view, gl, displayItem, locationTexture, PASS_LOCATION_LABEL, null, 0);
                                    gl.glTranslatef(-textWidth, yLocationLabelOffset, 0.0f);
                                }
                            }
                        }
                    }

                    GridDrawables.sLocationGrid.unbindArrays(gl);
                } else if (state == GridLayer.STATE_MEDIA_SETS && stackMixRatio > 0.0f) {
                    GridDrawables.sSourceIconGrid.bindArrays(gl);
                    Texture transparentTexture = drawables.mTextureTransparent;
                    for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
                        DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                        if (mCurrentScaleSlot != Shared.INVALID)
                            continue;
                        if (displayItem != null) {
                            if (displayItem.mAlive == true) {
                                DisplaySlot displaySlot = displaySlots[i - firstBufferedVisibleSlot];
                                Texture locationTexture = view.getResource(drawables
                                        .getIconForSet(displaySlot.getMediaSet(), false), false);

                                // Draw the icon at 0.85 alpha over the top item
                                // in the stack.
                                gl.glTranslatef(0.24f, 0.5f, 0);
                                drawDisplayItem(view, gl, displayItem, locationTexture, PASS_MEDIASET_SOURCE_LABEL,
                                        transparentTexture, 0.85f);
                                gl.glTranslatef(-0.24f, -0.5f, 0);
                            }
                        }
                    }
                    GridDrawables.sSourceIconGrid.unbindArrays(gl);
                }
                gl.glTranslatef(0.0f, yLocOffset, 0.0f);
                gl.glTranslatef(0.0f, textOffsetY, 0.0f);
                GridDrawables.sTextGrid.unbindArrays(gl);
            }
            if (hudMode == HudLayer.MODE_SELECT && state != GridLayer.STATE_FULL_SCREEN) {
                Texture textureSelectedOn = drawables.mTextureCheckmarkOn;
                Texture textureSelectedOff = drawables.mTextureCheckmarkOff;
                view.prime(textureSelectedOn, true);
                view.prime(textureSelectedOff, true);
                GridDrawables.sSelectedGrid.bindArrays(gl);
                for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
                    DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                    if (displayItem != null) {
                        Texture textureToUse = selectedBucketList.find(displayItem.mItemRef) ? textureSelectedOn
                                : textureSelectedOff;
                        drawDisplayItem(view, gl, displayItem, textureToUse, PASS_SELECTION_LABEL, null, 0);
                    }
                }
                GridDrawables.sSelectedGrid.unbindArrays(gl);
            }
            GridDrawables.sVideoGrid.bindArrays(gl);
            Texture videoTexture = drawables.mTextureVideo;
            for (int i = firstBufferedVisibleSlot; i <= lastBufferedVisibleSlot; ++i) {
                DisplayItem displayItem = displayItems[(i - firstBufferedVisibleSlot) * GridLayer.MAX_ITEMS_PER_SLOT];
                if (displayItem != null && displayItem.mAlive) {
                    if (displayItem.mItemRef.getMediaType() == MediaItem.MEDIA_TYPE_VIDEO) {
                        drawDisplayItem(view, gl, displayItem, videoTexture, PASS_VIDEO_LABEL, null, 0);
                    }
                }
            }
            GridDrawables.sVideoGrid.unbindArrays(gl);
            gl.glDepthFunc(GL10.GL_LEQUAL);
        }
    }

    private void drawDisplayItem(RenderView view, GL11 gl, DisplayItem displayItem, Texture texture, int pass,
            Texture previousTexture, float mixRatio) {
        GridCamera camera = mCamera;
        Vector3f animatedPosition = displayItem.mAnimatedPosition;
        float translateXf = animatedPosition.x * camera.mOneByScale;
        float translateYf = animatedPosition.y * camera.mOneByScale;
        float translateZf = -animatedPosition.z;
        int stackId = displayItem.getStackIndex();
        final int maxDisplayedItemsPerSlot = (displayItem.mCurrentSlotIndex == mCurrentScaleSlot && mCurrentScaleSlot != Shared.INVALID) ? GridLayer.MAX_DISPLAYED_ITEMS_PER_FOCUSED_SLOT
                : GridLayer.MAX_DISPLAYED_ITEMS_PER_SLOT;
        if (pass == PASS_PLACEHOLDER || pass == PASS_FRAME_PLACEHOLDER) {
            translateZf = -0.04f;
        } else {
            if (pass == PASS_FRAME)
                translateZf += 0.02f;
            if ((pass == PASS_TEXT_LABEL || pass == PASS_LOCATION_LABEL || pass == PASS_SELECTION_LABEL) && !displayItem.isAlive()) {
                translateZf = 0.0f;
            }
            if (pass == PASS_TEXT_LABEL && translateZf > 0) {
                translateZf = 0.0f;
            }
        }
        boolean usingMixedTextures = false;
        boolean bind = false;
        if ((pass != PASS_THUMBNAIL_CONTENT)
                || (stackId < maxDisplayedItemsPerSlot && texture.isLoaded() && (previousTexture == null || previousTexture
                        .isLoaded()))) {
            if (mixRatio == 1.0f || previousTexture == null || texture == previousTexture) {
                bind = view.bind(texture);
            } else if (mixRatio != 0.0f) {
                if (!texture.isLoaded() || !previousTexture.isLoaded()) {
                    // Submit the previous texture to the load queue
                    view.bind(previousTexture);
                    bind = view.bind(texture);
                } else {
                    usingMixedTextures = true;
                    bind = view.bindMixed(previousTexture, texture, mixRatio);
                }
            } else {
                bind = view.bind(previousTexture);
            }
        } else if (stackId >= maxDisplayedItemsPerSlot && pass == PASS_THUMBNAIL_CONTENT) {
            mDisplayList.setAlive(displayItem, true);
        }
        if (!texture.isLoaded() || !bind) {
            if (pass == PASS_THUMBNAIL_CONTENT) {
                if (previousTexture != null && previousTexture.isLoaded() && translateZf == 0.0f) {
                    translateZf = -0.08f;
                    bind |= view.bind(previousTexture);
                }
                if (!bind) {
                    return;
                }
            } else {
                return;
            }
        } else {
            if (pass == PASS_THUMBNAIL_CONTENT || pass == PASS_FOCUS_CONTENT) {
                if (!displayItem.mAlive) {
                    mDisplayList.setAlive(displayItem, true);
                }
            }
        }
        gl.glTranslatef(-translateXf, -translateYf, -translateZf);
        float theta = (pass == PASS_FOCUS_CONTENT) ? displayItem.mAnimatedImageTheta + displayItem.mAnimatedTheta
                : displayItem.mAnimatedTheta;
        if (theta != 0.0f) {
            gl.glRotatef(theta, 0.0f, 0.0f, 1.0f);
        }
        float orientation = 0.0f;
        if (pass == PASS_THUMBNAIL_CONTENT && displayItem.mAnimatedImageTheta != 0.0f) {
            orientation = displayItem.mAnimatedImageTheta;
        }
        if (pass == PASS_FRAME || pass == PASS_FRAME_PLACEHOLDER) {
            GridQuadFrame.draw(gl);
        } else {
            GridQuad.draw(gl, orientation);
        }
        if (theta != 0.0f) {
            gl.glRotatef(-theta, 0.0f, 0.0f, 1.0f);
        }
        gl.glTranslatef(translateXf, translateYf, translateZf);
        if (usingMixedTextures) {
            view.unbindMixed();
        }
    }
}
