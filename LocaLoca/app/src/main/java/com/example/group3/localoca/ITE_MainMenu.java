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
                Intent switchtoregister = new Intent(v.getContext(), MainMenu.class);
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
                // CONTENT HERE //
            }
        });

    }
    public class XYZ extends Activity {
        private long backPressedTime = 0;    // used by onBackPressed()


        @Override
        public void onBackPressed() {        // to prevent irritating accidental logouts
            long t = System.currentTimeMillis();
            if (t - backPressedTime > 2000) {    // 2 secs
                backPressedTime = t;
                Toast.makeText(this, "Press back again to logout",
                        Toast.LENGTH_SHORT).show();
            } else {    // this guy is serious
                // clean up
                super.onBackPressed();       // bye
            }
        }
    }

}
