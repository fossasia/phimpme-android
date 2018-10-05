package org.fossasia.phimpme.editor;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.editor.fragment.AddTextFragment;
import org.fossasia.phimpme.editor.fragment.CropFragment;
import org.fossasia.phimpme.editor.fragment.FrameFragment;
import org.fossasia.phimpme.editor.fragment.MainMenuFragment;
import org.fossasia.phimpme.editor.fragment.PaintFragment;
import org.fossasia.phimpme.editor.fragment.RecyclerMenuFragment;
import org.fossasia.phimpme.editor.fragment.RotateFragment;
import org.fossasia.phimpme.editor.fragment.SliderFragment;
import org.fossasia.phimpme.editor.fragment.StickersFragment;
import org.fossasia.phimpme.editor.fragment.TwoItemFragment;
import org.fossasia.phimpme.editor.utils.BitmapUtils;
import org.fossasia.phimpme.editor.utils.FileUtil;
import org.fossasia.phimpme.editor.view.CropImageView;
import org.fossasia.phimpme.editor.view.CustomPaintView;
import org.fossasia.phimpme.editor.view.RotateImageView;
import org.fossasia.phimpme.editor.view.StickerView;
import org.fossasia.phimpme.editor.view.TextStickerView;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouch;
import org.fossasia.phimpme.editor.view.imagezoom.ImageViewTouchBase;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ColorPalette;
import org.fossasia.phimpme.share.SharingActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Called from SingleMediaActivity when the user selects the 'edit' option in the toolbar overflow menu.
 */
public class EditImageActivity extends EditBaseActivity implements View.OnClickListener, View.OnTouchListener {
    public static final String FILE_PATH = "extra_input";
    public static final String EXTRA_OUTPUT = "extra_output";
    public static final String IMAGE_IS_EDIT = "image_is_edit";


    /**
     * Different edit modes.
     */
    public static final int MODE_MAIN = 0;
    public static final int MODE_SLIDER = 1;
    public static final int MODE_FILTERS = 2;
    public static final int MODE_ENHANCE = 3;
    public static final int MODE_ADJUST = 4;
    public static final int MODE_STICKER_TYPES = 5;
    public static final int MODE_WRITE = 6;

    public static final int MODE_STICKERS = 7;
    public static final int MODE_CROP = 8;

    public static final int MODE_ROTATE = 9;
    public static final int MODE_TEXT = 10;
    public static final int MODE_PAINT = 11;
    public static final int MODE_FRAME= 12;

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

    private LoadImageTask mLoadImageTask;

    private EditImageActivity mContext;
    public Bitmap mainBitmap;
    private Bitmap originalBitmap;
    @Nullable @BindView(R.id.main_image)
    public ImageViewTouch mainImage;


    @Nullable @BindView(R.id.parentLayout)
    View parentLayout;

    @Nullable @BindView(R.id.edit_cancel)
    ImageButton cancel;
    @Nullable @BindView(R.id.edit_save)
    ImageButton save;
    @Nullable @BindView(R.id.edit_befaft)
    ImageButton bef_aft;
    @Nullable @BindView(R.id.edit_undo)
    ImageButton undo;
    @Nullable @BindView(R.id.edit_redo)
    ImageButton redo;
    @Nullable @BindView(R.id.progress_bar_edit)
    ProgressBar progressBar;


    @Nullable @BindView(R.id.sticker_panel)
    public StickerView mStickerView;// Texture layers View
    @Nullable @BindView(R.id.crop_panel)
    public CropImageView mCropPanel;// Cut operation control
    @Nullable @BindView(R.id.rotate_panel)
    public RotateImageView mRotatePanel;//Rotation operation controls
    @Nullable @BindView(R.id.text_sticker_panel)
    public TextStickerView mTextStickerView;//Text display map View
    @Nullable @BindView(R.id.custom_paint_view)
    public CustomPaintView mPaintView;//drawing paint


