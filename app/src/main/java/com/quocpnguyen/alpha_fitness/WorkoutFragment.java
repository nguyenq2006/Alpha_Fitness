package com.quocpnguyen.alpha_fitness;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import static android.content.Context.LOCATION_SERVICE;


public class WorkoutFragment extends Fragment implements LocationSource.OnLocationChangedListener{
    private Context mContext;
    //reference to component view
    private TextView distance_view;
    private TextView time_view;
    private Button start_bttn;
    private Button stop_bttn;
    private MapView mapView;
    private ImageView imageView;

    //variable for map view
    private static GoogleMap mMap;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View workout_detail = inflater.inflate(R.layout.workout_fragment, container, false);
        mContext = workout_detail.getContext();
        distance_view = (TextView) workout_detail.findViewById(R.id.distance);
        time_view = (TextView) workout_detail.findViewById(R.id.time_view);

        imageView = (ImageView) workout_detail.findViewById(R.id.profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(intent);
            }
        });
        start_bttn = (Button) workout_detail.findViewById(R.id.start_bttn);
        start_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWorkout();
            }
        });

        stop_bttn = (Button) workout_detail.findViewById(R.id.stop_bttn);
        stop_bttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopWorkout();
            }
        });

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapView = (MapView) workout_detail.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(new MapListener());

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);

        }

        return workout_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new android.os.Handler();
        stopWatch = StopWatch.getInstance();

//        locationTracking = new ArrayList<>();
    }

    @Override
    public void onResume(){
        super.onResume();

        if(stopWatch.isRunning()){
            stop_bttn.setEnabled(true);
            start_bttn.setEnabled(false);
            stop_bttn.setVisibility(View.VISIBLE);
            start_bttn.setVisibility(View.INVISIBLE);
            mHandler.post(workoutStartedRunnable);
            drawPolyline();
            String d = String.format("%.2f", WorkoutService.getInstance().calculateDistance());
            Toast.makeText(mContext, "total distance: " + d, Toast.LENGTH_SHORT).show();
            distance_view.setText(d);
        }

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        mLocation = locationManager.getLastKnownLocation(provider);
        LatLng current = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        if(mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        WorkoutService.initializeService(mContext);
        Intent workoutService = new Intent(getActivity(), WorkoutService.class);
        getActivity().stopService(workoutService);

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

            if(minutes%5 == 0 && seconds%60 == 0){
                WorkoutService.getInstance().stepsCalGraph();
            }

            mHandler.post(this);
        }
    };


    public void startWorkout(){
        stop_bttn.setEnabled(true);
        start_bttn.setEnabled(false);
        stop_bttn.setVisibility(View.VISIBLE);
        start_bttn.setVisibility(View.INVISIBLE);

        stopWatch.setStartTime(SystemClock.uptimeMillis());
        mHandler.post(workoutStartedRunnable);

        WorkoutService.initializeService(mContext);
        Intent workoutService = new Intent(getActivity(), WorkoutService.class);
        getActivity().startService(workoutService);

        mLocationSource.activate(this);
        receiver = new ResponseReceiver();
        IntentFilter broadcastFilter = new IntentFilter(ResponseReceiver.LOCAL_ACTION);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        localBroadcastManager.registerReceiver(receiver, broadcastFilter);
    }

    public void stopWorkout(){
        stop_bttn.setEnabled(false);
        start_bttn.setEnabled(true);
        stop_bttn.setVisibility(View.INVISIBLE);
        start_bttn.setVisibility(View.VISIBLE);

        mHandler.removeCallbacks(workoutStartedRunnable);

        getActivity().stopService(new Intent(getActivity(), WorkoutService.class));
        mLocationSource.deactivate();

        WorkoutService.getInstance().stopTracking();

        WorkoutService.initializeService(mContext);
        Intent workoutService = new Intent(getActivity(), WorkoutService.class);
        getActivity().stopService(workoutService);

        receiver = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), true);

            LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            mLocation = locationManager.getLastKnownLocation(provider);
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

    private class MapListener implements OnMapReadyCallback{

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            // Add a marker in Sydney, Australia,
            // and move the map's camera to the same location.

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }

            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            mMap.setMyLocationEnabled(true);
            if(mLocation!=null){
                LatLng current = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
            }
        }
    }
}