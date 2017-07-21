package org.fossasia.phimpme.editor.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;

public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener{

    View menu_filter,menu_enhance,menu_adjust,menu_stickers, menu_text;

    public MainMenuFragment() {

    }

    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editor_main, container, false);
        menu_filter = view.findViewById(R.id.menu_filter);
        menu_enhance = view.findViewById(R.id.menu_enhance);
        menu_adjust = view.findViewById(R.id.menu_adjust);
        menu_stickers = view.findViewById(R.id.menu_sticker);
        menu_text = view.findViewById(R.id.menu_text);

        menu_filter.setOnClickListener(this);
        menu_enhance.setOnClickListener(this);
        menu_adjust.setOnClickListener(this);
        menu_stickers.setOnClickListener(this);
        menu_text.setOnClickListener(this);

        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_filter:
                EditImageActivity.mode = (EditImageActivity.MODE_FILTERS);
                activity.sliderFragment.resetBitmaps();
                activity.changeMiddleFragment(EditImageActivity.MODE_FILTERS);
                break;
            case R.id.menu_enhance:
                EditImageActivity.mode = (EditImageActivity.MODE_ENHANCE);
                activity.sliderFragment.resetBitmaps();
                activity.changeMiddleFragment(EditImageActivity.MODE_ENHANCE);
                break;
            case R.id.menu_adjust:
                EditImageActivity.mode = EditImageActivity.MODE_ADJUST;
                activity.changeMiddleFragment(EditImageActivity.MODE_ADJUST);
                break;
            case R.id.menu_sticker:
                EditImageActivity.mode = (EditImageActivity.MODE_STICKER_TYPES);
                activity.changeMiddleFragment(EditImageActivity.MODE_STICKER_TYPES);
                break;
            case R.id.menu_text:
                EditImageActivity.mode = EditImageActivity.MODE_WRITE;
                activity.changeMiddleFragment(EditImageActivity.MODE_WRITE);
                break;
        }
    }
}
