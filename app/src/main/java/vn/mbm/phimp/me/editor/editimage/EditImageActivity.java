package vn.mbm.phimp.me.editor.editimage;

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
import android.view.View;
import android.widget.Toast;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.editor.EditBaseActivity;
import vn.mbm.phimp.me.editor.editimage.fragment.AddTextFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.CropFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.MainMenuFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.PaintFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.RecyclerMenuFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.RotateFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.SliderFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.StickersFragment;
import vn.mbm.phimp.me.editor.editimage.fragment.TwoItemFragment;
import vn.mbm.phimp.me.editor.editimage.utils.BitmapUtils;
import vn.mbm.phimp.me.editor.editimage.utils.FileUtil;
import vn.mbm.phimp.me.editor.editimage.view.CropImageView;
import vn.mbm.phimp.me.editor.editimage.view.CustomPaintView;
import vn.mbm.phimp.me.editor.editimage.view.RotateImageView;
import vn.mbm.phimp.me.editor.editimage.view.StickerView;
import vn.mbm.phimp.me.editor.editimage.view.TextStickerView;
import vn.mbm.phimp.me.editor.editimage.view.imagezoom.ImageViewTouch;
import vn.mbm.phimp.me.editor.editimage.view.imagezoom.ImageViewTouchBase;
import vn.mbm.phimp.me.leafpic.activities.SingleMediaActivity;
import vn.mbm.phimp.me.leafpic.util.ThemeHelper;

public class EditImageActivity extends EditBaseActivity implements View.OnClickListener {
    public static final String FILE_PATH = "file_path";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String SAVE_FILE_PATH = "save_file_path";

    public static final String IMAGE_IS_EDIT = "image_is_edit";

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

    protected int mOpTimes = 0;
    protected boolean isBeenSaved = false;

    LoadImageTask mLoadImageTask;

    private EditImageActivity mContext;
    public Bitmap mainBitmap;
    public ImageViewTouch mainImage;
    private View cancel,save;

    public StickerView mStickerView;// 贴图层View
    public CropImageView mCropPanel;// 剪切操作控件
    public RotateImageView mRotatePanel;// 旋转操作控件
    public TextStickerView mTextStickerView;//文本贴图显示View
    public CustomPaintView mPaintView;//涂鸦模式画板


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

    private void getData() {
        filePath = getIntent().getStringExtra(FILE_PATH);
        saveFilePath = getIntent().getStringExtra(EXTRA_OUTPUT);
        loadImage(filePath);
    }

    private void initView() {
        mContext = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels / 2;
        imageHeight = metrics.heightPixels / 2;

        mainImage = (ImageViewTouch) findViewById(R.id.main_image);
        cancel = findViewById(R.id.edit_cancel);
        save = findViewById(R.id.edit_save);

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

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

    public int getMode(){
        return mode;
    }

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

    public void changeBottomFragment(int index){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.controls_container, getFragment(index))
                .commit();

        setButtonsVisibility();
    }

    private void setButtonsVisibility() {
    //    cancel.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        switch (mode){
            case MODE_SLIDER:
            case MODE_STICKERS:
            case MODE_CROP:
            case MODE_ROTATE:
            case MODE_TEXT:
            case MODE_PAINT:
      //          cancel.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);
        }
    }

    public void setEffectType(int type, int mode){
        effectType = 100 * mode + type;
    }

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

    public void loadImage(String filepath) {
        if (mLoadImageTask != null) {
            mLoadImageTask.cancel(true);

        }
        mLoadImageTask = new LoadImageTask();
        mLoadImageTask.execute(filepath);
    }

    protected void doSaveImage() {
        if (mOpTimes <= 0)
            return;

        if (mSaveImageTask != null) {
            mSaveImageTask.cancel(true);
        }

        mSaveImageTask = new SaveImageTask();
        mSaveImageTask.execute(mainBitmap);
    }

    public void increaseOpTimes() {
        mOpTimes++;
        isBeenSaved = false;
    }

    public void resetOpTimes() {
        isBeenSaved = true;
    }

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
            startActivity(intent);
            finish();
        }
        else if(mOpTimes>0) {
            Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(saveFilePath)));
            intent.setClass(getApplicationContext(), SingleMediaActivity.class);
            startActivity(intent);
            finish();
        }
        else {
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

        if (canAutoExit()) {
            onSaveTaskDone();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_save:
                if (mOpTimes == 0) {//并未修改图片
                    onSaveTaskDone();
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