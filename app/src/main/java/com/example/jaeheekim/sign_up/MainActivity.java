package com.example.jaeheekim.sign_up;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected TextView userName;
    private boolean flag = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        userName = findViewById(R.id.User_name);
        userName.setText(GlobalVar.getFname()+" "+GlobalVar.getLname());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
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
                    showDialog("Error", msg);
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                showDialog("Error", msg);
            } catch (ParseException e) {
                e.printStackTrace();
                msg = "Token expire Error";
                showDialog("Error", msg);
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
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_changePassword) {
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
        } else if (id == R.id.nav_LogOut) {
            this.showDialog("Log out", "Are you sure??");
        } else if (id == R.id.nav_DeleteAccount) {
            try {
                if(GlobalVar.isTokenExpired()) {
                    Intent toDelete = new Intent(getApplicationContext(), DeleteAccountActivity.class);
                    startActivity(toDelete);
                    finish();
                } else {
                    Intent toLogin = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                    startActivity(toLogin);
                    finish();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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