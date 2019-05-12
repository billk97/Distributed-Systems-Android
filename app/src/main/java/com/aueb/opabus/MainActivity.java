package com.aueb.opabus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.aueb.opabus.CodeFolder.Subscriber;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ImageView Main_ImageView_Bus;
    private EditText Main_editText_Search;
    private Button Main_button_Next;
    private ImageButton Main_imageButton_search;
    public Subscriber su = new Subscriber();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initializer();
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
        //

        Main_button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToMaps =new Intent(getApplicationContext(),Maps.class);
                startActivityForResult(goToMaps,0);
            }
        });
    }
    private void Initializer(){
        Main_ImageView_Bus=(ImageView) findViewById(R.id.Main_ImageView_Bus);
        Main_editText_Search=(EditText) findViewById(R.id.Main_editText_Search);
        Main_button_Next=(Button) findViewById(R.id.Main_button_Next);
        Main_imageButton_search= (ImageButton) findViewById(R.id.Main_imageButton_search);
    }
    private class AsyncTaskRunner extends AsyncTask<Subscriber,String,Subscriber>{
        ProgressDialog progressDialog;

        protected void onPostExecute(Subscriber sub){
            Log.e("tag",sub.BrokerList.get(0).brokerBusList.get(0)[0]);
            progressDialog.dismiss();
        }

        @Override
        protected Subscriber doInBackground(Subscriber... myObject) {
            Log.e("tag","bill");
            su.setBrokerIp("192.168.1.72");
            su.setBrokerport(4202);
            su.EstablishConnection();
            su.disconnect();
            return su;
        }

        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(MainActivity.this,"Initializing Connection","Connecting... ");
        }
        protected void onProgressUpdate(String... text){

        }

    }
}
