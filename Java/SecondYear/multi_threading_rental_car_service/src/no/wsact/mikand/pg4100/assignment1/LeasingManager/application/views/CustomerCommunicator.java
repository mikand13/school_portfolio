package no.wsact.mikand.pg4100.assignment1.LeasingManager.application.views;

/**
 * Created by Anders Mikkelsen on 27.01.15.
 *
 * View for producing output to the public.
 *
 * @author mikand@westerdals.no
 * @version 1.0
 */
public class CustomerCommunicator {
    /**
     * Newlines and outputs leaseevent.
     *
     * @param leaseMade java.lang.String
     */
    public static void print(String leaseMade) {
        System.out.print("\n" + leaseMade);
    }
}
