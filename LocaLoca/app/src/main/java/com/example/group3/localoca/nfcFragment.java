package com.example.group3.localoca;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Asbjørn on 21-01-2015.
 */
public class nfcFragment extends Fragment {

    Button btnBackToMainMenu, btnBook, btnCalendar, btnSeeOnMap;
    TextView tvTitle, tvDescription;
    String[] separated;
    int building, bookable;
    String buildingString, bookableString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nfcread, container, false);

        btnBackToMainMenu = (Button) rootView.findViewById(R.id.btnBackToMain);
        btnBook = (Button) rootView.findViewById(R.id.btnBook);
        btnCalendar = (Button) rootView.findViewById(R.id.btnCalendar);
        btnSeeOnMap = (Button) rootView.findViewById(R.id.btnSeeOnMap);
        tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
        tvDescription = (TextView)rootView.findViewById(R.id.tvDescription);

        DrawerActivity DrawerActivity = new DrawerActivity();
        separated = DrawerActivity.NFCRead().split("#");
        tvTitle.setText(separated[0]);
        building = Integer.valueOf(separated[1]);
        switch (building){
            case 1: buildingString = "A.C. Meyers Vænge 15";
                break;
            case 2: buildingString = "Frederikskaj 6";
                break;
            case 3: buildingString = "Frederikskaj 10A";
                break;
            case 4: buildingString = "Frederikskaj 10B";
                break;
            case 5: buildingString = "Frederikskaj 12";
                break;
        }

        bookable = Integer.valueOf(separated[4]);
        switch (bookable){
            case 0: bookableString = "No";
                break;
            case 1: bookableString = "Yes";
                break;
        }
        if(bookable == 0){
            btnBook.setEnabled(false);
        } else{
            btnBook.setEnabled(true);
        }

        String sourceString =
                        "<b>" + "Building:" + "</b><br> " + buildingString + "<br><br>" +
                        "<b>" + "Floor:" + "</b><br> " + separated[2] + "<br><br>" +
                        "<b>" + "Room:" + "</b><br> " + separated[3] + "<br><br>" +
                        "<b>" + "Bookable:" + "</b><br> " + bookableString + "<br><br>";
        tvDescription.setText(Html.fromHtml(sourceString));

        //tvDescription.setText(DrawerActivity.NFCRead());

        btnBackToMainMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainMenuFragment MainMenuFragment = new MainMenuFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, MainMenuFragment).commit();
            }
        });


        btnBook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BookingFragmentNFC BookingFragmentNFC = new BookingFragmentNFC();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, BookingFragmentNFC, "booking").commit();
            }
        });

        btnCalendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "This feature is currently disabled", Toast.LENGTH_SHORT).show();
            }
        });

        btnSeeOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment MapFragment = new MapFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, MapFragment).commit();
            }
        });
        return rootView;
    }
}
