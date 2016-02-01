package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Customer;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Leaser;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.views.CustomerCommunicator;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.views.UserInteraction;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.utils.Util;
import no.wsact.mikand.pg4100.assignment1.TestUtils.Generator;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * This singleton is the backbone of the rental system and administers flow.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class RentalProcessor implements Runnable {
    private static RentalProcessor instance;
    private static final int DEFAULT_CUSTOMER_VALUE = 10;
    private final int MIN_CUSTOMERS_FOR_BUSINESS_START = 4;

    private ExecutorService executorService;
    private ArrayList<Leaser> leasers;
    private ArrayList<Customer> users;

    private boolean storesOpen = false;
    private int maxNumberOfCustomers = 0;
    private int numberOfCustomers = 0;

    /**
     * Initializes the RentalProcessor Singleton with a max amount of customers
     *
     * @param maximumNumberOfCustomers int
     */
    @SuppressWarnings("SameParameterValue")
    public RentalProcessor(int maximumNumberOfCustomers) {
        if (instance == null) {
            instance = this;
            maxNumberOfCustomers = maximumNumberOfCustomers;
            executorService = Executors.newFixedThreadPool(
                    maximumNumberOfCustomers);
            users = new ArrayList<>();
        } else {
            System.err.println("\n\n" +
                    "There can only be one RentalProcessor" +
                    "\n\n");
        }
    }

    /**
     * Returns the singleton, if instance is not availible it returns a new
     * with a default customer value.
     *
     * @return RentalProcessor
     */
    public static RentalProcessor getInstance() {
        if (instance != null) {
            return instance;
        }

        new RentalProcessor(DEFAULT_CUSTOMER_VALUE);
        return instance;
    }

    /**
     * Contains creation and destruction logic for the RentalProcessor
     *
     * This method starts the userinteraction by instantiating the view.
     */
    @Override
    public void run() {
        initializeLeasers();

        Thread t = new Thread(new UserInteraction());
        t.start();

        //Generator.generateCustomers(maxNumberOfCustomers); // For testing

        shutdownAndOutputLogs(t);
    }

    /**
     * This Method generates Leasers for the RentalProcessor.
     */
    private void initializeLeasers() {
        leasers = Generator.generateLeasers();
    }

    /**
     * This method handles clean up and shutdown.
     *
     * @param userInteractionThread Thread
     */
    private void shutdownAndOutputLogs(Thread userInteractionThread) {
        try {
            // For very very extended shutdown, reduce to test shutdown
            Thread.sleep(10000000);
            killAllThreadsAndExit();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            for (Leaser l : leasers) {
                Util.storeLogsOnDisk(
                        l.getCompanyName(), l.getCompanyLeaseLog().getList());
            }

            if (userInteractionThread.isAlive()) {
                userInteractionThread.interrupt();
            }
        }
    }

    /**
     * Kills all threads and exits application.
     *
     * @throws InterruptedException InterruptedException
     */
    private void killAllThreadsAndExit() throws InterruptedException {
        storesOpen = false;
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);
        instance = null;
    }

    /**
     * Adds the generated customer to the customerlist and executes the thread.
     *
     * @param customer Customer
     */
    public void addCustomer(Customer customer) {
        if (numberOfCustomers < maxNumberOfCustomers) {
            users.add(customer);
            executorService.execute(customer);

            if (numberOfCustomers == MIN_CUSTOMERS_FOR_BUSINESS_START) {
                storesOpen = true;
                notifyAllCustomersOfOpenStores();
            }
            numberOfCustomers++;
        }
    }

    /**
     * Utilizes a parallelStream to notify all waiting customers
     * of open leasers.
     */
    private void notifyAllCustomersOfOpenStores() {
        try {
            users.parallelStream()
                    .forEach(Customer::notifyCustomerOfOpenStores);
        } catch (IllegalMonitorStateException imse) {
            imse.printStackTrace();
        }
    }

    /**
     * Returns the carleaser with the most amount of availible cars.
     *
     * @return Leaser
     */
    public Leaser getLeaser() {
        Leaser mostAvailibleCars = null;
        int mostAvailible = 0;

        for (Leaser l : leasers) {
            if (l.getCarsAvailible() > mostAvailible) {
                mostAvailible = l.getCarsAvailible();
                mostAvailibleCars = l;
            }
        }

        return mostAvailibleCars;
    }

    /**
     * Sends a message to the output for reading by the public.
     *
     * @param leaseMade java.lang.String
     */
    public void notifyPublic(String leaseMade) {
        CustomerCommunicator.print(leaseMade);
    }

    /**
     * Checks wether enough customers are availible for the stores to be open.
     *
     * @return boolean
     */
    public boolean isStoresOpen() {
        return storesOpen;
    }

    /**
     * Checks wether Leasers are at full capacity
     *
     * @return boolean
     */
    public boolean fullyQueuedOnLeasers() {
        return numberOfCustomers == maxNumberOfCustomers;
    }

    /**
     * Returns information on stores and customers.
     *
     * @return java.lang.String
     */
    @Override
    public String toString() {
        return "Stores open? " + storesOpen +
                "Leasers: " + leasers.size() +
                "Customers: " + users.size();
    }
}
