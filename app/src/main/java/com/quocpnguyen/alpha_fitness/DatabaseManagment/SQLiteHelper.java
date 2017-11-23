package com.quocpnguyen.alpha_fitness.DatabaseManagment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.quocpnguyen.alpha_fitness.GraphUtil.GraphData;

import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by kevin on 11/21/2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "AlphaFitness_db";
    public static final String TABLE_NAME = "WorkoutRecords";

    //table attributes
    public static final  String DATE_ATTR = "date";
    public static final  String TOTAL_DIST = "distance";
    public static final  String CALORIES = "calories";
    public static final  String DURATION = "duration";
    public static final  String STEPS_COUNTER = "steps_counter";

    public SQLiteHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table WorkoutRecords ( " +
                "_id integer primary key autoincrement, " +
                DATE_ATTR + " date, " +
                TOTAL_DIST + " double, " +
                CALORIES + " integer, " +
                DURATION + " integer, " +
                STEPS_COUNTER + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void insertData(WorkoutRecord data){
        SQLiteDatabase db = getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE_ATTR, data.getDate());
        contentValues.put(TOTAL_DIST, data.getTotalDistance());
        contentValues.put(CALORIES, data.getCaloriesBurned());
        contentValues.put(DURATION, data.getDuration());
        contentValues.put(STEPS_COUNTER, data.getStepCounter());
        db.insert(TABLE_NAME, null, contentValues);
        db.close();
    }

    public ArrayList<WorkoutRecord> retrieveData(){
        double max_duration = 0.0;
        double min_duration = 0.0;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        ArrayList<WorkoutRecord> result = new ArrayList<>();
        while (cursor.moveToNext()){
            cursor.moveToNext();
            WorkoutRecord data = new WorkoutRecord();
            data.setDate(cursor.getString(1));
            data.setTotalDistance(cursor.getDouble(2));
            data.setCaloriesBurned(cursor.getInt(3));
            data.setDuration(cursor.getInt(4));
            data.setStepCounter(cursor.getInt(5));

            double min_per_mi = data.getDuration()/60000.0/data.getTotalDistance();
            if(min_per_mi > max_duration){
                max_duration = min_per_mi;
            }

            if (min_per_mi < min_duration || min_duration == 0.0){
                min_duration = min_per_mi;
            }

            result.add(data);

            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.setMax_duration(max_duration);
            dbManager.setMin_duration(min_duration);

            GraphData gd = GraphData.getInstance();
            int time = (int) data.getDuration()/60000;
            gd.addData(5*data.getCaloriesBurned()/time, 5*data.getStepCounter()/time);
        }
        return result;
    }
}
