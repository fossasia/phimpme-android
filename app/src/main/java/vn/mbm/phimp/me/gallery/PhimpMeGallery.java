package vn.mbm.phimp.me.gallery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import vn.mbm.phimp.me.PhimpMe;
import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.Upload;
import vn.mbm.phimp.me.Utility;
import vn.mbm.phimp.me.image.CropImage;
import vn.mbm.phimp.me.utils.BasicCallBack;
import vn.mbm.phimp.me.utils.ImageUtil;
import vn.mbm.phimp.me.utils.OnSwipeTouchListener;
import vn.mbm.phimp.me.utils.geoDegrees;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import static vn.mbm.phimp.me.PhimpMe.ThemeDark;

public class PhimpMeGallery extends AppCompatActivity implements View.OnClickListener{
    private static ArrayList<String> filePath;
    public static int position; //// TODO: 6/5/17 these two variable are made static, remove them from static

    // UI elements
    private FloatingActionButton fab, fabEdit, fabUpload, fabShare, fabInfo, fabDelete;
    private Animation fabOpen, fabClose, rotateForward, rotateBackward;
	private GalleryImageAdapter galImageAdapter;
    private PopupWindow pwindo;
    private Button btnClosePopup;

    //data variable
    private Boolean isFabOpen = false;
    public int index = 0;
	private  String longtitude= "",latitude="",title="";
    ZoomDialog dialog;
    GestureDetector gestureDetector;
    BasicCallBack basicCallBack;
    private ViewPager pager;
    private OnSwipeTouchListener onSwipeTouchListener;


    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utility.getTheme(getApplicationContext()) == ThemeDark) {
            setTheme(R.style.AppTheme_Dark);
        }
        setContentView(R.layout.phimpmegallery);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        PhimpMe.gallery_delete = false;
        //num = filePath.size();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Intent intent = getIntent();
        Bundle extract = intent.getExtras();
        try {
            index = extract.getInt("index");
            PhimpMeGallery.position = index;
        } catch (Exception e) {

        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabEdit = (FloatingActionButton) findViewById(R.id.fabedit);
        fabUpload = (FloatingActionButton) findViewById(R.id.fabupload);
        fabShare = (FloatingActionButton) findViewById(R.id.fabshare);
        fabInfo = (FloatingActionButton) findViewById(R.id.fabinfo);
        fabDelete = (FloatingActionButton) findViewById(R.id.fabdelete);
        pager = (ViewPager) findViewById(R.id.gallery_pager);
        galImageAdapter = new GalleryImageAdapter(this, filePath);
        pager.setAdapter(galImageAdapter);
        pager.setCurrentItem(index);

        setListeners();

    }
    private void setListeners() {

        fab.setOnClickListener(this);
        fabEdit.setOnClickListener(this);
        fabUpload.setOnClickListener(this);
        fabShare.setOnClickListener(this);
        fabInfo.setOnClickListener(this);
        fabDelete.setOnClickListener(this);
        pager.setOnTouchListener(onSwipeTouchListener);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);


        onSwipeTouchListener = new OnSwipeTouchListener(this, false){
            @Override
            public void onDoubleTapView() {
                super.onDoubleTapView();
                openDailog();
            }
        };

        pager.setOnTouchListener(onSwipeTouchListener);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                PhimpMeGallery.position = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fabinfo:

                File file =  new File(filePath.get(position));
                LayoutInflater inflater = (LayoutInflater) PhimpMeGallery.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.info_popup,
                        (ViewGroup) findViewById(R.id.popup_element));
                pwindo = new PopupWindow(layout, AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT, true);
                getPopUpData(file);
                pwindo.setAnimationStyle(R.style.Animation);
                pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
                btnClosePopup = (Button)layout.findViewById(R.id.button_done);
                btnClosePopup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pwindo.dismiss();
                        return;
                    }
                });
                animateFAB();
                break;

            case R.id.fab:
                animateFAB();
                break;
            case R.id.fabedit:

                File f =  new File(filePath.get(position));
                ExifInterface exif_data = null;
                geoDegrees _g = null;
                String la = "";
                String lo = "";
                try
                {
                    exif_data = new ExifInterface(f.getAbsolutePath());
                    _g = new geoDegrees(exif_data);
                    if (_g.isValid())
                    {
                        la = _g.getLatitude() + "";
                        lo = _g.getLongitude() + "";
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    exif_data = null;
                    _g = null;
                }
                longtitude=lo;
                latitude=la;
                title=f.getName();

                Intent intent = new Intent();
                intent.setClass(PhimpMeGallery.this, CropImage.class);
                intent.putExtra("image-path", filePath.get(position));
                intent.putExtra("longtitude", longtitude);
                intent.putExtra("latitude", latitude);
                intent.putExtra("title", title);
                intent.putExtra("aspectX", 0);
                intent.putExtra("aspectY", 0);
                intent.putExtra("scale", true);
                intent.putExtra("activityName", "PhimpMeGallery");
                startActivity(intent);

                Log.d("", "Fab 1");
                animateFAB();

                break;
            case R.id.fabupload:
                AlertDialog.Builder builder=new AlertDialog.Builder(PhimpMeGallery.this);
                builder.setMessage(R.string.dialog_upload);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Upload.uploadGridList.add(filePath.get(position));
                    }
                });
                builder.show();
                break;
            case R.id.fabshare:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.setType("image/*");
                sendIntent.putExtra("image-path", filePath.get(position));
                sendIntent.putExtra("aspectX", 0);
                sendIntent.putExtra("aspectY", 0);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "PhimpMeTest");
                sendIntent.putExtra("scale", true);
                sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(filePath.get(position))));
                sendIntent.putExtra("activityName", "PhimpMeGallery");
                this.startActivity(sendIntent);
                animateFAB();

                break;

            case R.id.fabdelete:
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(this);
                deleteAlert.setMessage(R.string.delete_image)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    // Delete image from physical storage
                                    deleteImageFromMediaStore(filePath.get(position));
                                    // Remove from the cache
                                    PhimpMe.cache.deleteCachedFile(filePath.get(position));
                                    Upload.uploadGridList.remove(filePath.get(position));
                                    Toast.makeText(getBaseContext(), R.string.image_delete_success, Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(getBaseContext(), R.string.image_delete_fail, Toast.LENGTH_SHORT).show();
                                } finally {
                                    // Get back to gallery
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and show it
                deleteAlert.create().show();
                animateFAB();


        }

    }

    public void getPopUpData(File file){
        try {
            ExifInterface exif_data = new ExifInterface(file.getAbsolutePath());
            Date lastModDate = new Date(file.lastModified());
            long length = file.length()/1024;
            String img_width = exif_data.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
            String img_height = exif_data.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
            if("0".equals(img_width) || "0".equals(img_height)) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    ParcelFileDescriptor fd = getContentResolver().openFileDescriptor(Uri.fromFile(file), "r");
                    if (fd != null) {
                        BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);
                        img_width = options.outWidth + "";
                        img_height = options.outHeight + "";
                        fd.close();
                        options = null;
                    }
                } catch (Exception e) {
                }
            }
            ((TextView)pwindo.getContentView().findViewById(R.id.path)).setText(file.getAbsolutePath());
            ((TextView)pwindo.getContentView().findViewById(R.id.time)).setText(lastModDate.toString());
            ((TextView)pwindo.getContentView().findViewById(R.id.image_width)).setText(img_width);
            ((TextView)pwindo.getContentView().findViewById(R.id.height)).setText(img_height);
            ((TextView)pwindo.getContentView().findViewById(R.id.size)).setText(Long.toString(length)+ "KB");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotateBackward);
            fabEdit.startAnimation(fabClose);
            fabUpload.startAnimation(fabClose);
            fabShare.startAnimation(fabClose);
            fabInfo.startAnimation(fabClose);
            fabDelete.startAnimation(fabClose);

            fabEdit.setClickable(false);
            fabUpload.setClickable(false);
            fabShare.setClickable(false);
            fabInfo.setClickable(false);
            fabDelete.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotateForward);
            fabEdit.startAnimation(fabOpen);
            fabUpload.startAnimation(fabOpen);
            fabShare.startAnimation(fabOpen);
            fabInfo.startAnimation(fabOpen);
            fabDelete.startAnimation(fabOpen);
            fabDelete.setClickable(true);
            fabEdit.setClickable(true);
            fabUpload.setClickable(true);
            fabShare.setClickable(true);
            fabInfo.setClickable(true);
            isFabOpen = true;

        }
    }

    public void deleteImageFromMediaStore(String path) throws Exception {
        // Generate target
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        // Query for similar items
        String selection = "_data like ?";
        // Create a cursor to access the image
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                new String[]{path},
                null
        );
        // If there is an image under the path name,
        if (cursor != null) {
            cursor.moveToFirst();
            String id = cursor.getString(0);
            getContentResolver().delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    selection,
                    new String[]{path}
            );
            // Reset cursor to reuse it
            String[] proj = {
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.Thumbnails.IMAGE_ID,
                    MediaStore.Images.Thumbnails.DATA
            };
            // Point cursor to thumbnails
            cursor = getContentResolver().query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    proj,
                    "image_id = ?",
                    new String[]{id},
                    null
            );
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    String thumb = cursor.getString(2);
                    File f_thumb = new File(thumb);
                    if (f_thumb.exists()) f_thumb.delete();
                    getContentResolver().delete(
                            MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                            "image_id = ?",
                            new String[]{id}
                    );
                }
                cursor.close();
            }
        }
    }

    public static void setFileList(ArrayList<String> file){
        filePath = file;
    }

    public void onBackPressed(){
        super.onBackPressed();
        if(isFabOpen){
            animateFAB();
        }
    }

    private void openDailog(){
            dialog = new ZoomDialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            TouchImageView imageViewGallery = new TouchImageView(this);
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inPurgeable = true;
            try {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                int screen_w = display.getWidth();

                imageViewGallery.setImageBitmap(ImageUtil
                        .decodeSampledBitmapFromFile(PhimpMeGallery.this,
                                filePath.get(pager.getCurrentItem()),  screen_w));
                imageViewGallery.setMaxZoom(4f);
                //dialog show zoom photo
                dialog.setContentView(imageViewGallery);
                dialog.setCanceledOnTouchOutside(true);
                basicCallBack = new BasicCallBack() {
                    @Override
                    public void callBack(int status, Object data) {
                        if (status == 0) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                };
                imageViewGallery.setBasicCallBack(basicCallBack);


            } catch (Exception ex) {
                Log.e("Exception", ex.getLocalizedMessage());
            }

        if (!dialog.isShowing()) {
            dialog.show();
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                basicCallBack.callBack(0, "");
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return true;

    }

}