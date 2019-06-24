package org.fossasia.phimpme.editor.fragment;

import static com.mikepenz.iconics.Iconics.TAG;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;
import org.fossasia.phimpme.editor.filter.PhotoProcessing;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouchBase;

public class SliderFragment extends BaseEditFragment
    implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

  SeekBar seekBar;
  ImageButton cancel, apply;
  public Bitmap filterBit;
  private int SeekBarProgress;
  Bitmap currentBitmap;
  View fragmentView;

  public SliderFragment() {}

  public static SliderFragment newInstance() {
    SliderFragment fragment = new SliderFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    cancel = fragmentView.findViewById(R.id.seekbar_cancel);
    apply = fragmentView.findViewById(R.id.seekbar_apply);
    seekBar = fragmentView.findViewById(R.id.slider);

    cancel.setImageResource(R.drawable.ic_close_black_24dp);
    apply.setImageResource(R.drawable.ic_done_black_24dp);

    cancel.setOnClickListener(this);
    apply.setOnClickListener(this);

    seekBar.setMax(100);
    if (savedInstanceState != null) {
      SeekBarProgress = savedInstanceState.getInt("Seekbar Progress");
    }
    setDefaultSeekBarProgress();
    seekBar.setOnSeekBarChangeListener(this);

    onShow();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("Seekbar Progress", seekBar.getProgress());
    outState.putParcelable("FilterBit", filterBit);
  }

  private void setDefaultSeekBarProgress() {
    if (null != seekBar) {
      switch (EditImageActivity.effectType / 100) {
        case EditImageActivity.MODE_FILTERS:
          if (SeekBarProgress > 0) {
            seekBar.setProgress(SeekBarProgress);
          } else {
            Log.d(TAG, "setDefaultSeekBarProgress:Setting to 100");
            seekBar.setProgress(100);
          }
          break;
        case EditImageActivity.MODE_ENHANCE:
          switch (EditImageActivity.effectType % 300) {
            case 2:
            case 6:
            case 7:
            case 8:
              if (SeekBarProgress > 0) {
                seekBar.setProgress(SeekBarProgress);
              } else {
                seekBar.setProgress(0);
              }
              break;
            case 0:
            case 1:
            case 3:
            case 4:
            case 5:
              if (SeekBarProgress > 0) {
                seekBar.setProgress(SeekBarProgress);
              } else {
                seekBar.setProgress(50);
              }
              break;
          }
          break;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    fragmentView = inflater.inflate(R.layout.fragment_editor_slider, container, false);
    return fragmentView;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MyApplication.getRefWatcher(getActivity()).watch(this);
  }

  @Override
  public void onDetach() {
    super.onDetach();
  }

  @Override
  public void onShow() {

    if (activity != null) {
      setDefaultSeekBarProgress();
      activity.changeMode(EditImageActivity.MODE_SLIDER);
      currentBitmap = activity.mainBitmap;
      activity.mainImage.setImageBitmap(activity.mainBitmap);
      activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
      activity.mainImage.setScaleEnabled(false);
      defaultApply();
    }
  }

  private void defaultApply() {
    ProcessImageTask processImageTask = new ProcessImageTask();
    processImageTask.execute(seekBar.getProgress());
  }

  public void doPendingApply() {
    if (null != activity && null != filterBit) activity.changeMainBitmap(filterBit);
  }

  public void resetBitmaps() {
    if (null != filterBit) filterBit.recycle();
    filterBit = null;
    if (null != currentBitmap) currentBitmap.recycle();
    currentBitmap = null;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.seekbar_cancel:
        backToMain();
        break;
      case R.id.seekbar_apply:
        if (filterBit != null) {
          activity.changeMainBitmap(filterBit);
          filterBit = null;
        }
        if (EditImageActivity.effectType / 100 == EditImageActivity.MODE_FILTERS) {
          activity.filterFragment.getFilterThumbs();
        }
        switch (activity.mode) {
          case EditImageActivity.MODE_FILTERS:
            activity.filterFragment.clearCurrentSelection();
            break;
          case EditImageActivity.MODE_ENHANCE:
            activity.enhanceFragment.clearCurrentSelection();
            break;
          default:
            break;
        }
        backToMain();
        break;
    }
  }

  public void backToMain() {
    if (null != activity) {
      currentBitmap = null;
      activity.mainImage.setImageBitmap(activity.mainBitmap);
      EditImageActivity.mode = EditImageActivity.effectType / 100;
      activity.changeBottomFragment(EditImageActivity.MODE_MAIN);
      activity.mainImage.setScaleEnabled(true);

      switch (activity.mode) {
        case EditImageActivity.MODE_FILTERS:
          activity.filterFragment.clearCurrentSelection();
          break;

        case EditImageActivity.MODE_ENHANCE:
          activity.enhanceFragment.clearCurrentSelection();
          break;

        default:
          break;
      }
    }
  }

  int counter = 0;

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    /*
    if ((counter++) % 15 == 0) {
        counter = 0;
        processImageTask.execute(seekBar.getProgress());
    }*/
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {}

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    ProcessImageTask processImageTask = new ProcessImageTask();
    processImageTask.execute(seekBar.getProgress());
  }

  private final class ProcessImageTask extends AsyncTask<Integer, Void, Bitmap> {
    private Bitmap srcBitmap;
    Dialog dialog;
    int val;

    @Override
    protected Bitmap doInBackground(Integer... params) {
      val = params[0];
      if (srcBitmap != null && !srcBitmap.isRecycled()) {
        srcBitmap.recycle();
      }
      if (currentBitmap != null) {
        srcBitmap = Bitmap.createBitmap(currentBitmap.copy(Bitmap.Config.RGB_565, true));
        return PhotoProcessing.processImage(srcBitmap, EditImageActivity.effectType, val);
      }
      return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      super.onPostExecute(result);
      activity.hideProgressBar();
      if (result == null) {
        getActivity()
            .getSupportFragmentManager()
            .beginTransaction()
            .remove(SliderFragment.this)
            .commit();
        return;
      }
      filterBit = result;
      activity.mainImage.setImageBitmap(filterBit);
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      activity.showProgressBar();
    }
  }
}
