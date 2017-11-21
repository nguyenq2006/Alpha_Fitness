package com.quocpnguyen.alpha_fitness;

import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (fragment == null) {
                Fragment workoutDetail = new WorkDetailFragment();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, workoutDetail)
                        .commit();
            } else {
                Fragment workoutDetail = new WorkDetailFragment();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, workoutDetail)
                        .commit();
            }

        } else {

            if (fragment == null) {
                Fragment recordWorkout = new WorkoutActivity();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, recordWorkout)
                        .commit();
            } else {
                Fragment recordWorkout = new WorkoutActivity();
                fm.beginTransaction()
                        .replace(R.id.fragmentContainer, recordWorkout)
                        .commit();
            }
        }



    }

}
