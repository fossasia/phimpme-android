package org.fossasia.phimpme.editor.view.imagezoom.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

/**
 * Fast bitmap drawable. Does not support states. it only
 * support alpha and colormatrix
 * @author alessandro
 *
 */
public class FastBitmapDrawable extends Drawable implements IBitmapDrawable {

	protected Bitmap mBitmap;
	protected Paint mPaint;

	public FastBitmapDrawable( Bitmap b ) {
		mBitmap = b;
		mPaint = new Paint();
		mPaint.setDither( true );
		mPaint.setFilterBitmap( true );
	}

	@Override
	public void draw( Canvas canvas ) {
		canvas.drawBitmap( mBitmap, 0.0f, 0.0f, mPaint );
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha( int alpha ) {
		mPaint.setAlpha( alpha );
	}

	@Override
	public void setColorFilter( ColorFilter cf ) {
		mPaint.setColorFilter( cf );
	}

	@Override
	public int getIntrinsicWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getIntrinsicHeight() {
		return mBitmap.getHeight();
	}

	@Override
	public int getMinimumWidth() {
		return mBitmap.getWidth();
	}

	@Override
	public int getMinimumHeight() {
		return mBitmap.getHeight();
	}

	@Override
	public Bitmap getBitmap() {
		return mBitmap;
	}
}