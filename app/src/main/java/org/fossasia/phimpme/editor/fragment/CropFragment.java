package org.fossasia.phimpme.editor.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.IconicsSize;
import com.mikepenz.iconics.typeface.library.googlematerial.GoogleMaterial;

import java.util.ArrayList;
import java.util.List;
import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditBaseActivity;
import org.fossasia.phimpme.editor.EditImageActivity;
import org.fossasia.phimpme.editor.model.RatioItem;
import org.fossasia.phimpme.editor.utils.Matrix3;
import org.fossasia.phimpme.editor.view.CropImageView;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouchBase;

public class CropFragment extends BaseEditFragment {
  public static final String TAG = CropFragment.class.getName();
  private View mainView;
  private View cancel, apply;
  public CropImageView mCropPanel;
  private LinearLayout ratioList, imageList, combinedList;
  private static List<RatioItem> dataList = new ArrayList<RatioItem>();
  private List<TextView> textViewList = new ArrayList<TextView>();
  private RectF rec;

  public static int SELECTED_COLOR = Color.BLUE;
  public static int UNSELECTED_COLOR = Color.BLACK;
  private CropRationClick mCropRationClick = new CropRationClick();
  public TextView selctedTextView;
  private TextView sView;

  public static CropFragment newInstance() {
    CropFragment fragment = new CropFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    dataList.add(
        new RatioItem(
            getString(R.string.crop_1_1),
            1f,
            new IconicsDrawable(this.getContext())
                .icon(GoogleMaterial.Icon.gmd_crop_square)
                .size(IconicsSize.dp(18))));
    dataList.add(
        new RatioItem(
            getString(R.string.crop_3_2),
            3 / 2f,
            new IconicsDrawable(this.getContext())
                .icon(GoogleMaterial.Icon.gmd_crop_3_2)
                .size(IconicsSize.dp(18))));
    dataList.add(
        new RatioItem(
            getString(R.string.crop_free),
            -1f,
            new IconicsDrawable(this.getContext())
                .icon(GoogleMaterial.Icon.gmd_crop_free)
                .size(IconicsSize.dp(18))));
    dataList.add(
        new RatioItem(
            getString(R.string.crop_7_5),
            7 / 5f,
            new IconicsDrawable(this.getContext())
                .icon(GoogleMaterial.Icon.gmd_crop_7_5)
                .size(IconicsSize.dp(18))));
    dataList.add(
        new RatioItem(
            getString(R.string.crop_16_9),
            16 / 9f,
            new IconicsDrawable(this.getContext())
                .icon(GoogleMaterial.Icon.gmd_crop_16_9)
                .size(IconicsSize.dp(18))));
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
    return mainView;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    resetCropView();
  }

  private void resetCropView() {
    if (null != activity && null != mCropPanel && activity.mainBitmap != null) {
      mCropPanel.setVisibility(View.GONE);
      RectF r = activity.mainImage.getBitmapRect();
      activity.mCropPanel.setCropRect(r);
    }
  }

  private void setUpRatioListPortriat() {
    ratioList.removeAllViews();
    imageList.removeAllViews();

    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    LinearLayout.LayoutParams params1 =
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    params.gravity = Gravity.CENTER_VERTICAL;
    params1.leftMargin = 50;
    params.weight = (float) 0.2;
    params1.weight = (float) 0.2;
    for (int i = 0, len = 5; i < len; i++) {
      ImageView image = new ImageView(activity);
      TextView text = new TextView(activity);
      image.setImageDrawable(dataList.get(i).getImage());
      text.setTextColor(UNSELECTED_COLOR);
      text.setTextSize(18);
      text.setText(dataList.get(i).getText());
      textViewList.add(text);
      ratioList.addView(text, params1);
      imageList.addView(image, params);
      text.setTag(i);
      if (i == 0 && selctedTextView == null) {
        selctedTextView = text;
        sView = selctedTextView;
        sView.setTag(selctedTextView.getTag());
      }
      dataList.get(i).setIndex(i);
      text.setTag(dataList.get(i));
      text.setOnClickListener(mCropRationClick);
    }
    selctedTextView.setTextColor(SELECTED_COLOR);
  }

  private void setUpRatioListLandscape() {
    combinedList.removeAllViews();

    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    LinearLayout.LayoutParams params1 =
        new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    params.gravity = Gravity.CENTER_HORIZONTAL;
    params1.leftMargin = 50;
    params.weight = (float) 0.2;
    params1.weight = (float) 0.2;
    for (int i = 0, len = 5; i < len; i++) {
      ImageView image = new ImageView(activity);
      TextView text = new TextView(activity);
      image.setImageDrawable(dataList.get(i).getImage());
      text.setTextColor(UNSELECTED_COLOR);
      text.setTextSize(18);
      text.setText(dataList.get(i).getText());
      textViewList.add(text);
      combinedList.addView(image, params1);
      combinedList.addView(text, params);
      text.setTag(i);
      if (i == 0 && selctedTextView == null) {
        selctedTextView = text;
        sView = selctedTextView;
        sView.setTag(selctedTextView.getTag());
      }
      dataList.get(i).setIndex(i);
      text.setTag(dataList.get(i));
      text.setOnClickListener(mCropRationClick);
    }
    selctedTextView.setTextColor(SELECTED_COLOR);
  }

