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

public final class Pool<E extends Object> {
    private final E[] mFreeList;
    private int mFreeListIndex;

    public Pool(E[] objects) {
        mFreeList = objects;
        mFreeListIndex = objects.length;
    }

    public E create() {
        int index = --mFreeListIndex;
        if (index >= 0 && index < mFreeList.length) {
            E object = mFreeList[index];
            mFreeList[index] = null;
            return object;
        }
        return null;
    }

    public void delete(E object) {
        int index = mFreeListIndex;
        if (index >= 0 && index < mFreeList.length) {
            mFreeList[index] = object;
            mFreeListIndex++;
        }
    }
}
