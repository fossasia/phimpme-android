package org.fossasia.phimpme.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.utils.PaintUtil;

/**
 * Cut Pictures
 *
 * @author Pan Yi
 */
public class CropImageView extends View {
  private static int STATUS_IDLE = 1; // Idle
  private static int STATUS_MOVE = 2; // Mobile status
  private static int STATUS_SCALE = 3; // Zoom state

  private int CIRCLE_WIDTH = 46;
  private Context mContext;
  private float oldx, oldy;
  private int status = STATUS_IDLE;
  private int selectedControllerCicle;
  private RectF backUpRect = new RectF(); // ON
  private RectF backLeftRect = new RectF(); // left
  private RectF backRightRect = new RectF(); // right
  private RectF backDownRect = new RectF(); // under

  private RectF cropRect = new RectF(); // Cut a rectangle

  private Paint mBackgroundPaint; // background Paint
  private Bitmap circleBit;
  private Rect circleRect = new Rect();
  private RectF leftTopCircleRect;
  private RectF rightTopCircleRect;
  private RectF leftBottomRect;
  private RectF rightBottomRect;

  private RectF imageRect = new RectF(); // Image storage location information
  private RectF tempRect = new RectF(); // Temporary Storage of rectangular data

  private float ratio = -1; // Scaling cut

  public CropImageView(Context context) {
    super(context);
    init(context);
  }