    private SaveImageTask mSaveImageTask;
    private int requestCode;
    private int currentShowingIndex = -1;

    public ArrayList<Bitmap> bitmapsForUndo;
    public MainMenuFragment mainMenuFragment;
    public RecyclerMenuFragment filterFragment, enhanceFragment,stickerTypesFragment;
    public StickersFragment stickersFragment;
    public SliderFragment sliderFragment;
    public TwoItemFragment writeFragment,adjustFragment;
    public AddTextFragment addTextFragment;
    public PaintFragment paintFragment;
    public CropFragment cropFragment;
    public RotateFragment rotateFragment;
    public FrameFragment frameFragment;
    private static String stickerType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        checkInitImageLoader();
        setContentView(R.layout.activity_image_edit);
        ButterKnife.bind(this);
        initView();
        if (savedInstanceState != null) {
            mode =  savedInstanceState.getInt("PREVIOUS_FRAGMENT");
        }
        getData();
    }


    /**
     * Calleter onCreate() when the activity is first started. Loads the initial default fragments.
     */
    private void setInitialFragments() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.controls_container, mainMenuFragment)
                .commit();

        changeMiddleFragment(mode);

        setButtonsVisibility();
    }

    /**
     * Gets the image to be loaded from the intent and displays this image.
     */
    private void getData() {
        if (null != getIntent() && null != getIntent().getExtras()){
            Bundle bundle = getIntent().getExtras();
            filePath = bundle.getString(FILE_PATH);
            saveFilePath = bundle.getString(EXTRA_OUTPUT);
            requestCode = bundle.getInt("requestCode", 1);
            loadImage(filePath);
            return;
        }
        SnackBarHandler.show(parentLayout,R.string.image_invalid);
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

        bitmapsForUndo = new ArrayList<>();
        progressBar.getIndeterminateDrawable()
                .setColorFilter(ColorPalette.getLighterColor(getPrimaryColor()), PorterDuff.Mode.SRC_ATOP);

        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        undo.setOnClickListener(this);
        redo.setOnClickListener(this);
        bef_aft.setOnTouchListener(this);

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
     * Get current editing mode.
     * @return the editing mode.
     */
    public static int getMode(){
        return mode;
    }

    public void changeMode(int to_mode){
        EditImageActivity.mode = to_mode;
        highLightSelectedOption(to_mode);
    }

    private void highLightSelectedOption(int mode) {
        switch (mode){
            case MODE_FILTERS:
            case MODE_ENHANCE:
            case MODE_ADJUST:
            case MODE_STICKER_TYPES:
            case MODE_FRAME:
            case MODE_WRITE:
                mainMenuFragment.highLightSelectedOption(mode);
                break;
            case MODE_STICKERS:
            case MODE_TEXT:
            case MODE_PAINT:
            case MODE_CROP:
            case MODE_ROTATE:
            case MODE_SLIDER:


        }
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
            case MODE_FRAME:
                return frameFragment=FrameFragment.newInstance(mainBitmap);
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
        if (currentShowingIndex > 0) {
            undo.setColorFilter(Color.BLACK);
            undo.setEnabled(true);
        }else {
            undo.setColorFilter(getResources().getColor(R.color.md_grey_300));
            undo.setEnabled(false);
        }
        if (currentShowingIndex + 1 < bitmapsForUndo.size()) {
            redo.setColorFilter(Color.BLACK);
            redo.setEnabled(true);
        }else {
            redo.setColorFilter(getResources().getColor(R.color.md_grey_300));
            redo.setEnabled(false);
        }


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
        addToUndoList();
        setButtonsVisibility();
        increaseOpTimes();
    }

    private void addToUndoList() {
        try{
            TODO:// implement a more efficient way, like storing only the difference of bitmaps or
            // steps followed to edit
            recycleBitmapList(++currentShowingIndex);
            bitmapsForUndo.add(mainBitmap.copy(mainBitmap.getConfig(),true));
        }catch (OutOfMemoryError error){
            /**
             * When outOfMemory exception throws then to make space, remove the last edited step
             * from list and added the new operation in the end.
             */
            bitmapsForUndo.get(1).recycle();
            bitmapsForUndo.remove(1);
            bitmapsForUndo.add(mainBitmap.copy(mainBitmap.getConfig(),true));
        }
    }

    private void recycleBitmapList(int fromIndex){
        while (fromIndex < bitmapsForUndo.size()){
            bitmapsForUndo.get(fromIndex).recycle();
            bitmapsForUndo.remove(fromIndex);
        }
    }

    private Bitmap getUndoBitmap(){
        if (currentShowingIndex - 1 >= 0)
            currentShowingIndex -= 1;
        else currentShowingIndex = 0;

        return bitmapsForUndo
                .get(currentShowingIndex)
                .copy(bitmapsForUndo.get(currentShowingIndex).getConfig(), true);
    }


    private Bitmap getRedoBitmap(){
        if (currentShowingIndex + 1 <= bitmapsForUndo.size())
            currentShowingIndex += 1;
        else currentShowingIndex = bitmapsForUndo.size() - 1;

        return bitmapsForUndo
                .get(currentShowingIndex)
                .copy(bitmapsForUndo.get(currentShowingIndex).getConfig(), true);
    }

    private void onUndoPressed() {
        if (mainBitmap != null) {
            if (!mainBitmap.isRecycled()) {
                mainBitmap.recycle();
            }
        }
        mainBitmap = getUndoBitmap();
        mainImage.setImageBitmap(mainBitmap);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        setButtonsVisibility();
    }

    private void onRedoPressed() {

        if (mainBitmap != null) {
            if (!mainBitmap.isRecycled()) {
                mainBitmap.recycle();
            }
        }
        mainBitmap = getRedoBitmap();
        mainImage.setImageBitmap(mainBitmap);
        mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        setButtonsVisibility();
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

    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Allow exit only if image has not been modified or has been modified and saved.
     * @return true if can exit, false if cannot.
     */
    public boolean canAutoExit() {
        return isBeenSaved || mOpTimes == 0;
    }

    protected void onSaveTaskDone() {
        if (mOpTimes > 0 ){
            FileUtil.albumUpdate(this, saveFilePath);
            imageSavedDialog(saveFilePath);
        }else if(mOpTimes <= 0 && requestCode == 1 ){
            imageSavedDialog(filePath);
        }else {
            final AlertDialog.Builder discardChangesDialogBuilder = new AlertDialog.Builder(EditImageActivity.this, getDialogStyle());
            AlertDialogsHelper.getTextDialog(EditImageActivity.this, discardChangesDialogBuilder, R.string.no_changes_made, R.string.exit_without_edit, null);
            discardChangesDialogBuilder.setPositiveButton(getString(R.string.confirm).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            discardChangesDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(dialog != null)
                        dialog.dismiss();
                }
            });

            AlertDialog alertDialog = discardChangesDialogBuilder.create();
            alertDialog.show();
            AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialog);
        }
    }

    /**
     * Called when the edit_save button is pressed. Used to share the image on social media.
     */
    private void shareImage(String filePath){
        Intent shareIntent = new Intent(EditImageActivity.this, SharingActivity.class);
        shareIntent.putExtra(EXTRA_OUTPUT, filePath);
        startActivity(shareIntent);
        finish();
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
            addToUndoList();
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
                SnackBarHandler.show(parentLayout,R.string.save_error);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("PREVIOUS_FRAGMENT", mode);
    }

    @Override
    public void onBackPressed() {
        switch (mode){
            //On pressing back, ask whether the user wants to discard changes or not
            case MODE_SLIDER:
                showDiscardChangesDialog(MODE_SLIDER,R.string.discard_enhance_message);
                return;
            case MODE_STICKERS:
                showDiscardChangesDialog(MODE_STICKERS,R.string.discard_stickers_message);
                return;
            case MODE_CROP:
                showDiscardChangesDialog(MODE_CROP,R.string.discard_crop_message);
                return;
            case MODE_ROTATE:
                showDiscardChangesDialog(MODE_ROTATE,R.string.discard_rotate_message);
                return;
            case MODE_TEXT:
                showDiscardChangesDialog(MODE_TEXT,R.string.discard_text_message);
                return;
            case MODE_PAINT:
                showDiscardChangesDialog(MODE_PAINT,R.string.discard_paint_message);
            case MODE_FRAME:
                if(canAutoExit())
                {finish();}
                else{
                showDiscardChangesDialog(MODE_FRAME,R.string.discard_frame_mode_message);}
                return;

        }
        //if the image has not been edited or has been edited and saved.
        if (canAutoExit()) {
            finish();
        } else {
            final AlertDialog.Builder discardChangesDialogBuilder = new AlertDialog.Builder(EditImageActivity.this, getDialogStyle());
            AlertDialogsHelper.getTextDialog(EditImageActivity.this, discardChangesDialogBuilder, R.string.discard_changes_header, R.string.exit_without_save, null);
            discardChangesDialogBuilder.setPositiveButton(getString(R.string.confirm).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            discardChangesDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(dialog != null)
                        dialog.dismiss();
                }
            });
            discardChangesDialogBuilder.setNeutralButton(getString(R.string.save_action).toUpperCase(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    doSaveImage();
                }
            });

            AlertDialog alertDialog = discardChangesDialogBuilder.create();
            alertDialog.show();
            AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL}, getAccentColor(), alertDialog);
        }
    }

    private void showDiscardChangesDialog(final int editMode, @StringRes int message){
        AlertDialog.Builder discardChangesDialogBuilder=new AlertDialog.Builder(EditImageActivity.this,getDialogStyle());
        AlertDialogsHelper.getTextDialog(EditImageActivity.this,discardChangesDialogBuilder,R.string.discard_changes_header,message,null);
        discardChangesDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
        discardChangesDialogBuilder.setPositiveButton(getString(R.string.confirm).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(editMode){
                    case MODE_SLIDER:
                        sliderFragment.backToMain();
                        break;
                    case MODE_STICKERS:
                        stickersFragment.backToMain();
                        break;
                    case MODE_CROP:
                        cropFragment.backToMain();
                        break;
                    case MODE_ROTATE:
                        rotateFragment.backToMain();
                        break;
                    case MODE_TEXT:
                        addTextFragment.backToMain();
                        break;
                    case MODE_PAINT:
                        paintFragment.backToMain();
                        break;
                    case MODE_FRAME:
                        frameFragment.backToMain();
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog alertDialog=discardChangesDialogBuilder.create();
        alertDialog.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialog);
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

        recycleBitmapList(0);
    }


    @Override
    public void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_save:
                if (mOpTimes != 0)
                    doSaveImage();
                else
                    onSaveTaskDone();
                break;
            case R.id.edit_cancel:
                onBackPressed();
                break;
            case R.id.edit_undo:
                onUndoPressed();
                break;
            case R.id.edit_redo:
                onRedoPressed();
                break;
        }
    }



    /**
     * Appears when user saves the image, asking him to share the image or not.
     * @param path - path of the image
     */
    private  void imageSavedDialog(final String path){

        final AlertDialog.Builder imageSavedDialogBuilder = new AlertDialog.Builder(EditImageActivity.this, getDialogStyle());
        AlertDialogsHelper.getTextDialog(EditImageActivity.this, imageSavedDialogBuilder, R.string.image_saved, R.string.share_image, null);
        imageSavedDialogBuilder.setPositiveButton(getString(R.string.share).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                shareImage(path);
            }
        });
        imageSavedDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null)
                    onBackPressed();

            }
        });

        AlertDialog alertDialog = imageSavedDialogBuilder.create();
        alertDialog.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialog);
    }
}