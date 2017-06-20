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
 * 
 * NOTE(aws): Adapted into the project from the repository since this is a non-public class.
 */

package vn.mbm.phimp.me.gallery3d.media;

import java.lang.reflect.Array;

import android.util.Log;

/**
 * SparseArrays map longs to Objects. Unlike a normal array of Objects, there
 * can be gaps in the indices. It is intended to be more efficient than using a
 * HashMap to map Longs to Objects.
 * 
 * @hide
 */
public final class LongSparseArray<E> {
    private static final Object DELETED = new Object();
    private boolean mGarbage = false;

    /**
     * Creates a new SparseArray containing no mappings.
     */
    public LongSparseArray() {
        this(10);
    }

    /**
     * Creates a new SparseArray containing no mappings that will not require
     * any additional memory allocation to store the specified number of
     * mappings.
     */
    public LongSparseArray(int initialCapacity) {
        initialCapacity = ArrayUtils.idealIntArraySize(initialCapacity);

        mKeys = new long[initialCapacity];
        mValues = new Object[initialCapacity];
        mSize = 0;
    }

    /**
     * Gets the Object mapped from the specified key, or <code>null</code> if no
     * such mapping has been made.
     */
    public E get(long key) {
        return get(key, null);
    }

    /**
     * Gets the Object mapped from the specified key, or the specified Object if
     * no such mapping has been made.
     */
    @SuppressWarnings("unchecked")
    public E get(long key, E valueIfKeyNotFound) {
        int i = binarySearch(mKeys, 0, mSize, key);

        if (i < 0 || mValues[i] == DELETED) {
            return valueIfKeyNotFound;
        } else {
            return (E) mValues[i];
        }
    }

    /**
     * Removes the mapping from the specified key, if there was any.
     */
    public void delete(long key) {
        int i = binarySearch(mKeys, 0, mSize, key);

        if (i >= 0) {
            if (mValues[i] != DELETED) {
                mValues[i] = DELETED;
                mGarbage = true;
            }
        }
    }

    /**
     * Alias for {@link #delete(long)}.
     */
    public void remove(long key) {
        delete(key);
    }

    private void gc() {
        // Log.e("SparseArray", "gc start with " + mSize);

        int n = mSize;
        int o = 0;
        long[] keys = mKeys;
        Object[] values = mValues;

        for (int i = 0; i < n; i++) {
            Object val = values[i];

            if (val != DELETED) {
                if (i != o) {
                    keys[o] = keys[i];
                    values[o] = val;
                }

                o++;
            }
        }

        mGarbage = false;
        mSize = o;

        // Log.e("SparseArray", "gc end with " + mSize);
    }

    /**
     * Adds a mapping from the specified key to the specified value, replacing
     * the previous mapping from the specified key if there was one.
     */
    public void put(long key, E value) {
        int i = binarySearch(mKeys, 0, mSize, key);

        if (i >= 0) {
            mValues[i] = value;
        } else {
            i = ~i;

            if (i < mSize && mValues[i] == DELETED) {
                mKeys[i] = key;
                mValues[i] = value;
                return;
            }

            if (mGarbage && mSize >= mKeys.length) {
                gc();

                // Search again because indices may have changed.
                i = ~binarySearch(mKeys, 0, mSize, key);
            }

            if (mSize >= mKeys.length) {
                int n = ArrayUtils.idealIntArraySize(mSize + 1);

                long[] nkeys = new long[n];
                Object[] nvalues = new Object[n];

                // Log.e("SparseArray", "grow " + mKeys.length + " to " + n);
                System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
                System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

                mKeys = nkeys;
                mValues = nvalues;
            }

            if (mSize - i != 0) {
                // Log.e("SparseArray", "move " + (mSize - i));
                System.arraycopy(mKeys, i, mKeys, i + 1, mSize - i);
                System.arraycopy(mValues, i, mValues, i + 1, mSize - i);
            }

            mKeys[i] = key;
            mValues[i] = value;
            mSize++;
        }
    }

