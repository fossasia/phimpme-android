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

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

public final class IconTitleDrawable extends Drawable {
    private final String mTitle;
    private final int mTitleWidth;
    private final Drawable mIcon;
    private final Config mConfig;
    private StaticLayout mTitleLayout = null;
    private int mTitleY;

    public IconTitleDrawable(String title, Drawable icon, Config config) {
        mTitle = title;
        mTitleWidth = (int) StaticLayout.getDesiredWidth(mTitle, config.mPaint);
        mIcon = icon;
        mConfig = config;
    }

    @Override
    public int getIntrinsicWidth() {
        Config config = mConfig;
        return config.mTitleLeft + mTitleWidth + 15;
    }

    @Override
    public int getIntrinsicHeight() {
        Config config = mConfig;
        return Math.max(config.mIconSize, config.mPaint.getFontMetricsInt(null));
    }

    @Override
    public void draw(Canvas canvas) {
        // Paint test = new Paint();
        // test.setColor(0xff0000ff);
        // canvas.drawRect(getBounds(), test);

        Drawable icon = mIcon;
        if (icon != null) {
            icon.draw(canvas);
        }
        Rect bounds = getBounds();
        int x = bounds.left + mConfig.mTitleLeft;
        int y = mTitleY;
        canvas.translate(x, y);
        mTitleLayout.draw(canvas);
        canvas.translate(-x, -y);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        // Position the icon.
        int left = bounds.left;
        int top = bounds.top;
        int right = bounds.right;
        int height = bounds.bottom - top;
        Config config = mConfig;
        int iconLeft = left + config.mIconLeft;
        int iconSize = config.mIconSize;
        Drawable icon = mIcon;
        if (icon != null) {
            int iconY = top + (height - iconSize) / 2;
            icon.setBounds(iconLeft, iconY, iconLeft + iconSize, top + iconSize);
        }

        // Layout the text.
        int outerWidth = right - config.mTitleLeft;
        String title = mTitle;
        mTitleLayout = new StaticLayout(title, 0, title.length(), config.mPaint, outerWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0,
                true, TextUtils.TruncateAt.MIDDLE, outerWidth);
        mTitleY = top + (height - mTitleLayout.getHeight()) / 2;
    }

    public static final class Config {
        private final int mIconLeft;
        private final int mTitleLeft;
        private final int mIconSize;
        private final TextPaint mPaint;

        public Config(int iconSpan, int iconSize, TextPaint paint) {
            mIconLeft = (iconSpan - iconSize) / 2;
            mTitleLeft = iconSpan;
            mIconSize = iconSize;
            mPaint = paint;
        }
    }
}
