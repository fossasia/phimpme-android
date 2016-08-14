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

public abstract class VirtualFeed<E> {
    protected int mLoadedBegin = 0;
    protected int mLoadedEnd = 0;
    protected int mLoadingBegin = 0;
    protected int mLoadingEnd = 0;
    protected int mLoadableBegin = Integer.MIN_VALUE;
    protected int mLoadableEnd = Integer.MAX_VALUE;
    protected final Deque<E> mElements = new Deque<E>();

    public VirtualFeed() {
    }

    public abstract void setLoadingRange(int begin, int end);

    public final E get(int index) {

        return null;

    }

    public final int getLoadedBegin() {
        return mLoadedBegin;
    }

    public final int getLoadedEnd() {
        return mLoadedEnd;
    }

    public final int getLoadingBegin() {
        return mLoadingBegin;
    }

    public final int getLoadingEnd() {
        return mLoadingEnd;
    }

    public final int getLoadableBegin() {
        return mLoadableBegin;
    }

    public final int getLoadableEnd() {
        return mLoadableEnd;
    }

    public interface RangeListener<E> {
        void onRangeUpdated(VirtualFeed<E> array);
    }
}
