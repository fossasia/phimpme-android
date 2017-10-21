package org.fossasia.phimpme.gallery.activities;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.yalantis.ucrop.UCrop;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.SharedMediaActivity;
import org.fossasia.phimpme.base.ThemedActivity;
import org.fossasia.phimpme.data.local.DatabaseHelper;
import org.fossasia.phimpme.data.local.ImageDescModel;
import org.fossasia.phimpme.editor.EditImageActivity;
import org.fossasia.phimpme.editor.FileUtils;
import org.fossasia.phimpme.editor.utils.BitmapUtils;
import org.fossasia.phimpme.gallery.SelectAlbumBottomSheet;
import org.fossasia.phimpme.gallery.adapters.ImageAdapter;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.data.base.MediaDetailsMap;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ColorPalette;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.Measure;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.SecurityHelper;
import org.fossasia.phimpme.gallery.util.StringUtils;
import org.fossasia.phimpme.gallery.util.ThemeHelper;
import org.fossasia.phimpme.gallery.views.PagerRecyclerView;
import org.fossasia.phimpme.share.SharingActivity;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.BasicCallBack;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static org.fossasia.phimpme.utilities.Utils.promptSpeechInput;

/**
 * Created by dnld on 18/02/16.
 */
@SuppressWarnings("ResourceAsColor")
public class SingleMediaActivity extends SharedMediaActivity implements ImageAdapter.OnSingleTap {

    private static final String ISLOCKED_ARG = "isLocked";
    static final String ACTION_OPEN_ALBUM = "android.intent.action.pagerAlbumMedia";
    private static final String ACTION_REVIEW = "com.android.camera.action.REVIEW";
    private ImageAdapter adapter;
    private PreferenceUtil SP;
    private CoordinatorLayout ActivityBackground;
    private SelectAlbumBottomSheet bottomSheetDialogFragment;
    private SecurityHelper securityObj;
    private boolean fullScreenMode, customUri = false;
    public static final int TAKE_PHOTO_CODE = 8;
    public static final int ACTION_REQUEST_EDITIMAGE = 9;
    public static final int ACTION_STICKERS_IMAGE = 10;
    private Bitmap mainBitmap;
    private int imageWidth, imageHeight;
    private String path;
    private SingleMediaActivity context;
    public static final String EXTRA_OUTPUT = "extra_output";
    public static String pathForDescription;
    public Boolean allPhotoMode;
    public int all_photo_pos;
    public int size_all;
    public int current_image_pos;
    private Uri uri;
    private Realm realm;
    private DatabaseHelper databaseHelper;
    ImageDescModel temp;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String voiceInput;
    EditText editTextDescription;
    private CoordinatorLayout coordinator;

    @Nullable
    @BindView(R.id.PhotoPager_Layout)
    View parentView;

    @Nullable
    @BindView(R.id.toolbar_bottom)
    ActionMenuView bottomBar;

    @Nullable
    @BindView(R.id.img)
    ImageView imgView;

    @Nullable
    @BindView(R.id.photos_pager)
    PagerRecyclerView mViewPager;

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_pager);
        ButterKnife.bind(this);
        coordinator = (CoordinatorLayout) findViewById(R.id.PhotoPager_Layout);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        imageWidth = metrics.widthPixels;
        imageHeight = metrics.heightPixels;

        overridePendingTransition(R.anim.media_zoom_in,0);

        SP = PreferenceUtil.getInstance(getApplicationContext());
        securityObj = new SecurityHelper(SingleMediaActivity.this);
        allPhotoMode = getIntent().getBooleanExtra(getString(R.string.all_photo_mode), false);
        all_photo_pos = getIntent().getIntExtra(getString(R.string.position), 0);
        size_all = getIntent().getIntExtra(getString(R.string.allMediaSize), getAlbum().getCount());

        String path2 = getIntent().getStringExtra("path");
        pathForDescription = path2;

