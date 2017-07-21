package com.example.ammar.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ammar.inventoryapp.data.ProductContracts.ProductEnty;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {

    private static final String TAG = "permission method: ";
    private static int RESULT_LOAD_IMAGE = 1;
    Bitmap imageBitmap = null;
    private EditText name, price, quantity, supplier;
    private ImageView image;
    private boolean productHasChanged = false;

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Button saveProductButton = (Button) findViewById(R.id.new_product_save_button);
        Button addImage = (Button) findViewById(R.id.new_product_image_button);
        name = (EditText) findViewById(R.id.new_product_name);
        price = (EditText) findViewById(R.id.new_product_price);
        quantity = (EditText) findViewById(R.id.new_product_quantity);
        supplier = (EditText) findViewById(R.id.new_product_supplier);
        image = (ImageView) findViewById(R.id.new_product_image);

        name.setOnTouchListener(onTouchListener);
        price.setOnTouchListener(onTouchListener);
        quantity.setOnTouchListener(onTouchListener);
        supplier.setOnTouchListener(onTouchListener);
        addImage.setOnTouchListener(onTouchListener);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

        saveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    private void saveProduct() {
        String nameString = name.getText().toString().trim();
        String supplierNumber = supplier.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(AddProductActivity.this, R.string.product_require_name, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(price.getText().toString().trim())) {
            Toast.makeText(AddProductActivity.this, R.string.product_require_price, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(quantity.getText().toString().trim())) {
            Toast.makeText(AddProductActivity.this, R.string.Product_require_quantity, Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(supplierNumber)) {
            Toast.makeText(AddProductActivity.this, R.string.product_require_supplier, Toast.LENGTH_SHORT).show();
            return;
        } else if (imageBitmap == null) {
            Toast.makeText(AddProductActivity.this, R.string.product_require_image, Toast.LENGTH_SHORT).show();
            return;
        }
        //putting the two variable after checking the edit text is not empty to prevent app crash
        Double priceDouble = Double.parseDouble(price.getText().toString().trim());
        Integer quantityInt = Integer.parseInt(quantity.getText().toString().trim());
        if (priceDouble < 0) {
            Toast.makeText(AddProductActivity.this, getString(R.string.negative_price_error), Toast.LENGTH_SHORT).show();
            return;
        } else if (quantityInt < 0) {
            Toast.makeText(AddProductActivity.this, R.string.negative_quantity_input, Toast.LENGTH_SHORT).show();
            return;
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageByteArray = stream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ProductEnty.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEnty.COLUMN_PRODUCT_PRICE, priceDouble);
        values.put(ProductEnty.COLUMN_PRODUCT_QUANTITY, quantityInt);
        values.put(ProductEnty.COLUMN_PRODUCT_SUPPLIER, supplierNumber);
        values.put(ProductEnty.COLUMN_PRODUCT_IMAGE, imageByteArray);

        Uri newUri = getContentResolver().insert(ProductEnty.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(AddProductActivity.this,
                    R.string.insert_failed, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AddProductActivity.this,
                    R.string.insert_successful, Toast.LENGTH_SHORT).show();
        }
        finish();

    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions
                        (this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                imageBitmap = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
            }
            image.setImageBitmap(imageBitmap);

        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
