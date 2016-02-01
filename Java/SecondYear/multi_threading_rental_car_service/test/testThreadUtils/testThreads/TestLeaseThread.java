package testThreadUtils.testThreads;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Customer;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Leaser;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.RentalCar;
import testThreadUtils.TestThread;

/**
 * Created by anders on 27.01.15.
 *
 * Class for testing lease and deliver.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class TestLeaseThread extends TestThread {
    public TestLeaseThread(Leaser testLeaser, int runTimes, int threadNumber) {
        super(testLeaser, runTimes, threadNumber);
    }

    /**
     * This thread repeatedly leases and delivers with no pause.
     */
    @Override
    public void run() {
        for (int i = 0; i < runTimes; i++) {
            RentalCar rc = testLeaser
                    .leaseAvailibleRentalCar(new Customer("" + threadNumber));
            testLeaser.deliverRentalCar(rc);
        }
    }
}