//            mViewPager.setLocked(savedInstanceState.getBoolean(ISLOCKED_ARG, false));
        try {
            Album album;
            if ((getIntent().getAction().equals(Intent.ACTION_VIEW) || getIntent().getAction().equals(ACTION_REVIEW)) && getIntent().getData() != null) {

                String path = ContentHelper.getMediaPath(getApplicationContext(), getIntent().getData());
                pathForDescription = path;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initUI() {
        Menu bottomMenu = bottomBar.getMenu();
        getMenuInflater().inflate(R.menu.menu_bottom_view_pager, bottomMenu);
        for (int i = 0; i < bottomMenu.size(); i++) {
            bottomMenu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
        }
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
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActivitySwitchHelper.getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        mViewPager.setLayoutManager(linearLayoutManager);
        mViewPager.setHasFixedSize(true);
        mViewPager.setLongClickable(true);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) showSystemUI();
                        else hideSystemUI();
                    }
                });
        BasicCallBack basicCallBack = new BasicCallBack() {
            @Override
            public void callBack(int status, Object data) {
                toggleSystemUI();
            }
        };

        if (!allPhotoMode) {
            adapter = new ImageAdapter(getAlbum().getMedia(), basicCallBack, this);

            getSupportActionBar().setTitle((getAlbum().getCurrentMediaIndex() + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());
//            toolbar.setTitle((mViewPager.getCurrentItem() + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());


            mViewPager.setOnPageChangeListener(new PagerRecyclerView.OnPageChangeListener() {
                @Override
                public void onPageChanged(int oldPosition, int position) {
                    getAlbum().setCurrentPhotoIndex(position);
                    toolbar.setTitle((position + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());
                    invalidateOptionsMenu();
                    pathForDescription = getAlbum().getMedia().get(position).getPath();
                }
            });
            mViewPager.scrollToPosition(getAlbum().getCurrentMediaIndex());


        } else {
            adapter = new ImageAdapter(LFMainActivity.listAll, basicCallBack, this);
            getSupportActionBar().setTitle(all_photo_pos + 1 + " " + getString(R.string.of) + " " + size_all);
            current_image_pos = all_photo_pos;

            mViewPager.setOnPageChangeListener(new PagerRecyclerView.OnPageChangeListener() {
                @Override
                public void onPageChanged(int oldPosition, int position) {
                    current_image_pos = position;
                    getAlbum().setCurrentPhotoIndex(position);
                    toolbar.setTitle((position + 1) + " " + getString(R.string.of) + " " + size_all);
                    invalidateOptionsMenu();
                    pathForDescription = LFMainActivity.listAll.get(position).getPath();
                }
            });
            mViewPager.scrollToPosition(all_photo_pos);
        }
        Display aa = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        mViewPager.setAdapter(adapter);

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
                        ? ColorPalette.getTransparentColor(getPrimaryColor(), getTransparency())
                        : ColorPalette.getTransparentColor(getDefaultThemeToolbarColor3th(), 175));

        toolbar.setPopupTheme(getPopupToolbarStyle());

        ActivityBackground = (CoordinatorLayout) findViewById(R.id.PhotoPager_Layout);
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
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            params.setMargins(0, 0, Measure.getNavigationBarSize(SingleMediaActivity.this).x, 0);
        else
            params.setMargins(0, 0, 0, 0);

        toolbar.setLayoutParams(params);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (!allPhotoMode)
            menu.setGroupVisible(R.id.only_photos_options, true);

        if (customUri) {
            menu.setGroupVisible(R.id.on_internal_storage, false);
            menu.setGroupVisible(R.id.only_photos_options, false);
            menu.findItem(R.id.sort_action).setVisible(false);
        }
        return true;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SPEECH_INPUT && data != null) {
            ArrayList<String> result = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            voiceInput = result.get(0);
            editTextDescription.setText(editTextDescription.getText().toString().trim() + " " + voiceInput);
            editTextDescription.setSelection(editTextDescription.length());
            return;
        }


        if (data != null && resultCode == RESULT_OK) {
            switch (requestCode) {
                case UCrop.REQUEST_CROP:
                    final Uri imageUri = UCrop.getOutput(data);
                    if (imageUri != null && imageUri.getScheme().equals("file")) {
                        try {
                            //copyFileToDownloads(imageUri);
                            // TODO: 21/08/16 handle this better
                            handleEditorImage(data);
                            if (ContentHelper.copyFile(getApplicationContext(), new File(imageUri.getPath()), new File(getAlbum().getPath()))) {
                                //((ImageFragment) adapter.getRegisteredFragment(getAlbum().getCurrentMediaIndex())).displayMedia(true);
                                SnackBarHandler.show(parentView, R.string.new_file_created);
                            }
                            //adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e("ERROS - uCrop", imageUri.toString(), e);
                        }
                    } else
                        SnackBarHandler.show(parentView, "errori random");
                    break;
                default:
                    break;
            }
        }
    }


    private void handleEditorImage(Intent data) {
        String newFilePath = data.getStringExtra(EditImageActivity.EXTRA_OUTPUT);
        boolean isImageEdit = data.getBooleanExtra(EditImageActivity.IMAGE_IS_EDIT, false);

        if (isImageEdit) {

        } else {//Or use the original unedited pictures
            newFilePath = data.getStringExtra(EditImageActivity.FILE_PATH);
        }
        //System.out.println("newFilePath---->" + newFilePath);
        //File file = new File(newFilePath);
        //System.out.println("newFilePath size ---->" + (file.length() / 1024)+"KB");
        Log.d("image is edit", isImageEdit + "");
        LoadImageTask loadTask = new LoadImageTask();
        loadTask.execute(newFilePath);
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
        if (!allPhotoMode) {
            getAlbum().deleteCurrentMedia(getApplicationContext());
            if (getAlbum().getMedia().size() == 0) {
                if (customUri) finish();
                else {
                    getAlbums().removeCurrentAlbum();
                    displayAlbums(false);
                }
            }
            adapter.notifyDataSetChanged();
            getSupportActionBar().setTitle((getAlbum().getCurrentMediaIndex() + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());
        } else {
            deleteMedia(LFMainActivity.listAll.get(current_image_pos).getPath());
            LFMainActivity.listAll.remove(current_image_pos);
            size_all = LFMainActivity.listAll.size();
            adapter.notifyDataSetChanged();
//            mViewPager.setCurrentItem(current_image_pos);
//            toolbar.setTitle((mViewPager.getCurrentItem() + 1) + " " + getString(R.string.of) + " " + size_all);
        }
    }

    private void deleteMedia(String path) {
        String[] projection = {MediaStore.Images.Media._ID};

        // Match on the file path
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{path};

        // Query for the ID of the media matching the file path
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToFirst()) {
            // We found the ID. Deleting the item via the content provider will also remove the file
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(deleteUri, null, null);
        }
        c.close();
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
                        SnackBarHandler.show(coordinator, getString(R.string.copied_successfully) + " to " + path);
                    }
                });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                break;

            case R.id.action_share:
                Intent share = new Intent(SingleMediaActivity.this, SharingActivity.class);
                if (!allPhotoMode)
                    share.putExtra(EXTRA_OUTPUT, getAlbum().getCurrentMedia().getPath());
                else
                    share.putExtra(EXTRA_OUTPUT, LFMainActivity.listAll.get(current_image_pos).getPath());
                startActivity(share);
                return true;

            case R.id.action_edit:
                if (!allPhotoMode)
                    uri = Uri.fromFile(new File(getAlbum().getCurrentMedia().getPath()));
                else
                    uri = Uri.fromFile(new File(LFMainActivity.listAll.get(current_image_pos).getPath()));
                String extension = uri.getPath();
                if (extension != null) {
                    Intent editIntent = new Intent(SingleMediaActivity.this, EditImageActivity.class);
                    editIntent.putExtra("extra_input", uri.getPath());
                    editIntent.putExtra("extra_output", FileUtils.genEditFile(FileUtils.getExtension(extension)).getAbsolutePath());
                    editIntent.putExtra("requestCode", ACTION_REQUEST_EDITIMAGE);
                    startActivity(editIntent);
                } else
                    SnackBarHandler.show(parentView, R.string.image_invalid);
                break;

            case R.id.action_use_as:
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                if (!allPhotoMode)
                    intent.setDataAndType(
                            getAlbum().getCurrentMedia().getUri(), getAlbum().getCurrentMedia().getMimeType());
                else
                    intent.setDataAndType(Uri.fromFile(new File(LFMainActivity.listAll.get(current_image_pos).getPath())), StringUtils.getMimeType(LFMainActivity.listAll.get(current_image_pos).getPath()));
                startActivity(Intent.createChooser(intent, getString(R.string.use_as)));
                return true;

            case R.id.print:
                PrintHelper photoPrinter = new PrintHelper(this);
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                Bitmap bitmap = BitmapFactory.decodeFile(getAlbum().getCurrentMedia().getPath(), new BitmapFactory.Options());
                photoPrinter.printBitmap(getString(R.string.print), bitmap);
                return true;

            case R.id.action_delete:
                final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(SingleMediaActivity.this, getDialogStyle());

                AlertDialogsHelper.getTextDialog(SingleMediaActivity.this, deleteDialog,
                        R.string.delete, R.string.delete_photo_message, null);

                deleteDialog.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);
                deleteDialog.setPositiveButton(this.getString(R.string.delete).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (securityObj.isActiveSecurity() && securityObj.isPasswordOnDelete()) {

                            final AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(SingleMediaActivity.this, getDialogStyle());
                            final EditText editTextPassword = securityObj.getInsertPasswordDialog
                                    (SingleMediaActivity.this, passwordDialogBuilder);

                            passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                        deleteCurrentMedia();
                                    } else
                                        SnackBarHandler.show(parentView, R.string.wrong_password);

                                }
                            });
                            passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                            final AlertDialog passwordDialog = passwordDialogBuilder.create();
                            passwordDialog.show();
                            passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View
                                    .OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                        deleteCurrentMedia();
                                        passwordDialog.dismiss();
                                    } else {
                                        SnackBarHandler.show(parentView, R.string.wrong_password);
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
                        getSupportActionBar().setTitle((getAlbum().getCurrentMediaIndex() + 1) + " " + getString(R.string.of) + " " + getAlbum().getMedia().size());

                        if (getAlbum().getMedia().size() == 0) {
                            if (customUri) finish();
                            else {
                                getAlbums().removeCurrentAlbum();
                                displayAlbums(false);
                            }
                        }
                        adapter.notifyDataSetChanged();
//                        toolbar.setTitle((mViewPager.getCurrentItem() + 1) + " " + getString(R.string.of) + " " + getAlbum().getCount());
                        bottomSheetDialogFragment.dismiss();
                        SnackBarHandler.show(coordinator, getString(R.string.photo_moved_successfully) + " to " +  path
                        );
                    }
                });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                return true;

            case R.id.action_details:
                View v = getLayoutInflater().inflate(R.layout.image_description,mViewPager,false);
                LinearLayout linearLayout = (LinearLayout)v;
                Media media = getAlbum().getCurrentMedia();
                MediaDetailsMap<String,String> mediaDetailsMap = media.getMainDetails(this);

                /* Getting all the viewgroups and views of the image description layout */

                TextView  imgDate = (TextView) linearLayout.findViewById(R.id.image_desc_date);
                TextView  imgLocation = (TextView) linearLayout.findViewById(R.id.image_desc_loc);
                TextView  imgTitle = (TextView) linearLayout.findViewById(R.id.image_desc_title);
                TextView  imgType = (TextView) linearLayout.findViewById(R.id.image_desc_type);
                TextView  imgSize = (TextView) linearLayout.findViewById(R.id.image_desc_size);
                TextView  imgResolution = (TextView) linearLayout.findViewById(R.id.image_desc_res);
                TextView  imgPath = (TextView) linearLayout.findViewById(R.id.image_desc_path);
                TextView  imgOrientation = (TextView) linearLayout.findViewById(R.id.image_desc_orientation);
                TextView  imgExif = (TextView) linearLayout.findViewById(R.id.image_desc_exif);
                TextView  imgDesc = (TextView) linearLayout.findViewById(R.id.image_desc);
                ImageButton imgBack = (ImageButton) linearLayout.findViewById(R.id.img_desc_back_arrow);

                LinearLayout linearLayoutTop = (LinearLayout) linearLayout.findViewById(R.id.image_desc_top);
                linearLayoutTop.setBackgroundColor(this.getPrimaryColor());

                imgBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setContentView(parentView);
                    }
                });

                /*Setting the values to all the textViews*/

                try {
                        imgDate.setText(mediaDetailsMap.get("Date").toString());
                        imgTitle.setText(media.getName());
                        imgType.setText(mediaDetailsMap.get("Type").toUpperCase());
                        imgSize.setText(StringUtils.humanReadableByteCount(media.getSize(), true));
                        imgResolution.setText(mediaDetailsMap.get("Resolution"));
                        imgPath.setText(mediaDetailsMap.get("Path").toString());
                        imgOrientation.setText(mediaDetailsMap.get("Orientation"));
                        imgDesc.setText(mediaDetailsMap.get("Description"));
                        imgExif.setText(mediaDetailsMap.get("EXIF"));
                        imgLocation.setText(mediaDetailsMap.get("Location").toString());
                    }
                    catch (Exception e){
                        //Raised if null values is found, no need to handle
                    }
                setContentView(v);
                break;

            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            case R.id.action_description:
                AlertDialog.Builder descriptionDialogBuilder = new AlertDialog.Builder(SingleMediaActivity.this, getDialogStyle());
                editTextDescription = getDescriptionDialog(SingleMediaActivity.this, descriptionDialogBuilder);
                editTextDescription.setHintTextColor(getResources().getColor(R.color.grey, null));
                editTextDescription.setSelectAllOnFocus(true);
                editTextDescription.selectAll();
                descriptionDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                descriptionDialogBuilder.setPositiveButton((temp != null && temp.getTitle().length() != 0) ? getString(R.string.update_action) : getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //This should br empty it will be overwrite later

                    }
                });

                final AlertDialog descriptionDialog = descriptionDialogBuilder.create();
                descriptionDialog.show();
                descriptionDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager
                        .LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                descriptionDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                
                descriptionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        descriptionDialog.dismiss();
                        voiceInput = editTextDescription.getText().toString();
                        if (temp == null) {
                            databaseHelper.addImageDesc(new ImageDescModel(pathForDescription, editTextDescription.getText().toString()));
                        } else {
                            databaseHelper.update(new ImageDescModel(pathForDescription, editTextDescription.getText().toString()));
                        }

                    }
                });
                break;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                //return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    public EditText getDescriptionDialog(final ThemedActivity activity, AlertDialog.Builder descriptionDialog) {

        final View DescriptiondDialogLayout = activity.getLayoutInflater().inflate(R.layout.dialog_description, null);
        final TextView DescriptionDialogTitle = (TextView) DescriptiondDialogLayout.findViewById(R.id.description_dialog_title);
        final CardView DescriptionDialogCard = (CardView) DescriptiondDialogLayout.findViewById(R.id.description_dialog_card);
        EditText editxtDescription = (EditText) DescriptiondDialogLayout.findViewById(R.id.description_edittxt);
        ImageButton VoiceRecognition = (ImageButton) DescriptiondDialogLayout.findViewById(R.id.voice_input);
        VoiceRecognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(SingleMediaActivity.this, REQ_CODE_SPEECH_INPUT, parentView, getString(R.string.speech_prompt));
            }
        });
        DescriptionDialogTitle.setBackgroundColor(activity.getPrimaryColor());
        DescriptionDialogCard.setBackgroundColor(activity.getCardBackgroundColor());
        ThemeHelper.setCursorDrawableColor(editxtDescription, activity.getTextColor());
        editxtDescription.getBackground().mutate().setColorFilter(activity.getTextColor(), PorterDuff.Mode.SRC_ATOP);
        editxtDescription.setTextColor(activity.getTextColor());

        realm = Realm.getDefaultInstance();
        databaseHelper = new DatabaseHelper(realm);
        temp = databaseHelper.getImageDesc(pathForDescription);
        if (temp != null && temp.getTitle().length() != 0) {
            editxtDescription.setText(temp.getTitle());
            editxtDescription.setSelection(editxtDescription.getText().length());
            //Toast.makeText(SingleMediaActivity.this, voiceInput, Toast.LENGTH_SHORT).show();

        }
        descriptionDialog.setView(DescriptiondDialogLayout);
        return editxtDescription;
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
                    getWindow().setNavigationBarColor(ColorPalette.getTransparentColor(ColorPalette.getObscuredColor(getPrimaryColor()), getTransparency()));
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
      /*  if (mViewPager != null) {
            outState.putBoolean(ISLOCKED_ARG, mViewPager.isLocked());
        }*/
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
                bottomBar.animate().translationY(+bottomBar.getHeight()).setInterpolator(new AccelerateInterpolator())
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
                bottomBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();

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
    @Override
    public void onPause() {
        super.onPause();
        if (isFinishing()){
            overridePendingTransition(0, R.anim.media_zoom_out);
        }

    }

    @Override
    public void singleTap() {
        toggleSystemUI();
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
