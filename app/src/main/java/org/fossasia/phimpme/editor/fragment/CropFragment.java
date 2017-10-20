package org.fossasia.phimpme.editor.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import java.util.ArrayList;
import java.util.List;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditBaseActivity;
import org.fossasia.phimpme.editor.EditImageActivity;
import org.fossasia.phimpme.editor.model.RatioItem;
import org.fossasia.phimpme.editor.utils.Matrix3;
import org.fossasia.phimpme.editor.view.CropImageView;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouchBase;


public class CropFragment extends BaseEditFragment {
    public static final int INDEX = 3;
	public static final String TAG = CropFragment.class.getName();
	private View mainView;
	ImageButton cancel,apply;
	public CropImageView mCropPanel;
	private LinearLayout ratioList;
	private static List<RatioItem> dataList = new ArrayList<RatioItem>();
	static {
		dataList.add(new RatioItem("free", -1f));
		dataList.add(new RatioItem("1:1", 1f));
		dataList.add(new RatioItem("1:2", 1 / 2f));
		dataList.add(new RatioItem("1:3", 1 / 3f));
		dataList.add(new RatioItem("2:3", 2 / 3f));
		dataList.add(new RatioItem("3:4", 3 / 4f));
		dataList.add(new RatioItem("2:1", 2f));
		dataList.add(new RatioItem("3:1", 3f));
		dataList.add(new RatioItem("3:2", 3 / 2f));
		dataList.add(new RatioItem("4:3", 4 / 3f));
	}
	private List<TextView> textViewList = new ArrayList<TextView>();

	public static int SELECTED_COLOR = Color.GREEN;
	public static int UNSELECTED_COLOR = Color.BLACK;
	private CropRationClick mCropRationClick = new CropRationClick();
	public TextView selctedTextView;

	public static CropFragment newInstance() {
		CropFragment fragment = new CropFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_edit_image_crop, null);
		return mainView;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		resetCropView();
	}

	private void resetCropView() {
		if (null != activity && null != mCropPanel){
			mCropPanel.setVisibility(View.GONE);
			RectF r = activity.mainImage.getBitmapRect();
			activity.mCropPanel.setCropRect(r);
		}
	}

	private void setUpRatioList() {
		ratioList.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		params.leftMargin = 20;
		params.rightMargin = 20;
		for (int i = 0, len = dataList.size(); i < len; i++) {
			TextView text = new TextView(activity);
			text.setTextColor(UNSELECTED_COLOR);
			text.setTextSize(20);
			text.setText(dataList.get(i).getText());
			textViewList.add(text);
			ratioList.addView(text, params);
			text.setTag(i);
			if (i == 0) {
				selctedTextView = text;
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

			mCropPanel.setRatioCropRect(activity.mainImage.getBitmapRect(),
					dataItem.getRatio());
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        cancel = (ImageButton) mainView.findViewById(R.id.crop_cancel);
		apply = (ImageButton) mainView.findViewById(R.id.crop_apply);

		cancel.setImageDrawable(new IconicsDrawable(this.getContext()).icon(GoogleMaterial.Icon.gmd_clear).sizeDp(24));
		apply.setImageDrawable(new IconicsDrawable(this.getContext()).icon(GoogleMaterial.Icon.gmd_done).sizeDp(24));

		ratioList = (LinearLayout) mainView.findViewById(R.id.ratio_list_group);
        setUpRatioList();
        this.mCropPanel = ensureEditActivity().mCropPanel;
		cancel.setOnClickListener(new BackToMenuClick());
		apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				applyCropImage();
			}
		});
		onShow();
	}

    @Override
    public void onShow() {

		activity.changeMode(EditImageActivity.MODE_CROP);
        activity.mCropPanel.setVisibility(View.VISIBLE);
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        RectF r = activity.mainImage.getBitmapRect();
        activity.mCropPanel.setCropRect(r);
    }

	private final class BackToMenuClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			backToMain();
		}
	}

	public void backToMain() {
		activity.changeMode(EditImageActivity.MODE_ADJUST);
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
			dialog = EditBaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
					false);
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

			Bitmap resultBit = Bitmap.createBitmap(params[0],
					(int) cropRect.left, (int) cropRect.top,
					(int) cropRect.width(), (int) cropRect.height());

			return resultBit;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			dialog.dismiss();
			if (result == null)
				return;

            activity.changeMainBitmap(result);
			activity.mCropPanel.setCropRect(activity.mainImage.getBitmapRect());
			backToMain();
		}
	}
}
