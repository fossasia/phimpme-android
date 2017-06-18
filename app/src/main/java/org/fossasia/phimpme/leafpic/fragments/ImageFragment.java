package org.fossasia.phimpme.leafpic.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.leafpic.activities.SingleMediaActivity;
import org.fossasia.phimpme.leafpic.data.Media;
import org.fossasia.phimpme.leafpic.util.PreferenceUtil;
import org.fossasia.phimpme.leafpic.views.RotateTransformation;

import java.util.Date;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by dnld on 18/02/16.
 */

@SuppressWarnings("ResourceType")
public class ImageFragment extends Fragment {

    private Media img;

    public static ImageFragment newInstance(Media media) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putParcelable("image", media);
        imageFragment.setArguments(args);

        return imageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        img = getArguments().getParcelable("image");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (PreferenceUtil.getInstance(getContext()).getBoolean(getString(R.string.preference_sub_scaling) , false)) {
            SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(getContext());
            imageView.setImage(ImageSource.uri(img.getUri()));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((SingleMediaActivity) getActivity()).toggleSystemUI();
                }
            });
            return imageView;
        } else {
            PhotoView photoView = new PhotoView(getContext());
            displayMedia(photoView, true);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    ((SingleMediaActivity) getActivity()).toggleSystemUI();
                }

                @Override
                public void onOutsidePhotoTap() {
                    ((SingleMediaActivity) getActivity()).toggleSystemUI();
                }
            });
            photoView.setMaximumScale(5.0F);
            photoView.setMediumScale(3.0F);

            return photoView;
        }
    }

   /* private void rotateLoop() { //april fools
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                photoView.setRotationBy(1);
                rotateLoop();
            }
        }, 5);
    }*/

    private void displayMedia(PhotoView photoView, boolean useCache) {
        //PreferenceUtil SP = PreferenceUtil.getInstance(getContext());

        Glide.with(getContext())
                .load(img.getUri())
                .asBitmap()
                .signature(useCache ? img.getSignature(): new StringSignature(new Date().getTime()+""))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .thumbnail(0.5f)
                .transform(new RotateTransformation(getContext(), img.getOrientation(), false))
                .animate(R.anim.fade_in)
                .into(photoView);

    }

    public boolean rotatePicture(int rotation) {
        // TODO: 28/08/16 not working yet
        /*PhotoView photoView = (PhotoView) getView();

        int orientation = Measure.rotateBy(img.getOrientation(), rotation);
        Log.wtf("asd", img.getOrientation()+" + "+ rotation+" = " +orientation);

        if(photoView != null && img.setOrientation(orientation)) {
            Glide.clear(photoView);
            Glide.with(getContext())
                    .load(img.getUri())
                    .asBitmap()
                    .signature(img.getSignature())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    //.thumbnail(0.5f)
                    .transform(new RotateTransformation(getContext(), rotation , true))
                    .into(photoView);

            return true;
        }*/
        return false;
    }
}