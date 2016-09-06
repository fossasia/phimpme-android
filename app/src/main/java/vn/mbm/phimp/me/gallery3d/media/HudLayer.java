/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.mbm.phimp.me.gallery3d.media;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.gallery3d.media.MenuBar.Menu;
import vn.mbm.phimp.me.gallery3d.media.PopupMenu.Option;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.view.MotionEvent;

public final class HudLayer extends Layer {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SELECT = 1;

    private Context mContext;
    private GridLayer mGridLayer;
    private final ImageButton mTopRightButton = new ImageButton();
    private final ImageButton mZoomInButton = new ImageButton();
    private final ImageButton mZoomOutButton = new ImageButton();
    private PathBarLayer mPathBar;
    private TimeBar mTimeBar;
    private MenuBar.Menu[] mNormalBottomMenu = null;
    private MenuBar.Menu[] mSingleViewIntentBottomMenu = null;
    private MenuBar.Menu[] mNormalBottomMenuNoShare = null;
    private MenuBar.Menu[] mSingleViewIntentBottomMenuNoShare = null;
    private final MenuBar mSelectionMenuBottom;
    private final MenuBar mSelectionMenuTop;
    private final MenuBar mFullscreenMenu;
    private final LoadingLayer mLoadingLayer = new LoadingLayer();
    private RenderView mView = null;

    private int mMode = MODE_NORMAL;

    // Camera button - launches the camera intent when pressed.
    @SuppressWarnings("static-access")
	private static final int CAMERA_BUTTON_ICON = Res.drawable.btn_camera;
    @SuppressWarnings("static-access")
	private static final int CAMERA_BUTTON_ICON_PRESSED = Res.drawable.btn_camera_pressed;
    @SuppressWarnings("static-access")
	private static final int ZOOM_IN_ICON = Res.drawable.gallery_zoom_in;
    @SuppressWarnings("static-access")
	private static final int ZOOM_IN_ICON_PRESSED = Res.drawable.gallery_zoom_in_touch;
    @SuppressWarnings("static-access")
	private static final int ZOOM_OUT_ICON = Res.drawable.gallery_zoom_out;
    @SuppressWarnings("static-access")
	private static final int ZOOM_OUT_ICON_PRESSED = Res.drawable.gallery_zoom_out_touch;

    private final Runnable mCameraButtonAction = new Runnable() {
        public void run() {
            // Launch the camera intent.
            Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mContext.startActivity(intent);
        }
    };

    // Grid mode button - switches the media browser to grid mode.
    @SuppressWarnings("static-access")
	private static final int GRID_MODE_ICON = Res.drawable.mode_stack;
    @SuppressWarnings("static-access")
	private static final int GRID_MODE_PRESSED_ICON = Res.drawable.mode_stack;

    private final Runnable mZoomInButtonAction = new Runnable() {
        public void run() {
            mGridLayer.zoomInToSelectedItem();
            mGridLayer.markDirty(1);
        }
    };

    private final Runnable mZoomOutButtonAction = new Runnable() {
        public void run() {
            mGridLayer.zoomOutFromSelectedItem();
            mGridLayer.markDirty(1);
        }
    };

    private final Runnable mGridModeButtonAction = new Runnable() {
        public void run() {
            mGridLayer.setState(GridLayer.STATE_GRID_VIEW);
        }
    };

    /**
     * Stack mode button - switches the media browser to grid mode.
     */
    @SuppressWarnings("static-access")
	private static final int STACK_MODE_ICON = Res.drawable.mode_grid;
    @SuppressWarnings("static-access")
	private static final int STACK_MODE_PRESSED_ICON = Res.drawable.mode_grid;
    private final Runnable mStackModeButtonAction = new Runnable() {
        public void run() {
            mGridLayer.setState(GridLayer.STATE_TIMELINE);
        }
    };
    private float mAlpha;
    private float mAnimAlpha;
    private boolean mAutoHide;
    private long mLastTimeFullOpacity;
    private String mCachedCaption;
    private String mCachedPosition;
    private String mCachedCurrentLabel;

