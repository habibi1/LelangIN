package com.tugasmobile.lelangin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_description;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_image;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_name;
import static com.tugasmobile.lelangin.database.DatabaseContract.FavoriteColumns.product_owner;
import static com.tugasmobile.lelangin.database.DatabaseContract.NAMA_TABEL;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_TABLE = "product";
    private static final int VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_TABLE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NAMA_TABEL);
        onCreate(sqLiteDatabase);
    }

    private static final String TABLE = String.format("CREATE TABLE %s" +
                    "(%s TEXT NOT NULL PRIMARY KEY, " +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL)" ,
            NAMA_TABEL,
            _ID,
            product_name,
            product_owner,
            product_description,
            product_image
    );
}
