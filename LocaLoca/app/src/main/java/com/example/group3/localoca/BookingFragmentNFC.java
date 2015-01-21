package com.example.group3.localoca;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class BookingFragmentNFC extends Fragment{

    private MainMenuFragment MainMenu;

    String usernr, username;
    int buildingID, floorID;

    HttpPost httppost;
    StringBuffer buffer;
    String response, oldbuilding, oldfloor, oldroom;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    String[] separated;
    String[][] matrix;
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> TimeChoicesStart = new ArrayList<String>();
    ArrayList<String> TimeChoicesEnd = new ArrayList<String>();

    TextView tvBooktitle, tvBookBuilding, tvBookFloor, tvBookRoom, tvBookDate, tvBookTimeStart, tvBookTimeEnd;
    Spinner sBuilding, sFloor, sRoom, sTimeStart, sTimeEnd;
    EditText etTitle, etDescription;
    Button btnSubmitBooking,btnCheckDate, btnBookingSeeRoomOnMap;
    DatePicker dpBookDate;
    Object BuildingChosen, FloorChosen, RoomChosen, TimeStartChosen, TimeEndChosen;
    String[] buildingList = {"Choose a building", "A.C. Meyers Vænge 15","Frederikskaj 6", "Frederikskaj 10A", "Frederikskaj 10B", "Frederikskaj 12"};
    String[] FloorList = {"Choose a floor", "Ground Floor", "1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
    String[] TimeChoicesStartString;
    String[] TimeChoicesEndString;
    SharedPreferences oldbooking;
    boolean buildingchanged;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bookingNFC, container, false);

        SharedPreferences pref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        usernr = pref.getString("userNumber", "");
        username = pref.getString("userName", "");

        btnCheckDate = (Button) rootView.findViewById(R.id.btnCheckDate);
        btnBookingSeeRoomOnMap = (Button)rootView.findViewById(R.id.btnBookingSeeOnMap);
        tvBookBuilding = (TextView) rootView.findViewById(R.id.tvBookBuilding);
        tvBookFloor = (TextView) rootView.findViewById(R.id.tvBookFloor);
        tvBookRoom = (TextView) rootView.findViewById(R.id.tvBookRoom);
        tvBookDate = (TextView) rootView.findViewById(R.id.tvBookDate);
        tvBookTimeStart = (TextView) rootView.findViewById(R.id.tvBookTimeStart);
        tvBookTimeEnd = (TextView) rootView.findViewById(R.id.tvBookTimeEnd);
        sBuilding = (Spinner) rootView.findViewById(R.id.sBuilding);
        sFloor = (Spinner) rootView.findViewById(R.id.sFloor);
        sRoom = (Spinner) rootView.findViewById(R.id.sRoom);
        sTimeStart = (Spinner) rootView.findViewById(R.id.sTimeStart);
        sTimeEnd = (Spinner) rootView.findViewById(R.id.sTimeEnd);
        dpBookDate = (DatePicker) rootView.findViewById(R.id.dpBookDate);
        btnSubmitBooking = (Button) rootView.findViewById(R.id.btnSubmitBooking);
        etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        tvBookBuilding.setVisibility(View.GONE);
        tvBookFloor.setVisibility(View.GONE);
        btnBookingSeeRoomOnMap.setVisibility(View.INVISIBLE);
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
        btnCheckDate.setVisibility(View.INVISIBLE);

        arrayadapter(buildingList, sBuilding);
        arrayadapter(FloorList, sFloor);

        btnCheckDateClick();
        sBuildingClick();
        sFloorClick();
        sRoomClick();
        sTimeStartClick();
        sTimeEndClick();
        btnSubmitCLick();
        dpChanged();

        /*SharedPreferences old = getActivity().getSharedPreferences("oldbooking", Context.MODE_PRIVATE);
        oldbuilding = old.getString("oldBuilding", "");
        oldfloor = old.getString("oldFloor", "");
        oldroom = old.getString("oldRoom", "");

        if(oldbuilding.equals("") != true){
            System.out.println(oldbuilding);
            System.out.println(oldfloor);
            System.out.println(oldroom);
        } else {
            System.out.println("No previous bookings");
        }*/

        btnBookingSeeRoomOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "This feature is currently disabled", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public void arrayadapter(String[] list, Spinner spinner){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);
    }

    public void sBuildingClick(){
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int iCurrentSelection;
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {

                if (iCurrentSelection != position){
                    arrayadapter(FloorList, sFloor);
                    buildingchanged = true;
                    buildingID = position;
                    System.out.println(buildingID);
                    BuildingChosen = sBuilding.getItemAtPosition(position);
                    btnBookingSeeRoomOnMap.setVisibility(View.INVISIBLE);
                    //tvBookFloor.setVisibility(View.VISIBLE);
                    dpBookDate.setVisibility(View.INVISIBLE);
                    tvBookDate.setVisibility(View.INVISIBLE);
                    tvBookRoom.setVisibility(View.INVISIBLE);
                    sFloor.setVisibility(View.VISIBLE);
                    sRoom.setVisibility(View.INVISIBLE);
                    btnCheckDate.setVisibility(View.INVISIBLE);
                    tvBookTimeEnd.setVisibility(View.INVISIBLE);
                    sTimeEnd.setVisibility(View.INVISIBLE);
                    etTitle.setVisibility(View.INVISIBLE);
                    etDescription.setVisibility(View.INVISIBLE);
                    btnSubmitBooking.setVisibility(View.INVISIBLE);
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

                if(buildingchanged){
                    buildingchanged = false;
                } else {
                    if (iCurrentSelection != position) {
                        floorID = position;
                        dialog = ProgressDialog.show(getActivity(), "One moment please", "Fetching rooms");
                        new Thread(new Runnable() {
                            public void run() {
                                getRooms();

                            }
                        }).start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (separated != null) {
                                    arrayadapter(separated, sRoom);
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Unable to get roomlist, please check your connection" +
                                            " and try again", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        }, 1500);

                        System.out.println(floorID);
                        FloorChosen = sFloor.getItemAtPosition(position);
                        btnBookingSeeRoomOnMap.setVisibility(View.INVISIBLE);
                        tvBookRoom.setVisibility(View.VISIBLE);
                        sRoom.setVisibility(View.VISIBLE);
                        btnCheckDate.setVisibility(View.INVISIBLE);
                        tvBookTimeEnd.setVisibility(View.INVISIBLE);
                        sTimeEnd.setVisibility(View.INVISIBLE);
                        etTitle.setVisibility(View.INVISIBLE);
                        etDescription.setVisibility(View.INVISIBLE);
                        btnSubmitBooking.setVisibility(View.INVISIBLE);
                        sRoom.performClick();
                    }
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
                    btnBookingSeeRoomOnMap.setVisibility(View.VISIBLE);
                    tvBookDate.setVisibility(View.VISIBLE);
                    dpBookDate.setVisibility(View.VISIBLE);
                    btnCheckDate.setVisibility(View.VISIBLE);
                    tvBookTimeEnd.setVisibility(View.INVISIBLE);
                    sTimeStart.setVisibility(View.INVISIBLE);
                    sTimeEnd.setVisibility(View.INVISIBLE);
                    etTitle.setVisibility(View.INVISIBLE);
                    etDescription.setVisibility(View.INVISIBLE);
                    btnSubmitBooking.setVisibility(View.INVISIBLE);
                }
                iCurrentSelection = position;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public void btnCheckDateClick() {
        btnCheckDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(getActivity(), "One moment please", "Getting the current bookings placed");
                new Thread(new Runnable() {
                    public void run() {
                        getCurrentBookings();
                    }
                }).start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TimeChoicesStart.clear();
                        TimeChoicesEnd.clear();
                        for (int i = 0; i < 25; i++) {
                            TimeChoicesStart.add(i + ":00");
                            TimeChoicesEnd.add(i + ":00");
                        }
                        if (separated[1].equals("Fail") != true) {
                            String userDate;
                            if(dpBookDate.getDayOfMonth() < 10){
                                userDate = String.valueOf("0"+dpBookDate.getDayOfMonth()) + "/" +
                                        String.valueOf(dpBookDate.getMonth() + 1) + "/" + String.valueOf(dpBookDate.getYear());
                            } else {
                                userDate = String.valueOf(dpBookDate.getDayOfMonth()) + "/" +
                                        String.valueOf(dpBookDate.getMonth() + 1) + "/" + String.valueOf(dpBookDate.getYear());
                            }
                            for (int j = 1; separated.length > j; j++) {
                                String serverDate = matrix[j][4];
                                if(userDate.equals(serverDate)) {
                                    int serverTimeStart = Integer.valueOf(matrix[j][2].replaceAll(":.*", ""));
                                    int serverTimeEnd = Integer.valueOf(matrix[j][3].replaceAll(":.*", ""));
                                    for (int i = 0; i < 25; i++) {
                                        String currenttimei = i + ":00";
                                        if (i >= serverTimeStart && serverTimeEnd > i) {
                                            TimeChoicesStart.remove(currenttimei);
                                        }
                                        if (i > serverTimeStart && serverTimeEnd >= i) {
                                            TimeChoicesEnd.remove(currenttimei);
                                        }
                                    }
                                }
                            }
                        }
                        TimeChoicesStartString = new String[TimeChoicesStart.size()];
                        TimeChoicesStart.toArray(TimeChoicesStartString);
                        TimeChoicesEndString = new String[TimeChoicesEnd.size()];
                        TimeChoicesEnd.toArray(TimeChoicesEndString);
                        arrayadapter(TimeChoicesStartString, sTimeStart);
                        arrayadapter(TimeChoicesEndString, sTimeEnd);
                        sTimeStart.setVisibility(View.VISIBLE);
                        tvBookTimeStart.setVisibility(View.VISIBLE);
                        etTitle.setVisibility(View.INVISIBLE);
                        etDescription.setVisibility(View.INVISIBLE);
                        btnSubmitBooking.setVisibility(View.INVISIBLE);
                        dialog.dismiss();
                    }
                }, 1500);
            }
        });
    }

    public void dpChanged() {
        dpBookDate.init(dpBookDate.getYear(), dpBookDate.getMonth(), dpBookDate.getDayOfMonth(),
                new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker arg0, int arg1, int arg2, int arg3) {
                        tvBookTimeEnd.setVisibility(View.INVISIBLE);
                        sTimeStart.setVisibility(View.INVISIBLE);
                        sTimeEnd.setVisibility(View.INVISIBLE);
                        etTitle.setVisibility(View.INVISIBLE);
                        etDescription.setVisibility(View.INVISIBLE);
                        btnSubmitBooking.setVisibility(View.INVISIBLE);
                        sTimeStart.setVisibility(View.INVISIBLE);
                        tvBookTimeStart.setVisibility(View.INVISIBLE);
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
                    if (Integer.valueOf(TimeStartChosen.toString().replaceAll(":.*", "")) >
                            Integer.valueOf(TimeEndChosen.toString().replaceAll(":.*", ""))) {
                        Toast.makeText(getActivity(), "Starting time cannot be before ending time", Toast.LENGTH_SHORT).show();
                    } else {
                        if (separated[1].equals("Fail")) {
                            btnSubmitBooking.setVisibility(View.VISIBLE);
                            etTitle.setVisibility(View.VISIBLE);
                            etDescription.setVisibility(View.VISIBLE);
                        } else {
                            btnSubmitBooking.setVisibility(View.VISIBLE);
                            etTitle.setVisibility(View.VISIBLE);
                            etDescription.setVisibility(View.VISIBLE);
                            for (int i = 1; separated.length > i; i++) {
                                int userTimeStart = Integer.valueOf(TimeStartChosen.toString().replaceAll(":.*", ""));
                                int userTimeEnd = Integer.valueOf(TimeEndChosen.toString().replaceAll(":.*", ""));
                                int serverTimeStart = Integer.valueOf(matrix[i][2].replaceAll(":.*", ""));
                                int serverTimeEnd = Integer.valueOf(matrix[i][3].replaceAll(":.*", ""));
                                String userDate;
                                if (dpBookDate.getDayOfMonth() < 10) {
                                    userDate = String.valueOf("0" + dpBookDate.getDayOfMonth()) + "/" +
                                            String.valueOf(dpBookDate.getMonth() + 1) + "/" + String.valueOf(dpBookDate.getYear());
                                } else {
                                    userDate = String.valueOf(dpBookDate.getDayOfMonth()) + "/" +
                                            String.valueOf(dpBookDate.getMonth() + 1) + "/" + String.valueOf(dpBookDate.getYear());
                                }

                                String serverDate = matrix[i][4];
                                if (serverDate.equals(userDate)) {
                                    if (userTimeStart < serverTimeStart && serverTimeEnd < userTimeEnd) {
                                        Toast.makeText(getActivity(), "Booking has already been placed, please choose another one\n"
                                                + "\nTitle: " + matrix[i][1] + "\nDate: " + matrix[i][4] + "\nTime starting: " + matrix[i][2] +
                                                "\nTime Ending: " + matrix[i][3], Toast.LENGTH_LONG).show();
                                        btnSubmitBooking.setVisibility(View.INVISIBLE);
                                        etTitle.setVisibility(View.INVISIBLE);
                                        etDescription.setVisibility(View.INVISIBLE);

                                    }
                                }
                            }
                            dialog.dismiss();
                        }
                    }
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
                btnSubmitBooking.setEnabled(false);
                String stretTitle = etTitle.getText().toString();
                String stretDescription = etDescription.getText().toString();
                if (Integer.valueOf(TimeStartChosen.toString().replaceAll(":.*", "")) >
                        Integer.valueOf(TimeEndChosen.toString().replaceAll(":.*", ""))) {
                    Toast.makeText(getActivity(), "Starting time cannot be before ending time", Toast.LENGTH_SHORT).show();
                    btnSubmitBooking.setEnabled(true);
                } else {
                    if (TextUtils.isEmpty(stretTitle) || TextUtils.isEmpty(stretDescription) || RoomChosen.equals("")) {
                        Toast.makeText(getActivity(), "Room, Title or Description is missing", Toast.LENGTH_SHORT).show();
                        btnSubmitBooking.setEnabled(true);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                                                            /*oldbooking = getActivity().getSharedPreferences("oldbooking", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = oldbooking.edit();
                                                            editor.putString("oldBuilding", BuildingChosen.toString());
                                                            editor.putString("oldFloor", FloorChosen.toString());
                                                            editor.putString("oldRoom", RoomChosen.toString());*/
                                                            Toast.makeText(getActivity(), "Your booking has been successfully placed"
                                                                    , Toast.LENGTH_SHORT).show();
                                                            MainMenuFragment MainMenuFragment = new MainMenuFragment();
                                                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                            ft.replace(R.id.contentFrame, MainMenuFragment).commit();
                                                        } else {
                                                            Toast.makeText(getActivity(), "Booking failed, please check your connection"
                                                                    , Toast.LENGTH_SHORT).show();
                                                        }
                                                        btnSubmitBooking.setEnabled(true);
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
            String date;
            /*String roomid = buildingID + "." +
                    floorID + "." + RoomChosen.toString().replaceAll(" .*", "");*/
            String roomid = buildingID + "." +
                    floorID + "." + RoomChosen.toString();
            if(dpBookDate.getDayOfMonth() < 10 && dpBookDate.getMonth() < 10){
                date = String.valueOf("0" + dpBookDate.getDayOfMonth())+"/0"+
                        String.valueOf(dpBookDate.getMonth()+1)+"/"+String.valueOf(dpBookDate.getYear());
            }
            else if(dpBookDate.getDayOfMonth() < 10){
                date = String.valueOf("0" + dpBookDate.getDayOfMonth())+"/"+
                        String.valueOf(dpBookDate.getMonth()+1)+"/"+String.valueOf(dpBookDate.getYear());
            }
            else if(dpBookDate.getMonth() < 10){
                date = String.valueOf(dpBookDate.getDayOfMonth())+"/0"+
                        String.valueOf(dpBookDate.getMonth()+1)+"/"+String.valueOf(dpBookDate.getYear());
            }
            else {
                date = String.valueOf(dpBookDate.getDayOfMonth())+"/"+
                        String.valueOf(dpBookDate.getMonth()+1)+"/"+String.valueOf(dpBookDate.getYear());
            }

            System.out.println(roomid);
            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/placebookingPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(10);
            nameValuePairs.add(new BasicNameValuePair("usernr", usernr.replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("username", username.replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("title", etTitle.getText().toString().replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("description",etDescription.getText().toString().replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("roomid",roomid.replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("timeofbooking",currentTime.replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("dateofbooking",currentDate.replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("timestart",TimeStartChosen.toString().replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("timeend",TimeEndChosen.toString().replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("date", date.replaceAll("'", "")));
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
            /*String roomid = buildingID + "." +
                    floorID + "." + RoomChosen.toString().replaceAll(" .*", "");*/
            String roomid = buildingID + "." +
                    floorID + "." + RoomChosen.toString();
            nameValuePairs.add(new BasicNameValuePair("roomid", roomid));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            separated = response.split("@");
            matrix = new String[response.length()][];
            for (int i = 0; i < separated.length; i++) {
                matrix[i] = separated[i].split("#");
            }
        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }

    }

}
