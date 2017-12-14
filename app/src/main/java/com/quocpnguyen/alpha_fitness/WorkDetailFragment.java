package com.quocpnguyen.alpha_fitness;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.quocpnguyen.alpha_fitness.DatabaseManagment.DatabaseManager;
import com.quocpnguyen.alpha_fitness.GraphUtil.GraphData;
import com.quocpnguyen.alpha_fitness.GraphUtil.OnUpdateData;

import java.util.ArrayList;

/**
 * Created by cs on 11/13/17.
 */

public class WorkDetailFragment extends Fragment implements OnUpdateData {

    private GraphData graphData;
    private StopWatch stopWatch;
    private WorkoutService workoutService;

    private LineChart lineChart;
    private TextView workout_duration;
    private TextView max_time;
    private TextView min_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View workout_detail = inflater.inflate(R.layout.workout_detail_fragment, container, false);
        lineChart = (LineChart) workout_detail.findViewById(R.id.chart);
        workout_duration = (TextView) workout_detail.findViewById(R.id.workout_duration);
        max_time = (TextView) workout_detail.findViewById(R.id.max_time);
        min_time = (TextView) workout_detail.findViewById(R.id.min_time);

        workoutService = WorkoutService.getInstance();

        graphData = GraphData.getInstance();
        graphData.setDataChangeListener(this);
        stopWatch = StopWatch.getInstance();

        updateGraph();

        return workout_detail;
    }

    @Override
    public void updateGraph() {
        if (stopWatch != null && graphData.getxVal().size()>0) {
            ArrayList<LineDataSet> lineDataSets = new ArrayList<>();

            LineDataSet lineDataSet1 = new LineDataSet(graphData.getStepsData(), "step per 5 min");
            lineDataSet1.setDrawCircles(false);
            lineDataSet1.setColor(Color.BLUE);

            LineDataSet lineDataSet2 = new LineDataSet(graphData.getCaloriesData(), "calories per 5 min");
            lineDataSet2.setDrawCircles(false);
            lineDataSet2.setColor(Color.RED);

            lineDataSets.add(lineDataSet1);
            lineDataSets.add(lineDataSet2);


            lineChart.setData(new LineData(graphData.getxVal(), lineDataSets));


            long min = stopWatch.getTimeUpdate() / 60000;
            double min_per_mi = (workoutService.calculateDistance() == 0) ? 0 : (double) min / workoutService.calculateDistance();
            workout_duration.setText(String.format("%.2f", min_per_mi));

        }

        DatabaseManager dbManager = DatabaseManager.getInstance();
        max_time.setText(String.format("%.2f", dbManager.getMax_duration()));
        min_time.setText(String.format("%.2f", dbManager.getMin_duration()));
    }
}
