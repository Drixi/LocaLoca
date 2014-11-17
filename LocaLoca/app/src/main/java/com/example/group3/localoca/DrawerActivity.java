package com.example.group3.localoca;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

/**
 * Created by michael on 17-11-2014.
 */
public class DrawerActivity extends ActionBarActivity{

    private DrawerLayout drawerLayout;
    private ListView drawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawerlayout);
        drawerList=(ListView) findViewById(R.id.drawerList);
    }
}
