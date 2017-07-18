package com.example.ammar.inventoryapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.ammar.inventoryapp.data.ProductContracts.ProductEnty;

public class ProductsDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ProductsDbHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "products.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of{@link ProductsDbHelper}
     *
     * @param context of the app
     */
    public ProductsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATES_PRODUCTS_TABLE =
                "CREATE TABLE " + ProductEnty.TABLE_NAME + " ("
                + ProductEnty._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductEnty.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductEnty.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + ProductEnty.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + ProductEnty.COLUMN_PRODUCT_SUPPLIER + " INTEGER NOT NULL );";
        db.execSQL(SQL_CREATES_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
