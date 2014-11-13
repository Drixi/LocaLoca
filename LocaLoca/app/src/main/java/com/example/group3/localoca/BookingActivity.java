package com.example.group3.localoca;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asbjørn on 07-11-2014.
 */
public class BookingActivity extends Activity{

    SharedPreferences userinfo;
    String usernr;

    HttpPost httppost;
    StringBuffer buffer;
    String response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    static String[] separated;
    ArrayList<String> list = new ArrayList<String>();

    TextView tvBooktitle, tvBookBuilding, tvBookFloor, tvBookRoom, tvBookDate, tvBookTimeStart, tvBookTimeEnd;
    Spinner sBuilding, sFloor, sRoom, sTimeStart, sTimeEnd;
    Button btnSubmitBooking;
    DatePicker dpBookDate;
    Object BuildingChosen, FloorChosen, RoomChosen, TimeStartChosen, TimeEndChosen;
    String[] buildingList = {"Choose a building", "A.C. Meyers Vænge 15","Frederikskaj 6", "Frederikskaj 10A", "Frederikskaj 10B", "Frederikskaj 12"};
    String[] FloorList = {"Choose a floor", "Ground Floor", "1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
    String[] fk61st = {"Choose a room", "203","203A","203B","203C","203D","203E","204","205",
            "207","208","209","210","211","212","213","214","215","216","217","218","219","220"};
    String[] TimeChoices = {"Choose time of day", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        usernr = userinfo.getString("userNumber", "");

        tvBookBuilding = (TextView)findViewById(R.id.tvBookBuilding);
        tvBookFloor = (TextView)findViewById(R.id.tvBookFloor);
        tvBookRoom = (TextView)findViewById(R.id.tvBookRoom);
        tvBookDate = (TextView)findViewById(R.id.tvBookDate);
        tvBookTimeStart = (TextView)findViewById(R.id.tvBookTimeStart);
        tvBookTimeEnd = (TextView)findViewById(R.id.tvBookTimeEnd);
        sBuilding = (Spinner)findViewById(R.id.sBuilding);
        sFloor = (Spinner)findViewById(R.id.sFloor);
        sRoom = (Spinner)findViewById(R.id.sRoom);
        sTimeStart = (Spinner)findViewById(R.id.sTimeStart);
        sTimeEnd = (Spinner)findViewById(R.id.sTimeEnd);
        dpBookDate = (DatePicker)findViewById(R.id.dpBookDate);
        btnSubmitBooking = (Button)findViewById(R.id.btnSubmitBooking);
        tvBookFloor.setVisibility(View.INVISIBLE);
        tvBookRoom.setVisibility(View.INVISIBLE);
        tvBookDate.setVisibility(View.INVISIBLE);
        tvBookTimeStart.setVisibility(View.INVISIBLE);
        tvBookTimeEnd.setVisibility(View.INVISIBLE);
        sFloor.setVisibility(View.INVISIBLE);
        sRoom.setVisibility(View.INVISIBLE);
        sTimeStart.setVisibility(View.INVISIBLE);
        sTimeEnd.setVisibility(View.INVISIBLE);
        dpBookDate.setVisibility(View.INVISIBLE);
        btnSubmitBooking.setVisibility(View.INVISIBLE);


        arrayadapter(buildingList, sBuilding);
        arrayadapter(FloorList, sFloor);
        arrayadapter(fk61st, sRoom);
        arrayadapter(TimeChoices, sTimeStart);
        arrayadapter(TimeChoices, sTimeEnd);

        sBuildingClick();
        sFloorClick();
        sRoomClick();
        sTimeStartClick();
        sTimeEndClick();
        btnSubmitCLick();
    }

    public void arrayadapter(String[] list, Spinner spinner){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);
    }

    /*public void sClick(Spinner spinnerClick, Object o, TextView tvVisible, Spinner sVisible, EditText etVisible){
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                BuildingChoosen = sBuilding.getItemAtPosition(position);
                tvBookFloor.setVisibility(View.VISIBLE);
                sFloor.setVisibility(View.VISIBLE);
                sBuilding.setEnabled(false);

            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

    }*/

