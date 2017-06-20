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

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class IndexRangeIterator<E> implements Iterator<E> {
    private final E[] mList;
    private IndexRange mRange;
    private int mPos;

    public IndexRangeIterator(E[] list) {
        super();
        mList = list;
        mRange = new IndexRange();
        mPos = mRange.begin - 1;
    }

    public void rewind() {
        mPos = mRange.begin - 1;
    }

    public void setRange(int begin, int end) {
        mRange.begin = begin;
        mRange.end = end;
        mPos = begin - 1;
    }

    public int getBegin() {
        return mRange.begin;
    }

    public int getEnd() {
        return mRange.end;
    }

    public boolean hasNext() {
        int pos = mPos + 1;
        return (pos < mList.length && pos < mRange.end);
    }

    public int getCurrentPosition() {
        return mPos;
    }

    public E next() {
        // TODO Auto-generated method stub
        int pos = mPos + 1;
        ++mPos;
        return mList[pos];
    }

    public void remove() {
        // TODO Auto-generated method stub
        throw new ConcurrentModificationException();
    }

}
