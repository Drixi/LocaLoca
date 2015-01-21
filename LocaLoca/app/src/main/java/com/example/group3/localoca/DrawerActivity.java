package com.example.group3.localoca;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.util.Log;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    //public static int nfcisread = 0;
    private PendingIntent mPendingIntent;
    //public static List<NFCListener> nfcListeners = new ArrayList<NFCListener>();
    //private NFCListener nfcListener;

    /*public static void addNFCListener(NFCListener listener) {
        nfcListeners.add(listener);
    }

    public static void removeNFCListener(NFCListener listener) {
        nfcListeners.remove(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeNFCListener(nfcListener);
    }*/

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

        mPendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());

        /*nfcListener = new NFCListener() {
            @Override
            public void NFCRead(String data) {
                getActionBar().setTitle("Book Room");
                BookingFragment buttonBooking = new BookingFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonBooking, "booking").commit();
            }
        };
        addNFCListener(nfcListener);*/
    }


    @Override
    public void onNewIntent(Intent intent)
    {
        setIntent(intent);
        handleIntent(intent);
    }

    //PLy
    @Override
    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
    }

    //PLy
    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    //NFC Stuff
    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }


        }
    }
    public class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException {


            byte[] payload = record.getPayload();

            // Get the Text Encoding
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";

            // Get the Language Code
            int languageCodeLength = payload[0] & 0063;

            // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
            // e.g. "en"

            // Get the Text
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                getActionBar().setTitle("NFC Scanned");
                nfcFragment nfcFragment = new nfcFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, nfcFragment, "booking").commit();
                ReadResult = result;
                /*nfcisread = 1;
                System.out.print(result);

                for (NFCListener listener : nfcListeners) {
                    listener.NFCRead(result);
                }*/
            }
        }
    }

    public static String NFCRead(){
        return ReadResult;
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