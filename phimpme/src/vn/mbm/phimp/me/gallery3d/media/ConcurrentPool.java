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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class ConcurrentPool<E extends Object> {
    private AtomicReferenceArray<E> mFreeList;
    private AtomicInteger mFreeListIndex;

    public ConcurrentPool(E[] objects) {
        mFreeList = new AtomicReferenceArray<E>(objects);
        mFreeListIndex = new AtomicInteger(objects.length);
    }

    public E create() {
        final int index = mFreeListIndex.decrementAndGet();
        E object = mFreeList.get(index);
        mFreeList.set(index, null);
        return object;
    }

    public void delete(E object) {
        final int index = mFreeListIndex.getAndIncrement();
        while (!mFreeList.compareAndSet(index, null, object)) {
            Thread.yield();
        }
    }
}
