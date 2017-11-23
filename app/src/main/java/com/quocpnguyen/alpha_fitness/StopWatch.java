package com.quocpnguyen.alpha_fitness;

/**
 * Created by cs on 10/28/17.
 */

public class StopWatch {

    private static final StopWatch stopWatch = new StopWatch();
    private long mStartTime;
    private long mTimeUpdate;
    private long mStoreTime;
    private boolean isRunning;

    private StopWatch(){
        mStartTime = 0L;
        mTimeUpdate = 0L;
        mStoreTime = 0L;
    }

    public static StopWatch getInstance(){
        return stopWatch;
    }

    public void resetWatchTime(){
        stopWatch.mStartTime = 0L;
        stopWatch.mTimeUpdate = 0L;
        stopWatch.mStoreTime = 0L;
        stopWatch.isRunning=false;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long mStartTime) {
        stopWatch.mStartTime = mStartTime;
        stopWatch.isRunning=true;
    }

    public long getStoreTime() {
        return  stopWatch.mStoreTime;
    }

    public void addStoreTime(long mStoreTime) {
        stopWatch.mStoreTime += mStoreTime;
    }

    public long getTimeUpdate() {
        return  stopWatch.mTimeUpdate;
    }

    public void setTimeUpdate(long mTimeUpdate) {
        stopWatch.mTimeUpdate = mTimeUpdate;
    }

    public boolean isRunning() {
        return  stopWatch.isRunning;
    }
}
