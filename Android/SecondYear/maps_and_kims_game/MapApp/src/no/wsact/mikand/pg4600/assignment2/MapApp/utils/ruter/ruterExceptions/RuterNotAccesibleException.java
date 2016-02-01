package no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter.ruterExceptions;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter
 *
 * This class is a simple Facade for a Ruter Exception.
 *
 * @author Anders Mikkelsen
 * @version 11.05.15
 */
@SuppressWarnings("UnusedDeclaration")
public class RuterNotAccesibleException extends Throwable {
    @Override
    public String getMessage() {
        return "Ruter API Not Accessible";
    }
}
