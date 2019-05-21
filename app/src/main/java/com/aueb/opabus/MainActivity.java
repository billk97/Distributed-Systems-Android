package com.aueb.opabus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private String BrokerIp="192.168.1.72";
    private int BrokerPort=4202;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.menu_BrokerIp:
                //your action
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set Brokers Ip");
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);
                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BrokerIp = input.getText().toString();
                        Toast.makeText(getApplicationContext(),BrokerIp,Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initializer();
        MainAsyncTaskRunner runner = new MainAsyncTaskRunner();
        runner.execute();
        Main_button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!BusForSearch.equals("")){
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
                BusForSearch = Main_editText_Search.getText().toString();
                Log.e("BusForSearch ",BusForSearch);
                BusLineCode=su.BusLineIdToLineCode(BusForSearch);
                Log.e("BusId",BusLineCode);
                String BusDescription=su.BusLineIdToDescriptionEnglish(BusForSearch);
                Toast.makeText(getApplicationContext(),BusDescription,Toast.LENGTH_SHORT).show();
                BusDescriptionBackwards= su.LineCodeToRouteCodeDesrcription(BusLineCode);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), simple_expandable_list_item_1,BusDescriptionBackwards);
                Main_ListView_SelectRoute.setAdapter(arrayAdapter);

            }
        });
        Main_ListView_SelectRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selectedBusLine =(String) (Main_ListView_SelectRoute.getItemAtPosition(position));
                Toast.makeText(getApplicationContext(),"Selected: "+BusForSearch,Toast.LENGTH_SHORT).show();
                BusForSearch=LinetoId(selectedBusLine);
                Log.e("selectedLineCode: ",BusForSearch);
                BusLineCode=su.BusLineIdToLineCode(BusForSearch);
                Log.e("BusId",BusLineCode);
                BusDescriptionBackwards= su.LineCodeToRouteCodeDesrcription(BusLineCode);
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), simple_expandable_list_item_1,BusDescriptionBackwards);
                Main_ListView_SelectRoute.setAdapter(arrayAdapter);

            }
        });
    }
    /**this function takes a string and returns the first word only**/
    private String LinetoId(String line) {
        String arr[] = line.split(" ", 2);
        arr[0].replaceAll("\\s+","");
        return arr[0];
    }

    private void Initializer(){
        Main_ListView_SelectRoute=(android.widget.ListView)findViewById(R.id.Main_ListVIew_SelectRoute);
        Main_ListView_SelectRoute.setVerticalScrollBarEnabled(true);
        Main_ImageView_Bus=(ImageView) findViewById(R.id.Main_ImageView_Bus);
        Main_editText_Search=(EditText) findViewById(R.id.Main_editText_Search);
        Main_button_Next=(Button) findViewById(R.id.Main_button_Next);
        Main_imageButton_search= (ImageButton) findViewById(R.id.Main_imageButton_search);
    }

    private class MainAsyncTaskRunner extends AsyncTask<Subscriber,String,Subscriber> {
        ProgressDialog progressDialog;

        protected void onPostExecute(Subscriber sub){
            Log.e("tag",sub.BrokerList.get(0).brokerBusList.get(0)[0]);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), simple_expandable_list_item_1,su.AvailableBuses());
            Main_ListView_SelectRoute.setAdapter(arrayAdapter);
            progressDialog.dismiss();
        }

        @Override
        protected Subscriber doInBackground(Subscriber... myObject) {
            su.setBrokerIp(BrokerIp);
            su.setBrokerport(BrokerPort);
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
