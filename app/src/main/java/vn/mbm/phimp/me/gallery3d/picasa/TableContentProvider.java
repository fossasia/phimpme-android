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

package vn.mbm.phimp.me.gallery3d.picasa;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

public class TableContentProvider extends ContentProvider {
    private static final String NULL_COLUMN_HACK = "_id";
    protected SQLiteOpenHelper mDatabase = null;
    private final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private final ArrayList<Mapping> mMappings = new ArrayList<Mapping>();

    public void setDatabase(SQLiteOpenHelper database) {
        mDatabase = database;
    }

    public void addMapping(String authority, String path, String mimeSubtype, EntrySchema table) {
        // Add the table URI mapping.
        ArrayList<Mapping> mappings = mMappings;
        UriMatcher matcher = mUriMatcher;
        matcher.addURI(authority, path, mappings.size());
        mappings.add(new Mapping(table, mimeSubtype, false));

        // Add the row URI mapping.
        matcher.addURI(authority, path + "/#", mappings.size());
        mappings.add(new Mapping(table, mimeSubtype, true));
    }

    @Override
    public boolean onCreate() {
        // The database may not be loaded yet since attachInfo() has not been
        // called, so we cannot
        // check that the database opened successfully. Returns true
        // optimistically.
        return true;
    }

    @Override
    public String getType(Uri uri) {
        // Resolve the URI.
        int match = mUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // Combine the standard type with the user-provided subtype.
        Mapping mapping = mMappings.get(match);
        String prefix = mapping.hasId ? ContentResolver.CURSOR_ITEM_BASE_TYPE : ContentResolver.CURSOR_DIR_BASE_TYPE;
        return prefix + "/" + mapping.mimeSubtype;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Resolve the URI.
        int match = mUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // Add the ID predicate if needed.
        Mapping mapping = mMappings.get(match);
        if (mapping.hasId) {
            selection = whereWithId(uri, selection);
        }

        // System.out.println("QUERY " + uri + " WHERE (" + selection + ")");

        // Run the query.
        String tableName = mapping.table.getTableName();
        Cursor cursor = mDatabase.getReadableDatabase().query(tableName, projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Resolve the URI.
        int match = mUriMatcher.match(uri);
        Mapping mapping = match != UriMatcher.NO_MATCH ? mMappings.get(match) : null;
        if (mapping == null || mapping.hasId) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // Insert into the database, notify observers, and return the qualified
        // URI.
        String tableName = mapping.table.getTableName();
        long rowId = mDatabase.getWritableDatabase().insert(tableName, NULL_COLUMN_HACK, values);
        if (rowId > 0) {
            notifyChange(uri);
            return Uri.withAppendedPath(uri, Long.toString(rowId));
        } else {
            throw new SQLException("Failed to insert row at: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        // Resolve the URI.
        int match = mUriMatcher.match(uri);
        Mapping mapping = match != UriMatcher.NO_MATCH ? mMappings.get(match) : null;
        if (mapping == null || mapping.hasId) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // Insert all rows into the database and notify observers.
        String tableName = mapping.table.getTableName();
        SQLiteDatabase database = mDatabase.getWritableDatabase();
        int numInserted = 0;
        try {
            int length = values.length;
            database.beginTransaction();
            for (int i = 0; i != length; ++i) {
                database.insert(tableName, NULL_COLUMN_HACK, values[i]);
            }
            database.setTransactionSuccessful();
            numInserted = length;
        } finally {
            database.endTransaction();
        }
        notifyChange(uri);
        return numInserted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Resolve the URI.
        int match = mUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // Add the ID predicate if needed.
        Mapping mapping = mMappings.get(match);
        if (mapping.hasId) {
            selection = whereWithId(uri, selection);
        }

        // Update the item(s) and broadcast a change notification.
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        String tableName = mapping.table.getTableName();
        int count = db.update(tableName, values, selection, selectionArgs);
        notifyChange(uri);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Resolve the URI.
        int match = mUriMatcher.match(uri);
        if (match == UriMatcher.NO_MATCH) {
            throw new IllegalArgumentException("Invalid URI: " + uri);
        }

        // Add the ID predicate if needed.
        Mapping mapping = mMappings.get(match);
        if (mapping.hasId) {
            selection = whereWithId(uri, selection);
        }

        // Delete the item(s) and broadcast a change notification.
        SQLiteDatabase db = mDatabase.getWritableDatabase();
        String tableName = mapping.table.getTableName();
        int count = db.delete(tableName, selection, selectionArgs);
        notifyChange(uri);
        return count;
    }

    private final String whereWithId(Uri uri, String selection) {
        String id = uri.getPathSegments().get(1);
        StringBuilder where = new StringBuilder("_id=");
        where.append(id);
        if (!TextUtils.isEmpty(selection)) {
            where.append(" AND (");
            where.append(selection);
            where.append(')');
        }
        return where.toString();
    }

    private final void notifyChange(Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    private static final class Mapping {
        public EntrySchema table;
        public String mimeSubtype;
        public boolean hasId;

        public Mapping(EntrySchema table, String mimeSubtype, boolean hasId) {
            this.table = table;
            this.mimeSubtype = mimeSubtype;
            this.hasId = hasId;
        }
    }
}
