package com.aueb.opabus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aueb.opabus.CodeFolder.DataTypes.Topic;
import com.aueb.opabus.CodeFolder.DataTypes.Value;
import com.aueb.opabus.CodeFolder.Subscriber;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Maps extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private Button Maps_Button_Back;
    private String BusForSearch;
    public Subscriber su = new Subscriber();
    private MarkerOptions markerOptions = new MarkerOptions();
    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Initialize();
        Maps.AsyncTaskRunner runner = new Maps.AsyncTaskRunner();
        runner.execute();
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Maps_map);
        mapFragment.getMapAsync( this);
        BusForSearch=getIntent().getStringExtra("BusForSearch");
        Toast.makeText(getApplicationContext(),BusForSearch,Toast.LENGTH_SHORT).show();
        Maps_Button_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backToMain = new Intent(getApplicationContext(),MainActivity.class);
                startActivityForResult(backToMain,0);
            }
        });
    }


    private class AsyncTaskRunner extends AsyncTask<Subscriber,String,Subscriber>{
        ProgressDialog progressDialog;
        protected void onPostExecute(Subscriber subscriber){
            progressDialog.dismiss();
        }
        @Override
        protected Subscriber doInBackground(Subscriber... subscribers) {
            su.setBrokerIp("192.168.1.72");
            su.setBrokerport(4202);
            su.EstablishConnection();
            Thread t = new Thread(()->{
                System.out.println("searching for: "+ BusForSearch);
                su.register(new Topic(BusForSearch));
            });
            t.start();
            return null;
        }
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(Maps.this,"Initializing Connection","Connecting... ");
        }
        protected void onProgressUpdate(String... text){

        }
    }

    public void drawMarker(GoogleMap googleMap){
        if(googleMap!=null){
            googleMap.clear();
            LatLng gps = new LatLng(su.valueArrayList.get(0).getLatidude(),su.valueArrayList.get(0).getLatidude());
            googleMap.addMarker(new MarkerOptions().position(gps).title("bill"));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Thread t = new Thread(()->{
            while (su.value.getLatidude()!=0.0){
            LatLng temp =new LatLng(su.value.getLatidude(),su.value.getLongtitude());
            mMap.addMarker(new MarkerOptions().position(temp).title("myBus"));
        }
//        });
//        t.start();

        latLngs.add(new LatLng(37.973278,23.71061));
        latLngs.add(new LatLng(37.973278,23.71062));
        latLngs.add(new LatLng(39.250488,21.571842));
        int i=1;
        for (LatLng point :latLngs){
            markerOptions.position(point);
            markerOptions.title("point: "+ i);
            markerOptions.snippet("perigrafi");
            markers.add(mMap.addMarker(markerOptions));
            i++;
        }
        i=0;
        for(Marker m: markers){
            m.setTag(i);
            i++;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
//            mMap.addMarker(new MarkerOptions().position(Xvrio).title("Vacetion"));
//            Marker m = mMap.addMarker(new MarkerOptions().position(spiti).title("Marker in My home"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(spiti));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(spiti,12.0f));
//            mMap.addPolyline(new PolylineOptions().add(spiti,Xvrio).width(5).color(Color.RED));
    }


    private void Initialize(){
        Maps_Button_Back=(Button) findViewById(R.id.Maps_Button_Back);
    }
}
