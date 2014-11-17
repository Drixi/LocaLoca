package com.example.group3.localoca;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by michael on 17-11-2014.
 */
public class DrawerActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] drawerObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawerlayout);
        drawerObjects=getResources().getStringArray(R.array.drawerObjets);
        listView =(ListView) findViewById(R.id.drawerList);
        listView.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1));
        listView.setOnItemClickListener(this);

    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id ) {
        Toast.makeText(this, drawerObjects[position]+" was selected ", Toast.LENGTH_LONG).show();
    }


    
}