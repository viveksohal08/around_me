package com.example.vivek.nearby;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GoogleMapsActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    Button btnShowInMap;
    GoogleApiClient gac;
    Location loc;
    SupportMapFragment smf;
    GoogleMap gm;
    String place;
    String service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        smf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        btnShowInMap = (Button) findViewById(R.id.btnShowInMap);

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);
        builder.addApi(LocationServices.API);
        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        gac = builder.build();

        smf.getMapAsync(this);

        Intent i = getIntent();
        place = i.getStringExtra("place");
        service = i.getStringExtra("service");

        btnShowInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = 0.0, lon = 0.0;
                Geocoder g = new Geocoder(GoogleMapsActivity.this, Locale.ENGLISH);
                try {
                    List<Address> addressList = g.getFromLocationName(place, 1);
                    Address address = addressList.get(0);
                    lat = address.getLatitude();
                    lon = address.getLongitude();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                LatLng latLng = new LatLng(lat, lon);
                gm.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Nearby position"));
                gm.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gm.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        loc = LocationServices.FusedLocationApi.getLastLocation(gac);
        if (loc != null) {
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            gm.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            gm.addMarker(new MarkerOptions().position(latLng).draggable(true).title("Current position"));
            gm.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        } else {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspend", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (gac != null) {
            gac.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (gac != null) {
            gac.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gm = googleMap;
        LatLng latLng = new LatLng(-31, 111);
        gm.addMarker(new MarkerOptions().position(latLng));
        gm.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }
}
