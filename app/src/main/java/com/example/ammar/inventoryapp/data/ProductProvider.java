package com.example.ammar.inventoryapp.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ammar.inventoryapp.data.ProductContracts.ProductEnty;

public class ProductProvider extends ContentProvider {

    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCTS = 100;
    private static final int PRODUCTS_ID = 101;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContracts.CONTENT_AUTHORITY, ProductContracts.PATH_PRODUCTS, PRODUCTS);
        uriMatcher.addURI(ProductContracts.CONTENT_AUTHORITY, ProductContracts.PATH_PRODUCTS + "/#", PRODUCTS_ID);
    }

    private ProductsDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new ProductsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEnty.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCTS_ID:
                selection = ProductEnty._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEnty.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //Notify cursor update
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProducts(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported fot" + uri);
        }
    }

    private Uri insertProducts(Uri uri, ContentValues values) {
        String name = values.getAsString(ProductEnty.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        Double price = values.getAsDouble(ProductEnty.COLUMN_PRODUCT_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Products requires a price");
        }
        byte[] image = values.getAsByteArray(ProductEnty.COLUMN_PRODUCT_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Product require Image");
        }
        Integer quantity = values.getAsInteger(ProductEnty.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product require quantity greater than or equal to zero");
        }
        String supplierNumber = values.getAsString(ProductEnty.COLUMN_PRODUCT_SUPPLIER);
        if (supplierNumber == null) {
            throw new IllegalArgumentException("Product require supplier number");
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = database.insert(ProductEnty.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert the Product" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProducts(uri, values, selection, selectionArgs);
            case PRODUCTS_ID:
                selection = ProductEnty._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProducts(uri, values, selection, selectionArgs);
        }
        return 0;
    }

    private int updateProducts(Uri uri, ContentValues values, String selection, String[] selectionArgs) {


        if (values.containsKey(ProductEnty.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEnty.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        if (values.containsKey(ProductEnty.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(ProductEnty.COLUMN_PRODUCT_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Products requires a price");
            }
        }
        if (values.containsKey(ProductEnty.COLUMN_PRODUCT_IMAGE)) {
            byte[] image = values.getAsByteArray(ProductEnty.COLUMN_PRODUCT_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Product require Image");
            }
        }
        if (values.containsKey(ProductEnty.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEnty.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Product require quantity greater than or equal to zero");
            }
        }
        if (values.containsKey(ProductEnty.COLUMN_PRODUCT_SUPPLIER)) {
            String supplierNumber = values.getAsString(ProductEnty.COLUMN_PRODUCT_SUPPLIER);
            if (supplierNumber == null) {
                throw new IllegalArgumentException("Product require supplier number");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductEnty.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductEnty.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCTS_ID:
                selection = ProductEnty._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEnty.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEnty.CONTENT_LIST_TYPE;
            case PRODUCTS_ID:
                return ProductEnty.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}
