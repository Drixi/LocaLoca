package com.example.group3.localoca;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by michael on 10/27/2014.
 */

public class ITE_MainMenu extends Activity {

    private Button btn_main_1;
    private Button btn_main_2;
    private Button btn_main_3;
    private Button btn_main_4;
    private Toast backtoast;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ite_main_menu);
        btn_main_1 = (Button) findViewById(R.id.btn_main1);
        btn_main_2 = (Button) findViewById(R.id.btn_main2);
        btn_main_3 = (Button) findViewById(R.id.btn_main3);
        btn_main_4 = (Button) findViewById(R.id.btn_main4);
        logo = (ImageView) findViewById(R.id.img_menu_logo);


    // Bottom left button
        btn_main_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchtoregister = new Intent(v.getContext(), RoomList.class);
                startActivity(switchtoregister);
            }
        });
    // Bottom right button
        btn_main_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CONTENT HERE //
            }
        });
    // Top left button
        btn_main_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CONTENT HERE //
            }
        });
    // Top right button
        btn_main_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent switchtoregister = new Intent(v.getContext(), CalenderActivity.class);
                startActivity(switchtoregister);
            }
        });

    }
    public void onBackPressed() {
        //if(USER_IS_GOING_TO_EXIT) {
            if(backtoast!=null&&backtoast.getView().getWindowToken()!=null) {
                finish();
            } else {
                backtoast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
                backtoast.show();
            }
       // } else {
         //   //other stuff...
           // super.onBackPressed();
        //}
    }

}
