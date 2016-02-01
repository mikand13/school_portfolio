package no.wsact.mikand.pg4100.assignment2.views;

import java.util.UUID;

/**
 * Project: Assignment2
 * Package: no.wsact.mikand.pg4100.assignment2.views
 * <p>
 * This class is a template for server applications.
 *
 * @author Anders Mikkelsen
 * @version 15.03.2015
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class Application implements Runnable {
    private final UUID id;
    private final String name;

    /**
     * Gives every application an uuid and sets their name.
     *
     * @param name java.lang.String
     */
    Application(String name) {
        id = UUID.randomUUID();
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
