package com.quocpnguyen.alpha_fitness;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;



public class WorkoutActivity extends AppCompatActivity implements OnMapReadyCallback, LocationSource.OnLocationChangedListener{
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
    private Polyline polyline;
    private Location mLocation;
    public static LocationSource.OnLocationChangedListener mListener;
    private LocationSource mLocationSource = new LocationSource() {
        @Override
        public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
            mListener = onLocationChangedListener;
        }

        @Override
        public void deactivate() {
            mListener = null;
        }
    };

    private android.os.Handler mHandler;
    private StopWatch  stopWatch;
    private long timeInMilli = 0L;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentManager fm= getSupportFragmentManager();
            fm.beginTransaction()
                    .add(new WorkDetailFragment(), "WorkoutDetail")
            .commit();
        }

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
//        locationTracking = new ArrayList<>();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        mLocation = locationManager.getLastKnownLocation(provider);
    }

    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter broadcastFilter = new IntentFilter(ResponseReceiver.LOCAL_ACTION);
        receiver = new ResponseReceiver();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver, broadcastFilter);


    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        WorkoutService.initializeService(this);
        Intent workoutService = new Intent(this, WorkoutService.class);
        stopService(workoutService);

    }
    //Runnable object for stop watch
    private Runnable workoutStartedRunnable = new Runnable() {
        @Override
        public void run() {
            timeInMilli = SystemClock.uptimeMillis() - stopWatch.getStartTime();
            stopWatch.setTimeUpdate(stopWatch.getStoreTime() + timeInMilli);
            int time = (int) (stopWatch.getTimeUpdate() / 1000);

            int minutes = time / 60;
            int seconds = time % 60;

            String strTime = String.format("%02d:%02d", minutes, seconds);
            time_view.setText(strTime);

            mHandler.post(this);
        }
    };


    public void startStopWatch(View view){
        stop_bttn.setEnabled(true);
        start_bttn.setEnabled(false);
        stop_bttn.setVisibility(View.VISIBLE);
        start_bttn.setVisibility(View.INVISIBLE);

        stopWatch.setStartTime(SystemClock.uptimeMillis());
        mHandler.post(workoutStartedRunnable);

        WorkoutService.initializeService(this);
        Intent workoutService = new Intent(this, WorkoutService.class);
        startService(workoutService);

        mLocationSource.activate(this);
    }

    public void stopStopWatch(View view){
        stop_bttn.setEnabled(false);
        start_bttn.setEnabled(true);
        stop_bttn.setVisibility(View.INVISIBLE);
        start_bttn.setVisibility(View.VISIBLE);

        mHandler.removeCallbacks(workoutStartedRunnable);
        stopWatch.resetWatchTime();

        stopService(new Intent(this, WorkoutService.class));
        mLocationSource.deactivate();

        WorkoutService.initializeService(this);
        Intent workoutService = new Intent(this, WorkoutService.class);
        stopService(workoutService);
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
        if(mLocation!=null){
            LatLng current = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
        }
    }


    private void drawPolyline(){
        mMap.clear();

        PolylineOptions options = new PolylineOptions()
                .color(Color.BLUE)
                .width(5)
                .geodesic(true);

        for(LatLng point : WorkoutService.locationTracking){
            options.add(point);
        }
        polyline = mMap.addPolyline(options);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        LatLng current = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
    }


    public class ResponseReceiver extends BroadcastReceiver {

        public static final String LOCAL_ACTION = "Workout Update";

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String totalDistance = extras.getString("Total Distance");
            distance_view.setText(totalDistance);
            drawPolyline();
        }
    }
}