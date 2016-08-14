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

public final class DirectLinkedList<E> {
    private Entry<E> mHead;
    private Entry<E> mTail;
    private int mSize = 0;

    public static final class Entry<E> {
        Entry(E value) {
            this.value = value;
        }

        public final E value;
        public Entry<E> previous = null;
        public Entry<E> next = null;
        public boolean inserted = false;
    }

    public DirectLinkedList() {
    }

    public boolean isEmpty() {
        return mSize == 0;
    }

    public int size() {
        return mSize;
    }

    public void add(Entry<E> entry) {
        // Requires that entry not be inserted in a list.
        final Entry<E> tail = mTail;
        if (tail != null) {
            tail.next = entry;
            entry.previous = tail;
        } else {
            mHead = entry;
        }
        mTail = entry;
        entry.inserted = true;
        ++mSize;
    }

    public Entry<E> remove(Entry<E> entry) {
        // Requires that entry be inserted into this list.
        final Entry<E> previous = entry.previous;
        final Entry<E> next = entry.next;
        if (next != null) {
            next.previous = previous;
            entry.next = null;
        } else {
            mTail = previous;
        }
        if (previous != null) {
            previous.next = next;
            entry.previous = null;
        } else {
            mHead = next;
        }
        entry.inserted = false;
        --mSize;
        if (mSize < 0)
            mSize = 0;
        return next;
    }

    public Entry<E> getHead() {
        return mHead;
    }

    public Entry<E> getTail() {
        return mTail;
    }

    public void clear() {
        mHead = null;
        mTail = null;
        mSize = 0;
    }
}
