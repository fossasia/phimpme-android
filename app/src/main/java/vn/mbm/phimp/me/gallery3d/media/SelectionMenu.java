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

//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.microedition.khronos.opengles.GL11;
//
//import android.content.ActivityNotFoundException;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.net.Uri;
//import android.util.FloatMath;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.widget.Toast;
//
//public final class SelectionMenu extends Layer {
//    public static final int HEIGHT = 58;
//
//    private static final int BUTTON_SHARE = 0;
//    private static final int BUTTON_DELETE = 1;
//    private static final int BUTTON_MORE = 2;
//    private static final int BUTTON_SELECT_ALL = 3;
//    private static final int BUTTON_DESELECT_ALL = 4;
//
//    private static final int[] BUTTON_LABELS = { Res.string.Share, Res.string.Delete,
//                                                Res.string.More };
//    private static final int[] BUTTON_ICONS = { Res.drawable.icon_share,
//                                               Res.drawable.icon_delete, Res.drawable.icon_more };
//    private static final float BUTTON_ICON_SIZE = 34f;
//
//    protected static final String TAG = "SelectionMenu";
//
//    private final Context mContext;
//    private final HudLayer mHud;
//    private final Texture mLowerBackground = ResourceTexture.get(Res.drawable.selection_lower_bg);
//    private final ResourceTexture mDividerImage = ResourceTexture
//                    .get(Res.drawable.selection_menu_divider);
//    private final ResourceTexture mSelectionLeft = ResourceTexture
//                    .get(Res.drawable.selection_menu_bg_pressed_left);
//    private final ResourceTexture mSelectionFill = ResourceTexture
//                    .get(Res.drawable.selection_menu_bg_pressed);
//    private final ResourceTexture mSelectionRight = ResourceTexture
//                    .get(Res.drawable.selection_menu_bg_pressed_right);
//    private final Button[] mButtons = new Button[BUTTON_LABELS.length];
//    private float mDividerSpacing = 0f;
//    private boolean mDrawButtonIcons = true;
//    private int mTouchButton = -1;
//    private boolean mTouchOver = false;
//    private final PopupMenu mPopupMenu;
//
//    SelectionMenu(HudLayer hud, Context context) {
//        mContext = context;
//        mHud = hud;
//        mPopupMenu = new PopupMenu(context);
//
//        // Prepare format for the small labels.
//        StringTexture.Config smallLabelFormat = new StringTexture.Config();
//        smallLabelFormat.fontSize = 14;
//
//        // Prepare format for the large labels.
//        StringTexture.Config largeLabelFormat = new StringTexture.Config();
//        largeLabelFormat.fontSize = 20f;
//
//        // Create icon and label textures.
//        for (int i = 0; i < BUTTON_LABELS.length; ++i) {
//            Button button = new Button();
//            button.icon = ResourceTexture.get(BUTTON_ICONS[i]);
//            button.smallLabel = new SimpleStringTexture(context.getString(BUTTON_LABELS[i]),
//                                                        smallLabelFormat);
//            button.largeLabel = new SimpleStringTexture(context.getString(BUTTON_LABELS[i]),
//                                                        largeLabelFormat);
//            mButtons[i] = button;
//        }
//    }
//
//    private void layout() {
//        // Perform layout with icons and text labels.
//        final Button[] buttons = mButtons;
//        final float width = mWidth;
//        final float buttonWidth = width / buttons.length;
//        float dx = 0f;
//        boolean overflow = false;
//
//        mDividerSpacing = buttonWidth;
//
//        // Attempt layout with icons + labels.
//        for (int i = 0; i != buttons.length; ++i) {
//            final Button button = buttons[i];
//            final float labelWidth = button.largeLabel.getWidth();
//            final float iconLabelWidth = BUTTON_ICON_SIZE + labelWidth;
//            if (iconLabelWidth > buttonWidth) {
//                overflow = true;
//                break;
//            }
//            button.x = dx + FloatMath.floor(0.5f * (buttonWidth - iconLabelWidth));
//            button.centerX = dx + FloatMath.floor(0.5f * buttonWidth);
//            dx += buttonWidth;
//        }
//
//        /*
//        // In the case of overflow layout without icons.
//        if (overflow) {
//            dx = 0f;
//            for (int i = 0; i != buttons.length; ++i) {
//                final Button button = buttons[i];
//                final float labelWidth = button.largeLabel.getWidth();
//                button.x = dx + FloatMath.floor(0.5f * (buttonWidth - labelWidth));
//                dx += buttonWidth;
//            }
//        }
//        mDrawButtonIcons = !overflow;*/
//        mDrawButtonIcons = true;
//        
//        // Layout the popup menu.
//        mPopupMenu.setSize(230, 200);
//    }
//
//    @Override
//    public void renderBlended(RenderView view, GL11 gl) {
//        // TODO: only recompute layout when things have changed.
//        layout();
//
//        if (view.bind(mLowerBackground)) {
//            final float imageHeight = mLowerBackground.getHeight();
//            view.draw2D(0f, mY, 0, mWidth, imageHeight);
//        }
//
//        // Draw the selection highlight if needed.
//        if (mTouchOver) {
//            final float SELECTION_FILL_INSET = 10f;
//            final float SELECTION_CAP_WIDTH = 21f;
//            final int touchButton = mTouchButton;
//            final float x = mDividerSpacing * touchButton + SELECTION_FILL_INSET;
//            final float y = mY;
//            final float insetWidth = mDividerSpacing + 1f - SELECTION_FILL_INSET -
//                                     SELECTION_FILL_INSET;
//            final float height = mSelectionFill.getHeight();
//            if (view.bind(mSelectionLeft)) {
//                view.draw2D(x - SELECTION_CAP_WIDTH, y, 0f, SELECTION_CAP_WIDTH, height);
//            }
//            if (view.bind(mSelectionFill)) {
//                view.draw2D(x, y, 0f, insetWidth, height);
//            }
//            if (view.bind(mSelectionRight)) {
//                view.draw2D(x + insetWidth, y, 0f, SELECTION_CAP_WIDTH, height);
//            }
//        }
//
//        // Draw the button icon + labels.
//        final float labelY = mY + 20f;
//        final Button[] buttons = mButtons;
//        for (int i = 0; i < buttons.length; ++i) {
//            final Button button = buttons[i];
//            float x = button.x;
//            if (mDrawButtonIcons && view.bind(button.icon)) {
//                view.draw2D(x, labelY, 0f, BUTTON_ICON_SIZE, BUTTON_ICON_SIZE);
//                x += BUTTON_ICON_SIZE - 2f;
//            }
//            if (view.bind(button.largeLabel)) {
//                view.draw2D(x, labelY, 0f, button.largeLabel.getWidth(), button.largeLabel
//                                .getHeight());
//            }
//        }
//
//        // Draw the inter-button dividers.
//        final float dividerY = mY + 13f;
//        float x = mDividerSpacing;
//        if (view.bind(mDividerImage)) {
//            for (int i = 1; i < buttons.length; ++i) {
//                view.draw2D(x, dividerY, 0f, 1f, 60f);
//                x += mDividerSpacing;
//            }
//        }
//    }
//
//    private int hitTestButtons(float x) {
//        final Button[] buttons = mButtons;
//        for (int i = buttons.length - 1; i >= 0; --i) {
//            if (buttons[i].x < x) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        final float x = event.getX();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mTouchButton = hitTestButtons(x);
//                mTouchOver = mTouchButton != -1;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mTouchOver = hitTestButtons(x) == mTouchButton;
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                performAction(mTouchButton, event.getX(), event.getY());
//                mTouchButton = -1;
//                mTouchOver = false;
//                break;
//        }
//        return true;
//    }
//
//    private void performAction(final int button, float touchX, float touchY) {
//        // Gather the selection.
//        ArrayList<MediaBucket> selection = mHud.getGridLayer().getSelectedBuckets();
//
//        // Get the first item from the selection. TODO: support multiple items in Eclair.
//        MediaItem item = null;
//        if (!selection.isEmpty()) {
//            MediaBucket bucket = selection.get(0);
//            if (bucket.mediaItems != null && !bucket.mediaItems.isEmpty()) {
//                item = bucket.mediaItems.get(0);
//            }
//        }
//
//        // Perform the action on the selection.
//        if (item != null) {
//            Gallery gallery = (Gallery)mContext; // TODO: provide better context.
//            final GridLayer gridLayer = mHud.getGridLayer();
//            final MediaItem sharedItem = item;
//            if (sharedItem.mimetype == null) {
//                Toast.makeText(mContext, "Error: mime type is null", Toast.LENGTH_SHORT).show();
//            }
//            if (sharedItem.contentUri == null) {
//                Toast.makeText(mContext, "Error: content URI is null", Toast.LENGTH_SHORT).show();
//            }
//            if (button == BUTTON_SELECT_ALL) {
//                gridLayer.selectAll();
//            } else if (button == BUTTON_DESELECT_ALL) {
//                mHud.cancelSelection();
//            } else {
//                // Get the target button and popup focus location.
//                Button targetButton = mButtons[button];
//                int popupX = (int)targetButton.centerX;
//                int popupY = (int)mY;
//                
//                // Configure the popup depending on the button pressed.
//                PopupMenu menu = mPopupMenu;
//                menu.clear();
//                switch (button) {
//                    case BUTTON_SHARE:
//                        /*
//                        PackageManager packageManager = mContext.getPackageManager();
//                        List<ResolveInfo> activities = packageManager.queryIntentActivities(
//                                                               intent,
//                                                               PackageManager.MATCH_DEFAULT_ONLY);
//                        for (ResolveInfo activity: activities) {
//                            activity.icon
//                            PopupMenu.Option option = new PopupMenu.Option(activit, name, action)
//                        }
//                        menu.add(new PopupMenu.Option());*/
//                        mHud.cancelSelection();
//                        break;
//                    case BUTTON_DELETE:
//                        
//                    case BUTTON_MORE:
//                }
//                
//                if (button != BUTTON_SHARE) {
//                    mPopupMenu.showAtPoint(popupX, popupY, (int)mHud.getWidth(), (int)mHud.getHeight());
//                }
//                
//                gallery.getHandler().post(new Runnable() {
//                    public void run() {
//                        switch (button) {
//                            case BUTTON_SHARE:
//                                Intent intent = new Intent();
//                                intent.setAction(Intent.ACTION_SEND);
//                                intent.setType(sharedItem.mimetype);
//                                intent.putExtra(Intent.EXTRA_STREAM, Uri
//                                                .parse(sharedItem.contentUri));
//                                try {
//                                    mContext.startActivity(Intent.createChooser(intent,
//                                                                                "Share via"));
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(mContext, "Unable to share",
//                                                   Toast.LENGTH_SHORT).show();
//                                }
//
//                                break;
//                            case BUTTON_DELETE:
//                                gridLayer.deleteSelection();
//                            case BUTTON_MORE:
//                                // Show options for Set As, Details, Slideshow.
//                            default:
//                                break;
//                        }
//                    }
//                });
//            }
//        }
//
//        // Exit selection mode.
//        //mHud.cancelSelection();
//    }
//
//    @Override
//    public void generate(RenderView view, RenderView.Lists lists) {
//        lists.blendedList.add(this);
//        lists.hitTestList.add(this);
//        mPopupMenu.generate(view, lists);
//    }
//    
//    @Override
//    protected void onHiddenChanged() {
//        if (mHidden) {
//            mPopupMenu.close();
//        }
//    }
//
//    private static final class Button {
//        public Texture icon;
//        public SimpleStringTexture smallLabel;
//        public SimpleStringTexture largeLabel;
//        public float x;
//        public float centerX;
//    }
// }