  public CropImageView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    mContext = context;
    mBackgroundPaint = PaintUtil.newBackgroundPaint(context);
    circleBit = BitmapFactory.decodeResource(context.getResources(), R.drawable.sticker_rotate);
    circleRect.set(0, 0, circleBit.getWidth(), circleBit.getHeight());
    leftTopCircleRect = new RectF(0, 0, CIRCLE_WIDTH, CIRCLE_WIDTH);
    rightTopCircleRect = new RectF(leftTopCircleRect);
    leftBottomRect = new RectF(leftTopCircleRect);
    rightBottomRect = new RectF(leftTopCircleRect);
  }

  /**
   * Reset clipping plane
   *
   * @param rect
   */
  public void setCropRect(RectF rect) {
    imageRect.set(rect);
    cropRect.set(rect);
    scaleRect(cropRect, 0.5f);
    invalidate();
  }

  public void setRatioCropRect(RectF rect, float r) {
    this.ratio = r;
    if (r < 0) {
      setCropRect(rect);
      return;
    }

    imageRect.set(rect);
    cropRect.set(rect);
    // setCropRect(rect);
    // Adjustment Rect

    float h, w;
    if (cropRect.width() >= cropRect.height()) { // w>=h
      h = cropRect.height() / 2;
      w = this.ratio * h;
    } else { // w<h
      w = rect.width() / 2;
      h = w / this.ratio;
    } // end if
    float scaleX = w / cropRect.width();
    float scaleY = h / cropRect.height();
    scaleRect(cropRect, scaleX, scaleY);
    invalidate();
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);

    int w = getWidth();
    int h = getHeight();
    if (w <= 0 || h <= 0) return;

    // Draw a black background
    backUpRect.set(0, 0, w, cropRect.top);
    backLeftRect.set(0, cropRect.top, cropRect.left, cropRect.bottom);
    backRightRect.set(cropRect.right, cropRect.top, w, cropRect.bottom);
    backDownRect.set(0, cropRect.bottom, w, h);

    canvas.drawRect(backUpRect, mBackgroundPaint);
    canvas.drawRect(backLeftRect, mBackgroundPaint);
    canvas.drawRect(backRightRect, mBackgroundPaint);
    canvas.drawRect(backDownRect, mBackgroundPaint);

    // Draw four control points
    int radius = CIRCLE_WIDTH >> 1;
    leftTopCircleRect.set(
        cropRect.left - radius,
        cropRect.top - radius,
        cropRect.left + radius,
        cropRect.top + radius);
    rightTopCircleRect.set(
        cropRect.right - radius,
        cropRect.top - radius,
        cropRect.right + radius,
        cropRect.top + radius);
    leftBottomRect.set(
        cropRect.left - radius,
        cropRect.bottom - radius,
        cropRect.left + radius,
        cropRect.bottom + radius);
    rightBottomRect.set(
        cropRect.right - radius,
        cropRect.bottom - radius,
        cropRect.right + radius,
        cropRect.bottom + radius);

    canvas.drawBitmap(circleBit, circleRect, leftTopCircleRect, null);
    canvas.drawBitmap(circleBit, circleRect, rightTopCircleRect, null);
    canvas.drawBitmap(circleBit, circleRect, leftBottomRect, null);
    canvas.drawBitmap(circleBit, circleRect, rightBottomRect, null);
  }

  /** 触摸事件处理 */
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    boolean ret = super.onTouchEvent(event); // Whether the event flag down passing true to consume
    int action = event.getAction();
    float x = event.getX();
    float y = event.getY();
    switch (action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN:
        int selectCircle = isSeletedControllerCircle(x, y);
        if (selectCircle > 0) { // Select Control Points
          ret = true;
          selectedControllerCicle = selectCircle; // Select the record number of control points
          status = STATUS_SCALE; // Zoom into the state
        } else if (cropRect.contains(x, y)) { // Select the internal zoom box
          ret = true;
          status = STATUS_MOVE; // Moving into the state
        } else { // no choice

        } // end if
        break;
      case MotionEvent.ACTION_MOVE:
        if (status == STATUS_SCALE) { // Zoom control
          scaleCropController(x, y);
        } else if (status == STATUS_MOVE) { // Movement control
          translateCrop(x - oldx, y - oldy);
        }
        break;
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP:
        status = STATUS_IDLE; // Return to the idle state
        break;
    }

    // One action point on record
    oldx = x;
    oldy = y;

    return ret;
  }

  /**
   * Moving the shearing block
   *
   * @param dx
   * @param dy
   */
  private void translateCrop(float dx, float dy) {
    tempRect.set(cropRect); // Storing the original data，In order to restore

    translateRect(cropRect, dx, dy);
    // Boundary determination algorithm optimization
    float mdLeft = imageRect.left - cropRect.left;
    if (mdLeft > 0) {
      translateRect(cropRect, mdLeft, 0);
    }
    float mdRight = imageRect.right - cropRect.right;
    if (mdRight < 0) {
      translateRect(cropRect, mdRight, 0);
    }
    float mdTop = imageRect.top - cropRect.top;
    if (mdTop > 0) {
      translateRect(cropRect, 0, mdTop);
    }
    float mdBottom = imageRect.bottom - cropRect.bottom;
    if (mdBottom < 0) {
      translateRect(cropRect, 0, mdBottom);
    }

    this.invalidate();
  }

  /**
   * Move the rectangle
   *
   * @param rect
   * @param dx
   * @param dy
   */
  private static final void translateRect(RectF rect, float dx, float dy) {
    rect.left += dx;
    rect.right += dx;
    rect.top += dy;
    rect.bottom += dy;
  }

  /**
   * Control the zoom operation control point
   *
   * @param x
   * @param y
   */
  private void scaleCropController(float x, float y) {
    tempRect.set(cropRect); // Storing the original data，In order to restore
    switch (selectedControllerCicle) {
      case 1: // Upper left corner of the control point
        cropRect.left = x;
        cropRect.top = y;
        break;
      case 2:
        cropRect.right = x;
        cropRect.top = y;
        break;
      case 3:
        cropRect.left = x;
        cropRect.bottom = y;
        break;
      case 4:
        cropRect.right = x;
        cropRect.bottom = y;
        break;
    }

    if (ratio < 0) { // Arbitrary scaling ratio
      // Boundary condition detection
      validateCropRect();
      invalidate();
    } else {
      // Update clipping rectangle length and width
      // Determining the invariant point
      switch (selectedControllerCicle) {
        case 1: // Upper left corner of the control point
        case 2:
          cropRect.bottom = (cropRect.right - cropRect.left) / this.ratio + cropRect.top;
          break;
        case 3:
        case 4:
          cropRect.top = cropRect.bottom - (cropRect.right - cropRect.left) / this.ratio;
          break;
      }

      // validateCropRect();
      if (cropRect.left < imageRect.left
          || cropRect.right > imageRect.right
          || cropRect.top < imageRect.top
          || cropRect.bottom > imageRect.bottom
          || cropRect.width() < CIRCLE_WIDTH
          || cropRect.height() < CIRCLE_WIDTH) {
        cropRect.set(tempRect);
      }
      invalidate();
    }
  }

  /** Boundary condition detection */
  private void validateCropRect() {
    if (cropRect.width() < CIRCLE_WIDTH) {
      cropRect.left = tempRect.left;
      cropRect.right = tempRect.right;
    }
    if (cropRect.height() < CIRCLE_WIDTH) {
      cropRect.top = tempRect.top;
      cropRect.bottom = tempRect.bottom;
    }
    if (cropRect.left < imageRect.left) {
      cropRect.left = imageRect.left;
    }
    if (cropRect.right > imageRect.right) {
      cropRect.right = imageRect.right;
    }
    if (cropRect.top < imageRect.top) {
      cropRect.top = imageRect.top;
    }
    if (cropRect.bottom > imageRect.bottom) {
      cropRect.bottom = imageRect.bottom;
    }
  }

  /**
   * Whether the selected control point
   *
   * <p>-1It is not
   *
   * @param x
   * @param y
   * @return
   */
  private int isSeletedControllerCircle(float x, float y) {
    if (leftTopCircleRect.contains(x, y)) // Select the upper left corner
    return 1;
    if (rightTopCircleRect.contains(x, y)) // Select the upper right corner
    return 2;
    if (leftBottomRect.contains(x, y)) // Select the lower left corner
    return 3;
    if (rightBottomRect.contains(x, y)) // Select the lower right corner
    return 4;
    return -1;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }

  /**
   * Back clipping rectangle
   *
   * @return
   */
  public RectF getCropRect() {
    return new RectF(this.cropRect);
  }

  /**
   * Zoom specified rectangle
   *
   * @param rect
   */
  private static void scaleRect(RectF rect, float scaleX, float scaleY) {
    float w = rect.width();
    float h = rect.height();

    float newW = scaleX * w;
    float newH = scaleY * h;

    float dx = (newW - w) / 2;
    float dy = (newH - h) / 2;

    rect.left -= dx;
    rect.top -= dy;
    rect.right += dx;
    rect.bottom += dy;
  }

  /**
   * Zoom specified rectangle
   *
   * @param rect
   * @param scale
   */
  private static void scaleRect(RectF rect, float scale) {
    scaleRect(rect, scale, scale);
  }

  public float getRatio() {
    return ratio;
  }

  public void setRatio(float ratio) {
    this.ratio = ratio;
  }
}
