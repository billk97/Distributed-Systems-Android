package com.aueb.opabus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aueb.opabus.CodeFolder.DataTypes.Topic;
import com.aueb.opabus.CodeFolder.Subscriber;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private Button Maps_Button_Back;
    private String BusForSearch;
    public Subscriber su = new Subscriber();
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng bus = new LatLng(37.973278,23.71061);
        mMap.addMarker(new MarkerOptions().position(bus).title("Bus map"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bus));
    }
    private void Initialize(){
        Maps_Button_Back=(Button) findViewById(R.id.Maps_Button_Back);
    }
}
