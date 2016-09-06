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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import vn.mbm.phimp.me.gallery3d.cache.CacheService;
import android.util.Log;

public final class DiskCache {
    private static final String TAG = "DiskCache";
    private static final int CHUNK_SIZE = 1048576; // 1MB.
    private static final int INDEX_HEADER_MAGIC = 0xcafe;
    private static final int INDEX_HEADER_VERSION = 2;
    private static final String INDEX_FILE_NAME = "index";
    private static final String CHUNK_FILE_PREFIX = "chunk_";
    private final String mCacheDirectoryPath;
    private LongSparseArray<Record> mIndexMap;
    private final LongSparseArray<RandomAccessFile> mChunkFiles = new LongSparseArray<RandomAccessFile>();
    private int mTailChunk = 0;
    private int mNumInsertions = 0;

    public DiskCache(String cacheDirectoryName) {
        String cacheDirectoryPath = CacheService.getCachePath(cacheDirectoryName);

        // Create the cache directory if needed.
        File cacheDirectory = new File(cacheDirectoryPath);
        if (!cacheDirectory.isDirectory() && !cacheDirectory.mkdirs()) {
            Log.e(TAG, "Unable to create cache directory " + cacheDirectoryPath);
        }
        mCacheDirectoryPath = cacheDirectoryPath;
        loadIndex();
    }

    @Override
    public void finalize() {
        shutdown();
    }

