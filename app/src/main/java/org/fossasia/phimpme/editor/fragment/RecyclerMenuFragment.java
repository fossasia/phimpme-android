package org.fossasia.phimpme.editor.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;
import org.fossasia.phimpme.editor.filter.PhotoProcessing;
import org.fossasia.phimpme.editor.view.StickerView;

import java.util.ArrayList;

public class RecyclerMenuFragment extends BaseEditFragment {

    RecyclerView recyclerView;
    int MODE;
    private static ArrayList<Bitmap> filterThumbs;
    View fragmentView;
    private StickerView mStickerView;
    static final String[] stickerPath = {"stickers/type1", "stickers/type2", "stickers/type3", "stickers/type4", "stickers/type5", "stickers/type6", "stickers/type7"};
    Bitmap currentBitmap,tempBitmap;
    int bmWidth = -1,bmHeight = -1;
    int defaulticon;
    TypedArray iconlist,titlelist;
    static int currentSelection = -1;

    RecyclerView.LayoutManager layoutManager;

    public RecyclerMenuFragment() {

    }

    public static RecyclerMenuFragment newInstance(int mode) {
        RecyclerMenuFragment fragment = new RecyclerMenuFragment();
        fragment.MODE = mode;
        return fragment;
    }

    public void clearCurrentSelection(){
        if(currentSelection != -1){
            mRecyclerAdapter.mViewHolder holder = (mRecyclerAdapter.mViewHolder) recyclerView.findViewHolderForAdapterPosition(currentSelection);
            if(holder != null){
                holder.wrapper.setBackgroundColor(Color.TRANSPARENT);
            }
            currentSelection = -1;
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int orientation = getActivity().getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        } else if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = new LinearLayoutManager(getActivity());
        }
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.editor_recyclerview);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new mRecyclerAdapter());
        this.mStickerView = activity.mStickerView;
        onShow();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_editor_recycler, container, false);
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    //    if (filterThumbs != null)filterThumbs=null;
        MyApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onShow() {
        if (MODE == EditImageActivity.MODE_FILTERS) {
            if (this.currentBitmap != activity.mainBitmap) filterThumbs = null;
            this.currentBitmap = activity.mainBitmap;
            getFilterThumbs();
        }
    }

    public void getFilterThumbs() {
        if (null!= currentBitmap) {
                GetFilterThumbsTask getFilterThumbsTask = new GetFilterThumbsTask();
                getFilterThumbsTask.execute();
        }
    }

    private Bitmap getResizedBitmap(Bitmap bm, int divisor) {
        float scale = 1/(float)divisor;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        if (bmWidth <= 0 ) bmWidth = bm.getWidth();
        if (bmHeight <= 0) bmHeight = bm.getHeight();
        return Bitmap.createBitmap(bm, 0, 0, bmWidth, bmHeight, matrix, false);
    }

    private class GetFilterThumbsTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            if (filterThumbs==null) {
                filterThumbs = new ArrayList<>();
                bmWidth = currentBitmap.getWidth();
                bmHeight = currentBitmap.getHeight();
                int leng = (titlelist!=null) ? titlelist.length() : 0;
                for (int i = 0; i < leng; i++) {
                    filterThumbs.add(PhotoProcessing.processImage(getResizedBitmap(currentBitmap, 5), (i + 100 * EditImageActivity.MODE_FILTERS), 100));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void bitmaps) {
            super.onPostExecute(bitmaps);
            if (filterThumbs == null)
                return;

            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    class mRecyclerAdapter extends RecyclerView.Adapter<mRecyclerAdapter.mViewHolder>{

        class mViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView title;
            LinearLayout wrapper;
            View view;
            mViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                icon = (ImageView)itemView.findViewById(R.id.editor_item_image);
                title = (TextView)itemView.findViewById(R.id.editor_item_title);
                wrapper = (LinearLayout)itemView.findViewById(R.id.ll_effect_wrapper);
            }
        }

        mRecyclerAdapter() {
            defaulticon = R.drawable.ic_photo_filter;
            switch (MODE) {
                case EditImageActivity.MODE_FILTERS:
                    titlelist = getActivity().getResources().obtainTypedArray(R.array.filter_titles);
                    break;
                case EditImageActivity.MODE_ENHANCE:
                    iconlist = getActivity().getResources().obtainTypedArray(R.array.enhance_icons);
                    titlelist = getActivity().getResources().obtainTypedArray(R.array.enhance_titles);
                    break;
                case EditImageActivity.MODE_STICKER_TYPES:
                    iconlist = getActivity().getResources().obtainTypedArray(R.array.sticker_icons);
                    titlelist = getActivity().getResources().obtainTypedArray(R.array.sticker_titles);
                    break;
            }
        }

        @Override
        public mRecyclerAdapter.mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.editor_iconitem,parent,false);
            return new mViewHolder(view);
        }

        @Override
        public void onBindViewHolder(mRecyclerAdapter.mViewHolder holder, final int position) {

            if (MODE == EditImageActivity.MODE_STICKER_TYPES){
                holder.itemView.setTag(stickerPath[position]);
            }
            int iconImageSize = (int) getActivity().getResources().getDimension(R.dimen.icon_item_image_size_recycler);

            holder.icon.setScaleType(ImageView.ScaleType.FIT_CENTER);

            if (MODE == EditImageActivity.MODE_FILTERS) {
                if (currentBitmap!=null && filterThumbs!=null && filterThumbs.size() > position) {
                    iconImageSize = (int) getActivity().getResources().getDimension(R.dimen.icon_item_image_size_filter_preview);
                    holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.icon.setImageBitmap(filterThumbs.get(position));
                }else {
                    holder.icon.setImageResource(defaulticon);
                }
            }else {
                holder.icon.setImageResource(iconlist.getResourceId(position, defaulticon));
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iconImageSize,iconImageSize);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            holder.icon.setLayoutParams(layoutParams);
            holder.title.setText(titlelist.getString(position));
            holder.wrapper.setBackgroundColor(Color.TRANSPARENT);

            if(currentSelection == position)
                holder.wrapper.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.md_grey_200));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    highlightSelectedOption(position, v);
                    itemClicked(position,v);
                }
            });
        }

        private void highlightSelectedOption(int position, View v) {
            int color = ContextCompat.getColor(v.getContext(), R.color.md_grey_200);

            if(currentSelection != position){
                notifyItemChanged(currentSelection);
                ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            }

            if(currentSelection != -1 && recyclerView.findViewHolderForAdapterPosition(currentSelection) != null) {
                    ((mRecyclerAdapter.mViewHolder) recyclerView.findViewHolderForAdapterPosition(currentSelection))
                            .wrapper
                            .setBackgroundColor(Color.TRANSPARENT);
            }

            ((mViewHolder) recyclerView.findViewHolderForAdapterPosition(position))
                 .wrapper
                 .setBackgroundColor(color);

            currentSelection = position;
        }

        @Override
        public int getItemCount() {
            return titlelist.length();
        }

        void itemClicked(int pos, View view){
            switch (MODE){
                case EditImageActivity.MODE_FILTERS:
                    activity.setEffectType(pos,MODE);
                    activity.changeMode(EditImageActivity.MODE_SLIDER);
                    activity.changeBottomFragment(EditImageActivity.MODE_SLIDER);
                    break;

                case EditImageActivity.MODE_ENHANCE:
                    activity.setEffectType(pos,MODE);
                    activity.changeMode(EditImageActivity.MODE_SLIDER);
                    activity.changeBottomFragment(EditImageActivity.MODE_SLIDER);
                    break;

                case EditImageActivity.MODE_STICKER_TYPES:
                    String data = (String) view.getTag();
                    activity.setStickerType(data);
                    activity.changeMode(EditImageActivity.MODE_STICKERS);
                    activity.changeBottomFragment(EditImageActivity.MODE_STICKERS);
                    break;
            }
        }
    }
}
