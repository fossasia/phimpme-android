package vn.mbm.phimp.me.gallery;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Gallery;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

import vn.mbm.phimp.me.utils.BasicCallBack;


/**
 * Created by manuja on 6/5/17.
 */

public class GalleryImageView extends android.support.v7.widget.AppCompatImageView {
    private Context context;

    public GalleryImageView(Context context) {
        super(context);
        this.context = context;
        Gallery.LayoutParams param = new Gallery
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(param);
        requestLayout();
    }





    public GalleryImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setImage(String path) {
        // set Image here
        Log.d("glide", path);
        Glide.with(context)
                .load(Uri.fromFile(new File(path)))
                .fitCenter()
                .into(this);

    }

}
