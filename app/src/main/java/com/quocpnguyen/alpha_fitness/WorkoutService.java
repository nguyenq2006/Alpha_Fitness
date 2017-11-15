package com.quocpnguyen.alpha_fitness;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by cs on 10/28/17.
 */

public class WorkoutService extends Service implements LocationListener, LocationSource.OnLocationChangedListener, SensorEventListener {

    private LocationManager locationManager;
    private String provider;
    public static ArrayList<LatLng> locationTracking;
    private static Context mContext;
    private Handler mHandler;

    private static float totalDist;
    private long steps = 0;
    private Sensor stepSensor;
    private SensorManager sManager;



    public static void initializeService(Context context){
        WorkoutService.mContext = context;
        totalDist = 0;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        locationTracking = new ArrayList<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WorkoutService.this, "starting intent service...", Toast.LENGTH_SHORT).show();
            }
        });

        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);
        locationManager.requestLocationUpdates(provider, 0, 0, WorkoutService.this);

        sManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//        if(stepSensor != null) {
//            sManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
//        } else {
//            stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//        }

        PackageManager packageManager = mContext.getPackageManager();
        if(!(packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                && packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_DETECTOR))){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WorkoutService.this, "cannot find step detector sensor, nor step counter sensor", Toast.LENGTH_SHORT).show();
                }
            });
        }

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        locationManager.removeUpdates(WorkoutService.this);
        sManager.unregisterListener(this, stepSensor);
    }


    @Override
    public void onLocationChanged(Location location) {

//        if (WorkoutActivity.mListener != null) {
//            WorkoutActivity.mListener.onLocationChanged(location);
//        }

        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        LatLng point = new LatLng(latitude, longtitude);
        if(!locationTracking.contains(point)){
            locationTracking.add(point);
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(WorkoutActivity.ResponseReceiver.LOCAL_ACTION);
            String distance = String.format("%.2f",calculateDistance());
            broadcastIntent.putExtra("Total Distance", distance);
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.sendBroadcast(broadcastIntent);
        }
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

    private double calculateDistance(){
        int size = locationTracking.size();
        if(size > 2){
            float[] result = new float[2];
            LatLng prev = locationTracking.get(size-2);
            LatLng cur = locationTracking.get(size-1);
            Location.distanceBetween(prev.latitude, prev.longitude, cur.latitude, cur.longitude, result);
            totalDist += result[0];
        }
        return toMiles(totalDist);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
        }
        Log.d(WorkoutService.class.getSimpleName(), "steps counter = " + steps);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(WorkoutService.this, "Steps count: " + steps, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private double toMiles(float meter){
        return (double) (meter/1609.344);
    }
}
