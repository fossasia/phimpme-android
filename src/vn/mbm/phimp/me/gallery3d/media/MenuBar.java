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

import java.util.HashMap;

import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import android.content.Context;
import android.view.MotionEvent;

public final class MenuBar extends Layer implements PopupMenu.Listener {
    public static final int HEIGHT = 45;

    public static final StringTexture.Config MENU_TITLE_STYLE_TEXT = new StringTexture.Config();
    private static final StringTexture.Config MENU_TITLE_STYLE = new StringTexture.Config();
    private static final int MENU_HIGHLIGHT_EDGE_WIDTH = 21;
    private static final int MENU_HIGHLIGHT_EDGE_INSET = 9;
    private static final long LONG_PRESS_THRESHOLD_MS = 350;
    private static final int HIT_TEST_MARGIN = 15;

    static {
        MENU_TITLE_STYLE.fontSize = 17 * App.PIXEL_DENSITY;
        MENU_TITLE_STYLE.sizeMode = StringTexture.Config.SIZE_EXACT;
        MENU_TITLE_STYLE.overflowMode = StringTexture.Config.OVERFLOW_FADE;

        MENU_TITLE_STYLE_TEXT.fontSize = 15 * App.PIXEL_DENSITY;
        MENU_TITLE_STYLE_TEXT.xalignment = StringTexture.Config.ALIGN_HCENTER;
        MENU_TITLE_STYLE_TEXT.sizeMode = StringTexture.Config.SIZE_EXACT;
        MENU_TITLE_STYLE_TEXT.overflowMode = StringTexture.Config.OVERFLOW_FADE;
    }

    private boolean mNeedsLayout = false;
    private Menu[] mMenus = {};
    private int mTouchMenu = -1;
    private int mTouchMenuItem = -1;
    private boolean mTouchActive = false;
    private boolean mTouchOverMenu = false;
    private final PopupMenu mSubmenu;
    @SuppressWarnings("static-access")
	private static final int BACKGROUND = Res.drawable.selection_menu_bg;
    @SuppressWarnings("static-access")
	private static final int SEPERATOR = Res.drawable.selection_menu_divider;
    @SuppressWarnings("static-access")
	private static final int MENU_HIGHLIGHT_LEFT = Res.drawable.selection_menu_bg_pressed_left;
    @SuppressWarnings("static-access")
	private static final int MENU_HIGHLIGHT_MIDDLE = Res.drawable.selection_menu_bg_pressed;
    @SuppressWarnings("static-access")
	private static final int MENU_HIGHLIGHT_RIGHT = Res.drawable.selection_menu_bg_pressed_right;
    private final HashMap<String, Texture> mTextureMap = new HashMap<String, Texture>();
    private GL11 mGL;

    private boolean mSecondTouch;

    public MenuBar(Context context) {
        mSubmenu = new PopupMenu(context);
        mSubmenu.setListener(this);
    }

    public Menu[] getMenus() {
        return mMenus;
    }

    public void setMenus(Menu[] menus) {
        mMenus = menus;
        mNeedsLayout = true;
    }

    public void updateMenu(Menu menu, int index) {
        mMenus[index] = menu;
        mNeedsLayout = true;
    }

    @Override
    protected void onHiddenChanged() {
        if (mHidden) {
            mSubmenu.close(false);
        }
    }

    @Override
    protected void onSizeChanged() {
        mNeedsLayout = true;
    }

    @Override
    public void generate(RenderView view, RenderView.Lists lists) {
        lists.blendedList.add(this);
        lists.hitTestList.add(this);
        lists.systemList.add(this);
        lists.updateList.add(this);
        mSubmenu.generate(view, lists);
    }

