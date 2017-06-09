package vn.mbm.phimp.me.leafpic.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import vn.mbm.phimp.me.R;
import vn.mbm.phimp.me.base.SharedMediaActivity;
import vn.mbm.phimp.me.editor.FileUtils;
import vn.mbm.phimp.me.editor.editimage.EditImageActivity;
import vn.mbm.phimp.me.editor.editimage.utils.BitmapUtils;
import vn.mbm.phimp.me.leafpic.SelectAlbumBottomSheet;
import vn.mbm.phimp.me.leafpic.adapters.MediaPagerAdapter;
import vn.mbm.phimp.me.leafpic.animations.DepthPageTransformer;
import vn.mbm.phimp.me.leafpic.data.Album;
import vn.mbm.phimp.me.leafpic.data.base.SortingMode;
import vn.mbm.phimp.me.leafpic.data.base.SortingOrder;
import vn.mbm.phimp.me.leafpic.util.AlertDialogsHelper;
import vn.mbm.phimp.me.leafpic.util.ColorPalette;
import vn.mbm.phimp.me.leafpic.util.ContentHelper;
import vn.mbm.phimp.me.leafpic.util.Measure;
import vn.mbm.phimp.me.leafpic.util.PreferenceUtil;
import vn.mbm.phimp.me.leafpic.util.SecurityHelper;
import vn.mbm.phimp.me.leafpic.util.StringUtils;
import vn.mbm.phimp.me.leafpic.views.HackyViewPager;
import vn.mbm.phimp.me.utilities.ActivitySwitchHelper;

/**
 * Created by dnld on 18/02/16.
 */
@SuppressWarnings("ResourceAsColor")
public class SingleMediaActivity extends SharedMediaActivity {

    private static final String ISLOCKED_ARG = "isLocked";
    static final String ACTION_OPEN_ALBUM = "android.intent.action.pagerAlbumMedia";
    private static final String ACTION_REVIEW = "com.android.camera.action.REVIEW";

    private HackyViewPager mViewPager;
    private MediaPagerAdapter adapter;
    private PreferenceUtil SP;
    private RelativeLayout ActivityBackground;
    private SelectAlbumBottomSheet bottomSheetDialogFragment;
    private SecurityHelper securityObj;
    private Toolbar toolbar;
    private boolean fullScreenMode, customUri = false;
    public static final int REQUEST_PERMISSON_SORAGE = 1;
    public static final int REQUEST_PERMISSON_CAMERA = 2;
    public static final int SELECT_GALLERY_IMAGE_CODE = 7;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    public static final int ACTION_STICKERS_IMAGE = 10;
    private ImageView imgView;
    private View openAblum;
    private View editImage;//
    private Bitmap mainBitmap;
    private int imageWidth, imageHeight;//
    private String path;
    private SingleMediaActivity context;


    private View mTakenPhoto;//拍摄照片用于编辑
    private Uri photoURI = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        initView();

