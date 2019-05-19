package com.aueb.opabus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aueb.opabus.CodeFolder.Subscriber;

import java.util.ArrayList;

import static android.R.layout.simple_expandable_list_item_1;

public class MainActivity extends AppCompatActivity {
    private ImageView Main_ImageView_Bus;
    private EditText Main_editText_Search;
    private Button Main_button_Next;
    private ImageButton Main_imageButton_search;
    public Subscriber su = new Subscriber();
    public String BusForSearch=null;
    private String BusLineCode=null;
    public static android.widget.ListView Main_ListView_SelectRoute;
    private   ArrayList<String> BusDescriptionBackwards=null;
    private Boolean SearchPreased =false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initializer();
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute();
        Main_button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SearchPreased==true&&!BusForSearch.equals("")){
                    Intent goToMaps =new Intent(getApplicationContext(),Maps.class);
                    goToMaps.putExtra("BusForSearch",BusForSearch);
                    startActivityForResult(goToMaps,0);
                }else {
                    Toast.makeText(getApplicationContext(),"Please select a bus and press Search",Toast.LENGTH_SHORT).show();

                }
            }
        });

        Main_imageButton_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchPreased=true;
                BusForSearch = Main_editText_Search.getText().toString();
                Log.e("BusForSearch ",BusForSearch);
                BusLineCode=su.BusLineIdToLineCode(BusForSearch);
                Log.e("BusId",BusLineCode);
                String BusDescription=su.BusLineIdToDescriptionEnglish(BusForSearch);
                Toast.makeText(getApplicationContext(),BusDescription,Toast.LENGTH_SHORT).show();
                BusDescriptionBackwards= su.LineCodeToRouteCodeDesrcription(BusLineCode);
                System.out.println();
                System.out.println();
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), simple_expandable_list_item_1,BusDescriptionBackwards);
                Main_ListView_SelectRoute.setAdapter(arrayAdapter);

            }
        });
    }

    private void Initializer(){
        Main_ListView_SelectRoute=(android.widget.ListView)findViewById(R.id.Main_ListVIew_SelectRoute);
        Main_ListView_SelectRoute.setVerticalScrollBarEnabled(true);
        Main_ImageView_Bus=(ImageView) findViewById(R.id.Main_ImageView_Bus);
        Main_editText_Search=(EditText) findViewById(R.id.Main_editText_Search);
        Main_button_Next=(Button) findViewById(R.id.Main_button_Next);
        Main_imageButton_search= (ImageButton) findViewById(R.id.Main_imageButton_search);
    }
    private class AsyncTaskRunner extends AsyncTask<Subscriber,String,Subscriber>{
        ProgressDialog progressDialog;

        protected void onPostExecute(Subscriber sub){
            Log.e("tag",sub.BrokerList.get(0).brokerBusList.get(0)[0]);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), simple_expandable_list_item_1,su.AvailableBuses());
            Main_ListView_SelectRoute.setAdapter(arrayAdapter);
            progressDialog.dismiss();
        }

        @Override
        protected Subscriber doInBackground(Subscriber... myObject) {
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
