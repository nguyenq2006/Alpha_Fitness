package com.quocpnguyen.alpha_fitness;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;

/**
 * Created by cs on 11/13/17.
 */

public class WorkDetailFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View workout_detail = inflater.inflate(R.layout.workout_detail_fragment, container, false);
        BarChart chart = (BarChart) workout_detail.findViewById(R.id.chart);

//        BarData data = new BarData(getXAxisValues(), getDataSet());
//        chart.setData(data);
//        chart.setDescription("My Chart");
//        chart.animateXY(2000, 2000);
//        chart.invalidate();
        return workout_detail;
    }

}
