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

public final class IndexRange {
    public IndexRange(int beginRange, int endRange) {
        begin = beginRange;
        end = endRange;
    }

    public IndexRange() {
        begin = 0;
        end = 0;
    }

    public void set(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public boolean isEmpty() {
        return begin == end;
    }

    public int size() {
        return end - begin;
    }

    public int begin;
    public int end;
}
