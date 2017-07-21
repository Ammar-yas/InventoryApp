package com.example.ammar.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ammar.inventoryapp.data.ProductContracts.ProductEnty;

import com.example.ammar.inventoryapp.data.ProductsDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    ProductsCursorAdapter productsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.main_add_product);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        ListView productList = (ListView) findViewById(R.id.products_list);
        TextView instructions = (TextView) findViewById(R.id.products_insert_instructions);
        productList.setEmptyView(instructions);
        productsAdapter = new ProductsCursorAdapter(this, null);
        productList.setAdapter(productsAdapter);
        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEnty.CONTENT_URI, id);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEnty._ID,
                ProductEnty.COLUMN_PRODUCT_IMAGE,
                ProductEnty.COLUMN_PRODUCT_NAME,
                ProductEnty.COLUMN_PRODUCT_QUANTITY,
                ProductEnty.COLUMN_PRODUCT_PRICE};
        return new CursorLoader(this, ProductEnty.CONTENT_URI,
                projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productsAdapter.swapCursor(null);
    }
}