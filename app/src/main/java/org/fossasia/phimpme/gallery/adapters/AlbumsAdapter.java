package org.fossasia.phimpme.gallery.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;
import java.util.ArrayList;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.SharedMediaActivity;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.util.ColorPalette;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.jetbrains.annotations.NotNull;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.ViewHolder> {

  private ArrayList<Album> albums;

  private View.OnClickListener mOnClickListener;
  private View.OnLongClickListener mOnLongClickListener;
  private ThemeHelper theme;
  private BitmapDrawable placeholder;
  Context context;

  public AlbumsAdapter(ArrayList<Album> ph, Context context) {
    albums = ph;
    theme = new ThemeHelper(context);
    this.context = context;
    updateTheme();
  }

  public void updateTheme() {
    theme.updateTheme();
    Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ic_error);
    placeholder = (BitmapDrawable) drawable;
  }

  @NotNull
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_album, parent, false);
    v.setOnClickListener(mOnClickListener);
    v.setOnLongClickListener(mOnLongClickListener);
    return new ViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NotNull final AlbumsAdapter.ViewHolder holder, int position) {
    Album a = SharedMediaActivity.getAlbums().dispAlbums.get(position);
    Media media = a.getCoverAlbum();

    if (a.getPath().contains(Environment.getExternalStorageDirectory().getPath())) {
      holder.ivStorage.setVisibility(View.VISIBLE);
    } else {
      holder.ivStorage.setImageResource(
          theme.getBaseTheme() == ThemeHelper.LIGHT_THEME
              ? R.drawable.ic_sd_storage_black_24dp
              : R.drawable.ic_sd_storage_white_24dp);
      holder.ivStorage.setVisibility(View.VISIBLE);
    }

    if (a.isPinned() && (theme.getBaseTheme() == ThemeHelper.LIGHT_THEME)) {
      holder.ivPin.setVisibility(View.VISIBLE);
      holder.ivPin.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pin_black));
    } else if (a.isPinned()
        && (theme.getBaseTheme() == ThemeHelper.AMOLED_THEME
            || theme.getBaseTheme() == ThemeHelper.DARK_THEME)) {
      holder.ivPin.setVisibility(View.VISIBLE);
      holder.ivPin.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pin_white));
    } else holder.ivPin.setVisibility(View.GONE);

    Glide.with(holder.ivAlbumPreview.getContext())
        .asBitmap()
        .load(media.getPath())
        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        .priority(Priority.HIGH)
        .signature(media.getSignature())
        .centerCrop()
        .error(R.drawable.ic_error)
        .placeholder(placeholder)
        .transition(new GenericTransitionOptions<>().transition(R.anim.fade_in))
        .listener(
            new RequestListener<Bitmap>() {
              @Override
              public boolean onLoadFailed(
                  @Nullable GlideException e,
                  Object model,
                  Target<Bitmap> target,
                  boolean isFirstResource) {
                PreferenceUtil SP = PreferenceUtil.getInstance(context);
                SP.putBoolean(
                    holder
                        .ivAlbumPreview
                        .getContext()
                        .getString(R.string.preference_use_alternative_provider),
                    true);
                return false;
              }

              @Override
              public boolean onResourceReady(
                  Bitmap resource,
                  Object model,
                  Target<Bitmap> target,
                  DataSource dataSource,
                  boolean isFirstResource) {
                return false;
              }
            })
        .into(holder.ivAlbumPreview);

    holder.tvAlbumName.setTag(a);

    String hexPrimaryColor = String.format("#%06X", (0xFFFFFF & theme.getPrimaryColor()));

    String nameTextColor;
    String countTextColor;
    if (theme.getBaseTheme() == ThemeHelper.LIGHT_THEME) nameTextColor = "#2b2b2b";
    else {
      nameTextColor = "#FAFAFA";
    }
    countTextColor = "#B4BAC0";

    if (a.isSelected()) {
      holder.ivSelectedIcon.setColor(Color.WHITE);
      holder.ivSelectedIcon.setIcon(CommunityMaterial.Icon.cmd_check_circle);
      holder.tvAlbumName.setBackgroundColor(Color.parseColor(hexPrimaryColor));
      holder.tvPhotosCount.setBackgroundColor(Color.parseColor(hexPrimaryColor));
      holder.ivPin.setBackgroundColor(Color.parseColor(hexPrimaryColor));
      holder.ivAlbumPreview.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
      holder.ivSelectedIcon.setVisibility(View.VISIBLE);
      if (theme.getBaseTheme() == ThemeHelper.LIGHT_THEME) {
        nameTextColor = "#2b2b2b"; countTextColor = "#B4BAC0";
      }
    } else {
      holder.ivAlbumPreview.clearColorFilter();
      holder.ivSelectedIcon.setVisibility(View.GONE);
      holder.tvAlbumName.setBackgroundColor(theme.getBackgroundColor());
      holder.tvPhotosCount.setBackgroundColor(theme.getBackgroundColor());
      holder.ivPin.setBackgroundColor(
          ColorPalette.getTransparentColor(theme.getBackgroundColor(), 200));
    }
    holder.tvAlbumName.setTextColor(Color.parseColor(nameTextColor));
    holder.tvPhotosCount.setTextColor(Color.parseColor(countTextColor));

    holder.tvAlbumName.setText(a.getName());
    holder.tvPhotosCount.setText(
        a.getCount() + " " ;
  }

  public void setOnClickListener(View.OnClickListener lis) {
    mOnClickListener = lis;
  }

  public void setOnLongClickListener(View.OnLongClickListener lis) {
    mOnLongClickListener = lis;
  }

  public void swapDataSet(ArrayList<Album> asd) {
    if (SharedMediaActivity.getAlbums().dispAlbums.equals(asd)) return;
    SharedMediaActivity.getAlbums().dispAlbums = asd;
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return SharedMediaActivity.getAlbums().dispAlbums.size();
  }

  static class ViewHolder extends RecyclerView.ViewHolder {
    private ImageView ivAlbumPreview;
    private IconicsImageView ivSelectedIcon;
    private TextView tvAlbumName, tvPhotosCount;
    private ImageView ivPin;
    private ImageView ivStorage;

    ViewHolder(View itemView) {
      super(itemView);
      ivAlbumPreview = itemView.findViewById(R.id.iv_album_preview);
      ivSelectedIcon = itemView.findViewById(R.id.selected_icon);
      tvAlbumName = itemView.findViewById(R.id.tv_album_name);
      tvPhotosCount = itemView.findViewById(R.id.tv_album_photos_count);
      ivPin = itemView.findViewById(R.id.iv_pin);
      ivStorage = itemView.findViewById(R.id.iv_storage_icon);
    }
  }
}
