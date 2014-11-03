package com.example.group3.localoca;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity {
    private Button loginBtn;
    private EditText etUser, etPassword;
    private TextView tvLoginStatus;
    int user = 1;
    String password = "p";
    HttpPost httppost;
    StringBuffer buffer;
    String response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    static String[] separated;
    ArrayList<String> list = new ArrayList<String>();
    SharedPreferences SM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
/*                String etUserString = etUser.getText().toString();
                int etUserint = Integer.valueOf(etUserString);

                if (user == etUserint && password.equals(etPassword.getText().toString())) {
                    tvLoginStatus.setText("Login Succes");
                    Intent switchtoregister = new Intent(v.getContext(), MainMenu.class);
                    startActivity(switchtoregister);
                    finish(3);
                }
                else{
                    tvLoginStatus.setText("Fail");
                }*/

                if(etUser.getText().length() > 0 && etPassword.getText().length() > 0){
                    dialog = ProgressDialog.show(LoginActivity.this, "",
                            "Validating user...", true);
                    new Thread(new Runnable() {
                        public void run() {
                            logindb();
                        }
                    }).start();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(separated[2].length() > 0){
                                dialog.dismiss();
                                String pinString = etPassword.getText().toString();
                                //int pinInteger = Integer.valueOf(pinString);
                                //int responseInteger = Integer.valueOf(separated[3]);
                                SM = getSharedPreferences("userrecord", 0);
                                Boolean islogin = SM.getBoolean("userlogin", false);
                                if(pinString.equals(separated[3])){
                                    SharedPreferences.Editor edit = SM.edit();
                                    edit.putBoolean("userlogin", true);
                                    edit.commit();
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    globalvalues globalvalues = new globalvalues();
                                    globalvalues.logininfo = separated;
                                    startActivity(new Intent(LoginActivity.this, ITE_MainMenu.class));
                                    finish();
                                }
                                else{
                                    showAlert();
                                }
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"No user with that ID", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    }, 2500);
                }
                else{
                    Toast.makeText(LoginActivity.this,"Please enter valid username and password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void logindb(){
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://pomsen.com/phpscripts/loginPOST.php");
            nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("email",etUser.getText().toString().trim()));
            //nameValuePairs.add(new BasicNameValuePair("pin",etPIN.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httppost, responseHandler);

            Log.d("drixi", response);
            separated = response.split("#");
            for (int i = 0; i < separated.length; ++i) {
                list.add(separated[i]);}


        }catch(IOException e){
            Log.e("drixi", "FEJLET");

        }

    }

    public void showAlert(){
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Login Error.");
                builder.setMessage("Wrong ID or PIN.")
                        .setCancelable(false)
                        .setPositiveButton("TRY AGAIN", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public static String[] GetValue(){
        return separated;
    }
}