    @SuppressWarnings("unused")
	HudLayer(Context context) 
    {
        mAlpha = 1.0f;
        if (mTimeBar == null) 
        {
            mTimeBar = new TimeBar(context);
            mPathBar = new PathBarLayer();
        }
        mTopRightButton.setSize((int) (100 * App.PIXEL_DENSITY), (int) (94 * App.PIXEL_DENSITY));

        mZoomInButton.setSize(66.666f * App.PIXEL_DENSITY, 42 * App.PIXEL_DENSITY);
        mZoomOutButton.setSize(66.666f * App.PIXEL_DENSITY, 42 * App.PIXEL_DENSITY);
        mZoomInButton.setImages(ZOOM_IN_ICON, ZOOM_IN_ICON_PRESSED);
        mZoomInButton.setAction(mZoomInButtonAction);
        mZoomOutButton.setImages(ZOOM_OUT_ICON, ZOOM_OUT_ICON_PRESSED);
        mZoomOutButton.setAction(mZoomOutButtonAction);
        
        //TODO:thong
        mSelectionMenuBottom = new MenuBar(context);
        mSelectionMenuTop = new MenuBar(context);
        mFullscreenMenu = new MenuBar(context);

        // The Share submenu is populated dynamically when opened.
        Resources resources = context.getResources();
        /*PopupMenu.Option[] deleteOptions = {
                new PopupMenu.Option(context.getResources().getString(Res.string.confirm_delete), resources
                        .getDrawable(Res.drawable.icon_delete), new Runnable() {
                    public void run() {
                        deleteSelection();
                    }
                }),
                new PopupMenu.Option(context.getResources().getString(Res.string.cancel), resources
                        .getDrawable(Res.drawable.icon_cancel), new Runnable() {
                    public void run() {

                    }
                }), };*/
        /*mSelectionMenuBottom = new MenuBar(context);*/

        /*MenuBar.Menu shareMenu = new MenuBar.Menu.Builder(context.getResources().getString(Res.string.share)).icon(
                Res.drawable.icon_share).onSelect(new Runnable() {
            public void run() {
                updateShareMenu();
            }
        }).build();*/

        /*MenuBar.Menu deleteMenu = new MenuBar.Menu.Builder(context.getResources().getString(Res.string.delete)).icon(
                Res.drawable.icon_delete).options(deleteOptions).build();*/

        /*MenuBar.Menu moreMenu = new MenuBar.Menu.Builder(context.getResources().getString(Res.string.more)).icon(
                Res.drawable.icon_more).onSelect(new Runnable() {
            public void run() {
                buildMoreOptions();
            }
        }).build();*/

        /*mNormalBottomMenu = new MenuBar.Menu[] { shareMenu, deleteMenu, moreMenu };
        mSingleViewIntentBottomMenu = new MenuBar.Menu[] { shareMenu, moreMenu };
        
        mNormalBottomMenuNoShare = new MenuBar.Menu[] { deleteMenu, moreMenu };
        mSingleViewIntentBottomMenuNoShare = new MenuBar.Menu[] { moreMenu };*/

        /*mSelectionMenuBottom.setMenus(mNormalBottomMenu);
        mSelectionMenuTop = new MenuBar(context);
        mSelectionMenuTop.setMenus(new MenuBar.Menu[] {
                new MenuBar.Menu.Builder(context.getResources().getString(Res.string.select_all)).onSelect(new Runnable() {
                    public void run() {
                        mGridLayer.selectAll();
                    }
                }).build(), new MenuBar.Menu.Builder("").build(),
                new MenuBar.Menu.Builder(context.getResources().getString(Res.string.deselect_all)).onSelect(new Runnable() {
                    public void run() {
                        mGridLayer.deselectOrCancelSelectMode();
                    }
                }).build() });
        mFullscreenMenu = new MenuBar(context);*/
        /*mFullscreenMenu.setMenus(new MenuBar.Menu[] {
                new MenuBar.Menu.Builder(context.getResources().getString(Res.string.slideshow)).icon(Res.drawable.icon_play)
                        .onSingleTapUp(new Runnable() {
                            public void run() {
                                if (getAlpha() == 1.0f)
                                    mGridLayer.startSlideshow();
                                else
                                    setAlpha(1.0f);
                            }
                        }).build(),  new MenuBar.Menu.Builder("").build(), 
                new MenuBar.Menu.Builder(context.getResources().getString(Res.string.menu)).icon(Res.drawable.icon_more)
                        .onSingleTapUp(new Runnable() {
                            public void run() {
                                if (getAlpha() == 1.0f)
                                    mGridLayer.enterSelectionMode();
                                else
                                    setAlpha(1.0f);
                            }
                        }).build() });*/
    }

