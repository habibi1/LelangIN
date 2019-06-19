package com.tugasmobile.lelangin.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.tugasmobile.lelangin.model.Product;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_description;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_image;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_name;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_owner;
import static com.tugasmobile.lelangin.database.DatabaseContract.NAMA_TABEL;

public class ProductHelper {

    private Context context;
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public ProductHelper(Context context) {
        this.context = context;
    }

    public ProductHelper open() throws SQLException {
        databaseHelper = new DatabaseHelper(context);
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public ArrayList<Product> query() {
        ArrayList<Product> arrayList = new ArrayList<>();
        Cursor cursor = database.query(NAMA_TABEL,
                null,
                null,
                null,
                null,
                null,
                _ID + " DESC",
                null);

        cursor.moveToFirst();
        Product productModel;

        if (cursor.getCount() > 0) {
            do {
                productModel = new Product();

                productModel.setId(cursor.getString(cursor.getColumnIndexOrThrow(_ID)));
                productModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(product_name)));
                productModel.setOwner(cursor.getString(cursor.getColumnIndexOrThrow(product_owner)));
                productModel.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(product_description)));
                productModel.setImage(cursor.getString(cursor.getColumnIndexOrThrow(product_image)));

                arrayList.add(productModel);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }

        cursor.close();
        return arrayList;
    }

    public Cursor queryByIdProvider(String id) {
        return database.query(NAMA_TABEL,
                null,
                _ID + " = ?",
                new String[]{id},
                null,
                null,
                null,
                null);
    }

    public Cursor queryProvider() {
        return database.query(NAMA_TABEL,
                null,
                null,
                null,
                null,
                null,
                _ID + " DESC");
    }

    public long insert(ContentValues values) {
        return database.insert(NAMA_TABEL, null, values);
    }

    public int update(String id, ContentValues values) {
        return database.update(NAMA_TABEL, values, _ID + " ?", new String[]{id});
    }

    public int delete(String id) {
        return database.delete(NAMA_TABEL, _ID + " = ?", new String[]{id});
    }

}