        SP = PreferenceUtil.getInstance(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (HackyViewPager) findViewById(R.id.photos_pager);
        securityObj= new SecurityHelper(SingleMediaActivity.this);

        if (savedInstanceState != null)
            mViewPager.setLocked(savedInstanceState.getBoolean(ISLOCKED_ARG, false));
        try
        {
            Album album;
            if ((getIntent().getAction().equals(Intent.ACTION_VIEW) || getIntent().getAction().equals(ACTION_REVIEW)) && getIntent().getData() != null) {

                String path = ContentHelper.getMediaPath(getApplicationContext(), getIntent().getData());

                File file = null;
                if (path != null)
                    file = new File(path);

                if (file != null && file.isFile())
                    //the image is stored in the storage
                    album = new Album(getApplicationContext(), file);
                else {
                    //try to show with Uri
                    album = new Album(getApplicationContext(), getIntent().getData());
                    customUri = true;
                }
                getAlbums().addAlbum(0, album);
            }
            initUI();
            setupUI();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void initView() {
        context = this;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        imgView = (ImageView) findViewById(R.id.img);
    }

    private void initUI() {

        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        toolbar.setNavigationIcon(getToolbarIcon(CommunityMaterial.Icon.cmd_arrow_left));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setRecentApp(getString(R.string.app_name));
        setupSystemUI();

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) showSystemUI();
                        else hideSystemUI();
                    }
                });
        adapter = new MediaPagerAdapter(getSupportFragmentManager(), getAlbum().getMedia());

        adapter.setVideoOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SP.getBoolean("set_internal_player", false)) {
                    Intent mpdIntent = new Intent(SingleMediaActivity.this, PlayerActivity.class)
                            .setData(getAlbum().getCurrentMedia().getUri());
                    startActivity(mpdIntent);
                } else {
                    Intent intentOpenWith = new Intent(Intent.ACTION_VIEW);
                    intentOpenWith.setDataAndType(
                            getAlbum().getMedia().get(mViewPager.getCurrentItem()).getUri(),
                            getAlbum().getMedia().get(mViewPager.getCurrentItem()).getMimeType());
                    startActivity(intentOpenWith);
                }
            }
        });

        getSupportActionBar().setTitle((getAlbum().getCurrentMediaIndex() + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());

        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(getAlbum().getCurrentMediaIndex());
        mViewPager.setPageTransformer(true, new DepthPageTransformer());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                getAlbum().setCurrentPhotoIndex(position);
                toolbar.setTitle((position + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());
                invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        Display aa = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (aa.getRotation() == Surface.ROTATION_90) {
            Configuration configuration = new Configuration();
            configuration.orientation = Configuration.ORIENTATION_LANDSCAPE;
            onConfigurationChanged(configuration);
        }

    }

    private void setupUI() {

        /**** Theme ****/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(
                isApplyThemeOnImgAct()
                        ? ColorPalette.getTransparentColor (getPrimaryColor(), getTransparency())
                        : ColorPalette.getTransparentColor(getDefaultThemeToolbarColor3th(), 175));

        toolbar.setPopupTheme(getPopupToolbarStyle());

        ActivityBackground = (RelativeLayout) findViewById(R.id.PhotoPager_Layout);
        ActivityBackground.setBackgroundColor(getBackgroundColor());

        setStatusBarColor();
        setNavBarColor();

        securityObj.updateSecuritySetting();

        /**** SETTINGS ****/

        if (SP.getBoolean("set_max_luminosity", false))
            updateBrightness(1.0F);
        else try {
            float brightness = Settings.System.getInt(
                    getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            brightness = brightness == 1.0F ? 255.0F : brightness;
            updateBrightness(brightness);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        if (SP.getBoolean("set_picture_orientation", false))
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        else setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
    }


    @Override
    public void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
        setupUI();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Glide.get(getApplicationContext()).clearMemory();
        Glide.get(getApplicationContext()).trimMemory(TRIM_MEMORY_COMPLETE);
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_pager, menu);

        menu.findItem(R.id.action_delete).setIcon(getToolbarIcon(CommunityMaterial.Icon.cmd_delete));
        menu.findItem(R.id.action_share).setIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_share));

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            params.setMargins(0,0,Measure.getNavigationBarSize(SingleMediaActivity.this).x,0);
        else
            params.setMargins(0,0,0,0);

        toolbar.setLayoutParams(params);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        menu.setGroupVisible(R.id.only_photos_options, !getAlbum().getCurrentMedia().isVideo());

        if (customUri) {
            menu.setGroupVisible(R.id.on_internal_storage, false);
            menu.setGroupVisible(R.id.only_photos_options, false);
            menu.findItem(R.id.sort_action).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    final Uri imageUri = UCrop.getOutput(data);
                    if (imageUri != null && imageUri.getScheme().equals("file")) {
                        try {
                            //copyFileToDownloads(imageUri);
                            // TODO: 21/08/16 handle this better
                            handleEditorImage(data);
                            if(ContentHelper.copyFile(getApplicationContext(), new File(imageUri.getPath()), new File(getAlbum().getPath()))) {
                                //((ImageFragment) adapter.getRegisteredFragment(getAlbum().getCurrentMediaIndex())).displayMedia(true);
                                Toast.makeText(this, R.string.new_file_created, Toast.LENGTH_SHORT).show();
                            }
                            //adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e("ERROS - uCrop", imageUri.toString(), e);
                        }
                    } else
                        StringUtils.showToast(getApplicationContext(), "errori random");
                    break;
                default:
                    break;
            }
        }
    }

    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (isImageEdit){

        }else{//未编辑  还是用原来的图片
            newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);;
        }
        //System.out.println("newFilePath---->" + newFilePath);
        //File file = new File(newFilePath);
        //System.out.println("newFilePath size ---->" + (file.length() / 1024)+"KB");
        Log.d("image is edit", isImageEdit + "");
        LoadImageTask loadTask = new LoadImageTask();
        loadTask.execute(newFilePath);
    }

    private void startLoadTask() {
        LoadImageTask task = new LoadImageTask();
        task.execute(path);
    }


    private void displayAlbums(boolean reload) {
        Intent i = new Intent(SingleMediaActivity.this, LFMainActivity.class);
        Bundle b = new Bundle();
        b.putInt(SplashScreen.CONTENT, SplashScreen.ALBUMS_PREFETCHED);
        if (!reload) i.putExtras(b);
        startActivity(i);
        finish();
    }

    private void deleteCurrentMedia() {
        getAlbum().deleteCurrentMedia(getApplicationContext());
        if (getAlbum().getMedia().size() == 0) {
            if (customUri) finish();
            else {
                getAlbums().removeCurrentAlbum();
                displayAlbums(false);
            }
        }
        adapter.notifyDataSetChanged();
        toolbar.setTitle((mViewPager.getCurrentItem() + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_copy:
                bottomSheetDialogFragment = new SelectAlbumBottomSheet();
                bottomSheetDialogFragment.setTitle(getString(R.string.copy_to));
                bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
                    @Override
                    public void folderSelected(String path) {
                        getAlbum().copyPhoto(getApplicationContext(), getAlbum().getCurrentMedia().getPath(), path);
                        bottomSheetDialogFragment.dismiss();
                    }
                });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                break;

            case R.id.name_sort_action:
                getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.NAME);
                getAlbum().sortPhotos();
                adapter.swapDataSet(getAlbum().getMedia());
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_action:
                getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.DATE);
                getAlbum().sortPhotos();
                adapter.swapDataSet(getAlbum().getMedia());
                item.setChecked(true);
                return true;

            case R.id.size_sort_action:
                getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.SIZE);
                getAlbum().sortPhotos();
                adapter.swapDataSet(getAlbum().getMedia());
                item.setChecked(true);
                return true;

            case R.id.type_sort_action:
                getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.TYPE);
                getAlbum().sortPhotos();
                adapter.swapDataSet(getAlbum().getMedia());
                item.setChecked(true);
                return true;

            case R.id.numeric_sort_action:
                getAlbum().setDefaultSortingMode(getApplicationContext(), SortingMode.NUMERIC);
                getAlbum().sortPhotos();
                adapter.swapDataSet(getAlbum().getMedia());
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_action:
                getAlbum().setDefaultSortingAscending(getApplicationContext(), !item.isChecked() ? SortingOrder.ASCENDING : SortingOrder.DESCENDING);
                getAlbum().sortPhotos();
                adapter.swapDataSet(getAlbum().getMedia());

                item.setChecked(!item.isChecked());
                return true;


            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType(getAlbum().getCurrentMedia().getMimeType());
                share.putExtra(Intent.EXTRA_STREAM, getAlbum().getCurrentMedia().getUri());
                startActivity(Intent.createChooser(share, getString(R.string.send_to)));
                return true;

            case R.id.action_edit:
                Uri mDestinationUri = Uri.fromFile(new File(getCacheDir(), "croppedImage.png"));
                /*Uri uri = Uri.fromFile(new File(getAlbum().getCurrentMedia().getPath()));
                /*UCrop uCrop = UCrop.of(uri, mDestinationUri);
                uCrop.withOptions(getUcropOptions());
                uCrop.start(SingleMediaActivity.this);*/
                File outputFile = FileUtils.genEditFile();
                Uri uri = Uri.fromFile(new File(getAlbum().getCurrentMedia().getPath()));
                EditImageActivity.start(this,uri.getPath(),outputFile.getAbsolutePath(),ACTION_REQUEST_EDITIMAGE);
                break;

            case R.id.action_use_as:
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.setDataAndType(
                        getAlbum().getCurrentMedia().getUri(), getAlbum().getCurrentMedia().getMimeType());
                startActivity(Intent.createChooser(intent, getString(R.string.use_as)));
                return true;

            case R.id.action_delete:
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(SingleMediaActivity.this, getDialogStyle());

                AlertDialogsHelper.getTextDialog(SingleMediaActivity.this,deleteDialog,
                        R.string.delete, R.string.delete_photo_message);

                deleteDialog.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);
                deleteDialog.setPositiveButton(this.getString(R.string.delete).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (securityObj.isActiveSecurity()&&securityObj.isPasswordOnDelete()) {

                            final AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(SingleMediaActivity.this, getDialogStyle());
                            final EditText editTextPassword = securityObj.getInsertPasswordDialog
                                    (SingleMediaActivity.this, passwordDialogBuilder);

                            passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                        deleteCurrentMedia();
                                    } else
                                        Toast.makeText(passwordDialogBuilder.getContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();

                                }
                            });
                            passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                            final AlertDialog passwordDialog = passwordDialogBuilder.create();
                            passwordDialog.show();
                            passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View
                                    .OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (securityObj.checkPassword(editTextPassword.getText().toString())){
                                        deleteCurrentMedia();
                                        passwordDialog.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_SHORT).show();
                                        editTextPassword.getText().clear();
                                        editTextPassword.requestFocus();
                                    }
                                }
                            });
                        } else
                            deleteCurrentMedia();
                    }
                });
                deleteDialog.show();
                return true;

            case R.id.action_move:
                bottomSheetDialogFragment = new SelectAlbumBottomSheet();
                bottomSheetDialogFragment.setTitle(getString(R.string.move_to));
                bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
                    @Override
                    public void folderSelected(String path) {
                        getAlbum().moveCurrentMedia(getApplicationContext(), path);

                        if (getAlbum().getMedia().size() == 0) {
                            if (customUri) finish();
                            else {
                                getAlbums().removeCurrentAlbum();
                                //((MyApplication) getApplicationContext()).removeCurrentAlbum();
                                displayAlbums(false);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        toolbar.setTitle((mViewPager.getCurrentItem() + 1) + " " + getString(R.string.of) + " " + getAlbum().getCount());
                        bottomSheetDialogFragment.dismiss();
                    }
                });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                return true;

            case R.id.action_rename:
                AlertDialog.Builder renameDialogBuilder = new AlertDialog.Builder(SingleMediaActivity.this, getDialogStyle());
                final EditText editTextNewName = new EditText(getApplicationContext());
                editTextNewName.setText(StringUtils.getPhotoNameByPath(getAlbum().getCurrentMedia().getPath()));

                AlertDialog renameDialog =
                        AlertDialogsHelper.getInsertTextDialog(
                                this,renameDialogBuilder, editTextNewName, R.string.rename_photo_action);

                renameDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editTextNewName.length() != 0)
                            getAlbum().renameCurrentMedia(getApplicationContext(), editTextNewName.getText().toString());
                        else
                            StringUtils.showToast(getApplicationContext(), getString(R.string.nothing_changed));
                    }});
                renameDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { } });
                renameDialog.show();
                break;

            case R.id.action_details:
                AlertDialog.Builder detailsDialogBuilder = new AlertDialog.Builder(SingleMediaActivity.this, getDialogStyle());
                final AlertDialog detailsDialog =
                        AlertDialogsHelper.getDetailsDialog(this, detailsDialogBuilder,getAlbum().getCurrentMedia());

                detailsDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string
                        .ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }});
                detailsDialog.show();
                break;

            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                //return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    private void updateBrightness(float level) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = level;
        getWindow().setAttributes(lp);
    }

    @SuppressWarnings("ResourceAsColor")
    private UCrop.Options getUcropOptions() {

        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(90);
        options.setActiveWidgetColor(getAccentColor());
        options.setToolbarColor(getPrimaryColor());
        options.setStatusBarColor(isTranslucentStatusBar() ? ColorPalette.getObscuredColor(getPrimaryColor()) : getPrimaryColor());
        options.setCropFrameColor(getAccentColor());
        options.setFreeStyleCropEnabled(true);

        return options;
    }

    @Override
    public void setNavBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isApplyThemeOnImgAct())
                if (isNavigationBarColored())
                    getWindow().setNavigationBarColor(ColorPalette.getTransparentColor(getPrimaryColor(), getTransparency()));
                else
                    getWindow().setNavigationBarColor(ColorPalette.getTransparentColor(ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), getTransparency()));
            else
                getWindow().setNavigationBarColor(ColorPalette.getTransparentColor(ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 175));
        }
    }

    @Override
    protected void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (isApplyThemeOnImgAct())
                if (isTranslucentStatusBar() && isTransparencyZero())
                    getWindow().setStatusBarColor(ColorPalette.getObscuredColor(getPrimaryColor()));
                else
                    getWindow().setStatusBarColor(ColorPalette.getTransparentColor(getPrimaryColor(), getTransparency()));
            else
                getWindow().setStatusBarColor(ColorPalette.getTransparentColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.md_black_1000), 175));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mViewPager != null) {
            outState.putBoolean(ISLOCKED_ARG, mViewPager.isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    public void toggleSystemUI() {
        if (fullScreenMode)
            showSystemUI();
        else hideSystemUI();
    }

    private void hideSystemUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                toolbar.animate().translationY(-toolbar.getHeight()).setInterpolator(new AccelerateInterpolator())
                        .setDuration(200).start();
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_IMMERSIVE);

                fullScreenMode = true;
                changeBackGroundColor();
            }
        });
    }

    private void setupSystemUI() {
        toolbar.animate().translationY(Measure.getStatusBarHeight(getResources())).setInterpolator(new DecelerateInterpolator())
                .setDuration(0).start();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void showSystemUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                toolbar.animate().translationY(Measure.getStatusBarHeight(getResources())).setInterpolator(new DecelerateInterpolator())
                        .setDuration(240).start();
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                fullScreenMode = false;
                changeBackGroundColor();
            }
        });
    }

    private void changeBackGroundColor() {
        int colorTo;
        int colorFrom;
        if (fullScreenMode) {
            colorFrom = getBackgroundColor();
            colorTo = (ContextCompat.getColor(SingleMediaActivity.this, R.color.md_black_1000));
        } else {
            colorFrom = (ContextCompat.getColor(SingleMediaActivity.this, R.color.md_black_1000));
            colorTo = getBackgroundColor();
        }
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(240);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ActivityBackground.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }
    private final class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return BitmapUtils.getSampledBitmap(params[0], imageWidth / 4, imageHeight / 4);
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
        protected void onPreExecute() {
            super.onPreExecute();
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
            imgView.setImageBitmap(mainBitmap);
        }
    }
}

