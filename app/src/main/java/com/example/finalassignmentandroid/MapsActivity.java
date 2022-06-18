package com.example.finalassignmentandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.finalassignmentandroid.databinding.ActivityMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.finalassignmentandroid.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private static final int REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private static final long UPDATE_INTERVAL = 5000;
    private static final long FASTEST_INTERVAL = 3000;
    int mName = 0;


    private FusedLocationProviderClient mClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private final List<Marker> markerList = new ArrayList<>();
    private List<MarkerModel> markerModelList = new ArrayList<>();
    private MarkerModel markerModel ;

    private Marker userMarker, favMarker , visitedMarker ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mClient = LocationServices.getFusedLocationProviderClient(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (!isGrantedlocationPermission()) {
            requestLocationPermission();
        } else {
            startUpdatingLocation();
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {


                setMarker(latLng);
               String address =  getAddress(latLng);
               String city = getCity(latLng);
                MarkerModel markerModel = new MarkerModel(0, latLng.latitude, latLng.longitude, city , address , 0);
                 markerModelList.add(markerModel);





            }


        });


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker markers) {
                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                List<Address> addresses = null; //1 num of possible location returned
                String address ,city ,country , postalCode ;
                try {
                    addresses = geocoder.getFromLocation(markers.getPosition().latitude, markers.getPosition().longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(Objects.requireNonNull(addresses).isEmpty())
                {
                    address = "date";
                    city = country = postalCode = "";
                }
                else {

                    address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
                     city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                     country = addresses.get(0).getCountryName();
                     postalCode = addresses.get(0).getPostalCode();

                }
                //create your custom title
                String title = address ;
                markers.setTitle(title);
               markers.setSnippet(postalCode + " , " + city + " , " + country);
                markers.showInfoWindow();
                return true;
            }
        });
    }

    private String getCity(LatLng latLng) {

        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> addresses = null; //1 num of possible location returned
        String address ,city ,country , postalCode ;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(Objects.requireNonNull(addresses).isEmpty())
        {
            address = "date";
            city = country = postalCode = "";
        }
        else {



            address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
            city = addresses.get(0).getLocality();

            //MarkerModel markerModel = new MarkerModel(0, latlng.latitude, latlng.longitude, city , false);
            // markerModelList.add(markerModel);
//            String state = addresses.get(0).getAdminArea();
//            country = addresses.get(0).getCountryName();
//            postalCode = addresses.get(0).getPostalCode();

        }

        return  city;
    }


    private String getAddress(LatLng latlng)
    {
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        List<Address> addresses = null; //1 num of possible location returned
        String address ,city ,country , postalCode ;
        try {
            addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(Objects.requireNonNull(addresses).isEmpty())
        {
            address = "date";
            city = country = postalCode = "";
        }
        else {



            address = addresses.get(0).getAddressLine(0); //0 to obtain first possible address
            city = addresses.get(0).getLocality();

            //MarkerModel markerModel = new MarkerModel(0, latlng.latitude, latlng.longitude, city , false);
           // markerModelList.add(markerModel);
//            String state = addresses.get(0).getAdminArea();
//            country = addresses.get(0).getCountryName();
//            postalCode = addresses.get(0).getPostalCode();

        }

        return  address;

    }
    private void setMarker(LatLng latLng) {

        String ch = "A";


        MarkerOptions options = new MarkerOptions()
                .position(latLng).title(ch)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("nice place");
                 options.draggable(true);
        favMarker = mMap.addMarker(options);

        favMarker.setTag(mName);
        mName++;
        markerList.add(favMarker);


    }

    @SuppressLint("MissingPermission")
    private void startUpdatingLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                Location location = locationResult.getLastLocation();
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

           userMarker = mMap.addMarker(new MarkerOptions().position(userLocation).title("Vir").snippet("your are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
            }
        };

        mClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean isGrantedlocationPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_CODE == requestCode) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this).setMessage("Accessing th locaion is mandatory ").
                        setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                            }
                        }).setNegativeButton("Cancel", null).create().show();
            } else {
                startUpdatingLocation();
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance().
                    getErrorDialog
                            (this, REQUEST_CODE, REQUEST_CODE,
                                    new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {

                                            Toast.makeText(MapsActivity.this,
                                                    "The Service is not available",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

            assert errorDialog != null;
            errorDialog.show();
        }
    }
}