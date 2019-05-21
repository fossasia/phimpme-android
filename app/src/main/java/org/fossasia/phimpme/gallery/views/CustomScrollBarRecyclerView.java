package org.fossasia.phimpme.gallery.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/** Created by dhanraj on 25/1/18. */
public class CustomScrollBarRecyclerView extends RecyclerView {
  private int scrollBarColor = Color.RED;

  public CustomScrollBarRecyclerView(Context context) {
    super(context);
  }

  public CustomScrollBarRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomScrollBarRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public void setScrollBarColor(@ColorInt int scrollBarColor) {
    this.scrollBarColor = scrollBarColor;
  }

  /** Called by Android {@link android.view.View#onDrawScrollBars(Canvas)} */
  protected void onDrawHorizontalScrollBar(
      Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
    scrollBar.setColorFilter(scrollBarColor, PorterDuff.Mode.SRC_ATOP);
    scrollBar.setBounds(l, t, r, b);
    scrollBar.draw(canvas);
  }

  /** Called by Android {@link android.view.View#onDrawScrollBars(Canvas)} */
  protected void onDrawVerticalScrollBar(
      Canvas canvas, Drawable scrollBar, int l, int t, int r, int b) {
    scrollBar.setColorFilter(scrollBarColor, PorterDuff.Mode.SRC_ATOP);
    scrollBar.setBounds(l, t, r, b);
    scrollBar.draw(canvas);
  }

  @Override
  public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
    boolean returnValue;
    returnValue = super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    if (offsetInWindow[1] != 0) {
      offsetInWindow[1] = 0;
    }
    return returnValue;
  }
}
