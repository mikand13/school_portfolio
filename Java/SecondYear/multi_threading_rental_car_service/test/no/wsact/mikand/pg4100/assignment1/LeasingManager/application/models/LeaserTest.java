package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models;

import static org.junit.Assert.*;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.utils.StandardBeforeAfter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers.RentalProcessor;
import testThreadUtils.testThreads.TestLeaseThread;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * This class arranges a RentalProcessor and checks Concurrency in the Leaser
 * Class
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class LeaserTest {
    private final int TEST_THREADS = 200;
    private final int THREAD_RUN_COUNT = 100;

    private RentalProcessor rentalProcessor;
    private Leaser testLeaser;
    private ExecutorService testExecutorService;
    private ByteArrayOutputStream mockOutStream, mockErrStream;
    private PrintStream systemOut, systemErr, mockOut, mockErr;

    /**
     * Starts RentalProcessor and reroutes system out and err to avoid output
     * during testing.
     *
     * Makes Threadpool.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        rentalProcessor = StandardBeforeAfter.doBefore(rentalProcessor);

        mockOutStream = new ByteArrayOutputStream();
        mockOut = new PrintStream(mockOutStream);
        systemOut = System.out;
        System.setOut(mockOut);

        mockErrStream = new ByteArrayOutputStream();
        mockErr = new PrintStream(mockErrStream);
        systemErr = System.err;
        System.setErr(mockErr);

        testLeaser = new Leaser("TESTER COMPANY");
        testExecutorService = Executors.newFixedThreadPool(TEST_THREADS);
    }

    /**
     * Reset outputs and destroys the RentalProcessor singleton with Reflections
     *
     * @throws Exception Exception
     */
    @After
    public void tearDown() throws Exception {

        StandardBeforeAfter.doAfter(rentalProcessor);

        testLeaser = null;

        // resetting system out and err
        System.out.flush();
        System.setOut(systemOut);
        System.err.flush();
        System.setErr(systemErr);

        // resetting mockErr and mockOut
        mockErr = null;
        mockOut = null;

        // resetting instance of RentalProcessor singleton for new test
        rentalProcessor = null;
        Runtime.getRuntime().gc();
    }

    /**
     * This tests concurrency in Leaser by run many threads and leasing /
     * delivering cars with no pause.
     *
     * It asserts if all threads have been closed and asserts if an exception
     * has been thrown application-wide during testing.
     *
     * @throws Exception Exception
     */
    @Test
    public void testConcurrency() throws Exception {
        for (int i = 0; i < TEST_THREADS; i++) {
            testExecutorService.execute(
                    new TestLeaseThread(testLeaser, THREAD_RUN_COUNT, i));
        }

        testExecutorService.shutdown();
        testExecutorService.awaitTermination(20, TimeUnit.SECONDS);

        assertTrue(testExecutorService.isShutdown());
        assertFalse(mockErrStream.toString()
                .contains("ConcurrentModificationException"));
        assertFalse(mockErrStream.toString()
                .contains("IllegalMonitorStateException"));
    }

    /**
     * This test isolates leasing, and attempting to lease when all cars are
     * leased.
     *
     * @throws Exception Exception
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testLeaseAvailibleRentalCar() throws Exception {
        ArrayList<RentalCar> testList;

        for (int i = 0; i < 5 ; i++) {
            testList = leaseCar();
            assertTrue(testList.get(i).isLeased());
            assertEquals(testList.get(i).getOwner(), "testCustomer");
        }

        Field field = rentalProcessor
                .getClass()
                .getDeclaredField("leasers");
        field.setAccessible(true);

        ArrayList<Leaser> testLeasers =
                (ArrayList<Leaser>) field.get(rentalProcessor);
        testLeasers.add(testLeaser);

        Customer shouldBeWaiting = new Customer("I am the Waiter");
        Thread t = new Thread(shouldBeWaiting);
        t.start();

        Thread.sleep(11000);

        assertEquals(Thread.State.WAITING, t.getState());

        t.interrupt(); // cancel thread after test
    }

    /**
     * This test isolates delivery.
     *
     * @throws Exception Exception
     */
    @Test
    public void testDeliverRentalCar() throws Exception {
        ArrayList<RentalCar> testList = leaseCar();

        assertTrue(testList.get(0).isLeased());
        assertEquals(testList.get(0).getOwner(), "testCustomer");

        testLeaser.deliverRentalCar(testList.get(0));
        assertFalse(testList.get(0).isLeased());
        assertNull(testList.get(0).getOwner());
    }

    /**
     * This test isolates availability of cars.
     *
     * @throws Exception Exception
     */
    @Test
    public void testGetCarsAvailible() throws Exception {
        int availableAfterLease = leaseCar().size();
        int reportedAvailableAfterLease = testLeaser.getCarsAvailible();

        // -1 for one lease
        assertEquals(availableAfterLease - 1, reportedAvailableAfterLease);
    }

    /**
     * This helpermethod returns the rentalcar list of a company after
     * leasing one car.
     *
     * @return ArrayList RentalCar
     * @throws Exception Exception
     */
    @SuppressWarnings("unchecked")
    private ArrayList<RentalCar> leaseCar() throws Exception {
        Customer customer = new Customer("testCustomer");
        testLeaser.leaseAvailibleRentalCar(customer);

        Field field = testLeaser
                .getClass()
                .getDeclaredField("companyRentalCars");
        field.setAccessible(true);

        return (ArrayList<RentalCar>) field.get(testLeaser);
    }
}