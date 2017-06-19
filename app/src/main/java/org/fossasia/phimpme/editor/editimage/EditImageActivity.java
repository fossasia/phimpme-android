package org.fossasia.phimpme.editor.editimage;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.EditBaseActivity;
import org.fossasia.phimpme.editor.editimage.fragment.AddTextFragment;
import org.fossasia.phimpme.editor.editimage.fragment.CropFragment;
import org.fossasia.phimpme.editor.editimage.fragment.MainMenuFragment;
import org.fossasia.phimpme.editor.editimage.fragment.PaintFragment;
import org.fossasia.phimpme.editor.editimage.fragment.RecyclerMenuFragment;
import org.fossasia.phimpme.editor.editimage.fragment.RotateFragment;
import org.fossasia.phimpme.editor.editimage.fragment.SliderFragment;
import org.fossasia.phimpme.editor.editimage.fragment.StickersFragment;
import org.fossasia.phimpme.editor.editimage.fragment.TwoItemFragment;
import org.fossasia.phimpme.editor.editimage.utils.BitmapUtils;
import org.fossasia.phimpme.editor.editimage.utils.FileUtil;
import org.fossasia.phimpme.editor.editimage.view.CropImageView;
import org.fossasia.phimpme.editor.editimage.view.CustomPaintView;
import org.fossasia.phimpme.editor.editimage.view.RotateImageView;
import org.fossasia.phimpme.editor.editimage.view.StickerView;
import org.fossasia.phimpme.editor.editimage.view.TextStickerView;
import org.fossasia.phimpme.editor.editimage.view.imagezoom.ImageViewTouch;
import org.fossasia.phimpme.editor.editimage.view.imagezoom.ImageViewTouchBase;
import org.fossasia.phimpme.leafpic.activities.SingleMediaActivity;
import org.fossasia.phimpme.leafpic.util.ThemeHelper;
import org.fossasia.phimpme.shareActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Called from SingleMediaActivity when the user selects the 'edit' option in the toolbar overflow menu.
 */
public class EditImageActivity extends EditBaseActivity implements View.OnClickListener, View.OnTouchListener {
    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String SAVE_FILE_PATH = "save_file_path";
    public static final String IMAGE_IS_EDIT = "image_is_edit";

    /**
     * Different edit modes.
     */
    public static final int MODE_MAIN = 0;
    public static final int MODE_SLIDER = 1;
    public static final int MODE_FILTERS = 2;
    public static final int MODE_ENHANCE = 3;
    public static final int MODE_STICKER_TYPES = 4;
    public static final int MODE_STICKERS = 5;
    public static final int MODE_ADJUST = 6;
    public static final int MODE_CROP = 7;
    public static final int MODE_ROTATE = 8;
    public static final int MODE_WRITE = 9;
    public static final int MODE_TEXT = 10;
    public static final int MODE_PAINT = 11;

    public String filePath;
    public String saveFilePath;
    private int imageWidth, imageHeight;

    public static int mode;
    public static int effectType;

    /**
     * Number of times image has been edited. Indicates whether image has been edited or not.
     */
    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;

    LoadImageTask mLoadImageTask;

    private EditImageActivity mContext;
    public Bitmap mainBitmap;
    private Bitmap originalBitmap;
    public ImageViewTouch mainImage;
    private View cancel,save,bef_aft;

    public StickerView mStickerView;// Texture layers View
    public CropImageView mCropPanel;// Cut operation control
    public RotateImageView mRotatePanel;//Rotation operation controls
    public TextStickerView mTextStickerView;//Text display map View
    public CustomPaintView mPaintView;//drawing paint


    private SaveImageTask mSaveImageTask;
    private int requestCode;
    final String REVIEW_ACTION = "com.android.camera.action.REVIEW";


    public ThemeHelper themeHelper;
    public MainMenuFragment mainMenuFragment;
    public RecyclerMenuFragment filterFragment, enhanceFragment,stickerTypesFragment;
    public StickersFragment stickersFragment;
    public SliderFragment sliderFragment;
    public TwoItemFragment writeFragment,adjustFragment;
    public AddTextFragment addTextFragment;
    public PaintFragment paintFragment;
    public CropFragment cropFragment;
    public RotateFragment rotateFragment;
    private static String stickerType;

