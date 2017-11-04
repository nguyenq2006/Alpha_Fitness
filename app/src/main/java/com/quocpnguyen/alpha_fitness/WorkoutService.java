package com.quocpnguyen.alpha_fitness;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by cs on 10/28/17.
 */

public class WorkoutService extends IntentService{
    public WorkoutService(String name) {
        super(name);
    }

    @Override
    public void onCreate(){

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


}
