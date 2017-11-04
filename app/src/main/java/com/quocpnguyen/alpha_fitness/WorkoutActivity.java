package com.quocpnguyen.alpha_fitness;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;


/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link WorkoutDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener, LocationListener {
    //reference to component view
    private TextView distance_view;
    private TextView time_view;
    private Button start_bttn;
    private Button stop_bttn;

    //variable for map view
    private GoogleMap mMap;
    private LocationManager locationManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 20;
    private String provider;
    private LocationSource.OnLocationChangedListener mListener;
    private LocationSource mLocationSource;
    private Polyline polyline;
    private ArrayList<LatLng> locationTracking;

    private android.os.Handler mHandler;
    private StopWatch  stopWatch;
    private long timeInMilli = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        distance_view = (TextView) findViewById(R.id.distance);
        time_view = (TextView) findViewById(R.id.time_view);
        start_bttn = (Button) findViewById(R.id.start_bttn);
        stop_bttn = (Button) findViewById(R.id.stop_bttn);

        mHandler = new android.os.Handler();
        stopWatch = StopWatch.getInstance();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        }

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationTracking = new ArrayList<>();
    }

    //Runnable object for stop watch
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timeInMilli = SystemClock.uptimeMillis() - stopWatch.getStartTime();
            stopWatch.setTimeUpdate(stopWatch.getStoreTime() + timeInMilli);
            int time = (int) (stopWatch.getTimeUpdate() / 1000);

            int minutes = time / 60;
            int seconds = time % 60;
            int milliseconds = (int) (stopWatch.getTimeUpdate() % 1000);

            String strTime = String.format("%02d:%02d:%02d", minutes, seconds, milliseconds);
            time_view.setText(strTime);

            mHandler.postDelayed(this, 0);
        }
    };


    public void startStopWatch(View view){
        stop_bttn.setEnabled(true);
        start_bttn.setEnabled(false);
        stop_bttn.setVisibility(View.VISIBLE);
        start_bttn.setVisibility(View.INVISIBLE);

        stopWatch.setStartTime(SystemClock.uptimeMillis());
        mHandler.postDelayed(timerRunnable,0);

    }

    public void stopStopWatch(View view){
        stop_bttn.setEnabled(false);
        start_bttn.setEnabled(true);
        stop_bttn.setVisibility(View.INVISIBLE);
        start_bttn.setVisibility(View.VISIBLE);

        mHandler.removeCallbacks(timerRunnable);
        stopWatch.resetWatchTime();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMyLocationEnabled(true);
        mLocationSource = new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {

            }
        };
        mLocationSource.activate(this);

        locationManager.requestLocationUpdates(provider, 5000, 1, this);
//        Location location = locationManager.getLastKnownLocation(provider);
//
//        Log.d("Map Location", "Lat: " + location.getLatitude() + "; Long: " + location.getLongitude());
//        LatLng coor = new LatLng(location.getLatitude(), location.getLongitude());
//        locationTracking.add(coor);
//
//        CameraUpdate currentLocatiion = CameraUpdateFactory.newLatLngZoom(coor, 15);
//        mMap.animateCamera(currentLocatiion);
        drawPolyline();

    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        LatLng point = new LatLng(latitude, longtitude);
        locationTracking.add(point);

        CameraUpdate currentLocatiion = CameraUpdateFactory.newLatLngZoom(point, 15);
        mMap.animateCamera(currentLocatiion);
        drawPolyline();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void drawPolyline(){
        mMap.clear();

        PolylineOptions options = new PolylineOptions()
                .color(Color.BLUE)
                .width(5)
                .geodesic(true);

        for(LatLng point : locationTracking){
            options.add(point);
        }
        polyline = mMap.addPolyline(options);
    }
}
