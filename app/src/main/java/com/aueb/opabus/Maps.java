package com.aueb.opabus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends AppCompatActivity{
    private GoogleMap mMap;
    private Button Maps_Button_Back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Initialize();
//        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Maps_map);
//        mapFragment.getMapAsync( this);
//
        Maps_Button_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMain = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(backToMain,0);
            }
        });
    }
//
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        LatLng bus = new LatLng(37.973278,23.71061);
//        mMap.addMarker(new MarkerOptions().position(bus).title("Bus map"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(bus));
//    }
    private void Initialize(){
        Maps_Button_Back=(Button) findViewById(R.id.Maps_Button_Back);
    }
}
