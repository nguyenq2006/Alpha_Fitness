package com.quocpnguyen.alpha_fitness;

/**
 * Created by cs on 10/28/17.
 */

public class StopWatch {

    private static final StopWatch stopWatch = new StopWatch();
    private long mStartTime;
    private long mTimeUpdate;
    private long mStoreTime;

    private StopWatch(){
        mStartTime = 0L;
        mTimeUpdate = 0L;
        mStoreTime = 0L;
    }

    public static StopWatch getInstance(){
        return stopWatch;
    }

    public void resetWatchTime(){
        mStartTime = 0L;
        mTimeUpdate = 0L;
        mStoreTime = 0L;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public long getStoreTime() {
        return mStoreTime;
    }

    public void addStoreTime(long mStoreTime) {
        this.mStoreTime += mStoreTime;
    }

    public long getTimeUpdate() {
        return mTimeUpdate;
    }

    public void setTimeUpdate(long mTimeUpdate) {
        this.mTimeUpdate = mTimeUpdate;
    }
}
