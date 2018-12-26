package org.fossasia.phimpme.gallery.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.SharedMediaActivity;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.util.ColorPalette;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.ThemeHelper;

import java.util.ArrayList;

/**
 * Created by dnld on 1/7/16.
 */
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
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.placeholder);
        placeholder = (BitmapDrawable) drawable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_album, parent, false);
        v.setOnClickListener(mOnClickListener);
        v.setOnLongClickListener(mOnLongClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlbumsAdapter.ViewHolder holder, int position) {
        Album a = SharedMediaActivity.getAlbums().dispAlbums.get(position);
        Media f = a.getCoverAlbum();
      
        if(a.getPath().contains(Environment.getExternalStorageDirectory().getPath())){
            holder.storage.setVisibility(View.INVISIBLE);
        } else {
            holder.storage.setImageResource(theme.getBaseTheme() == ThemeHelper.LIGHT_THEME ? R.drawable.ic_sd_storage_black_24dp : R.drawable.ic_sd_storage_white_24dp);
            holder.storage.setVisibility(View.VISIBLE);
        }

        if (a.isPinned() && (theme.getBaseTheme() == ThemeHelper.LIGHT_THEME)){
            holder.pin.setVisibility(View.VISIBLE);
            holder.pin.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pin_black));
        }
        else if( a.isPinned() && (theme.getBaseTheme() == ThemeHelper.AMOLED_THEME || theme.getBaseTheme() == ThemeHelper.DARK_THEME)) {
            holder.pin.setVisibility(View.VISIBLE);
            holder.pin.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.pin_white));
        }
        else
            holder.pin.setVisibility(View.INVISIBLE);

        Glide.with(holder.picture.getContext())
                .load(f.getPath())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .priority(Priority.HIGH)
                .signature(f.getSignature())
                .centerCrop()
                .error(R.drawable.ic_error)
                .placeholder(placeholder)
                .animate(R.anim.fade_in)
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        PreferenceUtil SP = PreferenceUtil.getInstance(context);
                        SP.putBoolean(holder.picture.getContext().getString(R.string.preference_use_alternative_provider), true);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.picture);

        holder.name.setTag(a);

        String hexPrimaryColor = String.format("#%06X", (0xFFFFFF & theme.getPrimaryColor()));
        String hexAccentColor = String.format("#%06X", (0xFFFFFF & theme.getAccentColor()));

        if (hexAccentColor.equals(hexPrimaryColor)) {
            float[] hsv = new float[3];
            int color = theme.getAccentColor();
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.72f; // value component
            color = Color.HSVToColor(hsv);
            hexAccentColor = String.format("#%06X", (0xFFFFFF & color));
        }

        String textColor = theme.getBaseTheme() != ThemeHelper.LIGHT_THEME ? "#FAFAFA" : "#2b2b2b";

        if (a.isSelected()) {
            holder.selectedIcon.setColor(Color.WHITE);
            holder.selectedIcon.setIcon(CommunityMaterial.Icon.cmd_check);
            holder.layout.setBackgroundColor(Color.parseColor(hexPrimaryColor));
            holder.picture.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
            holder.selectedIcon.setVisibility(View.VISIBLE);
            if (theme.getBaseTheme() == ThemeHelper.LIGHT_THEME) {
                textColor = "#FAFAFA";
                hexAccentColor = "#FAFAFA";
            }
        } else {
            holder.picture.clearColorFilter();
            holder.selectedIcon.setVisibility(View.GONE);
            holder.layout.setBackgroundColor(ColorPalette.getTransparentColor(theme.getBackgroundColor(), 200));
        }

        String albumNameHtml = "<i><font color='" + textColor + "'>" + a.getName() + "</font></i>";
        String albumPhotoCountHtml = "<b><font color='" + hexAccentColor + "'>" + a.getCount() + "</font></b>" + "<font " +
                "color='" + textColor + "'> " + holder.nPhotos.getContext().getString(R.string.media) + "</font>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.name.setText(Html.fromHtml(albumNameHtml, Html.FROM_HTML_MODE_LEGACY));
            holder.nPhotos.setText(Html.fromHtml(albumPhotoCountHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.name.setText(Html.fromHtml(albumNameHtml));
            holder.nPhotos.setText(Html.fromHtml(albumPhotoCountHtml));
        }

        // (a.getImagesCount() == 1 ? c.getString(R.string.singular_photo) : c.getString(R.string.plural_photos))
    }

    public void setOnClickListener(View.OnClickListener lis) {
        mOnClickListener = lis;
    }

    public void setOnLongClickListener(View.OnLongClickListener lis) {
        mOnLongClickListener = lis;
    }

    public void swapDataSet(ArrayList<Album> asd) {
        if (SharedMediaActivity.getAlbums().dispAlbums.equals(asd))
            return;
        SharedMediaActivity.getAlbums().dispAlbums = asd;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return SharedMediaActivity.getAlbums().dispAlbums.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView picture;
        private View layout;
        private IconicsImageView selectedIcon;
        private TextView name, nPhotos;
        private ImageView pin;
        private ImageView storage;

        ViewHolder(View itemView) {
            super(itemView);
            picture = (ImageView) itemView.findViewById(R.id.album_preview);
            selectedIcon = (IconicsImageView) itemView.findViewById(R.id.selected_icon);
            layout = itemView.findViewById(R.id.linear_card_text);
            name = (TextView) itemView.findViewById(R.id.album_name);
            nPhotos = (TextView) itemView.findViewById(R.id.album_photos_count);
            pin = (ImageView) itemView.findViewById(R.id.icon_pinned);
            storage = (ImageView) itemView.findViewById(R.id.storage_icon);
        }
    }
}