    /**
     * @param context
     * @param editImagePath
     * @param outputPath
     * @param requestCode
     */
    public static void start(Activity context, final String editImagePath, final String outputPath, final int requestCode) {
        if (TextUtils.isEmpty(editImagePath)) {
            Toast.makeText(context, R.string.no_choose, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent it = new Intent(context, EditImageActivity.class);
        it.putExtra(EditImageActivity.FILE_PATH, editImagePath);
        it.putExtra(EditImageActivity.EXTRA_OUTPUT, outputPath);
        it.putExtra("requestCode",requestCode);
        context.startActivityForResult(it, requestCode);
        context.finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        checkInitImageLoader();
        setContentView(R.layout.activity_image_edit);
        initView();
        getData();
        requestCode = getIntent().getIntExtra("requestCode", 1);

//        setInitialFragments();
    }

    /**
     * Called after onCreate() when the activity is first started. Loads the initial default fragments.
     */
    private void setInitialFragments() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.controls_container, mainMenuFragment)
                .commit();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.preview_container, filterFragment)
                .commit();
    }

    /**
     * Gets the image to be loaded from the intent and displays this image.
     */
    private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        loadImage(filePath);
    }

    /**
     * Called from onCreate().
     * Initializes all view objects and fragments to be used.
     */
    private void initView() {
        mContext = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;

        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
        cancel = findViewById(R.id.edit_cancel);
        save = findViewById(R.id.edit_save);
        bef_aft = findViewById(R.id.edit_befaft);

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        bef_aft.setOnTouchListener(this);

        mStickerView = (StickerView) findViewById(R.id.sticker_panel);
        mCropPanel = (CropImageView) findViewById(R.id.crop_panel);
        mRotatePanel = (RotateImageView) findViewById(R.id.rotate_panel);
        mTextStickerView = (TextStickerView) findViewById(R.id.text_sticker_panel);
        mPaintView = (CustomPaintView) findViewById(R.id.custom_paint_view);

        mode = MODE_FILTERS;

        mainMenuFragment = MainMenuFragment.newInstance();
        sliderFragment = SliderFragment.newInstance();
        filterFragment = RecyclerMenuFragment.newInstance(MODE_FILTERS);
        enhanceFragment = RecyclerMenuFragment.newInstance(MODE_ENHANCE);
        stickerTypesFragment = RecyclerMenuFragment.newInstance(MODE_STICKER_TYPES);
        adjustFragment = TwoItemFragment.newInstance(MODE_ADJUST);
        writeFragment = TwoItemFragment.newInstance(MODE_WRITE);
        addTextFragment = AddTextFragment.newInstance();
        paintFragment = PaintFragment.newInstance();
        cropFragment = CropFragment.newInstance();
        rotateFragment = RotateFragment.newInstance();
    }

    /**
     * Called when the edit_save button is pressed. Used to share the image on social media.
     */
    private void shareImage() {
        Intent shareIntent = new Intent(EditImageActivity.this, shareActivity.class);
        if(mOpTimes>0) {
            shareIntent.putExtra(EXTRA_OUTPUT, saveFilePath);
            shareIntent.putExtra(IMAGE_IS_EDIT, mOpTimes > 0);
        }
        else {
            shareIntent.putExtra(EXTRA_OUTPUT, filePath);
        }
        FileUtil.ablumUpdate(this, saveFilePath);
        setResult(RESULT_OK, shareIntent);
        startActivity(shareIntent);
        finish();
    }

    /**
     * Get current editing mode.
     * @return the editing mode.
     */
    public int getMode(){
        return mode;
    }

    /**
     * Get the fragment corresponding to current editing mode.
     * @param index integer corresponding to editing mode.
     * @return Fragment of current editing mode.
     */
    public Fragment getFragment(int index){
        switch (index){
            case MODE_MAIN:
                return mainMenuFragment;
            case MODE_SLIDER:
                sliderFragment = SliderFragment.newInstance();
                return sliderFragment;
            case MODE_FILTERS:
                return filterFragment;
            case MODE_ENHANCE:
                return enhanceFragment;
            case MODE_STICKER_TYPES:
                return stickerTypesFragment;
            case MODE_STICKERS:
                stickersFragment = StickersFragment.newInstance(addStickerImages(stickerType));
                return stickersFragment;
            case MODE_WRITE:
                return writeFragment;
            case MODE_ADJUST:
                return adjustFragment;
            case MODE_TEXT:
                return addTextFragment;
            case MODE_PAINT:
                return paintFragment;
            case MODE_CROP:
                return cropFragment;
            case MODE_ROTATE:
                return rotateFragment;
        }
        return mainMenuFragment;
    }

    /**
     * Called when a particular option in the preview_container is selected. It reassigns
     * the controls_container. It displays options and tools for the selected editing mode.
     * @param index integer corresponding to the current editing mode.
     */
    public void changeBottomFragment(int index){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.controls_container, getFragment(index))
                .commit();

        setButtonsVisibility();
    }

    /**
     * Handles the visibility of the 'save' button.
     */
    private void setButtonsVisibility() {
        save.setVisibility(View.VISIBLE);
        bef_aft.setVisibility(View.VISIBLE);
        switch (mode){
            case MODE_STICKERS:
            case MODE_CROP:
            case MODE_ROTATE:
            case MODE_TEXT:
            case MODE_PAINT:
                save.setVisibility(View.INVISIBLE);
                bef_aft.setVisibility(View.INVISIBLE);
                break;
            case MODE_SLIDER:
                save.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void setEffectType(int type, int mode){
        effectType = 100 * mode + type;
    }

    /**
     * Is called when an editing mode is selected in the control_container. Reassigns the
     * preview_container according to the editing mode selected.
     * @param index integer representing selected editing mode
     */
    public void changeMiddleFragment(int index){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.preview_container, getFragment(index))
                    .commit();
    }

    public void changeMainBitmap(Bitmap newBit) {
        if (mainBitmap != null) {
            if (!mainBitmap.isRecycled()) {
                mainBitmap.recycle();
            }
        }
        mainBitmap = newBit;
        mainImage.setImageBitmap(mainBitmap);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        increaseOpTimes();
    }

    /**
     * Load the image from filepath into mainImage imageView.
     * @param filepath The image to be loaded.
     */
    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);

        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    protected void doSaveImage() {
        if (mOpTimes <= 0)
            shareImage();


        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        mSaveImageTask = new SaveImageTask();
        mSaveImageTask.execute(mainBitmap);
    }

    //Increment no. of times the image has been edited
    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }

    public void resetOpTimes() {
        isBeenSaved = true;
    }

    /**
     * Allow exit only if image has not been modified or has been modified and saved.
     * @return true if can exit, false if cannot.
     */
    public boolean canAutoExit() {
        return isBeenSaved || mOpTimes == 0;
    }

    protected void onSaveTaskDone() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FILE_PATH, filePath);
        returnIntent.putExtra(EXTRA_OUTPUT, saveFilePath);
        returnIntent.putExtra(IMAGE_IS_EDIT, mOpTimes > 0);

        FileUtil.ablumUpdate(this, saveFilePath);
        setResult(RESULT_OK, returnIntent);
        if(requestCode == 1 && mOpTimes<=0) {   //Checks if this Activity was started by PhotoActivity
            Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(filePath)));
            intent.setClass(getApplicationContext(), SingleMediaActivity.class);
            //shareImage();
            startActivity(intent);
            finish();
        }
        else if(mOpTimes>0) {
            Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(saveFilePath)));
            intent.setClass(getApplicationContext(), SingleMediaActivity.class);
            //shareImage();
            startActivity(intent);
            finish();
        }
    }

    private ArrayList<String> addStickerImages(String folderPath) {
        ArrayList<String> pathList = new ArrayList<>();
        try {
            String[] files = getAssets().list(folderPath);

            for (String name : files) {
                pathList.add(folderPath + File.separator + name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pathList;
    }

    public void setStickerType(String stickerType) {
        EditImageActivity.stickerType = stickerType;
    }

    public String getStickerType(){
        return stickerType;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (R.id.edit_befaft == v.getId()){
            if (MotionEvent.ACTION_DOWN == event.getAction()){
                switch (mode){
                    case MODE_SLIDER:
                        mainImage.setImageBitmap(mainBitmap);
                        break;
                    default:
                        mainImage.setImageBitmap(originalBitmap);
                }
            }else if (MotionEvent.ACTION_UP == event.getAction()){
                switch (mode){
                    case MODE_SLIDER:
                        mainImage.setImageBitmap(sliderFragment.filterBit);
                        break;
                    default:
                        mainImage.setImageBitmap(mainBitmap);
                }
            }
        }
        return true;
    }

    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {

            return BitmapUtils.getSampledBitmap(params[0], imageWidth,
                    imageHeight);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (mainBitmap != null) {
                mainBitmap.recycle();
                mainBitmap = null;
                System.gc();
            }
            mainBitmap = result;
            mainImage.setImageBitmap(result);
            mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
            originalBitmap = mainBitmap.copy(mainBitmap.getConfig(),true);
            setInitialFragments();
        }
    }

    private final class SaveImageTask extends AsyncTask<Bitmap, Void, Boolean> {
        private Dialog dialog;

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            if (TextUtils.isEmpty(saveFilePath))
                return false;

            return BitmapUtils.saveBitmap(params[0], saveFilePath);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();
        }

        @Override
        protected void onCancelled(Boolean result) {
            super.onCancelled(result);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = getLoadingDialog(mContext, R.string.saving_image, false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result) {
                resetOpTimes();
                onSaveTaskDone();
            } else {
                Toast.makeText(mContext, R.string.save_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        switch (mode){
            case MODE_SLIDER:
                sliderFragment.backToMain();
                return;
            case MODE_STICKERS:
                stickersFragment.backToMain();
                return;
            case MODE_CROP:
                cropFragment.backToMain();
                return;
            case MODE_ROTATE:
                rotateFragment.backToMain();
                return;
            case MODE_TEXT:
                addTextFragment.backToMain();
                return;
            case MODE_PAINT:
                paintFragment.backToMain();
                return;
        }

        //if the image has not been edited or has been edited and saved.
        if (canAutoExit()) {
            finish();
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(R.string.exit_without_save)
                    .setCancelable(false).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mContext.finish();
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (originalBitmap != null && !originalBitmap.isRecycled()) {
            originalBitmap.recycle();
            originalBitmap = null;
        }
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);
        }

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_save:
                if (mOpTimes == 0) {//Does not modify the image
                    onSaveTaskDone();
                    //shareImage();
                } else {
                    doSaveImage();
                }
                break;
            case R.id.edit_cancel:
                onBackPressed();
                break;
        }
    }

}