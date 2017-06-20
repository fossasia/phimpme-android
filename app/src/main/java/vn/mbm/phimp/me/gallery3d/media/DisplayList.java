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
import java.util.HashMap;


// CR: comment.
public final class DisplayList {
    private DirectLinkedList<DisplayItem> mAnimatables = new DirectLinkedList<DisplayItem>();
    private HashMap<MediaItem, DisplayItem> mDisplayMap = new HashMap<MediaItem, DisplayItem>(1024);
    private ArrayList<DisplayItem> mItems = new ArrayList<DisplayItem>(1024);

    public DisplayItem get(MediaItem item) {
        HashMap<MediaItem, DisplayItem> displayMap = mDisplayMap;
        DisplayItem displayItem = displayMap.get(item);
        if (displayItem == null) {
            displayItem = new DisplayItem(item);
            displayMap.put(item, displayItem);
            mItems.add(displayItem);
        }
        return displayItem;
    }

    public void setPositionAndStackIndex(DisplayItem item, Vector3f position, int stackId, boolean performTransition) {
        item.set(position, stackId, performTransition);
        if (!performTransition) {
            item.commit();
        } else {
            markIfDirty(item);
        }
    }

    public void setHasFocus(DisplayItem item, boolean hasFocus, boolean pushDown) {
        boolean currentHasFocus = item.getHasFocus();
        if (currentHasFocus != hasFocus) {
            item.setHasFocus(hasFocus, pushDown);
            markIfDirty(item);
        }
    }
    
    public final void setOffset(DisplayItem item, boolean useOffset, boolean pushDown, float span, float dx1, float dy1, float dx2, float dy2) {
        item.setOffset(useOffset, pushDown, span, dx1, dy1, dx2, dy2);
        markIfDirty(item);
    }
    
    public final void setSingleOffset(DisplayItem item, boolean useOffset, boolean pushAway, float x, float y, float z, float spreadValue) {
        item.setSingleOffset(useOffset, pushAway, x, y, z, spreadValue);
        markIfDirty(item);
    }

    public ArrayList<DisplayItem> getAllDisplayItems() {
        return mItems;
    }

    public void update(float timeElapsed) {
        final DirectLinkedList<DisplayItem> animatables = mAnimatables;
        synchronized (animatables) {
            DirectLinkedList.Entry<DisplayItem> entry = animatables.getHead();
            while (entry != null) {
                DisplayItem item = entry.value;
                item.update(timeElapsed);
                if (!item.isAnimating()) {
                    entry = animatables.remove(entry);
                } else {
                    entry = entry.next;
                }
            }
        }
    }

    public int getNumAnimatables() {
        return mAnimatables.size();
    }

    public void setAlive(DisplayItem item, boolean alive) {
        item.mAlive = alive;
        if (alive && item.isAnimating()) {
            final DirectLinkedList.Entry<DisplayItem> entry = item.getAnimatablesEntry();
            if (!entry.inserted) {
                mAnimatables.add(entry);
            }
        }
    }

    public void commit(DisplayItem item) {
        item.commit();
        final DirectLinkedList<DisplayItem> animatables = mAnimatables;
        synchronized (animatables) {
            animatables.remove(item.getAnimatablesEntry());
        }
    }

    public void addToAnimatables(DisplayItem item) {
        final DirectLinkedList.Entry<DisplayItem> entry = item.getAnimatablesEntry();
        if (!entry.inserted) {
            final DirectLinkedList<DisplayItem> animatables = mAnimatables;
            synchronized (animatables) {
                animatables.add(entry);
            }
        }
    }

    private void markIfDirty(DisplayItem item) {
        if (item.isAnimating()) {
            addToAnimatables(item);
        }
    }

    public void clear() {
        mDisplayMap.clear();
        synchronized (mItems) {
            mItems.clear();
        }
    }

    public void clearExcept(DisplayItem[] displayItems) {
        HashMap<MediaItem, DisplayItem> displayMap = mDisplayMap;
        displayMap.clear();
        synchronized (mItems) {
            mItems.clear();
            int numItems = displayItems.length;
            for (int i = 0; i < numItems; ++i) {
                DisplayItem displayItem = displayItems[i];
                if (displayItem != null) {
                    displayMap.put(displayItem.mItemRef, displayItem);
                    mItems.add(displayItem);
                }
            }
        }
    }
}
