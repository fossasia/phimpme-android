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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public final class EntrySchema {
    public static final int TYPE_STRING = 0;
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_SHORT = 2;
    public static final int TYPE_INT = 3;
    public static final int TYPE_LONG = 4;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_DOUBLE = 6;
    public static final int TYPE_BLOB = 7;
    public static final String SQLITE_TYPES[] = { "TEXT", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "REAL", "REAL", "NONE" };

    private static final String TAG = "SchemaInfo";
    private static final String FULL_TEXT_INDEX_SUFFIX = "_fulltext";

    private final String mTableName;
    private final ColumnInfo[] mColumnInfo;
    private final String[] mProjection;
    private final boolean mHasFullTextIndex;

    public EntrySchema(Class<? extends Entry> clazz) {
        // Get table and column metadata from reflection.
        ColumnInfo[] columns = parseColumnInfo(clazz);
        mTableName = parseTableName(clazz);
        mColumnInfo = columns;

        // Cache the list of projection columns and check for full-text columns.
        String[] projection = {};
        boolean hasFullTextIndex = false;
        if (columns != null) {
            projection = new String[columns.length];
            for (int i = 0; i != columns.length; ++i) {
                ColumnInfo column = columns[i];
                projection[i] = column.name;
                if (column.fullText) {
                    hasFullTextIndex = true;
                }
            }
        }
        mProjection = projection;
        mHasFullTextIndex = hasFullTextIndex;
    }

    public String getTableName() {
        return mTableName;
    }

    public ColumnInfo[] getColumnInfo() {
        return mColumnInfo;
    }

    public String[] getProjection() {
        return mProjection;
    }

    private void logExecSql(SQLiteDatabase db, String sql) {
        // Log.i(TAG, sql);
        db.execSQL(sql);
    }

    public void cursorToObject(Cursor cursor, Entry object) {
        try {
            ColumnInfo[] columns = mColumnInfo;
            for (int i = 0, size = columns.length; i != size; ++i) {
                ColumnInfo column = columns[i];
                int columnIndex = column.projectionIndex;
                Field field = column.field;
                switch (column.type) {
                case TYPE_STRING:
                    field.set(object, cursor.getString(columnIndex));
                    break;
                case TYPE_BOOLEAN:
                    field.setBoolean(object, cursor.getShort(columnIndex) == 1);
                    break;
                case TYPE_SHORT:
                    field.setShort(object, cursor.getShort(columnIndex));
                    break;
                case TYPE_INT:
                    field.setInt(object, cursor.getInt(columnIndex));
                    break;
                case TYPE_LONG:
                    field.setLong(object, cursor.getLong(columnIndex));
                    break;
                case TYPE_FLOAT:
                    field.setFloat(object, cursor.getFloat(columnIndex));
                    break;
                case TYPE_DOUBLE:
                    field.setDouble(object, cursor.getDouble(columnIndex));
                    break;
                case TYPE_BLOB:
                    field.set(object, cursor.getBlob(columnIndex));
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "SchemaInfo.setFromCursor: object not of the right type");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "SchemaInfo.setFromCursor: field not accessible");
        }
    }

    public void objectToValues(Entry object, ContentValues values) {
        try {
            ColumnInfo[] columns = mColumnInfo;
            for (int i = 0, size = columns.length; i != size; ++i) {
                ColumnInfo column = columns[i];
                String columnName = column.name;
                Field field = column.field;
                switch (column.type) {
                case TYPE_STRING:
                    values.put(columnName, (String) field.get(object));
                    break;
                case TYPE_BOOLEAN:
                    values.put(columnName, field.getBoolean(object));
                    break;
                case TYPE_SHORT:
                    values.put(columnName, field.getShort(object));
                    break;
                case TYPE_INT:
                    values.put(columnName, field.getInt(object));
                    break;
                case TYPE_LONG:
                    values.put(columnName, field.getLong(object));
                    break;
                case TYPE_FLOAT:
                    values.put(columnName, field.getFloat(object));
                    break;
                case TYPE_DOUBLE:
                    values.put(columnName, field.getDouble(object));
                    break;
                case TYPE_BLOB:
                    values.put(columnName, (byte[]) field.get(object));
                    break;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "SchemaInfo.setFromCursor: object not of the right type");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "SchemaInfo.setFromCursor: field not accessible");
        }
    }

    public Cursor queryAll(SQLiteDatabase db) {
        return db.query(mTableName, mProjection, null, null, null, null, null);
    }

    public boolean queryWithId(SQLiteDatabase db, long id, Entry entry) {
        Cursor cursor = db.query(mTableName, mProjection, "_id=?", new String[] { Long.toString(id) }, null, null, null);
        boolean success = false;
        if (cursor.moveToFirst()) {
            cursorToObject(cursor, entry);
            success = true;
        }
        cursor.close();
        return success;
    }

    public long insertOrReplace(SQLiteDatabase db, Entry entry) {
        ContentValues values = new ContentValues();
        objectToValues(entry, values);
        if (entry.id == 0) {
            Log.i(TAG, "removing id before insert");
            values.remove("_id");
        }
        long id = db.replace(mTableName, "_id", values);
        entry.id = id;
        return id;
    }

    public boolean deleteWithId(SQLiteDatabase db, long id) {
        return db.delete(mTableName, "_id=?", new String[] { Long.toString(id) }) == 1;
    }

    public void createTables(SQLiteDatabase db) {
        // Wrapped class must have a @Table.Definition.
        String tableName = mTableName;
        if (tableName == null) {
            return;
        }

        // Add the CREATE TABLE statement for the main table.
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(tableName);
        sql.append(" (_id INTEGER PRIMARY KEY");
        ColumnInfo[] columns = mColumnInfo;
        int numColumns = columns.length;
        for (int i = 0; i != numColumns; ++i) {
            ColumnInfo column = columns[i];
            if (!column.isId()) {
                sql.append(',');
                sql.append(column.name);
                sql.append(' ');
                sql.append(SQLITE_TYPES[column.type]);
                if (column.extraSql != null) {
                    sql.append(' ');
                    sql.append(column.extraSql);
                }
            }
        }
        sql.append(");");
        logExecSql(db, sql.toString());
        sql.setLength(0);

        // Create indexes for all indexed columns.
        for (int i = 0; i != numColumns; ++i) {
            // Create an index on the indexed columns.
            ColumnInfo column = columns[i];
            if (column.indexed) {
                sql.append("CREATE INDEX ");
                sql.append(tableName);
                sql.append("_index_");
                sql.append(column.name);
                sql.append(" ON ");
                sql.append(tableName);
                sql.append(" (");
                sql.append(column.name);
                sql.append(");");
                logExecSql(db, sql.toString());
                sql.setLength(0);
            }
        }

        if (mHasFullTextIndex) {
            // Add an FTS virtual table if using full-text search.
            String ftsTableName = tableName + FULL_TEXT_INDEX_SUFFIX;
            sql.append("CREATE VIRTUAL TABLE ");
            sql.append(ftsTableName);
            sql.append(" USING FTS3 (_id INTEGER PRIMARY KEY");
            for (int i = 0; i != numColumns; ++i) {
                ColumnInfo column = columns[i];
                if (column.fullText) {
                    // Add the column to the FTS table.
                    String columnName = column.name;
                    sql.append(',');
                    sql.append(columnName);
                    sql.append(" TEXT");
                }
            }
            sql.append(");");
            logExecSql(db, sql.toString());
            sql.setLength(0);

            // Build an insert statement that will automatically keep the FTS
            // table in sync.
            StringBuilder insertSql = new StringBuilder("INSERT OR REPLACE INTO ");
            insertSql.append(ftsTableName);
            insertSql.append(" (_id");
            for (int i = 0; i != numColumns; ++i) {
                ColumnInfo column = columns[i];
                if (column.fullText) {
                    insertSql.append(',');
                    insertSql.append(column.name);
                }
            }
            insertSql.append(") VALUES (new._id");
            for (int i = 0; i != numColumns; ++i) {
                ColumnInfo column = columns[i];
                if (column.fullText) {
                    insertSql.append(",new.");
                    insertSql.append(column.name);
                }
            }
            insertSql.append(");");
            String insertSqlString = insertSql.toString();

            // Add an insert trigger.
            sql.append("CREATE TRIGGER ");
            sql.append(tableName);
            sql.append("_insert_trigger AFTER INSERT ON ");
            sql.append(tableName);
            sql.append(" FOR EACH ROW BEGIN ");
            sql.append(insertSqlString);
            sql.append("END;");
            logExecSql(db, sql.toString());
            sql.setLength(0);

            // Add an update trigger.
            sql.append("CREATE TRIGGER ");
            sql.append(tableName);
            sql.append("_update_trigger AFTER UPDATE ON ");
            sql.append(tableName);
            sql.append(" FOR EACH ROW BEGIN ");
            sql.append(insertSqlString);
            sql.append("END;");
            logExecSql(db, sql.toString());
            sql.setLength(0);

            // Add a delete trigger.
            sql.append("CREATE TRIGGER ");
            sql.append(tableName);
            sql.append("_delete_trigger AFTER DELETE ON ");
            sql.append(tableName);
            sql.append(" FOR EACH ROW BEGIN DELETE FROM ");
            sql.append(ftsTableName);
            sql.append(" WHERE _id = old._id; END;");
            logExecSql(db, sql.toString());
            sql.setLength(0);
        }
    }

    public void dropTables(SQLiteDatabase db) {
        String tableName = mTableName;
        StringBuilder sql = new StringBuilder("DROP TABLE IF EXISTS ");
        sql.append(tableName);
        sql.append(';');
        logExecSql(db, sql.toString());
        sql.setLength(0);

        if (mHasFullTextIndex) {
            sql.append("DROP TABLE IF EXISTS ");
            sql.append(tableName);
            sql.append(FULL_TEXT_INDEX_SUFFIX);
            sql.append(';');
            logExecSql(db, sql.toString());
        }

    }

    public void deleteAll(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        sql.append(mTableName);
        sql.append(";");
        logExecSql(db, sql.toString());
    }

    private String parseTableName(Class<? extends Object> clazz) {
        // Check for a table annotation.
        Entry.Table table = clazz.getAnnotation(Entry.Table.class);
        if (table == null) {
            return null;
        }

        // Return the table name.
        return table.value();
    }

    private ColumnInfo[] parseColumnInfo(Class<? extends Object> clazz) {
        // Gather metadata from each annotated field.
        ArrayList<ColumnInfo> columns = new ArrayList<ColumnInfo>();
        Field[] fields = clazz.getFields();
        for (int i = 0; i != fields.length; ++i) {
            // Get column metadata from the annotation.
            Field field = fields[i];
            Entry.Column info = ((AnnotatedElement) field).getAnnotation(Entry.Column.class);
            if (info == null) {
                continue;
            }

            // Determine the field type.
            int type;
            Class<?> fieldType = field.getType();
            if (fieldType == String.class) {
                type = TYPE_STRING;
            } else if (fieldType == boolean.class) {
                type = TYPE_BOOLEAN;
            } else if (fieldType == short.class) {
                type = TYPE_SHORT;
            } else if (fieldType == int.class) {
                type = TYPE_INT;
            } else if (fieldType == long.class) {
                type = TYPE_LONG;
            } else if (fieldType == float.class) {
                type = TYPE_FLOAT;
            } else if (fieldType == double.class) {
                type = TYPE_DOUBLE;
            } else if (fieldType == byte[].class) {
                type = TYPE_BLOB;
            } else {
                throw new IllegalArgumentException("Unsupported field type for column: " + fieldType.getName());
            }

            // Add the column to the array.
            int index = columns.size();
            columns.add(new ColumnInfo(info.value(), type, info.indexed(), info.fullText(), field, index));
        }

        // Return a list.
        ColumnInfo[] columnList = new ColumnInfo[columns.size()];
        columns.toArray(columnList);
        return columnList;
    }

    public static final class ColumnInfo {
        public final String name;
        public final int type;
        public final boolean indexed;
        public final boolean fullText;
        public final String extraSql = "";
        public final Field field;
        public final int projectionIndex;

        public ColumnInfo(String name, int type, boolean indexed, boolean fullText, Field field, int projectionIndex) {
            this.name = name.toLowerCase();
            this.type = type;
            this.indexed = indexed;
            this.fullText = fullText;
            this.field = field;
            this.projectionIndex = projectionIndex;
        }

        public boolean isId() {
            return name == "_id";
        }
    }
}
