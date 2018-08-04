package org.fossasia.phimpme.gallery.activities;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.gifencoder.AnimatedGifEncoder;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.SharedMediaActivity;
import org.fossasia.phimpme.data.local.FavouriteImagesModel;
import org.fossasia.phimpme.data.local.ImageDescModel;
import org.fossasia.phimpme.data.local.TrashBinRealmModel;
import org.fossasia.phimpme.data.local.UploadHistoryRealmModel;
import org.fossasia.phimpme.gallery.SelectAlbumBottomSheet;
import org.fossasia.phimpme.gallery.adapters.AlbumsAdapter;
import org.fossasia.phimpme.gallery.adapters.MediaAdapter;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.CustomAlbumsHelper;
import org.fossasia.phimpme.gallery.data.HandlingAlbums;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.data.base.MediaComparators;
import org.fossasia.phimpme.gallery.data.base.SortingMode;
import org.fossasia.phimpme.gallery.data.base.SortingOrder;
import org.fossasia.phimpme.gallery.data.providers.MediaStoreProvider;
import org.fossasia.phimpme.gallery.data.providers.StorageProvider;
import org.fossasia.phimpme.gallery.util.Affix;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.Measure;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.SecurityHelper;
import org.fossasia.phimpme.gallery.util.StringUtils;
import org.fossasia.phimpme.gallery.views.CustomScrollBarRecyclerView;
import org.fossasia.phimpme.gallery.views.GridSpacingItemDecoration;
import org.fossasia.phimpme.trashbin.TrashBinActivity;
import org.fossasia.phimpme.uploadhistory.UploadHistory;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.Constants;
import org.fossasia.phimpme.utilities.NotificationHandler;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static org.fossasia.phimpme.gallery.data.base.SortingMode.DATE;
import static org.fossasia.phimpme.gallery.data.base.SortingMode.NAME;
import static org.fossasia.phimpme.gallery.data.base.SortingMode.NUMERIC;
import static org.fossasia.phimpme.gallery.data.base.SortingMode.SIZE;
import static org.fossasia.phimpme.gallery.util.ThemeHelper.LIGHT_THEME;

public class LFMainActivity extends SharedMediaActivity {

    private static String TAG = "AlbumsAct";
    private LFMainActivity activityContext;
    private int REQUEST_CODE_SD_CARD_PERMISSIONS = 42;
    private static final int BUFFER = 80000;
    private boolean about = false, settings = false, uploadHistory = false, favourites = false, trashbin = false;
    private CustomAlbumsHelper customAlbumsHelper = CustomAlbumsHelper.getInstance(LFMainActivity.this);
    private PreferenceUtil SP;
    private SecurityHelper securityObj;

    private AlbumsAdapter albumsAdapter;
    private GridSpacingItemDecoration rvAlbumsDecoration;
    private SwipeRefreshLayout.OnRefreshListener refreshListener;

    private MediaAdapter mediaAdapter;
    private GridSpacingItemDecoration rvMediaDecoration;

    private SelectAlbumBottomSheet bottomSheetDialogFragment;
    private boolean hidden = false, pickMode = false, editMode = false, albumsMode = true, firstLaunch = true, localFolder = true, hidenav = false;

    //to handle pinch gesture
    private ScaleGestureDetector mScaleGestureDetector;

    //To handle all photos/Album conditions
    public boolean all_photos = false;
    private boolean checkForReveal = true;
    final String REVIEW_ACTION = "com.android.camera.action.REVIEW";
    public static ArrayList<Media> listAll;
    public int size;
    public int pos;
    ArrayList<String> path;
    private ArrayList<Media> media;
    private ArrayList<Media> selectedMedias = new ArrayList<>();
    public boolean visible;
    private ArrayList<Album> albList;


    //To handle favourite collection
    private Realm realm;
    private ArrayList<Media> favouriteslist;
    public boolean fav_photos = false;
    private IconicsImageView favicon;
    
    private CustomScrollBarRecyclerView rvAlbums;
    private CustomScrollBarRecyclerView rvMedia;

    // To handle back pressed
    boolean doubleBackToExitPressedOnce = false;

