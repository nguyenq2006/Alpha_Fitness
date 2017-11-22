package com.quocpnguyen.alpha_fitness.DatabaseManagment;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cs on 11/21/17.
 */

public class WorkoutRecord {
    private int stepCounter;
    private double totalDistance;
    private int caloriesBurned;
    private long duration;//store in ms
    private String date;

    public WorkoutRecord(int stepCounter, double totalDistance, long duration, int caloriesBurned){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        date = dateFormat.format(new Date());

        this.stepCounter = stepCounter;
        this.totalDistance = totalDistance;
        this.duration = duration;
    }

    public WorkoutRecord(){

    }

    public int getStepCounter() {
        return stepCounter;
    }

    public void setStepCounter(int stepCounter) {
        this.stepCounter = stepCounter;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurnt) {
        this.caloriesBurned = caloriesBurnt;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String dateTime) {
        this.date = dateTime;
    }
}