    public void sBuildingClick(){
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int iCurrentSelection;
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (iCurrentSelection != position){
                    BuildingChosen = sBuilding.getItemAtPosition(position);
                    tvBookFloor.setVisibility(View.VISIBLE);
                    sFloor.setVisibility(View.VISIBLE);
                    sBuilding.setEnabled(false);
                }
                iCurrentSelection = position;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void sFloorClick(){
        sFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int iCurrentSelection;
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (iCurrentSelection != position){
                    FloorChosen = sFloor.getItemAtPosition(position);
                    tvBookRoom.setVisibility(View.VISIBLE);
                    sRoom.setVisibility(View.VISIBLE);
                    sFloor.setEnabled(false);
                }
                iCurrentSelection = position;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void sRoomClick(){
        sRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int iCurrentSelection;
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (iCurrentSelection != position){
                    RoomChosen = sRoom.getItemAtPosition(position);
                    tvBookDate.setVisibility(View.VISIBLE);
                    dpBookDate.setVisibility(View.VISIBLE);
                    tvBookTimeStart.setVisibility(View.VISIBLE);
                    sTimeStart.setVisibility(View.VISIBLE);
                    sRoom.setEnabled(false);
                }
                iCurrentSelection = position;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void sTimeStartClick(){
        sTimeStart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int iCurrentSelection;
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (iCurrentSelection != position){
                    TimeStartChosen = sTimeStart.getItemAtPosition(position);
                    tvBookTimeEnd.setVisibility(View.VISIBLE);
                    sTimeEnd.setVisibility(View.VISIBLE);
                    sTimeStart.setEnabled(false);
                }
                iCurrentSelection = position;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void sTimeEndClick(){
        sTimeEnd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int iCurrentSelection;
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (iCurrentSelection != position){
                    TimeEndChosen = sTimeEnd.getItemAtPosition(position);
                    btnSubmitBooking.setVisibility(View.VISIBLE);
                    sTimeEnd.setEnabled(false);
                }
                iCurrentSelection = position;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void btnSubmitCLick() {
        btnSubmitBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                    builder.setMessage("Building: " + BuildingChosen + "\nFloor: " + FloorChosen
                            + "\nRoom: " + RoomChosen + "\nDate: " + dpBookDate.getDayOfMonth() + "\nTime: "
                            + TimeStartChosen + "-" + TimeEndChosen)
                            .setCancelable(false)
                            .setTitle("Submit booking?")
                            .setPositiveButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .setNegativeButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // cancel the dialog box

                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();


                    /*Toast.makeText(BookingActivity.this, "Building: " + BuildingChosen + "\nFloor: " + FloorChosen
                            + "\nRoom: " + RoomChosen + "\nDate: " + etBookDate.getText() + "\nTime: "
                            + TimeStartChosen + "-" + TimeEndChosen, Toast.LENGTH_SHORT);*/

            }
        });
    }

    public void logindb(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/placebookingPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(9);
            nameValuePairs.add(new BasicNameValuePair("usernr", usernr));
            nameValuePairs.add(new BasicNameValuePair("title",""));
            nameValuePairs.add(new BasicNameValuePair("description",""));
            nameValuePairs.add(new BasicNameValuePair("roomid",""));
            nameValuePairs.add(new BasicNameValuePair("timeofbooking",""));
            nameValuePairs.add(new BasicNameValuePair("dateofbooking",""));
            nameValuePairs.add(new BasicNameValuePair("timestart",""));
            nameValuePairs.add(new BasicNameValuePair("timeend",""));
            nameValuePairs.add(new BasicNameValuePair("date",""));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            separated = response.split("#");
            for (int i = 0; i < separated.length; ++i) {
                list.add(separated[i]);}



        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }

    }
}
