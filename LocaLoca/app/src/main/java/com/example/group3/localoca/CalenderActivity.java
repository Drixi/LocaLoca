package com.example.group3.localoca;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Asbjørn on 03-11-2014.
 */
public class CalenderActivity extends Activity {

    TextView tvDay1, tvDay2, tvDay3, tvDay4, tvDay5, tvDay6, tvDay7;
    SharedPreferences userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        tvDay1 = (TextView)findViewById(R.id.tvDay1);
        tvDay2 = (TextView)findViewById(R.id.tvDay2);
        tvDay3 = (TextView)findViewById(R.id.tvDay3);
        tvDay4 = (TextView)findViewById(R.id.tvDay4);
        tvDay5 = (TextView)findViewById(R.id.tvDay5);
        tvDay6 = (TextView)findViewById(R.id.tvDay6);
        tvDay7 = (TextView)findViewById(R.id.tvDay7);
        userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        String userid = userinfo.getString("userID", "");
        String test = userinfo.getString("userName", "");
        //tvDay1.setText("User ID = " + userid + " name = " + test);
        tvDay1.setText(getDateString(0));
        tvDay2.setText(getDateString(1));
        tvDay3.setText(getDateString(2));
        tvDay4.setText(getDateString(3));
        tvDay5.setText(getDateString(4));
        tvDay6.setText(getDateString(5));
        tvDay7.setText(getDateString(6));
    }

    private String getDateString(int days) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return dateFormat.format(cal.getTime());
    }

}
