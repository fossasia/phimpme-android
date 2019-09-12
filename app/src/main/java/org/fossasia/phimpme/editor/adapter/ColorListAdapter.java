package org.fossasia.phimpme.editor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.fragment.PaintFragment;

/**
 * Color list Adapter
 *
 * @author panyi
 */
public class ColorListAdapter extends RecyclerView.Adapter<ViewHolder> {
  public static final int TYPE_COLOR = 1;
  public static final int TYPE_MORE = 2;

  public interface IColorListAction {
    void onColorSelected(final int position, final int color);

    void onMoreSelected(final int position);
  }

  private PaintFragment mContext;
  private int[] colorsData;

  private IColorListAction mCallback;

  public ColorListAdapter(PaintFragment frg, int[] colors, IColorListAction action) {
    super();
    this.mContext = frg;
    this.colorsData = colors;
    this.mCallback = action;
  }

  public class ColorViewHolder extends ViewHolder {
    View colorPanelView;

    public ColorViewHolder(View itemView) {
      super(itemView);
      this.colorPanelView = itemView.findViewById(R.id.color_panel_view);
    }
  }

  public class MoreViewHolder extends ViewHolder {
    View moreBtn;

    public MoreViewHolder(View itemView) {
      super(itemView);
      this.moreBtn = itemView.findViewById(R.id.color_panel_more);
    }
  }

  @Override
  public int getItemCount() {
    return colorsData.length + 1;
  }

  @Override
  public int getItemViewType(int position) {
    return colorsData.length == position ? TYPE_MORE : TYPE_COLOR;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = null;
    ViewHolder viewHolder = null;
    if (viewType == TYPE_COLOR) {
      v =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.view_color_panel, parent, false);
      viewHolder = new ColorViewHolder(v);
    } else if (viewType == TYPE_MORE) {
      v =
          LayoutInflater.from(parent.getContext())
              .inflate(R.layout.view_color_more_panel, parent, false);
      viewHolder = new MoreViewHolder(v);
    }

    return viewHolder;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    int type = getItemViewType(position);
    if (type == TYPE_COLOR) {
      onBindColorViewHolder((ColorViewHolder) holder, position);
    } else if (type == TYPE_MORE) {
      onBindColorMoreViewHolder((MoreViewHolder) holder, position);
    }
  }

  private void onBindColorViewHolder(final ColorViewHolder holder, final int position) {
    holder.colorPanelView.setBackgroundColor(colorsData[position]);
    holder.colorPanelView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mCallback != null) {
              mCallback.onColorSelected(position, colorsData[position]);
            }
          }
        });
  }

  private void onBindColorMoreViewHolder(final MoreViewHolder holder, final int position) {
    holder.moreBtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (mCallback != null) {
              mCallback.onMoreSelected(position);
            }
          }
        });
  }
}
