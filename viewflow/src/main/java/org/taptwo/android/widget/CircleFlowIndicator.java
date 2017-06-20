/*
 * Copyright (C) 2011 Patrik Åkerfeldt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.taptwo.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import org.taptwo.android.widget.viewflow.R;

/**
 * A FlowIndicator which draws circles (one for each view). 
 * <br/>
 * Available attributes are:<br/>
 * <ul>
 * <li>
 * activeColor: Define the color used to draw the active circle (default to white)
 * </li>
 * <li>
 * inactiveColor: Define the color used to draw the inactive circles (default to 0x44FFFFFF)
 * </li>
 * <li>
 * inactiveType: Define how to draw the inactive circles, either stroke or fill (default to stroke)
 * </li>
 * <li>
 * activeType: Define how to draw the active circle, either stroke or fill (default to fill)
 * </li>
 * <li>
 * fadeOut: Define the time (in ms) until the indicator will fade out (default to 0 = never fade out)
 * </li>
 * <li>
 * radius: Define the circle outer radius (default to 4.0)
 * </li>
 * <li>
 * spacing: Define the circle spacing (default to 4.0)
 * </li>
 * <li>
 * snap: If true, the 'active' indicator snaps from one page to the next; otherwise, it moves smoothly.
 * </li>
 * </ul>
 */
