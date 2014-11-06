package com.example.group3.localoca;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class RoomList extends Activity {

    Bitmap bitmap;
    ProgressDialog pDialog;
    ImageView img;
    Button DownloadImage, btnCheckLocation;
    TextView tvTest;
    private ListView lvFloors;
    boolean buildingChosen;
    long itemIDbuilding = 0;
    long itemIDfloor = 0;
    Double longitude, latitude;
    List<String> FloorList = new ArrayList<String>();
    String[] fk61st = {"203","203A","203B","203C","203D","203E","204","205",
            "207","208","209","210","211","212","213","214","215","216","217","218","219","220"};
    private LocationManager locationManager=null;
    private LocationListener locationListener=null;
    private static final String TAG = "Debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomlist);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        lvFloors = (ListView)findViewById(R.id.lvBuildings);
        img = (ImageView)findViewById(R.id.imgVFace);
        tvTest = (TextView)findViewById(R.id.tvTest);
        DownloadImage = (Button)findViewById(R.id.btnGetImage);
        btnCheckLocation = (Button)findViewById(R.id.btnLocationCheck);
        DownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //new LoadImage().execute("http://ampitere.eu/nurses/1.jpg");
                FloorList.clear();
                lvPopulate();
                itemIDfloor = 0;
                itemIDbuilding = 0;
                if(displayGpsStatus()){
                    tvTest.setText("GPS enabled");
                    locationListener = new MyLocationListener();

                    locationManager.requestLocationUpdates(LocationManager
                            .GPS_PROVIDER, 5000, 10,locationListener);

                }
                else{
                    alertbox();
                }
            }
        });
        btnCheckLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                atauu();
            }
        });
        lvPopulate();
        lvClick();
        displayGpsStatus();
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
            pDialog = new ProgressDialog(RoomList.this);
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
                Toast.makeText(RoomList.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void arrayadapter(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, FloorList );
        lvFloors.setAdapter(null);
        lvFloors.setAdapter(arrayAdapter);
    }

    public void lvPopulate() {
        FloorList.add("Frederikskaj 6");
        FloorList.add("Frederikskaj 10A");
        FloorList.add("Frederikskaj 10B");
        FloorList.add("Frederikskaj 12");
        FloorList.add("A.C. Meyer Vænge 15");
        arrayadapter();
    }

    public void lvClick(){
        lvFloors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = lvFloors.getItemAtPosition(position);
                FloorList.clear();
                if(itemIDbuilding != 0){
                    itemIDfloor = id;
                    lvRoomsPopulate();
                }
                if(itemIDbuilding == 0) {
                    itemIDbuilding = id;
                    lvFloorsPopulate();
                }

                Toast.makeText(getBaseContext(), o.toString() + " " + itemIDbuilding + " " + itemIDfloor, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void lvFloorsPopulate(){
        FloorList.add("Ground Floor");
        FloorList.add("1st Floor");
        FloorList.add("2nd Floor");
        FloorList.add("3rd Floor");
        if(itemIDbuilding == 4 || itemIDbuilding == 5) {
            FloorList.add("4rd Floor");
        }
        arrayadapter();
    }

    private void lvRoomsPopulate(){
        for(int i = 0; i<fk61st.length; i++){
            FloorList.add(fk61st[i]);
        }
        arrayadapter();
    }

    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    protected void alertbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disabled")
                .setCancelable(false)
                .setTitle("GPS Status")
                .setPositiveButton("Activate GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        //Settings.ACTION_SECURITY_SETTINGS);
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            longitude = loc.getLongitude();
            Log.v(TAG, longitude.toString());
            latitude = loc.getLatitude();
            Log.v(TAG, latitude.toString());

            String s = "Longitude: " + longitude+"\n"+  "Latitude: " + latitude;
            tvTest.setText(s);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }

    public void atauu(){
    /*
        double latStart = 55.647387;
        double latStop = 55.651175;
        double longStart = 12.539902;
        double longStop = 12.544216;
        System.out.println(latitude>latStart);
        System.out.println(latitude<latStop);
        System.out.println(longitude>longStart);
        System.out.println(longitude<latStop);
        if(latitude>latStart && latitude<latStop && longitude>longStart && longitude<latStop){
            Toast.makeText(getBaseContext(), "You are on AAU campus, Welcome", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getBaseContext(), "You are not on AAU campus at the moment", Toast.LENGTH_SHORT).show();
        }
        */

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = null;

        intent = new Intent(this, RoomList.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.locaicon).setAutoCancel(true)
                .setContentIntent(contentIntent).setContentTitle(this.getString(R.string.app_name))
                .setContentText("Welcome to AAU campus! Click to check your courses");

        // mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify((int) System.currentTimeMillis() % Integer.MAX_VALUE, mBuilder.build());
    }
}
