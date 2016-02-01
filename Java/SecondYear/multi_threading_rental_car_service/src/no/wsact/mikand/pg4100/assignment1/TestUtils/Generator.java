package no.wsact.mikand.pg4100.assignment1.TestUtils;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers.RentalProcessor;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Customer;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.Leaser;
import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models.RentalCar;

import java.util.ArrayList;

/**
 * Created by anders on 27.01.15.
 *
 * Class for generating test values for the program.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class Generator {
    /**
     * Generates a number of cars for a leaser.
     *
     * @param company java.lang.String
     * @return ArrayList.RentalCar
     */
    public static ArrayList<RentalCar> generateCars(String company) {
        ArrayList<RentalCar> rentalCars = new ArrayList<>();
        rentalCars.add(new RentalCar(company, "AA000000"));
        rentalCars.add(new RentalCar(company, "BB111111"));
        rentalCars.add(new RentalCar(company, "CC222222"));
        rentalCars.add(new RentalCar(company, "DD999999"));
        rentalCars.add(new RentalCar(company, "EE888888"));

        return rentalCars;
    }

    /**
     * Generates a number of leasers for a RentalProcessor singleton.
     *
     * @return ArrayList.Leaser
     */
    public static ArrayList<Leaser> generateLeasers() {
        ArrayList<Leaser> leasers = new ArrayList<>();
        leasers.add(new Leaser("Hertz"));
        leasers.add(new Leaser("Rent-A-Wreck"));
        leasers.add(new Leaser("Avis"));

        return leasers;
    }

    /**
     * Generates a number of customers for a RentalProcessor singleton.
     *
     * @param customers int
     */
    @SuppressWarnings("UnusedDeclaration")
    public static void generateCustomers(int customers) {
        for (int i = 0; i < customers; i++) {
            RentalProcessor.getInstance()
                    .addCustomer(new Customer("Customer: " + i));
        }
    }
}
