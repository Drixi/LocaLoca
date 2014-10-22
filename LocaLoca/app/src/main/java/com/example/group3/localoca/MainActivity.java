package com.example.group3.localoca;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;


public class MainActivity extends Activity {
    private Button loginBtn;
    private EditText etUser, etPassword;
    private TextView tvLoginStatus;
    int user = 1337;
    String password = "password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginBtn = (Button)findViewById(R.id.btnLogin);
        etUser = (EditText)findViewById(R.id.etUser);
        etPassword = (EditText)findViewById(R.id.etPassword);
        tvLoginStatus = (TextView)findViewById(R.id.tvLoginStatus);
        btnPressed();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void btnPressed() {
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String etUserString = etUser.getText().toString();
                int etUserint = Integer.valueOf(etUserString);

                if (user == etUserint && password.equals(etPassword.getText().toString())) {
                    tvLoginStatus.setText("Login Succes");
                }
                else{
                    tvLoginStatus.setText("Fail");
                }
            }
        });
    }

}
