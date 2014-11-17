package com.example.group3.localoca;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Asbjørn on 07-11-2014.
 */
public class BookingActivity extends Activity{

    SharedPreferences userinfo;
    String usernr;
    int buildingID, floorID;

    HttpPost httppost;
    StringBuffer buffer;
    String response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    String[] separated;
    String[][] matrix;
    ArrayList<String> list = new ArrayList<String>();

    TextView tvBooktitle, tvBookBuilding, tvBookFloor, tvBookRoom, tvBookDate, tvBookTimeStart, tvBookTimeEnd;
    Spinner sBuilding, sFloor, sRoom, sTimeStart, sTimeEnd;
    EditText etTitle, etDescription;
    Button btnSubmitBooking;
    DatePicker dpBookDate;
    Object BuildingChosen, FloorChosen, RoomChosen, TimeStartChosen, TimeEndChosen;
    String[] buildingList = {"Choose a building", "A.C. Meyers Vænge 15","Frederikskaj 6", "Frederikskaj 10A", "Frederikskaj 10B", "Frederikskaj 12"};
    String[] FloorList = {"Choose a floor", "Ground Floor", "1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
    String[] TimeChoices = {"Choose a time", "00:00","01:00","02:00","03:00","04:00","05:00","06:00","07:00"
            ,"08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00",
            "17:00","18:00","19:00","20:00","21:00","22:00","23:00","24:00"};

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
        etTitle = (EditText)findViewById(R.id.etTitle);
        etDescription = (EditText)findViewById(R.id.etDescription);
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
        etTitle.setVisibility(View.INVISIBLE);
        etDescription.setVisibility(View.INVISIBLE);

        arrayadapter(buildingList, sBuilding);
        arrayadapter(FloorList, sFloor);

        arrayadapter(TimeChoices, sTimeStart);

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

    public void sBuildingClick(){
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int iCurrentSelection;
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (iCurrentSelection != position){
                    buildingID = position;
                    System.out.println(buildingID);
                    BuildingChosen = sBuilding.getItemAtPosition(position);
                    tvBookFloor.setVisibility(View.VISIBLE);
                    sFloor.setVisibility(View.VISIBLE);
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
                    floorID = position;
                    dialog = ProgressDialog.show(BookingActivity.this, "One moment please", "Fetching rooms");
                    new Thread(new Runnable() {
                        public void run() {
                            getRooms();

                        }
                    }).start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(separated != null) {
                                arrayadapter(separated, sRoom);
                                dialog.dismiss();
                            }
                            else{
                                Toast.makeText(BookingActivity.this, "Unable to get roomlist, please check your connection" +
                                        " and try again", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }, 1500);

                    System.out.println(floorID);
                    FloorChosen = sFloor.getItemAtPosition(position);

                    tvBookRoom.setVisibility(View.VISIBLE);
                    sRoom.setVisibility(View.VISIBLE);
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
                    new Thread(new Runnable() {
                        public void run() {
                            getCurrentBookings();

                        }
                    }).start();
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
                    arrayadapter(TimeChoices, sTimeEnd);
                    sTimeEnd.setVisibility(View.VISIBLE);
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
                    //dialog = ProgressDialog.show(BookingActivity.this, "One moment please", "Getting current bookings");
                    //new Handler().postDelayed(new Runnable() {
                    //    @Override
                    //    public void run() {
                                for(int i=0; separated.length > i; i++){
                                    System.out.println("dbStart:" + Integer.valueOf(matrix[i][2].replaceAll(":.*", "")) + " " +
                                            "start:" + Integer.valueOf(TimeStartChosen.toString().replaceAll(":.*", "")) + " " +
                                            "dbend:" + Integer.valueOf(matrix[i][3].replaceAll(":.*", "")) + " " +
                                            "end:" + Integer.valueOf(TimeEndChosen.toString().replaceAll(":.*", "")) + " " +
                                            "dbdate" + matrix[i][4] + " " + "date" + (String.valueOf(dpBookDate.getDayOfMonth())+"/"+
                                            String.valueOf(dpBookDate.getMonth()+1)+"/"+String.valueOf(dpBookDate.getYear())));
                                    if(Integer.valueOf(matrix[i][2].replaceAll(":.*", "")) <
                                            Integer.valueOf(TimeStartChosen.toString().replaceAll(":.*", "")) &&
                                            Integer.valueOf(matrix[i][3].replaceAll(":.*", "")) >
                                            Integer.valueOf(TimeEndChosen.toString().replaceAll(":.*", "")) &&
                                            matrix[i][4].equals(String.valueOf(dpBookDate.getDayOfMonth())+"/"+
                                            String.valueOf(dpBookDate.getMonth()+1)+"/"+String.valueOf(dpBookDate.getYear()))){
                                        Toast.makeText(BookingActivity.this, "Booking has already been placed, please choose another one\n"
                                                + "\nTitle: " + matrix[i][1] + "\nDate: " + matrix[i][4] + "\nTime starting: " + matrix[i][2] +
                                                "\nTime Ending: " + matrix[i][3], Toast.LENGTH_LONG).show();

                                    }
                                    else{

                                        btnSubmitBooking.setVisibility(View.VISIBLE);
                                        etTitle.setVisibility(View.VISIBLE);
                                        etDescription.setVisibility(View.VISIBLE);
                                    }
                                dialog.dismiss();
                            }
                    //    }
                    //}, 1500);
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
                String stretTitle = etTitle.getText().toString();
                String stretDescription = etDescription.getText().toString();
                if (Integer.valueOf(TimeStartChosen.toString().replaceAll(":.*", "")) >
                        Integer.valueOf(TimeEndChosen.toString().replaceAll(":.*", ""))) {
                    Toast.makeText(BookingActivity.this, "Starting time cannot be before ending time", Toast.LENGTH_SHORT).show();

                } else {
                    if (TextUtils.isEmpty(stretTitle) || TextUtils.isEmpty(stretDescription)) {
                        Toast.makeText(BookingActivity.this, "Title or Description is missing", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                        builder.setMessage("Title: " + etTitle.getText().toString() + "\nDescription: " +
                                etDescription.getText().toString() + "\nBuilding: " + BuildingChosen + "\nFloor: " + FloorChosen
                                + "\nRoom: " + RoomChosen + "\nDate: " + String.valueOf(dpBookDate.getDayOfMonth()) + "/" +
                                String.valueOf(dpBookDate.getMonth() + 1) + "/" + String.valueOf(dpBookDate.getYear()) + "\nTime: "
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
                                            public void onClick(final DialogInterface dialog, int id) {
                                                new Thread(new Runnable() {
                                                    public void run() {
                                                        //dialogStart("Booking in progress", "Your booking is being placed");
                                                        makeBooking();

                                                    }
                                                }).start();
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        //dialogEnd();
                                                        System.out.println(separated[0]);
                                                        if (separated[1].equals("Success")) {
                                                            Toast.makeText(BookingActivity.this, "Your booking has been successfully placed"
                                                                    , Toast.LENGTH_SHORT).show();
                                                            finish();
                                                        } else {
                                                            Toast.makeText(BookingActivity.this, "Booking failed, please check your connection"
                                                                    , Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }, 1500);


                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();


                    /*Toast.makeText(BookingActivity.this, "Building: " + BuildingChosen + "\nFloor: " + FloorChosen
                            + "\nRoom: " + RoomChosen + "\nDate: " + etBookDate.getText() + "\nTime: "
                            + TimeStartChosen + "-" + TimeEndChosen, Toast.LENGTH_SHORT);*/

                    }
                }
            }
        });
    }

    public void getRooms(){
        try{
            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/getroomsPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("building", String.valueOf(buildingID)));
            nameValuePairs.add(new BasicNameValuePair("floor", String.valueOf(floorID)));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            separated = response.split("@");
            for (int i = 0; i < separated.length; ++i) {
                list.add(separated[i]);
            }

        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }
    }

    public void makeBooking(){
        try{
            SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm");
            String currentTime = sdftime.format(new Date());
            SimpleDateFormat sdfdate = new SimpleDateFormat("dd/MM/yyyy");
            String currentDate = sdfdate.format(new Date());
            String roomid = buildingID + "." +
                    floorID + "." + RoomChosen.toString().replaceAll(" .*", "");
            System.out.println(roomid);
            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/placebookingPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(9);
            nameValuePairs.add(new BasicNameValuePair("usernr", usernr));
            nameValuePairs.add(new BasicNameValuePair("title", etTitle.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("description",etDescription.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("roomid",roomid));
            nameValuePairs.add(new BasicNameValuePair("timeofbooking",currentTime));
            nameValuePairs.add(new BasicNameValuePair("dateofbooking",currentDate));
            nameValuePairs.add(new BasicNameValuePair("timestart",TimeStartChosen.toString()));
            nameValuePairs.add(new BasicNameValuePair("timeend",TimeEndChosen.toString()));
            nameValuePairs.add(new BasicNameValuePair("date", String.valueOf(dpBookDate.getDayOfMonth())+"/"+
                    String.valueOf(dpBookDate.getMonth()+1)+"/"+String.valueOf(dpBookDate.getYear())));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            separated = response.split("#");
            for (int i = 0; i < separated.length; ++i) {
                list.add(separated[i]);
            }



        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }
    }

    public void getCurrentBookings(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/getExistingBookingsPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            String roomid = buildingID + "." +
                    floorID + "." + RoomChosen.toString().replaceAll(" .*", "");
            nameValuePairs.add(new BasicNameValuePair("roomid", roomid));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            separated = response.split("@");
            matrix = new String[response.length()][];
            for(int i=0; i<separated.length; i++){
                matrix[i] = separated[i].split("#");
            }
        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }

    }

}
