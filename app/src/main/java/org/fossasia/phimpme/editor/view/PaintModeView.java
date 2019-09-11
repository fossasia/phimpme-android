package org.fossasia.phimpme.editor.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;

/** Created by panyi on 17/2/11. */
public class PaintModeView extends View {
  private Paint mPaint;

  private int mStokeColor;
  private float mStokeWidth = -1;

  private float mRadius;

  public PaintModeView(Context context) {
    super(context);
    initView(context);
  }

  public PaintModeView(Context context, AttributeSet attrs) {
    super(context, attrs);
    initView(context);
  }

  public PaintModeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initView(context);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public PaintModeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initView(context);
  }

  protected void initView(Context context) {
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setColor(Color.RED);

    // mStokeWidth = 10;
    // mStokeColor = Color.RED;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    mPaint.setColor(mStokeColor);
    mRadius = mStokeWidth;

    canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, mRadius, mPaint);
  }

  public void setPaintStrokeColor(final int newColor) {
    this.mStokeColor = newColor;
    this.invalidate();
  }

  public void setPaintStrokeWidth(final float width) {
    this.mStokeWidth = width;
    this.invalidate();
  }

  public float getStokenWidth() {
    if (mStokeWidth < 0) {
      mStokeWidth = getMeasuredHeight();
    }
    return mStokeWidth;
  }

  public int getStokenColor() {
    return mStokeColor;
  }
}
