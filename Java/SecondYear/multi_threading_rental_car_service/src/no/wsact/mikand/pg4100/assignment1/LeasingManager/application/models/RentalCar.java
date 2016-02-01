package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.models;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * VO for Rentalcars.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class RentalCar {
    private final String company;
    private final String regNumber;
    private boolean leased;
    private Customer customer;

    /**
     * Generates car and sets false lease and undefined customer.
     *
     * @param company java.lang.String
     * @param regNumber java.lang.String
     */
    public RentalCar(String company, String regNumber) {
        this.company = company;
        this.regNumber = regNumber;
        this.leased = false;
        this.customer = null;
    }

    public void setLeased(boolean leased) {
        this.leased = leased;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLeased() {
        return leased;
    }


    public void setOwner(Customer customer) {
        this.customer = customer;
    }

    public String getOwner() {
        if (customer == null) return null;
        return customer.toString();
    }

    /**
     * Returns a string representing the company, regnumber and wether
     * the car is leased or not. Does not release name of owner.
     *
     * @return java.lang.String
     */
    @Override
    public String toString() {
        return company +
                "-> " + regNumber +
                ": " + (leased ? "Leased" : "Availible");
    }
}
