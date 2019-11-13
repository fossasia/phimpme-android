package org.fossasia.phimpme.gallery.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.core.widget.NestedScrollView;

public class CustomNestedView extends NestedScrollView {
  private boolean enableScrolling = true;

  public CustomNestedView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public CustomNestedView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (scrollingEnabled()) {
      return super.onInterceptTouchEvent(ev);
    } else {
      return false;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    if (scrollingEnabled()) {
      return super.onTouchEvent(ev);
    } else {
      return false;
    }
  }

  private boolean scrollingEnabled() {
    return enableScrolling;
  }

  public void setScrolling(boolean enableScrolling) {
    this.enableScrolling = enableScrolling;
  }
}
