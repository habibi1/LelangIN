package com.tugasmobile.lelangin.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.tugasmobile.lelangin.database.DatabaseContract.AUTHORITY;
import static com.tugasmobile.lelangin.database.DatabaseContract.CONTENT_URI;

public class ProductProvider extends ContentProvider {

    static final int PRODUCT = 1;
    static final int PRODUCT_ID = 2;
    ProductHelper productHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, DatabaseContract.NAMA_TABEL, PRODUCT);
        uriMatcher.addURI(AUTHORITY, DatabaseContract.NAMA_TABEL + "/#", PRODUCT_ID);
    }


    @Override
    public boolean onCreate() {
        productHelper = new ProductHelper(getContext());
        productHelper.open();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case PRODUCT:
                cursor = productHelper.queryProvider();
                break;
            case PRODUCT_ID:
                cursor = productHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            default:
                cursor = null;
                break;
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int update;

        switch (uriMatcher.match(uri)) {
            case PRODUCT_ID:
                update = productHelper.update(uri.getLastPathSegment(), contentValues);
                break;
            default:
                update = 0;
                break;
        }

        if (update > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long add;

        switch (uriMatcher.match(uri)) {
            case PRODUCT:
                add = productHelper.insert(contentValues);
                break;
            default:
                add = 0;
                break;
        }

        if (add > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return Uri.parse(CONTENT_URI + "/" + add);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int delete;

        switch (uriMatcher.match(uri)) {
            case PRODUCT_ID:
                delete = productHelper.delete(uri.getLastPathSegment());
                break;
            default:
                delete = 0;
                break;
        }

        if (delete > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return delete;
    }

}
