package com.quocpnguyen.alpha_fitness;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

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

public class WorkoutService extends IntentService implements LocationListener, LocationSource.OnLocationChangedListener {
    private GoogleMap mMap;
    private LocationManager locationManager;
    public final int MY_PERMISSIONS_REQUEST_LOCATION = 20;
    private String provider;
    private LocationSource.OnLocationChangedListener mListener;
    private LocationSource mLocationSource;
    public static ArrayList<LatLng> locationTracking;
    private static Context mContext;


    public WorkoutService() {
        super("Workout service");
    }

    public static void initializeService(Context context){
        WorkoutService.mContext = context;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        locationTracking = new ArrayList<>();
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);
        mLocationSource = new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener onLocationChangedListener) {
                mListener = onLocationChangedListener;
            }

            @Override
            public void deactivate() {
                mListener = null;
            }
        };
        locationManager.requestLocationUpdates(provider, 5000, 1, this);
        mLocationSource.activate(this);

    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longtitude = location.getLongitude();
        LatLng point = new LatLng(latitude, longtitude);
        if(!locationTracking.contains(point)){
            locationTracking.add(point);
        }


        CameraUpdate currentLocatiion = CameraUpdateFactory.newLatLngZoom(point, 15);
        mMap.animateCamera(currentLocatiion);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(WorkoutActivity.ResponseReceiver.LOCAL_ACTION);
        broadcastIntent.putExtra("Total Distance", 20.0);

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
}
