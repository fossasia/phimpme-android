package org.fossasia.phimpme.gallery.activities;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.view.IconicsImageView;

import org.fossasia.phimpme.R;
import org.fossasia.phimpme.base.SharedMediaActivity;
import org.fossasia.phimpme.gallery.SelectAlbumBottomSheet;
import org.fossasia.phimpme.gallery.adapters.AlbumsAdapter;
import org.fossasia.phimpme.gallery.adapters.MediaAdapter;
import org.fossasia.phimpme.gallery.data.Album;
import org.fossasia.phimpme.gallery.data.CustomAlbumsHelper;
import org.fossasia.phimpme.gallery.data.HandlingAlbums;
import org.fossasia.phimpme.gallery.data.Media;
import org.fossasia.phimpme.gallery.data.base.MediaComparators;
import org.fossasia.phimpme.gallery.data.base.SortingOrder;
import org.fossasia.phimpme.gallery.data.providers.StorageProvider;
import org.fossasia.phimpme.gallery.util.Affix;
import org.fossasia.phimpme.gallery.util.AlertDialogsHelper;
import org.fossasia.phimpme.gallery.util.ContentHelper;
import org.fossasia.phimpme.gallery.util.Measure;
import org.fossasia.phimpme.gallery.util.PreferenceUtil;
import org.fossasia.phimpme.gallery.util.SecurityHelper;
import org.fossasia.phimpme.gallery.util.StringUtils;
import org.fossasia.phimpme.gallery.views.GridSpacingItemDecoration;
import org.fossasia.phimpme.uploadhistory.UploadHistory;
import org.fossasia.phimpme.utilities.ActivitySwitchHelper;
import org.fossasia.phimpme.utilities.SnackBarHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import static org.fossasia.phimpme.gallery.data.base.SortingMode.DATE;
import static org.fossasia.phimpme.gallery.data.base.SortingMode.NAME;
import static org.fossasia.phimpme.gallery.data.base.SortingMode.NUMERIC;
import static org.fossasia.phimpme.gallery.data.base.SortingMode.SIZE;


public class LFMainActivity extends SharedMediaActivity {

    private static String TAG = "AlbumsAct";
    private int REQUEST_CODE_SD_CARD_PERMISSIONS = 42;
    private boolean about=false,settings=false,uploadHistory=false;
    private CustomAlbumsHelper customAlbumsHelper = CustomAlbumsHelper.getInstance(LFMainActivity.this);
    private PreferenceUtil SP;
    private SecurityHelper securityObj;

    private RecyclerView rvAlbums;
    private AlbumsAdapter albumsAdapter;
    private GridSpacingItemDecoration rvAlbumsDecoration;

    private RecyclerView rvMedia;
    private MediaAdapter mediaAdapter;
    private GridSpacingItemDecoration rvMediaDecoration;

    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private SelectAlbumBottomSheet bottomSheetDialogFragment;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean hidden = false, pickMode = false, editMode = false, albumsMode = true, firstLaunch = true,localFolder=true,hidenav=false;

    //To handle all photos/Album conditions
    public boolean all_photos = false;
    private boolean checkForReveal = true;
    final String REVIEW_ACTION = "com.android.camera.action.REVIEW";
    public static ArrayList<Media> listAll;
    public int size;
    public int pos;
    private ArrayList<Media> media;
    private ArrayList<Media> selectedMedias = new ArrayList<>();
    public boolean visible;

    CoordinatorLayout coordinatorLayoutMainContent;

