package com.quocpnguyen.alpha_fitness;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView avg_disctance;
    private TextView avg_time;
    private TextView avg_workouts;
    private TextView avg_calories;

    private TextView total_distance;
    private TextView total_time;
    private TextView total_workouts;
    private TextView total_calories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        avg_disctance = (TextView)findViewById(R.id.avg_distance);
        avg_time = (TextView)findViewById(R.id.avg_time);
        avg_workouts = (TextView)findViewById(R.id.avg_workouts);
        avg_calories = (TextView)findViewById(R.id.avg_calories);

        total_distance = (TextView)findViewById(R.id.total_distance);
        total_time = (TextView)findViewById(R.id.total_time);
        total_workouts = (TextView)findViewById(R.id.total_workouts);
        total_calories = (TextView)findViewById(R.id.total_calories);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
