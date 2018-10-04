package org.fossasia.phimpme.editor.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.utils.RectUtil;

import java.util.Random;

/**
 * @author panyi
 */
public class StickerItem {
    private static final float MIN_SCALE = 0.15f;
    private static final int HELP_BOX_PAD = 25;

    private static final int BUTTON_WIDTH = Constants.STICKER_BTN_HALF_SIZE;

    public Bitmap bitmap;
    public Rect srcRect;// The coordinates of the original picture
    public RectF dstRect;// Draw target coordinates
    private Rect helpToolsRect;
    public RectF deleteRect;// Delete button position
    public RectF rotateRect;// Rotate button position

    RectF helpBox;
    public Matrix matrix;// Change matrix
    private float roatetAngle = 0;
    boolean isDrawHelpTool = false;
    private Paint dstPaint = new Paint();
    private Paint paint = new Paint();
    private Paint helpBoxPaint = new Paint();

    private float initWidth;// When added to the original width of the screen

    private static Bitmap deleteBit;
    private static Bitmap rotateBit;
    private Random random = new Random();

    private Paint greenPaint = new Paint();
    public RectF detectRotateRect;

    public RectF detectDeleteRect;

    public StickerItem(Context context) {

        helpBoxPaint.setColor(Color.BLACK);
        helpBoxPaint.setStyle(Style.STROKE);
        helpBoxPaint.setAntiAlias(true);
        helpBoxPaint.setStrokeWidth(4);

        dstPaint = new Paint();
        dstPaint.setColor(Color.RED);
        dstPaint.setAlpha(120);

        greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);
        greenPaint.setAlpha(120);

        // Import bitmap tool button
        if (deleteBit == null) {
            deleteBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_delete);
        }
        if (rotateBit == null) {
            rotateBit = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.sticker_rotate);
        }
    }

    public void init(Bitmap addBit, View parentView) {
        this.bitmap = addBit;
        this.srcRect = new Rect(0, 0, addBit.getWidth(), addBit.getHeight());
        int bitWidth = Math.min(addBit.getWidth(), parentView.getWidth() >> 1);
        int bitHeight = (int) bitWidth * addBit.getHeight() / addBit.getWidth();
        int left = (random.nextInt(parentView.getWidth()-(int)(parentView.getWidth()*0.4f)));
        int top =  (random.nextInt(parentView.getHeight()-(int)(parentView.getHeight()*0.4f)));
        this.dstRect = new RectF(left, top, left + bitWidth, top + bitHeight);
        this.matrix = new Matrix();
        this.matrix.postTranslate(left, top);
        this.matrix.postScale((float) bitWidth / addBit.getWidth(),
                (float) bitHeight / addBit.getHeight(), this.dstRect.left,
                this.dstRect.top);
        initWidth = this.dstRect.width();// Record original width
        // item.matrix.setScale((float)bitWidth/addBit.getWidth(),
        // (float)bitHeight/addBit.getHeight());
        this.isDrawHelpTool = true;
        this.helpBox = new RectF(this.dstRect);
        updateHelpBoxRect();

        helpToolsRect = new Rect(0, 0, deleteBit.getWidth(),
                deleteBit.getHeight());

        deleteRect = new RectF(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH, helpBox.left + BUTTON_WIDTH, helpBox.top
                + BUTTON_WIDTH);
        rotateRect = new RectF(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH, helpBox.right + BUTTON_WIDTH, helpBox.bottom
                + BUTTON_WIDTH);

        detectRotateRect = new RectF(rotateRect);
        detectDeleteRect = new RectF(deleteRect);
    }

    private void updateHelpBoxRect() {
        this.helpBox.left -= HELP_BOX_PAD;
        this.helpBox.right += HELP_BOX_PAD;
        this.helpBox.top -= HELP_BOX_PAD;
        this.helpBox.bottom += HELP_BOX_PAD;
    }

    /**
     *Location Update
     * @param dx
     * @param dy
     */
    public void updatePos(final float dx, final float dy) {
        this.matrix.postTranslate(dx, dy);

        dstRect.offset(dx, dy);

        // Tools button will move
        helpBox.offset(dx, dy);
        deleteRect.offset(dx, dy);
        rotateRect.offset(dx, dy);

        this.detectRotateRect.offset(dx, dy);
        this.detectDeleteRect.offset(dx, dy);
    }

    /**
     *Rotating zoom Updates
     * @param dx
     * @param dy
     */
    public void updateRotateAndScale(final float oldx, final float oldy,
                                     final float dx, final float dy) {
        float c_x = dstRect.centerX();
        float c_y = dstRect.centerY();

        float x = this.detectRotateRect.centerX();
        float y = this.detectRotateRect.centerY();

        // float x = oldx;
        // float y = oldy;

        float n_x = x + dx;
        float n_y = y + dy;

        float xa = x - c_x;
        float ya = y - c_y;

        float xb = n_x - c_x;
        float yb = n_y - c_y;

        float srcLen = (float) Math.sqrt(xa * xa + ya * ya);
        float curLen = (float) Math.sqrt(xb * xb + yb * yb);

        float scale = curLen / srcLen;// Calculating a zoom ratio

        float newWidth = dstRect.width() * scale;
        if (newWidth / initWidth < MIN_SCALE) {// The minimum scale value to detect
            return;
        }

        this.matrix.postScale(scale, scale, this.dstRect.centerX(),
                this.dstRect.centerY());// Deposit scale matrix
        RectUtil.scaleRect(this.dstRect, scale);// Zoom destination rectangle

        // Toolbox recalculated coordinates
        helpBox.set(dstRect);
        updateHelpBoxRect();// recalculate
        rotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        deleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);

        detectRotateRect.offsetTo(helpBox.right - BUTTON_WIDTH, helpBox.bottom
                - BUTTON_WIDTH);
        detectDeleteRect.offsetTo(helpBox.left - BUTTON_WIDTH, helpBox.top
                - BUTTON_WIDTH);

        double cos = (xa * xb + ya * yb) / (srcLen * curLen);
        if (cos > 1 || cos < -1)
            return;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        float calMatrix = xa * yb - xb * ya;// Determinant calculation to determine the direction of rotation

        int flag = calMatrix > 0 ? 1 : -1;
        angle = flag * angle;

        roatetAngle += angle;
        this.matrix.postRotate(angle, this.dstRect.centerX(),
                this.dstRect.centerY());

        RectUtil.rotateRect(this.detectRotateRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
        RectUtil.rotateRect(this.detectDeleteRect, this.dstRect.centerX(),
                this.dstRect.centerY(), roatetAngle);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(this.bitmap, this.matrix, null);// Maps drawn elements
        // canvas.drawRect(this.dstRect, dstPaint);

        if (this.isDrawHelpTool) {// Line drawing aids
            canvas.save();
            canvas.rotate(roatetAngle, helpBox.centerX(), helpBox.centerY());
            canvas.drawRoundRect(helpBox, 10, 10, helpBoxPaint);
            //Drawing tools button
            canvas.drawBitmap(deleteBit, helpToolsRect, deleteRect, null);
            canvas.drawBitmap(rotateBit, helpToolsRect, rotateRect, null);
            canvas.restore();
            // canvas.drawRect(deleteRect, dstPaint);
            // canvas.drawRect(rotateRect, dstPaint);
            // canvas.drawRect(detectRotateRect, this.greenPaint);
            // canvas.drawRect(detectDeleteRect, this.greenPaint);
        }

        // detectRotateRect
    }
}
