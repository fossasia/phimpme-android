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

public final class MediaBucketList {
    private static final Boolean TRUE = new Boolean(true);
    private static final Boolean FALSE = new Boolean(false);

    private ArrayList<MediaBucket> mBuckets = new ArrayList<MediaBucket>(1024);
    private boolean mDirtyCount;
    private boolean mDirtyAcceleratedLookup;
    private int mCount;
    private HashMap<MediaItem, Boolean> mCachedItems = new HashMap<MediaItem, Boolean>(1024);

    // If only albums are selected, a bucket contains mediaSets.
    // If items are selected, a bucket contains mediaSets and mediaItems.

    // Returns the first item selection (ignoring items within set selections).
    public static MediaItem getFirstItemSelection(ArrayList<MediaBucket> buckets) {
        MediaItem item = null;
        if (buckets != null) {
            int numBuckets = buckets.size();
            for (int i = 0; i < numBuckets; i++) {
                MediaBucket bucket = buckets.get(0);
                if (bucket != null && !isSetSelection(bucket)) {
                    ArrayList<MediaItem> items = bucket.mediaItems;
                    if (items != null && items.size() > 0) {
                        item = items.get(0);
                        break;
                    }
                }
            }
        }
        return item;
    }

    // Returns the first set selection (ignoring sets corresponding to item
    // selections).
    public static MediaSet getFirstSetSelection(ArrayList<MediaBucket> buckets) {
        MediaSet set = null;
        if (buckets != null) {
            int numBuckets = buckets.size();
            for (int i = 0; i < numBuckets; i++) {
                MediaBucket bucket = buckets.get(0);
                if (bucket != null && isSetSelection(bucket)) {
                    set = bucket.mediaSet;
                }
            }
        }
        return set;
    }

    public ArrayList<MediaBucket> get() {
        return mBuckets;
    }

    public int size() {
        if (mDirtyCount) {
            ArrayList<MediaBucket> buckets = mBuckets;
            int numBuckets = buckets.size();
            int count = 0;
            for (int i = 0; i < numBuckets; ++i) {
                MediaBucket bucket = buckets.get(i);
                int numItems = 0;
                if (bucket.mediaItems == null && bucket.mediaSet != null) {
                    numItems = bucket.mediaSet.getNumItems();
                    // This selection reflects the bucket itself, and not the
                    // items inside the bucket (which is 0).
                    if (numItems == 0) {
                        numItems = 1;
                    }
                } else if (bucket.mediaItems != null && bucket.mediaItems != null) {
                    numItems = bucket.mediaItems.size();
                }
                count += numItems;
            }
            mCount = count;
            mDirtyCount = false;
        }
        return mCount;
    }

    public void add(int slotId, MediaFeed feed, boolean removeIfAlreadyAdded) {
        if (slotId == Shared.INVALID) {
            return;
        }
        setDirty();
        final ArrayList<MediaBucket> selectedBuckets = mBuckets;
        final int numSelectedBuckets = selectedBuckets.size();
        MediaSet mediaSetToAdd = null;
        ArrayList<MediaItem> selectedItems = null;
        MediaBucket bucket = null;
        final boolean hasExpandedMediaSet = feed.hasExpandedMediaSet();
        if (!hasExpandedMediaSet) {
            ArrayList<MediaSet> mediaSets = feed.getMediaSets();
            if (slotId >= mediaSets.size()) {
                return;
            }
            mediaSetToAdd = mediaSets.get(slotId);
        } else {
            int numSlots = feed.getNumSlots();
            if (slotId < numSlots) {
                MediaSet set = feed.getSetForSlot(slotId);
                if (set != null) {
                    ArrayList<MediaItem> items = set.getItems();
                    if (set.getNumItems() > 0) {
                        mediaSetToAdd = items.get(0).mParentMediaSet;
                    }
                }
            }
        }

        // Search for the bucket for this media set
        for (int i = 0; i < numSelectedBuckets; ++i) {
            final MediaBucket bucketCompare = selectedBuckets.get(i);
            if (bucketCompare.mediaSet != null && mediaSetToAdd != null && bucketCompare.mediaSet.mId == mediaSetToAdd.mId) {
                // We found the MediaSet.
                if (!hasExpandedMediaSet) {
                    // Remove this bucket from the list since this bucket was
                    // already selected.
                    if (removeIfAlreadyAdded) {
                        selectedBuckets.remove(bucketCompare);
                    }
                    return;
                } else {
                    bucket = bucketCompare;
                    break;
                }
            }
        }
        if (bucket == null) {
            // Did not find the media bucket.
            bucket = new MediaBucket();
            bucket.mediaSet = mediaSetToAdd;
            bucket.mediaItems = selectedItems;
            selectedBuckets.add(bucket);
        }
        if (hasExpandedMediaSet) {
            int numSlots = feed.getNumSlots();
            if (slotId < numSlots) {
                MediaSet set = feed.getSetForSlot(slotId);
                if (set != null) {
                    ArrayList<MediaItem> items = set.getItems();
                    int numItems = set.getNumItems();
                    selectedItems = bucket.mediaItems;
                    if (selectedItems == null) {
                        selectedItems = new ArrayList<MediaItem>(numItems);
                        bucket.mediaItems = selectedItems;
                    }
                    for (int i = 0; i < numItems; ++i) {
                        MediaItem item = items.get(i);
                        // We see if this item has already been added.
                        int numPresentItems = selectedItems.size();
                        boolean foundIndex = false;
                        for (int j = 0; j < numPresentItems; ++j) {
                            final MediaItem selectedItem = selectedItems.get(j);
                            if (selectedItem != null && item != null && selectedItem.mId == item.mId) {
                                // This index was already present, we need to
                                // remove it.
                                foundIndex = true;
                                if (removeIfAlreadyAdded) {
                                    selectedItems.remove(j);
                                }
                                break;
                            }
                        }
                        if (foundIndex == false) {
                            selectedItems.add(item);
                        }
                    }
                }
            }
        }
        setDirty();
    }

