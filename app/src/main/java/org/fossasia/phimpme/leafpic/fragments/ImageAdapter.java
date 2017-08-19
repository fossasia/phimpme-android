package org.fossasia.phimpme.leafpic.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.leafpic.data.Media;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.fossasia.phimpme.utilities.ActivitySwitchHelper.getContext;

/**
 * Created by Mohit on 24/07/17.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private ArrayList<Media> media;
    BasicCallBack basicCallBack;

    public ImageAdapter(ArrayList<Media> media, BasicCallBack onItemClickListener) {
        this.media = media;
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
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                basicCallBack.callBack(0,null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return media.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.unit_imageview)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
