package com.example.group3.localoca;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Asbjørn on 07-11-2014.
 */
public class BookingActivity extends Activity{

    TextView tvBooktitle, tvBookBuilding, tvBookFloor, tvBookRoom, tvBookDate, tvBookTimeStart, tvBookTimeEnd;
    Spinner sBuilding, sFloor, sRoom, sTimeStart, sTimeEnd;
    EditText etBookDate;
    Object BuildingChoosen, FloorChoosen;
    int iCurrentSelection;
    String[] buildingList = {"", "A.C. Meyers Vænge 15","Frederikskaj 6", "Frederikskaj 10A", "Frederikskaj 10B", "Frederikskaj 12"};
    String[] FloorList = {"", "Ground Floor", "1st Floor", "2nd Floor", "3rd Floor", "4th Floor"};
    String[] fk61st = {"", "203","203A","203B","203C","203D","203E","204","205",
            "207","208","209","210","211","212","213","214","215","216","217","218","219","220"};
    String[] TimeChoices = {"", "00:00", "01:00", "02:00", "03:00", "04:00", "05:00", "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00", "23:00", "24:00"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        tvBookBuilding = (TextView)findViewById(R.id.tvBookBuilding);
        tvBookFloor = (TextView)findViewById(R.id.tvBookFloor);
        tvBookRoom = (TextView)findViewById(R.id.tvBookRoom);
        tvBookDate = (TextView)findViewById(R.id.tvBookDate);
        tvBookTimeStart = (TextView)findViewById(R.id.tvBookTimeStart);
        tvBookTimeEnd = (TextView)findViewById(R.id.tvBookTimeEnd);
        sBuilding = (Spinner)findViewById(R.id.sBuilding);
        sFloor = (Spinner)findViewById(R.id.sFloor);
        sRoom = (Spinner)findViewById(R.id.sRoom);
        sTimeStart = (Spinner)findViewById(R.id.sTimeStart);
        sTimeEnd = (Spinner)findViewById(R.id.sTimeEnd);
        etBookDate = (EditText)findViewById(R.id.etBookDate);

        tvBookFloor.setVisibility(View.INVISIBLE);
        tvBookRoom.setVisibility(View.INVISIBLE);
        tvBookDate.setVisibility(View.INVISIBLE);
        tvBookTimeStart.setVisibility(View.INVISIBLE);
        tvBookTimeEnd.setVisibility(View.INVISIBLE);
        sFloor.setVisibility(View.INVISIBLE);
        sRoom.setVisibility(View.INVISIBLE);
        sTimeStart.setVisibility(View.INVISIBLE);
        sTimeEnd.setVisibility(View.INVISIBLE);
        etBookDate.setVisibility(View.INVISIBLE);



        arrayadapter(buildingList, sBuilding);
        arrayadapter(FloorList, sFloor);
        arrayadapter(fk61st, sRoom);
        arrayadapter(TimeChoices, sTimeStart);
        arrayadapter(TimeChoices, sTimeEnd);

        sBuildingClick();
    }

    public void arrayadapter(String[] list, Spinner spinner){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);
    }

    public void sClick(Spinner spinnerClick, Object o, TextView tvVisible, Spinner sVisible, EditText etVisible){
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                BuildingChoosen = sBuilding.getItemAtPosition(position);
                tvBookFloor.setVisibility(View.VISIBLE);
                sFloor.setVisibility(View.VISIBLE);
                sBuilding.setEnabled(false);

            }
            public void onNothingSelected(AdapterView<?> arg0) { }
        });

    }

    public void sBuildingClick(){
        sBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
                if (iCurrentSelection != position){
                    BuildingChoosen = sBuilding.getItemAtPosition(position);
                    tvBookFloor.setVisibility(View.VISIBLE);
                    sFloor.setVisibility(View.VISIBLE);
                    sBuilding.setEnabled(false);
                }
                iCurrentSelection = position;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }
}
