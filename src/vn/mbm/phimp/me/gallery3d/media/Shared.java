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

import android.graphics.Color;
import android.media.ExifInterface;

public final class Shared {
    // Constants.
    public static final int INVALID = -1;
    public static final int INFINITY = Integer.MAX_VALUE;

    public static int argb(float a, float r, float g, float b) {
        return Color.argb((int) (a * 255f), (int) (r * 255f), (int) (g * 255f), (int) (b * 255f));
    }

    public static boolean isPowerOf2(int n) {
        return (n & -n) == n;
    }

    /**
     * @param i
     *            : running variable
     * @return 0, +1, -1, +2, -2, +3, -3 ..
     */
    public static int midPointIterator(int i) {
        if (i != 0) {
            int tick = ((i - 1) / 2) + 1;
            int pass = ((i - 1) % 2 == 0) ? 1 : -1;
            return tick * pass;
        }
        return 0;
    }

    public static int nextPowerOf2(int n) {
        n -= 1;
        n |= n >>> 16;
        n |= n >>> 8;
        n |= n >>> 4;
        n |= n >>> 2;
        n |= n >>> 1;
        return n + 1;
    }

    public static int prevPowerOf2(int n) {
        if (isPowerOf2(n)) {
            return nextPowerOf2(n);
        } else {
            return nextPowerOf2(n) - 1;
        }
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }

    public static long clamp(long value, long min, long max) {
        if (value < min) {
            value = min;
        } else if (value > max) {
            value = max;
        }
        return value;
    }

    public static float scaleToFit(float srcWidth, float srcHeight, float outerWidth, float outerHeight, boolean clipToFit) {
        float scaleX = outerWidth / srcWidth;
        float scaleY = outerHeight / srcHeight;
        return (clipToFit ? scaleX > scaleY : scaleX < scaleY) ? scaleX : scaleY;
    }

    // Returns an angle between 0 and 360 degrees independent of the input
    // angle.
    public static float normalizePositive(float angleToRotate) {
        if (angleToRotate == 0.0f) {
            return 0.0f;
        }
        float nf = (angleToRotate / 360.0f);
        int n = 0;
        if (angleToRotate < 0) {
            n = (int) (nf - 1.0f);
        } else if (angleToRotate > 360) {
            n = (int) (nf);
        }
        angleToRotate -= (n * 360.0f);
        if (angleToRotate == 360.0f) {
            angleToRotate = 0;
        }
        return angleToRotate;
    }

    public static int degreesToExifOrientation(float normalizedAngle) {
        if (normalizedAngle == 0.0f) {
            return ExifInterface.ORIENTATION_NORMAL;
        } else if (normalizedAngle == 90.0f) {
            return ExifInterface.ORIENTATION_ROTATE_90;
        } else if (normalizedAngle == 180.0f) {
            return ExifInterface.ORIENTATION_ROTATE_180;
        } else if (normalizedAngle == 270.0f) {
            return ExifInterface.ORIENTATION_ROTATE_270;
        }
        return ExifInterface.ORIENTATION_NORMAL;
    }

    public static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
}
