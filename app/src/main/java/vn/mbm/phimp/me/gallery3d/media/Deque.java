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

public final class Deque<E extends Object> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    private E[] mArray;
    private int mHead = 0;
    private int mTail = 0;

    @SuppressWarnings("unchecked")
    public Deque() {
        mArray = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    @SuppressWarnings("unchecked")
    public Deque(int initialCapacity) {
        // CR: check initialCapacity & (initialCapacity - 1) == 0.
        mArray = (E[]) new Object[initialCapacity];
    }

    public boolean isEmpty() {
        return mHead == mTail;
    }

    public int size() {
        return (mTail - mHead) & (mArray.length - 1); // CR: wtf?!? this
                                                      // definitely needs a
                                                      // comment.
    }

    public void clear() {
        E[] array = mArray;
        int head = mHead;
        int tail = mTail;
        if (head != tail) {
            int mask = array.length - 1;
            do {
                array[head] = null;
                head = (head + 1) & mask;
            } while (head != tail);
            mHead = 0;
            mTail = 0;
        }
    }

    public E get(int index) {
        E[] array = mArray;
        if (index >= size()) {
            throw new IndexOutOfBoundsException();
        }
        return array[(mHead + index) & (array.length - 1)];
    }

    public void addFirst(E e) {
        E[] array = mArray;
        int head = (mHead - 1) & (array.length - 1);
        mHead = head;
        array[head] = e;
        if (head == mTail) {
            expand();
        }
    }

    public void addLast(E e) {
        E[] array = mArray;
        int tail = mTail;
        array[tail] = e;
        tail = (tail + 1) & (array.length - 1);
        mTail = tail;
        if (mHead == tail) {
            expand();
        }
    }

    public E pollFirst() {
        E[] array = mArray;
        int head = mHead;
        E result = array[head];
        if (result == null) {
            return null;
        }
        array[head] = null;
        mHead = (head + 1) & (array.length - 1);
        return result;
    }

    public E pollLast() {
        E[] array = mArray;
        int tail = (mTail - 1) & (array.length - 1);
        E result = array[tail];
        if (result == null) {
            return null;
        }
        array[tail] = null;
        mTail = tail;
        return result;
    }

    @SuppressWarnings("unchecked")
    private void expand() {
        // Must be called only when head == tail.
        E[] array = mArray;
        int head = mHead;
        int capacity = array.length;
        int rightSize = capacity - head;
        int newCapacity = capacity << 1;
        Object[] newArray = new Object[newCapacity];
        System.arraycopy(array, head, newArray, 0, rightSize);
        System.arraycopy(array, 0, newArray, rightSize, head);
        mArray = (E[]) newArray;
        mHead = 0;
        mTail = capacity;
    }
}
