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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.gallery3d.media.RenderView.Lists;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.SparseArray;
import android.view.MotionEvent;

public final class TimeBar extends Layer implements MediaFeed.Listener {
    public static final int HEIGHT = 48;
    private static final int MARKER_SPACING_PIXELS = 50;
    private static final float AUTO_SCROLL_MARGIN = 100f;
    private static final Paint SRC_PAINT = new Paint();
    private Listener mListener = null;
    private MediaFeed mFeed = null;
    private float mTotalWidth = 0f;
    private float mPosition = 0f;
    private float mPositionAnim = 0f;
    private float mScroll = 0f;
    private float mScrollAnim = 0f;
    private boolean mInDrag = false;
    private float mDragX = 0f;

    private ArrayList<Marker> mMarkers = new ArrayList<Marker>();
    private ArrayList<Marker> mMarkersCopy = new ArrayList<Marker>();

    @SuppressWarnings("static-access")
	private static final int KNOB = Res.drawable.scroller_new;
    @SuppressWarnings("static-access")
	private static final int KNOB_PRESSED = Res.drawable.scroller_pressed_new;
    private final StringTexture.Config mMonthYearFormat = new StringTexture.Config();
    private final StringTexture.Config mDayFormat = new StringTexture.Config();
    private final SparseArray<StringTexture> mYearLabels = new SparseArray<StringTexture>();
    private StringTexture mDateUnknown;
    private final StringTexture[] mMonthLabels = new StringTexture[12];
    private final StringTexture[] mDayLabels = new StringTexture[32];
    private final StringTexture[] mOpaqueDayLabels = new StringTexture[32];
    private final StringTexture mDot = new StringTexture("�");
    private final HashMap<MediaItem, Marker> mTracker = new HashMap<MediaItem, Marker>(1024);
    private int mState;
    private float mTextAlpha = 0.0f;
    private float mAnimTextAlpha = 0.0f;
    private boolean mShowTime;
    private NinePatch mBackground;
    private Rect mBackgroundRect;
    private BitmapTexture mBackgroundTexture;

    public interface Listener {
        public void onTimeChanged(TimeBar timebar);
    }