    public void setContext(Context context) {
        if (mContext != context) {
            mContext = context;
            mTimeBar.regenerateStringsForContext(context);
        }
    }

    @SuppressWarnings({ "unused", "static-access" })
	private void buildMoreOptions() {
        ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();

        int numBuckets = buckets.size();
        boolean albumMode = false;
        boolean singleItem = false;
        boolean isPicasa = false;
        int mediaType = MediaItem.MEDIA_TYPE_IMAGE;
        if (numBuckets > 1) {
            albumMode = true;
        }
        if (numBuckets == 1) {
            MediaBucket bucket = buckets.get(0);
            MediaSet mediaSet = bucket.mediaSet;
            if (mediaSet == null) {
                return;
            }
            isPicasa = mediaSet.mPicasaAlbumId != Shared.INVALID;
            if (bucket.mediaItems == null || bucket.mediaItems.size() == 0) {
                albumMode = true;
            } else {
                ArrayList<MediaItem> items = bucket.mediaItems;
                int numItems = items.size();
                mediaType = items.get(0).getMediaType();
                if (numItems == 1) {
                    singleItem = true;
                } else {
                    for (int i = 1; i < numItems; ++i) {
                        if (items.get(0).getMediaType() != mediaType) {
                            albumMode = true;
                            break;
                        }
                    }
                }
            }
        }

        Option[] optionAll = new Option[] { new PopupMenu.Option(mContext.getResources().getString(Res.string.details), mContext
                .getResources().getDrawable(Res.drawable.ic_menu_view_details), new Runnable() {
            public void run() {
                ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getResources().getString(Res.string.details));
                boolean foundDataToDisplay = true;

                if (buckets == null) {
                    foundDataToDisplay = false;
                } else {
                    CharSequence[] strings = DetailMode.populateDetailModeStrings(mContext, buckets);
                    if (strings == null) {
                        foundDataToDisplay = false;
                    } else {
                        builder.setItems(strings, null);
                    }
                }

                mGridLayer.deselectAll();
                if (foundDataToDisplay) {
                    builder.setNeutralButton(Res.string.details_ok, null);
                    App.get(mContext).getHandler().post(new Runnable() {
                        public void run() {
                            builder.show();
                        }
                    });
                }
            }
        }) };

        Option[] optionSingle = new Option[] { new PopupMenu.Option(mContext.getResources().getString(Res.string.show_on_map),
                mContext.getResources().getDrawable(Res.drawable.ic_menu_mapmode), new Runnable() {
                    public void run() {
                        ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                        MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                        if (item == null) {
                            return;
                        }
                        mGridLayer.deselectAll();
                        Util.openMaps(mContext, item.mLatitude, item.mLongitude);
                    }
                }), };

