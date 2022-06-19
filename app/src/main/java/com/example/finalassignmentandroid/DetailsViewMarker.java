package com.example.finalassignmentandroid;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DetailsViewMarker extends AppCompatActivity implements OnMapReadyCallback {


    private final List<MarkerModel> markerModelList = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;
    private MarkerModel markerModel;
    TextView city , address ;
    private GoogleMap mMap;
    Marker marker;
    FloatingActionButton isVisited;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view_marker);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sqLiteDatabase = openOrCreateDatabase("products_db", MODE_PRIVATE, null);


        Intent i = getIntent();
        Double lat = i.getDoubleExtra("lat", 0);
        Double lng = i.getDoubleExtra("lng", 0);
        int id = i.getIntExtra("id", 0);
        System.out.println("lat : " + lat + "long : " + lng + "----" + id);

        String sql = "SELECT *  FROM markers WHERE id = ? ";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,
                new String[]{
                        String.valueOf(id)});


        if (cursor.moveToFirst()) {
            do {
                // fetch info from columns
                markerModel =
                        new MarkerModel(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getDouble(3),
                                cursor.getDouble(4),
                                cursor.getInt(5)

                        );

            } while (cursor.moveToNext());
            cursor.close();
        }
        //setMarker(new LatLng(lat, lng));
        System.out.println(markerModel);
        city = findViewById(R.id.detail_city);
        address = findViewById(R.id.detail_address);

        city.setText(markerModel.geCity());
        address.setText(markerModel.getAddress());


        isVisited  = findViewById(R.id.isVisited_btn);
        if (markerModel.isVisited() == 1 )
        {
            isVisited.setVisibility(View.GONE);
        }


        isVisited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sql = "UPDATE markers SET isvisited = ?  WHERE id = ? ";
                sqLiteDatabase.execSQL(sql,
                        new String[]{
                                String.valueOf(1) ,
                                String.valueOf(id)
                        });

                isVisited.setVisibility(View.GONE);
            }


        });




    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {


        mMap = googleMap;

        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(markerModel.getLat() , markerModel.getLng()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        options.draggable(false);
        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom((new LatLng(markerModel.getLat() , markerModel.getLng()))
                , 13));

    }
}