    TimeBar(Context context) {
        // Setup formatting for text labels.
        mMonthYearFormat.fontSize = 17f * App.PIXEL_DENSITY;
        mMonthYearFormat.bold = true;
        mMonthYearFormat.a = 0.85f;
        mDayFormat.fontSize = 17f * App.PIXEL_DENSITY;
        mDayFormat.a = 0.61f;
        regenerateStringsForContext(context);
        @SuppressWarnings("static-access")
		Bitmap background = BitmapFactory.decodeResource(context.getResources(), Res.drawable.popup);
        mBackground = new NinePatch(background, background.getNinePatchChunk(), null);
        mBackgroundRect = new Rect();
        SRC_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    @SuppressWarnings("static-access")
	public void regenerateStringsForContext(Context context) {
        // Create textures for month names.
        String[] months = context.getResources().getStringArray(Res.array.months_abbreviated);
        for (int i = 0; i < months.length; ++i) {
            mMonthLabels[i] = new StringTexture(months[i], mMonthYearFormat);
        }

        for (int i = 0; i <= 31; ++i) {
            mDayLabels[i] = new StringTexture(Integer.toString(i), mDayFormat);
            mOpaqueDayLabels[i] = new StringTexture(Integer.toString(i), mMonthYearFormat);
        }
        mDateUnknown = new StringTexture(context.getResources().getString(Res.string.date_unknown), mMonthYearFormat);
        mBackgroundTexture = null;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setFeed(MediaFeed feed, int state, boolean needsLayout) {
        mFeed = feed;
        mState = state;
        layout();
        if (needsLayout) {
            mPosition = 0;
            mScroll = getScrollForPosition(mPosition);
        }
    }

    @Override
    protected void onSizeChanged() {
        mScroll = getScrollForPosition(mPosition);
    }

    public MediaItem getItem() {
        synchronized (mMarkers) {
            // x is between 0 and 1.0f
            int numMarkers = mMarkers.size();
            if (numMarkers == 0)
                return null;
            int index = (int) (mPosition * (numMarkers));
            if (index >= numMarkers)
                index = numMarkers - 1;
            Marker marker = mMarkers.get(index);
            if (marker != null) {
                // we have to find the index of the media item depending upon
                // the value of mPosition
                float deltaBetweenMarkers = 1.0f / numMarkers;
                float increment = mPosition - index * deltaBetweenMarkers;
                // if (increment > deltaBetweenMarkers)
                // increment = deltaBetweenMarkers;
                // if (increment < 0)
                // increment = 0;
                ArrayList<MediaItem> items = marker.items;
                int numItems = items.size();
                if (numItems == 0)
                    return null;
                int itemIndex = (int) ((numItems) * increment / deltaBetweenMarkers);
                if (itemIndex >= numItems)
                    itemIndex = numItems - 1;
                return marker.items.get(itemIndex);
            }
        }
        return null;
    }

    private Marker getAnchorMarker() {
        synchronized (mMarkers) {
            // x is between 0 and 1.0f
            int numMarkers = mMarkers.size();
            if (numMarkers == 0)
                return null;
            int index = (int) (mPosition * (numMarkers));
            if (index >= numMarkers)
                index = numMarkers - 1;
            Marker marker = mMarkers.get(index);
            return marker;
        }
    }

    public void setItem(MediaItem item) {
        Marker marker = mTracker.get(item);
        if (marker != null) {
            float markerX = (mTotalWidth == 0.0f) ? 0.0f : marker.x / mTotalWidth;
            mPosition = Math.max(0.0f, Math.min(1.0f, markerX));
            mScroll = getScrollForPosition(mPosition);
        }
    }

    @SuppressWarnings("unchecked")
    private void layout() {
        if (mFeed != null) {
            // Clear existing markers.
            mTracker.clear();
            synchronized (mMarkers) {
                mMarkers.clear();
            }
            float scrollX = mScroll;
            // Place markers for every time interval that intersects one of the
            // clusters.
            // Markers for a full month would be for example: Jan 5 10 15 20 25
            // 30.
            MediaFeed feed = mFeed;
            int lastYear = -1;
            int lastMonth = -1;
            int lastDayBlock = -1;
            float dx = 0f;
            int increment = 12;
            MediaSet set = null;
            mShowTime = true;
            if (mState == GridLayer.STATE_GRID_VIEW) {
                set = feed.getFilteredSet();
                if (set == null) {
                    set = feed.getCurrentSet();
                }
            } else {
                increment = 2;
                if (!feed.hasExpandedMediaSet()) {
                    mShowTime = false;
                }
                set = new MediaSet();
                int numSlots = feed.getNumSlots();
                for (int i = 0; i < numSlots; ++i) {
                    MediaSet slotSet = feed.getSetForSlot(i);
                    if (slotSet != null) {
                        ArrayList<MediaItem> slotSetItems = slotSet.getItems();
                        if (slotSetItems != null && slotSet.getNumItems() > 0) {
                            MediaItem item = slotSetItems.get(0);
                            if (item != null) {
                                set.addItem(item);
                            }
                        }
                    }
                }
            }
            if (set != null) {
                GregorianCalendar time = new GregorianCalendar();
                ArrayList<MediaItem> items = set.getItems();
                if (items != null) {
                    items = (ArrayList<MediaItem>)items.clone();
                    int j = 0;
                    while (j < set.getNumItems()) {
                        final MediaItem item = items.get(j);
                        if (item == null)
                            continue;
                        time.setTimeInMillis(item.mDateTakenInMs);
                        // Detect year rollovers.
                        final int year = time.get(Calendar.YEAR);
                        if (year != lastYear) {
                            lastYear = year;
                            lastMonth = -1;
                            lastDayBlock = -1;
                        }
                        Marker marker = null;
                        // Detect month rollovers and emit a month marker.
                        final int month = time.get(Calendar.MONTH);
                        final int dayBlock = time.get(Calendar.DATE);
                        if (month != lastMonth) {
                            lastMonth = month;
                            lastDayBlock = -1;
                            marker = new Marker(dx, time.getTimeInMillis(), year, month, dayBlock, Marker.TYPE_MONTH, increment);
                            dx = addMarker(marker);
                        } else if (dayBlock != lastDayBlock) {
                            lastDayBlock = dayBlock;
                            if (dayBlock != 0) {
                                marker = new Marker(dx, time.getTimeInMillis(), year, month, dayBlock, Marker.TYPE_DAY, increment);
                                dx = addMarker(marker);
                            }
                        } else {
                            marker = new Marker(dx, time.getTimeInMillis(), year, month, dayBlock, Marker.TYPE_DOT, increment);
                            dx = addMarker(marker);
                        }
                        for (int k = 0; k < increment; ++k) {
                            int index = k + j;
                            if (index < 0)
                                continue;
                            if (index >= items.size())
                                break;
                            if (index == items.size() - 1 && k != 0)
                                break;
                            MediaItem thisItem = items.get(index);
                            marker.items.add(thisItem);
                            mTracker.put(thisItem, marker);
                        }
                        if (j == items.size() - 1)
                            break;
                        j += increment;
                        if (j >= items.size() - 1)
                            j = items.size() - 1;
                    }
                }
                mTotalWidth = dx - MARKER_SPACING_PIXELS * App.PIXEL_DENSITY;
            }
            mPosition = getPositionForScroll(scrollX);
            mPositionAnim = mPosition;
            synchronized (mMarkersCopy) {
                int numMarkers = mMarkers.size();
                mMarkersCopy.clear();
                mMarkersCopy.ensureCapacity(numMarkers);
                for (int i = 0; i < numMarkers; ++i) {
                    mMarkersCopy.add(mMarkers.get(i));
                }
            }
        }
    }

    private float addMarker(Marker marker) {
        mMarkers.add(marker);
        return marker.x + MARKER_SPACING_PIXELS * App.PIXEL_DENSITY;
    }

    /*
     * private float getKnobXForPosition(float position) { return position *
     * (mTotalWidth - mKnob.getWidth()); }
     * 
     * private float getPositionForKnobX(float knobX) { return Math.max(0f,
     * Math.min(1f, knobX / (mTotalWidth - mKnob.getWidth()))); }
     * 
     * private float getScrollForPosition(float position) { return position *
     * (mTotalWidth - mWidth);// - (1f - 2f * position) * MARKER_SPACING_PIXELS;
     * }
     */

    private float getScrollForPosition(float position) {
        // Map position [0, 1] to scroll [-visibleWidth/2, totalWidth -
        // visibleWidth/2].
        // This has the effect of centering the scroll knob on screen.
        float halfWidth = mWidth * 0.5f;
        float positionInv = 1f - position;
        float centered = positionInv * -halfWidth + position * (mTotalWidth - halfWidth);
        return centered;
    }

    private float getPositionForScroll(float scroll) {
        float halfWidth = mWidth * 0.5f;
        if (mTotalWidth == 0)
            return 0;
        return ((scroll + halfWidth) / (mTotalWidth));
    }

    private float getKnobXForPosition(float position) {
        return position * mTotalWidth;
    }

    private float getPositionForKnobX(float knobX) {
        float normKnobX = (mTotalWidth == 0) ? 0 : knobX / mTotalWidth;
        return Math.max(0f, Math.min(1f, normKnobX));
    }

    @Override
    public boolean update(RenderView view, float dt) {
        // Update animations.
        final float ratio = Math.min(1f, 10f * dt);
        final float invRatio = 1f - ratio;
        mPositionAnim = ratio * mPosition + invRatio * mPositionAnim;
        mScrollAnim = ratio * mScroll + invRatio * mScrollAnim;
        // Handle autoscroll.
        if (mInDrag) {
            final float x = getKnobXForPosition(mPosition) - mScrollAnim;
            float velocity;
            float autoScrollMargin = AUTO_SCROLL_MARGIN * App.PIXEL_DENSITY;
            if (x < autoScrollMargin) {
                velocity = -(float) Math.pow((1f - x / autoScrollMargin), 2);
            } else if (x > mWidth - autoScrollMargin) {
                velocity = (float) Math.pow(1f - (mWidth - x) / autoScrollMargin, 2);
            } else {
                velocity = 0;
            }
            mScroll += velocity * 400f * dt;
            mPosition = getPositionForKnobX(mDragX + mScroll);
            mTextAlpha = 1.0f;
        } else {
            mTextAlpha = 0.0f;
        }
        mAnimTextAlpha = FloatUtils.animate(mAnimTextAlpha, mTextAlpha, dt);
        return mAnimTextAlpha != mTextAlpha;
    }

    @Override
    public void renderBlended(RenderView view, GL11 gl) {
        final float originX = mX;
        final float originY = mY;
        final float scrollOffset = mScrollAnim;
        final float scrolledOriginX = originX - scrollOffset;
        final float position = mPositionAnim;
        final int knobId = mInDrag ? KNOB_PRESSED : KNOB;
        final Texture knob = view.getResource(knobId);
        // Draw the scroller knob.
        if (!mShowTime) {
            if (view.bind(knob)) {
                final float knobWidth = knob.getWidth();
                view.draw2D(scrolledOriginX + getKnobXForPosition(position) - knobWidth * 0.5f, originY, 0f, knobWidth, knob
                        .getHeight());
            }
        } else {
            if (view.bind(knob)) {
                final float knobWidth = knob.getWidth();
                final float knobHeight = knob.getHeight();
                view.draw2D(scrolledOriginX + getKnobXForPosition(position) - knobWidth * 0.5f, view.getHeight() - knobHeight, 0f,
                        knobWidth, knobHeight);
            }
            // we draw the current time on top of the knob
            if (mInDrag || mAnimTextAlpha != 0.0f) {
                Marker anchor = getAnchorMarker();
                if (anchor != null) {
                    Texture month = mMonthLabels[anchor.month];
                    Texture day = mOpaqueDayLabels[anchor.day];
                    Texture year = getYearLabel(anchor.year);
                    boolean validDate = true;
                    if (anchor.year <= 1970) {
                        month = mDateUnknown;
                        day = null;
                        year = null;
                        validDate = false;
                    }
                    view.loadTexture(month);
                    if (validDate) {
                        view.loadTexture(day);
                        view.loadTexture(year);
                    }
                    int numPixelsBufferX = 70;
                    float expectedWidth = month.getWidth()
                            + ((validDate) ? (day.getWidth() + year.getWidth() + 10 * App.PIXEL_DENSITY) : 0);
                    if ((expectedWidth + numPixelsBufferX * App.PIXEL_DENSITY) != mBackgroundRect.right) {
                        mBackgroundRect.right = (int) (expectedWidth + numPixelsBufferX * App.PIXEL_DENSITY);
                        mBackgroundRect.bottom = (int) (month.getHeight() + 20 * App.PIXEL_DENSITY);
                        try {
                            Bitmap bitmap = Bitmap.createBitmap(mBackgroundRect.right, mBackgroundRect.bottom,
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas();
                            canvas.setBitmap(bitmap);
                            mBackground.draw(canvas, mBackgroundRect, SRC_PAINT);
                            mBackgroundTexture = new BitmapTexture(bitmap);
                            view.loadTexture(mBackgroundTexture);
                            bitmap.recycle();
                        } catch (OutOfMemoryError e) {
                            // Do nothing.
                        }
                    }
                    gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
                    gl.glColor4f(mAnimTextAlpha, mAnimTextAlpha, mAnimTextAlpha, mAnimTextAlpha);
                    float x = (view.getWidth() - expectedWidth - numPixelsBufferX * App.PIXEL_DENSITY) / 2;
                    float y = (view.getHeight() - 10 * App.PIXEL_DENSITY) * 0.5f;
                    if (mBackgroundTexture != null) {
                        view.draw2D(mBackgroundTexture, x, y);
                    }
                    y = view.getHeight() * 0.5f;
                    x = (view.getWidth() - expectedWidth) / 2;
                    view.draw2D(month, x, y);
                    if (validDate) {
                        x += month.getWidth() + 3 * App.PIXEL_DENSITY;
                        view.draw2D(day, x, y);
                        x += day.getWidth() + 7 * App.PIXEL_DENSITY;
                        view.draw2D(year, x, y);
                    }
                    if (mAnimTextAlpha != 1f) {
                        gl.glColor4f(1f, 1f, 1f, 1f);
                    }
                    gl.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_REPLACE);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Set position on touch movement.
        mDragX = event.getX();
        mPosition = getPositionForKnobX(mDragX + mScroll);

        // Notify the listener.
        if (mListener != null) {
            mListener.onTimeChanged(this);
        }

        // Update state when touch begins and ends.
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mInDrag = true;
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            // mScroll = getScrollForPosition(mPosition);
            mInDrag = false;

            // Clamp to the nearest marker.
            setItem(getItem());
        default:
            break;
        }

        return true;
    }

    public void onFeedChanged(MediaFeed feed, boolean needsLayout) {
        layout();
    }

    private static final class Marker {
        Marker(float x, long time, int year, int month, int day, int type, int expectedCapacity) {
            this.x = x;
            this.year = year;
            this.month = month;
            this.day = day;
            this.items = new ArrayList<MediaItem>(expectedCapacity);
        }

        public static final int TYPE_MONTH = 1;
        public static final int TYPE_DAY = 2;
        public static final int TYPE_DOT = 3;
        public ArrayList<MediaItem> items;
        public final float x;
        public final int year;
        public final int month;
        public final int day;
    }

    @Override
    public void generate(RenderView view, Lists lists) {
        lists.updateList.add(this);
        lists.blendedList.add(this);
        lists.hitTestList.add(this);
    }

    public void onFeedAboutToChange(MediaFeed feed) {
        // nothing needs to be done
        return;
    }

    private StringTexture getYearLabel(int year) {
        if (year <= 1970)
            return mDot;
        StringTexture label = mYearLabels.get(year);
        if (label == null) {
            label = new StringTexture(Integer.toString(year), mMonthYearFormat);
            mYearLabels.put(year, label);
        }
        return label;
    }

    public boolean isDragged() {
        return mInDrag;
    }
}