    @Override
    public void renderBlended(RenderView view, GL11 gl) {
        // Layout if needed.
        if (mNeedsLayout) {
            layoutMenus();
            mNeedsLayout = false;
        }
        if (mGL != gl) {
            mTextureMap.clear();
            mGL = gl;
        }

        // Draw the background.
        Texture background = view.getResource(BACKGROUND);
        int backgroundHeight = background.getHeight();
        int menuHeight = (int) (HEIGHT * App.PIXEL_DENSITY + 0.5f);
        int extra = background.getHeight() - menuHeight;
        view.draw2D(background, mX, mY - extra, mWidth, backgroundHeight);

        // Draw the separators.
        Menu[] menus = mMenus;
        int numMenus = menus.length;
        int y = (int) mY;
        if (view.bind(view.getResource(SEPERATOR))) {
            for (int i = 1; i < numMenus; ++i) {
                view.draw2D(menus[i].x, y, 0, 1, menuHeight);
            }
        }

        // Draw the selection / focus highlight.
        int touchMenu = mTouchMenu;
        if (canDrawHighlight()) {
            drawHighlight(view, gl, touchMenu);
        }

        // Draw labels.
        float height = mHeight;
        for (int i = 0; i != numMenus; ++i) {
            // Draw the icon and title.
            Menu menu = menus[i];
            ResourceTexture icon = view.getResource(menu.icon);

            StringTexture titleTexture = (StringTexture) mTextureMap.get(menu.title);
            if (titleTexture == null) {
                titleTexture = new StringTexture(menu.title, menu.config, menu.titleWidth, MENU_TITLE_STYLE.height);
                view.loadTexture(titleTexture);
                menu.titleTexture = titleTexture;
                mTextureMap.put(menu.title, titleTexture);
            }
            int iconWidth = icon != null ? icon.getWidth() : 0;
            int width = iconWidth + menu.titleWidth;
            int offset = (menu.mWidth - width) / 2;
            if (icon != null) {
                float iconY = y + (height - icon.getHeight()) / 2;
                view.draw2D(icon, menu.x + offset, iconY);
            }
            float titleY = y + (height - MENU_TITLE_STYLE.height) / 2 + 1;
            view.draw2D(titleTexture, menu.x + offset + iconWidth, titleY);
        }
    }

    private void drawHighlight(RenderView view, GL11 gl, int touchMenu) {
        Texture highlightLeft = view.getResource(MENU_HIGHLIGHT_LEFT);
        Texture highlightMiddle = view.getResource(MENU_HIGHLIGHT_MIDDLE);
        Texture highlightRight = view.getResource(MENU_HIGHLIGHT_RIGHT);

        int height = highlightLeft.getHeight();
        int extra = height - (int) (HEIGHT * App.PIXEL_DENSITY);
        Menu menu = mMenus[touchMenu];
        int x = menu.x + (int) (MENU_HIGHLIGHT_EDGE_INSET * App.PIXEL_DENSITY);
        int width = menu.mWidth - (int) ((MENU_HIGHLIGHT_EDGE_INSET * 2) * App.PIXEL_DENSITY);
        int y = (int) mY - extra;

        // Draw left edge.
        view.draw2D(highlightLeft, x - MENU_HIGHLIGHT_EDGE_WIDTH * App.PIXEL_DENSITY, y, MENU_HIGHLIGHT_EDGE_WIDTH
                * App.PIXEL_DENSITY, height);

        // Draw middle.
        view.draw2D(highlightMiddle, x, y, width, height);

        // Draw right edge.
        view.draw2D(highlightRight, x + width, y, MENU_HIGHLIGHT_EDGE_WIDTH * App.PIXEL_DENSITY, height);
    }

    private int hitTestMenu(int x, int y) {
        if (y > mY - HIT_TEST_MARGIN * App.PIXEL_DENSITY) {
            Menu[] menus = mMenus;
            for (int i = menus.length - 1; i >= 0; --i) {
                if (x > menus[i].x) {
                    if (menus[i].onSelect != null || menus[i].options != null || menus[i].onSingleTapUp != null) {
                        return i;
                    } else {
                        return -1;
                    }
                }
            }
        }
        return -1;
    }

    private void selectMenu(int index) {
        int oldIndex = mTouchMenu;
        if (oldIndex != index) {
            // Notify on deselect.
            Menu[] menus = mMenus;
            if (oldIndex != -1) {
                Menu oldMenu = menus[oldIndex];
                if (oldMenu.onDeselect != null) {
                    oldMenu.onDeselect.run();
                }
            }

            // Select the new menu.
            mTouchMenu = index;
            mTouchMenuItem = -1;

            // Show the submenu for the selected menu if one is provided.
            PopupMenu submenu = mSubmenu;
            boolean didShow = false;
            if (index != -1) {
                // Notify on select.
                Menu menu = mMenus[index];
                if (menu.onSelect != null) {
                    menu.onSelect.run();
                }

                // Show the popup menu if options are provided.
                PopupMenu.Option[] options = menu.options;
                if (options != null) {
                    int x = (int) mX + menu.x + menu.mWidth / 2;
                    int y = (int) mY;
                    didShow = true;
                    submenu.setOptions(options);
                    submenu.showAtPoint(x, y, (int) mWidth, (int) mHeight);
                }
            }
            if (!didShow) {
                submenu.close(true);
            }
        }
    }

