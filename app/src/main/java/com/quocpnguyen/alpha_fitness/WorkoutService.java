package com.quocpnguyen.alpha_fitness;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.model.LatLng;
import com.quocpnguyen.alpha_fitness.DatabaseManagment.DatabaseManager;
import com.quocpnguyen.alpha_fitness.DatabaseManagment.WorkoutRecord;
import com.quocpnguyen.alpha_fitness.GraphUtil.GraphData;
import com.quocpnguyen.alpha_fitness.StepCounterUtil.StepDetector;
import com.quocpnguyen.alpha_fitness.StepCounterUtil.StepListener;

import java.util.ArrayList;

/**
 * Created by cs on 10/28/17.
 */

public class WorkoutService extends Service implements LocationListener, LocationSource.OnLocationChangedListener, SensorEventListener, StepListener {

    private static WorkoutService workoutService;
    private static LocationManager locationManager;
    private String provider;
    public static ArrayList<LatLng> locationTracking;
    private static Context mContext;
    private Handler mHandler;

    private double totalDist;
    private static int steps;


    private StepDetector simpleStepDetector;
    private static SensorManager sensorManager;
    private Sensor accel;



    public static void initializeService(Context context){
        WorkoutService.mContext = context;
        workoutService = new WorkoutService();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        steps = 0;
        totalDist = 0;
        locationTracking = new ArrayList<>();
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);

        locationManager.requestLocationUpdates(provider, 0, 0, WorkoutService.this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        sensorManager.registerListener(WorkoutService.this, accel, SensorManager.SENSOR_DELAY_FASTEST);

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onLocationChanged(Location location) {


        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        LatLng point = new LatLng(latitude, longtitude);
        if(!locationTracking.contains(point)){
            locationTracking.add(point);
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

    public double calculateDistance(){
        totalDist =  steps/2325.0; //2325 avg steps/mi

        //test
//        totalDist = (double) steps/(2325/100);
        return totalDist;
    }

    public double calculateCalories(){
        return (double) steps*23/1000;
    }

    public void stepsCalGraph(){
        GraphData graph = GraphData.getInstance();
        graph.addData(calculateCalories(), steps);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void stopTracking(){
        locationManager.removeUpdates(WorkoutService.this);
        sensorManager.unregisterListener(WorkoutService.this);

        DatabaseManager db = DatabaseManager.getInstance();

        StopWatch stopWatch = StopWatch.getInstance();
        if(calculateDistance() > 0 && calculateCalories() > 0) {
            WorkoutRecord data = new WorkoutRecord(steps, calculateDistance(),
                    stopWatch.getTimeUpdate(), (int) calculateCalories());
            db.insertData(data);
        }

        stopWatch.resetWatchTime();

        stopSelf();
    }

    public static WorkoutService getInstance(){
        return workoutService;
    }

    @Override
    public void step(long timeNs) {
        steps++;
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(WorkoutFragment.ResponseReceiver.LOCAL_ACTION);
        String distance = String.format("%.2f",calculateDistance());
        broadcastIntent.putExtra("Total Distance", distance);
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.sendBroadcast(broadcastIntent);
        Log.d(WorkoutService.class.getSimpleName(), "Step Counter: " + steps);
    }
}
