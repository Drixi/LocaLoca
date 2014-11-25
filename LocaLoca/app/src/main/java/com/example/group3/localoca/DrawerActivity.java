package com.example.group3.localoca;

import android.content.Context;
import android.content.res.Configuration;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by michael on 17-11-2014.
 */
public class DrawerActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ListView listView;
    private String[] drawerObjects;
    private ActionBarDrawerToggle drawerListener;
    private Myadapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        drawerLayout=(DrawerLayout) findViewById(R.id.drawerlayout);
        listView =(ListView) findViewById(R.id.drawerList);
        myAdapter = new Myadapter(this);
        listView.setAdapter(myAdapter);
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerListener.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id ) {
        selectItem(position);
    }

    public void selectItem(int position){
        listView.setItemChecked(position, true);
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

}

class Myadapter extends BaseAdapter {
    private Context context;
    String[] drawerObjects;
    int[] images = {R.drawable.xl_maplogo, R.drawable.xl_bookinglogo, R.drawable.xl_calendarlogo, R.drawable.xl_settingslogo, R.drawable.xl_aboutlogo, R.drawable.xl_logoutlogo};

    public Myadapter(Context context) {
        this.context=context;
        drawerObjects=context.getResources().getStringArray(R.array.drawerObjets);
    }

    @Override
    public int getCount() {
        return drawerObjects.length;
    }

    @Override
    public Object getItem(int position) {
        return drawerObjects[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.c_drawer_layout, parent, Boolean.parseBoolean(null));
        }
        else {
            row = convertView;
        }
        TextView titleText = (TextView) row.findViewById(R.id.textView);
        ImageView titleImage = (ImageView) row.findViewById(R.id.imageView);
        titleText.setText(drawerObjects[position]);
        titleImage.setImageResource(images[position]);
        return row;
    }
}