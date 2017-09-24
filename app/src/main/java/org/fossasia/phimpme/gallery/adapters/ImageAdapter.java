package org.fossasia.phimpme.gallery.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

/**
 * Created by Mohit on 24/07/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    BasicCallBack basicCallBack;
    private ArrayList<Media> media;
    private OnSingleTap onSingleTap;

    public ImageAdapter(ArrayList<Media> media, BasicCallBack onItemClickListener, OnSingleTap singleTap) {
        this.media = media;
        this.onSingleTap = singleTap;
        this.basicCallBack = onItemClickListener;
    }

    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_image_pager, null, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(getContext())
                .load(media.get(position).getUri())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(0.5f)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basicCallBack.callBack(0, null);
            }
        });
    }

    /**
     * Interface for calling up the toggleUI on single tap on the image.
     */
    public interface OnSingleTap {
        void singleTap();
    }

    @Override
    public int getItemCount() {
        return media.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView imageView;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = new PhotoView(ActivitySwitchHelper.context);
            imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    onSingleTap.singleTap();
                }

                @Override
                public void onOutsidePhotoTap() {
                    onSingleTap.singleTap();
                }
            });

            linearLayout = (LinearLayout) itemView.findViewById(R.id.layout);
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    width, height);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            linearLayout.addView(imageView);


        }
    }
}
