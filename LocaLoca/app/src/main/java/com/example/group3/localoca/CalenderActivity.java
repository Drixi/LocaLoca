package com.example.group3.localoca;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Asbjørn on 03-11-2014.
 */
public class CalenderActivity extends Activity {

    globalvalues globalvalues = new globalvalues();
    String[] loginInfo = globalvalues.getLoginInfo();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        Toast.makeText(getBaseContext(), loginInfo[1], Toast.LENGTH_SHORT);
    }

}
