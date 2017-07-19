package com.example.ammar.inventoryapp;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ammar.inventoryapp.data.ProductsDbHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProductsDbHelper dbHelper = new ProductsDbHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
    }
}