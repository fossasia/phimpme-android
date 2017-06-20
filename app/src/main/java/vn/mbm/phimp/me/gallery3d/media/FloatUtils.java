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

/**
 * A static class for some useful operations on Floats and Vectors
 */

public class FloatUtils {
    private static final float ANIMATION_SPEED = 4.0f;

    /**
     * This function animates a float value to another float value
     * 
     * @param prevVal
     *            : The previous value (or the animated value)
     * @param targetVal
     *            : The target value
     * @param timeElapsed
     *            Time elapsed since the last time this function was called
     * @return The new animated value that is closer to the target value and
     *         clamped to the target value
     */
    public static final float animate(float prevVal, float targetVal, float timeElapsed) {
        timeElapsed = timeElapsed * ANIMATION_SPEED;
        return animateAfterFactoringSpeed(prevVal, targetVal, timeElapsed);
    }
    
    public static final float animateWithMaxSpeed(float prevVal, float targetVal, float timeElapsed, float maxSpeed) {
        float newTargetVal = targetVal;
        float delta = newTargetVal - prevVal;
        if (Math.abs(delta) > maxSpeed) {
            newTargetVal = prevVal + (Math.signum(delta) * maxSpeed);
        }
        timeElapsed = timeElapsed * ANIMATION_SPEED;
        return animateAfterFactoringSpeed(prevVal, newTargetVal, timeElapsed);
    }

    /**
     * This function animates a Tuple3f value to another Tuple3f value
     * 
     * @param animVal
     *            : The animating Tuple
     * @param targetVal
     *            : The target value for the Tuple
     * @param timeElapsed
     *            : Time elapsed since the last time this function was called
     */
    public static final void animate(Vector3f animVal, Vector3f targetVal, float timeElapsed) {
        timeElapsed = timeElapsed * ANIMATION_SPEED;
        animVal.x = animateAfterFactoringSpeed(animVal.x, targetVal.x, timeElapsed);
        animVal.y = animateAfterFactoringSpeed(animVal.y, targetVal.y, timeElapsed);
        animVal.z = animateAfterFactoringSpeed(animVal.z, targetVal.z, timeElapsed);
    }

    /**
     * Clamp a float to a lower bound
     * 
     * @param val
     *            : the input float value
     * @param minVal
     *            : the minimum value to use to clamp
     * @return the clamped value
     */
    public static final float clampMin(float val, float minVal) {
        if (val < minVal)
            return minVal; // CR: braces
        else
            return val;
    }

    /**
     * Clamp a float to an upper bound
     * 
     * @param val
     *            : the input float value
     * @param maxVal
     *            : the maximum value to use to clamp
     * @return the clamped value
     */
    public static final float clampMax(float val, float maxVal) {
        if (val > maxVal)
            return maxVal;
        else
            return val;
    }

    // CR: these comments are barely useful. they mostly just fill space. If
    // anything, a one-liner would be sufficient.
    /**
     * Clamp a float to a lower and upper bound
     * 
     * @param val
     *            : the input float value
     * @param minVal
     *            : the minimum value to use to clamp
     * @param maxVal
     *            : the maximum value to use to clamp
     * @return the clamped value
     */
    public static final float clamp(float val, float minVal, float maxVal) {
        if (val < minVal)
            return minVal;
        else if (val > maxVal)
            return maxVal;
        else
            return val;
    }

    /**
     * Clamp an integer to a lower and upper bound
     * 
     * @param val
     *            : the input float value
     * @param minVal
     *            : the minimum value to use to clamp
     * @param maxVal
     *            : the maximum value to use to clamp
     * @return the clamped value
     */
    public static final int clamp(int val, int minVal, int maxVal) {
        if (val < minVal)
            return minVal;
        else if (val > maxVal)
            return maxVal;
        else
            return val;
    }

    /**
     * Function to check whether a point lies inside a rectangle
     * 
     * @param left
     *            : the x coordinate of the left most point
     * @param right
     *            : the x coordinate of the right most point
     * @param top
     *            : the y coordinate of the top most point
     * @param bottom
     *            : the y coordinate of the bottom most point
     * @param posX
     *            : the input point's x coordinate
     * @param posY
     *            : the input point's y coordinate
     * @return true if point is inside the rectangle else return false
     */
    public static final boolean boundsContainsPoint(float left, float right, float top, float bottom, float posX, float posY) {
        // CR: return ... (one statement).
        if (posX < left || posX > right || posY < top || posY > bottom)
            return false;
        else
            return true;
    }

    private static final float animateAfterFactoringSpeed(float prevVal, float targetVal, float timeElapsed) {
        if (prevVal == targetVal)
            return targetVal;
        float newVal = prevVal + ((targetVal - prevVal) * timeElapsed);
        if (Math.abs(newVal - prevVal) < 0.0001f)
            return targetVal;
        if (newVal == prevVal) {
            return targetVal;
        } else { // } else if (...) { ... }; no need for a new level of
                 // indentation.
            if (prevVal > targetVal && newVal < targetVal) {
                return targetVal;
            } else if (prevVal < targetVal && newVal > targetVal) {
                return targetVal;
            } else {
                return newVal;
            }
        }
    }

    public static final float max(float scaleX, float scaleY) {
        if (scaleX > scaleY)
            return scaleX;
        else
            return scaleY;
    }
}