    /**
     * Returns the number of key-value mappings that this SparseArray currently
     * stores.
     */
    public int size() {
        if (mGarbage) {
            gc();
        }

        return mSize;
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, returns the key
     * from the <code>index</code>th key-value mapping that this SparseArray
     * stores.
     */
    public long keyAt(int index) {
        if (mGarbage) {
            gc();
        }

        return mKeys[index];
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, returns the value
     * from the <code>index</code>th key-value mapping that this SparseArray
     * stores.
     */
    @SuppressWarnings("unchecked")
    public E valueAt(int index) {
        if (mGarbage) {
            gc();
        }

        return (E) mValues[index];
    }

    /**
     * Given an index in the range <code>0...size()-1</code>, sets a new value
     * for the <code>index</code>th key-value mapping that this SparseArray
     * stores.
     */
    public void setValueAt(int index, E value) {
        if (mGarbage) {
            gc();
        }

        mValues[index] = value;
    }

    /**
     * Returns the index for which {@link #keyAt} would return the specified
     * key, or a negative number if the specified key is not mapped.
     */
    public int indexOfKey(long key) {
        if (mGarbage) {
            gc();
        }

        return binarySearch(mKeys, 0, mSize, key);
    }

    /**
     * Returns an index for which {@link #valueAt} would return the specified
     * key, or a negative number if no keys map to the specified value. Beware
     * that this is a linear search, unlike lookups by key, and that multiple
     * keys can map to the same value and this will find only one of them.
     */
    public int indexOfValue(E value) {
        if (mGarbage) {
            gc();
        }

        for (int i = 0; i < mSize; i++)
            if (mValues[i] == value)
                return i;

        return -1;
    }

    /**
     * Removes all key-value mappings from this SparseArray.
     */
    public void clear() {
        int n = mSize;
        Object[] values = mValues;

        for (int i = 0; i < n; i++) {
            values[i] = null;
        }

        mSize = 0;
        mGarbage = false;
    }

    /**
     * Puts a key/value pair into the array, optimizing for the case where the
     * key is greater than all existing keys in the array.
     */
    public void append(long key, E value) {
        if (mSize != 0 && key <= mKeys[mSize - 1]) {
            put(key, value);
            return;
        }

        if (mGarbage && mSize >= mKeys.length) {
            gc();
        }

        int pos = mSize;
        if (pos >= mKeys.length) {
            int n = ArrayUtils.idealIntArraySize(pos + 1);

            long[] nkeys = new long[n];
            Object[] nvalues = new Object[n];

            // Log.e("SparseArray", "grow " + mKeys.length + " to " + n);
            System.arraycopy(mKeys, 0, nkeys, 0, mKeys.length);
            System.arraycopy(mValues, 0, nvalues, 0, mValues.length);

            mKeys = nkeys;
            mValues = nvalues;
        }

        mKeys[pos] = key;
        mValues[pos] = value;
        mSize = pos + 1;
    }

    private static int binarySearch(long[] a, int start, int len, long key) {
        int high = start + len, low = start - 1, guess;

        while (high - low > 1) {
            guess = (high + low) / 2;

            if (a[guess] < key)
                low = guess;
            else
                high = guess;
        }

        if (high == start + len)
            return ~(start + len);
        else if (a[high] == key)
            return high;
        else
            return ~high;
    }

    @SuppressWarnings("unused")
    private void checkIntegrity() {
        for (int i = 1; i < mSize; i++) {
            if (mKeys[i] <= mKeys[i - 1]) {
                for (int j = 0; j < mSize; j++) {
                    Log.e("FAIL", j + ": " + mKeys[j] + " -> " + mValues[j]);
                }

                throw new RuntimeException();
            }
        }
    }

    private long[] mKeys;
    private Object[] mValues;
    private int mSize;

    public static final class ArrayUtils {
        private static Object[] EMPTY = new Object[0];
        private static final int CACHE_SIZE = 73;
        private static Object[] sCache = new Object[CACHE_SIZE];

        private ArrayUtils() { /* cannot be instantiated */
        }

        public static int idealByteArraySize(int need) {
            for (int i = 4; i < 32; i++)
                if (need <= (1 << i) - 12)
                    return (1 << i) - 12;

            return need;
        }

        public static int idealBooleanArraySize(int need) {
            return idealByteArraySize(need);
        }

        public static int idealShortArraySize(int need) {
            return idealByteArraySize(need * 2) / 2;
        }

        public static int idealCharArraySize(int need) {
            return idealByteArraySize(need * 2) / 2;
        }

        public static int idealIntArraySize(int need) {
            return idealByteArraySize(need * 4) / 4;
        }

        public static int idealFloatArraySize(int need) {
            return idealByteArraySize(need * 4) / 4;
        }

        public static int idealObjectArraySize(int need) {
            return idealByteArraySize(need * 4) / 4;
        }

        public static int idealLongArraySize(int need) {
            return idealByteArraySize(need * 8) / 8;
        }

        /**
         * Checks if the beginnings of two byte arrays are equal.
         * 
         * @param array1
         *            the first byte array
         * @param array2
         *            the second byte array
         * @param length
         *            the number of bytes to check
         * @return true if they're equal, false otherwise
         */
        public static boolean equals(byte[] array1, byte[] array2, int length) {
            if (array1 == array2) {
                return true;
            }
            if (array1 == null || array2 == null || array1.length < length || array2.length < length) {
                return false;
            }
            for (int i = 0; i < length; i++) {
                if (array1[i] != array2[i]) {
                    return false;
                }
            }
            return true;
        }

        /**
         * Returns an empty array of the specified type. The intent is that it
         * will return the same empty array every time to avoid reallocation,
         * although this is not guaranteed.
         */
        @SuppressWarnings("unchecked")
        public static <T> T[] emptyArray(Class<T> kind) {
            if (kind == Object.class) {
                return (T[]) EMPTY;
            }

            int bucket = ((System.identityHashCode(kind) / 8) & 0x7FFFFFFF) % CACHE_SIZE;
            Object cache = sCache[bucket];

            if (cache == null || cache.getClass().getComponentType() != kind) {
                cache = Array.newInstance(kind, 0);
                sCache[bucket] = cache;

                // Log.e("cache", "new empty " + kind.getName() + " at " +
                // bucket);
            }

            return (T[]) cache;
        }

        /**
         * Checks that value is present as at least one of the elements of the
         * array.
         * 
         * @param array
         *            the array to check in
         * @param value
         *            the value to check for
         * @return true if the value is present in the array
         */
        public static <T> boolean contains(T[] array, T value) {
            for (T element : array) {
                if (element == null) {
                    if (value == null)
                        return true;
                } else {
                    if (value != null && element.equals(value))
                        return true;
                }
            }
            return false;
        }
    }
}