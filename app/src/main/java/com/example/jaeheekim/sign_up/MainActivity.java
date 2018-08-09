package com.example.jaeheekim.sign_up;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.jaeheekim.sign_up.device.BluetoothActivity;
import com.example.jaeheekim.sign_up.device.MyPolarBleReceiver;
import com.example.jaeheekim.sign_up.userManagement.ChangePasswordActivity;
import com.example.jaeheekim.sign_up.userManagement.DeleteAccountActivity;
import com.example.jaeheekim.sign_up.userManagement.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected TextView userName;
    private boolean flag = true;
    private long backPressedTime = 0;    // used by onBackPressed()
    public  static Activity mainActivity;
    private ProgressBar progressBar;
    private Handler handler = new Handler();

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView heart = (ImageView) findViewById(R.id.gif_image);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(heart);
        Glide.with(this).load(R.drawable.heart).into(gifImage);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        activatePolar();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(GlobalVar.getHeartRate());
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userName = findViewById(R.id.User_name);
        userName.setText(GlobalVar.getFname()+" "+GlobalVar.getLname());

        mainActivity = this;
    }

    private final MyPolarBleReceiver mPolarBleUpdateReceiver = new MyPolarBleReceiver() {};

    protected void activatePolar() {
        Log.w(this.getClass().getName(), "activatePolar()");
        registerReceiver(mPolarBleUpdateReceiver, makePolarGattUpdateIntentFilter());
    }

    protected void deactivatePolar() {
        unregisterReceiver(mPolarBleUpdateReceiver);
    }

    private static IntentFilter makePolarGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyPolarBleReceiver.ACTION_GATT_CONNECTED);
        intentFilter.addAction(MyPolarBleReceiver.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(MyPolarBleReceiver.ACTION_HR_DATA_AVAILABLE);
        return intentFilter;
    }

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
            String url = "http://teamf-iot.calit2.net/user";
            String values = "function=sign-out&token="+GlobalVar.getToken();
            if(flag == true) {
                NetworkTaskLogOut networkTaskLogOut = new NetworkTaskLogOut(url, values);
                networkTaskLogOut.execute();
            }
        }
    }

    // to communication with Server to check ID duplication
    public class NetworkTaskLogOut extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android
        // constructor
        public NetworkTaskLogOut(String url, String values) {
            this.url = url;
            this.values = values;
        }
        // start from here
        @Override
        protected String doInBackground(Void... params) {
            String result;       // Variable to store value from Server "url"
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // get result from this "url"
            return result;
        }
        // start after done doInBackground, result will be s in this function
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String msg;                         // msg to show to the user
            String title;                       // title of Msg
            try {
                JSONObject json_result = new JSONObject(s);             // make JSONObject to store data from the Server
                title = json_result.getString("status");                // title will be value of s's "status"
                // ID user enter is not in database
                if (title.equals("ok")) {
                    showDialog("ok","you Logout successfully");
                    GlobalVar.makeTokenExpired();
                    return;
                }
                else {  // ID user enter is in database already
                    msg = "sorry";     // show this massage
                    this.showDialog("Error", msg);
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                this.showDialog("Error", msg);
            } catch (ParseException e) {
                e.printStackTrace();
                msg = "Token expire Error";
                this.showDialog("Error", msg);
            }
            flag = false;
        }
        // to show the message to user
        public void showDialog(final String title, String Msg){
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setTitle(title);
            ad.setMessage(Msg);
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent tologin = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(tologin);
                    finish();
                }
            });
            ad.show();
        }
    }

    String menuUser[] = new String[] {"Log out", "Change password", "ID cancellation"};
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        for(int i = 0; i < menuUser.length; i++)
            menu.add(0, i+1, 0, menuUser[i]);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                try {
                    if(GlobalVar.isTokenExpired()) {
                        this.showDialog("Log out", "Are you sure??");
                    } else {
                        Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            case 2:
                try {
                    if(GlobalVar.isTokenExpired()) {
                        Intent toChangepw = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                        startActivity(toChangepw);
                    } else {
                        Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
            case 3:
                try {
                    if(GlobalVar.isTokenExpired()) {
                        Intent toDelete = new Intent(getApplicationContext(), DeleteAccountActivity.class);
                        startActivity(toDelete);
                    } else {
                        Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(toLogin);
                        finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return true;
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_current) {
            try {
                if(GlobalVar.isTokenExpired()) {
                    Intent toCurrent = new Intent(getApplicationContext(), CurrentLocationActivity.class);
                    startActivity(toCurrent);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_fingindRoute) {
            try {
                if(GlobalVar.isTokenExpired()) {
                    Intent toFinding = new Intent(getApplicationContext(), FindingRouteActivity.class);
                    startActivity(toFinding);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_slideshow) {
        } else if (id == R.id.nav_history) {
            Intent toFinding = new Intent(getApplicationContext(), CombinedChartActivity.class);
            startActivity(toFinding);
        } else if (id == R.id.nav_manage) {
            Intent toFinding = new Intent(getApplicationContext(), BluetoothActivity.class);
            startActivity(toFinding);
        } else if (id == R.id.nav_share) {
            Intent toFinding = new Intent(getApplicationContext(), SensorListViewActivity.class);
            startActivity(toFinding);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // to show the message to user
    public void showDialog(final String title, String msg){
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle(title);
        ad.setMessage(msg);
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String url = "http://teamf-iot.calit2.net/user";
                String values = "function=sign-out&token="+GlobalVar.getToken();
                if(flag == true) {
                    NetworkTaskLogOut networkTaskLogOut = new NetworkTaskLogOut(url, values);
                    networkTaskLogOut.execute();
                }
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        ad.show();
    }
}