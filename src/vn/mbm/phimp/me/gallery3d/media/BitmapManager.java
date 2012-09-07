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

import java.io.FileDescriptor;
import java.util.Iterator;
import java.util.WeakHashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * This class provides several utilities to cancel bitmap decoding.
 * 
 * The function decodeFileDescriptor() is used to decode a bitmap. During
 * decoding if another thread wants to cancel it, it calls the function
 * cancelThreadDecoding() specifying the Thread which is in decoding.
 * 
 * cancelThreadDecoding() is sticky until allowThreadDecoding() is called.
 * 
 * You can also cancel decoding for a set of threads using ThreadSet as the
 * parameter for cancelThreadDecoding. To put a thread into a ThreadSet, use the
 * add() method. A ThreadSet holds (weak) references to the threads, so you
 * don't need to remove Thread from it if some thread dies.
 */
public class BitmapManager {
    private static final String TAG = "BitmapManager";

    private static enum State {
        CANCEL, ALLOW
    }

    private static class ThreadStatus {
        public State mState = State.ALLOW;
        public BitmapFactory.Options mOptions;

        @Override
        public String toString() {
            String s;
            if (mState == State.CANCEL) {
                s = "Cancel";
            } else if (mState == State.ALLOW) {
                s = "Allow";
            } else {
                s = "?";
            }
            s = "thread state = " + s + ", options = " + mOptions;
            return s;
        }
    }

    public static class ThreadSet implements Iterable<Thread> {
        private final WeakHashMap<Thread, Object> mWeakCollection = new WeakHashMap<Thread, Object>();

        public void add(Thread t) {
            mWeakCollection.put(t, null);
        }

        public void remove(Thread t) {
            mWeakCollection.remove(t);
        }

        public Iterator<Thread> iterator() {
            return mWeakCollection.keySet().iterator();
        }
    }

    private final WeakHashMap<Thread, ThreadStatus> mThreadStatus = new WeakHashMap<Thread, ThreadStatus>();

    private static BitmapManager sManager = null;

    private BitmapManager() {
    }

    /**
     * Get thread status and create one if specified.
     */
    private synchronized ThreadStatus getOrCreateThreadStatus(Thread t) {
        ThreadStatus status = mThreadStatus.get(t);
        if (status == null) {
            status = new ThreadStatus();
            mThreadStatus.put(t, status);
        }
        return status;
    }

    /**
     * The following three methods are used to keep track of
     * BitmapFaction.Options used for decoding and cancelling.
     */
    private synchronized void setDecodingOptions(Thread t, BitmapFactory.Options options) {
        getOrCreateThreadStatus(t).mOptions = options;
    }

    synchronized void removeDecodingOptions(Thread t) {
        ThreadStatus status = mThreadStatus.get(t);
        status.mOptions = null;
    }

    /**
     * The following two methods are used to allow/cancel a set of threads for
     * bitmap decoding.
     */
    public synchronized void allowThreadDecoding(ThreadSet threads) {
        for (Thread t : threads) {
            allowThreadDecoding(t);
        }
    }

    public synchronized void cancelThreadDecoding(ThreadSet threads) {
        for (Thread t : threads) {
            cancelThreadDecoding(t);
        }
    }

    /**
     * The following three methods are used to keep track of which thread is
     * being disabled for bitmap decoding.
     */
    public synchronized boolean canThreadDecoding(Thread t) {
        ThreadStatus status = mThreadStatus.get(t);
        if (status == null) {
            // allow decoding by default
            return true;
        }

        // CR: return this shit directly; no need for 'result'.
        boolean result = (status.mState != State.CANCEL);
        return result;
    }

    public synchronized void allowThreadDecoding(Thread t) {
        getOrCreateThreadStatus(t).mState = State.ALLOW;
    }

    public synchronized void cancelThreadDecoding(Thread t) {
        ThreadStatus status = getOrCreateThreadStatus(t);
        status.mState = State.CANCEL;
        if (status.mOptions != null) {
            status.mOptions.requestCancelDecode();
        }

        // Wake up threads in waiting list
        notifyAll();
    }

    public static synchronized BitmapManager instance() {
        if (sManager == null) {
            sManager = new BitmapManager();
        }
        return sManager;
    }

    /**
     * The real place to delegate bitmap decoding to BitmapFactory.
     */
    public Bitmap decodeFileDescriptor(FileDescriptor fd, BitmapFactory.Options options) {
        if (options.mCancel) {
            return null;
        }

        Thread thread = Thread.currentThread();
        if (!canThreadDecoding(thread)) {
            Log.d(TAG, "Thread " + thread + " is not allowed to decode.");
            return null;
        }

        setDecodingOptions(thread, options);
        Bitmap b = BitmapFactory.decodeFileDescriptor(fd, null, options);

        removeDecodingOptions(thread);
        return b;
    }
}
