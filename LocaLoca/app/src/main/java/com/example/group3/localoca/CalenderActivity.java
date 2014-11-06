package com.example.group3.localoca;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Asbjørn on 03-11-2014.
 */
public class CalenderActivity extends Activity {

    TextView tvTest, tvTest2;
    SharedPreferences userinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        tvTest = (TextView)findViewById(R.id.tvTest);
        tvTest2 = (TextView)findViewById(R.id.tvTest2);
        userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        String userid = userinfo.getString("userID", "");
        String test = userinfo.getString("userName", "");
        //tvTest.setText("User ID = " + userid + " name = " + test);
        tvTest.setText(getDateString(0));
        tvTest2.setText(getDateString(1) + " " + getDateString(2));
    }

    private String getDateString(int days) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE - dd/MM/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, days);
        return dateFormat.format(cal.getTime());
    }

}
