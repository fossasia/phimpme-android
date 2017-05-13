package vn.mbm.phimp.me.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import vn.mbm.phimp.me.Fragments.Gallery;
import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.Utils;
import vn.mbm.phimp.me.Models.GalleryImageModel;


/**
 * Created by vinay on 13/5/17.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.mViewHolder> {

    public static ArrayList<GalleryImageModel> galleryImageList;
    private Context context;

    public GalleryAdapter(ArrayList<GalleryImageModel> imageList){
        galleryImageList = imageList;
    }

    public class mViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
        ImageView imageView;
        ItemClickListener itemClickListener;
        public mViewHolder(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.image);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getPosition(), true);
            return true;
        }
    }

    @Override
    public GalleryAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery,parent,false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Utils.getScreenWidth(context)/(Gallery.COLUMN_COUNT),
                Utils.getScreenWidth(context)/Gallery.COLUMN_COUNT);
        view.setLayoutParams(layoutParams);

        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.mViewHolder holder, final int position) {
        Glide.with(context)
                .load(Uri.fromFile(new File(galleryImageList.get(position).getPath())))
                .override(100,100)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .thumbnail(0.1f)
                .into(holder.imageView);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "Long Click on " + position, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Clicked on " + position , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != galleryImageList) ? galleryImageList.size() : 0;
    }
}

