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

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.TextPaint;
import android.view.MotionEvent;

public final class PopupMenu extends Layer {
    private static final int POPUP_TRIANGLE_EXTRA_HEIGHT = 14;
    private static final int POPUP_TRIANGLE_X_MARGIN = 16;
    private static final int POPUP_Y_OFFSET = 20;
    private static final Paint SRC_PAINT = new Paint();
    private static final int PADDING_LEFT = 10 + 5;
    private static final int PADDING_TOP = 10 + 3;
    private static final int PADDING_RIGHT = 10 + 5;
    private static final int PADDING_BOTTOM = 30 + 10;
    private static final int ICON_TITLE_MIN_WIDTH = 100;
    private static final IconTitleDrawable.Config ICON_TITLE_CONFIG;

    private PopupTexture mPopupTexture;
    private Listener mListener = null;
    private Option[] mOptions = {};
    private boolean mNeedsLayout = false;
    private boolean mShow = false;
    private final FloatAnim mShowAnim = new FloatAnim(0f);
    private int mRowHeight = 36;
    private int mSelectedItem = -1;

    static {
        TextPaint paint = new TextPaint();
        paint.setTextSize(17f * App.PIXEL_DENSITY);
        paint.setColor(0xffffffff);
        paint.setAntiAlias(true);
        ICON_TITLE_CONFIG = new IconTitleDrawable.Config((int) (45 * App.PIXEL_DENSITY), (int) (34 * App.PIXEL_DENSITY),
                paint);
        SRC_PAINT.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    public PopupMenu(Context context) {
        mPopupTexture = new PopupTexture(context);
        setHidden(true);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public void setOptions(Option[] options) {
        close(false);
        mOptions = options;
        mNeedsLayout = true;
    }

    public void showAtPoint(int pointX, int pointY, int outerWidth, int outerHeight) {
        // Compute layout if needed.
        if (mNeedsLayout) {
            layout();
        }
        // Try to center the popup over the target point.
        int width = (int) mWidth;
        int height = (int) mHeight;
        int widthOver2 = width / 2;
        int x = pointX - widthOver2;
        int y = pointY + POPUP_Y_OFFSET - height;
        int clampedX = Shared.clamp(x, 0, outerWidth - width);
        int triangleWidthOver2 = mPopupTexture.mTriangleBottom.getWidth() / 2;
        mPopupTexture.mTriangleX = Shared.clamp(widthOver2 + (x - clampedX) - triangleWidthOver2, POPUP_TRIANGLE_X_MARGIN, width
                - POPUP_TRIANGLE_X_MARGIN * 2);
        mPopupTexture.setNeedsDraw();
        setPosition(clampedX, y);

        // Fade in the menu if it is not already visible, otherwise snap to the
        // new location.
        // if (!mShow) {
        mShow = true;
        setHidden(false);
        mShowAnim.setValue(0);
        mShowAnim.animateValue(1f, 0.4f, SystemClock.uptimeMillis());
        // }
    }

    public void close(boolean fadeOut) {
        if (mShow) {
            if (fadeOut) {
                mShowAnim.animateValue(0, 0.3f, SystemClock.uptimeMillis());
            } else {
                mShowAnim.setValue(0);
            }
            mShow = false;
            mSelectedItem = -1;
        }

    }

    @Override
    public void generate(RenderView view, RenderView.Lists lists) {
        lists.blendedList.add(this);
        lists.hitTestList.add(this);
        lists.systemList.add(this);
        lists.updateList.add(this);
    }

    @Override
    protected void onSizeChanged() {
        super.onSizeChanged();
        mPopupTexture.setSize((int) mWidth, (int) mHeight);
    }

    @Override
    protected void onSurfaceCreated(RenderView view, GL11 gl) {
        close(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int hit = hitTestOptions((int) event.getX(), (int) event.getY());
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
        case MotionEvent.ACTION_MOVE:
            setSelectedItem(hit);
            break;
        case MotionEvent.ACTION_UP:
            if (hit != -1 && mSelectedItem == hit) {
                mOptions[hit].mAction.run();
                if (mListener != null) {
                    mListener.onSelectionClicked(this, hit);
                }
            }
        case MotionEvent.ACTION_CANCEL:
            setSelectedItem(-1);
            break;
        }
        return true;
    }

    private void setSelectedItem(int hit) {
        if (mSelectedItem != hit) {
            mSelectedItem = hit;
            mPopupTexture.setNeedsDraw();
            if (mListener != null) {
                mListener.onSelectionChanged(this, hit);
            }
        }
    }

    @Override
    public boolean update(RenderView view, float timeElapsed) {
        return (mShowAnim.getTimeRemaining(SystemClock.uptimeMillis()) > 0);
    }

    @Override
    public void renderBlended(RenderView view, GL11 gl) {
        // Hide the layer if the close animation is complete.
        float showRatio = mShowAnim.getValue(SystemClock.uptimeMillis());
        boolean show = mShow;
        if (showRatio < 0.003f && !show) {
            setHidden(true);
        }

        // Draw the selection menu with the show animation.
        int x = (int) mX;
        int y = (int) mY;
        if (show && showRatio < 1f) {
            // Animate the scale as well for the open animation.
            float scale;
            float split = 0.7f;
            if (showRatio < split) {
                scale = 0.8f + 0.3f * showRatio / split;
            } else {
                scale = 1f + ((1f - showRatio) / (1f - split)) * 0.1f;
            }
            mPopupTexture.drawWithEffect(view, gl, x, y, 0.5f, 0.65f, showRatio, scale);
        } else {
            if (showRatio < 1f) {
                view.setAlpha(showRatio);
            }
            mPopupTexture.draw(view, gl, x, y);
            if (showRatio < 1f) {
                view.resetColor();
            }
        }

    }

    private void layout() {
        // Mark as not needing layout.
        mNeedsLayout = false;

        // Measure the menu options.
        Option[] options = mOptions;
        int numOptions = options.length;
        int maxWidth = (int) (ICON_TITLE_MIN_WIDTH * App.PIXEL_DENSITY);
        for (int i = 0; i != numOptions; ++i) {
            Option option = options[i];
            IconTitleDrawable drawable = option.mDrawable;
            if (drawable == null) {
                drawable = new IconTitleDrawable(option.mTitle, option.mIcon, ICON_TITLE_CONFIG);
                option.mDrawable = drawable;
            }
            int width = drawable.getIntrinsicWidth();
            if (width > maxWidth) {
                maxWidth = width;
            }
        }

        // Layout the menu options.
        int rowHeight = (int) (mRowHeight * App.PIXEL_DENSITY);
        int left = (int) (PADDING_LEFT * App.PIXEL_DENSITY);
        int top = (int) (PADDING_TOP * App.PIXEL_DENSITY);
        int right = left + maxWidth;
        for (int i = 0; i != numOptions; ++i) {
            Option option = options[i];
            IconTitleDrawable drawable = option.mDrawable;
            option.mBottom = top + rowHeight;
            drawable.setBounds(left, top, right, option.mBottom);
            top += rowHeight;
        }

        // Resize the popup menu.
        setSize(right + PADDING_RIGHT * App.PIXEL_DENSITY, top + PADDING_BOTTOM * App.PIXEL_DENSITY);

    }

    private int hitTestOptions(int x, int y) {
        Option[] options = mOptions;
        int numOptions = options.length;
        x -= mX;
        y -= mY;
        if (numOptions != 0 && x >= 0 && x < mWidth && y >= 0) {
            for (int i = 0; i != numOptions; ++i) {
                if (y < options[i].mBottom) {
                    return i;
                }
            }
        }
        return -1;
    }

    public interface Listener {
        void onSelectionChanged(PopupMenu menu, int selectedIndex);

        void onSelectionClicked(PopupMenu menu, int selectedIndex);
    }

    public static final class Option {
        private final String mTitle;
        private final Drawable mIcon;
        private final Runnable mAction;
        private IconTitleDrawable mDrawable = null;
        private int mBottom;

        public Option(String title, Drawable icon, Runnable action) {
            mTitle = title;
            mIcon = icon;
            mAction = action;
        }
    }

    private final class PopupTexture extends CanvasTexture {
        private final NinePatch mBackground;
        private final NinePatch mHighlightSelected;
        private final Bitmap mTriangleBottom;
        private final Rect mBackgroundRect = new Rect();
        private int mTriangleX = 0;

        @SuppressWarnings("static-access")
		public PopupTexture(Context context) {
            super(Bitmap.Config.ARGB_8888);
            Resources resources = context.getResources();
            Bitmap background = BitmapFactory.decodeResource(resources, Res.drawable.popup);
            mBackground = new NinePatch(background, background.getNinePatchChunk(), null);
            Bitmap highlightSelected = BitmapFactory.decodeResource(resources, Res.drawable.popup_option_selected);
            mHighlightSelected = new NinePatch(highlightSelected, highlightSelected.getNinePatchChunk(), null);
            mTriangleBottom = BitmapFactory.decodeResource(resources, Res.drawable.popup_triangle_bottom);
        }

        @Override
        protected void onSizeChanged() {
            mBackgroundRect.set(0, 0, getWidth(), getHeight() - (int) (POPUP_TRIANGLE_EXTRA_HEIGHT * App.PIXEL_DENSITY));
        }

        @Override
        protected void renderCanvas(Canvas canvas, Bitmap backing, int width, int height) {
            // Draw the background.
            backing.eraseColor(0);
            mBackground.draw(canvas, mBackgroundRect, SRC_PAINT);

            // Stamp the popup triangle over the appropriate region ignoring
            // alpha.
            Bitmap triangle = mTriangleBottom;
            canvas.drawBitmap(triangle, mTriangleX, height - triangle.getHeight() - 1, SRC_PAINT);

            // Draw the selection / focus highlight.
            Option[] options = mOptions;
            int selectedItem = mSelectedItem;
            if (selectedItem != -1) {
                Option option = options[selectedItem];
                mHighlightSelected.draw(canvas, option.mDrawable.getBounds());
            }

            // Draw icons and titles.
            int numOptions = options.length;
            for (int i = 0; i != numOptions; ++i) {
                options[i].mDrawable.draw(canvas);
            }
        }

    }
}