    public void close() {
        int oldIndex = mTouchMenu;
        if (oldIndex != -1) {
            // Notify on deselect.
            Menu[] menus = mMenus;
            if (oldIndex != -1) {
                Menu oldMenu = menus[oldIndex];
                if (oldMenu.onDeselect != null) {
                    oldMenu.onDeselect.run();
                }
            }
            oldIndex = -1;
        }
        selectMenu(-1);
        if (mSubmenu != null)
            mSubmenu.close(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int hit = hitTestMenu(x, y);
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mTouchActive = true;
            if (mTouchMenu == hit) {
                mSecondTouch = true;
            } else {
                mSecondTouch = false;
            }
        case MotionEvent.ACTION_MOVE:
            // Determine which menu the touch is over.
            if (hit != -1) {
                // Select the menu and invoke the action.
                selectMenu(hit);
                mTouchOverMenu = true;
            } else {
                // Forward events outside the menubar to the active popup menu.
                mTouchOverMenu = false;
            }
            mSubmenu.onTouchEvent(event);
            break;
        case MotionEvent.ACTION_UP:
            if (mTouchMenu == hit && mSecondTouch) {
                mSubmenu.close(true);
                mTouchMenu = -1;
                break;
            }
            // Forward event to submenu.
            mSubmenu.onTouchEvent(event);

            // Leave the submenu open if the touch ends on the menu button in
            // less than
            // a time threshold.
            long elapsed = event.getEventTime() - event.getDownTime();
            if (hit != -1) {
                // Notify on single tap.
                Menu menu = mMenus[hit];
                if (menu.onSingleTapUp != null) {
                    menu.onSingleTapUp.run();
                }
                if (menu.options == null)
                    selectMenu(-1);
            } else if (elapsed > LONG_PRESS_THRESHOLD_MS) {
                selectMenu(-1);
            }
            break;

        case MotionEvent.ACTION_CANCEL:
            // Always deselect if canceled.
            selectMenu(-1);
            break;
        }
        return true;
    }

    private boolean canDrawHighlight() {
        return mTouchMenu != -1 && mTouchMenuItem == -1 && (!mTouchActive || mTouchOverMenu);
    }

    private void layoutMenus() {
        mTextureMap.clear();

        Menu[] menus = mMenus;
        int numMenus = menus.length;
        // we do the best attempt to fit the menu items and resize them
        // also, it tries to minimize different sized menu items
        // it finds the maximum width for a set of menu items, and checks
        // whether that width
        // can be used for all the cells, else, it goes to the next maximum
        // width, so on and
        // so forth
        if (numMenus != 0) {
            float viewWidth = mWidth;
            int occupiedWidth = 0;
            int previousMaxWidth = Integer.MAX_VALUE;
            int totalDesiredWidth = 0;

            for (int i = 0; i < numMenus; i++) {
                totalDesiredWidth += menus[i].computeRequiredWidth();
            }

            if (totalDesiredWidth > viewWidth) {
                // Just split the menus up by available size / nr of menus.
                int widthPerMenu = (int) Math.floor(viewWidth / numMenus);
                int x = 0;

                for (int i = 0; i < numMenus; i++) {
                    Menu menu = menus[i];
                    menu.x = x;
                    menu.mWidth = widthPerMenu;
                    menu.titleWidth = widthPerMenu - (20 + (menu.icon != 0 ? 45 : 0)); // TODO
                                                                                       // factor
                                                                                       // out
                                                                                       // padding
                                                                                       // etc

                    // fix up rounding errors by adding the last pixel to the
                    // last menu.
                    if (i == numMenus - 1) {
                        menu.mWidth = (int) viewWidth - x;
                    }
                    x += widthPerMenu;

                }
            } else {
                boolean foundANewMaxWidth = true;
                int menusProcessed = 0;

                while (foundANewMaxWidth && menusProcessed < numMenus) {
                    foundANewMaxWidth = false;
                    int maxWidth = 0;
                    for (int i = 0; i < numMenus; ++i) {
                        int width = menus[i].computeRequiredWidth();
                        if (width > maxWidth && width < previousMaxWidth) {
                            foundANewMaxWidth = true;
                            maxWidth = width;
                        }
                    }
                    // can all the menus have this width
                    int cumulativeWidth = maxWidth * (numMenus - menusProcessed) + occupiedWidth;
                    if (cumulativeWidth < viewWidth || !foundANewMaxWidth || menusProcessed == numMenus - 1) {
                        float delta = (viewWidth - cumulativeWidth) / numMenus;
                        if (delta < 0) {
                            delta = 0;
                        }
                        int x = 0;
                        for (int i = 0; i < numMenus; ++i) {
                            Menu menu = menus[i];
                            menu.x = x;
                            float width = menus[i].computeRequiredWidth();
                            if (width < maxWidth) {
                                width = maxWidth + delta;
                            } else {
                                width += delta;
                            }
                            menu.mWidth = (int) width;
                            menu.titleWidth = StringTexture.computeTextWidthForConfig(menu.title, menu.config); // (int)menus[i].title.computeTextWidth();
                            x += width;
                        }
                        break;
                    } else {
                        ++menusProcessed;
                        previousMaxWidth = maxWidth;
                        occupiedWidth += maxWidth;
                    }
                }
            }
        }
    }