    private boolean fromOnClick = false;
    // Binding various views with Butterknife

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.swipeRefreshLayout)
    protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.fab_scroll_up)
    protected FloatingActionButton fabScrollUp;
    @BindView(R.id.Drawer_Setting_Item)
    protected TextView drawerSettingText;
    @BindView(R.id.Drawer_About_Item)
    protected TextView drawerAboutText;
    @BindView(R.id.Drawer_share_Item)
    protected TextView drawerShareText;
    @BindView(R.id.Drawer_rate_Item)
    protected TextView drawerRateText;
    @BindView(R.id.Drawer_Upload_Item)
    protected TextView drawerUploadText;
    @BindView(R.id.Drawer_TrashBin_Item)
    protected TextView drawerTrashText;
    @BindView(R.id.Drawer_Setting_Icon)
    protected IconicsImageView drawerSettingIcon;
    @BindView(R.id.Drawer_About_Icon)
    protected IconicsImageView drawerAboutIcon;
    @BindView(R.id.Drawer_share_Icon)
    protected IconicsImageView drawerShareIcon;
    @BindView(R.id.Drawer_rate_Icon)
    protected IconicsImageView drawerRateIcon;
    @BindView(R.id.Drawer_Upload_Icon)
    protected IconicsImageView drawerUploadIcon;
    @BindView(R.id.Drawer_trashbin_Icon)
    protected IconicsImageView drawerTrashIcon;
    @BindView(R.id.drawer_scrollbar)
    protected ScrollView scrollView;
    @BindView(R.id.appbar_toolbar)
    protected View toolbari;
    @BindView(R.id.nothing_to_show)
    protected TextView nothingToShow;
    @BindView(R.id.no_search_results)
    protected TextView textView;
    @BindView(R.id.Drawer_Default_Icon)
    protected IconicsImageView defaultIcon;
    @BindView(R.id.Drawer_hidden_Icon)
    protected IconicsImageView hiddenIcon;
    @BindView(R.id.Drawer_Default_Item)
    protected TextView defaultText;
    @BindView(R.id.Drawer_hidden_Item)
    protected TextView hiddenText;
    @BindView(R.id.star_image_view)
    protected ImageView starImageView;

    /*
    editMode-  When true, user can select items by clicking on them one by one
     */

    /**
     * Handles long clicks on photos.
     * If first long click on photo (editMode = false), go into selection mode and set editMode = true.
     * If not first long click, means that already in selection mode- s0 select all photos upto chosen one.
     */
    private View.OnLongClickListener photosOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (checkForReveal) {
                enterReveal();
                checkForReveal = false;
            }
            Media m = (Media) v.findViewById(R.id.photo_path).getTag();
            //If first long press, turn on selection mode
            hideNavigationBar();
            hidenav = true;
            if (!all_photos && !fav_photos) {
                appBarOverlay();
                if (!editMode) {
                    mediaAdapter.notifyItemChanged(getAlbum().toggleSelectPhoto(m));
                    editMode = true;
                } else getAlbum().selectAllPhotosUpTo(getAlbum().getIndex(m), mediaAdapter);
                invalidateOptionsMenu();
            } else if (all_photos && !fav_photos) {
                if (!editMode) {
                    mediaAdapter.notifyItemChanged(toggleSelectPhoto(m));
                    editMode = true;
                }
            } else if (fav_photos && !all_photos) {
                if (!editMode) {
                    mediaAdapter.notifyItemChanged(toggleSelectPhoto(m));
                    editMode = true;
                }
            } else selectAllPhotosUpTo(getImagePosition(m.getPath()), mediaAdapter);
            return true;
        }
    };

    /**
     * Helper method for making reveal animation for toolbar when any item is selected by long click.
     */
    private void enterReveal() {

        // get the center for the clipping circle
        int cx = toolbari.getMeasuredWidth() / 2;
        int cy = toolbari.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(toolbari.getWidth(), toolbari.getHeight()) / 2;

        // create the animator for this view
        Animator anim =
                ViewAnimationUtils.createCircularReveal(toolbari, cx, cy, 5, finalRadius);

        anim.start();
    }

    /**
     * Helper method for making reveal animation for toolbar when back is presses in edit mode.
     */
    private void exitReveal() {

        // get the center for the clipping circle
        int cx = toolbari.getMeasuredWidth() / 2;
        int cy = toolbari.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(toolbari.getWidth(), toolbari.getHeight()) / 2;

        // create the animator for this view
        Animator anim =
                ViewAnimationUtils.createCircularReveal(toolbari, cx, cy, finalRadius, 5);

        anim.start();
    }

    private int toggleSelectPhoto(Media m) {
        if (m != null) {
            m.setSelected(!m.isSelected());
            if (m.isSelected())
                selectedMedias.add(m);
            else
                selectedMedias.remove(m);
        }
        if (selectedMedias.size() == 0) {
            getNavigationBar();
            editMode = false;
            toolbar.setTitle(getString(R.string.all));
        } else {
            if (!fav_photos) {
                toolbar.setTitle(selectedMedias.size() + "/" + size);
            } else if (fav_photos) {
                toolbar.setTitle(selectedMedias.size() + "/" + favouriteslist.size());
            }
        }
        invalidateOptionsMenu();
        return getImagePosition(m.getPath());
    }

    public void clearSelectedPhotos() {
        for (Media m : selectedMedias)
            m.setSelected(false);
        if (selectedMedias != null)
            selectedMedias.clear();
        if (localFolder) toolbar.setTitle(getString(R.string.local_folder));
        else toolbar.setTitle(getString(R.string.hidden_folder));
    }


    public void selectAllPhotos() {
        if (all_photos && !fav_photos) {
            for (Media m : listAll) {
                m.setSelected(true);
                selectedMedias.add(m);
            }
            toolbar.setTitle(selectedMedias.size() + "/" + size);
        } else if (!all_photos && fav_photos) {
            for (Media m : favouriteslist) {
                m.setSelected(true);
                if (m.isSelected())
                    selectedMedias.add(m);
            }
            toolbar.setTitle(selectedMedias.size() + "/" + favouriteslist.size());
        }
    }

    public void selectAllPhotosUpTo(int targetIndex, MediaAdapter adapter) {
        int indexRightBeforeOrAfter = -1;
        int indexNow;
        for (Media sm : selectedMedias) {
            indexNow = getImagePosition(sm.getPath());
            if (indexRightBeforeOrAfter == -1) indexRightBeforeOrAfter = indexNow;

            if (indexNow > targetIndex) break;
            indexRightBeforeOrAfter = indexNow;
        }

        if (indexRightBeforeOrAfter != -1) {
            for (int index = Math.min(targetIndex, indexRightBeforeOrAfter); index <= Math.max(targetIndex, indexRightBeforeOrAfter); index++) {
                if (listAll.get(index) != null && !listAll.get(index).isSelected()) {
                    listAll.get(index).setSelected(true);
                    selectedMedias.add(listAll.get(index));
                    adapter.notifyItemChanged(index);
                }
            }
        }
        toolbar.setTitle(selectedMedias.size() + "/" + size);
    }

    public void populateAlbum() {
        albList = new ArrayList<>();
        for (Album album : getAlbums().dispAlbums) {
            albList.add(album);
        }
    }

    /**
     * Handles short clicks on photos.
     * If in selection mode (editMode = true) , select the photo if it is unselected and unselect it if it's selected.
     * This mechanism makes it possible to select photos one by one by short-clicking on them.
     * If not in selection mode (editMode = false) , get current photo from album and open it in singleActivity
     */
    private View.OnClickListener photosOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Media m = (Media) v.findViewById(R.id.photo_path).getTag();
            if (all_photos) {
                pos = getImagePosition(m.getPath());
            }
            if (fav_photos) {
                pos = getImagePosition(m.getPath());
            }
            if (!all_photos && !fav_photos) {
                if (!pickMode) {
                    //if in selection mode, toggle the selected/unselect state of photo
                    if (editMode) {
                        appBarOverlay();
                        mediaAdapter.notifyItemChanged(getAlbum().toggleSelectPhoto(m));
                        if (getAlbum().selectedMedias.size() == 0)
                            getNavigationBar();
                        invalidateOptionsMenu();
                    } else {
                        v.setTransitionName(getString(R.string.transition_photo));
                        getAlbum().setCurrentPhotoIndex(m);
                        Intent intent = new Intent(LFMainActivity.this, SingleMediaActivity.class);
                        intent.putExtra("path", Uri.fromFile(new File(m.getPath())).toString());
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation(LFMainActivity.this, v, v.getTransitionName());
                        intent.setAction(SingleMediaActivity.ACTION_OPEN_ALBUM);
                        startActivity(intent, options.toBundle());
                    }
                } else {
                    setResult(RESULT_OK, new Intent().setData(m.getUri()));
                    finish();
                }
            } else if (all_photos && !fav_photos) {
                if (!editMode) {
                    Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(m.getPath())));
                    intent.putExtra(getString(R.string.all_photo_mode), true);
                    intent.putExtra(getString(R.string.position), pos);
                    intent.putExtra(getString(R.string.allMediaSize), size);
                    v.setTransitionName(getString(R.string.transition_photo));
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(LFMainActivity.this, v, v.getTransitionName());
                    intent.setClass(getApplicationContext(), SingleMediaActivity.class);
                    startActivity(intent, options.toBundle());
                } else {
                    mediaAdapter.notifyItemChanged(toggleSelectPhoto(m));
                }
            } else if (!all_photos && fav_photos) {
                if (!editMode) {
                    Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(m.getPath())));
                    intent.putExtra("fav_photos", true);
                    intent.putExtra(getString(R.string.position), pos);
                    intent.putParcelableArrayListExtra("favouriteslist", favouriteslist);
                    intent.putExtra(getString(R.string.allMediaSize), favouriteslist.size());
                    v.setTransitionName(getString(R.string.transition_photo));
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(LFMainActivity.this, v, v.getTransitionName());
                    intent.setClass(getApplicationContext(), SingleMediaActivity.class);
                    startActivity(intent, options.toBundle());
                } else {
                    mediaAdapter.notifyItemChanged(toggleSelectPhoto(m));
                }
            }
        }
    };

    private View.OnLongClickListener albumOnLongCLickListener = new View.OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            final Album album = (Album) v.findViewById(R.id.album_name).getTag();
            if(securityObj.isActiveSecurity() && securityObj.isPasswordOnfolder()) {
                final boolean passco[] = {false};
                if (check(album.getPath())) {
                    AlertDialog.Builder passwordDialogBuilder =
                            new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                    final EditText editTextPassword =
                            securityObj.getInsertPasswordDialog(LFMainActivity.this, passwordDialogBuilder);
                    editTextPassword.setHintTextColor(getResources().getColor(R.color.grey, null));
                    passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                    passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //This should br empty it will be overwrite later
                                    //to avoid dismiss of the dialog on wrong password
                                }
                            });
                    editTextPassword.addTextChangedListener(new TextWatcher() {
                        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void afterTextChanged(Editable editable) {
                            if(securityObj.getTextInputLayout().getVisibility() == View.VISIBLE && !passco[0]){
                                securityObj.getTextInputLayout().setVisibility(View.INVISIBLE);
                            }
                            else{
                                passco[0]=false;
                            }

                        }
                    });

                    final AlertDialog passwordDialog = passwordDialogBuilder.create();
                    passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    passwordDialog.show();
                    AlertDialogsHelper.setButtonTextColor(
                            new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
                            getAccentColor(), passwordDialog);
                    passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                        passwordDialog.dismiss();
                                        if (checkForReveal) {
                                            enterReveal();
                                            checkForReveal = false;
                                        }
                                        albumsAdapter.notifyItemChanged(getAlbums().toggleSelectAlbum(album));
                                        editMode = true;
                                        invalidateOptionsMenu();
                                        if (getAlbums().getSelectedCount() == 0)
                                            getNavigationBar();
                                        else {
                                            hideNavigationBar();
                                            hidenav = true;
                                        }
                                    }
                                    // if password is incorrect, notify user of incorrect password
                                    else {
                                        passco[0] = true;
                                        securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
                                        SnackBarHandler
                                                .showWithBottomMargin(mDrawerLayout, getString(R.string.wrong_password),
                                                        navigationView.getHeight());
                                        editTextPassword.getText().clear();
                                        editTextPassword.requestFocus();
                                    }
                                }
                            });
                } else {
                    if (checkForReveal) {
                        enterReveal();
                        checkForReveal = false;
                    }
                    albumsAdapter.notifyItemChanged(getAlbums().toggleSelectAlbum(album));
                    editMode = true;
                    invalidateOptionsMenu();
                    if (getAlbums().getSelectedCount() == 0)
                        getNavigationBar();
                    else {
                        hideNavigationBar();
                        hidenav = true;
                    }
                }
            } else {
                if (checkForReveal) {
                    enterReveal();
                    checkForReveal = false;
                }
                //for selecting albums upto a particular range
                if(editMode) {
                    int currentAlbum = getAlbums().getCurrentAlbumIndex(album);
                    getAlbums().selectAllPhotosUpToAlbums(currentAlbum, albumsAdapter);
                }
                albumsAdapter.notifyItemChanged(getAlbums().toggleSelectAlbum(album));
                editMode = true;
                invalidateOptionsMenu();
                if (getAlbums().getSelectedCount() == 0)
                    getNavigationBar();
                else {
                    hideNavigationBar();
                    hidenav = true;
                }
            }
            return true;
        }
    };

    private boolean check(String path) {
        boolean dr = false;
        for (String s : securityObj.getSecuredfolders()) {
            if (s.equals(path)) {
                dr = true;
                break;
            }
        }
        return dr;
    }

    private View.OnClickListener albumOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            fromOnClick = true;
            final Album album = (Album) v.findViewById(R.id.album_name).getTag();
            //int index = Integer.parseInt(v.findViewById(R.id.album_name).getTag().toString());
            if (editMode) {
                albumsAdapter.notifyItemChanged(getAlbums().toggleSelectAlbum(album));
                if (getAlbums().getSelectedCount() == 0)
                    getNavigationBar();
                invalidateOptionsMenu();
            } else if(securityObj.isActiveSecurity() && securityObj.isPasswordOnfolder()){
                final boolean[] passco = {false};
                if (check(album.getPath())) {
                    AlertDialog.Builder passwordDialogBuilder =
                            new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                    final EditText editTextPassword =
                            securityObj.getInsertPasswordDialog(LFMainActivity.this, passwordDialogBuilder);
                    editTextPassword.setHintTextColor(getResources().getColor(R.color.grey, null));
                    editTextPassword.addTextChangedListener(new TextWatcher() {
                        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void afterTextChanged(Editable editable) {
                            if(securityObj.getTextInputLayout().getVisibility() == View.VISIBLE && !passco[0]){
                                securityObj.getTextInputLayout().setVisibility(View.INVISIBLE);
                            }
                            else{
                                passco[0]=false;
                            }
                        }
                    });
                    passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                    passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //This should br empty it will be overwrite later
                                    //to avoid dismiss of the dialog on wrong password
                                }
                            });

                    final AlertDialog passwordDialog = passwordDialogBuilder.create();
                    passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    passwordDialog.show();
                    AlertDialogsHelper.setButtonTextColor(
                            new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE},
                            getAccentColor(), passwordDialog);
                    passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                        passwordDialog.dismiss();
                                        getAlbums().setCurrentAlbum(album);
                                        displayCurrentAlbumMedia(true);
                                    }
                                    // if password is incorrect, notify user of incorrect password
                                    else {
                                        passco[0] =true;
                                        securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
                                        SnackBarHandler
                                                .showWithBottomMargin(mDrawerLayout, getString(R.string.wrong_password),
                                                        navigationView.getHeight());
                                        editTextPassword.getText().clear();
                                        editTextPassword.requestFocus();
                                    }
                                }
                            });
                } else {
                    getAlbums().setCurrentAlbum(album);
                    displayCurrentAlbumMedia(true);
                }

            } else {
                getAlbums().setCurrentAlbum(album);
                displayCurrentAlbumMedia(true);
            }
        }
    };

    /**
     * Method for clearing the scroll flags.
     */
    private void appBarOverlay() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);  // clear all scroll flags
    }

    /**
     * Method for adding the scroll flags.
     */
    private void clearOverlay() {
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

    public int getImagePosition(String path) {
        int pos = 0;
        if (all_photos) {
            for (int i = 0; i < listAll.size(); i++) {
                if (listAll.get(i).getPath().equals(path)) {
                    pos = i;
                    break;
                }
            }
        } else if (fav_photos) {
            Collections.sort(favouriteslist, MediaComparators.getComparator(getAlbum().settings.getSortingMode(), getAlbum().settings
                    .getSortingOrder()));
            for (int i = 0; i < favouriteslist.size(); i++) {
                if (favouriteslist.get(i).getPath().equals(path)) {
                    pos = i;
                    break;
                }
            }
        }
        return pos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "lfmain");
        ButterKnife.bind(this);

        BottomNavigationView navigationView = (BottomNavigationView) findViewById(R.id.bottombar);
        favicon = (IconicsImageView) findViewById(R.id.Drawer_favourite_Icon);
        
        rvAlbums = (CustomScrollBarRecyclerView) findViewById(R.id.grid_albums);
        rvMedia  = (CustomScrollBarRecyclerView) findViewById(R.id.grid_photos);

        overridePendingTransition(R.anim.right_to_left,
                R.anim.left_to_right);
        SP = PreferenceUtil.getInstance(getApplicationContext());
        albumsMode = true;
        editMode = false;
        securityObj = new SecurityHelper(LFMainActivity.this);
        if (getIntent().getExtras() != null)
            pickMode = getIntent().getExtras().getBoolean(SplashScreen.PICK_MODE);
        SP.putBoolean(getString(R.string.preference_use_alternative_provider), false);
        initUI();
        activityContext = this;
        new initAllPhotos().execute();
        new SortModeSet(activityContext).execute(DATE);
        displayData(getIntent().getExtras());
        checkNothing();
        populateAlbum();
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if (itemID == R.id.navigation_home) {
                    if(textView.getVisibility() == View.VISIBLE){
                        textView.setVisibility(View.GONE);
                    }
                    if (!localFolder) {
                        hidden = false;
                        localFolder = true;
                        findViewById(R.id.ll_drawer_hidden).setBackgroundColor(Color.TRANSPARENT);
                        findViewById(R.id.ll_drawer_Default).setBackgroundColor(getHighlightedItemColor());
                        tint();
                    }
                    displayAlbums();
                    return true;
                }
                return LFMainActivity.super.onNavigationItemSelected(item);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitySwitchHelper.setContext(this);
        securityObj.updateSecuritySetting();
        setupUI();
        if (all_photos && !fav_photos) {
            new PrepareAllPhotos(activityContext).execute();
        }
        if (!all_photos && fav_photos) {
            new FavouritePhotos(activityContext).execute();
        }
        if (!all_photos && !fav_photos) {
            if (SP.getBoolean("auto_update_media", false)) {
                if (albumsMode) {
                    if (!firstLaunch) new PrepareAlbumTask(activityContext).execute();
                } else new PreparePhotosTask(activityContext).execute();
            } else {
                albumsAdapter.notifyDataSetChanged();
                mediaAdapter.notifyDataSetChanged();
            }
        }
        invalidateOptionsMenu();
        firstLaunch = false;
    }

    private void displayCurrentAlbumMedia(boolean reload) {
        toolbar.setTitle(getAlbum().getName());
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mediaAdapter.swapDataSet(getAlbum().getMedia(), false);
        if (reload) new PreparePhotosTask(activityContext).execute();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlbums();
            }
        });
        albumsMode = editMode = false;
        invalidateOptionsMenu();
    }

    private void displayAllMedia(boolean reload) {
        clearSelectedPhotos();
        toolbar.setTitle(getString(R.string.all_media));
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mediaAdapter.swapDataSet(listAll, false);
        if (reload) new PrepareAllPhotos(activityContext).execute();
        if (reload) new PrepareAllPhotos(activityContext).execute();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlbums();
            }
        });
        albumsMode = editMode = false;
        invalidateOptionsMenu();
    }

    private void getfavouriteslist() {
        favouriteslist = new ArrayList<Media>();
        ArrayList<String> todelete = new ArrayList<>();
        realm = Realm.getDefaultInstance();
        RealmQuery<FavouriteImagesModel> favouriteImagesModelRealmQuery = realm.where(FavouriteImagesModel.class);
        int count = Integer.parseInt(String.valueOf(favouriteImagesModelRealmQuery.count()));
        for (int i = 0; i < count; i++) {
            final String path = favouriteImagesModelRealmQuery.findAll().get(i).getPath();
            if (new File(favouriteImagesModelRealmQuery.findAll().get(i).getPath()).exists()) {
                favouriteslist.add(new Media(new File(favouriteImagesModelRealmQuery.findAll().get(i).getPath())));
            } else {
                todelete.add(path);
            }
        }
        for(int i = 0; i < todelete.size(); i++){
            final String path = todelete.get(i);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<FavouriteImagesModel> result = realm.where(FavouriteImagesModel.class).equalTo
                            ("path", path).findAll();
                    result.deleteAllFromRealm();
                }
            });
        }
    }

    private void displayfavourites() {
        toolbar.setTitle(getResources().getString(R.string.favourite_title));
        getfavouriteslist();
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        fav_photos=true;
        mediaAdapter.swapDataSet(favouriteslist, true);
        if(fav_photos){
            new FavouritePhotos(activityContext).execute();
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlbums();
            }
        });
        albumsMode = editMode = all_photos = false;
        invalidateOptionsMenu();
    }

    private void displayAlbums() {
        all_photos = false;
        fav_photos = false;
        displayAlbums(true);
    }

    private void displayAlbums(boolean reload) {
        if (localFolder) {
            toolbar.setTitle(getString(R.string.local_folder));
        } else {
            toolbar.setTitle(getString(R.string.hidden_folder));
        }
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_menu));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        albumsAdapter.swapDataSet(getAlbums().dispAlbums);
        if (reload) new PrepareAlbumTask(activityContext).execute();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        albumsMode = true;
        editMode = false;
        invalidateOptionsMenu();
        mediaAdapter.swapDataSet(new ArrayList<Media>(), false);
        rvMedia.scrollToPosition(0);
    }

    private ArrayList<Media> getselecteditems(){
        ArrayList<Media> storeselmedia = new ArrayList<>();
        for(Media m: getAlbum().getSelectedMedia()){
            storeselmedia.add(m);
        }
        return storeselmedia;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateColumnsRvs();
    }

    private boolean displayData(Bundle data) {
        if (data != null) {
            switch (data.getInt(SplashScreen.CONTENT)) {
                case SplashScreen.ALBUMS_PREFETCHED:
                    displayAlbums(false);
                    // we pass the albumMode here . If true, show rvAlbum recyclerView. If false, show rvMedia recyclerView
                    toggleRecyclersVisibility(true);
                    return true;

                case SplashScreen.ALBUMS_BACKUP:
                    displayAlbums(true);
                    // we pass the albumMode here . If true, show rvAlbum recyclerView. If false, show rvMedia recyclerView
                    toggleRecyclersVisibility(true);
                    return true;

                case SplashScreen.PHOTOS_PREFETCHED:
                    //TODO ask password if hidden
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            getAlbums().loadAlbums(getApplicationContext(), getAlbum().isHidden());
                        }
                    }).start();
                    displayCurrentAlbumMedia(false);

                    // we pass the albumMode here . If true, show rvAlbum recyclerView. If false, show rvMedia recyclerView
                    toggleRecyclersVisibility(false);
                    return true;
            }
        }

        displayAlbums(true);
        return false;
    }

    private class initAllPhotos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            listAll = StorageProvider.getAllShownImages(LFMainActivity.this);
            size = listAll.size();
            media = listAll;
            Collections.sort(listAll, MediaComparators.getComparator(getAlbum().settings.getSortingMode(), getAlbum().settings.getSortingOrder()));
            return null;
        }
    }

    private void initUI() {
        clearOverlay();

        setSupportActionBar(toolbar);

        rvAlbums.setHasFixedSize(true);
        rvAlbums.setItemAnimator(new DefaultItemAnimator());
        rvMedia.setHasFixedSize(true);
        rvMedia.setItemAnimator(new DefaultItemAnimator());

        albumsAdapter = new AlbumsAdapter(getAlbums().dispAlbums, LFMainActivity.this);

        albumsAdapter.setOnClickListener(albumOnClickListener);
        albumsAdapter.setOnLongClickListener(albumOnLongCLickListener);
        rvAlbums.setAdapter(albumsAdapter);

        //set scale gesture detector for resizing the gridItem
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {

                if (detector.getCurrentSpan() > 200 && detector.getTimeDelta() > 200) {
                    int spanCount;
                    if (albumsMode)
                        spanCount = columnsCount();
                    else
                        spanCount = mediaCount();

                    //zooming out
                    if ((detector.getCurrentSpan() - detector.getPreviousSpan() < -300) && spanCount < 6) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            if (albumsMode)
                                SP.putInt("n_columns_folders", spanCount + 1);
                            else
                                SP.putInt("n_columns_media", spanCount + 1);
                        } else {
                            if (albumsMode)
                                SP.putInt("n_columns_folders_landscape", spanCount + 1);
                            else
                                SP.putInt("n_columns_media_landscape", spanCount + 1);
                        }

                        if (albumsMode)
                            updateColumnsRvAlbums();
                        else
                            updateColumnsRvMedia();
                    }
                    //zooming in
                    else if ((detector.getCurrentSpan() - detector.getPreviousSpan() > 300) && spanCount > 1) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            if (albumsMode)
                                SP.putInt("n_columns_folders", spanCount - 1);
                            else
                                SP.putInt("n_columns_media", spanCount - 1);
                        } else {
                            if (albumsMode)
                                SP.putInt("n_columns_folders_landscape", spanCount - 1);
                            else
                                SP.putInt("n_columns_media_landscape", spanCount - 1);
                        }

                        if (albumsMode)
                            updateColumnsRvAlbums();
                        else
                            updateColumnsRvMedia();
                    }
                }
                return false;

            }
        });

        //set touch listener on recycler view
        rvAlbums.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        rvMedia.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });

        mediaAdapter = new MediaAdapter(getAlbum().getMedia(), LFMainActivity.this);

        mediaAdapter.setOnClickListener(photosOnClickListener);
        mediaAdapter.setOnLongClickListener(photosOnLongClickListener);
        rvMedia.setAdapter(mediaAdapter);

        int spanCount = columnsCount();
        rvAlbumsDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(3, getApplicationContext()), true);
        rvAlbums.addItemDecoration(rvAlbumsDecoration);
        rvAlbums.setLayoutManager(new GridLayoutManager(this, spanCount));

        spanCount = mediaCount();
        rvMediaDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(3, getApplicationContext()), true);
        rvMedia.setLayoutManager(new GridLayoutManager(getApplicationContext(), spanCount));
        rvMedia.addItemDecoration(rvMediaDecoration);


        /**** SWIPE TO REFRESH ****/
        swipeRefreshLayout.setColorSchemeColors(getAccentColor());
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());
        refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNavigationBar();
                if (albumsMode) {
                    getAlbums().clearSelectedAlbums();
                    new PrepareAlbumTask(activityContext).execute();
                } else {
                    if (!all_photos && !fav_photos) {
                        getAlbum().clearSelectedPhotos();
                        new PreparePhotosTask(activityContext).execute();
                    } else {
                        if (all_photos && !fav_photos) {
                            new PrepareAllPhotos(activityContext).execute();
                        } else if (!all_photos && fav_photos) {
                            new FavouritePhotos(activityContext).execute();
                        }

                    }
                }
            }
        };
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        /**** DRAWER ****/
        mDrawerLayout.addDrawerListener(new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                //Put your code here
                // materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER);
                Intent intent = null;
                if (settings) {
                    intent = new Intent(LFMainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    settings = false;
                } else if (about) {
                    intent = new Intent(LFMainActivity.this, AboutActivity.class);
                    startActivity(intent);
                    about = false;
                } else if (uploadHistory) {
                    intent = new Intent(LFMainActivity.this, UploadHistory.class);
                    startActivity(intent);
                    uploadHistory = false;
                } else if (favourites) {
                    displayfavourites();
                    favourites = false;
                } else if (trashbin) {
                    Intent intent1 = new Intent(LFMainActivity.this, TrashBinActivity.class);
                    startActivity(intent1);
                    trashbin = false;
                }
            }

            public void onDrawerOpened(View drawerView) {
                //Put your code here
                //materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
            }
        });

        /**
         * Floating Action Button to Scroll Up
         */
        setUpFab();

        setRecentApp(getString(R.string.app_name));
        setupUI();
        if (pickMode) {
            hideNavigationBar();
            swipeRefreshLayout.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * Method to set scroll listeners for recycler view
     */
    private void setUpFab() {

        fabScrollUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvMedia.smoothScrollToPosition(0);
                fabScrollUp.hide();
            }
        });
        fabScrollUp.hide();
        rvMedia.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager.findFirstVisibleItemPosition() > 30 && !fabScrollUp.isShown())
                    fabScrollUp.show();
                else if (linearLayoutManager.findFirstVisibleItemPosition() < 30 && fabScrollUp.isShown())
                    fabScrollUp.hide();
                fabScrollUp.setAlpha(0.7f);
            }
        });
    }

    public int columnsCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? SP.getInt("n_columns_folders", 2)
                : SP.getInt("n_columns_folders_landscape", 3);
    }

    public int mediaCount() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                ? SP.getInt("n_columns_media", 3)
                : SP.getInt("n_columns_media_landscape", 4);
    }

    private void updateColumnsRvs() {
        updateColumnsRvAlbums();
        updateColumnsRvMedia();
    }

    private void updateColumnsRvAlbums() {
        int spanCount = columnsCount();
        if (spanCount != ((GridLayoutManager) rvAlbums.getLayoutManager()).getSpanCount()) {
            rvAlbums.removeItemDecoration(rvAlbumsDecoration);
            rvAlbumsDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(3, getApplicationContext()), true);
            rvAlbums.addItemDecoration(rvAlbumsDecoration);
            rvAlbums.setLayoutManager(new GridLayoutManager(this, spanCount));
        }
    }

    private void updateColumnsRvMedia() {
        int spanCount = mediaCount();
        if (spanCount != ((GridLayoutManager) rvMedia.getLayoutManager()).getSpanCount()) {
            ((GridLayoutManager) rvMedia.getLayoutManager()).getSpanCount();
            rvMedia.removeItemDecoration(rvMediaDecoration);
            rvMediaDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(3, getApplicationContext()), true);
            rvMedia.setLayoutManager(new GridLayoutManager(getApplicationContext(), spanCount));
            rvMedia.addItemDecoration(rvMediaDecoration);
        }
    }

    //region TESTING

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final void onActivityResult(final int requestCode, final int resultCode, final Intent resultData) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SD_CARD_PERMISSIONS) {
                Uri treeUri = resultData.getData();
                // Persist URI in shared preference so that you can use it later.
                ContentHelper.saveSdCardInfo(getApplicationContext(), treeUri);
                getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.got_permission_wr_sdcard), 0);

            }
        }
    }
    //endregion

    private void requestSdCardPermissions() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());

        AlertDialogsHelper.getTextDialog(LFMainActivity.this, dialogBuilder,
                R.string.sd_card_write_permission_title, R.string.sd_card_permissions_message, null);

        dialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), REQUEST_CODE_SD_CARD_PERMISSIONS);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialog);
    }


    //region UI/GRAPHIC
    private void setupUI() {
        updateColumnsRvs();
        //TODO: MUST BE FIXED
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        if (localFolder) {
            toolbar.setTitle(getString(R.string.local_folder));
        } else {
            toolbar.setTitle(getString(R.string.hidden_folder));
        }

        /**** SWIPE TO REFRESH ****/
        swipeRefreshLayout.setColorSchemeColors(getAccentColor());
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());

        setStatusBarColor();
        setNavBarColor();

        setDrawerTheme();
        rvAlbums.setBackgroundColor(getBackgroundColor());
        rvMedia.setBackgroundColor(getBackgroundColor());
        rvAlbums.setScrollBarColor(getPrimaryColor());
        rvMedia.setScrollBarColor(getPrimaryColor());
        mediaAdapter.updatePlaceholder(getApplicationContext());
        albumsAdapter.updateTheme();
        /**** DRAWER ****/
        setScrollViewColor(scrollView);

        /**** recyclers drawable *****/
        Drawable drawableScrollBar = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scrollbar);
        drawableScrollBar.setColorFilter(new PorterDuffColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP));

        /**** FAB ****/
        fabScrollUp.setBackgroundTintList(ColorStateList.valueOf(getAccentColor()));
        fabScrollUp.setAlpha(0.7f);
    }

    private void setDrawerTheme() {

        findViewById(R.id.Drawer_Header).setBackgroundColor(getPrimaryColor());
        findViewById(R.id.Drawer_Body).setBackgroundColor(getDrawerBackground());
        findViewById(R.id.drawer_scrollbar).setBackgroundColor(getDrawerBackground());
        findViewById(R.id.Drawer_Body_Divider).setBackgroundColor(getIconColor());
/** TEXT VIEWS **/
        int color = getTextColor();
        defaultText.setTextColor(color);
        drawerSettingText.setTextColor(color);
        drawerAboutText.setTextColor(color);
        hiddenText.setTextColor(color);
        drawerShareText.setTextColor(color);
        drawerRateText.setTextColor(color);
        drawerUploadText.setTextColor(color);
        drawerTrashText.setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_Default_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_Setting_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_About_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_hidden_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_share_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_rate_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_Upload_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_TrashBin_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_favourite_Item)).setTextColor(color);