  private final class CropRationClick implements OnClickListener {
    @Override
    public void onClick(View v) {
      TextView curTextView = (TextView) v;
      selctedTextView.setTextColor(UNSELECTED_COLOR);
      RatioItem dataItem = (RatioItem) v.getTag();
      selctedTextView = curTextView;
      selctedTextView.setTextColor(SELECTED_COLOR);
      sView = selctedTextView;
      sView.setTag(v.getTag());
      mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(), dataItem.getRatio());
    }
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    cancel = mainView.findViewById(R.id.crop_cancel);
    apply = mainView.findViewById(R.id.crop_apply);
    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
      combinedList = mainView.findViewById(R.id.combinedList);
      setUpRatioListLandscape();
    } else if (getResources().getConfiguration().orientation
        == Configuration.ORIENTATION_PORTRAIT) {
      ratioList = mainView.findViewById(R.id.ratio_list_group);
      imageList = mainView.findViewById(R.id.image_crop);
      setUpRatioListPortriat();
    }
    this.mCropPanel = ensureEditActivity().mCropPanel;
    cancel.setOnClickListener(new BackToMenuClick());
    apply.setOnClickListener(
        new OnClickListener() {
          @Override
          public void onClick(View v) {
            applyCropImage();
          }
        });
    if (savedInstanceState != null) {
      rec = savedInstanceState.getParcelable("Rect");
      sView.setText(savedInstanceState.getString("text"));
    }
    onShow();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable("Rect", activity.mainImage.getBitmapRect());
    outState.putString("text", sView.getText().toString());
  }

  @Override
  public void onShow() {
    if (activity.mainBitmap == null) {
      getActivity()
          .getSupportFragmentManager()
          .beginTransaction()
          .remove(CropFragment.this)
          .commit();
      return;
    }
    activity.changeMode(EditImageActivity.MODE_CROP);
    activity.mCropPanel.setVisibility(View.VISIBLE);
    activity.mainImage.setImageBitmap(activity.mainBitmap);
    activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
    activity.mainImage.setScaleEnabled(false);
    RectF r = activity.mainImage.getBitmapRect();
    if (rec != null) {
      activity.mCropPanel.setCropRect(rec);
    } else {
      activity.mCropPanel.setCropRect(r);
    }

    if (sView != null) {
      selctedTextView.setTextColor(UNSELECTED_COLOR);
      RatioItem dataItem = (RatioItem) sView.getTag();
      selctedTextView = sView;
      sView.setTextColor(SELECTED_COLOR);
      selctedTextView.setTextColor(SELECTED_COLOR);
      if (rec != null) mCropPanel.setRatioCropRect(rec, dataItem.getRatio());
    }
  }

  private final class BackToMenuClick implements OnClickListener {
    @Override
    public void onClick(View v) {
      backToMain();
    }
  }

  public void backToMain() {
    EditImageActivity.mode = EditImageActivity.MODE_ADJUST;
    activity.changeBottomFragment(EditImageActivity.MODE_MAIN);
    activity.adjustFragment.clearSelection();
    mCropPanel.setVisibility(View.GONE);
    activity.mainImage.setScaleEnabled(true);
    if (selctedTextView != null) {
      selctedTextView.setTextColor(UNSELECTED_COLOR);
    }
    mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(), -1);
  }

  public void applyCropImage() {
    CropImageTask task = new CropImageTask();
    task.execute(activity.mainBitmap);
  }

  private final class CropImageTask extends AsyncTask<Bitmap, Void, Bitmap> {
    private Dialog dialog;

    @Override
    protected void onCancelled() {
      super.onCancelled();
      dialog.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCancelled(Bitmap result) {
      super.onCancelled(result);
      dialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      dialog = EditBaseActivity.getLoadingDialog(getActivity(), R.string.saving_image, false);
      dialog.show();
    }

    @SuppressWarnings("WrongThread")
    @Override
    protected Bitmap doInBackground(Bitmap... params) {
      RectF cropRect = mCropPanel.getCropRect();
      Matrix touchMatrix = activity.mainImage.getImageViewMatrix();
      // Canvas canvas = new Canvas(resultBit);
      float[] data = new float[9];
      touchMatrix.getValues(data);
      Matrix3 cal = new Matrix3(data);
      Matrix3 inverseMatrix = cal.inverseMatrix();
      Matrix m = new Matrix();
      m.setValues(inverseMatrix.getValues());
      m.mapRect(cropRect);

      Bitmap resultBit =
          Bitmap.createBitmap(
              params[0],
              (int) cropRect.left,
              (int) cropRect.top,
              (int) cropRect.width(),
              (int) cropRect.height());

      return resultBit;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
      super.onPostExecute(result);
      dialog.dismiss();
      if (result == null) return;

      activity.changeMainBitmap(result);
      activity.mCropPanel.setCropRect(activity.mainImage.getBitmapRect());
      backToMain();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    MyApplication.getRefWatcher(getActivity()).watch(this);
  }
}
