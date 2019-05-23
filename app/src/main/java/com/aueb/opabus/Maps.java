package com.aueb.opabus;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Maps extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private Button Maps_Button_Back;
    private String BusForSearch;
    public Subscriber su = new Subscriber();
    private MarkerOptions markerOptions = new MarkerOptions();
    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<>();
    private boolean initialized = false;
    public String MapsBrokerIp ="192.168.1.80";
    public int MapsBrokerPort=4202;
    public float [] color = new float[6];
    public int counter =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Initialize();
        AsyncTaskRunner Mrunner = new AsyncTaskRunner();
        Mrunner.execute();
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
        ProgressDialog MapsprogressDialog;
        @Override
        protected void onPostExecute(Subscriber subscriber){
            MapsprogressDialog.dismiss();
            MapsprogressDialog=null;
        }
        @Override
        protected void onCancelled(){
            MapsprogressDialog.dismiss();
        }
        @Override
        protected Subscriber doInBackground(Subscriber... subscribers) {
            su.setBrokerIp(su.getfindBroker(new Topic(BusForSearch)));
            su.setBrokerport(MapsBrokerPort);
            Thread t = new Thread(()->{
                System.out.println("searching for: "+ BusForSearch);
                su.register(new Topic(BusForSearch));
            });
            t.start();
            initialized = true;
            return null;
        }
        @Override
        protected void onPreExecute(){
            MapsprogressDialog = ProgressDialog.show(Maps.this,"Initializing Connection","Connecting... ");
        }
        @Override
        protected void onProgressUpdate(String... text){

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap=googleMap;
        final Handler handler = new Handler();
        color[0]=BitmapDescriptorFactory.HUE_CYAN;
        color[1]=BitmapDescriptorFactory.HUE_GREEN;
        color[2]=BitmapDescriptorFactory.HUE_ORANGE;
        color[3]=BitmapDescriptorFactory.HUE_RED;
        color[4]=BitmapDescriptorFactory.HUE_YELLOW;
        color[5]=BitmapDescriptorFactory.HUE_BLUE;
        HashMap <String, Float> hashMap = new HashMap<String, Float>();
        Timer timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            System.out.println("Clicked");
                            //googleMap.clear();
                            //mMap.clear();
                            for(Value v1 : su.valueArrayList){
                                    LatLng LAT = new LatLng(v1.getLatidude(),v1.getLongtitude());
                                    markerOptions.position(LAT);
                                    markerOptions.title(v1.getBus().getLineName());
                                    markerOptions.snippet("Vehicle Id: "+v1.getBus().getVehicleId());
                                    Log.e("Bus","Bus: "+ v1.getBus().getLineName() + " Vehicle Id: "+v1.getBus().getVehicleId() +" BusRouteCode "+ v1.getBus().getRouteCode());
                                    if(!hashMap.containsKey(v1.getBus().getVehicleId())){
                                        hashMap.put(v1.getBus().getVehicleId(),color[counter]);
                                        counter ++;
                                    }
                                    Log.e("counter: ",""+ counter);
                                    Log.e("hashMap",Float.toString(hashMap.get(v1.getBus().getVehicleId())));
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hashMap.get(v1.getBus().getVehicleId())));
                                    markers.add(mMap.addMarker(markerOptions));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LAT,14.2f));
                                    Polyline line = mMap.addPolyline(new PolylineOptions()
                                        .add()
                                        .width(5)
                                        .color(Color.CYAN));
                                    latLngs.add(LAT);

                            }
                            Log.e("valueArrayList:", Integer.toString(su.valueArrayList.size()));
                            int i=0;
                            for(Marker m: markers){
                                m.setTag(i);
                                i++;
                            }
                            su.valueArrayList.clear();

                        } catch (Exception e) {
                        }
                    }
                });

            }
        };
        counter=0;
        timer.schedule(doAsynchronousTask, 0, 2500); //execute in every 1 second
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

    }


    private void Initialize(){
        Maps_Button_Back=(Button) findViewById(R.id.Maps_Button_Back);
    }
}
