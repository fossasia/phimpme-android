package org.fossasia.phimpme.gallery.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/** Created by mohit on 19/8/17. */
public class PagerRecyclerView extends RecyclerView {

  private int currPosition = -1;

  public PagerRecyclerView(Context context) {
    super(context);
    if (!isInEditMode()) init();
  }

  public PagerRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    if (!isInEditMode()) init();
  }

  public PagerRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    if (!isInEditMode()) init();
  }

  private void init() {
    new PagerSnapHelper().attachToRecyclerView(this);
  }

  public void setOnPageChangeListener(final OnPageChangeListener onPageChangeListener) {
    if (onPageChangeListener == null) return;
    if (currPosition == -1) {
      currPosition =
          ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
      if (currPosition == -1) currPosition = 0;
      addOnScrollListener(
          new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
              super.onScrolled(recyclerView, dx, dy);
              int oldPosition = currPosition;
              int newPosition =
                  ((LinearLayoutManager) getLayoutManager())
                      .findFirstCompletelyVisibleItemPosition();
              if (newPosition != -1) currPosition = newPosition;
              if (currPosition != oldPosition)
                onPageChangeListener.onPageChanged(oldPosition, currPosition);
            }
          });
    }
  }

  public interface OnPageChangeListener {
    void onPageChanged(int oldPosition, int newPosition);
  }
}
