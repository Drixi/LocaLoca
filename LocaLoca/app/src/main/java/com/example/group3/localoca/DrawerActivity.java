package com.example.group3.localoca;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private MainMenuFragment contentView;
    private Toast backtoast;
    SharedPreferences userinfo;
    private MainMenuFragment MainMenu;
    boolean dOpen;


    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private NfcAdapter mNfcAdapter;
    public static String ReadResult = "";
    public static int nfcisread = 0;
    private PendingIntent mPendingIntent;
    public static List<NFCListener> nfcListeners = new ArrayList<NFCListener>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_activity_layout);
        userinfo = getSharedPreferences("userinfo", MODE_PRIVATE);
        getActionBar().setTitle("Main Menu");
        if (savedInstanceState == null) {
            contentView = new MainMenuFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(R.id.contentFrame, contentView).commit();
        }

        drawerLayout=(DrawerLayout) findViewById(R.id.drawerlayout);
        listView =(ListView) findViewById(R.id.drawerList);
        myAdapter = new Myadapter(this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(this);
        drawerLayout.setDrawerListener(drawerListener);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout.openDrawer(Gravity.START);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(Gravity.START);
            }
        }, 1500);




        drawerListener= new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer,
                R.string.drawer_open, R.string.drawer_close ){
            @Override
            public void onDrawerClosed(View drawerView) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerListener.onOptionsItemSelected(item))
        {

            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if(dOpen != true) {
                dOpen = true;
                drawerLayout.openDrawer(Gravity.START);
                return true;
            } else {
                dOpen = false;
                drawerLayout.closeDrawer(Gravity.START);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void onBackPressed() {
        contentView = new MainMenuFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.contentFrame, contentView).commit();
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
        System.out.println(position);
        drawerLayout.closeDrawers();
        switch(position) {
            case 0:
                getActionBar().setTitle("Main Menu");
                contentView = new MainMenuFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, contentView).commit();
                break;
            case 1:
                getActionBar().setTitle("Map");
                MapFragment buttonMap = new MapFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonMap).addToBackStack("tag").commit();
                break;
            case 2:
                getActionBar().setTitle("Book Room");
                BookingFragment buttonBooking = new BookingFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonBooking, "booking").commit();
                break;
            case 3:
                getActionBar().setTitle("Calender");
                CalenderFragment buttonCalendar = new CalenderFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonCalendar).commit();
                break;
            case 4:
                Toast.makeText(DrawerActivity.this, "This feature is currently disabled", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                getActionBar().setTitle("About");
                AboutFragment buttonAbout = new AboutFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonAbout, "about").commit();
                break;
            case 6:
                alertbuilderLogout();
                break;
        }
    }

    public void selectItem(int position){
        listView.setItemChecked(position, true);
    }

    public void setTitle(String title){
        getSupportActionBar().setTitle(title);
    }

    public void alertbuilderLogout(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerActivity.this);
        builder.setMessage("Are you sure you want to log out")
                .setCancelable(false)
                .setTitle("Log out")
                .setPositiveButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int id) {
                                SharedPreferences settings = DrawerActivity.this.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                                settings.edit().remove("userEmail").commit();
                                settings.edit().remove("userPassword").commit();
                                Toast.makeText(DrawerActivity.this, "User logged out", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(DrawerActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}

class Myadapter extends BaseAdapter {
    private Context context;
    String[] drawerObjects;
    int[] images = {R.drawable.xl_homelogo, R.drawable.xl_maplogo, R.drawable.xl_bookinglogo, R.drawable.xl_calendarlogo, R.drawable.xl_settingslogo, R.drawable.xl_aboutlogo, R.drawable.xl_logoutlogo};

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
            row = inflater.inflate(R.layout.drawer_layout, parent, Boolean.parseBoolean(null));
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