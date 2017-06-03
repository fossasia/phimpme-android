package vn.mbm.phimp.me.editor.editimage.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.editor.editimage.EditImageActivity;
import vn.mbm.phimp.me.editor.editimage.filter.PhotoProcessing;
import vn.mbm.phimp.me.editor.editimage.view.imagezoom.ImageViewTouchBase;


public class TuningFragment extends BaseEditFragment {
    public static final int INDEX = 8;
    public static final String TAG = TuningFragment.class.getName();
    private View mainView;
    private View backToMenu;
    public SeekBar mSeekBar;
    private Bitmap filterBit,currentSource;


    public static TuningFragment newInstance() {
        TuningFragment fragment = new TuningFragment();
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
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backToMenu = mainView.findViewById(R.id.back_to_main);
        mSeekBar = (SeekBar) mainView.findViewById(R.id.rotate_bar);

        backToMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backToTune();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekValueChange());
        mSeekBar.setProgress(0);
        mSeekBar.setMax(100);
    }

    @Override
    public void onShow() {
        activity.mode = EditImageActivity.MODE_TUNE;
        currentSource = activity.mTuneListFragment.getCurrentBitmap();
        activity.mainImage.setImageBitmap(activity.mTuneListFragment.getCurrentBitmap());
        activity.mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        activity.mainImage.setScaleEnabled(false);
        activity.bannerFlipper.setVisibility(View.VISIBLE);
        mSeekBar.setProgress(50);
        switch (TuneListFragment.MODE){
            case TuneListFragment.HUE:
            case TuneListFragment.BLUR:
            case TuneListFragment.VIGNETTE:
            case TuneListFragment.SHARPNESS:
                mSeekBar.setProgress(0);
                break;
        }
    }


    public void backToTune() {
        activity.mainImage.setImageBitmap(activity.mTuneListFragment.getCurrentBitmap());
        activity.mode = EditImageActivity.MODE_TUNELIST;
        activity.bottomGallery.setCurrentItem(TuneListFragment.INDEX);
        activity.mainImage.setVisibility(View.VISIBLE);
        filterBit = null;
    }


    public void applyEffect() {
        if (filterBit!=null){
            activity.mTuneListFragment.setCurrentBitmap(filterBit);
        }
        backToTune();
    }


    private final class SeekValueChange implements OnSeekBarChangeListener {
        int counter = 0;

        @Override
        public void onProgressChanged(SeekBar seekBar, int angle,
                                      boolean fromUser) {
            if ((counter++) % 25 == 0) {
                counter = 0;/*
               TuneImage task = new TuneImage();
               task.execute(seekBar.getProgress());*/
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            TuneImage task = new TuneImage();
            task.execute(seekBar.getProgress());
        }
    }

    private final class TuneImage extends AsyncTask<Integer, Void, Bitmap> {
        private Bitmap srcBitmap;
        Dialog dialog;
        int val;

        @Override
        protected Bitmap doInBackground(Integer... params) {
            val = params[0];
            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
            }

            srcBitmap = Bitmap.createBitmap(activity.mTuneListFragment.getCurrentBitmap().copy(
                    Bitmap.Config.RGB_565, true));
            return PhotoProcessing.tunePhoto(srcBitmap, TuneListFragment.MODE,val);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
         }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        protected void onCancelled(Bitmap result) {
            super.onCancelled(result);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (dialog!=null)dialog.dismiss();
            if (result == null)
                return;
            filterBit = result;
            activity.mainImage.setImageBitmap(filterBit);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = EditImageActivity.getLoadingDialog(getActivity(),
                    R.string.applying, false);
            dialog.show();
        }

    }

}
