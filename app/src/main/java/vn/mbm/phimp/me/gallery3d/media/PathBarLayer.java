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

import javax.microedition.khronos.opengles.GL11;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import android.graphics.Typeface;
import android.view.MotionEvent;

public final class PathBarLayer extends Layer {
    private static final StringTexture.Config sPathFormat = new StringTexture.Config();
    private final ArrayList<Component> mComponents = new ArrayList<Component>();
    @SuppressWarnings("static-access")
	private static final int FILL = Res.drawable.pathbar_bg;
    @SuppressWarnings("static-access")
	private static final int JOIN = Res.drawable.pathbar_join;
    @SuppressWarnings("static-access")
	private static final int CAP = Res.drawable.pathbar_cap;
    private Component mTouchItem = null;

    static {
        sPathFormat.fontSize = 18f * App.PIXEL_DENSITY;
    }

    public PathBarLayer() {
    }

    public void pushLabel(int icon, String label, Runnable action) {
        synchronized (mComponents) {
            mComponents.add(new Component(icon, label, action, 0));
        }
        recomputeComponents();
    }

    public void setAnimatedIcons(final int[] icons) {
        synchronized (mComponents) {
            final int numComponents = mComponents.size();
            for (int i = 0; i < numComponents; ++i) {
                final Component component = mComponents.get(i);
                if (component != null) {
                    if (component.animatedIcons != null) {
                        component.animatedIcons = null;
                    }
                    if (i == numComponents - 1) {
                        component.animatedIcons = icons;
                    }
                }
            }
        }
    }

    public void changeLabel(String label) {
        if (label == null || label.length() == 0)
            return;
        Component component = popLabel();
        if (component != null) {
            pushLabel(component.icon, label, component.action);
        }
    }

    public String getCurrentLabel() {
        final ArrayList<Component> components = mComponents;
        synchronized (components) {
            int lastIndex = components.size() - 1;
            if (lastIndex < 0) {
                return "";
            }
            Component retVal = components.get(lastIndex);
            return retVal.origString;
        }
    }

    public Component popLabel() {
        final ArrayList<Component> components = mComponents;
        synchronized (components) {
            int lastIndex = components.size() - 1;
            if (lastIndex < 0) {
                return null;
            }
            Component retVal = components.get(lastIndex);
            components.remove(lastIndex);
            return retVal;
        }
    }

    private static final class Component {
        public String origString;
        public int icon;
        public Runnable action;
        public StringTexture texture;
        public float width;
        public float animWidth;
        public float x;
        public int[] animatedIcons;
        public float timeElapsed;
        private static final float ICON_WIDTH = 38.0f;

        Component(int icon, String label, Runnable action, float widthLeft) {
            this.action = action;
            origString = label;
            this.icon = icon;
            computeLabel(widthLeft);
        }

        public final void computeLabel(float widthLeft) {
            Typeface typeface = sPathFormat.bold ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT;
            String label = "";
            if (origString != null) {
                label = origString.substring(0, StringTexture.lengthToFit(sPathFormat.fontSize, widthLeft, typeface, origString));
                if (label.length() != origString.length()) {
                    label += "...";
                }
            }
            this.texture = new StringTexture(label, sPathFormat);
        }

        public final boolean update(float timeElapsed) {
            this.timeElapsed += timeElapsed;
            if (animWidth == 0.0f) {
                animWidth = width;
            }
            animWidth = FloatUtils.animate(animWidth, width, timeElapsed);
            if (animatedIcons != null && animatedIcons.length > 1)
                return true;
            if (animWidth == width) {
                return false;
            } else {
                return true;
            }
        }

        public float getIconWidth() {
            return ICON_WIDTH * App.PIXEL_DENSITY;
        }
    }

    @Override
    public void generate(RenderView view, RenderView.Lists lists) {
        lists.blendedList.add(this);
        lists.hitTestList.add(this);
        lists.updateList.add(this);
    }

