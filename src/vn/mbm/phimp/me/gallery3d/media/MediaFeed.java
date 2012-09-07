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
import java.util.Set;

import vn.mbm.phimp.me.gallery3d.app.App;
import vn.mbm.phimp.me.gallery3d.app.Res;
import vn.mbm.phimp.me.gallery3d.media.MediaClustering.Cluster;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public final class MediaFeed implements Runnable {
    private final String TAG = "MediaFeed";
    public static final int OPERATION_DELETE = 0;
    public static final int OPERATION_ROTATE = 1;
    public static final int OPERATION_CROP = 2;

    private static final int NUM_ITEMS_LOOKAHEAD = 60;
    private static final int NUM_INTERRUPT_RETRIES = 30;
    private static final int JOIN_TIMEOUT = 50;

    private IndexRange mVisibleRange = new IndexRange();
    private IndexRange mBufferedRange = new IndexRange();
    private ArrayList<MediaSet> mMediaSets = new ArrayList<MediaSet>();
    private Listener mListener;
    private DataSource mDataSource;
    private boolean mListenerNeedsUpdate = false;
    private boolean mMediaFeedNeedsToRun = false;
    private MediaSet mSingleWrapper = new MediaSet();
    private boolean mInClusteringMode = false;
    private HashMap<MediaSet, MediaClustering> mClusterSets = new HashMap<MediaSet, MediaClustering>(32);
    private int mExpandedMediaSetIndex = Shared.INVALID;
    private MediaFilter mMediaFilter;
    private MediaSet mMediaFilteredSet;
    private Context mContext;
    private Thread mDataSourceThread = null;
    private Thread mAlbumSourceThread = null;
    private boolean mListenerNeedsLayout;
    private boolean mWaitingForMediaScanner;
    private boolean mSingleImageMode;
    private boolean mLoading;
    private HashMap<String, ContentObserver> mContentObservers = new HashMap<String, ContentObserver>();
    private ArrayList<String[]> mRequestedRefresh = new ArrayList<String[]>();
    private volatile boolean mIsShutdown = false;

    public interface Listener {
        public abstract void onFeedAboutToChange(MediaFeed feed);

        public abstract void onFeedChanged(MediaFeed feed, boolean needsLayout);
    }

    public MediaFeed(Context context, DataSource dataSource, Listener listener) {
        mContext = context;
        mListener = listener;
        mDataSource = dataSource;
        mSingleWrapper.setNumExpectedItems(1);
        mLoading = true;
    }

    public void shutdown() {
        mIsShutdown = true;
        if (mDataSourceThread != null) {
            mDataSource.shutdown();
            repeatShuttingDownThread(mDataSourceThread);
            mDataSourceThread = null;
        }
        if (mAlbumSourceThread != null) {
            repeatShuttingDownThread(mAlbumSourceThread);
            mAlbumSourceThread = null;
        }
        int numSets = mMediaSets.size();
        for (int i = 0; i < numSets; ++i) {
            MediaSet set = mMediaSets.get(i);
            set.clear();
        }
        synchronized (mMediaSets) {
            mMediaSets.clear();
        }
        int numClusters = mClusterSets.size();
        for (int i = 0; i < numClusters; ++i) {
            MediaClustering mc = mClusterSets.get(i);
            if (mc != null) {
                mc.clear();
            }
        }
        mClusterSets.clear();
        mListener = null;
        mDataSource = null;
        mSingleWrapper = null;
    }

    private void repeatShuttingDownThread(Thread targetThread) {
        for (int i = 0; i < NUM_INTERRUPT_RETRIES && targetThread.isAlive(); ++i) {
            targetThread.interrupt();
            try {
                targetThread.join(JOIN_TIMEOUT);
            } catch (InterruptedException e) {
                Log.w(TAG, "Cannot stop the thread: " + targetThread.getName(), e);
                Thread.currentThread().interrupt();
                return;
            }
        }

        if (targetThread.isAlive()) {
            Log.w(TAG, "Cannot stop the thread: " + targetThread.getName());
        }
    }

    public void setVisibleRange(int begin, int end) {
        if (begin != mVisibleRange.begin || end != mVisibleRange.end) {
            mVisibleRange.begin = begin;
            mVisibleRange.end = end;
            int numItems = 96;
            int numItemsBy2 = numItems / 2;
            int numItemsBy4 = numItems / 4;
            mBufferedRange.begin = (begin / numItemsBy2) * numItemsBy2 - numItemsBy4;
            mBufferedRange.end = mBufferedRange.begin + numItems;
            mMediaFeedNeedsToRun = true;
        }
    }

    public void setFilter(MediaFilter filter) {
        mMediaFilter = filter;
        mMediaFilteredSet = null;
        if (mListener != null) {
            mListener.onFeedAboutToChange(this);
        }
        mMediaFeedNeedsToRun = true;
    }

    public void removeFilter() {
        mMediaFilter = null;
        mMediaFilteredSet = null;
        if (mListener != null) {
            mListener.onFeedAboutToChange(this);
            updateListener(true);
        }
        mMediaFeedNeedsToRun = true;
    }

    public ArrayList<MediaSet> getMediaSets() {
        return mMediaSets;
    }

    public MediaSet getMediaSet(final long setId) {
        if (setId != Shared.INVALID) {
            try {
                int mMediaSetsSize = mMediaSets.size();
                for (int i = 0; i < mMediaSetsSize; i++) {
                    final MediaSet set = mMediaSets.get(i);
                    if (set.mId == setId) {
                        set.mFlagForDelete = false;
                        return set;
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public MediaSet getFilteredSet() {
        return mMediaFilteredSet;
    }

    public MediaSet addMediaSet(final long setId, DataSource dataSource) {
        MediaSet mediaSet = new MediaSet(dataSource);
        mediaSet.mId = setId;
        mMediaSets.add(mediaSet);
        if (mDataSourceThread != null && !mDataSourceThread.isAlive()) {
            mDataSourceThread.start();
        }
        mMediaFeedNeedsToRun = true;
        return mediaSet;
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public MediaClustering getClustering() {
        if (mExpandedMediaSetIndex != Shared.INVALID && mExpandedMediaSetIndex < mMediaSets.size()) {
            return mClusterSets.get(mMediaSets.get(mExpandedMediaSetIndex));
        }
        return null;
    }

    public ArrayList<Cluster> getClustersForSet(final MediaSet set) {
        ArrayList<Cluster> clusters = null;
        if (mClusterSets != null && mClusterSets.containsKey(set)) {
            MediaClustering mediaClustering = mClusterSets.get(set);
            if (mediaClustering != null) {
                clusters = mediaClustering.getClusters();
            }
        }
        return clusters;
    }

    public void addItemToMediaSet(MediaItem item, MediaSet mediaSet) {
        item.mParentMediaSet = mediaSet;
        mediaSet.addItem(item);
        synchronized (mClusterSets) {
            if (item.mClusteringState == MediaItem.NOT_CLUSTERED) {
                MediaClustering clustering = mClusterSets.get(mediaSet);
                if (clustering == null) {
                    clustering = new MediaClustering(mediaSet.isPicassaAlbum());
                    mClusterSets.put(mediaSet, clustering);
                }
                clustering.setTimeRange(mediaSet.mMaxTimestamp - mediaSet.mMinTimestamp, mediaSet.getNumExpectedItems());
                clustering.addItemForClustering(item);
                item.mClusteringState = MediaItem.CLUSTERED;
            }
        }
        mMediaFeedNeedsToRun = true;
    }

    public void performOperation(final int operation, final ArrayList<MediaBucket> mediaBuckets, final Object data) {
        int numBuckets = mediaBuckets.size();
        final ArrayList<MediaBucket> copyMediaBuckets = new ArrayList<MediaBucket>(numBuckets);
        for (int i = 0; i < numBuckets; ++i) {
            copyMediaBuckets.add(mediaBuckets.get(i));
        }
        if (operation == OPERATION_DELETE && mListener != null) {
            mListener.onFeedAboutToChange(this);
        }
        Thread operationThread = new Thread(new Runnable() {
            public void run() {
                ArrayList<MediaBucket> mediaBuckets = copyMediaBuckets;
                if (operation == OPERATION_DELETE) {
                    int numBuckets = mediaBuckets.size();
                    for (int i = 0; i < numBuckets; ++i) {
                        MediaBucket bucket = mediaBuckets.get(i);
                        MediaSet set = bucket.mediaSet;
                        ArrayList<MediaItem> items = bucket.mediaItems;
                        if (set != null && items == null) {
                            // Remove the entire bucket.
                            removeMediaSet(set);
                        } else if (set != null && items != null) {
                            // We need to remove these items from the set.
                            int numItems = items.size();
                            // We also need to delete the items from the
                            // cluster.
                            MediaClustering clustering = mClusterSets.get(set);
                            for (int j = 0; j < numItems; ++j) {
                                MediaItem item = items.get(j);
                                removeItemFromMediaSet(item, set);
                                if (clustering != null) {
                                    clustering.removeItemFromClustering(item);
                                }
                            }
                            set.updateNumExpectedItems();
                            set.generateTitle(true);
                        }
                    }
                    updateListener(true);
                    mMediaFeedNeedsToRun = true;
                    if (mDataSource != null) {
                        mDataSource.performOperation(OPERATION_DELETE, mediaBuckets, null);
                    }
                } else {
                    mDataSource.performOperation(operation, mediaBuckets, data);
                }
            }
        });
        operationThread.setName("Operation " + operation);
        operationThread.start();
    }

    public void removeMediaSet(MediaSet set) {
        synchronized (mMediaSets) {
            mMediaSets.remove(set);
        }
        mMediaFeedNeedsToRun = true;
    }

    private void removeItemFromMediaSet(MediaItem item, MediaSet mediaSet) {
        mediaSet.removeItem(item);
        synchronized (mClusterSets) {
            MediaClustering clustering = mClusterSets.get(mediaSet);
            if (clustering != null) {
                clustering.removeItemFromClustering(item);
            }
        }
        mMediaFeedNeedsToRun = true;
    }

    public void updateListener(boolean needsLayout) {
        mListenerNeedsUpdate = true;
        mListenerNeedsLayout = needsLayout;
    }

    public int getNumSlots() {
        int currentMediaSetIndex = mExpandedMediaSetIndex;
        ArrayList<MediaSet> mediaSets = mMediaSets;
        int mediaSetsSize = mediaSets.size();

        if (mInClusteringMode == false) {
            if (currentMediaSetIndex == Shared.INVALID || currentMediaSetIndex >= mediaSetsSize) {
                return mediaSetsSize;
            } else {
                MediaSet setToUse = (mMediaFilteredSet == null) ? mediaSets.get(currentMediaSetIndex) : mMediaFilteredSet;
                return setToUse.getNumExpectedItems();
            }
        } else if (currentMediaSetIndex != Shared.INVALID && currentMediaSetIndex < mediaSetsSize) {
            MediaSet set = mediaSets.get(currentMediaSetIndex);
            MediaClustering clustering = mClusterSets.get(set);
            if (clustering != null) {
                return clustering.getClustersForDisplay().size();
            }
        }
        return 0;
    }

    public void copySlotStateFrom(MediaFeed another) {
        mExpandedMediaSetIndex = another.mExpandedMediaSetIndex;
        mInClusteringMode = another.mInClusteringMode;
    }

    public ArrayList<Integer> getBreaks() {
        if (true)
            return null;
        @SuppressWarnings("unused")
		int currentMediaSetIndex = mExpandedMediaSetIndex;
        ArrayList<MediaSet> mediaSets = mMediaSets;
        int mediaSetsSize = mediaSets.size();
        if (currentMediaSetIndex == Shared.INVALID || currentMediaSetIndex >= mediaSetsSize)
            return null;
        MediaSet set = mediaSets.get(currentMediaSetIndex);
        MediaClustering clustering = mClusterSets.get(set);
        if (clustering != null) {
            clustering.compute(null, true);
            final ArrayList<Cluster> clusters = clustering.getClustersForDisplay();
            int numClusters = clusters.size();
            final ArrayList<Integer> retVal = new ArrayList<Integer>(numClusters);
            int size = 0;
            for (int i = 0; i < numClusters; ++i) {
                size += clusters.get(i).getItems().size();
                retVal.add(size);
            }
            return retVal;
        } else {
            return null;
        }
    }

    public MediaSet getSetForSlot(int slotIndex) {
        if (slotIndex < 0) {
            return null;
        }

        ArrayList<MediaSet> mediaSets = mMediaSets;
        int mediaSetsSize = mediaSets.size();
        int currentMediaSetIndex = mExpandedMediaSetIndex;

        if (mInClusteringMode == false) {
            if (currentMediaSetIndex == Shared.INVALID || currentMediaSetIndex >= mediaSetsSize) {
                if (slotIndex >= mediaSetsSize) {
                    return null;
                }
                return mMediaSets.get(slotIndex);
            }
            if (mSingleWrapper.getNumItems() == 0) {
                mSingleWrapper.addItem(null);
            }
            MediaSet setToUse = (mMediaFilteredSet == null) ? mMediaSets.get(currentMediaSetIndex) : mMediaFilteredSet;
            ArrayList<MediaItem> items = setToUse.getItems();
            if (slotIndex >= setToUse.getNumItems()) {
                return null;
            }
            mSingleWrapper.getItems().set(0, items.get(slotIndex));
            return mSingleWrapper;
        } else if (currentMediaSetIndex != Shared.INVALID && currentMediaSetIndex < mediaSetsSize) {
            MediaSet set = mediaSets.get(currentMediaSetIndex);
            MediaClustering clustering = mClusterSets.get(set);
            if (clustering != null) {
                ArrayList<MediaClustering.Cluster> clusters = clustering.getClustersForDisplay();
                if (clusters.size() > slotIndex) {
                    MediaClustering.Cluster cluster = clusters.get(slotIndex);
                    cluster.generateCaption(mContext);
                    return cluster;
                }
            }
        }
        return null;
    }

    public boolean getWaitingForMediaScanner() {
        return mWaitingForMediaScanner;
    }

    public boolean isLoading() {
        return mLoading;
    }

    @SuppressWarnings("unused")
	public void start() 
    {
        final MediaFeed feed = this;
        onResume();
        mLoading = true;
        mDataSourceThread = new Thread(this);
        mDataSourceThread.setName("MediaFeed");
        mIsShutdown = false;
        mAlbumSourceThread = new Thread(new Runnable() 
        {
            @SuppressWarnings("static-access")
			public void run() 
            {
                if (mContext == null)
                    return;
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                DataSource dataSource = mDataSource;
                // We must wait while the SD card is mounted or the MediaScanner
                // is running.
                if (dataSource != null) 
                {
                    loadMediaSets();
                }
                mWaitingForMediaScanner = false;
                while (ImageManager.isMediaScannerScanning(mContext.getContentResolver())) {
                    // MediaScanner is still running, wait
                    if (Thread.interrupted())
                        return;
                    mWaitingForMediaScanner = true;
                    try {
                        if (mContext == null)
                            return;
                        showToast(mContext.getResources().getString(Res.string.initializing), Toast.LENGTH_LONG);
                        if (dataSource != null) {
                            loadMediaSets();
                        }
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                if (mWaitingForMediaScanner) {
                    showToast(mContext.getResources().getString(Res.string.loading_new), Toast.LENGTH_LONG);
                    mWaitingForMediaScanner = false;
                    loadMediaSets();
                }
                mLoading = false;
            }
        });
        mAlbumSourceThread.setName("MediaSets");
        mAlbumSourceThread.start();
    }

    private void loadMediaSets() {
        if (mDataSource == null)
            return;
        final ArrayList<MediaSet> sets = mMediaSets;
        synchronized (sets) {
            final int numSets = sets.size();
            for (int i = 0; i < numSets; ++i) {
                final MediaSet set = sets.get(i);
                set.mFlagForDelete = true;
            }
            mDataSource.refresh(MediaFeed.this, mDataSource.getDatabaseUris());
            //TODO: Thong
            mDataSource.loadMediaSets(MediaFeed.this);
            final ArrayList<MediaSet> setsToRemove = new ArrayList<MediaSet>();
            for (int i = 0; i < numSets; ++i) {
                final MediaSet set = sets.get(i);
                if (set.mFlagForDelete) {
                    setsToRemove.add(set);
                }
            }
            int numSetsToRemove = setsToRemove.size();
            for (int i = 0; i < numSetsToRemove; ++i) {
                sets.remove(setsToRemove.get(i));
            }
            setsToRemove.clear();
        }
        mMediaFeedNeedsToRun = true;
        updateListener(false);
    }

    private void showToast(final String string, final int duration) {
        showToast(string, duration, false);
    }

    private void showToast(final String string, final int duration, final boolean centered) {
        if (mContext != null && !App.get(mContext).isPaused()) {
            App.get(mContext).getHandler().post(new Runnable() {
                public void run() {
                    if (mContext != null) {
                        Toast toast = Toast.makeText(mContext, string, duration);
                        if (centered) {
                            toast.setGravity(Gravity.CENTER, 0, 0);
                        }
                        toast.show();
                    }
                }
            });
        }
    }

    public void run() {
        DataSource dataSource = mDataSource;
        int sleepMs = 10;
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        if (dataSource != null) {
            while (!Thread.interrupted() && !mIsShutdown) {
                String[] databaseUris = null;
                boolean performRefresh = false;
                synchronized (mRequestedRefresh) {
                    if (mRequestedRefresh.size() > 0) {
                        // We prune this first.
                        int numRequests = mRequestedRefresh.size();
                        for (int i = 0; i < numRequests; ++i) {
                            databaseUris = ArrayUtils.addAll(databaseUris, mRequestedRefresh.get(i));
                        }
                        mRequestedRefresh.clear();
                        performRefresh = true;
                        // We need to eliminate duplicate uris in this array
                        final HashMap<String, String> uris = new HashMap<String, String>();
                        if (databaseUris != null) {
                            int numUris = databaseUris.length;
                            for (int i = 0; i < numUris; ++i) {
                                final String uri = databaseUris[i];
                                if (uri != null)
                                    uris.put(uri, uri);
                            }
                        }
                        databaseUris = new String[0];
                        databaseUris = (String[]) uris.keySet().toArray(databaseUris);
                    }
                }
                boolean settingFeedAboutToChange = false;
                if (performRefresh) {
                    if (dataSource != null) {
                        if (mListener != null) {
                            settingFeedAboutToChange = true;
                            mListener.onFeedAboutToChange(this);
                        }
                        dataSource.refresh(this, databaseUris);
                        mMediaFeedNeedsToRun = true;
                    }
                }
                if (mListenerNeedsUpdate && !mMediaFeedNeedsToRun) {
                    mListenerNeedsUpdate = false;
                    if (mListener != null)
                        synchronized (mMediaSets) {
                            mListener.onFeedChanged(this, mListenerNeedsLayout);
                        }
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        return;
                    }
                } else {
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                sleepMs = 300;
                if (!mMediaFeedNeedsToRun)
                    continue;
                App app = App.get(mContext);
                if (app == null || app.isPaused())
                    continue;
                if (settingFeedAboutToChange) {
                    updateListener(true);
                }
                mMediaFeedNeedsToRun = false;
                ArrayList<MediaSet> mediaSets = mMediaSets;
                synchronized (mediaSets) {
                    int expandedSetIndex = mExpandedMediaSetIndex;
                    if (expandedSetIndex >= mMediaSets.size()) {
                        expandedSetIndex = Shared.INVALID;
                    }
                    if (expandedSetIndex == Shared.INVALID) {
                        // We purge the sets outside this visibleRange.
                        int numSets = mediaSets.size();
                        IndexRange visibleRange = mVisibleRange;
                        IndexRange bufferedRange = mBufferedRange;
                        boolean scanMediaSets = true;
                        for (int i = 0; i < numSets; ++i) {
                            if (i >= visibleRange.begin && i <= visibleRange.end && scanMediaSets) {
                                MediaSet set = mediaSets.get(i);
                                int numItemsLoaded = set.mNumItemsLoaded;
                                if (numItemsLoaded < set.getNumExpectedItems() && numItemsLoaded < 8) {
                                    synchronized (set) {
                                        dataSource.loadItemsForSet(this, set, numItemsLoaded, 8);
                                        set.checkForDeletedItems();
                                    }
                                    if (set.getNumExpectedItems() == 0) {
                                        mediaSets.remove(set);
                                        break;
                                    }
                                    if (mListener != null) {
                                        mListenerNeedsUpdate = false;
                                        mListener.onFeedChanged(this, mListenerNeedsLayout);
                                        mListenerNeedsLayout = false;
                                    }
                                    sleepMs = 100;
                                    scanMediaSets = false;
                                }
                                if (!set.setContainsValidItems()) {
                                    mediaSets.remove(set);
                                    if (mListener != null) {
                                        mListenerNeedsUpdate = false;
                                        mListener.onFeedChanged(this, mListenerNeedsLayout);
                                        mListenerNeedsLayout = false;
                                    }
                                    break;
                                }
                            }
                        }
                        numSets = mediaSets.size();
                        for (int i = 0; i < numSets; ++i) {
                            MediaSet set = mediaSets.get(i);
                            if (i >= bufferedRange.begin && i <= bufferedRange.end) {
                                if (scanMediaSets) {
                                    int numItemsLoaded = set.mNumItemsLoaded;
                                    if (numItemsLoaded < set.getNumExpectedItems() && numItemsLoaded < 8) {
                                        synchronized (set) {
                                            dataSource.loadItemsForSet(this, set, numItemsLoaded, 8);
                                            set.checkForDeletedItems();
                                        }
                                        if (set.getNumExpectedItems() == 0) {
                                            mediaSets.remove(set);
                                            break;
                                        }
                                        if (mListener != null) {
                                            mListenerNeedsUpdate = false;
                                            mListener.onFeedChanged(this, mListenerNeedsLayout);
                                            mListenerNeedsLayout = false;
                                        }
                                        sleepMs = 100;
                                        scanMediaSets = false;
                                    }
                                }
                            } else if (!mListenerNeedsUpdate && (i < bufferedRange.begin || i > bufferedRange.end)) {
                                // Purge this set to its initial status.
                                MediaClustering clustering = mClusterSets.get(set);
                                if (clustering != null) {
                                    clustering.clear();
                                    mClusterSets.remove(set);
                                }
                                if (set.getNumItems() != 0)
                                    set.clear();
                            }
                        }
                    }
                    if (expandedSetIndex != Shared.INVALID) {
                        int numSets = mMediaSets.size();
                        for (int i = 0; i < numSets; ++i) {
                            // Purge other sets.
                            if (i != expandedSetIndex) {
                                MediaSet set = mediaSets.get(i);
                                MediaClustering clustering = mClusterSets.get(set);
                                if (clustering != null) {
                                    clustering.clear();
                                    mClusterSets.remove(set);
                                }
                                if (set.mNumItemsLoaded != 0)
                                    set.clear();
                            }
                        }
                        // Make sure all the items are loaded for the album.
                        int numItemsLoaded = mediaSets.get(expandedSetIndex).mNumItemsLoaded;
                        int requestedItems = mVisibleRange.end;
                        // requestedItems count changes in clustering mode.
                        if (mInClusteringMode && mClusterSets != null) {
                            requestedItems = 0;
                            MediaClustering clustering = mClusterSets.get(mediaSets.get(expandedSetIndex));
                            if (clustering != null) {
                                ArrayList<Cluster> clusters = clustering.getClustersForDisplay();
                                int numClusters = clusters.size();
                                for (int i = 0; i < numClusters; i++) {
                                    requestedItems += clusters.get(i).getNumExpectedItems();
                                }
                            }
                        }
                        MediaSet set = mediaSets.get(expandedSetIndex);
                        if (numItemsLoaded < set.getNumExpectedItems()) {
                            // We perform calculations for a window that gets
                            // anchored to a multiple of NUM_ITEMS_LOOKAHEAD.
                            // The start of the window is 0, x, 2x, 3x ... etc
                            // where x = NUM_ITEMS_LOOKAHEAD.
                            synchronized (set) {
                                dataSource.loadItemsForSet(this, set, numItemsLoaded, (requestedItems / NUM_ITEMS_LOOKAHEAD)
                                        * NUM_ITEMS_LOOKAHEAD + NUM_ITEMS_LOOKAHEAD);
                                set.checkForDeletedItems();
                            }
                            if (set.getNumExpectedItems() == 0) {
                                mediaSets.remove(set);
                                mListenerNeedsUpdate = false;
                                mListener.onFeedChanged(this, mListenerNeedsLayout);
                                mListenerNeedsLayout = false;
                            }
                            if (numItemsLoaded != set.mNumItemsLoaded && mListener != null) {
                                mListenerNeedsUpdate = false;
                                mListener.onFeedChanged(this, mListenerNeedsLayout);
                                mListenerNeedsLayout = false;
                            }
                        }
                    }
                    MediaFilter filter = mMediaFilter;
                    if (filter != null && mMediaFilteredSet == null) {
                        if (expandedSetIndex != Shared.INVALID) {
                            MediaSet set = mediaSets.get(expandedSetIndex);
                            ArrayList<MediaItem> items = set.getItems();
                            int numItems = set.getNumItems();
                            MediaSet filteredSet = new MediaSet();
                            filteredSet.setNumExpectedItems(numItems);
                            mMediaFilteredSet = filteredSet;
                            for (int i = 0; i < numItems; ++i) {
                                MediaItem item = items.get(i);
                                if (filter.pass(item)) {
                                    filteredSet.addItem(item);
                                }
                            }
                            filteredSet.updateNumExpectedItems();
                            filteredSet.generateTitle(true);
                        }
                        updateListener(true);
                    }
                }
            }
        }
    }

    public void expandMediaSet(int mediaSetIndex) {
        // We need to check if this slot can be focused or not.
        if (mListener != null) {
            mListener.onFeedAboutToChange(this);
        }
        if (mExpandedMediaSetIndex > 0 && mediaSetIndex == Shared.INVALID) {
            // We are collapsing a previously expanded media set
            if (mediaSetIndex < mMediaSets.size() && mExpandedMediaSetIndex >= 0 && mExpandedMediaSetIndex < mMediaSets.size()) {
                MediaSet set = mMediaSets.get(mExpandedMediaSetIndex);
                if (set.getNumItems() == 0) {
                    set.clear();
                }
            }
        }
        mExpandedMediaSetIndex = mediaSetIndex;
        if (mediaSetIndex < mMediaSets.size() && mediaSetIndex >= 0) {
            // Notify Picasa that the user entered the album.
            // MediaSet set = mMediaSets.get(mediaSetIndex);
            // PicasaService.requestSync(mContext,
            // PicasaService.TYPE_ALBUM_PHOTOS, set.mPicasaAlbumId);
        }
        updateListener(true);
        mMediaFeedNeedsToRun = true;
    }

    public boolean canExpandSet(int slotIndex) {
        int mediaSetIndex = slotIndex;
        if (mediaSetIndex < mMediaSets.size() && mediaSetIndex >= 0) {
            MediaSet set = mMediaSets.get(mediaSetIndex);
            if (set.getNumItems() > 0) {
                MediaItem item = set.getItems().get(0);
                if (item.mId == Shared.INVALID) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public boolean hasExpandedMediaSet() {
        return (mExpandedMediaSetIndex != Shared.INVALID);
    }

    public boolean restorePreviousClusteringState() {
        boolean retVal = disableClusteringIfNecessary();
        if (retVal) {
            if (mListener != null) {
                mListener.onFeedAboutToChange(this);
            }
            updateListener(true);
            mMediaFeedNeedsToRun = true;
        }
        return retVal;
    }

    private boolean disableClusteringIfNecessary() {
        if (mInClusteringMode) {
            // Disable clustering.
            mInClusteringMode = false;
            mMediaFeedNeedsToRun = true;
            return true;
        }
        return false;
    }

    public boolean isClustered() {
        return mInClusteringMode;
    }

    public MediaSet getCurrentSet() {
        if (mExpandedMediaSetIndex != Shared.INVALID && mExpandedMediaSetIndex < mMediaSets.size()) {
            return mMediaSets.get(mExpandedMediaSetIndex);
        }
        return null;
    }

    public void performClustering() {
        if (mListener != null) {
            mListener.onFeedAboutToChange(this);
        }
        MediaSet setToUse = null;
        if (mExpandedMediaSetIndex != Shared.INVALID && mExpandedMediaSetIndex < mMediaSets.size()) {
            setToUse = mMediaSets.get(mExpandedMediaSetIndex);
        }
        if (setToUse != null) {
            MediaClustering clustering = null;
            synchronized (mClusterSets) {
                // Make sure the computation is completed to the end.
                clustering = mClusterSets.get(setToUse);
                if (clustering != null) {
                    clustering.compute(null, true);
                } else {
                    return;
                }
            }
            mInClusteringMode = true;
            updateListener(true);
        }
    }

    public void moveSetToFront(MediaSet mediaSet) {
        ArrayList<MediaSet> mediaSets = mMediaSets;
        int numSets = mediaSets.size();
        if (numSets == 0) {
            mediaSets.add(mediaSet);
            return;
        }
        MediaSet setToFind = mediaSets.get(0);
        if (setToFind == mediaSet) {
            return;
        }
        mediaSets.set(0, mediaSet);
        int indexToSwapTill = -1;
        for (int i = 1; i < numSets; ++i) {
            MediaSet set = mediaSets.get(i);
            if (set == mediaSet) {
                mediaSets.set(i, setToFind);
                indexToSwapTill = i;
                break;
            }
        }
        if (indexToSwapTill != Shared.INVALID) {
            for (int i = indexToSwapTill; i > 1; --i) {
                MediaSet setEnd = mediaSets.get(i);
                MediaSet setPrev = mediaSets.get(i - 1);
                mediaSets.set(i, setPrev);
                mediaSets.set(i - 1, setEnd);
            }
        }
        mMediaFeedNeedsToRun = true;
    }

    public MediaSet replaceMediaSet(long setId, DataSource dataSource) {
        Log.i(TAG, "Replacing media set " + setId);
        final MediaSet set = getMediaSet(setId);
        if (set != null)
            set.refresh();
        return set;
    }

    public void setSingleImageMode(boolean singleImageMode) {
        mSingleImageMode = singleImageMode;
    }

    public boolean isSingleImageMode() {
        return mSingleImageMode;
    }

    public MediaSet getExpandedMediaSet() {
        if (mExpandedMediaSetIndex == Shared.INVALID)
            return null;
        if (mExpandedMediaSetIndex >= mMediaSets.size())
            return null;
        return mMediaSets.get(mExpandedMediaSetIndex);
    }

    public void refresh() {
        if (mDataSource != null) {
            synchronized (mRequestedRefresh) {
                mRequestedRefresh.add(mDataSource.getDatabaseUris());
            }
        }
    }

    private void refresh(final String[] databaseUris) {
        synchronized (mMediaSets) {
            if (mDataSource != null) {
                synchronized (mRequestedRefresh) {
                    mRequestedRefresh.add(databaseUris);
                }
            }
        }
    }

    public void onPause() {
        final HashMap<String, ContentObserver> observers = mContentObservers;
        final int numObservers = observers.size();
        if (numObservers > 0) {
            String[] uris = new String[numObservers];
            final Set<String> keySet = observers.keySet();
            if (keySet != null) {
                uris = keySet.toArray(uris);
                final int numUris = uris.length;
                final ContentResolver cr = mContext.getContentResolver();
                for (int i = 0; i < numUris; ++i) {
                    final String uri = uris[i];
                    if (uri != null) {
                        final ContentObserver observer = observers.get(uri);
                        cr.unregisterContentObserver(observer);
                        observers.remove(uri);
                    }
                }
            }
        }
        observers.clear();
    }

    @SuppressWarnings("unused")
	public void onResume() {
        final Context context = mContext;
        final DataSource dataSource = mDataSource;
        if (context == null || dataSource == null)
            return;
        // We setup the listeners for this datasource
        final String[] uris = dataSource.getDatabaseUris();
        final HashMap<String, ContentObserver> observers = mContentObservers;
        if (context instanceof Gallery) {
            final Gallery gallery = (Gallery) context;
            final ContentResolver cr = context.getContentResolver();
            if (uris != null) {
                final int numUris = uris.length;
                for (int i = 0; i < numUris; ++i) {
                    final String uri = uris[i];
                    final ContentObserver presentObserver = observers.get(uri);
                    if (presentObserver == null) {
                        final Handler handler = App.get(context).getHandler();
                        final ContentObserver observer = new ContentObserver(handler) {
                            public void onChange(boolean selfChange) {
                                if (!mWaitingForMediaScanner) {
                                    MediaFeed.this.refresh(new String[] { uri });
                                }
                            }
                        };
                        cr.registerContentObserver(Uri.parse(uri), true, observer);
                        observers.put(uri, observer);
                    }
                }
            }
        }
        refresh();
    }
}