        Option[] optionImageMultiple = new Option[] {
                new PopupMenu.Option(mContext.getResources().getString(Res.string.rotate_left), mContext.getResources()
                        .getDrawable(Res.drawable.ic_menu_rotate_left), new Runnable() {
                    public void run() {
                        mGridLayer.rotateSelectedItems(-90.0f);
                    }
                }),
                new PopupMenu.Option(mContext.getResources().getString(Res.string.rotate_right), mContext.getResources()
                        .getDrawable(Res.drawable.ic_menu_rotate_right), new Runnable() {
                    public void run() {
                        mGridLayer.rotateSelectedItems(90.0f);
                    }
                }), };

        if (isPicasa) {
            optionImageMultiple = new Option[] {};
        }
        Option[] optionImageSingle;
        if (isPicasa) {
            optionImageSingle = new Option[] { new PopupMenu.Option(mContext.getResources().getString(Res.string.set_as_wallpaper),
                    mContext.getResources().getDrawable(Res.drawable.ic_menu_set_as), new Runnable() {
                        public void run() {
                            ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                            MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                            if (item == null) {
                                return;
                            }
                            mGridLayer.deselectAll();
                            if (item.mParentMediaSet.mPicasaAlbumId != Shared.INVALID) {
                                final Intent intent = new Intent("android.intent.action.ATTACH_DATA");
                                intent.setClass(mContext, Photographs.class);
                                intent.setData(Uri.parse(item.mContentUri));
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                ((Activity) mContext).startActivityForResult(intent, 0);
                            }
                        }
                    }) };
        } else {
            optionImageSingle = new Option[] {
                    new PopupMenu.Option((isPicasa) ? mContext.getResources().getString(Res.string.set_as_wallpaper) : mContext
                            .getResources().getString(Res.string.set_as), mContext.getResources().getDrawable(
                            Res.drawable.ic_menu_set_as), new Runnable() {
                        public void run() {
                            ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                            MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                            if (item == null) {
                                return;
                            }
                            mGridLayer.deselectAll();
                            if (item.mParentMediaSet.mPicasaAlbumId != Shared.INVALID) {
                                final Intent intent = new Intent("android.intent.action.ATTACH_DATA");
                                intent.setClass(mContext, Photographs.class);
                                intent.setData(Uri.parse(item.mContentUri));
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                ((Activity) mContext).startActivityForResult(intent, 0);
                            } else {
                                Intent intent = Util.createSetAsIntent(Uri.parse(item.mContentUri), item.mMimeType);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                ((Activity) mContext).startActivity(Intent.createChooser(intent, mContext
                                        .getText(Res.string.set_image)));
                            }
                        }
                    }),
                    new PopupMenu.Option(mContext.getResources().getString(Res.string.crop), mContext.getResources().getDrawable(
                            Res.drawable.ic_menu_crop), new Runnable() {
                        public void run() {
                            ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                            MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                            if (item == null) {
                                return;
                            }
                            mGridLayer.deselectAll();
                            final Intent intent = new Intent("com.android.camera.action.CROP");
                            intent.setClass(mContext, CropImage.class);
                            intent.setData(Uri.parse(item.mContentUri));
                            ((Activity) mContext).startActivityForResult(intent, CropImage.CROP_MSG_INTERNAL);
                        }
                    }) };
        }
        Option[] options = optionAll;
        if (!albumMode) {
            if (!singleItem) {
                if (mediaType == MediaItem.MEDIA_TYPE_IMAGE)
                    options = concat(options, optionImageMultiple);
            } else {
                MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                if (item.mLatitude != 0.0f && item.mLongitude != 0.0f) {
                    options = concat(options, optionSingle);
                }
                if (mediaType == MediaItem.MEDIA_TYPE_IMAGE) {
                    options = concat(options, optionImageSingle);
                    options = concat(options, optionImageMultiple);
                }
            }
        }

