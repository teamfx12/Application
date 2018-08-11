package com.example.jaeheekim.sign_up;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    protected TextView userName;
    private long backPressedTime = 0;    // used by onBackPressed()
    public static Activity mainActivity;
    private ProgressBar progressBar;
    private TextView bpm;
    private TextView pnn;
    private Handler handler = new Handler();
    private GPSInfo gps;
    private boolean polar = true;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = new GPSInfo(MainActivity.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            LatLng myLocation = new LatLng(latitude,longitude);
            GlobalVar.setmLocation(myLocation);
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bpm = (TextView) findViewById(R.id.bpm);
        pnn = (TextView) findViewById(R.id.pnn);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userName = findViewById(R.id.User_name);
        userName.setText(GlobalVar.getFname() + " " + GlobalVar.getLname());

        mainActivity = this;
    }

    private String makeJSONObject() throws JSONException {
        JSONObject info = new JSONObject();

        try {
            info.put("function","transfer_polar");
            info.put("token",GlobalVar.getToken());
            info.put("latitude",String.valueOf(GlobalVar.getmLocation().latitude));
            info.put("longitude",String.valueOf(GlobalVar.getmLocation().longitude));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject polarValue = new JSONObject();

        try {
            polarValue.put("collected_time",GlobalVar.currentDate());
            polarValue.put("heartrate",String.valueOf(GlobalVar.getHeartRate()));
            polarValue.put("pnn50",String.valueOf(GlobalVar.getPnnPercent()));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(info);
        jsonArray.put(polarValue);

        String jsonStr = jsonArray.toString();

        return jsonStr;
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

            if(GlobalVar.getFlag() == true) {
                GlobalVar.setFlag(false);
                NetworkTaskLogOut networkTaskLogOut = new NetworkTaskLogOut(url, values);
                networkTaskLogOut.execute();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        deactivatePolar();

    }

    public void onClickPolar(View view) {
        if(polar) {
            activatePolar();
            Thread polarThread = new Thread(new Runnable() {
                private boolean polarStop = false;

                @Override
                public void run() {
                    while (true) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!polarStop) {
                                    progressBar.setProgress(GlobalVar.getHeartRate());
                                    bpm.setText(GlobalVar.getHeartRate() + " bpm");
                                    bpm.setText(GlobalVar.getPnnPercent() + " %");
                                    if (GlobalVar.getHeartRate() < 60)
                                        bpm.setTextColor(Color.DKGRAY);
                                    else if (GlobalVar.getHeartRate() < 80)
                                        bpm.setTextColor(Color.GREEN);
                                    else if (GlobalVar.getHeartRate() < 100)
                                        bpm.setTextColor(Color.BLUE);
                                    else
                                        bpm.setTextColor(Color.RED);

                                    if (GlobalVar.getFlag()) {
                                        GlobalVar.setFlag(false);
                                        String url = "http://teamf-iot.calit2.net/API/transfer";
                                        try {
                                            String value = makeJSONObject();
                                            NetworkTaskPolar networkTaskPolar = new NetworkTaskPolar(url, value);
                                            networkTaskPolar.execute();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                protected void setPolarStop() {
                    polarStop = false;
                }
            });
            polarThread.start();
        } else {

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
                GlobalVar.setFlag(true);
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
            GlobalVar.setFlag(true);
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

    // to communication with Server to check ID duplication
    public class NetworkTaskPolar extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android
        // constructor
        public NetworkTaskPolar(String url, String values) {
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
                if (!title.equals("ok")) {
                    msg = json_result.getString("msg");     // show this massage
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                } else {
                    if(polar) {
                        Toast.makeText(MainActivity.this, "Polar sensor is connected", Toast.LENGTH_LONG).show();
                        polar = false;
                    }
                }
            } catch (JSONException e) {
                msg = "Error: JSON parsing Error";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            }
            GlobalVar.setFlag(true);
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
                    Intent toCurrent = new Intent(getApplicationContext(), CurrentAllSensorActivity.class);
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
                if(GlobalVar.getFlag() == true) {
                    GlobalVar.setFlag(false);
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