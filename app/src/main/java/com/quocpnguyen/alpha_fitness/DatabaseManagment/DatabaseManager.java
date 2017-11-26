package com.quocpnguyen.alpha_fitness.DatabaseManagment;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by kevin on 11/21/2017.
 */

public class DatabaseManager {
    private static DatabaseManager dbManager = new DatabaseManager();
    private ArrayList<WorkoutRecord> workoutData;
    private SQLiteHelper dbHelper;

    private double max_duration;
    private double min_duration;

    private DatabaseManager() {
        workoutData = new ArrayList<>();
    }

    public static DatabaseManager getInstance() {
        return DatabaseManager.dbManager;
    }

    public void initializeDatabase(Context context) {
        dbHelper = new SQLiteHelper(context);
        workoutData = dbHelper.retrieveData();

    }

    public void insertData(WorkoutRecord data) {
        workoutData.add(data);
        dbHelper.insertData(data);

        double min_per_mi = data.getDuration() / 60000.0 / data.getTotalDistance();
        if (min_per_mi > max_duration) {
            max_duration = min_per_mi;
        }

        if (min_per_mi < min_duration || min_duration == 0.0) {
            min_duration = min_per_mi;
        }
    }

    public double getMax_duration() {
        return max_duration;
    }

    public void setMax_duration(double max_duration) {
        this.max_duration = max_duration;
    }

    public double getMin_duration() {
        return min_duration;
    }

    public void setMin_duration(double min_duration) {
        this.min_duration = min_duration;
    }

    public String[] findTotalData() {
        int workoutCounter = 0;
        double totalDistance = 0;
        int totalCaloriesBurned = 0;
        long totalDuration = 0;
        for (WorkoutRecord record : workoutData) {
            workoutCounter++;
            totalDistance += record.getTotalDistance();
            totalCaloriesBurned += record.getCaloriesBurned();
            totalDuration += record.getDuration();
        }

        String[] result = {workoutCounter + "",
                String.format("%.2f",totalDistance),
                totalCaloriesBurned + "",
                convertMs(totalDuration)};
        return result;
    }

    public String[] findWeeklAvg() {
        int workoutCounter = 0;
        double totalDistance = 0;
        int totalCaloriesBurned = 0;
        long totalDuration = 0;
        Date startDate = null;
        int weekCounter = 1;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            for (WorkoutRecord record : workoutData) {
                workoutCounter++;
                totalDistance += record.getTotalDistance();
                totalCaloriesBurned += record.getCaloriesBurned();
                totalDuration += record.getDuration();


                Date recordedDate = dateFormat.parse(record.getDate());

                if (startDate == null) {
                    startDate = recordedDate;
                } else if (dayDifference(startDate, recordedDate) >= 7) {
                    weekCounter++;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] result = {workoutCounter / weekCounter + "",
                String.format("%.2f",totalDistance / weekCounter),
                totalCaloriesBurned / weekCounter + "",
                convertMs(totalDuration / weekCounter)};
        return result;
    }


    final int SECOND = 1000;
    final int MINUTE = 60 * SECOND;
    final int HOUR = 60 * MINUTE;
    final int DAY = 24 * HOUR;

    private String convertMs(long ms) {


        StringBuffer text = new StringBuffer("");
        if (ms > DAY)

        {
            text.append(ms / DAY).append(" days ");
            ms %= DAY;
        }
        if (ms > HOUR)

        {
            text.append(ms / HOUR).append(" hours ");
            ms %= HOUR;
        }
        if (ms > MINUTE)

        {
            text.append(ms / MINUTE).append(" minutes ");
            ms %= MINUTE;
        }
        if (ms > SECOND)

        {
            text.append(ms / SECOND).append(" seconds ");
            ms %= SECOND;
        }
        text.append(ms + " ms");
        return text.toString();
    }

    private long dayDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        return different / DAY;

    }

}
