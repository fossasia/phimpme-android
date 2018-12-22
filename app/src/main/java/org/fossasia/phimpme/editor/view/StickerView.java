package org.fossasia.phimpme.editor.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouch;

import java.util.LinkedHashMap;

/**
 * 贴图操作控件
 *
 * @author panyi
 */
public class StickerView extends View {
    private static int STATUS_IDLE = 0;
    private static int STATUS_MOVE = 1;// Mobile status
    private static int STATUS_DELETE = 2;// Delete status
    private static int STATUS_ROTATE = 3;// Pictures state of rotation

    private int imageCount;// The number of photos have been added
    private Context mContext;
    private int currentStatus;//Current state
    private StickerItem currentItem;// Current map data manipulation
    private float oldx, oldy;

    private Paint rectPaint = new Paint();

    public Bitmap mainBitmap;
    public ImageViewTouch mainImage;
    private float leftX, rightX, topY, bottomY;

    private LinkedHashMap<Integer, StickerItem> bank = new LinkedHashMap<Integer, StickerItem>();//Storing each data map

    public StickerView(Context context) {
        super(context);
        init(context);
    }

    public StickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int displayW = mainBitmap.getWidth() * getMeasuredHeight() / mainBitmap.getHeight();
        int displayH = mainBitmap.getHeight() * getMeasuredWidth() / mainBitmap.getWidth();
        leftX = (mainImage.getMeasuredWidth() - displayW) >> 1;
        rightX = leftX + displayW;
        topY = (mainImage.getMeasuredHeight() - displayH) >> 1;
        bottomY = topY + displayH;
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        currentStatus = STATUS_IDLE;

        rectPaint.setColor(Color.RED);
        rectPaint.setAlpha(100);

    }

    public void addBitImage(final Bitmap addBit) {
        StickerItem item = new StickerItem(this.getContext());
        item.init(addBit, this);
        if (currentItem != null) {
            currentItem.isDrawHelpTool = false;
        }
        bank.put(++imageCount, item);
        this.invalidate();// 重绘视图
    }

    /**
     * Draw customers page
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // System.out.println("on draw!!~");
        for (Integer id : bank.keySet()) {
            StickerItem item = bank.get(id);
            canvas.clipRect(leftX, topY, rightX, bottomY);
            item.draw(canvas);
        }// end for each
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);// Whether passed down event flag to true consumption

        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                int deleteId = -1;
                for (Integer id : bank.keySet()) {
                    StickerItem item = bank.get(id);
                    if (item.detectDeleteRect.contains(x, y)) {// Delete mode
                        // ret = true;
                        deleteId = id;
                        currentStatus = STATUS_DELETE;
                    } else if (item.detectRotateRect.contains(x, y)) {//Click the Rotate button
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_ROTATE;
                        oldx = x;
                        oldy = y;
                    } else if (item.dstRect.contains(x, y)) {//Mobile mode
                        // Selected one map
                        ret = true;
                        if (currentItem != null) {
                            currentItem.isDrawHelpTool = false;
                        }
                        currentItem = item;
                        currentItem.isDrawHelpTool = true;
                        currentStatus = STATUS_MOVE;
                        oldx = x;
                        oldy = y;
                    }
                }

                if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {// No map is selected
                    currentItem.isDrawHelpTool = false;
                    currentItem = null;
                    invalidate();
                }

                if (deleteId > 0 && currentStatus == STATUS_DELETE) {// Delete the selected map
                    bank.remove(deleteId);
                    currentStatus = STATUS_IDLE;// Returns to the idle state
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                if (currentStatus == STATUS_MOVE) {//Mobile map
                    float dx = x - oldx;
                    float dy = y - oldy;
                    if (currentItem != null) {
                        currentItem.updatePos(dx, dy);
                        invalidate();
                    }
                    oldx = x;
                    oldy = y;
                } else if (currentStatus == STATUS_ROTATE) {// Rotating zoom image manipulation
                    float dx = x - oldx;
                    float dy = y - oldy;
                    if (currentItem != null) {
                        currentItem.updateRotateAndScale(oldx, oldy, dx, dy);// Rotation
                        invalidate();
                    }
                    oldx = x;
                    oldy = y;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                ret = false;
                currentStatus = STATUS_IDLE;
                break;
        }
        return ret;
    }

    public LinkedHashMap<Integer, StickerItem> getBank() {
        return bank;
    }

    public void clear() {
        bank.clear();
        this.invalidate();
    }
}
