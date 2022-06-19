package com.example.finalassignmentandroid;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetailsViewMarker extends AppCompatActivity implements OnMapReadyCallback {


    private static final int AUTOCOMPLETE_REQUEST_CODE = 100;
    private final List<MarkerModel> markerModelList = new ArrayList<>();
    SQLiteDatabase sqLiteDatabase;
    private MarkerModel markerModel;
    TextView city , address ,change_btn , change_city;
    private GoogleMap mMap;
    Marker marker;
    FloatingActionButton isVisited;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_view_marker);

        Places.initialize(getApplicationContext(),"AIzaSyCZzuYsD-YRcvK8pT6MDXLPnwCLNoCe-kU");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        sqLiteDatabase = openOrCreateDatabase("products_db", MODE_PRIVATE, null);


       loadData();

    }

    private void loadData() {
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

        change_city = findViewById(R.id.change_city_btn);
        change_btn = findViewById(R.id.change_address_btn);

        change_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsViewMarker.this);

                LayoutInflater inflater = LayoutInflater.from(DetailsViewMarker.this);
                View v = inflater.inflate(R.layout.dialog_update_marker, null);
                builder.setView(v);

                AlertDialog dialog = builder.create();


                EditText cityEt = v.findViewById(R.id.et_city);

                cityEt.setText(markerModel.geCity());


                v.findViewById(R.id.btn_update_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String city = cityEt.getText().toString().trim();

                        if (city.isEmpty()) {
                            cityEt.setError("city field is empty");
                            cityEt.requestFocus();
                            return;
                        }



                        String sql = "UPDATE markers SET city = ?  WHERE id = ? ";
                        sqLiteDatabase.execSQL(sql,
                                new String[]{
                                        city,
                                        String.valueOf(markerModel.getId())
                                });

                        loadData();
                        dialog.dismiss();


                    }


                });
                dialog.show();
            }
        });
        change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                markerModel.setLat(place.getLatLng().latitude);
                markerModel.setLng(place.getLatLng().longitude);
                markerModel.setAddress(place.getAddress());

                if (place.getLatLng().latitude != 0) {
                    LatLng latLng = new LatLng(place.getLatLng().latitude,
                            place.getLatLng().longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                }

                String sql = "UPDATE markers SET latitude = ? , longitude = ? , address = ?  WHERE id = ? ";
                sqLiteDatabase.execSQL(sql,
                        new String[]{
                                String.valueOf(markerModel.getLat()),
                                String.valueOf(markerModel.getLng()),
                                String.valueOf(markerModel.getAddress()),
                                String.valueOf(markerModel.getId())

                        });
                loadData();
                onMapReady(mMap);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
               // Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}