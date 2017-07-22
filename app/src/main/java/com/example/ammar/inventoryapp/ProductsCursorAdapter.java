package com.example.ammar.inventoryapp;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ammar.inventoryapp.data.ProductContracts.ProductEnty;

import static android.R.attr.id;

public class ProductsCursorAdapter extends CursorAdapter {

    /**
     * constructs a new {@link ProductsCursorAdapter}
     *
     * @param context context of the app.
     * @param cursor  the cursor from which the app get the data.
     */
    public ProductsCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    Context newViewContext;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        this.newViewContext = context;
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView productImageView = (ImageView) view.findViewById(R.id.product_image);
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        Button saleButton = (Button) view.findViewById(R.id.product_sale);

        int idColumnIndex = cursor.getColumnIndex(ProductEnty._ID);
        int imageColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_IMAGE);
        int nameColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_QUANTITY);

        byte[] imageByteArray = cursor.getBlob(imageColumnIndex);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        productImageView.setImageBitmap(bitmapImage);
        nameTextView.setText(cursor.getString(nameColumnIndex));
        priceTextView.setText(Double.toString(cursor.getDouble(priceColumnIndex)) + "$");
        quantityTextView.setText(Integer.toString(cursor.getInt(quantityColumnIndex)));

        saleButton.setTag(R.id.id, cursor.getInt(idColumnIndex));
        saleButton.setTag(R.id.cursor, cursor);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = (Cursor) v.getTag(R.id.cursor);
                int id = (Integer) v.getTag(R.id.id);
                saleDone(id, cursor);
            }
        });
    }

    private void saleDone(int id, Cursor cursor) {

        Log.e("cursor / id ", cursor.toString() + "/" +id);
        cursor.moveToFirst();
        int quantityColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_QUANTITY);
        int quantity = cursor.getInt(quantityColumnIndex);
        if (quantity > 0) {
            quantity = quantity - 1;
            ContentValues values = new ContentValues();
            values.put(ProductEnty.COLUMN_PRODUCT_QUANTITY, quantity);
            Uri currentProductUri = ContentUris.withAppendedId(ProductEnty.CONTENT_URI, id);
            newViewContext.getContentResolver().update(currentProductUri, values, null, null);
        }else {
            Toast.makeText(newViewContext, R.string.quantity_zero, Toast.LENGTH_LONG).show();
        }
    }

}


