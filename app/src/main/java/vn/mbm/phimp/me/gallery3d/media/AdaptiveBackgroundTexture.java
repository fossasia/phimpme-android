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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;

// CR: class comment
public final class AdaptiveBackgroundTexture extends Texture {
    private static final int RED_MASK = 0xff0000;
    private static final int RED_MASK_SHIFT = 16;
    private static final int GREEN_MASK = 0x00ff00;
    private static final int GREEN_MASK_SHIFT = 8;
    private static final int BLUE_MASK = 0x0000ff;
    private static final int RADIUS = 4;
    private static final int KERNEL_SIZE = RADIUS * 2 + 1;
    private static final int NUM_COLORS = 256;
    private static final int MAX_COLOR_VALUE = NUM_COLORS - 1;
    private static final int[] KERNEL_NORM = new int[KERNEL_SIZE * NUM_COLORS];
    private static final int MULTIPLY_COLOR = 0xffaaaaaa;
    private static final int START_FADE_X = 96;
    private static final int THUMBNAIL_MAX_X = 128;

    private final int mWidth;
    private final int mHeight;
    private final Bitmap mSource;
    private Texture mBaseTexture;

    static {
        // Build a lookup table from summed to normalized kernel values.
        for (int i = KERNEL_SIZE * NUM_COLORS - 1; i >= 0; --i) {
            KERNEL_NORM[i] = i / KERNEL_SIZE;
        }
    }

    public AdaptiveBackgroundTexture(Bitmap source, int width, int height) {
        mSource = source;
        mWidth = width;
        mHeight = height;
        mBaseTexture = null;
    }

    public AdaptiveBackgroundTexture(Texture texture, int width, int height) {
        mBaseTexture = texture;
        mSource = null;
        mWidth = width;
        mHeight = height;
    }

    @Override
    protected boolean shouldQueue() {
        return true;
    }

    @Override
    public boolean isCached() {
        return true;
    }

    @Override
    protected Bitmap load(RenderView view) {
        // Determine a crop rectangle for the source image that is the aspect
        // ratio of the destination.
        Bitmap source = mSource;
        if (source == null) {
            if (mBaseTexture != null) {
                source = mBaseTexture.load(view);
                if (source == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        source = Utils.resizeBitmap(source, THUMBNAIL_MAX_X);
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        int destWidth = mWidth;
        int destHeight = mHeight;
        float fitX = (float) sourceWidth / (float) destWidth;
        float fitY = (float) sourceHeight / (float) destHeight;
        float scale;
        int cropX;
        int cropY;
        int cropWidth;
        int cropHeight;
        if (fitX < fitY) {
            // Full width, partial height.
            cropWidth = sourceWidth;
            cropHeight = (int) (destHeight * fitX);
            cropX = 0;
            cropY = (sourceHeight - cropHeight) / 2;
            scale = 1.0f / fitX;
        } else {
            // Full height, partial or full width.
            cropWidth = (int) (destHeight * fitY);
            cropHeight = sourceHeight;
            cropX = (sourceWidth - cropWidth) / 2;
            cropY = 0;
            scale = 1f / fitY;
        }

        // Create a source and destination buffer for the image.
        int numPixels = cropWidth * cropHeight;
        int[] in = new int[numPixels];
        int[] tmp = new int[numPixels];

        // Get the source pixels as 32-bit ARGB.
        source.getPixels(in, 0, cropWidth, cropX, cropY, cropWidth, cropHeight);

        // Box blur is a separable kernel, so it is decomposed into a horizontal
        // and vertical pass.
        // The filter function applies the kernel across each row and transposes
        // the output.
        // Hence we apply it twice to provide efficient horizontal and vertical
        // convolution.
        // The filter discards the alpha channel.
        boxBlurFilter(in, tmp, cropWidth, cropHeight, cropWidth);
        boxBlurFilter(tmp, in, cropHeight, cropWidth, START_FADE_X);

        // Return a bitmap scaled to the desired size.
        Bitmap filtered = Bitmap.createBitmap(in, cropWidth, cropHeight, Bitmap.Config.ARGB_8888);

        // Composite the bitmap scaled to the target size and darken the pixels.
        Bitmap output = Bitmap.createBitmap(destWidth, destHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColorFilter(new LightingColorFilter(MULTIPLY_COLOR, 0));
        canvas.scale(scale, scale);
        canvas.drawBitmap(filtered, 0f, 0f, paint);
        filtered.recycle();

        // Clear the texture
        mBaseTexture = null;
        return output;
    }

    private static void boxBlurFilter(int[] in, int[] out, int width, int height, int startFadeX) {
        int inPos = 0;
        int maxX = width - 1;
        for (int y = 0; y < height; ++y) {
            // Evaluate the kernel for the first pixel in the row.
            int red = 0;
            int green = 0;
            int blue = 0;
            for (int i = -RADIUS; i <= RADIUS; ++i) {
                int argb = in[inPos + FloatUtils.clamp(i, 0, maxX)];
                red += (argb & RED_MASK) >> RED_MASK_SHIFT;
                green += (argb & GREEN_MASK) >> GREEN_MASK_SHIFT;
                blue += argb & BLUE_MASK;
            }
            // Compute the alpha value.
            int alpha = (y < startFadeX) ? 0xff : ((height - y - 1) * MAX_COLOR_VALUE / (height - startFadeX));
            // Compute output values for the row.
            int outPos = y;
            for (int x = 0; x != width; ++x) { // CR: x < width
                // Output the current pixel.
                out[outPos] = (alpha << 24) | (KERNEL_NORM[red] << RED_MASK_SHIFT) | (KERNEL_NORM[green] << GREEN_MASK_SHIFT)
                        | KERNEL_NORM[blue];
                // Slide to the next pixel, adding the new rightmost pixel and
                // subtracting the former leftmost.
                int prevX = FloatUtils.clamp(x - RADIUS, 0, maxX);
                int nextX = FloatUtils.clamp(x + RADIUS + 1, 0, maxX);
                int prevArgb = in[inPos + prevX];
                int nextArgb = in[inPos + nextX];
                red += ((nextArgb & RED_MASK) - (prevArgb & RED_MASK)) >> RED_MASK_SHIFT;
                green += ((nextArgb & GREEN_MASK) - (prevArgb & GREEN_MASK)) >> GREEN_MASK_SHIFT;
                blue += (nextArgb & BLUE_MASK) - (prevArgb & BLUE_MASK);
                outPos += height;
            }
            inPos += width;
        }
    }
}
