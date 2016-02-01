package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.utils;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers.RentalProcessor;

import java.lang.reflect.Method;

/**
 * Created by Anders Mikkelsen on 29.01.15.
 *
 * This class defines general startup and shutdown for RentalProcessor in tests.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class StandardBeforeAfter {
    /**
     * Does standard procedures needed for creating a RentalProcessor.
     *
     * @param rentalProcessor RentalProcessor
     * @throws Exception Exception
     * @return rentalProcessor RentalProcessor
     */
    @SuppressWarnings("ParameterCanBeLocal")
    public static RentalProcessor doBefore(RentalProcessor rentalProcessor)
            throws Exception {
        Thread.sleep(4000); // Ensures last singelton is dead

        rentalProcessor = RentalProcessor.getInstance();
        Method method = rentalProcessor.getClass()
                .getDeclaredMethod("initializeLeasers");
        method.setAccessible(true);
        method.invoke(rentalProcessor);

        return rentalProcessor;
    }

    /**
     * Does standard shutdownprocedures for rentalProcessor
     *
     * @param rentalProcessor RentalProcessor
     * @throws Exception Exception
     */
    public static void doAfter(RentalProcessor rentalProcessor)
            throws Exception {
        // ending all threads in rentalprocessor
        Method method = rentalProcessor.getClass()
                .getDeclaredMethod("killAllThreadsAndExit");
        method.setAccessible(true);
        method.invoke(rentalProcessor);

        Thread.sleep(5000); // Ensures shutdown
    }
}