/** ICONS **/
        color = getIconColor();
        defaultIcon.setColor(color);
        drawerSettingIcon.setColor(color);
        drawerAboutIcon.setColor(color);
        hiddenIcon.setColor(color);
        drawerShareIcon.setColor(color);
        drawerRateIcon.setColor(color);
        drawerUploadIcon.setColor(color);
        drawerTrashIcon.setColor(color);
        favicon.setColor(color);
// Default setting
        if (localFolder)
            findViewById(R.id.ll_drawer_Default).setBackgroundColor(getHighlightedItemColor());
        else
            findViewById(R.id.ll_drawer_hidden).setBackgroundColor(getHighlightedItemColor());
        tint();

        findViewById(R.id.ll_drawer_Setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings = true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.ll_drawer_About).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about = true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.ll_drawer_favourites).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favourites = true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.ll_drawer_uploadhistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadHistory = true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.ll_drawer_trashbin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trashbin = true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
                //toolbar.setTitle("Trash Bin");
            }
        });

        findViewById(R.id.ll_drawer_Default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFolder = true;
                findViewById(R.id.ll_drawer_hidden).setBackgroundColor(Color.TRANSPARENT);
                findViewById(R.id.ll_drawer_Default).setBackgroundColor(getHighlightedItemColor());
                tint();
                toolbar.setTitle(getString(R.string.local_folder));
                hidden = false;
                mDrawerLayout.closeDrawer(GravityCompat.START);
                new PrepareAlbumTask(activityContext).execute();
            }
        });

        findViewById(R.id.ll_drawer_hidden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFolder = false;
                findViewById(R.id.ll_drawer_Default).setBackgroundColor(Color.TRANSPARENT);
                findViewById(R.id.ll_drawer_hidden).setBackgroundColor(getHighlightedItemColor());
                tint();
                toolbar.setTitle(getString(R.string.hidden_folder));
                if (securityObj.isActiveSecurity() && securityObj.isPasswordOnHidden()) {
                    final boolean[] passco = {false};
                    AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                    final EditText editTextPassword = securityObj.getInsertPasswordDialog(LFMainActivity.this, passwordDialogBuilder);
                    editTextPassword.setHintTextColor(getResources().getColor(R.color.grey, null));
                    passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                    editTextPassword.addTextChangedListener(new TextWatcher() {
                        @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                            //empty method body
                        }

                        @Override public void afterTextChanged(Editable editable) {
                            if(securityObj.getTextInputLayout().getVisibility() == View.VISIBLE && !passco[0]){
                                securityObj.getTextInputLayout().setVisibility(View.INVISIBLE);
                            }
                            else{
                                passco[0]=false;
                            }

                        }
                    });
                    final AlertDialog passwordDialog = passwordDialogBuilder.create();
                    passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    passwordDialog.show();
                    AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), passwordDialog);
                    passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                hidden = true;
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                new PrepareAlbumTask(activityContext).execute();
                                passwordDialog.dismiss();
                            } else {
                                passco[0] = true;
                                securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
                                SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.wrong_password), 0);
                                editTextPassword.getText().clear();
                                editTextPassword.requestFocus();
                            }
                        }
                    });
                } else {
                    hidden = true;
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    new PrepareAlbumTask(activityContext).execute();
                }
            }
        });

        findViewById(R.id.ll_share_phimpme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInviteClicked();
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.ll_rate_phimpme).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void onInviteClicked() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.install_phimpme) + "\n " + getString(R.string.invitation_deep_link));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
    //endregion

    private void updateSelectedStuff() {
        if (albumsMode) {
            if (getAlbums().getSelectedCount() == 0) {
                clearOverlay();
                checkForReveal = true;
                swipeRefreshLayout.setEnabled(true);
            } else {
                appBarOverlay();
                swipeRefreshLayout.setEnabled(false);
            }
            if (editMode)
                toolbar.setTitle(getAlbums().getSelectedCount() + "/" + getAlbums().dispAlbums.size());
            else {
                if (hidden)
                    toolbar.setTitle(getString(R.string.hidden_folder));
                else toolbar.setTitle(getString(R.string.local_folder));
                toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_menu));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDrawerLayout.openDrawer(GravityCompat.START);
                    }
                });
            }
        } else {

            if (!all_photos) {
                if (getAlbum().getSelectedCount() == 0) {
                    clearOverlay();
                    checkForReveal = true;
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    appBarOverlay();
                    swipeRefreshLayout.setEnabled(false);
                }

            } else {
                if (selectedMedias.size() == 0) {
                    clearOverlay();
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    appBarOverlay();
                    swipeRefreshLayout.setEnabled(false);
                }
            }

            if (editMode) {
                if (!all_photos && !fav_photos)
                    toolbar.setTitle(getAlbum().getSelectedCount() + "/" + getAlbum().getMedia().size());
                else if (!fav_photos && all_photos) {
                    toolbar.setTitle(selectedMedias.size() + "/" + size);
                } else if (fav_photos && !all_photos) {
                    toolbar.setTitle(selectedMedias.size() + "/" + favouriteslist.size());
                }
            } else {
                if (!all_photos && !fav_photos)
                    toolbar.setTitle(getAlbum().getName());
                else if (all_photos && !fav_photos) {
                    toolbar.setTitle(getString(R.string.all_media));
                } else if (fav_photos && !all_photos) {
                    toolbar.setTitle(getResources().getString(R.string.favourite_title));
                }
                toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_arrow_back));
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displayAlbums();
                    }
                });
            }
        }

        if (editMode) {
            toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_clear));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNavigationBar();
                    finishEditMode();
                    clearSelectedPhotos();
                }
            });
        }
    }

    //called from onBackPressed()

    private void finishEditMode() {
        if (editMode)
            enterReveal();
        editMode = false;
        if (albumsMode) {
            getAlbums().clearSelectedAlbums();
            albumsAdapter.notifyDataSetChanged();
        } else {
            if (!all_photos) {
                getAlbum().clearSelectedPhotos();
                mediaAdapter.notifyDataSetChanged();
            } else {
                clearSelectedPhotos();
                mediaAdapter.notifyDataSetChanged();
            }
        }
        invalidateOptionsMenu();
    }

    private void checkNothing() {
        nothingToShow.setTextColor(getTextColor());
        nothingToShow.setText(getString(R.string.there_is_nothing_to_show));
        nothingToShow.setVisibility((albumsMode && getAlbums().dispAlbums.size() == 0) ||
                (!albumsMode && getAlbum().getMedia().size() == 0) ? View.VISIBLE : View.GONE);
        TextView a = (TextView) findViewById(R.id.nothing_to_show);
        a.setTextColor(getTextColor());
        a.setVisibility((albumsMode && getAlbums().dispAlbums.size() == 0 && !fav_photos) || (!albumsMode && getAlbum
                ().getMedia().size() == 0 && !fav_photos) || (fav_photos && favouriteslist.size() == 0) ? View
                .VISIBLE : View
                .GONE);
        starImageView.setVisibility(View.GONE);
    }

    private void checkNothingFavourites() {
        nothingToShow.setTextColor(getTextColor());
        nothingToShow.setText(R.string.no_favourites_text);
        nothingToShow.setVisibility((albumsMode && getAlbums().dispAlbums.size() == 0 && !fav_photos) || (!albumsMode && getAlbum
                ().getMedia().size() == 0 && !fav_photos) || (fav_photos && favouriteslist.size() == 0) ? View
                .VISIBLE : View
                .GONE);
        starImageView.setVisibility((albumsMode && getAlbums().dispAlbums.size() == 0 && !fav_photos) || (!albumsMode && getAlbum
                ().getMedia().size() == 0 && !fav_photos) || (fav_photos && favouriteslist.size() == 0) ? View
                .VISIBLE : View
                .GONE);
        if (getBaseTheme() != LIGHT_THEME)
            starImageView.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
        else
            starImageView.setColorFilter(ContextCompat.getColor(this, R.color.accent_grey), PorterDuff.Mode.SRC_ATOP);
    }


    private void showsnackbar(Boolean result) {
        if(result) {
            SnackBarHandler.show(mDrawerLayout,getApplicationContext().getString(R.string.photo_deleted_msg), navigationView.getHeight());
        } else {
            SnackBarHandler.show(mDrawerLayout,getApplicationContext().getString(R.string.photo_deletion_failed), navigationView.getHeight());
        }
    }

    private void checkNoSearchResults(String result){
        textView.setText(getString(R.string.null_search_result) + " " + '"' + result + '"' );
        textView.setTextColor(getTextColor());
        textView.setVisibility(View.VISIBLE);
    }

    //region MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albums, menu);
        MenuItem menuitem = menu.findItem(R.id.search_action);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuitem);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override public void onFocusChange(final View view, boolean b) {
                if (b) {
                    view.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context
                                    .INPUT_METHOD_SERVICE);
                            imm.showSoftInput(view.findFocus(), 0);
                        }
                    }, 200);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context
                            .INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        if (albumsMode) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return searchTitle(newText);
                }
            });

            menu.findItem(R.id.select_all).setVisible(getAlbums().getSelectedCount() != albumsAdapter.getItemCount() ? true : false);
            menu.findItem(R.id.ascending_sort_action).setChecked(getAlbums().getSortingOrder() == SortingOrder.ASCENDING);
            switch (getAlbums().getSortingMode()) {
                case NAME:
                    menu.findItem(R.id.name_sort_action).setChecked(true);
                    break;
                case SIZE:
                    menu.findItem(R.id.size_sort_action).setChecked(true);
                    break;
                case DATE:
                default:
                    menu.findItem(R.id.date_taken_sort_action).setChecked(true);
                    break;
                case NUMERIC:
                    menu.findItem(R.id.numeric_sort_action).setChecked(true);
                    break;
            }

        } else {
            getfavouriteslist();
            menu.findItem(R.id.select_all).setVisible(getAlbum().getSelectedCount() == mediaAdapter
                    .getItemCount() || selectedMedias.size() == size || (selectedMedias.size() == favouriteslist.size
                    () && fav_photos) ? false : true);
            menu.findItem(R.id.ascending_sort_action).setChecked(getAlbum().settings.getSortingOrder() == SortingOrder.ASCENDING);
            switch (getAlbum().settings.getSortingMode()) {
                case NAME:
                    menu.findItem(R.id.name_sort_action).setChecked(true);
                    break;
                case SIZE:
                    menu.findItem(R.id.size_sort_action).setChecked(true);
                    break;
                case DATE:
                default:
                    menu.findItem(R.id.date_taken_sort_action).setChecked(true);
                    break;
                case NUMERIC:
                    menu.findItem(R.id.numeric_sort_action).setChecked(true);
                    break;
            }
        }

        menu.findItem(R.id.hideAlbumButton).setTitle(hidden ? getString(R.string.unhide) : getString(R.string.hide));
        menu.findItem(R.id.delete_action).setIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_delete));
        menu.findItem(R.id.sort_action).setIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_sort));
        menu.findItem(R.id.sharePhotos).setIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_share));
        return true;
    }

    public boolean searchTitle(String newText) {
        if (!fromOnClick) {
            String queryText = newText;
            queryText = queryText.toLowerCase();
            final ArrayList<Album> newList = new ArrayList<>();
            for (Album album : albList) {
                String name = album.getName().toLowerCase();
                if (name.contains(queryText)) {
                    newList.add(album);
                }
            }
            if(newList.isEmpty()){
                checkNoSearchResults(newText);
            }
            else{
                if(textView.getVisibility() == View.VISIBLE){
                    textView.setVisibility(View.INVISIBLE);
                }
            }
            albumsAdapter.swapDataSet(newList);
        } else {
            fromOnClick = false;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (albumsMode) {
            editMode = getAlbums().getSelectedCount() != 0;
            menu.setGroupVisible(R.id.album_options_menu, editMode);
            menu.setGroupVisible(R.id.photos_option_men, false);
            menu.findItem(R.id.all_photos).setVisible(!editMode && !hidden);
            menu.findItem(R.id.search_action).setVisible(!editMode);
            menu.findItem(R.id.create_gif).setVisible(false);
            menu.findItem(R.id.select_all).setVisible(getAlbums().getSelectedCount() != albumsAdapter.getItemCount() ? true : false);
            menu.findItem(R.id.settings).setVisible(false);


            if (getAlbums().getSelectedCount() >= 1) {
                if (getAlbums().getSelectedCount() > 1) {
                    menu.findItem(R.id.album_details).setVisible(false);
                }
                if (getAlbums().getSelectedCount() == 1) {
                    menu.findItem(R.id.search_action).setVisible(false);
                }
            }
        } else {
            menu.findItem(R.id.search_action).setVisible(false);
            if (!all_photos && !fav_photos) {
                editMode = getAlbum().areMediaSelected();
                menu.setGroupVisible(R.id.photos_option_men, editMode);
                menu.setGroupVisible(R.id.album_options_menu, !editMode);
                menu.findItem(R.id.settings).setVisible(!editMode);
                menu.findItem(R.id.all_photos).setVisible(false);
                menu.findItem(R.id.album_details).setVisible(false);
            } else if (all_photos && !fav_photos) {
                editMode = selectedMedias.size() != 0;
                menu.setGroupVisible(R.id.photos_option_men, editMode);
                menu.setGroupVisible(R.id.album_options_menu, !editMode);
                menu.findItem(R.id.all_photos).setVisible(false);
                menu.findItem(R.id.action_move).setVisible(false);
                menu.findItem(R.id.settings).setVisible(!editMode);
                menu.findItem(R.id.album_details).setVisible(false);
            } else if (!all_photos && fav_photos) {
                editMode = selectedMedias.size() != 0;
                menu.setGroupVisible(R.id.photos_option_men, editMode);
                menu.setGroupVisible(R.id.album_options_menu, !editMode);
                menu.findItem(R.id.settings).setVisible(!editMode);
                menu.findItem(R.id.create_gif).setVisible(false);
                menu.findItem(R.id.album_details).setVisible(false);
                menu.findItem(R.id.all_photos).setVisible(false);
            }
            menu.findItem(R.id.select_all).setVisible(getAlbum().getSelectedCount() == mediaAdapter
                    .getItemCount() || selectedMedias.size() == size || (selectedMedias.size() == favouriteslist.size
                    () && fav_photos) ? false : true);
        }

        togglePrimaryToolbarOptions(menu);
        updateSelectedStuff();
        if(!albumsMode)
            visible = getAlbum().getSelectedCount() > 0;
        else
            visible = false;
        menu.findItem(R.id.action_copy).setVisible(visible);
        menu.findItem(R.id.action_move).setVisible((visible || editMode) && !fav_photos);
        menu.findItem(R.id.action_add_favourites).setVisible((visible || editMode) && (!albumsMode && !fav_photos));
        menu.findItem(R.id.excludeAlbumButton).setVisible(editMode && !all_photos && albumsMode && !fav_photos);
        menu.findItem(R.id.zipAlbumButton).setVisible(editMode && !all_photos && albumsMode && !fav_photos && !hidden &&
                getAlbums().getSelectedCount() == 1);
        menu.findItem(R.id.delete_action).setVisible((!albumsMode || editMode) && (!all_photos || editMode));
        if(fav_photos && favouriteslist.size() == 0 ){
            menu.findItem(R.id.delete_action).setVisible(false);
            menu.findItem(R.id.sort_action).setVisible(false);
        }
        menu.findItem(R.id.hideAlbumButton).setVisible(!all_photos && !fav_photos && getAlbums().getSelectedCount() >
                0);

        menu.findItem(R.id.clear_album_preview).setVisible(!albumsMode && getAlbum().hasCustomCover() && !fav_photos && !all_photos);
        menu.findItem(R.id.renameAlbum).setVisible(((albumsMode && getAlbums().getSelectedCount() == 1) ||
                (!albumsMode && !editMode)) && (!all_photos && !fav_photos));
        if (getAlbums().getSelectedCount() == 1)
            menu.findItem(R.id.set_pin_album).setTitle(getAlbums().getSelectedAlbum(0).isPinned() ? getString(R.string.un_pin) : getString(R.string.pin));
        menu.findItem(R.id.set_pin_album).setVisible(albumsMode && getAlbums().getSelectedCount() == 1);
        menu.findItem(R.id.setAsAlbumPreview).setVisible(!albumsMode && !all_photos && getAlbum()
                .getSelectedCount() == 1);
        menu.findItem(R.id.affixPhoto).setVisible((!albumsMode && (getAlbum().getSelectedCount() > 1) ||
                selectedMedias.size() > 1) && !fav_photos);
        if (albumsMode)
            menu.findItem(R.id.action_move).setVisible(getAlbums().getSelectedCount() == 1);
        return super.onPrepareOptionsMenu(menu);
    }

    private void togglePrimaryToolbarOptions(final Menu menu) {
        menu.setGroupVisible(R.id.general_action, !editMode);

    }

    //endregion

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getNavigationBar();
        switch (item.getItemId()) {

            case R.id.all_photos:
                if (!all_photos) {
                    all_photos = true;
                    displayAllMedia(true);
                } else {
                    displayAlbums();
                }
                return true;

            case R.id.album_details:
                AlertDialog.Builder detailsDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                AlertDialog detailsDialog;
                detailsDialog =
                        AlertDialogsHelper.getAlbumDetailsDialog(this, detailsDialogBuilder, getAlbums().getSelectedAlbum(0));

                detailsDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string
                        .ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishEditMode();
                    }
                });
                detailsDialog.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE}, getAccentColor(), detailsDialog);
                return true;

            case R.id.select_all:
                if (albumsMode) {
                    getAlbums().selectAllAlbums();
                    albumsAdapter.notifyDataSetChanged();
                } else {
                    if (!all_photos && !fav_photos) {
                        getAlbum().selectAllPhotos();
                        mediaAdapter.notifyDataSetChanged();
                    } else if(all_photos && !fav_photos){
                            clearSelectedPhotos();
                            selectAllPhotos();
                        mediaAdapter.notifyDataSetChanged();
                    }
                    else if(fav_photos && !all_photos){
                            clearSelectedPhotos();
                            selectAllPhotos();
                        Collections.sort(favouriteslist, MediaComparators.getComparator(getAlbum().settings.getSortingMode(),
                                getAlbum().settings.getSortingOrder()));
                        mediaAdapter.swapDataSet(favouriteslist, true);
                    }
                }
                invalidateOptionsMenu();
                return true;

            case R.id.create_gif:
                new CreateGIFTask().execute();
                return true;


            case R.id.set_pin_album:
                getAlbums().getSelectedAlbum(0).settings.togglePin(getApplicationContext());
                getAlbums().sortAlbums();
                getAlbums().clearSelectedAlbums();
                invalidateOptionsMenu();
                albumsAdapter.notifyDataSetChanged();
                return true;

            case R.id.settings:
                startActivity(new Intent(LFMainActivity.this, SettingsActivity.class));
                return true;

            case R.id.hideAlbumButton:
                final AlertDialog.Builder hideDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());

                AlertDialogsHelper.getTextDialog(LFMainActivity.this, hideDialogBuilder,
                        hidden ? R.string.unhide : R.string.hide,
                        hidden ? R.string.unhide_album_message : R.string.hide_album_message, null);

                hideDialogBuilder.setPositiveButton(getString(hidden ? R.string.unhide : R.string.hide).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (albumsMode) {
                            if (hidden) getAlbums().unHideSelectedAlbums(getApplicationContext());
                            else getAlbums().hideSelectedAlbums(getApplicationContext());
                            albumsAdapter.notifyDataSetChanged();
                            invalidateOptionsMenu();
                        } else {
                            if (hidden)
                                getAlbums().unHideAlbum(getAlbum().getPath(), getApplicationContext());
                            else
                                getAlbums().hideAlbum(getAlbum().getPath(), getApplicationContext());
                            displayAlbums(true);
                        }
                    }
                });
                if (!hidden) {
                    hideDialogBuilder.setNeutralButton(this.getString(R.string.exclude).toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (albumsMode) {
                                getAlbums().excludeSelectedAlbums(getApplicationContext());
                                albumsAdapter.notifyDataSetChanged();
                                invalidateOptionsMenu();
                            } else {
                                customAlbumsHelper.excludeAlbum(getAlbum().getPath());
                                displayAlbums(true);
                            }
                        }
                    });
                }
                hideDialogBuilder.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);
                AlertDialog alertDialog = hideDialogBuilder.create();
                alertDialog.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL}, getAccentColor(), alertDialog);
                return true;
            
            case R.id.delete_action:
                getNavigationBar();
                class DeletePhotos extends AsyncTask<String, Integer, Boolean> {

                    private boolean succ = false;
                    private int imagesUnfav = 0;

                    @Override
                    protected void onPreExecute() {
                        swipeRefreshLayout.setRefreshing(true);
                        super.onPreExecute();
                    }

                    @Override
                    protected Boolean doInBackground(String... arg0) {
                        //if in album mode, delete selected albums
                        if (albumsMode)
                            succ = getAlbums().deleteSelectedAlbums(LFMainActivity.this);
                        else {
                            // if in selection mode, delete selected media
                            if (editMode) {
                                if (!all_photos && !fav_photos) {
                                    //clearSelectedPhotos();
                                    if (AlertDialogsHelper.check)  {
                                        succ = addToTrash();
                                        if (succ) {
                                            addTrashObjectsToRealm(getAlbum().getSelectedMedia());
                                        }
                                        getAlbum().clearSelectedPhotos();
                                    } else {
                                        succ = getAlbum().deleteSelectedMedia(getApplicationContext());
                                    }
                                } else if (all_photos && !fav_photos) {
                                   // addToTrash();
                                    if (AlertDialogsHelper.check) {
                                        succ = addToTrash();
                                        if (succ) {
                                            addTrashObjectsToRealm(selectedMedias);
                                        }
                                    } else {
                                        for (Media media : selectedMedias) {
                                            String[] projection = {MediaStore.Images.Media._ID};

                                            // Match on the file path
                                            String selection = MediaStore.Images.Media.DATA + " = ?";
                                            String[] selectionArgs = new String[]{media.getPath()};

                                            // Query for the ID of the media matching the file path
                                            Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                            ContentResolver contentResolver = getContentResolver();
                                            Cursor c =
                                                    contentResolver
                                                            .query(queryUri, projection, selection, selectionArgs,
                                                                    null);
                                            if (c.moveToFirst()) {
                                                // We found the ID. Deleting the item via the content provider will also remove the file
                                                long id =
                                                        c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                                Uri deleteUri = ContentUris
                                                        .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                                id);
                                                contentResolver.delete(deleteUri, null, null);
                                                succ = true;
                                            } else {
                                                succ = false;
                                                // File not found in media store DB
                                            }
                                            c.close();
                                        }
                                    }
                                } else if (!all_photos && fav_photos) {
                                    realm = Realm.getDefaultInstance();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            for (int i = 0; i < selectedMedias.size(); i++) {
                                                RealmResults<FavouriteImagesModel> favouriteImagesModels = realm.where
                                                        (FavouriteImagesModel.class)
                                                        .equalTo("path", selectedMedias.get(i).getPath()).findAll();
                                                imagesUnfav++;
                                                favouriteImagesModels.deleteAllFromRealm();
                                            }
                                        }
                                    });
                                    succ = true;
                                }
                            }
                            // if not in selection mode, delete current album entirely
                            else if (!editMode) {
                                if (!fav_photos) {
                                    if (AlertDialogsHelper.check) {
                                        succ = addToTrash();
                                        if (succ) {
                                            addTrashObjectsToRealm(getAlbum().getMedia());
                                        }
                                        //succ = getAlbums().deleteAlbum(getAlbum(), getApplicationContext());
                                        getAlbum().getMedia().clear();
                                    } else {
                                        succ = getAlbums().deleteAlbum(getAlbum(), getApplicationContext());
                                        getAlbum().getMedia().clear();
                                    }
                                } else {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override public void execute(Realm realm) {
                                            RealmQuery<FavouriteImagesModel> favouriteImagesModelRealmQuery = realm
                                                    .where(FavouriteImagesModel.class);
                                            succ = favouriteImagesModelRealmQuery.findAll().deleteAllFromRealm();
                                            favouriteslist.clear();
                                        }
                                    });
                                }
                            }
                        }
                        return succ;
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            // in albumsMode, the selected albums have been deleted.
                            if (albumsMode) {
                                getAlbums().clearSelectedAlbums();
                                albumsAdapter.notifyDataSetChanged();
                            } else {
                                if (!all_photos && !fav_photos) {
                                    //if all media in current album have been deleted, delete current album too.
                                    if (getAlbum().getMedia().size() == 0) {
                                        getAlbums().removeCurrentAlbum();
                                        albumsAdapter.notifyDataSetChanged();
                                        displayAlbums();
                                        showsnackbar(succ);
                                        swipeRefreshLayout.setRefreshing(true);
                                    } else
                                        mediaAdapter.swapDataSet(getAlbum().getMedia(), false);
                                } else if(all_photos && !fav_photos){
                                    clearSelectedPhotos();
                                    listAll = StorageProvider.getAllShownImages(LFMainActivity.this);
                                    media = listAll;
                                    size = listAll.size();
                                    showsnackbar(succ);
                                    Collections.sort(listAll, MediaComparators.getComparator(getAlbum().settings
                                            .getSortingMode(), getAlbum().settings.getSortingOrder()));
                                    mediaAdapter.swapDataSet(listAll, false);
                                }
                                else if(fav_photos && !all_photos){
                                    if (imagesUnfav >= 2)
                                        SnackBarHandler.show(mDrawerLayout, imagesUnfav + " " + getResources().getString(R.string.remove_from_favourite));
                                    else
                                        SnackBarHandler.show(mDrawerLayout, getResources().getString(R.string.single_image_removed));
                                    clearSelectedPhotos();
                                    getfavouriteslist();
                                    new FavouritePhotos(activityContext).execute();
                                }
                            }
                        } else requestSdCardPermissions();

                        invalidateOptionsMenu();
                        checkNothing();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());

                if(fav_photos && !all_photos)
                    AlertDialogsHelper.getTextDialog(this, deleteDialog, R.string.remove_from_favourites, R.string.remove_favourites_body, null);
                else
                    AlertDialogsHelper.getTextCheckboxDialog(this, deleteDialog, R.string.delete, albumsMode || !editMode ?
                    R.string.delete_album_message :
                    R.string.delete_photos_message, null, getResources().getString(R.string.move_to_trashbin), getAccentColor());

                deleteDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                deleteDialog.setPositiveButton(fav_photos && !all_photos ? getString(R.string.remove).toUpperCase() : getString(R.string.delete).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (securityObj.isActiveSecurity() && securityObj.isPasswordOnDelete()) {
                            final boolean passco[] = {false};
                            AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                            final EditText editTextPassword = securityObj.getInsertPasswordDialog(LFMainActivity.this, passwordDialogBuilder);
                            editTextPassword.setHintTextColor(getResources().getColor(R.color.grey, null));
                            passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);
                            passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //This should be empty. It will be overwritten later
                                    //to avoid dismiss of the dialog on wrong password
                                }
                            });
                            editTextPassword.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    //empty method body
                                }

                                @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    //empty method body
                                }

                                @Override public void afterTextChanged(Editable editable) {
                                    if(securityObj.getTextInputLayout().getVisibility() == View.VISIBLE && !passco[0]){
                                        securityObj.getTextInputLayout().setVisibility(View.INVISIBLE);
                                    }
                                    else{
                                        passco[0]=false;
                                    }
                                }
                            });

                            final AlertDialog passwordDialog = passwordDialogBuilder.create();
                            passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            passwordDialog.show();
                            AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), passwordDialog);
                            passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // if password is correct, call DeletePhotos and perform deletion
                                    if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                        passwordDialog.dismiss();
                                        new DeletePhotos().execute();
                                    }
                                    // if password is incorrect, don't delete and notify user of incorrect password
                                    else {
                                        passco[0] = true;
                                        securityObj.getTextInputLayout().setVisibility(View.VISIBLE);
                                        SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.wrong_password), navigationView.getHeight());
                                        editTextPassword.getText().clear();
                                        editTextPassword.requestFocus();
                                    }
                                }
                            });
                        } else {
                            new DeletePhotos().execute();
                        }
                    }
                });
                AlertDialog alertDialogDelete = deleteDialog.create();
                alertDialogDelete.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialogDelete);

                return true;
            case R.id.excludeAlbumButton:
                final AlertDialog.Builder excludeDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());

                final View excludeDialogLayout = getLayoutInflater().inflate(R.layout.dialog_exclude, null);
                TextView textViewExcludeTitle = (TextView) excludeDialogLayout.findViewById(R.id.text_dialog_title);
                TextView textViewExcludeMessage = (TextView) excludeDialogLayout.findViewById(R.id.text_dialog_message);
                final Spinner spinnerParents = (Spinner) excludeDialogLayout.findViewById(R.id.parents_folder);

                spinnerParents.getBackground().setColorFilter(getIconColor(), PorterDuff.Mode.SRC_ATOP);

                ((CardView) excludeDialogLayout.findViewById(R.id.message_card)).setCardBackgroundColor(getCardBackgroundColor());
                textViewExcludeTitle.setBackgroundColor(getPrimaryColor());
                textViewExcludeTitle.setText(getString(R.string.exclude));

                if ((albumsMode && getAlbums().getSelectedCount() > 1)) {
                    textViewExcludeMessage.setText(R.string.exclude_albums_message);
                    spinnerParents.setVisibility(View.GONE);
                } else {
                    textViewExcludeMessage.setText(R.string.exclude_album_message);
                    spinnerParents.setAdapter(getSpinnerAdapter(albumsMode ? getAlbums().getSelectedAlbum(0).getParentsFolders() : getAlbum().getParentsFolders()));
                }

                textViewExcludeMessage.setTextColor(getTextColor());
                excludeDialogBuilder.setView(excludeDialogLayout);

                excludeDialogBuilder.setPositiveButton(this.getString(R.string.exclude).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if ((albumsMode && getAlbums().getSelectedCount() > 1)) {
                            getAlbums().excludeSelectedAlbums(getApplicationContext());
                            albumsAdapter.notifyDataSetChanged();
                            invalidateOptionsMenu();
                        } else {
                            customAlbumsHelper.excludeAlbum(spinnerParents.getSelectedItem().toString());
                            finishEditMode();
                            displayAlbums(true);
                        }
                    }
                });
                excludeDialogBuilder.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);
                AlertDialog alertDialogExclude = excludeDialogBuilder.create();
                alertDialogExclude.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), alertDialogExclude);
                return true;

            case R.id.zipAlbumButton:

                path = new ArrayList<>();
                File folder = new File(getAlbums().getSelectedAlbum(0).getPath() + "/");
                File[] fpath = folder.listFiles();
                for (int i = 0; i < fpath.length; i++) {
                    if (fpath[i].getPath().endsWith(".jpg")) {
                        path.add(fpath[i].getPath());
                    }

                }
                new ZipAlbumTask().execute();
                return true;

            case R.id.sharePhotos:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sent_to_action));

                // list of all selected media in current album
                ArrayList<Uri> files = new ArrayList<Uri>();
                if (!all_photos && !fav_photos) {
                    for (Media f : getAlbum().getSelectedMedia())
                        files.add(f.getUri());
                } else if (all_photos && !fav_photos) {
                    for (Media f : selectedMedias)
                        files.add(f.getUri());
                } else if (fav_photos && !all_photos) {
                    for (Media m : selectedMedias) {
                        files.add(m.getUri());
                    }
                }

                if (!all_photos && !fav_photos) {
                    for (Media f : getAlbum().getSelectedMedia()) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        UploadHistoryRealmModel uploadHistory;
                        uploadHistory = realm.createObject(UploadHistoryRealmModel.class);
                        uploadHistory.setName("OTHERS");
                        uploadHistory.setPathname(f.getPath());
                        uploadHistory.setDatetime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                        uploadHistory.setStatus(getString(R.string.upload_done));
                        realm.commitTransaction();
                        Intent result = new Intent();
                        result.putExtra(Constants.SHARE_RESULT, 0);
                        setResult(RESULT_OK, result);
                    }
                } else if (all_photos || fav_photos) {
                    for (Media m : selectedMedias) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        UploadHistoryRealmModel uploadHistory;
                        uploadHistory = realm.createObject(UploadHistoryRealmModel.class);
                        uploadHistory.setName("OTHERS");
                        uploadHistory.setPathname(m.getPath());
                        uploadHistory.setDatetime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                        uploadHistory.setStatus(getString(R.string.upload_done));
                        realm.commitTransaction();
                        Intent result = new Intent();
                        result.putExtra(Constants.SHARE_RESULT, 0);
                        setResult(RESULT_OK, result);
                    }
                }


                String extension = files.get(0).getPath().substring(files.get(0).getPath().lastIndexOf('.') + 1);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                if (!all_photos && !fav_photos)
                    intent.setType(StringUtils.getGenericMIME(getAlbum().getSelectedMedia(0).getMimeType()));
                else if (all_photos && !fav_photos)
                    intent.setType(mimeType);
                else if (fav_photos && !all_photos)
                    intent.setType(mimeType);
                finishEditMode();
                startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
                return true;


            case R.id.name_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(NAME);
                    new SortingUtilsAlbums(activityContext).execute();
                } else {
                    new SortModeSet(activityContext).execute(NAME);
                    if (!all_photos && !fav_photos) {
                        new SortingUtilsPhtots(activityContext).execute();
                    } else if (all_photos && !fav_photos) {
                        new SortingUtilsListAll(activityContext).execute();
                    } else if (fav_photos && !all_photos) {
                        new SortingUtilsFavouritelist(activityContext).execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(DATE);
                    new SortingUtilsAlbums(activityContext).execute();
                } else {
                    new SortModeSet(activityContext).execute(DATE);
                    if (!all_photos && !fav_photos) {
                        new SortingUtilsPhtots(activityContext).execute();
                    } else if (all_photos && !fav_photos) {
                        new SortingUtilsListAll(activityContext).execute();
                    } else if (fav_photos && !all_photos) {
                        new SortingUtilsFavouritelist(activityContext).execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.size_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(SIZE);
                    new SortingUtilsAlbums(activityContext).execute();
                } else {
                    new SortModeSet(activityContext).execute(SIZE);
                    if (!all_photos && !fav_photos) {
                        new SortingUtilsPhtots(activityContext).execute();
                    } else if (all_photos && !fav_photos) {
                        new SortingUtilsListAll(activityContext).execute();
                    } else if (fav_photos && !all_photos) {
                        new SortingUtilsFavouritelist(activityContext).execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.numeric_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(NUMERIC);
                    new SortingUtilsAlbums(activityContext).execute();
                } else {
                    new SortModeSet(activityContext).execute(NUMERIC);
                    if (!all_photos && !fav_photos) {
                        new SortingUtilsPhtots(activityContext).execute();
                    } else if (all_photos && !fav_photos) {
                        new SortingUtilsListAll(activityContext).execute();
                    } else if (fav_photos && !all_photos) {
                        new SortingUtilsFavouritelist(activityContext).execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingAscending(item.isChecked() ? SortingOrder.DESCENDING : SortingOrder.ASCENDING);
                    new SortingUtilsAlbums(activityContext).execute();
                } else {
                    getAlbum().setDefaultSortingAscending(getApplicationContext(), item.isChecked() ? SortingOrder.DESCENDING : SortingOrder.ASCENDING);
                    if (!all_photos && !fav_photos) {
                        new SortingUtilsPhtots(activityContext).execute();
                    } else if (all_photos && !fav_photos) {
                        new SortingUtilsListAll(activityContext).execute();
                    } else if (fav_photos && !all_photos) {
                        new SortingUtilsFavouritelist(activityContext).execute();
                    }
                }
                item.setChecked(!item.isChecked());
                return true;

            //region Affix
            case R.id.affixPhoto:

                //region Async MediaAffix
                class affixMedia extends AsyncTask<Affix.Options, Integer, Void> {
                    private AlertDialog dialog;

                    @Override
                    protected void onPreExecute() {
                        AlertDialog.Builder progressDialog = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());

                        dialog = AlertDialogsHelper.getProgressDialog(LFMainActivity.this, progressDialog,
                                getString(R.string.affix), getString(R.string.affix_text));
                        dialog.show();
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Affix.Options... arg0) {
                        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
                        if (!all_photos) {
                            for (int i = 0; i < getAlbum().getSelectedCount(); i++) {
                                bitmapArray.add(getBitmap(getAlbum().getSelectedMedia(i).getPath()));
                            }
                        } else {
                            for (int i = 0; i < selectedMedias.size(); i++) {
                                bitmapArray.add(getBitmap(selectedMedias.get(i).getPath()));
                            }
                        }

                        if (bitmapArray.size() > 1)
                            Affix.AffixBitmapList(getApplicationContext(), bitmapArray, arg0[0]);
                        else runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.affix_error), navigationView.getHeight());

                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        editMode = false;
                        if (!all_photos)
                            getAlbum().clearSelectedPhotos();
                        else clearSelectedPhotos();
                        dialog.dismiss();
                        invalidateOptionsMenu();
                        mediaAdapter.notifyDataSetChanged();
                        if (!all_photos)
                            new PreparePhotosTask(activityContext).execute();
                        else clearSelectedPhotos();
                    }
                }
                //endregion

                final AlertDialog.Builder builder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_affix, null);

                dialogLayout.findViewById(R.id.affix_title).setBackgroundColor(getPrimaryColor());
                ((CardView) dialogLayout.findViewById(R.id.affix_card)).setCardBackgroundColor(getCardBackgroundColor());

                //ITEMS
                final SwitchCompat swVertical = (SwitchCompat) dialogLayout.findViewById(R.id.affix_vertical_switch);
                final SwitchCompat swSaveHere = (SwitchCompat) dialogLayout.findViewById(R.id.save_here_switch);

                final RadioGroup radioFormatGroup = (RadioGroup) dialogLayout.findViewById(R.id.radio_format);

                final TextView txtQuality = (TextView) dialogLayout.findViewById(R.id.affix_quality_title);
                final SeekBar seekQuality = (SeekBar) dialogLayout.findViewById(R.id.seek_bar_quality);

                //region THEME STUFF
                setScrollViewColor((ScrollView) dialogLayout.findViewById(R.id.affix_scrollView));

                /** TextViews **/
                int color = getTextColor();
                ((TextView) dialogLayout.findViewById(R.id.affix_vertical_title)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.compression_settings_title)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.save_here_title)).setTextColor(color);

                /** Sub TextViews **/
                color = getTextColor();
                ((TextView) dialogLayout.findViewById(R.id.save_here_sub)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.affix_vertical_sub)).setTextColor(color);
                ((TextView) dialogLayout.findViewById(R.id.affix_format_sub)).setTextColor(color);
                txtQuality.setTextColor(color);

                /** Icons **/
                color = getIconColor();
                ((IconicsImageView) dialogLayout.findViewById(R.id.affix_quality_icon)).setColor(color);
                ((IconicsImageView) dialogLayout.findViewById(R.id.affix_format_icon)).setColor(color);
                ((IconicsImageView) dialogLayout.findViewById(R.id.affix_vertical_icon)).setColor(color);
                ((IconicsImageView) dialogLayout.findViewById(R.id.save_here_icon)).setColor(color);

                seekQuality.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN));
                seekQuality.getThumb().setColorFilter(new PorterDuffColorFilter(getAccentColor(), PorterDuff.Mode.SRC_IN));

                updateRadioButtonColor((RadioButton) dialogLayout.findViewById(R.id.radio_jpeg));
                updateRadioButtonColor((RadioButton) dialogLayout.findViewById(R.id.radio_png));
                updateRadioButtonColor((RadioButton) dialogLayout.findViewById(R.id.radio_webp));

                updateSwitchColor(swVertical, getAccentColor());
                updateSwitchColor(swSaveHere, getAccentColor());
                //endregion

                seekQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        txtQuality.setText(Html.fromHtml(
                                String.format(Locale.getDefault(), "%s <b>%d</b>", getString(R.string.quality), progress)));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                seekQuality.setProgress(90); //DEFAULT

                swVertical.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        updateSwitchColor(swVertical, getAccentColor());
                    }
                });

                swSaveHere.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        updateSwitchColor(swSaveHere, getAccentColor());
                    }
                });
                builder.setView(dialogLayout);
                builder.setPositiveButton(this.getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Bitmap.CompressFormat compressFormat;
                        switch (radioFormatGroup.getCheckedRadioButtonId()) {
                            case R.id.radio_jpeg:
                            default:
                                compressFormat = Bitmap.CompressFormat.JPEG;
                                break;
                            case R.id.radio_png:
                                compressFormat = Bitmap.CompressFormat.PNG;
                                break;
                            case R.id.radio_webp:
                                compressFormat = Bitmap.CompressFormat.WEBP;
                                break;
                        }

                        Affix.Options options = new Affix.Options(
                                swSaveHere.isChecked() ? getAlbum().getPath() : Affix.getDefaultDirectoryPath(),
                                compressFormat,
                                seekQuality.getProgress(),
                                swVertical.isChecked());
                        new affixMedia().execute(options);
                    }
                });
                builder.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);

                AlertDialog affixDialog = builder.create();
                affixDialog.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE}, getAccentColor(), affixDialog);


                return true;
            //endregion

            case R.id.action_move:
                final Snackbar[] snackbar = {null};
                final ArrayList<Media> dr = getselecteditems();
                final String[] pathofalbum = {null};
                bottomSheetDialogFragment = new SelectAlbumBottomSheet();
                bottomSheetDialogFragment.setTitle(getString(R.string.move_to));
                if (!albumsMode) {
                    bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
                        @Override
                        public void folderSelected(final String path) {
                            final ArrayList<Media> stringio = storeTemporaryphotos(path);
                            pathofalbum[0] = path;
                            swipeRefreshLayout.setRefreshing(true);
                            int numberOfImagesMoved;

                            if ((numberOfImagesMoved = getAlbum().moveSelectedMedia(getApplicationContext(), path)) > 0) {

                                if (getAlbum().getMedia().size() == 0) {
                                    getAlbums().removeCurrentAlbum();
                                    albumsAdapter.notifyDataSetChanged();
                                    displayAlbums();
                                }
                                mediaAdapter.swapDataSet(getAlbum().getMedia(), false);
                                finishEditMode();
                                invalidateOptionsMenu();
                                checkForFavourites(path, dr);
                                checkDescription(path, dr);
                                if (numberOfImagesMoved > 1){
                                    snackbar[0] = SnackBarHandler.showWithBottomMargin2(mDrawerLayout, getString(R.string.photos_moved_successfully), navigationView.getHeight(), Snackbar.LENGTH_SHORT);
                                    snackbar[0].setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getAlbum().moveAllMedia(getApplicationContext(), getAlbum().getPath(), stringio);
                                        }
                                    });
                                    snackbar[0].show();
                                }

                                else{
                                    Snackbar snackbar1 = SnackBarHandler.showWithBottomMargin2(mDrawerLayout, getString(R.string.photo_moved_successfully), navigationView.getHeight(), Snackbar.LENGTH_SHORT);
                                    snackbar1.setAction("UNDO", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            getAlbum().moveAllMedia(getApplicationContext(), getAlbum().getPath(), stringio);
                                        }
                                    });
                                    snackbar1.show();

                                }

                            } else if (numberOfImagesMoved == -1 && getAlbum().getPath().equals(path)) {
                                //moving to the same folder
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                                alertDialog.setCancelable(false);
                                AlertDialogsHelper.getTextDialog(LFMainActivity.this, alertDialog, R.string.move_to, R.string.move, null);

                                alertDialog.setNeutralButton(getString(R.string.make_copies).toUpperCase(), new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {
                                        new CopyPhotos(path, true, false, activityContext).execute();
                                    }
                                });
                                alertDialog.setPositiveButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                                alertDialog.setNegativeButton(getString(R.string.replace).toUpperCase(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        finishEditMode();
                                        invalidateOptionsMenu();
                                        SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.photo_moved_successfully), navigationView.getHeight());

                                    }
                                });

                                AlertDialog alert = alertDialog.create();
                                alert.show();
                                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEGATIVE, DialogInterface.BUTTON_NEUTRAL}, getAccentColor(), alert);

                            } else

                                requestSdCardPermissions();

                            swipeRefreshLayout.setRefreshing(false);
                            bottomSheetDialogFragment.dismiss();
                        }
                    });
                    bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                } else {
                    AlertDialog.Builder alertDialogMoveAll = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                    alertDialogMoveAll.setCancelable(false);
                    AlertDialogsHelper.getTextDialog(LFMainActivity.this, alertDialogMoveAll, R.string.move_to, R.string.move_all_photos, null);
                    alertDialogMoveAll.setPositiveButton(R.string.ok_action, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        }
                    });
                    alertDialogMoveAll.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
                        @Override
                        public void folderSelected(String path) {
                            swipeRefreshLayout.setRefreshing(true);
                            if (getAlbums().moveSelectedAlbum(LFMainActivity.this, path)) {
                                SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.moved_target_folder_success), SnackBarHandler.LONG);
                                getAlbums().deleteSelectedAlbums(LFMainActivity.this);
                                getAlbums().clearSelectedAlbums();
                                new PrepareAlbumTask(activityContext).execute();
                            } else {
                                requestSdCardPermissions();
                                swipeRefreshLayout.setRefreshing(false);
                                invalidateOptionsMenu();
                            }
                            bottomSheetDialogFragment.dismiss();
                        }
                    });
                    AlertDialog dialog = alertDialogMoveAll.create();
                    dialog.show();
                    AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface
                            .BUTTON_NEGATIVE}, getAccentColor(), dialog);
                }
                return true;

            case R.id.action_add_favourites:
                new AddToFavourites().execute();
                return true;

            case R.id.action_copy:
                bottomSheetDialogFragment = new SelectAlbumBottomSheet();
                bottomSheetDialogFragment.setTitle(getString(R.string.copy_to));
                bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
                    @Override
                    public void folderSelected(String path) {
                        new CopyPhotos(path, false, true, activityContext).execute();
                        bottomSheetDialogFragment.dismiss();
                    }
                });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                return true;

            case R.id.renameAlbum:
                AlertDialog.Builder renameDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                final EditText editTextNewName = new EditText(getApplicationContext());
                editTextNewName.setText(albumsMode ? getAlbums().getSelectedAlbum(0).getName() : getAlbum().getName());
                editTextNewName.setSelectAllOnFocus(true);
                editTextNewName.setHint(R.string.description_hint);
                editTextNewName.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.grey));
                editTextNewName.setHighlightColor(ContextCompat.getColor(getApplicationContext(), R.color.cardview_shadow_start_color));
                editTextNewName.selectAll();
                editTextNewName.setSingleLine(false);
                final String albumName = albumsMode ? getAlbums().getSelectedAlbum(0).getName() : getAlbum().getName();

                AlertDialogsHelper.getInsertTextDialog(LFMainActivity.this, renameDialogBuilder,
                        editTextNewName, R.string.rename_album, null);

                renameDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);

                renameDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //This should br empty it will be overwrite later
                        //to avoid dismiss of the dialog
                    }
                });
                final AlertDialog renameDialog = renameDialogBuilder.create();
                renameDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_IS_FORWARD_NAVIGATION);
                editTextNewName.setSelection(editTextNewName.getText().toString().length());
                renameDialog.show();
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE, DialogInterface
                        .BUTTON_NEGATIVE}, getAccentColor(), renameDialog);
                renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE}, ContextCompat.getColor(LFMainActivity.this, R.color.grey), renameDialog);
                editTextNewName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //empty method body
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //empty method body

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        if (TextUtils.isEmpty(editable)) {
                            // Disable ok button
                            renameDialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE}, ContextCompat.getColor(LFMainActivity.this, R.color.grey), renameDialog);

                        } else {
                            // Something into edit text. Enable the button.
                            renameDialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                            AlertDialogsHelper.setButtonTextColor(new int[]{DialogInterface.BUTTON_POSITIVE}, getAccentColor(),
                                    renameDialog);
                        }

                    }
                });
                renameDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View dialog) {
                        boolean rename = false;
                        if (editTextNewName.length() != 0) {
                            swipeRefreshLayout.setRefreshing(true);
                            boolean success = false;
                            if (albumsMode) {
                                if (!editTextNewName.getText().toString().equals(albumName)) {
                                    int index = getAlbums().dispAlbums.indexOf(getAlbums().getSelectedAlbum(0));
                                    getAlbums().getAlbum(index).updatePhotos(getApplicationContext());
                                    success = getAlbums().getAlbum(index).renameAlbum(getApplicationContext(),
                                            editTextNewName.getText().toString());
                                    albumsAdapter.notifyItemChanged(index);
                                } else {
                                    SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.rename_no_change), navigationView.getHeight());
                                    rename = true;
                                }
                            } else {
                                if (!editTextNewName.getText().toString().equals(albumName)) {
                                    success = getAlbum().renameAlbum(getApplicationContext(), editTextNewName.getText().toString());
                                    toolbar.setTitle(getAlbum().getName());
                                    mediaAdapter.notifyDataSetChanged();
                                } else {
                                    SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.rename_no_change), navigationView.getHeight());
                                    rename = true;
                                }
                            }
                            renameDialog.dismiss();
                            if (success) {
                                SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.rename_succes), navigationView.getHeight());
                                getAlbums().clearSelectedAlbums();
                                invalidateOptionsMenu();
                            } else if (!rename) {
                                SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.rename_error), navigationView.getHeight());
                                requestSdCardPermissions();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            SnackBarHandler.showWithBottomMargin(mDrawerLayout, getString(R.string.insert_something), navigationView.getHeight());
                            editTextNewName.requestFocus();
                        }
                    }
                });
                return true;

            case R.id.clear_album_preview:
                if (!albumsMode) {
                    getAlbum().removeCoverAlbum(getApplicationContext());
                }
                return true;

            case R.id.setAsAlbumPreview:
                if (!albumsMode) {
                    getAlbum().setSelectedPhotoAsPreview(getApplicationContext());
                    finishEditMode();
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private ArrayList<Media> storeTemporaryphotos(String path){
        ArrayList<Media> temp = new ArrayList<>();
        if(!all_photos && !fav_photos && editMode){
            for(Media m: getAlbum().getSelectedMedia()){
                String name = m.getPath().substring(m.getPath().lastIndexOf("/") + 1);
                temp.add(new Media(path + "/" + name));
            }
        }
        return temp;
    }

    private void checkDescription(String newpath, ArrayList<Media> selecteditems){
        for(int i = 0; i < selecteditems.size(); i++){
            getDescriptionPaths(selecteditems.get(i).getPath(), newpath);
        }
    }

    private void performRealmAction(final ImageDescModel descModel, String newpath){
        realm = Realm.getDefaultInstance();
        int index = descModel.getId().lastIndexOf("/");
        String name = descModel.getId().substring(index + 1);
        String newpathy = newpath + "/" + name;
        realm.beginTransaction();
        ImageDescModel imageDescModel = realm.createObject(ImageDescModel.class, newpathy);
        imageDescModel.setTitle(descModel.getTitle());
        realm.commitTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmResults<ImageDescModel> result = realm.where(ImageDescModel.class).equalTo
                        ("path", descModel.getId()).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

    private void getDescriptionPaths(String patjs, String newpth){
        realm = Realm.getDefaultInstance();
        RealmQuery<ImageDescModel> realmQuery = realm.where(ImageDescModel.class);
        for(int i = 0; i < realmQuery.count(); i++) {
            if (realmQuery.findAll().get(i).getId().equals(patjs)) {
                performRealmAction(realmQuery.findAll().get(i), newpth);
                break;
            }
        }
    }

    private void checkForFavourites(String path, ArrayList<Media> selectedphotos){
        for(Media m: selectedphotos){
            checkIfFav(m.getPath(), path);
        }
    }

    private void checkIfFav(String currentpath, String newpath){
        realm = Realm.getDefaultInstance();
        RealmQuery<FavouriteImagesModel> favouriteImagesModelRealmQuery = realm.where(FavouriteImagesModel.class);
        for(int i = 0; i < favouriteImagesModelRealmQuery.count(); i++){
            if(favouriteImagesModelRealmQuery.findAll().get(i).getPath().equals(currentpath)){
                performAddToFavOp(favouriteImagesModelRealmQuery.findAll().get(i), newpath);
                break;
            }
        }
    }

    private void performAddToFavOp(final FavouriteImagesModel favouriteImagesModel, String newpath) {
        realm = Realm.getDefaultInstance();
        int index = favouriteImagesModel.getPath().lastIndexOf("/");
        String name = favouriteImagesModel.getPath().substring(index + 1);
        String newpathy = newpath + "/" + name;
        realm.beginTransaction();
        FavouriteImagesModel favouriteImagesModel1 = realm.createObject(FavouriteImagesModel.class, newpathy);
        ImageDescModel q =
                realm.where(ImageDescModel.class).equalTo("path", favouriteImagesModel.getPath()).findFirst();
        if (q != null) {
            favouriteImagesModel1.setDescription(q.getTitle());
        } else {
            favouriteImagesModel1.setDescription(" ");
        }
        realm.commitTransaction();
        realm.executeTransaction(new Realm.Transaction() {
            @Override public void execute(Realm realm) {
                RealmResults<FavouriteImagesModel> result = realm.where(FavouriteImagesModel.class).equalTo
                        ("path", favouriteImagesModel.getPath()).findAll();
                result.deleteAllFromRealm();
            }
        });
    }

    private boolean addToTrash(){
        int no = 0;
        boolean succ = false;
        final ArrayList<Media> media1 = storeDeletedFilesTemporarily();
        File file = new File(Environment.getExternalStorageDirectory() + "/" + ".nomedia");
        if(file.exists() && file.isDirectory()){
            if(!all_photos && !fav_photos && editMode){
                no = getAlbum().moveSelectedMedia(getApplicationContext(), file.getAbsolutePath());
            }else if(all_photos && !fav_photos && editMode){
                no = getAlbum().moveAllMedia(getApplicationContext(), file.getAbsolutePath(), selectedMedias);
            }else if(!editMode && !all_photos && !fav_photos){
                no = getAlbum().moveAllMedia(getApplicationContext(), file.getAbsolutePath(), getAlbum().getMedia());
            }
            if(no > 0){
                succ = true;
                if(no == 1){
                    Snackbar snackbar = SnackBarHandler.showWithBottomMargin2(mDrawerLayout, String.valueOf(no) + " " + getString(R.string
                                    .trashbin_move_onefile),
                            navigationView.getHeight
                                    (), Snackbar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getAlbum().moveAllMedia(getApplicationContext(), getAlbum().getPath(), media1);
                            refreshListener.onRefresh();
                        }
                    });
                }else{
                    Snackbar snackbar = SnackBarHandler.showWithBottomMargin2(mDrawerLayout, String.valueOf(no) + " " + getString(R.string
                                    .trashbin_move_onefile),
                            navigationView.getHeight
                                    (), Snackbar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getAlbum().moveAllMedia(getApplicationContext(), getAlbum().getPath(), media1);
                            refreshListener.onRefresh();
                        }
                    });
                    snackbar.show();
                }
            }else{
                SnackBarHandler.showWithBottomMargin(mDrawerLayout, String.valueOf(no) + " " + getString(R.string
                                .trashbin_move_error),
                        navigationView.getHeight
                                ());
            }
        }else{
            if(file.mkdir()){
                if(!all_photos && !fav_photos && editMode){
                    no = getAlbum().moveSelectedMedia(getApplicationContext(), file.getAbsolutePath());
                }else if(all_photos && !fav_photos && editMode){
                    no = getAlbum().moveAllMedia(getApplicationContext(), file.getAbsolutePath(), selectedMedias);
                }else if(!editMode && !all_photos && !fav_photos){
                    no = getAlbum().moveAllMedia(getApplicationContext(), file.getAbsolutePath(), getAlbum().getMedia());
                }
               // no = getAlbum().moveSelectedMedia(getApplicationContext(), file.getAbsolutePath());
                if(no > 0){
                    succ = true;
                    if(no == 1){
                        SnackBarHandler.showWithBottomMargin(mDrawerLayout, String.valueOf(no) + " " + getString(R.string
                                        .trashbin_move_onefile),
                                navigationView.getHeight
                                        ());
                    }else{
                        SnackBarHandler.showWithBottomMargin(mDrawerLayout, String.valueOf(no) + " " + getString(R.string
                                        .trashbin_move),
                                navigationView.getHeight
                                        ());
                    }
                }else{
                    SnackBarHandler.showWithBottomMargin(mDrawerLayout, String.valueOf(no) + " " + getString(R.string
                                    .trashbin_move_error),
                            navigationView.getHeight
                                    ());
                }
            }
        }
       // clearSelectedPhotos();
        return succ;
    }

    private ArrayList<Media> storeDeletedFilesTemporarily(){
        ArrayList<Media> deletedImages = new ArrayList<>();
        if(!all_photos && !fav_photos && editMode){
            for(Media m: getAlbum().getSelectedMedia()){
                String name = m.getPath().substring(m.getPath().lastIndexOf("/") + 1);
                deletedImages.add(new Media(Environment.getExternalStorageDirectory() + "/" + ".nomedia" + "/" + name));
            }
        } else if(all_photos && !fav_photos && editMode){
            for(Media m: selectedMedias){
                String name = m.getPath().substring(m.getPath().lastIndexOf("/") + 1);
                deletedImages.add(new Media(Environment.getExternalStorageDirectory() + "/" + ".nomedia" + "/" + name));
            }
        }
        return deletedImages;
    }

    private void addTrashObjectsToRealm(ArrayList<Media> media){
        String trashbinpath = Environment.getExternalStorageDirectory() + "/" + ".nomedia";
        realm = Realm.getDefaultInstance();
        for(int i = 0; i < media.size(); i++){
            int index = media.get(i).getPath().lastIndexOf("/");
            String name = media.get(i).getPath().substring(index + 1);
            realm.beginTransaction();
            String trashpath = trashbinpath + "/" + name;
            TrashBinRealmModel trashBinRealmModel = realm.createObject(TrashBinRealmModel.class, trashpath);
            trashBinRealmModel.setOldpath(media.get(i).getPath());
            trashBinRealmModel.setDatetime(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
            trashBinRealmModel.setTimeperiod("null");
            realm.commitTransaction();
        }
    }

    private static class SortModeSet extends AsyncTask<SortingMode, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        public SortModeSet(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected Void doInBackground(SortingMode... sortingModes) {
            for (Album a : getAlbums().dispAlbums) {
                if (a.settings.getSortingMode().getValue() != sortingModes[0].getValue()) {
                    a.setDefaultSortingMode(reference.get(), sortingModes[0]);
                }
            }
            return null;
        }
    }

    public Bitmap getBitmap(String path) {

        Uri uri = Uri.fromFile(new File(path));
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap bitmap = null;
            in = getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                bitmap = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) x,
                        (int) y, true);
                bitmap.recycle();
                bitmap = scaledBitmap;

                System.gc();
            } else {
                bitmap = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " + bitmap.getWidth() + ", height: " +
                    bitmap.getHeight());
            return bitmap;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public void getNavigationBar() {
        if (editMode && hidenav) {
            showNavigationBar();
            hidenav = false;
        }
    }

    //to copy from all photos.
    private boolean copyfromallphotos(Context context, String folderPath) {
        boolean success = false;
        for (Media m : selectedMedias) {
            try {
                File from = new File(m.getPath());
                File to = new File(folderPath);
                if (success = ContentHelper.copyFile(context, from, to))
                    scanFile(context, new String[]{StringUtils.getPhotoPathMoved(m.getPath(), folderPath)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context, path, null, null);
    }

    /**
     * If we are in albumsMode, make the albums recyclerView visible. If we are not, make media recyclerView visible.
     *
     * @param albumsMode it indicates whether we are in album selection mode or not
     */
    private void toggleRecyclersVisibility(boolean albumsMode) {
        rvAlbums.setVisibility(albumsMode ? View.VISIBLE : View.GONE);
        rvMedia.setVisibility(albumsMode ? View.GONE : View.VISIBLE);
        nothingToShow.setVisibility(View.GONE);
        starImageView.setVisibility(View.GONE);
        if (albumsMode)
            fabScrollUp.hide();
        //touchScrollBar.setScrollBarHidden(albumsMode);

    }

    private void tint() {
        if (localFolder) {
            defaultIcon.setColor(getPrimaryColor());
            defaultText.setTextColor(getPrimaryColor());
            hiddenIcon.setColor(getIconColor());
            hiddenText.setTextColor(getTextColor());
        } else {
            hiddenIcon.setColor(getPrimaryColor());
            hiddenText.setTextColor(getPrimaryColor());
            defaultIcon.setColor(getIconColor());
            defaultText.setTextColor(getTextColor());
        }
    }

    /**
     * handles back presses.
     * If we are currently in selection mode, back press will take us out of selection mode.
     * If we are not in selection mode but in albumsMode and the drawer is open, back press will close it.
     * If we are not in selection mode but in albumsMode and the drawer is closed, finish the activity.
     * If we are neither in selection mode nor in albumsMode, display the albums again.
     */
    @Override
    public void onBackPressed() {
        checkForReveal = true;
        if ((editMode && all_photos) || (editMode && fav_photos))
            clearSelectedPhotos();
        getNavigationBar();
        if (editMode) finishEditMode();
        else {
            if (albumsMode) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                else {
                    if (doubleBackToExitPressedOnce && isTaskRoot())
                        finish();
                    else if (isTaskRoot()) {
                        doubleBackToExitPressedOnce = true;
                        View rootView = LFMainActivity.this.getWindow().getDecorView().findViewById(android.R.id.content);
                        Snackbar snackbar = Snackbar
                                .make(rootView, R.string.press_back_again_to_exit, Snackbar.LENGTH_LONG)
                                .setAction(R.string.exit, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        finishAffinity();
                                    }
                                })
                                .setActionTextColor(getAccentColor());
                        View sbView = snackbar.getView();
                        final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
                        params.setMargins(params.leftMargin,
                                params.topMargin,
                                params.rightMargin,
                                params.bottomMargin + navigationView.getHeight());
                        sbView.setLayoutParams(params);
                        snackbar.show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);
                    } else
                        super.onBackPressed();
                }
            } else {
                displayAlbums();
            }
        }
    }

    private class CreateGIFTask extends AsyncTask<Void, Void, Void>{
        private ArrayList<Bitmap> bitmaps = new ArrayList<>();

        @Override protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override protected Void doInBackground(Void... voids) {

            if(!albumsMode && !all_photos && !fav_photos){
                for(Media m: getAlbum().getSelectedMedia()){
                    bitmaps.add(getBitmap(m.getPath()));
                }
            }else if(!albumsMode && all_photos && !fav_photos){
                for(Media m: selectedMedias){
                    bitmaps.add(getBitmap(m.getPath()));
                }
            }
            byte[] bytes = createGIFFromImages(bitmaps);
            File file = new File(Environment.getExternalStorageDirectory() + "/" + "Phimpme_gifs");
            if(file.exists() && file.isDirectory()){
                FileOutputStream outStream = null;
                try{
                    outStream = new FileOutputStream(file.getPath() + "/" + "GIF.gif");
                    outStream.write(bytes);
                    outStream.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else {
                if (file.mkdir()) {
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(file.getPath() + "/" + "GIF.gif");
                        outStream.write(bytes);
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(!albumsMode && !all_photos && !fav_photos){
                getAlbum().clearSelectedPhotos();
            }else if(!albumsMode && all_photos && !fav_photos){
                clearSelectedPhotos();
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private byte[] createGIFFromImages(ArrayList<Bitmap> bitmaps){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {
            encoder.addFrame(bitmap);
        }
        encoder.finish();
        return bos.toByteArray();
    }

    private class ZipAlbumTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NotificationHandler.make(R.string.folder, R.string.zip_fol, R.drawable.ic_archive_black_24dp);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                double c = 0.0;
                BufferedInputStream origin = null;
                FileOutputStream dest = new FileOutputStream(getAlbums().getSelectedAlbum(0).getParentsFolders().get
                        (1) + "/" + getAlbums().getSelectedAlbum(0).getName() +
                        ".zip");
                ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                        dest));
                byte data[] = new byte[BUFFER];

                for (int i = 0; i < path.size(); i++) {
                    FileInputStream fi = new FileInputStream(path.get(i));
                    origin = new BufferedInputStream(fi, BUFFER);

                    ZipEntry entry = new ZipEntry(path.get(i).substring(path.get(i).lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    c++;
                    if ((int) ((c / size) * 100) > 100) {
                        NotificationHandler.actionProgress((int) c, path.size(), 100, R.string.zip_operation);
                    } else {
                        NotificationHandler.actionProgress((int) c, path.size(), (int) ((c / path.size()) * 100), R.string
                                .zip_operation);
                    }
                    int count;

                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    origin.close();
                }
                out.close();
                if (isCancelled()) {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            NotificationHandler.actionPassed(R.string.zip_completion);
            String path = getAlbums().getSelectedAlbum(0).getParentsFolders().get(1) + getAlbums().getSelectedAlbum
                    (0).getName() + ".zip";
            SnackBarHandler.show(mDrawerLayout, getResources().getString(R.string.zip_location) +
                    path);
            getAlbums().clearSelectedAlbums();
            albumsAdapter.notifyDataSetChanged();
            invalidateOptionsMenu();
        }
    }


    private static class PrepareAlbumTask extends AsyncTask<Void, Integer, Void> {

        private WeakReference<LFMainActivity> reference;

        PrepareAlbumTask(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
            asyncActivityRef.toggleRecyclersVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            LFMainActivity asynActivityRef = reference.get();
            getAlbums().loadAlbums(asynActivityRef.getApplicationContext(), asynActivityRef.hidden);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
            asyncActivityRef.albList = new ArrayList<>();
            asyncActivityRef.populateAlbum();
            asyncActivityRef.checkNothing();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            getAlbums().saveBackup(asyncActivityRef);
            asyncActivityRef.invalidateOptionsMenu();
            asyncActivityRef.finishEditMode();
        }
    }

    private static class PreparePhotosTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        PreparePhotosTask(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            // Declaring globally in Async might lead to leakage of the context
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
            asyncActivityRef.toggleRecyclersVisibility(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            reference.get().getAlbum().updatePhotos(reference.get());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.mediaAdapter.swapDataSet(asyncActivityRef.getAlbum().getMedia(), false);
            if (!asyncActivityRef.hidden)
                HandlingAlbums.addAlbumToBackup(asyncActivityRef, reference.get().getAlbum());
            asyncActivityRef.checkNothing();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            asyncActivityRef.invalidateOptionsMenu();
            asyncActivityRef.finishEditMode();
        }
    }

    private static class PrepareAllPhotos extends AsyncTask<Void, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        PrepareAllPhotos(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
            asyncActivityRef.toggleRecyclersVisibility(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.getAlbum().updatePhotos(asyncActivityRef);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            LFMainActivity asyncActivityRef = reference.get();
            listAll = StorageProvider.getAllShownImages(asyncActivityRef);
            asyncActivityRef.size = listAll.size();
            Collections.sort(listAll, MediaComparators.getComparator(asyncActivityRef.getAlbum().settings.getSortingMode(),
                asyncActivityRef.getAlbum().settings.getSortingOrder()));
            asyncActivityRef.mediaAdapter.swapDataSet(listAll, false);
            if (!asyncActivityRef.hidden)
                HandlingAlbums.addAlbumToBackup(asyncActivityRef, asyncActivityRef.getAlbum());
            asyncActivityRef.checkNothing();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            asyncActivityRef.invalidateOptionsMenu();
            asyncActivityRef.finishEditMode();
            asyncActivityRef.toolbar.setTitle(asyncActivityRef.getString(R.string.all_media));
            asyncActivityRef.clearSelectedPhotos();
        }
    }

    private static class FavouritePhotos extends AsyncTask<Void, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        FavouritePhotos(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
            asyncActivityRef.toggleRecyclersVisibility(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.getAlbum().updatePhotos(asyncActivityRef);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            LFMainActivity asyncActivityRef = reference.get();
            Collections.sort(asyncActivityRef.favouriteslist, MediaComparators.getComparator(asyncActivityRef.getAlbum().settings.getSortingMode(),
                asyncActivityRef.getAlbum().settings.getSortingOrder()));
            asyncActivityRef.mediaAdapter.swapDataSet(asyncActivityRef.favouriteslist, true);
            asyncActivityRef.checkNothingFavourites();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            asyncActivityRef.invalidateOptionsMenu();
            asyncActivityRef.finishEditMode();
            asyncActivityRef.toolbar.setTitle(asyncActivityRef.getResources().getString(R.string.favourite_title));
            asyncActivityRef.clearSelectedPhotos();
        }
    }

    /* AsyncTask for Add to favourites operation */
    private class AddToFavourites extends AsyncTask<Void, Integer, Integer>{

        @Override
        protected void onPreExecute() {
            getNavigationBar();
            swipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override protected Integer doInBackground(Void... voids) {
            int count = 0;
            realm = Realm.getDefaultInstance();
            ArrayList<Media> favadd;
            if (!all_photos) {
                favadd = getAlbum().getSelectedMedia();
            } else {
                favadd = selectedMedias;
            }

            for (int i = 0; i < favadd.size(); i++) {
                String realpath = favadd.get(i).getPath();
                RealmQuery<FavouriteImagesModel> query = realm.where(FavouriteImagesModel.class).equalTo("path",
                        realpath);
                if (query.count() == 0) {
                    count++;
                    realm.beginTransaction();
                    FavouriteImagesModel fav = realm.createObject(FavouriteImagesModel.class,
                            realpath);
                    ImageDescModel q = realm.where(ImageDescModel.class).equalTo("path", realpath).findFirst();
                    if (q != null) {
                        fav.setDescription(q.getTitle());
                    } else {
                        fav.setDescription(" ");
                    }

                    realm.commitTransaction();
                }
            }
            return count;
        }

        @Override protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            swipeRefreshLayout.setRefreshing(false);
            finishEditMode();
            if (count == 0) {
                SnackBarHandler.show(mDrawerLayout, getResources().getString(R.string.check_favourite_multipleitems));
            } else if (count == 1) {
                final Snackbar snackbar = SnackBarHandler.show(mDrawerLayout,
                        getResources().getString(R.string.add_favourite) );
                snackbar.setAction(R.string.openfav, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        displayfavourites();
                        favourites = false;
                    }
                });
                snackbar.show();
            } else {
                SnackBarHandler.show(mDrawerLayout, count + " " + getResources().getString(R.string
                        .add_favourite_multiple));
                final Snackbar snackbar = SnackBarHandler.show(mDrawerLayout,
                        getResources().getString(R.string.add_favourite) );
                snackbar.setAction(R.string.openfav, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        displayfavourites();
                        favourites = false;
                    }
                });
                snackbar.show();
            }
            mediaAdapter.notifyDataSetChanged();
        }
    }

    /*
    Async Class for Sorting Photos - NOT listAll
     */
    private static class SortingUtilsPhtots extends AsyncTask<Void, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        SortingUtilsPhtots(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPreExecute();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.getAlbum().sortPhotos();
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPostExecute(aVoid);
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            asyncActivityRef.mediaAdapter.swapDataSet(asyncActivityRef.getAlbum().getMedia(), false);
        }
    }

    /*
    Async Class for Sorting Photos - listAll
     */
    private static class SortingUtilsListAll extends AsyncTask<Void, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        SortingUtilsListAll(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPreExecute();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            LFMainActivity asyncActivityRef = reference.get();
            Collections.sort(listAll, MediaComparators.getComparator(asyncActivityRef.getAlbum().settings.getSortingMode(),
                asyncActivityRef.getAlbum().settings.getSortingOrder()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPostExecute(aVoid);
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            asyncActivityRef.mediaAdapter.swapDataSet(listAll, false);
        }
    }

    /*
    Async Class for Sorting Favourites
     */

    private static class SortingUtilsFavouritelist extends AsyncTask<Void, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        SortingUtilsFavouritelist(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPreExecute();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            LFMainActivity asyncActivityRef = reference.get();
            Collections.sort(asyncActivityRef.favouriteslist, MediaComparators.getComparator(asyncActivityRef.getAlbum().settings.getSortingMode(),
                asyncActivityRef.getAlbum().settings.getSortingOrder()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPostExecute(aVoid);
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            asyncActivityRef.mediaAdapter.swapDataSet(asyncActivityRef.favouriteslist, true);
        }
    }

    /*
    Async Class for Sorting Albums
     */
    private static class SortingUtilsAlbums extends AsyncTask<Void, Void, Void> {

        private WeakReference<LFMainActivity> reference;

        SortingUtilsAlbums(LFMainActivity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPreExecute();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            getAlbums().sortAlbums();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            LFMainActivity asyncActivityRef = reference.get();
            super.onPostExecute(aVoid);
            asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
            asyncActivityRef.albumsAdapter.swapDataSet(getAlbums().dispAlbums);
            new PrepareAlbumTask(asyncActivityRef.activityContext).execute();
        }
    }

    /*
    Async Class for coping images
     */
    private class CopyPhotos extends AsyncTask<String, Integer, Boolean> {

        private WeakReference<LFMainActivity> reference;
        private String path;
        private Snackbar snackbar;
        private ArrayList<Media> temp;
        private Boolean moveAction, copyAction, success;

        CopyPhotos(String path, Boolean moveAction, Boolean copyAction, LFMainActivity reference) {
            this.path = path;
            this.moveAction = moveAction;
            this.copyAction = copyAction;
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            LFMainActivity asyncActivityRef = reference.get();
            asyncActivityRef.swipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            temp = storeTemporaryphotos(path);
            LFMainActivity asyncActivityRef = reference.get();
            if (!asyncActivityRef.all_photos) {
                success = asyncActivityRef.getAlbum().copySelectedPhotos(asyncActivityRef, path);
                MediaStoreProvider.getAlbums(asyncActivityRef);
                asyncActivityRef.getAlbum().updatePhotos(asyncActivityRef);
            } else {
                success = asyncActivityRef.copyfromallphotos(asyncActivityRef.getApplicationContext(), path);
            }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            LFMainActivity asyncActivityRef = reference.get();
            if(result)
            {
                if(!asyncActivityRef.all_photos){
                    asyncActivityRef.mediaAdapter.swapDataSet(asyncActivityRef.getAlbum().getMedia(), false);
                }else {
                    asyncActivityRef.mediaAdapter.swapDataSet(listAll, false);
                }
                asyncActivityRef.mediaAdapter.notifyDataSetChanged();
                asyncActivityRef.invalidateOptionsMenu();
                asyncActivityRef.swipeRefreshLayout.setRefreshing(false);
                asyncActivityRef.finishEditMode();
                if (moveAction)
                    SnackBarHandler.showWithBottomMargin(asyncActivityRef.mDrawerLayout,
                        asyncActivityRef.getString(R.string.photos_moved_successfully),
                        asyncActivityRef.navigationView.getHeight());
                else if (copyAction){
                    snackbar = SnackBarHandler.showWithBottomMargin2(asyncActivityRef.mDrawerLayout,
                            asyncActivityRef.getString(R.string.copied_successfully),
                            asyncActivityRef.navigationView.getHeight(), Snackbar.LENGTH_SHORT);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (Media media : temp) {
                                String[] projection = {MediaStore.Images.Media._ID};

                                // Match on the file path
                                String selection = MediaStore.Images.Media.DATA + " = ?";
                                String[] selectionArgs = new String[]{media.getPath()};

                                // Query for the ID of the media matching the file path
                                Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                ContentResolver contentResolver = getContentResolver();
                                Cursor c =
                                        contentResolver
                                                .query(queryUri, projection, selection, selectionArgs,
                                                        null);
                                if (c.moveToFirst()) {
                                    // We found the ID. Deleting the item via the content provider will also remove the file
                                    long id =
                                            c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                    Uri deleteUri = ContentUris
                                            .withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                                    id);
                                    contentResolver.delete(deleteUri, null, null);
                                }
                                c.close();
                            }
                        }
                    });
                }

            } else
                asyncActivityRef.requestSdCardPermissions();
        }
    }
}
