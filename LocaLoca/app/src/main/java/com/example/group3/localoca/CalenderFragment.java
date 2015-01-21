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
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

/**
 * Created by Asbjørn on 03-11-2014.
 */
public class CalenderFragment extends Fragment {

    float x1,x2;
    float y1, y2;
    int week = 0;

    ListView lvDay;
    TextView tvBooking, tvDate ;
    LinearLayout thisui;
    SharedPreferences userinfo;
    Button btnAddUsers,btnDeleteBooking, btnBookingBack, btnBack, btnForward, btnFindRoom;
    String usernr, newUserNr, username;
    String[][] matrix;
    String[] currentBookingSelected = new String[10];
    HttpPost httppost;
    StringBuffer buffer;
    String response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    List<String> lvlist = new ArrayList<String>();
    ProgressDialog dialog = null;
    String[] separated, getUserArray, deleteArray;
    ArrayList<String> list = new ArrayList<String>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calender, container, false);

        getActivity().getActionBar().setTitle("Calender");

        SharedPreferences pref = getActivity().getSharedPreferences("userinfo", Context.MODE_PRIVATE);
        usernr = pref.getString("userNumber", "");
        username = pref.getString("userName", "");

        lvDay = (ListView) rootView.findViewById(R.id.lvDay);
        tvBooking = (TextView) rootView.findViewById(R.id.tvBooking);
        thisui = (LinearLayout) rootView.findViewById(R.id.thisui);
        tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        btnAddUsers = (Button) rootView.findViewById(R.id.btnAddUsers);
        btnDeleteBooking = (Button) rootView.findViewById(R.id.btnDeleteBooking);
        btnBookingBack = (Button) rootView.findViewById(R.id.btnBookingBack);
        btnBack = (Button) rootView.findViewById(R.id.btnBack);
        btnForward = (Button) rootView.findViewById(R.id.btnForward);
        btnFindRoom = (Button) rootView.findViewById(R.id.btnFindRoom);

        btnForward.setVisibility(View.GONE);
        btnBack.setVisibility(View.GONE);
        tvBooking.setVisibility(View.GONE);
        btnAddUsers.setVisibility(View.GONE);
        btnDeleteBooking.setVisibility(View.GONE);
        btnBookingBack.setVisibility(View.GONE);

        lvClick();
        btnClick();

        System.out.println(getDateString(0));

        dialog = ProgressDialog.show(getActivity(), "One moment please", "Fetching your calender");
        new Thread(new Runnable() {
            public void run() {
                getUserBookings();

            }
        }).start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(separated != null) {
                    lvRoomsPopulate();
                    dialog.dismiss();
                    btnBack.setVisibility(View.VISIBLE);
                    btnForward.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), "Fetching calender failed, check your connection", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    MainMenuFragment contentView = new MainMenuFragment();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.contentFrame, contentView).commit();
                }
            }
        }, 1500);
        return rootView;
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

    public void deleteBooking(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/deleteBookingPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("title",currentBookingSelected[1]));
            nameValuePairs.add(new BasicNameValuePair("description",currentBookingSelected[2]));
            nameValuePairs.add(new BasicNameValuePair("roomid",currentBookingSelected[3]));
            nameValuePairs.add(new BasicNameValuePair("usernr",usernr));
            nameValuePairs.add(new BasicNameValuePair("username", username.replaceAll("'", "")));
            nameValuePairs.add(new BasicNameValuePair("timebooked",currentBookingSelected[5]));
            nameValuePairs.add(new BasicNameValuePair("datebooked",currentBookingSelected[6]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            deleteArray = response.split("#");

        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }

    }

    private void arrayadapter() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1, lvlist);
        lvDay.setAdapter(arrayAdapter);
    }

    private void lvRoomsPopulate(){
        lvlist.clear();
        //tvDate.setText("<   "  + getDateString(week) + "-" + getDateString(week + 7) + "   >");
        tvDate.setText(getDateString(week) + " - " + getDateString(week + 7));
        for(int i = 0; i<7; i++){
            lvlist.add(getDayString(i + week) + " " + getDateString(i + week));
            if(separated.length > 0) {
                for (int n = 1; separated.length > n; n++) {
                    System.out.println(getDateString(i + week)+ " = " + matrix[n][9]);
                    if (getDateString(i + week).equals(matrix[n][9])) {
                        lvlist.add(matrix[n][7] + " " + matrix[n][1] + " \n" + matrix[n][8] + " " + matrix[n][2]);
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
                thisui.setVisibility(View.VISIBLE);
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
                alertbuilderAdd();
            }
        });

        btnDeleteBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertbuilderDelete();
            }
        });

        btnFindRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getActivity(), "This feature is currently disabled", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                week = week - 7;
                lvRoomsPopulate();
                Toast.makeText(getActivity(), "Changed one week backwards", Toast.LENGTH_SHORT).show();
            }
        });

        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                week = week + 7;
                lvRoomsPopulate();
                Toast.makeText(getActivity(), "Changed one week forward", Toast.LENGTH_SHORT).show();
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
                        String temp = matrix[i][7] + " " + matrix[i][1] + " \n" + matrix[i][8] + " " + matrix[i][2];
                        if(o.toString().equals(temp)){
                            thisui.setVisibility(View.GONE);
                            tvBooking.setVisibility(View.VISIBLE);
                            btnBookingBack.setVisibility(View.VISIBLE);
                            btnDeleteBooking.setVisibility(View.VISIBLE);
                            btnAddUsers.setVisibility(View.VISIBLE);
                            currentBookingSelected[1] = matrix[i][1];
                            currentBookingSelected[2] = matrix[i][2];
                            currentBookingSelected[3] = matrix[i][3];
                            currentBookingSelected[4] = matrix[i][4].replaceAll(" .*", "");
                            currentBookingSelected[5] = matrix[i][5];
                            currentBookingSelected[6] = matrix[i][6];
                            currentBookingSelected[7] = matrix[i][7];
                            currentBookingSelected[8] = matrix[i][8];
                            currentBookingSelected[9] = matrix[i][9];
                            String sourceString =
                                    "<b>" + "Title:" + "</b><br> " + matrix[i][1] + "<br><br>" +
                                    "<b>" + "Description:" + "</b><br> " + matrix[i][2] + "<br><br>" +
                                    "<b>" + "Room:" + "</b><br> " + matrix[i][3] + "<br><br>" +
                                    "<b>" + "Booking Date:" + "</b><br> " + matrix[i][9] + "<br><br>" +
                                    "<b>" + "Booking Time:" + "</b><br> " + matrix[i][7] + " - " + matrix[i][8] + "<br><br>" +
                                    "<b>" + "Creator:" + "</b><br> " + matrix[i][4].replaceAll("1|2|3|4|5|6|7|8|9|0|-", "") + "<br><br>" +
                                    "<b>" + "Time and date booked:" + "</b><br> " +  matrix[i][5] + " - " + matrix[i][6] + "<br><br>";
                            tvBooking.setText(Html.fromHtml(sourceString));
                            if(usernr.equals(currentBookingSelected[4]) == false){
                                btnAddUsers.setEnabled(false);
                                btnDeleteBooking.setText("Remove me from this booking");
                            }
                        }
                    }
                }

            }
        });
    }

    public void alertbuilderAdd(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
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
                .setNegativeButton("Add",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                if(input.getText().toString().length() < 8){
                                    Toast toast = Toast.makeText(getActivity(), "Please insert a valid user nr", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                    toast.show();
                                } else {
                                    newUserNr = input.getText().toString();
                                    addUserToBooking();
                                }
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void alertbuilderDelete(){
        if(usernr.equals(currentBookingSelected[4])) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to delete this booking?")
                    .setCancelable(false)
                    .setTitle("Delete booking")
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
                                            deleteBooking();

                                        }
                                    }).start();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (deleteArray.length > 0) {
                                                if (deleteArray[1].equals("Success")) {
                                                    Toast.makeText(getActivity(), "Booking deleted", Toast.LENGTH_SHORT).show();
                                                    CalenderFragment CalendarFragment = new CalenderFragment();
                                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                    ft.replace(R.id.contentFrame, CalendarFragment).commit();
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "Deletion failed. Check your connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, 1500);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure you want to remove yourself from this booking?")
                    .setCancelable(false)
                    .setTitle("Remove booking")
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
                                            deleteBooking();

                                        }
                                    }).start();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(deleteArray.length > 0) {
                                                if (deleteArray[1].equals("Success")) {
                                                    Toast.makeText(getActivity(), "Booking deleted", Toast.LENGTH_SHORT).show();
                                                    CalenderFragment CalendarFragment = new CalenderFragment();
                                                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                    ft.replace(R.id.contentFrame, CalendarFragment).commit();
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "Deletion failed. Check your connection", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, 1500);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    private enum dbanswers {
        Fail, Userhas, Error, Success;
    }

    public void addUserToBooking(){

        dialog = ProgressDialog.show(getActivity(), "One moment please", "Adding person to booking");
            new Thread(new Runnable() {
                public void run() {
                    getUserAndPlaceBooking();

                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(getUserArray.length > 0) {
                        dbanswers Answer = dbanswers.valueOf(getUserArray[1]);
                        switch (Answer) {
                            case Fail:
                                Toast toast = Toast.makeText(getActivity(), "No user with that number", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                                break;
                            case Userhas:
                                toast = Toast.makeText(getActivity(), "User is already on this booking", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                                break;
                            case Error:
                                toast = Toast.makeText(getActivity(), "Booking failed, please check connection", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                                break;
                            case Success:
                                toast = Toast.makeText(getActivity(), "User succesfully added to booking", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                                toast.show();
                                break;

                        }
                    } else {
                        Toast toast = Toast.makeText(getActivity(), "Adding failed. Check your connection", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                        toast.show();
                    }
                    dialog.dismiss();
                }
            }, 1500);
        }

        public boolean onTouchEvent(MotionEvent touchevent)
        {
            switch (touchevent.getAction())
            {
                // when user first touches the screen we get x and y coordinate
                case MotionEvent.ACTION_DOWN:
                {
                    x1 = touchevent.getX();
                    y1 = touchevent.getY();
                    break;
                }
                case MotionEvent.ACTION_UP:
                {
                    x2 = touchevent.getX();
                    y2 = touchevent.getY();

                    if (x2 - x1 > 200)
                    {
                        week = week - 7;
                        lvRoomsPopulate();
                        Toast.makeText(getActivity(), "Changed one week backwards", Toast.LENGTH_SHORT).show();
                    }

                    // if right to left sweep event on screen
                    if (x1 - x2 > 200)
                    {
                        week = week + 7;
                        lvRoomsPopulate();
                        Toast.makeText(getActivity(), "Changed one week forward", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                break;
            }
            return false;
        }

}
