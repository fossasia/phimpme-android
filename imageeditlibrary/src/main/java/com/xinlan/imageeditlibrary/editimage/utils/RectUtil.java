package com.xinlan.imageeditlibrary.editimage.utils;

import android.graphics.Rect;
import android.graphics.RectF;

import com.xinlan.imageeditlibrary.editimage.view.TextStickerView;

/**
 * Created by panyi on 2016/6/16.
 */
public class RectUtil {
    /**
     * 缩放指定矩形
     *
     * @param rect
     * @param scale
     */
    public static void scaleRect(RectF rect, float scale) {
        float w = rect.width();
        float h = rect.height();

        float newW = scale * w;
        float newH = scale * h;

        float dx = (newW - w) / 2;
        float dy = (newH - h) / 2;

        rect.left -= dx;
        rect.top -= dy;
        rect.right += dx;
        rect.bottom += dy;
    }

    /**
     * 矩形绕指定点旋转
     *
     * @param rect
     * @param roatetAngle
     */
    public static void rotateRect(RectF rect, float center_x, float center_y,
                                  float roatetAngle) {
        float x = rect.centerX();
        float y = rect.centerY();
        float sinA = (float) Math.sin(Math.toRadians(roatetAngle));
        float cosA = (float) Math.cos(Math.toRadians(roatetAngle));
        float newX = center_x + (x - center_x) * cosA - (y - center_y) * sinA;
        float newY = center_y + (y - center_y) * cosA + (x - center_x) * sinA;

        float dx = newX - x;
        float dy = newY - y;

        rect.offset(dx, dy);
    }

    /**
     * 矩形在Y轴方向上的加法操作
     *
     * @param srcRect
     * @param addRect
     * @param padding
     */
    public static void rectAddV(final RectF srcRect, final RectF addRect, int padding) {
        if (srcRect == null || addRect == null)
            return;

        float left = srcRect.left;
        float top = srcRect.top;
        float right = srcRect.right;
        float bottom = srcRect.bottom;

        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width();
        }

        bottom += padding + addRect.height();

        srcRect.set(left, top, right, bottom);
    }

    /**
     * 矩形在Y轴方向上的加法操作
     *
     * @param srcRect
     * @param addRect
     * @param padding
     */
    public static void rectAddV(final Rect srcRect, final Rect addRect, int padding) {
        if (srcRect == null || addRect == null)
            return;

        int left = srcRect.left;
        int top = srcRect.top;
        int right = srcRect.right;
        int bottom = srcRect.bottom;

        if (srcRect.width() <= addRect.width()) {
            right = left + addRect.width();
        }

        bottom += padding + Math.max(addRect.height(), TextStickerView.CHAR_MIN_HEIGHT);

        srcRect.set(left, top, right, bottom);
    }
}//end class
