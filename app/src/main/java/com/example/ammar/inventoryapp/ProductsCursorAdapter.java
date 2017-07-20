package com.example.ammar.inventoryapp;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ammar.inventoryapp.data.ProductContracts.ProductEnty;

import static android.R.attr.bitmap;

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

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView productImageView = (ImageView) view.findViewById(R.id.product_image);
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_quantity);
        Button saleButton = (Button) view.findViewById(R.id.product_sale);

        int imageColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_IMAGE);
        int nameColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEnty.COLUMN_PRODUCT_QUANTITY);

        byte[] imageByteArray = cursor.getBlob(imageColumnIndex);
        Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
        productImageView.setImageBitmap(bitmapImage);

        nameTextView.setText(cursor.getString(nameColumnIndex));
        priceTextView.setText(Double.toString(cursor.getDouble(priceColumnIndex)));
        quantityTextView.setText(Integer.toString(cursor.getInt(quantityColumnIndex)));

        //TODO do the button implementation

    }
}