    public boolean find(MediaItem item) {
        HashMap<MediaItem, Boolean> cachedItems = mCachedItems;
        if (mDirtyAcceleratedLookup) {
            cachedItems.clear();
            mDirtyAcceleratedLookup = false;
        }
        Boolean itemAdded = cachedItems.get(item);
        if (itemAdded == null) {
            ArrayList<MediaBucket> selectedBuckets = mBuckets;
            int numSelectedBuckets = selectedBuckets.size();
            for (int i = 0; i < numSelectedBuckets; ++i) {
                MediaBucket bucket = selectedBuckets.get(i);
                ArrayList<MediaItem> mediaItems = bucket.mediaItems;
                if (mediaItems == null) {
                    MediaSet parentMediaSet = item.mParentMediaSet;
                    if (parentMediaSet != null && parentMediaSet.equals(bucket.mediaSet)) {
                        cachedItems.put(item, TRUE);
                        return true;
                    }
                } else {
                    int numMediaItems = mediaItems.size();
                    for (int j = 0; j < numMediaItems; ++j) {
                        MediaItem itemCompare = mediaItems.get(j);
                        if (itemCompare == item) {
                            cachedItems.put(item, TRUE);
                            return true;
                        }
                    }
                }
            }
            cachedItems.put(item, FALSE);
            return false;
        } else {
            return itemAdded.booleanValue();
        }
    }

    public void clear() {
        mBuckets.clear();
        setDirty();
    }

    private void setDirty() {
        mDirtyCount = true;
        mDirtyAcceleratedLookup = true;
    }

    // Assumption: No item and set selection combinations.
    protected static boolean isSetSelection(ArrayList<MediaBucket> buckets) {
        if (buckets != null) {
            int numBuckets = buckets.size();
            if (numBuckets == 0) {
                return false;
            } else if (numBuckets == 1) {
                return isSetSelection(buckets.get(0));
            } else {
                // If there are multiple sets, must be a set selection.
                return true;
            }
        }
        return false;
    }

    protected static boolean isSetSelection(MediaBucket bucket) {
        return (bucket.mediaSet != null && bucket.mediaItems == null) ? true : false;
    }

    // Assumption: If multiple items are selected, they must all be in the first
    // bucket.
    protected static boolean isMultipleItemSelection(ArrayList<MediaBucket> buckets) {
        if (buckets != null) {
            int numBuckets = buckets.size();
            if (numBuckets == 0) {
                return false;
            } else {
                return isMultipleSetSelection(buckets.get(0));
            }
        }
        return false;
    }

    protected static boolean isMultipleSetSelection(MediaBucket bucket) {
        return (bucket.mediaItems != null && bucket.mediaItems.size() > 1) ? true : false;
    }
}
