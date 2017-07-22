package com.example.ammar.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ammar.inventoryapp.data.ProductContracts.ProductEnty;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri currentUri;
    private int quantityInt;

    private ImageView imageView;
    private TextView name, price, quantity;
    private Button callSupplier, deleteProduct, addProduct, removeProduct;
    private static final int EXISTING_PRODUCT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        currentUri = intent.getData();

        imageView = (ImageView) findViewById(R.id.detail_product_image);
        name = (TextView) findViewById(R.id.detail_product_name);
        price = (TextView) findViewById(R.id.detail_product_price);
        quantity = (TextView) findViewById(R.id.detail_product_quantity);
        callSupplier = (Button) findViewById(R.id.detail_call_supplier);
        deleteProduct = (Button) findViewById(R.id.detail_delete_product);
        addProduct = (Button) findViewById(R.id.detail_add_product);
        removeProduct = (Button) findViewById(R.id.detail_remove_product);

        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projecttions = {
                ProductEnty._ID,
                ProductEnty.COLUMN_PRODUCT_NAME,
                ProductEnty.COLUMN_PRODUCT_PRICE,
                ProductEnty.COLUMN_PRODUCT_IMAGE,
                ProductEnty.COLUMN_PRODUCT_QUANTITY,
                ProductEnty.COLUMN_PRODUCT_SUPPLIER};
        return new CursorLoader(this, currentUri, projecttions,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(ProductEnty.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(ProductEnty.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = data.getColumnIndex(ProductEnty.COLUMN_PRODUCT_IMAGE);
            int quantityColumnIndex = data.getColumnIndex(ProductEnty.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = data.getColumnIndex(ProductEnty.COLUMN_PRODUCT_SUPPLIER);

            String nameString = data.getString(nameColumnIndex);
            Double priceDouble = data.getDouble(priceColumnIndex);
            byte[] imageByteArray = data.getBlob(imageColumnIndex);
            quantityInt = data.getInt(quantityColumnIndex);
            final String supplierNumber = data.getString(supplierColumnIndex);

            name.setText(nameString);
            price.setText(priceDouble + "$");
            quantity.setText("" + quantityInt);
            Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            imageView.setImageBitmap(bitmapImage);
            callSupplier.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + supplierNumber));
                    startActivity(intent);
                }
            });
            addProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addProductMehtod();
                }
            });
            removeProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeProductMethod();
                }
            });

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        name.setText("");
        price.setText("");
        quantity.setText("");
        imageView.setImageResource(0);
        callSupplier.setOnClickListener(null);
        deleteProduct.setOnClickListener(null);
        addProduct.setOnClickListener(null);
        removeProduct.setOnClickListener(null);
    }

    private void addProductMehtod() {
        quantityInt = quantityInt + 1;
        ContentValues values = new ContentValues();
        values.put(ProductEnty.COLUMN_PRODUCT_QUANTITY, quantityInt);
        int rowsAffected = getContentResolver().update(currentUri, values, null, null);
        if (rowsAffected == 0) {
            Toast.makeText(this, "failed to increase quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeProductMethod() {
        if (quantityInt > 0) {
            quantityInt = quantityInt - 1;
            ContentValues values = new ContentValues();
            values.put(ProductEnty.COLUMN_PRODUCT_QUANTITY, quantityInt);
            getContentResolver().update(currentUri, values, null, null);
        } else {
            Toast.makeText(this, R.string.below_zero_quantity_error, Toast.LENGTH_SHORT).show();

        }
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentUri != null) {
            int rowsDeleted = getContentResolver().delete(currentUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, R.string.delete_successful, Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
