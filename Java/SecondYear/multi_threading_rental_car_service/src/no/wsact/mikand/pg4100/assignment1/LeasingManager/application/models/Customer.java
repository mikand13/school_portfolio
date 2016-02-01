package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers.RentalProcessor;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.utils.Util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * This class defines every Customer object. It has attributes to define the
 * customer and methods for defining when the customer should go to a store
 * or go home, and for leasing and deliver cars.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class Customer implements Runnable {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition storesNotYetOpen = lock.newCondition();

    private final int LEASE_TIMER = 10;
    private final int RETURN_TIMER = 3;

    private final String customerName;
    private Leaser leasingCompany;
    private RentalCar leasedCar;

    /**
     * Generates a Customer with customerName as its name.
     *
     * @param customerName java.lang.String
     */
    public Customer(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Queries the RentalProcessor for availible leasers when they open.
     */
    @Override
    public void run() {
        // verifies open leasers before the customer starts contacting them
        try {
            leaveHomeAndCheckForOpenStores();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return; // consume interrupt and exit
        }

        interactWithLeasers();
    }

    /**
     * Asks RentalProcessor for status of stores and awaits if closed.
     */
    private void leaveHomeAndCheckForOpenStores() throws InterruptedException {
        lock.lock();

        while (!RentalProcessor.getInstance().isStoresOpen()) {
            storesNotYetOpen.await();
        }

        lock.unlock();
    }

    /**
     * Leases and delivers cars repeatedly on interval.
     */
    private void interactWithLeasers() {
        while (RentalProcessor.getInstance().isStoresOpen()) {
            try {
                // sleep and lease
                Util.sleepThreadForOneToSuppliedSecond(
                        Thread.currentThread(), LEASE_TIMER);
                leasingCompany = RentalProcessor.getInstance().getLeaser();

                // skip iteration if no availible leaser; "Customer goes home."
                if (leasingCompany == null)
                    continue;

                leasedCar = leasingCompany.leaseAvailibleRentalCar(this);

                // sleep and deliver
                Util.sleepThreadForOneToSuppliedSecond(
                        Thread.currentThread(), RETURN_TIMER);
                leasingCompany.deliverRentalCar(leasedCar);
                leasedCar = null;
            } catch (InterruptedException e) {
                e.printStackTrace();

                // clear possible leased car before death
                if (leasedCar != null) {
                    leasingCompany.deliverRentalCar(leasedCar);
                }

                Thread.currentThread().interrupt(); // reinstate interrupt
            }
        }
    }

    /**
     * This unlocks the thread for quering when stores open.
     */
    public void notifyCustomerOfOpenStores() {
        lock.lock();
        try {
            storesNotYetOpen.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the name of the Customer.
     *
     * @return java.lang.String
     */
    @Override
    public String toString() {
        return customerName;
    }
}
