package no.wsact.mikand.pg4100.assignment1.LeasingManager.program;

import no.wsact.mikand.pg4100.assignment1.LeasingManager.application.controllers.RentalProcessor;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * Driver for initializing the program.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
class RentalCarProgram {
    private static final int MAX_CUSTOMERS = 10;

    public static void main(String[] args) {
        new Thread(new RentalProcessor(MAX_CUSTOMERS)).start();
    }
}
