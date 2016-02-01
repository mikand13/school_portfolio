package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers.RentalProcessor;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.utils.Log;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.utils.Logger;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.utils.Util;
import no.wsact.mikand.pg4100.assignment1.TestUtils.Generator;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * This class defines a leaser object. The leaser object will service any
 * customer that contacts it until stores close in the RentalProcessor.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class Leaser {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition noneAvailible = lock.newCondition();

    private final String companyName;
    private final Logger companyLeaseLog;
    private final ArrayList<RentalCar> companyRentalCars;

    /**
     * Generates a Company based on a name.
     *
     * @param companyName java.lang.String
     */
    public Leaser(String companyName) {
        this.companyName = companyName;
        companyLeaseLog = new Logger();
        companyRentalCars = Generator.generateCars(companyName);
    }

    /**
     * This method checks for availible cars, if not are availible it waits
     * until one is made availible. If it is, it leases it to customer.
     *
     * @param customer Customer
     * @return RentalCar
     */
    public RentalCar leaseAvailibleRentalCar(Customer customer) {
        lock.lock();

        try {
            if (getCarsAvailible() == 0) {
                notifyPublicOfLeaseEvent(
                        customer + " is in a queue at " + companyName + "!");
                try {
                    noneAvailible.await();
                    notifyPublicOfLeaseEvent(
                            customer + " has finished a queue at " +
                                    companyName + "!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return checkForAvailibleCar(customer);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks for availible cars in leaser.
     *
     * @param customer Customer
     * @return RentalCar
     */
    private RentalCar checkForAvailibleCar(Customer customer) {
        for (RentalCar rc : companyRentalCars) {
            if (!rc.isLeased()) {
                leaseCar(customer, rc);
                return rc;
            }
        }

        return null;
    }

    /**
     * Leases the availible care to a customer.
     *
     * @param customer Customer
     * @param rc RentalCar
     */
    private void leaseCar(Customer customer, RentalCar rc) {
        rc.setOwner(customer);
        rc.setLeased(true);
        notifyPublicOfLeaseEvent("Lease of: " + rc +
                "\nLeased to: " + customer.toString());
    }

    /**
     * Proclaims that a car is leased.
     *
     * @param event java.lang.String
     */
    private void notifyPublicOfLeaseEvent(String event) {
        String notify = event + "\n\n" + toString();

        companyLeaseLog.add(new Log(notify));
        RentalProcessor.getInstance().notifyPublic(notify);
    }

    /**
     * Delivers the car rc leased by rc.getOwner().
     *
     * @param rc RentalCar
     */
    public void deliverRentalCar(RentalCar rc) {
        lock.lock();

        try {
            companyRentalCars.stream()
                    .filter(rental -> rental.equals(rc)).forEach(rental -> {
                rc.setLeased(false);
                notifyPublicOfLeaseEvent("Delivery of: " + rc +
                        "\nDelivered from: " + rc.getOwner());
                rc.setOwner(null);
                noneAvailible.signal();
            });
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks availability of cars in leaser.
     *
     * @return int
     */
    public int getCarsAvailible() {
        lock.lock();
        int carsAvailible = 0;

        try {
            for (RentalCar rc : companyRentalCars) {
                if (!rc.isLeased()) {
                    carsAvailible++;
                }
            }
        } finally {
            lock.unlock();
        }

        return carsAvailible;
    }

    /**
     * Gets the companies log for output to file.
     *
     * @return Logger
     */
    public Logger getCompanyLeaseLog() {
        return companyLeaseLog;
    }

    public String getCompanyName() {
        return companyName;
    }

    /**
     * Returns a String with information about the leaser, formatted as a table
     *
     * @return java.lang.String
     */
    @Override
    public String toString() {
        return Util.buildStringTableFromArrayList(companyName,
                                                  companyRentalCars);
    }
}
