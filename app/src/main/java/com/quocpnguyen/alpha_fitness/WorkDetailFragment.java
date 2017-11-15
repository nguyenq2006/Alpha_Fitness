package com.quocpnguyen.alpha_fitness;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by cs on 11/13/17.
 */

public class WorkDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View workout_detail = inflater.inflate(R.layout.workout_detail, container);
        return workout_detail;
    }

}