    // To handle back pressed
    boolean doubleBackToExitPressedOnce = false;

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
            if(checkForReveal) {
                enterReveal();
                checkForReveal = false;
            }
            Media m = (Media) v.findViewById(R.id.photo_path).getTag();
            //If first long press, turn on selection mode
            hideNavigationBar();
            hidenav=true;
            if (!all_photos) {
                appBarOverlay();
                if (!editMode) {
                    mediaAdapter.notifyItemChanged(getAlbum().toggleSelectPhoto(m));
                    editMode = true;
                } else getAlbum().selectAllPhotosUpTo(getAlbum().getIndex(m), mediaAdapter);

                invalidateOptionsMenu();
            } else if (!editMode) {
                mediaAdapter.notifyItemChanged(toggleSelectPhoto(m));
                editMode = true;
            } else selectAllPhotosUpTo(getImagePosition(m.getPath()), mediaAdapter);
            return true;
        }
    };

    /**
     * Helper method for making reveal animation for toolbar when any item is selected by long click.
     */
    private void enterReveal() {

        final View toolbar = findViewById(R.id.appbar_toolbar);

        // get the center for the clipping circle
        int cx = toolbar.getMeasuredWidth() / 2;
        int cy = toolbar.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(toolbar.getWidth(), toolbar.getHeight()) / 2;

        // create the animator for this view
        Animator anim =
                ViewAnimationUtils.createCircularReveal(toolbar, cx, cy, 5, finalRadius);

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
            toolbar.setTitle(selectedMedias.size() + "/" + size);
        }
        invalidateOptionsMenu();
        return getImagePosition(m.getPath());
    }

    public void clearSelectedPhotos() {
        for (Media m : selectedMedias)
            m.setSelected(false);
        if (selectedMedias != null)
            selectedMedias.clear();
        if(localFolder) toolbar.setTitle(getString(R.string.local_folder));
        else toolbar.setTitle(getString(R.string.hidden_folder));
    }


    public void selectAllPhotos() {
        for (Media m : listAll) {
            m.setSelected(true);
            selectedMedias.add(m);
        }
        toolbar.setTitle(selectedMedias.size() + "/" + size);
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
            if (!all_photos) {
                if (!pickMode) {
                    //if in selection mode, toggle the selected/unselect state of photo
                    if (editMode) {
                        appBarOverlay();
                        mediaAdapter.notifyItemChanged(getAlbum().toggleSelectPhoto(m));
                        if(selectedMedias.size()==0)
                            getNavigationBar();

                        invalidateOptionsMenu();
                    } else {
                        getAlbum().setCurrentPhotoIndex(m);
                        Intent intent = new Intent(LFMainActivity.this, SingleMediaActivity.class);
                        intent.putExtra("path", Uri.fromFile(new File(m.getPath())).toString());
                        intent.setAction(SingleMediaActivity.ACTION_OPEN_ALBUM);
                        startActivity(intent);
                    }
                } else {
                    setResult(RESULT_OK, new Intent().setData(m.getUri()));
                    finish();
                }
            } else {
                if (!editMode) {
                    Intent intent = new Intent(REVIEW_ACTION, Uri.fromFile(new File(m.getPath())));
                    intent.putExtra(getString(R.string.all_photo_mode), true);
                    intent.putExtra(getString(R.string.position), pos);
                    intent.putExtra(getString(R.string.allMediaSize), size);
                    intent.setClass(getApplicationContext(), SingleMediaActivity.class);
                    startActivity(intent);
                } else {
                    mediaAdapter.notifyItemChanged(toggleSelectPhoto(m));
                }

            }
        }
    };

    private View.OnLongClickListener albumOnLongCLickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if(checkForReveal) {
                enterReveal();
                checkForReveal = false;
            }
            albumsAdapter.notifyItemChanged(getAlbums().toggleSelectAlbum(((Album) v.findViewById(R.id.album_name).getTag())));
            editMode = true;
            invalidateOptionsMenu();
            hideNavigationBar();
            hidenav=true;
            return true;
        }
    };

    private View.OnClickListener albumOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Album album = (Album) v.findViewById(R.id.album_name).getTag();
            //int index = Integer.parseInt(v.findViewById(R.id.album_name).getTag().toString());
            if (editMode) {
                albumsAdapter.notifyItemChanged(getAlbums().toggleSelectAlbum(album));
                if(getAlbums().getSelectedCount()==0)
                    getNavigationBar();
                invalidateOptionsMenu();
            } else {
                getAlbums().setCurrentAlbum(album);
                displayCurrentAlbumMedia(true);
                setRecentApp(getAlbums().getCurrentAlbum().getName());
            }
        }
    };

    /**
     *  Method for clearing the scroll flags.
     */
    private void appBarOverlay(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);  // clear all scroll flags
    }

    /**
     * Method for adding the scroll flags.
     */
    private void clearOverlay(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
    }

    public int getImagePosition(String path) {
        int pos = 0;
        for (int i = 0; i < listAll.size(); i++) {
            if (listAll.get(i).getPath().equals(path)) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "lfmain");

        coordinatorLayoutMainContent = (CoordinatorLayout) findViewById(R.id.cl_main_content);
        BottomNavigationView navigationView = (BottomNavigationView)findViewById(R.id.bottombar);

        SP = PreferenceUtil.getInstance(getApplicationContext());
        albumsMode = true;
        editMode = false;
        securityObj = new SecurityHelper(LFMainActivity.this);
        if (getIntent().getExtras() != null)
            pickMode = getIntent().getExtras().getBoolean(SplashScreen.PICK_MODE);
        SP.putBoolean(getString(R.string.preference_use_alternative_provider), false);
        initUI();
        new initAllPhotos().execute();
        displayData(getIntent().getExtras());
        checkNothing();

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();
                if(itemID==R.id.navigation_home){
                    if(!localFolder){
                        hidden = false;
                        localFolder = true;
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
        getAlbums().clearSelectedAlbums();
        getAlbum().clearSelectedPhotos();
        if (all_photos)
            mediaAdapter.swapDataSet(listAll);
        if (!all_photos) {
            if (SP.getBoolean("auto_update_media", false)) {
                if (albumsMode) {
                    if (!firstLaunch) new PrepareAlbumTask().execute();
                } else new PreparePhotosTask().execute();
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
        mediaAdapter.swapDataSet(getAlbum().getMedia());
        if (reload) new PreparePhotosTask().execute();
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
        mediaAdapter.swapDataSet(listAll);
        if (reload) new PrepareAllPhotos().execute();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAlbums();
            }
        });
        albumsMode = editMode = false;
        invalidateOptionsMenu();
    }

    private void displayAlbums() {
        all_photos = false;
        displayAlbums(true);
    }

    private void displayAlbums(boolean reload) {
        if(localFolder) {
            toolbar.setTitle(getString(R.string.local_folder));
        }
        else{
            toolbar.setTitle(getString(R.string.hidden_folder));
        }
        toolbar.setNavigationIcon(getToolbarIcon(GoogleMaterial.Icon.gmd_menu));
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        albumsAdapter.swapDataSet(getAlbums().dispAlbums);
        if (reload) new PrepareAlbumTask().execute();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        albumsMode = true;
        editMode = false;
        invalidateOptionsMenu();
        mediaAdapter.swapDataSet(new ArrayList<Media>());
        rvMedia.scrollToPosition(0);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        /**** TOOLBAR ****/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**** RECYCLER VIEW ****/
        rvAlbums = (RecyclerView) findViewById(R.id.grid_albums);
        rvMedia = ((RecyclerView) findViewById(R.id.grid_photos));
        rvAlbums.setHasFixedSize(true);
        rvAlbums.setItemAnimator(new DefaultItemAnimator());
        rvMedia.setHasFixedSize(true);
        rvMedia.setItemAnimator(new DefaultItemAnimator());


        albumsAdapter = new AlbumsAdapter(getAlbums().dispAlbums, LFMainActivity.this);

        albumsAdapter.setOnClickListener(albumOnClickListener);
        albumsAdapter.setOnLongClickListener(albumOnLongCLickListener);
        rvAlbums.setAdapter(albumsAdapter);

        mediaAdapter = new MediaAdapter(getAlbum().getMedia(), LFMainActivity.this);

        mediaAdapter.setOnClickListener(photosOnClickListener);
        mediaAdapter.setOnLongClickListener(photosOnLongClickListener);
        rvMedia.setAdapter(mediaAdapter);

        int spanCount = SP.getInt("n_columns_folders", 2);
        rvAlbumsDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(3, getApplicationContext()), true);
        rvAlbums.addItemDecoration(rvAlbumsDecoration);
        rvAlbums.setLayoutManager(new GridLayoutManager(this, spanCount));

        spanCount = SP.getInt("n_columns_media", 3);
        rvMediaDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(3, getApplicationContext()), true);
        rvMedia.setLayoutManager(new GridLayoutManager(getApplicationContext(), spanCount));
        rvMedia.addItemDecoration(rvMediaDecoration);


        /**** SWIPE TO REFRESH ****/
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getAccentColor());
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(getBackgroundColor());
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNavigationBar();
                if (albumsMode) {
                    getAlbums().clearSelectedAlbums();
                    new PrepareAlbumTask().execute();
                } else {
                    if (!all_photos) {
                        getAlbum().clearSelectedPhotos();
                        new PreparePhotosTask().execute();
                    } else {
                        new PrepareAllPhotos().execute();
                    }
                }
            }
        });

        /**** DRAWER ****/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.addDrawerListener(new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                //Put your code here
                // materialMenu.animateIconState(MaterialMenuDrawable.IconState.BURGER);
                Intent intent=null;
                if(settings){
                    intent = new Intent(LFMainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    settings=false;
                } else if(about){
                    intent = new Intent(LFMainActivity.this, AboutActivity.class);
                    startActivity(intent);
                    about=false;
                } else if(uploadHistory){
                    intent = new Intent(LFMainActivity.this, UploadHistory.class);
                    startActivity(intent);
                    uploadHistory=false;
                }

            }

            public void onDrawerOpened(View drawerView) {
                //Put your code here
                //materialMenu.animateIconState(MaterialMenuDrawable.IconState.ARROW);
            }
        });

        setRecentApp(getString(R.string.app_name));
        setupUI();
        if (pickMode) {
            hideNavigationBar();
            swipeRefreshLayout.setPadding(0, 0, 0, 0);
        }
    }

    private void updateColumnsRvs() {
        updateColumnsRvAlbums();
        updateColumnsRvMedia();
    }

    private void updateColumnsRvAlbums() {
        int spanCount = SP.getInt("n_columns_folders", 2);
        if (spanCount != ((GridLayoutManager) rvAlbums.getLayoutManager()).getSpanCount()) {
            rvAlbums.removeItemDecoration(rvAlbumsDecoration);
            rvAlbumsDecoration = new GridSpacingItemDecoration(spanCount, Measure.pxToDp(3, getApplicationContext()), true);
            rvAlbums.addItemDecoration(rvAlbumsDecoration);
            rvAlbums.setLayoutManager(new GridLayoutManager(this, spanCount));
        }
    }

    private void updateColumnsRvMedia() {
        int spanCount = SP.getInt("n_columns_media", 3);
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
                SnackBarHandler.show(mDrawerLayout, R.string.got_permission_wr_sdcard);
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
        dialogBuilder.show();
    }


    //region UI/GRAPHIC
    private void setupUI() {
        updateColumnsRvs();
        //TODO: MUST BE FIXED
        toolbar.setPopupTheme(getPopupToolbarStyle());
        toolbar.setBackgroundColor(getPrimaryColor());
        if(localFolder) {
            toolbar.setTitle(getString(R.string.local_folder));
        }
        else{
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
        mediaAdapter.updatePlaceholder(getApplicationContext());
        albumsAdapter.updateTheme();
        /**** DRAWER ****/
        setScrollViewColor((ScrollView) findViewById(R.id.drawer_scrollbar));

        /**** recyclers drawable *****/
        Drawable drawableScrollBar = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_scrollbar);
        drawableScrollBar.setColorFilter(new PorterDuffColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP));
    }

    private void setDrawerTheme() {

        findViewById(R.id.Drawer_Header).setBackgroundColor(getPrimaryColor());
        findViewById(R.id.Drawer_Body).setBackgroundColor(getDrawerBackground());
        findViewById(R.id.drawer_scrollbar).setBackgroundColor(getDrawerBackground());
        findViewById(R.id.Drawer_Body_Divider).setBackgroundColor(getIconColor());

        /** TEXT VIEWS **/
        int color = getTextColor();
        ((TextView) findViewById(R.id.Drawer_Default_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_Setting_Item)).setTextColor(color);

        ((TextView) findViewById(R.id.Drawer_About_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_hidden_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_share_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_rate_Item)).setTextColor(color);
        ((TextView) findViewById(R.id.Drawer_Upload_Item)).setTextColor(color);

        /** ICONS **/
        color = getIconColor();
        ((IconicsImageView) findViewById(R.id.Drawer_Default_Icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.Drawer_Setting_Icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.Drawer_About_Icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.Drawer_hidden_Icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.Drawer_share_Icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.Drawer_rate_Icon)).setColor(color);
        ((IconicsImageView) findViewById(R.id.Drawer_Upload_Icon)).setColor(color);

        // Default setting
        if(localFolder)
            findViewById(R.id.ll_drawer_Default).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_200));

        tint();
        findViewById(R.id.ll_drawer_Setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings=true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.ll_drawer_About).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about=true;
                mDrawerLayout.closeDrawer(GravityCompat.START);

            }
        });

        findViewById(R.id.ll_drawer_uploadhistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadHistory=true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        findViewById(R.id.ll_drawer_Default).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFolder=true;
                findViewById(R.id.ll_drawer_hidden).setBackgroundColor(Color.TRANSPARENT);
                findViewById(R.id.ll_drawer_Default).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_200));
                tint();
                toolbar.setTitle(getString(R.string.local_folder));
                hidden = false;
                mDrawerLayout.closeDrawer(GravityCompat.START);
                new PrepareAlbumTask().execute();
            }
        });
        findViewById(R.id.ll_drawer_hidden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localFolder=false;
                findViewById(R.id.ll_drawer_Default).setBackgroundColor(Color.TRANSPARENT);
                findViewById(R.id.ll_drawer_hidden).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.md_grey_200));
                tint();
                toolbar.setTitle(getString(R.string.hidden_folder));
                if (securityObj.isActiveSecurity() && securityObj.isPasswordOnHidden()) {
                    AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                    final EditText editTextPassword = securityObj.getInsertPasswordDialog(LFMainActivity.this, passwordDialogBuilder);
                    passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);

                    final AlertDialog passwordDialog = passwordDialogBuilder.create();
                    passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    passwordDialog.show();

                    passwordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (securityObj.checkPassword(editTextPassword.getText().toString())) {
                                hidden = true;
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                new PrepareAlbumTask().execute();
                                passwordDialog.dismiss();
                            } else {
                                SnackBarHandler.show(mDrawerLayout, R.string.wrong_password);
                                editTextPassword.getText().clear();
                                editTextPassword.requestFocus();
                            }
                        }
                    });
                } else {
                    hidden = true;
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    new PrepareAlbumTask().execute();
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
            if(getAlbums().getSelectedCount()==0) {
                clearOverlay();
                checkForReveal = true;
                swipeRefreshLayout.setEnabled(true);
            }
            else {
                appBarOverlay();
                swipeRefreshLayout.setEnabled(false);
            }
            if (editMode)
                toolbar.setTitle(getAlbums().getSelectedCount() + "/" + getAlbums().dispAlbums.size());
            else {
                if(hidden)
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
            if(getAlbum().getSelectedCount()==0) {
                clearOverlay();
                checkForReveal = true;
                swipeRefreshLayout.setEnabled(true);
            }
            else {
                appBarOverlay();
                swipeRefreshLayout.setEnabled(false);
            }
            if (editMode)
                if (!all_photos)
                    toolbar.setTitle(getAlbum().getSelectedCount() + "/" + getAlbum().getMedia().size());
                else {
                    toolbar.setTitle(selectedMedias.size() + "/" + size);}
            else {
                if (!all_photos)
                    toolbar.setTitle(getAlbum().getName());
                else toolbar.setTitle(getString(R.string.all_media));
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
        editMode = false;
        if (albumsMode) {
            getAlbums().clearSelectedAlbums();
            albumsAdapter.notifyDataSetChanged();
        } else {
            getAlbum().clearSelectedPhotos();
            mediaAdapter.notifyDataSetChanged();
        }
        invalidateOptionsMenu();
    }

    private void checkNothing() {
        TextView a = (TextView) findViewById(R.id.nothing_to_show);
        a.setTextColor(getTextColor());
        a.setVisibility((albumsMode && getAlbums().dispAlbums.size() == 0) || (!albumsMode && getAlbum().getMedia().size() == 0) ? View.VISIBLE : View.GONE);
    }

    //region MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_albums, menu);

        if (albumsMode) {
            menu.findItem(R.id.select_all).setTitle(
                    getString(getAlbums().getSelectedCount() == albumsAdapter.getItemCount()
                            ? R.string.clear_selected
                            : R.string.select_all));
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
            menu.findItem(R.id.select_all).setTitle(getString(
                    getAlbum().getSelectedCount() == mediaAdapter.getItemCount()
                            || selectedMedias.size() == size
                            ? R.string.clear_selected
                            : R.string.select_all));
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

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        if (albumsMode) {
            editMode = getAlbums().getSelectedCount() != 0;
            menu.setGroupVisible(R.id.album_options_menu, editMode);
            menu.setGroupVisible(R.id.photos_option_men, false);
            menu.findItem(R.id.all_photos).setVisible(!editMode);
            if(getAlbums().getSelectedCount() > 1)
                menu.findItem(R.id.album_details).setVisible(false);
        } else {
            if (!all_photos) {
                editMode = getAlbum().areMediaSelected();
                menu.setGroupVisible(R.id.photos_option_men, editMode);
                menu.setGroupVisible(R.id.album_options_menu, !editMode);
                menu.findItem(R.id.all_photos).setVisible(false);
                menu.findItem(R.id.album_details).setVisible(false);
            } else {
                editMode = selectedMedias.size() != 0;
                menu.setGroupVisible(R.id.photos_option_men, editMode);
                menu.setGroupVisible(R.id.album_options_menu, !editMode);
                menu.findItem(R.id.all_photos).setVisible(false);
                menu.findItem(R.id.album_details).setVisible(false);
            }
        }

        togglePrimaryToolbarOptions(menu);
        updateSelectedStuff();
        visible = getAlbum().getSelectedCount() > 0;
        menu.findItem(R.id.action_copy).setVisible(visible);
        menu.findItem(R.id.action_move).setVisible(visible || editMode);
        menu.findItem(R.id.excludeAlbumButton).setVisible(editMode && !all_photos && albumsMode);
        menu.findItem(R.id.select_all).setVisible(editMode);
        menu.findItem(R.id.delete_action).setVisible((!albumsMode || editMode) && (!all_photos || editMode));
        menu.findItem(R.id.hideAlbumButton).setVisible(!all_photos && getAlbums().getSelectedCount() > 0);

        menu.findItem(R.id.clear_album_preview).setVisible(!albumsMode && getAlbum().hasCustomCover());
        menu.findItem(R.id.renameAlbum).setVisible(((albumsMode && getAlbums().getSelectedCount() == 1) || (!albumsMode && !editMode)) && !all_photos);
        if (getAlbums().getSelectedCount() == 1)
            menu.findItem(R.id.set_pin_album).setTitle(getAlbums().getSelectedAlbum(0).isPinned() ? getString(R.string.un_pin) : getString(R.string.pin));
        menu.findItem(R.id.set_pin_album).setVisible(albumsMode && getAlbums().getSelectedCount() == 1);
        menu.findItem(R.id.setAsAlbumPreview).setVisible(!albumsMode && !all_photos && getAlbum().getSelectedCount() == 1);
        menu.findItem(R.id.affixPhoto).setVisible(!albumsMode && (getAlbum().getSelectedCount() > 1) || selectedMedias.size() > 1);
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
                        //empty method body
                    }});
                detailsDialog.show();
                return true;

            case R.id.select_all:
                if (albumsMode) {
                    //if all albums are already selected, unselect all of them
                    if (getAlbums().getSelectedCount() == albumsAdapter.getItemCount()) {
                        editMode = false;
                        getAlbums().clearSelectedAlbums();
                    }
                    // else, select all albums
                    else getAlbums().selectAllAlbums();
                    albumsAdapter.notifyDataSetChanged();
                } else {
                    if (!all_photos) {
                        //if all photos are already selected, unselect all of them
                        if (getAlbum().getSelectedCount() == mediaAdapter.getItemCount()) {
                            editMode = false;
                            getAlbum().clearSelectedPhotos();
                        }
                        // else, select all photos
                        else getAlbum().selectAllPhotos();
                        mediaAdapter.notifyDataSetChanged();
                    } else {

                        if (selectedMedias.size() == size) {
                            editMode = false;
                            clearSelectedPhotos();
                        }
                        // else, select all photos
                        else {
                            clearSelectedPhotos();
                            selectAllPhotos();
                        }
                        mediaAdapter.notifyDataSetChanged();
                    }
                }
                invalidateOptionsMenu();
                return true;

            case R.id.set_pin_album:
                getAlbums().getSelectedAlbum(0).settings.togglePin(getApplicationContext());
                getAlbums().sortAlbums(getApplicationContext());
                getAlbums().clearSelectedAlbums();
                albumsAdapter.swapDataSet(getAlbums().dispAlbums);
                invalidateOptionsMenu();
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
                hideDialogBuilder.show();
                return true;

            case R.id.delete_action:
                getNavigationBar();
                class DeletePhotos extends AsyncTask<String, Integer, Boolean> {
                    @Override
                    protected void onPreExecute() {
                        swipeRefreshLayout.setRefreshing(true);
                        super.onPreExecute();
                    }


                    @Override
                    protected Boolean doInBackground(String... arg0) {
                        //if in album mode, delete selected albums
                        if (albumsMode)
                            return getAlbums().deleteSelectedAlbums(LFMainActivity.this);
                        else {
                            // if in selection mode, delete selected media
                            if (editMode && !all_photos)
                                return getAlbum().deleteSelectedMedia(getApplicationContext());
                            else if (all_photos) {
                                Boolean succ = false;
                                for (Media media : selectedMedias) {
                                    String[] projection = {MediaStore.Images.Media._ID};

                                    // Match on the file path
                                    String selection = MediaStore.Images.Media.DATA + " = ?";
                                    String[] selectionArgs = new String[]{media.getPath()};

                                    // Query for the ID of the media matching the file path
                                    Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                    ContentResolver contentResolver = getContentResolver();
                                    Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
                                    if (c.moveToFirst()) {
                                        // We found the ID. Deleting the item via the content provider will also remove the file
                                        long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                        Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                                        contentResolver.delete(deleteUri, null, null);
                                        succ = true;
                                    } else {
                                        succ = false;
                                        // File not found in media store DB
                                    }
                                    c.close();
                                }
                                return succ;
                            }
                            // if not in selection mode, delete current album entirely
                            else {
                                boolean succ = getAlbums().deleteAlbum(getAlbum(), getApplicationContext());
                                getAlbum().getMedia().clear();
                                return succ;
                            }
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        if (result) {
                            // in albumsMode, the selected albums have been deleted.
                            if (albumsMode) {
                                getAlbums().clearSelectedAlbums();
                                albumsAdapter.notifyDataSetChanged();
                            } else {
                                if (!all_photos) {
                                    //if all media in current album have been deleted, delete current album too.
                                    if (getAlbum().getMedia().size() == 0) {
                                        getAlbums().removeCurrentAlbum();
                                        albumsAdapter.notifyDataSetChanged();
                                        displayAlbums();
                                        swipeRefreshLayout.setRefreshing(true);
                                    } else
                                        mediaAdapter.swapDataSet(getAlbum().getMedia());
                                } else {
                                    clearSelectedPhotos();
                                    listAll = StorageProvider.getAllShownImages(LFMainActivity.this);
                                    media = listAll;
                                    size = listAll.size();
                                    mediaAdapter.swapDataSet(listAll);
                                }
                            }
                        } else requestSdCardPermissions();

                        invalidateOptionsMenu();
                        checkNothing();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                AlertDialogsHelper.getTextDialog(this, deleteDialog, R.string.delete, albumsMode || !editMode ? R.string.delete_album_message : R.string.delete_photos_message, null);

                deleteDialog.setNegativeButton(this.getString(R.string.cancel).toUpperCase(), null);
                deleteDialog.setPositiveButton(this.getString(R.string.delete).toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (securityObj.isActiveSecurity() && securityObj.isPasswordOnDelete()) {
                            AlertDialog.Builder passwordDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                            final EditText editTextPassword = securityObj.getInsertPasswordDialog(LFMainActivity.this, passwordDialogBuilder);
                            passwordDialogBuilder.setNegativeButton(getString(R.string.cancel).toUpperCase(), null);

                            passwordDialogBuilder.setPositiveButton(getString(R.string.ok_action).toUpperCase(), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //This should br empty it will be overwrite later
                                    //to avoid dismiss of the dialog on wrong password
                                }
                            });

                            final AlertDialog passwordDialog = passwordDialogBuilder.create();
                            passwordDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                            passwordDialog.show();

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
                                        SnackBarHandler.show(mDrawerLayout, R.string.wrong_password);
                                        editTextPassword.getText().clear();
                                        editTextPassword.requestFocus();
                                    }
                                }
                            });
                        } else new DeletePhotos().execute();
                    }
                });
                deleteDialog.show();

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
                excludeDialogBuilder.show();
                return true;

            case R.id.sharePhotos:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.sent_to_action));

                // list of all selected media in current album
                ArrayList<Uri> files = new ArrayList<Uri>();
                if (!all_photos) {
                    for (Media f : getAlbum().getSelectedMedia())
                        files.add(f.getUri());
                } else {
                    for (Media f : selectedMedias)
                        files.add(f.getUri());
                }
                String extension = files.get(0).getPath().substring(files.get(0).getPath().lastIndexOf('.') + 1);
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
                if (!all_photos)
                    intent.setType(StringUtils.getGenericMIME(getAlbum().getSelectedMedia(0).getMimeType()));
                else intent.setType(mimeType);
                finishEditMode();
                startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
                return true;


            case R.id.name_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(NAME);
                    new SortingUtilsAlbums().execute();
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), NAME);
                    new SortingUtilsPhtots().execute();
                    if (all_photos) {
                        new SortingUtilsListAll().execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.date_taken_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(DATE);
                    new SortingUtilsAlbums().execute();
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), DATE);
                    new SortingUtilsPhtots().execute();
                    if (all_photos) {
                        new SortingUtilsListAll().execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.size_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(SIZE);
                    new SortingUtilsAlbums().execute();
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), SIZE);
                    new SortingUtilsPhtots().execute();
                    if (all_photos) {
                        new SortingUtilsListAll().execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.numeric_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingMode(NUMERIC);
                    new SortingUtilsAlbums().execute();
                } else {
                    getAlbum().setDefaultSortingMode(getApplicationContext(), NUMERIC);
                    new SortingUtilsPhtots().execute();
                    if (all_photos) {
                        new SortingUtilsListAll().execute();
                    }
                }
                item.setChecked(true);
                return true;

            case R.id.ascending_sort_action:
                if (albumsMode) {
                    getAlbums().setDefaultSortingAscending(item.isChecked() ? SortingOrder.DESCENDING : SortingOrder.ASCENDING);
                    new SortingUtilsAlbums().execute();
                } else {
                    getAlbum().setDefaultSortingAscending(getApplicationContext(), item.isChecked() ? SortingOrder.DESCENDING : SortingOrder.ASCENDING);
                    new SortingUtilsPhtots().execute();
                    if (all_photos) {
                        new SortingUtilsListAll().execute();
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
                                SnackBarHandler.show(mDrawerLayout, R.string.affix_error);
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
                            new PreparePhotosTask().execute();
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
                builder.show();


                return true;
            //endregion

            case R.id.action_move:

                bottomSheetDialogFragment = new SelectAlbumBottomSheet();
                bottomSheetDialogFragment.setTitle(getString(R.string.move_to));
                bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
                    @Override
                    public void folderSelected(String path) {
                        swipeRefreshLayout.setRefreshing(true);
                        int numberOfImagesMoved;
                        if ((numberOfImagesMoved = getAlbum().moveSelectedMedia(getApplicationContext(), path)) > 0) {
                            if (getAlbum().getMedia().size() == 0) {
                                getAlbums().removeCurrentAlbum();
                                albumsAdapter.notifyDataSetChanged();
                                displayAlbums();
                            }
                            mediaAdapter.swapDataSet(getAlbum().getMedia());
                            finishEditMode();
                            invalidateOptionsMenu();
                            if(numberOfImagesMoved > 1)
                                SnackBarHandler.show(coordinatorLayoutMainContent, getString(R.string.photos_moved_successfully));
                            else
                                SnackBarHandler.show(coordinatorLayoutMainContent, getString(R.string.photo_moved_successfully));
                        } else requestSdCardPermissions();

                        swipeRefreshLayout.setRefreshing(false);
                        bottomSheetDialogFragment.dismiss();
                    }
                });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                return true;

            case R.id.action_copy:
                bottomSheetDialogFragment = new SelectAlbumBottomSheet();
                bottomSheetDialogFragment.setTitle(getString(R.string.copy_to));
                bottomSheetDialogFragment.setSelectAlbumInterface(new SelectAlbumBottomSheet.SelectAlbumInterface() {
                    @Override
                    public void folderSelected(String path) {
                        boolean success = getAlbum().copySelectedPhotos(getApplicationContext(), path);
                        finishEditMode();
                        bottomSheetDialogFragment.dismiss();
                        if (!success)
                            requestSdCardPermissions();
                        else
                            SnackBarHandler.show(coordinatorLayoutMainContent, getString(R.string.copied_successfully));
                    }
                });
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                return true;

            case R.id.renameAlbum:
                AlertDialog.Builder renameDialogBuilder = new AlertDialog.Builder(LFMainActivity.this, getDialogStyle());
                final EditText editTextNewName = new EditText(getApplicationContext());
                editTextNewName.setText(albumsMode ? getAlbums().getSelectedAlbum(0).getName() : getAlbum().getName());
                final String albumName=albumsMode ? getAlbums().getSelectedAlbum(0).getName() : getAlbum().getName();

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

                renameDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View dialog) {
                        boolean rename=false;
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
                                    SnackBarHandler.show(mDrawerLayout, getString(R.string.rename_no_change));
                                    rename = true;
                                }
                            } else {
                                success = getAlbum().renameAlbum(getApplicationContext(), editTextNewName.getText().toString());
                                toolbar.setTitle(getAlbum().getName());
                                mediaAdapter.notifyDataSetChanged();
                            }
                            renameDialog.dismiss();
                            if (success) {
                                SnackBarHandler.show(getWindow().getDecorView().getRootView(), getString(R.string.rename_succes));
                                getAlbums().clearSelectedAlbums();
                                invalidateOptionsMenu();
                            } else if(!rename){
                                SnackBarHandler.show(getWindow().getDecorView().getRootView(), getString(R.string.rename_error));
                                requestSdCardPermissions();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            SnackBarHandler.show(mDrawerLayout, R.string.insert_something);
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

    private Bitmap getBitmap(String path) {

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
        if(editMode && hidenav)
        {
            navigationView.setVisibility(View.VISIBLE);
            hidenav=false;
        }
    }

    /**
     * If we are in albumsMode, make the albums recyclerView visible. If we are not, make media recyclerView visible.
     *
     * @param albumsMode it indicates whether we are in album selection mode or not
     */
    private void toggleRecyclersVisibility(boolean albumsMode) {
        rvAlbums.setVisibility(albumsMode ? View.VISIBLE : View.GONE);
        rvMedia.setVisibility(albumsMode ? View.GONE : View.VISIBLE);
        //touchScrollBar.setScrollBarHidden(albumsMode);

    }
    private void tint()
    {
        IconicsImageView defaultIcon=(IconicsImageView) findViewById(R.id.Drawer_Default_Icon);
        IconicsImageView hiddenIcon=(IconicsImageView) findViewById(R.id.Drawer_hidden_Icon);
        TextView  defaultText=(TextView) findViewById(R.id.Drawer_Default_Item);
        TextView  hiddenText=(TextView) findViewById(R.id.Drawer_hidden_Item);

        if(localFolder) {
        defaultIcon.setColor(getPrimaryColor());
        defaultText.setTextColor(getPrimaryColor());
        hiddenIcon.setColor(getIconColor());
        hiddenText.setTextColor(getTextColor());
    }
    else  {
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
        if(editMode && all_photos)
            clearSelectedPhotos();
        getNavigationBar();
        if (editMode) finishEditMode();
        else {
            if (albumsMode) {
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                else
                {
                    if(doubleBackToExitPressedOnce && isTaskRoot())
                        finish();
                    else if(isTaskRoot())
                    {
                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(this, R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);
                    }
                    else
                        super.onBackPressed();
                }
            } else {
                displayAlbums();
                setRecentApp(getString(R.string.app_name));
            }
        }
    }


    private class PrepareAlbumTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            toggleRecyclersVisibility(true);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getAlbums().loadAlbums(getApplicationContext(), hidden);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            albumsAdapter.swapDataSet(getAlbums().dispAlbums);
            checkNothing();
            swipeRefreshLayout.setRefreshing(false);
            getAlbums().saveBackup(getApplicationContext());
            invalidateOptionsMenu();
            finishEditMode();
        }
    }

    private class PreparePhotosTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            toggleRecyclersVisibility(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getAlbum().updatePhotos(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mediaAdapter.swapDataSet(getAlbum().getMedia());
            if (!hidden)
                HandlingAlbums.addAlbumToBackup(getApplicationContext(), getAlbum());
            checkNothing();
            swipeRefreshLayout.setRefreshing(false);
            invalidateOptionsMenu();
            finishEditMode();
        }
    }

    private class PrepareAllPhotos extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            toggleRecyclersVisibility(false);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getAlbum().updatePhotos(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listAll = StorageProvider.getAllShownImages(LFMainActivity.this);
            Collections.sort(listAll, MediaComparators.getComparator(getAlbum().settings.getSortingMode(), getAlbum().settings.getSortingOrder()));
            mediaAdapter.swapDataSet(listAll);
            if (!hidden)
                HandlingAlbums.addAlbumToBackup(getApplicationContext(), getAlbum());
            checkNothing();
            swipeRefreshLayout.setRefreshing(false);
            invalidateOptionsMenu();
            finishEditMode();
            toolbar.setTitle(getString(R.string.all_media));
            clearSelectedPhotos();
        }
    }

    /*
    Async Class for Sorting Photos - NOT listAll
     */
    private class SortingUtilsPhtots extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            getAlbum().sortPhotos();
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            mediaAdapter.swapDataSet(getAlbum().getMedia());
        }
    }

    /*
    Async Class for Sorting Photos - listAll
     */
    private class SortingUtilsListAll extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            Collections.sort(listAll, MediaComparators.getComparator(getAlbum().settings.getSortingMode(), getAlbum().settings.getSortingOrder()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            mediaAdapter.swapDataSet(listAll);
        }
    }

    /*
    Async Class for Sorting Albums
     */
    private class SortingUtilsAlbums extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... aVoid) {
            getAlbums().sortAlbums(getApplicationContext());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            swipeRefreshLayout.setRefreshing(false);
            albumsAdapter.swapDataSet(getAlbums().dispAlbums);
            new PrepareAlbumTask().execute();
        }
    }
}
