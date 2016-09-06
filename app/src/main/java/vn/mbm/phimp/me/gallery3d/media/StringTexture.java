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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.FloatMath;

public final class StringTexture extends Texture {
    private String mString;
    private Config mConfig;
    private Paint mPaint;
    private int mBaselineHeight;

    private static final Paint sPaint = new Paint();

    public static int computeTextWidthForConfig(String string, Config config) {
        return computeTextWidthForConfig(config.fontSize, config.bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT, string);
    }

    public static int computeTextWidthForConfig(float textSize, Typeface typeface, String string) {
        Paint paint = sPaint;
        synchronized (paint) {
            paint.setAntiAlias(true);
            paint.setTypeface(typeface);
            paint.setTextSize(textSize);
            // 10 pixel buffer to compensate for the shade at the end.
            return (int) (10.0f * App.PIXEL_DENSITY) + (int) Math.ceil(paint.measureText(string));
        }
    }

    public static int lengthToFit(float textSize, float maxWidth, Typeface typeface, String string) {
        if (maxWidth <= 0)
            return 0;
        Paint paint = sPaint;
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setTextSize(textSize);
        int length = string.length();
        float retVal = paint.measureText(string);
        if (retVal <= maxWidth)
            return length;
        else {
            while (retVal > maxWidth) {
                --length;
                retVal = paint.measureText(string, 0, length - 1);
            }
            return length;
        }
    }

    public StringTexture(String string) {
        mString = string;
        mConfig = Config.DEFAULT_CONFIG_SCALED;
    }

    public StringTexture(String string, Config config) {
        this(string, config, config.width, config.height);
    }

    public StringTexture(String string, Config config, int width, int height) {
        mString = string;
        mConfig = config;
        mWidth = width;
        mHeight = height;
    }

    public float computeTextWidth() {
        Paint paint = computePaint();
        if (paint != null) {
            if (mString != null)
                return paint.measureText(mString);
            else
                return 0;
        } else {
            return 0;
        }
    }

    @Override
    public boolean isCached() {
        return true;
    }

    public float getBaselineHeight() {
        return mBaselineHeight;
    }

