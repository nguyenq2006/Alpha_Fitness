package com.quocpnguyen.alpha_fitness.DatabaseManagment;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by kevin on 11/21/2017.
 */

public class DatabaseManager {
    private static DatabaseManager dbManager = new DatabaseManager();
    private ArrayList<WorkoutRecord> workoutData;
    private SQLiteHelper dbHelper;

    private DatabaseManager(){
        workoutData = new ArrayList<>();
    }

    public static DatabaseManager getInstance(){
        return DatabaseManager.dbManager;
    }

    public void initializeDatabase(Context context){
        dbHelper = new SQLiteHelper(context);
        workoutData = dbHelper.retrieveData();
    }

    public void insertData(WorkoutRecord data){
        dbHelper.insertData(data);
    }
}
