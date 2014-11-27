package com.example.group3.localoca;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Asbjørn on 03-11-2014.
 */
public class CalenderActivity extends Activity {

    ListView lvDay;
    TextView tvBooking;
    SharedPreferences userinfo;
    Button btnAddUsers,btnDeleteBooking, btnBookingBack;
    String usernr, newUserNr;
    String[][] matrix;
    String[] currentBookingSelected = new String[9];
    HttpPost httppost;
    StringBuffer buffer;
    String response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    List<String> lvlist = new ArrayList<String>();
    ProgressDialog dialog = null;
    static String[] separated, getUserArray;
    ArrayList<String> list = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        usernr = userinfo.getString("userNumber", "");
        lvDay = (ListView)findViewById(R.id.lvDay);
        tvBooking = (TextView)findViewById(R.id.tvBooking);
        btnAddUsers = (Button)findViewById(R.id.btnAddUsers);
        btnDeleteBooking = (Button)findViewById(R.id.btnDeleteBooking);
        btnBookingBack = (Button)findViewById(R.id.btnBookingBack);

        tvBooking.setVisibility(View.GONE);
        btnAddUsers.setVisibility(View.GONE);
        btnDeleteBooking.setVisibility(View.GONE);
        btnBookingBack.setVisibility(View.GONE);

        lvClick();
        btnClick();

        dialog = ProgressDialog.show(this, "One moment please", "Fetching your calender");
        new Thread(new Runnable() {
            public void run() {
                getUserBookings();

            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lvRoomsPopulate();
                dialog.dismiss();
            }
        }, 1500);
    }

    private String getDateString(int days) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return dateFormat.format(cal.getTime());
    }

    private String getDayString(int days) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, days);
        return dateFormat.format(cal.getTime());
    }

    public void getUserBookings(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/getuserbookingsPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("usernr", usernr));
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

    public void getUserAndPlaceBooking(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/getUserAndPlaceBookingPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("usernr",newUserNr));
            nameValuePairs.add(new BasicNameValuePair("oldusernr",usernr));
            nameValuePairs.add(new BasicNameValuePair("title",currentBookingSelected[1]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            getUserArray = response.split("#");

        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }

    }

    private void arrayadapter() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, lvlist);
        lvDay.setAdapter(arrayAdapter);
    }

    private void lvRoomsPopulate(){
        for(int i = 0; i<7; i++){
            lvlist.add(getDayString(i) + " " + getDateString(i));
            if(separated.length > 0) {
                for (int n = 1; separated.length > n; n++) {
                    if (getDateString(i).equals(matrix[n][8])) {
                        lvlist.add(matrix[n][6] + " " + matrix[n][1] + " \n" + matrix[n][7] + " " + matrix[n][2]);
                        String[] temp = matrix[n][1].split(" ");
                        list.add(temp[0]);
                    }
                }
            }
        }
        arrayadapter();
    }

    public void btnClick(){
        btnBookingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvDay.setVisibility(View.VISIBLE);
                tvBooking.setVisibility(View.GONE);
                btnBookingBack.setVisibility(View.GONE);
                btnDeleteBooking.setVisibility(View.GONE);
                btnAddUsers.setVisibility(View.GONE);
                tvBooking.setText("");
            }
        });

        btnAddUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertbuilder();
            }
        });

        btnDeleteBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void lvClick(){
        lvDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object o = lvDay.getItemAtPosition(position);
                String[] strO = String.valueOf(o).split(" ");
                System.out.println(strO[1]);
                if(list.contains(strO[1])){
                    for(int i = 1 ; separated.length > i ; i++){
                        String temp = matrix[i][6] + " " + matrix[i][1] + " \n" + matrix[i][7] + " " + matrix[i][2];
                        if(o.toString().equals(temp)){
                            lvDay.setVisibility(View.GONE);
                            tvBooking.setVisibility(View.VISIBLE);
                            btnBookingBack.setVisibility(View.VISIBLE);
                            btnDeleteBooking.setVisibility(View.VISIBLE);
                            btnAddUsers.setVisibility(View.VISIBLE);
                            currentBookingSelected[1] = matrix[i][1];
                            currentBookingSelected[2] = matrix[i][2];
                            currentBookingSelected[3] = matrix[i][3];
                            currentBookingSelected[4] = matrix[i][4];
                            currentBookingSelected[5] = matrix[i][5];
                            currentBookingSelected[6] = matrix[i][6];
                            currentBookingSelected[7] = matrix[i][7];
                            currentBookingSelected[8] = matrix[i][8];
                            String sourceString = "<b>" + "Booking ID:" + "</b><br> " + matrix[i][0] + "<br><br>" +
                                    "<b>" + "Title:" + "</b><br> " + matrix[i][1] + "<br><br>" +
                                    "<b>" + "Description:" + "</b><br> " + matrix[i][2] + "<br><br>" +
                                    "<b>" + "Room:" + "</b><br> " + matrix[i][3] + "<br><br>" +
                                    "<b>" + "Time and date booked:" + "</b><br> " +  matrix[i][4] + " - " + matrix[i][5] + "<br><br>" +
                                    "<b>" + "Booking Time:" + "</b><br> " + matrix[i][6] + " - " + matrix[i][7] + "<br><br>" +
                                    "<b>" + "Booking Date:" + "</b><br> " + matrix[i][8];
                            tvBooking.setText(Html.fromHtml(sourceString));
                        }
                    }
                }

            }
        });
    }

    public void alertbuilder(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CalenderActivity.this);
        final EditText input = new EditText(CalenderActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);
        builder.setMessage("Please add the user number of the person you want to add")
                .setCancelable(false)
                .setTitle("Add additional person")
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                if(input.getText().toString().length() < 8){
                                    Toast.makeText(CalenderActivity.this,
                                            "Please enter a valid user number", Toast.LENGTH_SHORT).show();
                                } else {
                                    newUserNr = input.getText().toString();
                                    addUserToBooking();
                                }
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private enum dbanswers {
        Fail, Userhas, Error, Success;
    }

    public void addUserToBooking(){

        dialog = ProgressDialog.show(CalenderActivity.this, "One moment please", "Adding person to booking");
            new Thread(new Runnable() {
                public void run() {
                    getUserAndPlaceBooking();

                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dbanswers Answer = dbanswers.valueOf(getUserArray[1]);
                    switch (Answer){
                        case Fail:
                            Toast.makeText(CalenderActivity.this, "No user with that number", Toast.LENGTH_SHORT).show();
                            break;
                        case Userhas:
                            Toast.makeText(CalenderActivity.this, "User is already on this booking", Toast.LENGTH_SHORT).show();
                            break;
                        case Error:
                            Toast.makeText(CalenderActivity.this, "Booking failed, please check connection", Toast.LENGTH_SHORT).show();
                            break;
                        case Success:
                            Toast.makeText(CalenderActivity.this, "User succesfully added to booking", Toast.LENGTH_SHORT).show();
                            break;

                    }
                    dialog.dismiss();
                }
            }, 1500);
        }

}
