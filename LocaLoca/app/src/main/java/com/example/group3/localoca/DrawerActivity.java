package com.example.group3.localoca;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.MenuItem;
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
    private ActionBarDrawerToggle drawerListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawerlayout);
        drawerObjects=getResources().getStringArray(R.array.drawerObjets);
        listView =(ListView) findViewById(R.id.drawerList);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerObjects));
        listView.setOnItemClickListener(this);
        drawerListener= new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close ){
            @Override
            public void onDrawerClosed(View drawerView) {
                Toast.makeText(DrawerActivity.this, "Drawer Closed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Toast.makeText(DrawerActivity.this, "Drawer Opened", Toast.LENGTH_SHORT).show();
            }
        };
        drawerLayout.setDrawerListener(drawerListener);

        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerListener.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerListener.syncState();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id ) {
        Toast.makeText(this, drawerObjects[position]+" was selected ", Toast.LENGTH_LONG).show();
        selectItem(position);
    }
    public void selectItem(int position){
        listView.setItemChecked(position, true);
        setTitle(drawerObjects[position]);
    }
    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    
}