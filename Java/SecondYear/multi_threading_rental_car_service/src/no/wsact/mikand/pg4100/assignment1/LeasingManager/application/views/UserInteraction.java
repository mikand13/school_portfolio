package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.views;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers.RentalProcessor;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Customer;

import javax.swing.*;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * View for interaction with the customer.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class UserInteraction implements Runnable {
    /**
     * Runs a continuous interaction with user to add customers
     * until limit in RentalProcessor is reached.
     */
    @Override
    public void run() {
        while (!RentalProcessor.getInstance().fullyQueuedOnLeasers()) {
            String temp =
                    JOptionPane.showInputDialog(
                            null, "Enter new customer or exit with x:");

            if (!temp.equalsIgnoreCase("x")) {
                RentalProcessor.getInstance()
                        .addCustomer(new Customer(temp));
            } else {
                break;
            }
        }
    }
}
