package com.quocpnguyen.alpha_fitness;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;

public class MainActivity extends AppCompatActivity{

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();

//        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//
//        } else {
//            Fragment recordWorkout = new WorkoutActivity();
//            fm.beginTransaction()
//                    .replace(R.id.fragmentContainer, recordWorkout)
//                    .commit();
//        }



    }

}
