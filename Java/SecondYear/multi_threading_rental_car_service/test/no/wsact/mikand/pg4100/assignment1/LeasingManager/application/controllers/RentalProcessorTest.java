package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Leaser;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.RentalCar;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.utils.StandardBeforeAfter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Customer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;


@SuppressWarnings("FieldCanBeLocal")
public class RentalProcessorTest {
    private RentalProcessor rentalProcessor;
    private ByteArrayOutputStream mockOutStream, mockErrStream;
    private PrintStream systemOut, systemErr, mockOut, mockErr;

    /**
     * Starts RentalProcessor and reroutes system out to avoid output
     * during testing.
     *
     * Makes Threadpool.
     *
     * @throws Exception Exception
     */
    @Before
    public void setUp() throws Exception {
        /**
         * Reroute output to focus on concurrency checks
         */

        mockOutStream = new ByteArrayOutputStream();
        mockOut = new PrintStream(mockOutStream);
        systemOut = System.out;
        System.setOut(mockOut);

        mockErrStream = new ByteArrayOutputStream();
        mockErr = new PrintStream(mockErrStream);
        systemErr = System.err;
        System.setErr(mockErr);

        rentalProcessor = StandardBeforeAfter.doBefore(rentalProcessor);
    }

    /**
     * Reset outputs and destroys the RentalProcessor singleton with
     * Reflections
     *
     * @throws Exception Exception
     */
    @After
    public void tearDown() throws Exception {
        StandardBeforeAfter.doAfter(rentalProcessor);

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
     * This tests wether the program halts until the predetermined amount of
     * customers are entered into the system. The amount is reflected out of
     * the RentalProcessor.
     *
     * @throws Exception Exception
     */
    @Test
    public void testAddCustomer() throws Exception {
        Field field = rentalProcessor
                .getClass()
                .getDeclaredField("MIN_CUSTOMERS_FOR_BUSINESS_START");
        field.setAccessible(true);

        int CUSTOMERS_TO_WAIT = field.getInt(rentalProcessor);

        for (int i = 0; i < CUSTOMERS_TO_WAIT; i++) {
            Customer c = new Customer("" + i);
            rentalProcessor.addCustomer(c);
        }

        // check if threads are waiting
        Thread.sleep(10000);
        assertFalse(mockOutStream.toString().contains("*"));

        rentalProcessor.addCustomer(new Customer("" + 5));

        // check if threads have started
        Thread.sleep(10000);
        assertTrue(mockOutStream.toString().contains("*"));
        assertFalse(mockErrStream.toString()
                .contains("IllegalMonitorStateException"));
    }

    /**
     * This tests verifies that the RentalProcessor picks the leaser
     * with the most amount of availible cars.
     *
     * @throws Exception Exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetLeaser() throws Exception {
        Thread.sleep(1000);

        Field field = rentalProcessor
                .getClass()
                .getDeclaredField("leasers");
        field.setAccessible(true);

        ArrayList<Leaser> leasers =
                (ArrayList<Leaser>) field.get(rentalProcessor);

        Field leaserField = leasers.get(1)
                .getClass()
                .getDeclaredField("companyRentalCars");
        leaserField.setAccessible(true);

        ArrayList<RentalCar> rentalCars =
                (ArrayList<RentalCar>) leaserField.get(leasers.get(1));

        for (int i = 0; i < 10; i++) {
            rentalCars.add(new RentalCar("Rofl", "123213"));
        }

        assertEquals(rentalProcessor.getLeaser(), leasers.get(1));
    }
}