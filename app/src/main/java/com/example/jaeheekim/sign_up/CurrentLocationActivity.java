package com.example.jaeheekim.sign_up;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static java.security.AccessController.getContext;


public class CurrentLocationActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.InfoWindowAdapter {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private Marker mSelectedMarker = null;

    protected int toColor[] = {0x807fff00, 0x80ffff00, 0x80ff7f50, 0x80ff0000, 0x80b03060, 0x80a0522d};
    protected String formString[] = {"Good", "Moderate", "Unhealthy for Sensitive Groups",
            "Unhealthy", "Very Unhealthy", "Hazardous"};

    private static final LatLng BRISBANE = new LatLng(32.890793 , -117.244088);
    private static final LatLng MELBOURNE = new LatLng(32.891521 , -117.237196);
    private static final LatLng SYDNEY = new LatLng(32.888096 , -117.235407);
    private static final LatLng ADELAIDE = new LatLng(32.882791 , -117.237879);
    private static final LatLng PERTH = new LatLng(32.881912 , -117.243562);
    private static final LatLng ZOE = new LatLng(32.886615 , -117.241287);

    static final LatLng locationArray[] = {BRISBANE, MELBOURNE, SYDNEY, ADELAIDE, PERTH, ZOE};

    String api[] = new String[] {"35","58","124","166","260","380"};
    String CO2[] = new String[] {"35","20","2","166","255","380"};
    String O3[] = new String[] {"20","58","124","40","140","211"};
    String NO2[] = new String[] {"34","14","42","98","260","300"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        Toolbar tb = findViewById(R.id.toolbar_current);
        setSupportActionBar(tb);
        setTitle("Current Air Condition");
        tb.setSubtitle("Your Location");
        tb.setTitleTextColor(Color.WHITE);
        tb.setSubtitleTextColor(Color.WHITE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.current_location);
        new OnMapAndViewReadyListener(mapFragment, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())) {
            case 1:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case 2:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "Type : HYBRID");
        menu.add(0, 2, 0, "Type : NORMAL");
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        addMarkersToMap();

        mMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        enableMyLocationIfPermitted();

        mMap.getUiSettings().setZoomControlsEnabled(true);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(PERTH)
                .include(SYDNEY)
                .include(ADELAIDE)
                .include(BRISBANE)
                .include(MELBOURNE)
                .include(ZOE)
                .build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 300));
        mMap.setMinZoomPreference(5);

        for (int i = 0; i < 6; i++) {
            mMap.addCircle(new CircleOptions()
                    .center(locationArray[i])
                    .fillColor(toColor[i])
                    .radius(300)
                    .strokeWidth(1));
        }

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

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent toChart = new Intent(getApplicationContext(), CombinedChartActivity.class);
                toChart.putExtra("Location", "  ");
                startActivity(toChart);
            }
        });
    }

    private void addMarkersToMap() {

        for(int i = 0; i < 6; i++){
            mMap.addMarker(new MarkerOptions().position(locationArray[i]).title("API : "+api[i])
                .snippet("CO2 : "+CO2[i]+"\nO3 : "+O3[i]+"\nNO2 : "+NO2[i])
                .icon(BitmapDescriptorFactory.fromBitmap(getbmp(api[i]))));
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
}
