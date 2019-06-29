package org.fossasia.phimpme.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouch;

/** Created by panyi on 17/2/11. */
public class CustomPaintView extends View {
  private Paint mPaint;
  public Bitmap mDrawBit;
  private Paint mEraserPaint;

  public static Canvas mPaintCanvas = null;

  private float last_x;
  private float last_y;
  private boolean eraser;

  private int mColor;

  public Bitmap mainBitmap;
  public ImageViewTouch mainImage;
  private float leftX, rightX, topY, bottomY;

  public CustomPaintView(Context context) {
    super(context);
    init(context);
  }

  public CustomPaintView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public CustomPaintView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    if (mainBitmap == null) {
      if (this.getParent() != null) {
        ((ViewGroup) this.getParent()).removeView(this);
      }
      return;
    }
    int displayW = mainBitmap.getWidth() * getMeasuredHeight() / mainBitmap.getHeight();
    int displayH = mainBitmap.getHeight() * getMeasuredWidth() / mainBitmap.getWidth();
    leftX = (mainImage.getMeasuredWidth() - displayW) >> 1;
    rightX = leftX + displayW;
    topY = (mainImage.getMeasuredHeight() - displayH) >> 1;
    bottomY = topY + displayH;
    if (mDrawBit == null) {
      generatorBit();
    }
  }

  private void generatorBit() {
    mDrawBit =
        Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
    mPaintCanvas = new Canvas(mDrawBit);
  }

  private void init(Context context) {
    mPaint = new Paint();
    mPaint.setAntiAlias(true);

    mPaint.setColor(Color.RED);
    mPaint.setStrokeJoin(Paint.Join.ROUND);
    mPaint.setStrokeCap(Paint.Cap.ROUND);

    mEraserPaint = new Paint();
    mEraserPaint.setAlpha(0);
    mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    mEraserPaint.setAntiAlias(true);
    mEraserPaint.setDither(true);
    mEraserPaint.setStyle(Paint.Style.STROKE);
    mEraserPaint.setStrokeJoin(Paint.Join.ROUND);
    mEraserPaint.setStrokeCap(Paint.Cap.ROUND);
    mEraserPaint.setStrokeWidth(40);
  }

  public void setColor(int color) {
    this.mColor = color;
    this.mPaint.setColor(mColor);
  }

  public void setWidth(float width) {
    this.mPaint.setStrokeWidth(width);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    if (mDrawBit != null) {
      canvas.clipRect(leftX, topY, rightX, bottomY);
      canvas.drawBitmap(mDrawBit, 0, 0, null);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    boolean ret = super.onTouchEvent(event);
    float x = event.getX();
    float y = event.getY();

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        ret = true;
        last_x = x;
        last_y = y;
        break;
      case MotionEvent.ACTION_MOVE:
        ret = true;
        mPaintCanvas.drawLine(last_x, last_y, x, y, eraser ? mEraserPaint : mPaint);
        last_x = x;
        last_y = y;
        this.postInvalidate();
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        ret = false;
        break;
    }
    return ret;
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (mDrawBit != null && !mDrawBit.isRecycled()) {
      mDrawBit.recycle();
    }
  }

  public void setEraser(boolean eraser) {
    this.eraser = eraser;
    mPaint.setColor(eraser ? Color.TRANSPARENT : mColor);
  }

  public Bitmap getPaintBit() {
    return mDrawBit;
  }

  public void reset() {
    if (mDrawBit != null && !mDrawBit.isRecycled()) {
      mDrawBit.recycle();
    }

    generatorBit();
  }
}
