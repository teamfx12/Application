package com.example.jaeheekim.sign_up;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SensorListViewActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.InfoWindowAdapter {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Marker mSelectedMarker = null;
    private int listSize = 0;

    protected int toColor[] = {0x807fff00, 0x80ffff00, 0x80ff7f50, 0x80ff0000, 0x80b03060, 0x80a0522d};
    protected String formString[] = {"Good", "Moderate", "Unhealthy for Sensitive Groups",
            "Unhealthy", "Very Unhealthy", "Hazardous"};
/*
    private static final LatLng BRISBANE = new LatLng(32.890793 , -117.244088);
    private static final LatLng MELBOURNE = new LatLng(32.891521 , -117.237196);
    private static final LatLng SYDNEY = new LatLng(32.888096 , -117.235407);
    private static final LatLng ADELAIDE = new LatLng(32.882791 , -117.237879);
    private static final LatLng PERTH = new LatLng(32.881912 , -117.243562);*/
    private static final LatLng ZOE = new LatLng(32.886615 , -117.241287);

    //private static ArrayList<DeviceInfo> deviceList = new ArrayList<DeviceInfo>();
    //private static ArrayList<PolarInfo> polarList = new ArrayList<PolarInfo>();

    private static ArrayList<LatLng> locationArray = new ArrayList<LatLng>();

    private static ArrayList<Integer> AQIArray = new ArrayList<Integer>();

    private static ArrayList<String> boardMACList = new ArrayList<String>();
    private static ArrayList<String> boardNameList = new ArrayList<String>();

    int num = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list_view);

        Toolbar toolbar = findViewById(R.id.toolbar_list);
        setSupportActionBar(toolbar);
        setTitle("Sensor List");
        toolbar.setSubtitle("you've got");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.current_location);
        new OnMapAndViewReadyListener(mapFragment, this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if(GlobalVar.getFlag()) {
            GlobalVar.setFlag(false);
            String url = "http://teamf-iot.calit2.net/API/sensor";
            String msg = "function=list&token=" + GlobalVar.getToken();
            NetworkTaskListRequest networkTaskListRequest = new NetworkTaskListRequest(url, msg);
            networkTaskListRequest.execute();
        }

        enableMyLocationIfPermitted();
        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(ZOE));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ZOE, 70));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context mContext = getApplicationContext();

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                info.addView(title);

                return info;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent toChart = new Intent(SensorListViewActivity.this, CombinedChartActivity.class);
                toChart.putExtra("id", marker.getSnippet());
                toChart.putExtra("name", marker.getTitle());
                startActivity(toChart);
            }
        });

        mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
            @Override
            public void onInfoWindowLongClick(Marker marker) {
                showDialog("Double Check", "Do you want to DEREGIST your sensor?",marker.getSnippet());
            }
        });
    }

    private void addMarkersToMap() {

        for(int i = 0; i < num-1; i++){
            if(locationArray.get(i).longitude == -1){
                continue;
            }else if(AQIArray.get(i) == -1){
                mMap.addMarker(new MarkerOptions().position(locationArray.get(i))
                        .title("   Name : " +boardNameList.get(i)+"   ")
                        .snippet(boardMACList.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(getbmp("XX"))));

                mMap.addCircle(new CircleOptions()
                        .center(locationArray.get(i))
                        .radius(300)
                        .strokeWidth(1));
            } else {
                mMap.addMarker(new MarkerOptions().position(locationArray.get(i))
                        .title("   Name : " + boardNameList.get(i) + "   ")
                        .snippet(boardMACList.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(getbmp(String.valueOf(AQIArray.get(i))))));

                int color;
                int AQI = AQIArray.get(i);

                if(AQI > 300) { color = 5;
                } else if(AQI > 200) { color = 4;
                } else if(AQI > 150) { color = 3;
                } else if(AQI > 100) { color = 2;
                } else if(AQI > 50) { color = 1;
                } else { color = 0;
                }

                mMap.addCircle(new CircleOptions()
                        .center(locationArray.get(i))
                        .fillColor(toColor[color])
                        .radius(300)
                        .strokeWidth(1));
            }
        }
    }

    private Bitmap getbmp(String title){

        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(80, 80, conf);
        Canvas canvas = new Canvas(bmp);

        // paint defines the text color, stroke width and size
        Paint color = new Paint();
        color.setTextSize(45);
        color.setColor(Color.BLACK);

        // modify canvas
        canvas.drawText(title, 3, 60, color);
        return bmp;
    }

    private void enableMyLocationIfPermitted() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void showDefaultLocation() {

        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }
        }
    }

    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {

                @Override
                public boolean onMyLocationButtonClick() {
                    mMap.setMinZoomPreference(10);
                    return false;
                }
            };

    @Override
    public void onMapClick(LatLng latLng) {
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {
            // The showing info window has already been closed - that's the first thing to happen
            // when any marker is clicked.
            // Return true to indicate we have consumed the event and that we do not want the
            // the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).

            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;
        marker.showInfoWindow();

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    // to communication with Server to check ID duplication
    public class NetworkTaskListRequest extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android
        // constructor
        public NetworkTaskListRequest(String url, String values) {
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

                // make JSONObject to store data from the Server
                JSONArray jsonArray = new JSONArray(s);
                JSONObject info = jsonArray.getJSONObject(0);

                title = info.getString("status");

                //title = json_result.getString("status");                // title will be value of s's "status"
                // if user entered right email and first name
                if (title.equals("ok")) {
                    listSize = info.getInt("size");

                    if (listSize == 0) {
                        showDialog("No Sensor", "You don't have any sensor",null);
                        GlobalVar.setFlag(true);
                        return;
                    } else
                        num++;

                    for (; num <= listSize; num++) {
                        JSONObject jsonBoard = jsonArray.getJSONObject(num);

                        boardMACList.add(jsonBoard.getString("air_sensor_id"));
                        boardNameList.add(jsonBoard.getString("air_sensor_name"));
                        LatLng latLng = new LatLng(Double.valueOf(jsonBoard.getString("latitude")),
                                Double.valueOf(jsonBoard.getString("longitude")));
                        locationArray.add(latLng);
                        AQIArray.add(Integer.valueOf(jsonBoard.getInt("AQI")));
                    }
                    Toast.makeText(SensorListViewActivity.this, "all your Device", Toast.LENGTH_SHORT).show();
                    addMarkersToMap();
                } else {
                    msg = "Msg : "+ info.getString("msg");
                    Toast.makeText(SensorListViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                Toast.makeText(SensorListViewActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            GlobalVar.setFlag(true);
        }
    }

    private void showDialog(final String title, String Msg, final String deviceID){
        AlertDialog.Builder ad = new AlertDialog.Builder(SensorListViewActivity.this);
        ad.setTitle(title);
        ad.setMessage(Msg);
        if(title.equals("No Sensor")) {
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    dialog.dismiss();
                }
            });
        } else if (title.equals("Double Check")) {
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String url = "http://teamf-iot.calit2.net/API/sensor";
                    String value = "function=deregister-air&token="+GlobalVar.getToken()+
                            "&id="+deviceID;
                    NetworkTaskListDeregi networkTaskListDeregi = new NetworkTaskListDeregi(url,value);
                    networkTaskListDeregi.execute();
                    dialog.dismiss();
                }
            });
            ad.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }
        ad.show();
    }

    // to communication with Server to check ID duplication
    public class NetworkTaskListDeregi extends AsyncTask<Void, Void, String> {

        private String url;                         // Server URL
        private String values;                      // data passing to Server from Android
        // constructor
        public NetworkTaskListDeregi(String url, String values) {
            this.url = url;
            this.values = values;
        }
        //start from here
        @Override
        protected String doInBackground(Void... params) {
            String result;                      // Variable to store value from Server "url"
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values);     // get result from this "url"
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
                // if user can change their password,
                if (title.equals("ok")) {
                    msg = "Deregist successfully";
                    Toast.makeText(SensorListViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                    GlobalVar.setFlag(true);
                    return;
                } else {
                    msg = "Msg : " + json_result.getString("msg");
                    Toast.makeText(SensorListViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                msg = "JSON parsing Error";
                Toast.makeText(SensorListViewActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            GlobalVar.setFlag(true);
        }
    }
}
