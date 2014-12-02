package com.example.group3.localoca;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by michael on 10/27/2014.
 */

public class MainMenuFragment extends Fragment {

    Button btn_main_1, btn_main_2, btn_main_3, btn_main_4;
    Toast backtoast;
    ImageView logo;
    private MapFragment buttonMap;
    private BookingFragment buttonBooking;
    private CalenderFragment buttonCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_menu, container, false);
        btn_main_1 = (Button) rootView.findViewById(R.id.btn_main1);
        btn_main_2 = (Button) rootView.findViewById(R.id.btn_main2);
        btn_main_3 = (Button) rootView.findViewById(R.id.btn_main3);
        btn_main_4 = (Button) rootView.findViewById(R.id.btn_main4);
        logo = (ImageView) rootView.findViewById(R.id.img_menu_logo);


        // Bottom left button
        btn_main_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonMap = new MapFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonMap).commit();
            }
        });

        // Bottom right button
        btn_main_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonBooking = new BookingFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonBooking).commit();
            }
        });

        // Top right button
        btn_main_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonCalendar = new CalenderFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.contentFrame, buttonCalendar).commit();
            }
        });
        return rootView;
    }
}