    public static final class Menu {
        public final String title;
        public StringTexture titleTexture = null;
        public int titleWidth = 0;
        public final StringTexture.Config config;
        public final int icon;
        public final Runnable onSelect;
        public final Runnable onDeselect;
        public final Runnable onSingleTapUp;
        public final boolean resizeToAccomodate;
        public PopupMenu.Option[] options;
        private int x;
        private int mWidth;
        private static final float ICON_WIDTH = 45.0f;

        public static final class Builder {
            private final String title;
            private StringTexture.Config config;
            private int icon = 0;
            private Runnable onSelect = null;
            private Runnable onDeselect = null;
            private Runnable onSingleTapUp = null;
            private PopupMenu.Option[] options = null;
            private boolean resizeToAccomodate;

            public Builder(String title) {
                this.title = title;
                config = MENU_TITLE_STYLE;
            }

            public Builder config(StringTexture.Config config) {
                this.config = config;
                return this;
            }

            public Builder resizeToAccomodate() {
                this.resizeToAccomodate = true;
                return this;
            }

            public Builder icon(int icon) {
                this.icon = icon;
                return this;
            }

            public Builder onSelect(Runnable onSelect) {
                this.onSelect = onSelect;
                return this;
            }

            public Builder onDeselect(Runnable onDeselect) {
                this.onDeselect = onDeselect;
                return this;
            }

            public Builder onSingleTapUp(Runnable onSingleTapUp) {
                this.onSingleTapUp = onSingleTapUp;
                return this;
            }

            public Builder options(PopupMenu.Option[] options) {
                this.options = options;
                return this;
            }

            public Menu build() {
                return new Menu(this);
            }
        }

        private Menu(Builder builder) {
            config = builder.config;
            title = builder.title; // new StringTexture(builder.title, config);
            icon = builder.icon;
            onSelect = builder.onSelect;
            onDeselect = builder.onDeselect;
            onSingleTapUp = builder.onSingleTapUp;
            options = builder.options;
            resizeToAccomodate = builder.resizeToAccomodate;
        }

        public int computeRequiredWidth() {
            int width = 0;
            if (icon != 0) {
                width += (ICON_WIDTH); // * App.PIXEL_DENSITY);
            }
            if (title != null) {
                width += StringTexture.computeTextWidthForConfig(title, config);// title.computeTextWidth();
            }
            // pad it
            width += 20;
            if (width < HEIGHT)
                width = HEIGHT;
            return width;
        }

    }

    public void onSelectionChanged(PopupMenu menu, int selectedIndex) {
        mTouchMenuItem = selectedIndex;
    }

    public void onSelectionClicked(PopupMenu menu, int selectedIndex) {
        selectMenu(-1);
    }
}
