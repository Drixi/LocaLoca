package com.example.group3.localoca;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainMenu extends Activity {

    Bitmap bitmap;
    ProgressDialog pDialog;
    ImageView img;
    Button DownloadImage;
    private ListView lvFloors;
    List<String> FloorList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        lvFloors = (ListView)findViewById(R.id.lvBuildings);
        img = (ImageView)findViewById(R.id.imgVFace);
        DownloadImage = (Button)findViewById(R.id.btnGetImage);
        DownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new LoadImage().execute("http://ampitere.eu/nurses/1.jpg");
            }
        });
        lvPopulate();
        lvClick();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainMenu.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();
        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
        protected void onPostExecute(Bitmap image) {
            if(image != null){
                img.setImageBitmap(image);
                pDialog.dismiss();
            }else{
                pDialog.dismiss();
                Toast.makeText(MainMenu.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void lvPopulate() {

        FloorList.add("Frederikskaj 6");
        FloorList.add("Frederikskaj 10A");
        FloorList.add("Frederikskaj 10B");
        FloorList.add("Frederikskaj 12");
        FloorList.add("A.C. Meyer Vænge 15");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, FloorList );

        lvFloors.setAdapter(arrayAdapter);
    }

    public void lvClick(){
        lvFloors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = lvFloors.getItemAtPosition(position);
                lvFloors.setAdapter(null);
                FloorList.add("New List Item");
                lvPopulate();
                Toast.makeText(getBaseContext(), o.toString() + " " + id, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
