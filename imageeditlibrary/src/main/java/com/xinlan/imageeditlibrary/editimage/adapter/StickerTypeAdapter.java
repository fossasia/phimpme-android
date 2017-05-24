package com.xinlan.imageeditlibrary.editimage.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinlan.imageeditlibrary.R;
import com.xinlan.imageeditlibrary.editimage.fragment.StirckerFragment;


/**
 * 贴图分类列表Adapter
 *
 * @author panyi
 */
public class StickerTypeAdapter extends RecyclerView.Adapter<ViewHolder> {
    public static final int[] typeIcon = {R.drawable.stickers_type_animal,
            R.drawable.stickers_type_motion, R.drawable.stickers_type_cos,
            R.drawable.stickers_type_mark, R.drawable.stickers_type_decoration};
    public static final String[] stickerPath = {"stickers/type1", "stickers/type2", "stickers/type3", "stickers/type4", "stickers/type5", "stickers/type6"};
    public static final String[] stickerPathName = {"表情1", "表情2", "表情3", "表情4", "表情5", "表情6"};
    private StirckerFragment mStirckerFragment;
    private ImageClick mImageClick = new ImageClick();

    public StickerTypeAdapter(StirckerFragment fragment) {
        super();
        this.mStirckerFragment = fragment;
    }

    public class ImageHolder extends ViewHolder {
        public ImageView icon;
        public TextView text;

        public ImageHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.icon);
            this.text = (TextView) itemView.findViewById(R.id.text);
        }
    }// end inner class

    @Override
    public int getItemCount() {
        return stickerPathName.length;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v = null;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.view_sticker_type_item, parent, false);
        ImageHolder holer = new ImageHolder(v);
        return holer;
    }

    /**
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageHolder imageHoler = (ImageHolder) holder;
        // imageHoler.icon.setImageResource(R.drawable.ic_launcher);
        String name = stickerPathName[position];
        imageHoler.text.setText(name);
        // TODO
        //imageHoler.icon.setImageResource(typeIcon[position]);
        imageHoler.text.setTag(stickerPath[position]);
        imageHoler.text.setOnClickListener(mImageClick);
    }

    /**
     * 选择贴图类型
     *
     * @author panyi
     */
    private final class ImageClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            String data = (String) v.getTag();
            // System.out.println("data---->" + data);
            mStirckerFragment.swipToStickerDetails(data);
        }
    }// end inner class
}// end class
