package org.fossasia.phimpme.editor.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;

public class ThreeItemFragment extends BaseEditFragment implements View.OnClickListener{
    private LinearLayout ll_item1, ll_item2, ll_item3;

    public static ThreeItemFragment newInstance(int mode) {
        ThreeItemFragment fragment = new ThreeItemFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor_threeitem, container, false);

        ImageView icon1,icon2,icon3;
        TextView text1,text2,text3;
        View item1,item2,item3;

        item1 = view.findViewById(R.id.menu_item3);
        item2 = view.findViewById(R.id.menu_item4);
        item3 = view.findViewById(R.id.menu_item5);

        ll_item1 = (LinearLayout)view.findViewById(R.id.menu_item3);
        ll_item2 = (LinearLayout)view.findViewById(R.id.menu_item4);
        ll_item3 = (LinearLayout)view.findViewById(R.id.menu_item5);


        icon1 = (ImageView) view.findViewById(R.id.item3iconimage);
        icon2 = (ImageView) view.findViewById(R.id.item4iconimage);
        icon3 = (ImageView) view.findViewById(R.id.item5iconimage);


        text1 = (TextView) view.findViewById(R.id.item3text);
        text2 = (TextView) view.findViewById(R.id.item4text);
        text3 = (TextView) view.findViewById(R.id.item5text);


        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);


        icon1.setImageResource(R.drawable.ic_crop);
        icon2.setImageResource(R.drawable.ic_rotate);
        icon3.setImageResource(R.drawable.ic_square);


        text1.setText(getString(R.string.crop));
        text2.setText(getString(R.string.rotate));
        text3.setText(getString(R.string.square));


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onShow() {
        System.out.println("onShow()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_item3:
                clearSelection();
                ll_item1.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.md_grey_200));
                firstItemClicked();
                break;
            case R.id.menu_item4:
                clearSelection();
                ll_item2.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.md_grey_200));
                secondItemClicked();
                break;
            case R.id.menu_item5:
                clearSelection();
                ll_item3.setBackgroundColor(ContextCompat.getColor(v.getContext(), R.color.md_grey_200));
                thirdItemClicked();
                break;
            default:
                break;
        }
    }

    public void clearSelection() {
        ll_item1.setBackgroundColor(Color.TRANSPARENT);
        ll_item2.setBackgroundColor(Color.TRANSPARENT);
        ll_item3.setBackgroundColor(Color.TRANSPARENT);
    }


    private void firstItemClicked() {
        activity.changeMode(EditImageActivity.MODE_CROP);
        activity.changeBottomFragment(EditImageActivity.MODE_CROP);

    }

    private void secondItemClicked() {
        activity.changeMode(EditImageActivity.MODE_ROTATE);
        activity.changeBottomFragment(EditImageActivity.MODE_ROTATE);

    }

    private void thirdItemClicked() {
        activity.changeMode(EditImageActivity.MODE_SQUARE);
        activity.changeBottomFragment(EditImageActivity.MODE_SQUARE);

    }

}
