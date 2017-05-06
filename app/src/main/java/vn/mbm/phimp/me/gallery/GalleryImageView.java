package vn.mbm.phimp.me.gallery;


import android.app.Dialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.GridLayout;
import android.widget.Gallery;


import com.bumptech.glide.Glide;

import java.io.File;

import static vn.mbm.phimp.me.gallery.GalleryImageAdapter.screen_h;
import static vn.mbm.phimp.me.gallery.GalleryImageAdapter.screen_w;


/**
 * Created by manuja on 6/5/17.
 */

public class GalleryImageView extends android.support.v7.widget.AppCompatImageView {
    private Context context;
    ScaleGestureDetector scaleGestureDetector;
    private String path;
    ZoomDialog dialog;

    public GalleryImageView(Context context) {
        super(context);
        this.context=context;
        Gallery.LayoutParams param = new Gallery
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(param);
        requestLayout();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public GalleryImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(String path){
        // set Image here
        this.path=path;
        Log.d("glide", path);
        Glide.with(context)
                .load(Uri.fromFile(new File(path)))
                .centerCrop()
                .crossFade()
                .into(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);
        return true;
    }


    private class ScaleListener extends ScaleGestureDetector.
            SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (dialog != null){
                dialog = new ZoomDialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
                TouchImageView imageViewGallery = new TouchImageView(context);
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inPurgeable = true;
                try
                {
                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    Display display = wm.getDefaultDisplay();
                    @SuppressWarnings("deprecation")
                    int screen_w = display.getWidth();
                    imageViewGallery.setImageBitmap(GalleryImageAdapter
                            .decodeSampledBitmapFromFile(context, path,  screen_w));
                    imageViewGallery.setMaxZoom(4f);

                    //dialog show zoom photo
                    dialog.setContentView(imageViewGallery);
                    dialog.setCanceledOnTouchOutside(true);
                }
                catch (Exception ex)
                {
                    Log.e("Exception", ex.getLocalizedMessage());
                }
            }

            if (!dialog.isShowing()){
                dialog.show();
            }





            Log.d("scale","");
            return false;
        }
    }

}