        // We are assuming that the more menu is the last item in the menu
        // array.
        int lastIndex = mSelectionMenuBottom.getMenus().length - 1;
        mSelectionMenuBottom.getMenus()[lastIndex].options = options;
    }

    private static final Option[] concat(Option[] A, Option[] B) {
        Option[] C = (Option[]) new Option[A.length + B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        return C;
    }

    @SuppressWarnings("static-access")
	public void updateNumItemsSelected(int numItems) {
        String items = " " + ((numItems == 1) ? mContext.getString(Res.string.item) : mContext.getString(Res.string.items));
        Menu menu = new MenuBar.Menu.Builder(numItems + items).config(MenuBar.MENU_TITLE_STYLE_TEXT).build();
        mSelectionMenuTop.updateMenu(menu, 1);
    }

    protected void deleteSelection() {
        mGridLayer.deleteSelection();
    }

    void setGridLayer(GridLayer layer) {
        mGridLayer = layer;
        updateViews();
    }

    int getMode() {
        return mMode;
    }

    void setMode(int mode) {
        if (mMode != mode) {
            mMode = mode;
            updateViews();
        }
    }

    @Override
    protected void onSizeChanged() {
        final float width = mWidth;
        final float height = mHeight;
        closeSelectionMenu();

        mTimeBar.setPosition(0f, height - TimeBar.HEIGHT * App.PIXEL_DENSITY);
        mTimeBar.setSize(width, TimeBar.HEIGHT * App.PIXEL_DENSITY);
        mSelectionMenuTop.setPosition(0f, 0);
        mSelectionMenuTop.setSize(width, MenuBar.HEIGHT * App.PIXEL_DENSITY);
        mSelectionMenuBottom.setPosition(0f, height - MenuBar.HEIGHT * App.PIXEL_DENSITY);
        mSelectionMenuBottom.setSize(width, MenuBar.HEIGHT * App.PIXEL_DENSITY);

        mFullscreenMenu.setPosition(0f, height - MenuBar.HEIGHT * App.PIXEL_DENSITY);
        mFullscreenMenu.setSize(width, MenuBar.HEIGHT * App.PIXEL_DENSITY);

        mPathBar.setPosition(0f, -4f * App.PIXEL_DENSITY);
        computeSizeForPathbar();

        mTopRightButton.setPosition(width - mTopRightButton.getWidth(), 0f);
        float zoomY = height - MenuBar.HEIGHT * App.PIXEL_DENSITY - mZoomInButton.getHeight();
        mZoomInButton.setPosition(width - mZoomInButton.getWidth(), zoomY);
        mZoomOutButton.setPosition(width - mZoomInButton.getWidth() * 2.0f, zoomY);
    }

    private void computeSizeForPathbar() {
        float pathBarWidth = mWidth
                - ((mGridLayer.getState() == GridLayer.STATE_FULL_SCREEN) ? 32 * App.PIXEL_DENSITY : 120 * App.PIXEL_DENSITY);
        mPathBar.setSize(pathBarWidth, (float) Math.ceil(39 * App.PIXEL_DENSITY));
        mPathBar.recomputeComponents();
    }

    public void setFeed(MediaFeed feed, int state, boolean needsLayout) {
        mTimeBar.setFeed(feed, state, needsLayout);
    }

    public void onGridStateChanged() {
        updateViews();
    }

    private void updateViews() {
        if (mGridLayer == null)
            return;
        final int state = mGridLayer.getState();
        // Show the selection menu in selection mode.
        final boolean selectionMode = mMode == MODE_SELECT;
        final boolean fullscreenMode = state == GridLayer.STATE_FULL_SCREEN;
        final boolean stackMode = state == GridLayer.STATE_MEDIA_SETS || state == GridLayer.STATE_TIMELINE;
        mSelectionMenuTop.setHidden(!selectionMode || fullscreenMode);
        mSelectionMenuBottom.setHidden(!selectionMode);
        mFullscreenMenu.setHidden(!fullscreenMode || selectionMode);
        mZoomInButton.setHidden(mFullscreenMenu.isHidden());
        mZoomOutButton.setHidden(mFullscreenMenu.isHidden());

        // Show the time bar in stack and grid states, except in selection mode.
        mTimeBar.setHidden(fullscreenMode || selectionMode || stackMode);
        // mTimeBar.setHidden(selectionMode || (state !=
        // GridLayer.STATE_TIMELINE && state != GridLayer.STATE_GRID_VIEW));

        // Hide the path bar and top-right button in selection mode.
        mPathBar.setHidden(selectionMode);
        mTopRightButton.setHidden(selectionMode || fullscreenMode);
        computeSizeForPathbar();

        // Configure the top-right button.
        int image = 0;
        int pressedImage = 0;
        Runnable action = null;
        final ImageButton topRightButton = mTopRightButton;
        int height = (int) (94 * App.PIXEL_DENSITY);
        switch (state) {
        case GridLayer.STATE_MEDIA_SETS:
            image = CAMERA_BUTTON_ICON;
            pressedImage = CAMERA_BUTTON_ICON_PRESSED;
            action = mCameraButtonAction;
            break;
        case GridLayer.STATE_GRID_VIEW:
            height /= 2;
            image = STACK_MODE_ICON;
            pressedImage = STACK_MODE_PRESSED_ICON;
            action = mStackModeButtonAction;
            break;
        case GridLayer.STATE_TIMELINE:
            image = GRID_MODE_ICON;
            pressedImage = GRID_MODE_PRESSED_ICON;
            action = mGridModeButtonAction;
            break;
        case GridLayer.STATE_FULL_SCREEN:
            if (getGridLayer() != null && getGridLayer().getFeed() != null
                    && getGridLayer().getFeed().getExpandedMediaSet() != null) {
                if (getGridLayer().getFeed().getExpandedMediaSet().mId == LocalDataSource.CAMERA_BUCKET_ID) {
                    image = CAMERA_BUTTON_ICON;
                    pressedImage = CAMERA_BUTTON_ICON_PRESSED;
                    action = mCameraButtonAction;
                    topRightButton.setHidden(false);
                }
            }
            break;
        default:
            break;
        }
        topRightButton.setSize((int) (100 * App.PIXEL_DENSITY), height);
        topRightButton.setImages(image, pressedImage);
        topRightButton.setAction(action);
    }

    public TimeBar getTimeBar() {
        return mTimeBar;
    }

    public PathBarLayer getPathBar() {
        return mPathBar;
    }

    public GridLayer getGridLayer() {
        return mGridLayer;
    }

    @Override
    public boolean update(RenderView view, float frameInterval) {
        float factor = 1.0f;
        if (mAlpha == 1.0f) {
            // Speed up the animation when it becomes visible.
            factor = 4.0f;
        }
        mAnimAlpha = FloatUtils.animate(mAnimAlpha, mAlpha, frameInterval * factor);

        if (mAutoHide) {
            if (mAlpha == 1.0f && mMode != MODE_SELECT) {
                long now = System.currentTimeMillis();
                if (now - mLastTimeFullOpacity >= 5000) {
                    setAlpha(0);
                }
            }
        }

        return (mAnimAlpha != mAlpha);
    }

    public void renderOpaque(RenderView view, GL11 gl) {

    }

    public void renderBlended(RenderView view, GL11 gl) {
        view.setAlpha(mAnimAlpha);
    }

    public void setAlpha(float alpha) {
        float oldAlpha = mAlpha;
        mAlpha = alpha;
        if (oldAlpha != alpha) {
            if (mView != null)
                mView.requestRender();
        }

        // We try to invoke update() again in 5 seconds to see if
        // auto hide is needed.
        if (alpha == 1.0f) {
            mLastTimeFullOpacity = System.currentTimeMillis();
            App.get(mContext).getHandler().postDelayed(new Runnable() {
                public void run() {
                    if (mView != null) {
                        mView.requestRender();
                    }
                }
            }, 5000);
        }
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setTimeBarTime(long time) {
        // mTimeBar.setTime(time);
    }

    @Override
    public void generate(RenderView view, RenderView.Lists lists) {
        lists.opaqueList.add(this);
        lists.blendedList.add(this);
        lists.hitTestList.add(this);
        lists.updateList.add(this);
        mTopRightButton.generate(view, lists);
        mZoomInButton.generate(view, lists);
        mZoomOutButton.generate(view, lists);
        mTimeBar.generate(view, lists);
        mSelectionMenuTop.generate(view, lists);
        mSelectionMenuBottom.generate(view, lists);
        mFullscreenMenu.generate(view, lists);
        mPathBar.generate(view, lists);
        mLoadingLayer.generate(view, lists);
        mView = view;
    }

    @Override
    public boolean containsPoint(float x, float y) {
        return false;
    }

    public void cancelSelection() {
        mSelectionMenuBottom.close();
        closeSelectionMenu();
        setMode(MODE_NORMAL);
    }

    public void closeSelectionMenu() {
        mSelectionMenuBottom.close();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mMode == MODE_SELECT) {
            /*
             * setMode(MODE_NORMAL); ArrayList<MediaBucket> displayBuckets =
             * mGridLayer.getSelectedBuckets(); // use this list, and then clear
             * the items return true;
             */
            return false;
        } else {
            return false;
        }
    }

    public boolean isLoaded() {
        return mLoadingLayer.isLoaded();
    }

    void reset() {
        mLoadingLayer.reset();
        mTimeBar.regenerateStringsForContext(mContext);
    }

    public void fullscreenSelectionChanged(MediaItem item, int index, int count) {
        // request = new ReverseGeocoder.Request();
        // request.firstLatitude = request.secondLatitude = item.latitude;
        // request.firstLongitude = request.secondLongitude = item.longitude;
        // mGeo.enqueue(request);
        if (item == null)
            return;
        String location = index + "/" + count;
        mCachedCaption = item.mCaption;
        mCachedPosition = location;
        mCachedCurrentLabel = location;
        mPathBar.changeLabel(location);
    }

    @SuppressWarnings("unused")
	private void updateShareMenu() {
        // Get the first selected item. Wire this up to multiple-item intents
        // when we move
        // to Eclair.
        ArrayList<MediaBucket> selection = mGridLayer.getSelectedBuckets();
        ArrayList<Uri> uris = new ArrayList<Uri>();
        String mimeType = null;
        if (!selection.isEmpty()) {
            int mediaType = Shared.INVALID;
            int numBuckets = selection.size();
            for (int j = 0; j < numBuckets; ++j) {
                MediaBucket bucket = selection.get(j);
                ArrayList<MediaItem> items = null;
                int numItems = 0;
                if (bucket.mediaItems != null && !bucket.mediaItems.isEmpty()) {
                    items = bucket.mediaItems;
                    numItems = items.size();
                } else if (bucket.mediaSet != null) {
                    // We need to delete the entire bucket.
                    items = bucket.mediaSet.getItems();
                    numItems = bucket.mediaSet.getNumItems();
                }
                for (int i = 0; i < numItems; ++i) {
                    MediaItem item = items.get(i);
                    if (mimeType == null) {
                        mimeType = item.mMimeType;
                        mediaType = item.getMediaType();
                        MediaSet parentMediaSet = item.mParentMediaSet;
                        if (parentMediaSet != null && parentMediaSet.mPicasaAlbumId != Shared.INVALID) {
                            // This will go away once http uri's are supported
                            // for all media types.
                            // This ensures that just the link is shared as a
                            // text
                            mimeType = "text/plain";
                        }
                    }
                    // Ensure that the media type remains the same
                    if (mediaType != item.getMediaType()) {
                        if (!mimeType.contains("text"))
                            mimeType = "*/*";
                    }
                    // add this uri
                    if (item.mContentUri != null) {
                        Uri uri = Uri.parse(item.mContentUri);
                        uris.add(uri);
                    }
                }
            }
        }
        PopupMenu.Option[] options = null;
        if (uris.size() != 0) {
            final Intent intent = new Intent();
            if (mimeType == null)
                mimeType = "image/jpeg";
            if (mimeType.contains("text")) {
                // We need to share this as a text string.
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(mimeType);

                // Create a newline-separated list of URLs.
                StringBuilder builder = new StringBuilder();
                for (int i = 0, size = uris.size(); i < size; ++i) {
                    builder.append(uris.get(i));
                    if (i != size - 1) {
                        builder.append('\n');
                    }
                }
                intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
            } else {
                if (uris.size() == 1) {
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
                } else {
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_STREAM, uris);
                }
                intent.setType(mimeType);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Query the system for matching activities.
            PackageManager packageManager = mContext.getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            int numActivities = activities.size();
            options = new PopupMenu.Option[numActivities];
            for (int i = 0; i != numActivities; ++i) {
                final ResolveInfo info = activities.get(i);
                String label = info.loadLabel(packageManager).toString();
                options[i] = new PopupMenu.Option(label, info.loadIcon(packageManager), new Runnable() {
                    public void run() {
                        startResolvedActivity(intent, info);
                    }
                });
            }
        }
        mSelectionMenuBottom.getMenus()[0].options = options;
    }

    private void startResolvedActivity(Intent intent, ResolveInfo info) {
        final Intent resolvedIntent = new Intent(intent);
        ActivityInfo ai = info.activityInfo;
        resolvedIntent.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));
        App.get(mContext).getHandler().post(new Runnable() {
            public void run() {
                mContext.startActivity(resolvedIntent);
            }
        });
    }

    public void autoHide(boolean hide) {
        mAutoHide = hide;
    }

    public void swapFullscreenLabel() {
        mCachedCurrentLabel = (mCachedCurrentLabel == mCachedCaption || mCachedCaption == null) ? mCachedPosition : mCachedCaption;
        mPathBar.changeLabel(mCachedCurrentLabel);
    }

    public void clear() {

    }

    public void enterSelectionMode() {
        // Do not enter selection mode if the feed is about to change.
        if (mGridLayer.feedAboutToChange())
            return;
        // Disable sharing if it is the pick intent.
        if (mGridLayer.getPickIntent()) {
            mSingleViewIntentBottomMenu = mSingleViewIntentBottomMenuNoShare;
            mNormalBottomMenu = mNormalBottomMenuNoShare;
        }
        setAlpha(1.0f);
        setMode(HudLayer.MODE_SELECT);
        // if we are in single view mode, show the bottom menu without the
        // delete button.
        if (mGridLayer.noDeleteMode()) {
            mSelectionMenuBottom.setMenus(mSingleViewIntentBottomMenu);
        } else {
            mSelectionMenuBottom.setMenus(mNormalBottomMenu);
        }
    }

    public void computeBottomMenu() {
        // we need to the same for picasa albums
        ArrayList<MediaBucket> selection = mGridLayer.getSelectedBuckets();
        Menu[] menus = mSelectionMenuBottom.getMenus();
        if (menus == mSingleViewIntentBottomMenu)
            return;
        int numBuckets = selection.size();
        for (int i = 0; i < numBuckets; ++i) {
            MediaBucket bucket = selection.get(i);
            if (bucket == null || bucket.mediaSet == null)
                continue;
            if (bucket.mediaSet.mPicasaAlbumId != Shared.INVALID) {
                mSelectionMenuBottom.setMenus(mSingleViewIntentBottomMenu);
                break;
            }
        }
    }

    public Layer getMenuBar() {
        return mFullscreenMenu;
    }

    public void hideZoomButtons(boolean hide) {
        mZoomInButton.setHidden(hide);
        mZoomOutButton.setHidden(hide);
    }
}
