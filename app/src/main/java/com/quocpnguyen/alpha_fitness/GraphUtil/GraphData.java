package com.quocpnguyen.alpha_fitness.GraphUtil;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cs on 11/21/17.
 */

public class GraphData {
    private ArrayList<Entry> caloriesData;
    private ArrayList<Entry> stepsData;
    private List<String> xVal;

    private static GraphData graphData = new GraphData();
    private OnUpdateData mListener;

    private GraphData(){
        caloriesData = new ArrayList<>();
        stepsData = new ArrayList<>();
        xVal = new ArrayList<>();
    }


    public ArrayList<Entry> getCaloriesData() {
        return caloriesData;
    }

    public ArrayList<Entry> getStepsData() {
        return stepsData;
    }

    public List<String> getxVal() {
        return xVal;
    }

    public void addData(double calories, int steps){
        int n = xVal.size();
        float avg_calories = (float) calories/(n+1);
        float avg_steps = (float) steps/(n+1);
        caloriesData.add(new Entry(avg_calories, n));
        stepsData.add(new Entry(avg_steps, n));
        xVal.add(""+n);

        if(mListener != null){
            mListener.updateGraph();
        }

    }

    public static GraphData getInstance(){
        return graphData;
    }

    public void setDataChangeListener(OnUpdateData listener){
        mListener = listener;
    }

}