    protected Paint computePaint() {
        if (mPaint != null)
            return mPaint;
        Paint paint = new Paint();
        mPaint = paint;
        paint.setAntiAlias(true);
        Config config = mConfig;
        int alpha = (int) (config.a * 255);
        int red = (int) (config.r * 255);
        int green = (int) (config.g * 255);
        int blue = (int) (config.b * 255);
        int color = Color.argb(alpha, red, green, blue);
        paint.setColor(color);
        paint.setShadowLayer(config.shadowRadius, 0, 0, Color.BLACK);
        paint.setUnderlineText(config.underline);
        paint.setTypeface(config.bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        paint.setStrikeThruText(config.strikeThrough);
        // int originX = 0;
        if (config.xalignment == Config.ALIGN_LEFT) {
            paint.setTextAlign(Align.LEFT);
        } else if (config.xalignment == Config.ALIGN_RIGHT) {
            paint.setTextAlign(Align.RIGHT);
            // originX = (int)config.width;
        } else {
            paint.setTextAlign(Align.CENTER);
            // originX = (int)config.height;
        }
        if (config.italic)
            paint.setTextSkewX(-0.25f);
        String stringToDraw = mString;
        paint.setTextSize(config.fontSize);
        if (config.sizeMode == Config.SIZE_TEXT_TO_BOUNDS) {
            // we have to compute the fontsize appropriately
            while (true) {
                // we measure the textwidth
                float currentTextSize = paint.getTextSize();
                float measuredTextWidth = 0;
                measuredTextWidth = paint.measureText(stringToDraw);
                if (measuredTextWidth < mWidth)
                    break;
                paint.setTextSize(currentTextSize - 1.0f);
                if (currentTextSize <= 6.0f)
                    break;
            }
        }
        return paint;
    }

    @Override
    protected Bitmap load(RenderView view) {
        if (mString == null)
            return null;
        Paint paint = computePaint();
        String stringToDraw = mString;
        Config config = mConfig;
        Bitmap.Config bmConfig = Bitmap.Config.ARGB_4444;
        Paint.FontMetricsInt metrics = paint.getFontMetricsInt();
        // 1 pixel for anti-aliasing
        int padding = 1 + config.shadowRadius;
        int ascent = metrics.ascent - padding;
        int descent = metrics.descent + padding;
        int backWidth = mWidth;
        int backHeight = mHeight;

        String string = mString;
        Rect bounds = new Rect();
        paint.getTextBounds(string, 0, string.length(), bounds);

        if (config.sizeMode == Config.SIZE_BOUNDS_TO_TEXT) {
            // do something else
            backWidth = bounds.width() + 2 * padding;
            int height = descent - ascent;
            backHeight = height + padding;
        }
        if (backWidth <= 0 || backHeight <= 0)
            return null;
        Bitmap bitmap = Bitmap.createBitmap(backWidth, backHeight, bmConfig);
        Canvas canvas = new Canvas(bitmap);
        // for top
        int x = (config.xalignment == Config.ALIGN_LEFT) ? padding : (config.xalignment == Config.ALIGN_RIGHT ? backWidth - padding
                : backWidth / 2);
        int y = (config.yalignment == Config.ALIGN_TOP) ? -metrics.top + padding
                : ((config.yalignment == Config.ALIGN_BOTTOM) ? (backHeight - descent)
                        : ((int) backHeight - (descent + ascent)) / 2);
        // bitmap.eraseColor(0xff00ff00);
        canvas.drawText(stringToDraw, x, y, paint);

        if (bounds.width() > backWidth && config.overflowMode == Config.OVERFLOW_FADE) {
            // Fade the right edge of the string if the text overflows. TODO:
            // BIDI text should fade on the left.
            float gradientLeft = backWidth - Config.FADE_WIDTH;
            LinearGradient gradient = new LinearGradient(gradientLeft, 0, backWidth, 0, 0xffffffff, 0x00ffffff,
                    Shader.TileMode.CLAMP);
            paint = new Paint();
            paint.setSubpixelText(true);
            paint.setShader(gradient);
            paint.setDither(true);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
            canvas.drawRect(gradientLeft, 0, backWidth, backHeight, paint);
        }

        mBaselineHeight = padding + metrics.bottom;
        return bitmap;
    }

    public static final class Config {
        public static final int SIZE_EXACT = 0;
        public static final int SIZE_TEXT_TO_BOUNDS = 1;
        public static final int SIZE_BOUNDS_TO_TEXT = 2;

        public static final int OVERFLOW_CLIP = 0;
        public static final int OVERFLOW_ELLIPSIZE = 1;
        public static final int OVERFLOW_FADE = 2;

        public static final int ALIGN_HCENTER = 0;
        public static final int ALIGN_LEFT = 1;
        public static final int ALIGN_RIGHT = 2;
        public static final int ALIGN_TOP = 3;
        public static final int ALIGN_BOTTOM = 4;
        public static final int ALIGN_VCENTER = 5;

        private static final int FADE_WIDTH = 30;

        public static final Config DEFAULT_CONFIG_SCALED = new Config();
        public static final Config DEFAULT_CONFIG_TRUNCATED = new Config(SIZE_TEXT_TO_BOUNDS);

        public float fontSize = 20f;
        public float r = 1f;
        public float g = 1f;
        public float b = 1f;
        public float a = 1f;
        public int shadowRadius = 4 * (int) App.PIXEL_DENSITY;
        public boolean underline = false;
        public boolean bold = false;
        public boolean italic = false;
        public boolean strikeThrough = false;
        public int width = 256; // TODO: there is no good default for this,
                                // require explicit specification.
        public int height = 32;
        public int xalignment = ALIGN_LEFT;
        public int yalignment = ALIGN_VCENTER;
        public int sizeMode = SIZE_BOUNDS_TO_TEXT;
        public int overflowMode = OVERFLOW_FADE;

        public Config() {
        }

        public Config(int sizeMode) {
            this.sizeMode = sizeMode;
        }

        public Config(float fontSize, int width, int height) {
            this.fontSize = fontSize;
            this.width = width;
            this.height = height;
            this.sizeMode = SIZE_EXACT;
        }
    };

}
