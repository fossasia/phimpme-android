package vn.mbm.phimp.me.editor.editimage.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.editor.editimage.EditImageActivity;
import vn.mbm.phimp.me.editor.editimage.view.StickerView;

public class RecyclerMenuFragment extends BaseEditFragment {

    RecyclerView recyclerView;
    int MODE;
    private ArrayList<Bitmap> filterThumbs;
    View fragmentView;
    private StickerView mStickerView;
    static final String[] stickerPath = {"stickers/type1", "stickers/type2", "stickers/type3", "stickers/type4", "stickers/type5", "stickers/type6"};

    public RecyclerMenuFragment() {

    }

    public static RecyclerMenuFragment newInstance(int mode) {
        RecyclerMenuFragment fragment = new RecyclerMenuFragment();
        fragment.MODE = mode;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) fragmentView.findViewById(R.id.editor_recyclerview);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(new mRecyclerAdapter());
        this.mStickerView = activity.mStickerView;
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
    public void onShow() {

    }

    public ArrayList<Bitmap> getFilterThumbs() {
        Bitmap bitmap = null;
        GetFilterThumbsTask getFilterThumbsTask = new GetFilterThumbsTask();
        return null;
    }

    private class GetFilterThumbsTask extends AsyncTask<Bitmap,Void,ArrayList<Bitmap>>{


        @Override
        protected ArrayList<Bitmap> doInBackground(Bitmap... params) {

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
        }
    }

    class mRecyclerAdapter extends RecyclerView.Adapter<mRecyclerAdapter.mViewHolder>{

        int defaulticon;
        TypedArray iconlist,titlelist;
        ArrayList<Bitmap> filterthumb;

        class mViewHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            TextView title;
            View view;
            mViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                icon = (ImageView)itemView.findViewById(R.id.editor_item_image);
                title = (TextView)itemView.findViewById(R.id.editor_item_title);
            }
        }

        mRecyclerAdapter() {
            defaulticon = R.mipmap.ic_launcher;
            switch (MODE) {
                case EditImageActivity.MODE_FILTERS:
                    //filterthumb = getFilterThumbs();
                    iconlist = getActivity().getResources().obtainTypedArray(R.array.filter_icons);
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

            /*if (RecyclerMenuFragment.mode == EditImageActivity.MODE_FILTERS)
                holder.icon.setImageBitmap(filterthumb.get(position));
            else*/
                holder.icon.setImageResource(iconlist.getResourceId(position,defaulticon));

            holder.title.setText(titlelist.getString(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClicked(position,v);
                }
            });
        }

        @Override
        public int getItemCount() {
            return titlelist.length();
        }

        void itemClicked(int pos, View view){
            switch (MODE){
                case EditImageActivity.MODE_FILTERS:
                    activity.changeBottomFragment(EditImageActivity.MODE_SLIDER);
                    activity.setEffectType(pos,MODE);
                    break;

                case EditImageActivity.MODE_ENHANCE:
                    activity.sliderFragment.doPendingApply();
                    activity.changeBottomFragment(EditImageActivity.MODE_SLIDER);
                    activity.setEffectType(pos,MODE);
                    break;

                case EditImageActivity.MODE_STICKER_TYPES:
                    String data = (String) view.getTag();
                    activity.setStickerType(data);
                    activity.changeBottomFragment(EditImageActivity.MODE_STICKERS);
                    break;
            }
        }
    }
}
