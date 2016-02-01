package testThreadUtils;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Leaser;

/**
 * Created by anders on 27.01.15.
 *
 * Abstract class for various teststhreads.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public abstract class TestThread implements Runnable {
    protected final int runTimes;
    protected final int threadNumber;
    protected final Leaser testLeaser;

    /**
     * Initializes a thread with a Leaser, times it needs to run and a number.
     *
     * @param testLeaser Leaser
     * @param runTimes int
     * @param threadNumber int
     */
    protected TestThread (Leaser testLeaser, int runTimes, int threadNumber) {
        this.testLeaser = testLeaser;
        this.runTimes = runTimes;
        this.threadNumber = threadNumber;
    }
}
