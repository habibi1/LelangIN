package com.tugasmobile.lelangin.database;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static java.security.AccessController.getContext;

public class DatabaseContract {

    public static String NAMA_TABEL = "favorite";

    public static final class FavoriteColumns implements BaseColumns {
        public static String product_name = "name";
        public static String product_owner = "owner";
        public static String product_description = "description";
        public static String product_image = "image";
    }

    public static String AUTHORITY = "com.tugasmobile.lelangin";

    public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
            .authority(AUTHORITY)
            .appendPath(NAMA_TABEL)
            .build();

    public static String getColumnString(Cursor cursor, String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static int getColumnInt(Cursor cursor, String columnName) {
        return cursor.getInt(cursor.getColumnIndex(columnName));
    }

    public static Double getColumnDouble(Cursor cursor, String columnName) {
        return cursor.getDouble(cursor.getColumnIndex(columnName));
    }

    public static Long getColumnLong(Cursor cursor, String columnName) {
        return cursor.getLong(cursor.getColumnIndex(columnName));
    }
}
