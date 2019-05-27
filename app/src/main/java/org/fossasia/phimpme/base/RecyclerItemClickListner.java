package org.fossasia.phimpme.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListner implements RecyclerView.OnItemTouchListener {

  protected OnItemClickListener mListener;

  private GestureDetector mGestureDetector;

  @Nullable private View mChildView;

  private int mChildViewPosition;

  public RecyclerItemClickListner(Context context, OnItemClickListener listener) {
    this.mGestureDetector = new GestureDetector(context, new GestureListener());
    this.mListener = listener;
  }

  @Override
  public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
    mChildView = view.findChildViewUnder(event.getX(), event.getY());
    mChildViewPosition = view.getChildAdapterPosition(mChildView);

    return mChildView != null && mGestureDetector.onTouchEvent(event);
  }

  @Override
  public void onTouchEvent(RecyclerView view, MotionEvent event) {
    // Not needed.
  }

  @Override
  public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    // Not needed.
  }

  /** A click mListener for items. */
  public interface OnItemClickListener {

    /**
     * Called when an item is clicked.
     *
     * @param childView View of the item that was clicked.
     * @param position Position of the item that was clicked.
     */
    void onItemClick(View childView, int position);

    /**
     * Called when an item is long pressed.
     *
     * @param childView View of the item that was long pressed.
     * @param position Position of the item that was long pressed.
     */
    void onItemLongPress(View childView, int position);
  }

  protected class GestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
      if (mChildView != null) {
        mListener.onItemClick(mChildView, mChildViewPosition);
      }

      return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
      if (mChildView != null) {
        mListener.onItemLongPress(mChildView, mChildViewPosition);
      }
    }

    @Override
    public boolean onDown(MotionEvent event) {
      // Best practice to always return true here.
      // http://developer.android.com/training/gestures/detector.html#detect
      return true;
    }
  }
}
