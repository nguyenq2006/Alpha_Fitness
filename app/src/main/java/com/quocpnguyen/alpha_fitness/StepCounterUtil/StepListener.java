package com.quocpnguyen.alpha_fitness.StepCounterUtil;

/**
 * Created by cs on 11/21/17.
 */

// Will listen to step alerts
public interface StepListener {

    public void step(long timeNs);

}