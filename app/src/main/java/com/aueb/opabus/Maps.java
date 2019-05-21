package com.aueb.opabus;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Maps extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private Button Maps_Button_Back;
    private String BusForSearch;
    public Subscriber su = new Subscriber();
    private MarkerOptions markerOptions = new MarkerOptions();
    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private boolean initialized = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Initialize();
        Maps.AsyncTaskRunner runner = new Maps.AsyncTaskRunner();
        runner.execute();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(initialized) drawMarker(mMap);
            }
        },0, 1000);
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
            /*final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                drawMarker(mMap);
                System.out.println("runs");
            }, 1000);*/
        }
        @Override
        protected Subscriber doInBackground(Subscriber... subscribers) {
            su.setBrokerIp("172.20.1.71");
            su.setBrokerport(4202);
            su.EstablishConnection();
            Thread t = new Thread(()->{
                System.out.println("searching for: "+ BusForSearch);
                su.register(new Topic(BusForSearch));
            });
            t.start();
            initialized = true;
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
            //googleMap.clear();
                while(su.valueArrayList.size()<=0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                LatLng gps = new LatLng(su.value.getLatidude(),su.value.getLongtitude());
                googleMap.addMarker(new MarkerOptions().position(gps).title("bill "));

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        latLngs.add(new LatLng(37.973278,23.71061));
        latLngs.add(new LatLng(37.973278,23.71062));
        latLngs.add(new LatLng(39.250488,21.57184));
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
            return;
        }
        mMap.setMyLocationEnabled(true);
    }


    private void Initialize(){
        Maps_Button_Back=(Button) findViewById(R.id.Maps_Button_Back);
    }
}