    private void layout() {
        synchronized (mComponents) {
            int numComponents = mComponents.size();
            for (int i = 0; i < numComponents; ++i) {
                Component component = mComponents.get(i);
                if (component == null)
                    continue;
                float iconWidth = (component.icon == 0) ? 0 : component.getIconWidth();
                if (iconWidth == 0) {
                    iconWidth = 8 * App.PIXEL_DENSITY;
                }
                float offset = 5 * App.PIXEL_DENSITY;
                float thisComponentWidth = (i != numComponents - 1) ? iconWidth + offset : component.texture.computeTextWidth()
                        + iconWidth + offset;
                component.width = thisComponentWidth;
            }
        }
    }

    @Override
    public boolean update(RenderView view, float timeElapsed) {
        layout();
        boolean retVal = false;
        synchronized (mComponents) {
            int numComponents = mComponents.size();
            for (int i = 0; i < numComponents; i++) {
                Component component = mComponents.get(i);
                retVal |= component.update(timeElapsed);
            }
        }
        return retVal;
    }

    @Override
    public void renderBlended(RenderView view, GL11 gl) {
        // Draw components.
        final Texture fill = view.getResource(FILL);
        final Texture join = view.getResource(JOIN);
        final Texture cap = view.getResource(CAP);
        final float y = mY + 3;
        int x = (int) (3 * App.PIXEL_DENSITY);
        float height = mHeight;
        synchronized (mComponents) {
            int numComponents = mComponents.size();
            for (int i = 0; i < numComponents; ++i) {
                Component component = mComponents.get(i);
                component.x = x;
                // Draw the left join if not the first component, and the fill.
                // TODO: Draw the pressed background for mTouchItem.
                final int width = (int) component.animWidth;
                if (i != 0) {
                    view.draw2D(join, x - join.getWidth(), y);
                    if (view.bind(fill)) {
                        view.draw2D(x, y, 0f, width, height);
                    }
                } else if (view.bind(fill)) {
                    view.draw2D(0f, y, 0f, x + width, height);
                }

                if (i == numComponents - 1) {
                    // Draw the cap on the right edge.
                    view.draw2D(cap, x + width, y);
                }
                float xOffset = 5 * App.PIXEL_DENSITY;
                // Draw the label.
                final int[] icons = component.animatedIcons;

                // Cycles animated icons.
                final int iconId = (icons != null && icons.length > 0) ? icons[(int) (component.timeElapsed * 20.0f) % icons.length]
                        : component.icon;
                final Texture icon = view.getResource(iconId);
                if (icon != null) {
                    view.loadTexture(icon);
                    view.draw2D(icon, x + xOffset, y - 2 * App.PIXEL_DENSITY);
                }
                if (i == numComponents - 1) {
                    final StringTexture texture = component.texture;
                    view.loadTexture(texture);
                    float iconWidth = component.getIconWidth();
                    if (texture.computeTextWidth() <= (width - iconWidth)) {
                        float textOffset = (iconWidth == 0) ? 8 * App.PIXEL_DENSITY : iconWidth;
                        view.draw2D(texture, x + textOffset, y + 5);
                    }
                }
                x += (int) (width + (21 * App.PIXEL_DENSITY + 0.5f));
            }
        }
    }

    private Component hitTestItems(float x, float y) {
        if (y >= mY && y < mY + mHeight) {
            synchronized (mComponents) {
                int numComponents = mComponents.size();
                for (int i = 0; i < numComponents; i++) {
                    final Component component = mComponents.get(i);
                    float componentx = component.x;
                    if (x >= componentx && x < componentx + component.width) {
                        return component;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mTouchItem = hitTestItems(x, y);
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
            if (mTouchItem != null) {
                mTouchItem.action.run();
            }
        case MotionEvent.ACTION_CANCEL:
            mTouchItem = null;
            break;
        }
        return true;
    }

    public void recomputeComponents() {
        float width = mWidth;
        width -= 20f * App.PIXEL_DENSITY;
        synchronized (mComponents) {
            int numComponents = mComponents.size();
            for (int i = 0; i < numComponents; i++) {
                Component component = mComponents.get(i);
                width -= (component.getIconWidth() + 20.0f * App.PIXEL_DENSITY);
                component.computeLabel(width);
            }
        }
    }

    public int getNumLevels() {
        synchronized (mComponents) {
            return mComponents.size();
        }
    }

    public void clear() {
        synchronized (mComponents) {
            mComponents.clear();
        }
    }
}
