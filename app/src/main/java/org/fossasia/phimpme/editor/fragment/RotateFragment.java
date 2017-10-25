package org.fossasia.phimpme.editor.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.shchurov.horizontalwheelview.HorizontalWheelView;

import org.fossasia.phimpme.MyApplication;
import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditImageActivity;
import org.fossasia.phimpme.editor.view.RotateImageView;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouchBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;


public class RotateFragment extends BaseEditFragment {
    public static final String TAG = RotateFragment.class.getName();
    private View mainView;
    private View cancel, apply;
    private RotateImageView mRotatePanel;

    private HorizontalWheelView horizontalWheelView;
    private TextView tvAngle;

    public static RotateFragment newInstance() {
        RotateFragment fragment = new RotateFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_rotate, null);

        initViews();
        setupListeners();
        updateUi();
        onShow();

        return mainView;
    }

    private void initViews() {
        cancel = mainView.findViewById(R.id.rotate_cancel);
        apply = mainView.findViewById(R.id.rotate_apply);
        horizontalWheelView = (HorizontalWheelView) mainView.findViewById(R.id.horizontalWheelView);
        tvAngle = (TextView) mainView.findViewById(R.id.tvAngle);
        this.mRotatePanel = ensureEditActivity().mRotatePanel;
    }

    private void setupListeners() {
        cancel.setOnClickListener(new BackToMenuClick());
        apply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                applyRotateImage();
            }
        });
        horizontalWheelView.setListener(new HorizontalWheelView.Listener() {
            @Override
            public void onRotationChanged(double radians) {
                updateUi();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void updateUi() {
        updateText();
        updateImage();
    }

    private void updateText() {
        String text = String.format(Locale.US, "%.0f°", horizontalWheelView.getDegreesAngle());
        tvAngle.setText(text);
    }

    private void updateImage() {
        int angle = (int) horizontalWheelView.getDegreesAngle();
        mRotatePanel.rotateImage(angle);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyApplication.getRefWatcher(getActivity()).watch(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        resetRotateView();
    }

    private void resetRotateView() {
        if (null != activity && null != mRotatePanel) {
            activity.mRotatePanel.rotateImage(0);
            activity.mRotatePanel.reset();
            activity.mRotatePanel.setVisibility(View.GONE);
            activity.mainImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShow() {
        activity.changeMode(EditImageActivity.MODE_ROTATE);
        activity.mainImage.setImageBitmap(activity.mainBitmap);
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setVisibility(View.GONE);

        activity.mRotatePanel.addBit(activity.mainBitmap, activity.mainImage.getBitmapRect());
        activity.mRotatePanel.reset();
        activity.mRotatePanel.setVisibility(View.VISIBLE);
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
        activity.mainImage.setVisibility(View.VISIBLE);
        this.mRotatePanel.setVisibility(View.GONE);
    }

    public void applyRotateImage() {
        SaveRotateImageTask task = new SaveRotateImageTask();
        task.execute(activity.mainBitmap);
    }

    private final class SaveRotateImageTask extends
            AsyncTask<Bitmap, Void, Bitmap> {
        //private Dialog dialog;

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //dialog.dismiss();
        }

        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
            //dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //dialog = EditBaseActivity.getLoadingDialog(getActivity(), R.string.saving_image,
            //        false);
            //dialog.show();
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            RectF imageRect = mRotatePanel.getImageNewRect();
            Bitmap originBit = params[0];
            Bitmap result = Bitmap.createBitmap((int) imageRect.width(),
                    (int) imageRect.height(), Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(result);
            int w = originBit.getWidth() >> 1;
            int h = originBit.getHeight() >> 1;
            float centerX = imageRect.width() / 2;
            float centerY = imageRect.height() / 2;

            float left = centerX - w;
            float top = centerY - h;

            RectF dst = new RectF(left, top, left + originBit.getWidth(), top
                    + originBit.getHeight());
            canvas.save();
            canvas.scale(mRotatePanel.getScale(), mRotatePanel.getScale(),
                    imageRect.width() / 2, imageRect.height() / 2);
            canvas.rotate(mRotatePanel.getRotateAngle(), imageRect.width() / 2,
                    imageRect.height() / 2);

            canvas.drawBitmap(originBit, new Rect(0, 0, originBit.getWidth(),
                    originBit.getHeight()), dst, null);
            canvas.restore();

            //saveBitmap(result, activity.saveFilePath);// 保存图片
            return result;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //dialog.dismiss();
            if (result == null)
                return;

            activity.changeMainBitmap(result);
            backToMain();
        }
    }// end inner class

    public static void saveBitmap(Bitmap bm, String filePath) {
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}// end class