public class CircleFlowIndicator extends View implements FlowIndicator,
		AnimationListener {
	private static final int STYLE_STROKE = 0;
	private static final int STYLE_FILL = 1;

	private float mRadius = 4;
	private float mRadiusInactive = 4;
	private float mRadiusActive = 4;
	private float spacing = 4;
	private int fadeOutTime = 0;
	private final Paint mPaintInactive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint mPaintActive = new Paint(Paint.ANTI_ALIAS_FLAG);
	private ViewFlow viewFlow;
	private int currentScroll = 0;
	private int currentPosition = 0;
	private int flowWidth = 0;
	private FadeTimer timer;
	public AnimationListener animationListener = this;
	private Animation animation;
	private boolean mCentered = false;
	private boolean mSnap = false;

	/**
	 * Default constructor
	 * 
	 * @param context
	 */
	public CircleFlowIndicator(Context context) {
		super(context);
		initColors(0xFFFFFFFF, 0xFFFFFFFF, STYLE_FILL, STYLE_STROKE);
	}

	/**
	 * The contructor used with an inflater
	 * 
	 * @param context
	 * @param attrs
	 */
	public CircleFlowIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Retrieve styles attributs
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.CircleFlowIndicator);

		// Gets the active circle type, defaulting to "fill"
		int activeType = a.getInt(R.styleable.CircleFlowIndicator_activeType,
				STYLE_FILL);
		
		int activeDefaultColor = 0xFFFFFFFF;
		
		// Get a custom active color if there is one
		int activeColor = a
				.getColor(R.styleable.CircleFlowIndicator_activeColor,
						activeDefaultColor);

		// Gets the inactive circle type, defaulting to "stroke"
		int inactiveType = a.getInt(
				R.styleable.CircleFlowIndicator_inactiveType, STYLE_STROKE);

		int inactiveDefaultColor = 0x44FFFFFF;
		// Get a custom inactive color if there is one
		int inactiveColor = a.getColor(
				R.styleable.CircleFlowIndicator_inactiveColor,
				inactiveDefaultColor);

		// Retrieve the radius
		mRadius = a.getDimension(R.styleable.CircleFlowIndicator_radius, 4.0f);
		mRadiusActive = mRadius;
		mRadiusInactive = mRadius;

		// Retrieve the spacing
		spacing = a.getDimension(R.styleable.CircleFlowIndicator_spacing, 4.0f);
		// We want the spacing to be center-to-center
		spacing += 2 * mRadiusActive;
		
		// Retrieve the fade out time
		fadeOutTime = a.getInt(R.styleable.CircleFlowIndicator_fadeOut, 0);
		
		mCentered = a.getBoolean(R.styleable.CircleFlowIndicator_centered, false);

		mSnap = a.getBoolean(R.styleable.CircleFlowIndicator_snap, false);
		
		initColors(activeColor, inactiveColor, activeType, inactiveType);
	}

	private void initColors(int activeColor, int inactiveColor, int activeType,
			int inactiveType) {
		// Select the paint type given the type attr
		switch (inactiveType) {
		case STYLE_FILL:
			mPaintInactive.setStyle(Style.FILL);
			break;
		default:
			mPaintInactive.setStyle(Style.STROKE);
			float strokeWidth = mPaintInactive.getStrokeWidth();
			if (strokeWidth == 0.0f) {
				// It draws in "hairline mode", which is 1 px wide.
				strokeWidth = 1.0f / getResources().getDisplayMetrics().density;
			}
			mRadiusInactive -= strokeWidth / 2.0f;
		}
		mPaintInactive.setColor(inactiveColor);

		// Select the paint type given the type attr
		switch (activeType) {
		case STYLE_STROKE:
			mPaintActive.setStyle(Style.STROKE);
			float strokeWidth = mPaintInactive.getStrokeWidth();
			if (strokeWidth == 0.0f) {
				// It draws in "hairline mode", which is 1 px wide.
				strokeWidth = 1.0f / getResources().getDisplayMetrics().density;
			}
			mRadiusActive -= strokeWidth / 2.0f;
			break;
		default:
			mPaintActive.setStyle(Style.FILL);
		}
		mPaintActive.setColor(activeColor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int count = 3;
		if (viewFlow != null) {
			count = viewFlow.getViewsCount();
		}
		
		//this is the amount the first circle should be offset to make the entire thing centered
		float centeringOffset = 0;
		
		int leftPadding = getPaddingLeft();
		
		// Draw stroked circles
		for (int iLoop = 0; iLoop < count; iLoop++) {
			canvas.drawCircle(leftPadding + mRadius
					+ (iLoop * spacing) + centeringOffset,
					getPaddingTop() + mRadius, mRadiusInactive, mPaintInactive);
		}
		float cx = 0;
		if (mSnap) {
			cx = currentPosition * spacing;
		} else {
			if (flowWidth != 0) {
				// Draw the filled circle according to the current scroll
				cx = (currentScroll * spacing) / flowWidth;
			}
			// else, the flow width hasn't been updated yet. Draw the default position.
		}
		canvas.drawCircle(leftPadding + mRadius + cx+centeringOffset, getPaddingTop()
				+ mRadius, mRadiusActive, mPaintActive);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.taptwo.android.widget.ViewFlow.ViewSwitchListener#onSwitched(android
	 * .view.View, int)
	 */
	@Override
	public void onSwitched(View view, int position) {
		currentPosition = position;
		if (mSnap) {
			setVisibility(View.VISIBLE);
			resetTimer();
			invalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.taptwo.android.widget.FlowIndicator#setViewFlow(org.taptwo.android
	 * .widget.ViewFlow)
	 */
	@Override
	public void setViewFlow(ViewFlow view) {
		resetTimer();
		viewFlow = view;
		flowWidth = viewFlow.getChildWidth();
		invalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.taptwo.android.widget.FlowIndicator#onScrolled(int, int, int,
	 * int)
	 */
	@Override
	public void onScrolled(int h, int v, int oldh, int oldv) {
		currentScroll = h;
		flowWidth = viewFlow.getChildWidth();
		if (!mSnap) {
			setVisibility(View.VISIBLE);
			resetTimer();
			invalidate();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *			  A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// We were told how big to be
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		// Calculate the width according the views count
		else {
			int count = 3;
			if (viewFlow != null) {
				count = viewFlow.getViewsCount();
			}
			// Remember that spacing is centre-to-centre
			result = (int) (getPaddingLeft() + getPaddingRight()
					+ (2 * mRadius) + (count - 1) * spacing);
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *			  A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		// We were told how big to be
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		// Measure the height
		else {
			result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Sets the fill color
	 * 
	 * @param color
	 *			  ARGB value for the text
	 */
	public void setFillColor(int color) {
		mPaintActive.setColor(color);
		invalidate();
	}

	/**
	 * Sets the stroke color
	 * 
	 * @param color
	 *			  ARGB value for the text
	 */
	public void setStrokeColor(int color) {
		mPaintInactive.setColor(color);
		invalidate();
	}

	/**
	 * Resets the fade out timer to 0. Creating a new one if needed
	 */
	private void resetTimer() {
		// Only set the timer if we have a timeout of at least 1 millisecond
		if (fadeOutTime > 0) {
			// Check if we need to create a new timer
			if (timer == null || timer._run == false) {
				// Create and start a new timer
				timer = new FadeTimer();
				timer.execute();
			} else {
				// Reset the current tiemr to 0
				timer.resetTimer();
			}
		}
	}

	/**
	 * Counts from 0 to the fade out time and animates the view away when
	 * reached
	 */
	private class FadeTimer extends AsyncTask<Void, Void, Void> {
		// The current count
		private int timer = 0;
		// If we are inside the timing loop
		private boolean _run = true;

		public void resetTimer() {
			timer = 0;
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			while (_run) {
				try {
					// Wait for a millisecond
					Thread.sleep(1);
					// Increment the timer
					timer++;

					// Check if we've reached the fade out time
					if (timer == fadeOutTime) {
						// Stop running
						_run = false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			animation = AnimationUtils.loadAnimation(getContext(),
					android.R.anim.fade_out);
			animation.setAnimationListener(animationListener);
			startAnimation(animation);
		}
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		setVisibility(View.GONE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}
}
