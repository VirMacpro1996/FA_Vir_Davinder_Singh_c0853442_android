package com.example.finalassignmentandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalassignmentandroid.databinding.ActivityMarkerBinding;

import java.util.ArrayList;
import java.util.List;

public class MarkerActivity extends AppCompatActivity {

    private static final String FILE_KEY = "users";
    SQLiteDatabase sqLiteDatabase;
    private ActivityMarkerBinding binding;
    private SharedPreferences sharedPreferences;
    private List<MarkerModel> markerModelList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMarkerBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        sharedPreferences = this.getSharedPreferences(this.FILE_KEY, Context.MODE_PRIVATE);
        sqLiteDatabase = openOrCreateDatabase("products_db", MODE_PRIVATE, null);
        createTables();
        // load data from db


        if (sharedPreferences.getBoolean("is_first_turn", true)) {
            addDb();
        }


        loadMarkers();

        findViewById(R.id.add_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(i);

            }
        });



    }

    private void createTables() {

        String sql = "CREATE TABLE IF NOT EXISTS markers (" +
                "id INTEGER NOT NULL CONSTRAINT markers_pk PRIMARY KEY AUTOINCREMENT , " +
                "city VARCHAR(20) NOT NULL ,  " +
                "address VARCHAR(20) NOT NULL ,  " +
                "latitude DOUBLE NOT NULL , " +
                "longitude DOUBLE NOT NULL , " +
                "isVisited INTEGER NOT NULL );";

        sqLiteDatabase.execSQL(sql);
    }

    private void isFirst(boolean data) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_first_turn", data);
        editor.apply();
    }

    private void loadMarkers() {
        markerModelList = new ArrayList<>();
        /// read data from db

        String sql = "SELECT *  FROM markers";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                // fetch info from columns
                markerModelList.add(
                        new MarkerModel(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                cursor.getDouble(4),
                                cursor.getInt(5)

                        )
                );

            } while (cursor.moveToNext());
            cursor.close();
        }
        MarkerAdapter productAdapter = new MarkerAdapter(
                this,
                R.layout.list_layout_marker,
                markerModelList,
                sqLiteDatabase,
                new Runnable() {
                    @Override
                    public void run() {
                        loadMarkers();
                    }
                }
        );
        binding.lvMarkers.setAdapter(productAdapter);
    }

    private void addDb() {

        String sql1 = "INSERT INTO markers( city, address  , latitude ,longitude , isvisited )" +
                "VALUES (?,?,?,?,?)";
        sqLiteDatabase.execSQL(sql1, new String[]{ "Toronto", "CN Tower","43.6425662", "-79.3892455", "0"});
        String sql2 = "INSERT INTO markers( city, address  , latitude ,longitude , isvisited )" +
                "VALUES (?,?,?,?,?)";
        sqLiteDatabase.execSQL(sql2, new String[]{ "Toronto", "Queens Park","43.6672424", "-79.395718", "0"});

        String sql3 = "INSERT INTO markers( city, address  , latitude ,longitude , isvisited )" +
                "VALUES (?,?,?,?,?)";
        sqLiteDatabase.execSQL(sql3, new String[]{ "Vancouver", "New Brighton Beach","49.301334", "-123.2285811", "0"});
        isFirst(false);

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadMarkers();
    }
}
