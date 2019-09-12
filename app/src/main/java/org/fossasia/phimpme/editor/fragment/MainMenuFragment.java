package org.fossasia.phimpme.editor.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;

public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener {

  private View menu_filter, menu_enhance, menu_adjust, menu_stickers, menu_write, menu_frame;
  Context context;

  public MainMenuFragment() {}

  public static MainMenuFragment newInstance() {
    MainMenuFragment fragment = new MainMenuFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_editor_main, container, false);
    context = getActivity();
    menu_filter = view.findViewById(R.id.menu_filter);
    menu_enhance = view.findViewById(R.id.menu_enhance);
    menu_adjust = view.findViewById(R.id.menu_adjust);
    menu_stickers = view.findViewById(R.id.menu_sticker);
    menu_write = view.findViewById(R.id.menu_write);
    menu_frame = view.findViewById(R.id.menu_frame);
    menu_filter.setOnClickListener(this);
    menu_enhance.setOnClickListener(this);
    menu_adjust.setOnClickListener(this);
    menu_stickers.setOnClickListener(this);
    menu_write.setOnClickListener(this);
    menu_frame.setOnClickListener(this);
    highLightSelectedOption(EditImageActivity.getMode());
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
  public void onDestroy() {
    super.onDestroy();
    MyApplication.getRefWatcher(getActivity()).watch(this);
  }

  @Override
  public void onShow() {}

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.menu_filter:
        activity.changeMode(EditImageActivity.MODE_FILTERS);
        activity.sliderFragment.resetBitmaps();
        activity.changeMiddleFragment(EditImageActivity.MODE_FILTERS);
        break;
      case R.id.menu_enhance:
        activity.changeMode(EditImageActivity.MODE_ENHANCE);
        activity.sliderFragment.resetBitmaps();
        activity.changeMiddleFragment(EditImageActivity.MODE_ENHANCE);
        break;
      case R.id.menu_adjust:
        activity.changeMode(EditImageActivity.MODE_ADJUST);
        activity.changeMiddleFragment(EditImageActivity.MODE_ADJUST);
        break;
      case R.id.menu_sticker:
        activity.changeMode(EditImageActivity.MODE_STICKER_TYPES);
        activity.changeMiddleFragment(EditImageActivity.MODE_STICKER_TYPES);
        break;
      case R.id.menu_write:
        activity.changeMode(EditImageActivity.MODE_WRITE);
        activity.changeMiddleFragment(EditImageActivity.MODE_WRITE);
        break;
      case R.id.menu_frame:
        activity.changeMode(EditImageActivity.MODE_FRAME);
        activity.changeMiddleFragment(EditImageActivity.MODE_FRAME);
        break;
    }
  }

  public void highLightSelectedOption(int mode) {
    menu_filter.setBackgroundColor(Color.TRANSPARENT);
    menu_enhance.setBackgroundColor(Color.TRANSPARENT);
    menu_adjust.setBackgroundColor(Color.TRANSPARENT);
    menu_stickers.setBackgroundColor(Color.TRANSPARENT);
    menu_write.setBackgroundColor(Color.TRANSPARENT);
    menu_frame.setBackgroundColor(Color.TRANSPARENT);
    int color = ContextCompat.getColor(context, R.color.md_grey_200);
    switch (mode) {
      case 2:
        menu_filter.setBackgroundColor(color);
        break;
      case 3:
        menu_enhance.setBackgroundColor(color);
        break;
      case 4:
        menu_adjust.setBackgroundColor(color);
        break;
      case 5:
        menu_stickers.setBackgroundColor(color);
        break;
      case 6:
        menu_write.setBackgroundColor(color);
        break;
      case 12:
        menu_frame.setBackgroundColor(color);
        break;
      default:
        break;
    }
  }
}
