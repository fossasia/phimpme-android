/*
 * Copyright 2013, Edmodo, Inc. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file, or at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language 
 * governing permissions and limitations under the License. 
 */

package org.fossasia.phimpme.editor.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Utility class for handling all of the Paint used to draw the CropOverlayView.
 */
public class PaintUtil {

	// Private Constants ///////////////////////////////////////////////////////

	private static final int DEFAULT_CORNER_COLOR = Color.WHITE;
	private static final String SEMI_TRANSPARENT = "#AAFFFFFF";
	private static final String DEFAULT_BACKGROUND_COLOR_ID = "#B0000000";
	private static final float DEFAULT_LINE_THICKNESS_DP = 3;
	private static final float DEFAULT_CORNER_THICKNESS_DP = 5;
	private static final float DEFAULT_GUIDELINE_THICKNESS_PX = 1;

	// Public Methods //////////////////////////////////////////////////////////
	/**
	 * Creates the Paint object for drawing the crop window guidelines.
	 * 
	 * @return the new Paint object
	 */
	public static Paint newRotateBottomImagePaint() {

		final Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(3);

		return paint;
	}

	/**
	 * Creates the Paint object for drawing the translucent overlay outside the
	 * crop window.
	 * 
	 * @param context
	 *            the Context
	 * @return the new Paint object
	 */
	public static Paint newBackgroundPaint(Context context) {

		final Paint paint = new Paint();
		paint.setColor(Color.parseColor(DEFAULT_BACKGROUND_COLOR_ID));

		return paint;
	}
}