    public byte[] get(long key, long timestamp) {
        // Look up the record for the given key.
        Record record = null;
        synchronized (mIndexMap) {
            record = mIndexMap.get(key);
        }
        if (record != null) {
            // Read the chunk from the file.
            if (record.timestamp < timestamp) {
                Log.i(TAG, "File has been updated to " + timestamp + " since the last time " + record.timestamp
                        + " stored in cache.");
                return null;
            }
            try {
                RandomAccessFile chunkFile = getChunkFile(record.chunk);
                if (chunkFile != null) {
                    byte[] data = new byte[record.size];
                    chunkFile.seek(record.offset);
                    chunkFile.readFully(data);
                    return data;
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to read from chunk file");
            }
        }
        return null;
    }

    public boolean isDataAvailable(long key, long timestamp) {
        Record record = null;
        synchronized (mIndexMap) {
            record = mIndexMap.get(key);
        }
        if (record == null) {
            return false;
        }
        if (record.timestamp < timestamp) {
            return false;
        }
        if (record.size == 0)
            return false;
        return true;
    }

    public void put(long key, byte[] data, long timestamp) {
        // Check to see if the record already exists.
        Record record = null;
        synchronized (mIndexMap) {
            record = mIndexMap.get(key);
        }
        if (record != null && data.length <= record.sizeOnDisk) {
            // We just replace the chunk.
            int currentChunk = record.chunk;
            try {
                RandomAccessFile chunkFile = getChunkFile(record.chunk);
                if (chunkFile != null) {
                    chunkFile.seek(record.offset);
                    chunkFile.write(data);
                    synchronized (mIndexMap) {
                        mIndexMap.put(key, new Record(currentChunk, record.offset, data.length, record.sizeOnDisk, timestamp));
                    }
                    if (++mNumInsertions == 32) { // CR: 32 => constant
                        // Flush the index file at a regular interval. To avoid
                        // writing the entire
                        // index each time the format could be changed to an
                        // append-only journal with
                        // a snapshot generated on exit.
                        flush();
                    }
                    return;
                }
            } catch (Exception e) {
                Log.e(TAG, "Unable to read from chunk file");
            }
        }
        // Append a new chunk to the current chunk.
        final int chunk = mTailChunk;
        final RandomAccessFile chunkFile = getChunkFile(chunk);
        if (chunkFile != null) {
            try {
                final int offset = (int) chunkFile.length();
                chunkFile.seek(offset);
                chunkFile.write(data);
                synchronized (mIndexMap) {
                    mIndexMap.put(key, new Record(chunk, offset, data.length, data.length, timestamp));
                }
                if (offset + data.length > CHUNK_SIZE) {
                    ++mTailChunk;
                }

                if (++mNumInsertions == 32) { // CR: 32 => constant
                    // Flush the index file at a regular interval. To avoid
                    // writing the entire
                    // index each time the format could be changed to an
                    // append-only journal with
                    // a snapshot generated on exit.
                    flush();
                }
            } catch (IOException e) {
                Log.e(TAG, "Unable to write new entry to chunk file");
            }
        } else {
            Log.e(TAG, "getChunkFile() returned null");
        }
    }

    public void delete(long key) {
        synchronized (mIndexMap) {
            mIndexMap.remove(key);
        }
    }

    public void deleteAll() {
        // Close all open files and clear data structures.
        shutdown();

        // Delete all cache files.
        File cacheDirectory = new File(mCacheDirectoryPath);
        String[] cacheFiles = cacheDirectory.list();
        if (cacheFiles == null)
            return;
        for (String cacheFile : cacheFiles) {
            new File(cacheDirectory, cacheFile).delete();
        }
    }

    public void flush() {
        if (mNumInsertions != 0) {
            mNumInsertions = 0;
            writeIndex();
        }
    }

    public void close() {
        writeIndex();
        shutdown();
    }

    private void shutdown() {
        synchronized (mChunkFiles) {
            for (int i = 0, size = mChunkFiles.size(); i < size; ++i) {
                try {
                    mChunkFiles.valueAt(i).close();
                } catch (Exception e) {
                    Log.e(TAG, "Unable to close chunk file");
                }
            }
            mChunkFiles.clear();
        }
        if (mIndexMap != null) {
            synchronized (mIndexMap) {
                if (mIndexMap != null) {
                    mIndexMap.clear();
                }
            }
        }
    }

    private String getIndexFilePath() {
        return mCacheDirectoryPath + INDEX_FILE_NAME;
    }

    private void loadIndex() {
        final String indexFilePath = getIndexFilePath();
        try {
            // Open the input stream.
            final FileInputStream fileInput = new FileInputStream(indexFilePath);
            final BufferedInputStream bufferedInput = new BufferedInputStream(fileInput, 1024);
            final DataInputStream dataInput = new DataInputStream(bufferedInput);

            // Read the header.
            final int magic = dataInput.readInt();
            final int version = dataInput.readInt();
            boolean valid = true;
            if (magic != INDEX_HEADER_MAGIC) {
                Log.e(TAG, "Index file appears to be corrupt (" + magic + " != " + INDEX_HEADER_MAGIC + "), " + indexFilePath);
                valid = false;
            }
            if (valid && version != INDEX_HEADER_VERSION) {
                // Future versions can implement upgrade in this case.
                Log.e(TAG, "Index file version " + version + " not supported");
                valid = false;
            }
            if (valid) {
                mTailChunk = dataInput.readShort();
            }

            // Read the entries.
            if (valid) {
                // Parse the index file body into the in-memory map.
                final int numEntries = dataInput.readInt();
                mIndexMap = new LongSparseArray<Record>(numEntries);
                synchronized (mIndexMap) {
                    for (int i = 0; i < numEntries; ++i) {
                        final long key = dataInput.readLong();
                        final int chunk = dataInput.readShort();
                        final int offset = dataInput.readInt();
                        final int size = dataInput.readInt();
                        final int sizeOnDisk = dataInput.readInt();
                        final long timestamp = dataInput.readLong();
                        mIndexMap.append(key, new Record(chunk, offset, size, sizeOnDisk, timestamp));
                    }
                }
            }

            dataInput.close();
            if (!valid) {
                deleteAll();
            }

        } catch (FileNotFoundException e) {
            // If the file does not exist the cache is empty, so just continue.
        } catch (IOException e) {
            Log.e(TAG, "Unable to read the index file " + indexFilePath);
        } finally {
            if (mIndexMap == null) {
                mIndexMap = new LongSparseArray<Record>();
            }
        }
    }

    private void writeIndex() {
        File tempFile = null;
        final String tempFilePath = mCacheDirectoryPath;
        final String indexFilePath = getIndexFilePath();
        try {
            tempFile = File.createTempFile("DiskCache", null, new File(tempFilePath));
        } catch (Exception e) {
            Log.e(TAG, "Unable to create or tempFile " + tempFilePath);
            return;
        }
        try {
            final FileOutputStream fileOutput = new FileOutputStream(tempFile);
            final BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput, 1024);
            final DataOutputStream dataOutput = new DataOutputStream(bufferedOutput);

            // Write the index header.
            final int numRecords = mIndexMap.size();
            dataOutput.writeInt(INDEX_HEADER_MAGIC);
            dataOutput.writeInt(INDEX_HEADER_VERSION);
            dataOutput.writeShort(mTailChunk);
            dataOutput.writeInt(numRecords);

            // Write the records.
            for (int i = 0; i < numRecords; ++i) {
                final long key = mIndexMap.keyAt(i);
                final Record record = mIndexMap.valueAt(i);
                dataOutput.writeLong(key);
                dataOutput.writeShort(record.chunk);
                dataOutput.writeInt(record.offset);
                dataOutput.writeInt(record.size);
                dataOutput.writeInt(record.sizeOnDisk);
                dataOutput.writeLong(record.timestamp);
            }

            // Close the file.
            dataOutput.close();

            // Log.d(TAG, "Wrote index with " + numRecords + " records.");

            // Atomically overwrite the old index file.
            tempFile.renameTo(new File(indexFilePath));
        } catch (Exception e) {
            // Was unable to perform the operation, we delete the temp file
            Log.e(TAG, "Unable to write the index file " + indexFilePath);
            tempFile.delete();
        }
    }

    private RandomAccessFile getChunkFile(int chunk) {
        RandomAccessFile chunkFile = null;
        synchronized (mChunkFiles) {
            chunkFile = mChunkFiles.get(chunk);
        }
        if (chunkFile == null) {
            final String chunkFilePath = mCacheDirectoryPath + CHUNK_FILE_PREFIX + chunk;
            try {
                chunkFile = new RandomAccessFile(chunkFilePath, "rw");
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Unable to create or open the chunk file " + chunkFilePath);
            }
            synchronized (mChunkFiles) {
                mChunkFiles.put(chunk, chunkFile);
            }
        }
        return chunkFile;
    }

    private static final class Record {
        public Record(int chunk, int offset, int size, int sizeOnDisk, long timestamp) {
            this.chunk = chunk;
            this.offset = offset;
            this.size = size;
            this.timestamp = timestamp;
            this.sizeOnDisk = sizeOnDisk;
        }

        public final long timestamp;
        public final int chunk;
        public final int offset;
        public final int size;
        public final int sizeOnDisk;
    }
}
