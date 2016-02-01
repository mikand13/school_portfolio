package no.wsact.mikand.pg4100.assignment2.views;

import no.wsact.mikand.pg4100.assignment2.controllers.ApplicationServer;
import no.wsact.mikand.pg4100.assignment2.utils.Logger;
import no.wsact.mikand.pg4100.assignment2.views.utils.ApplicationUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This is a simple class I made to give the server admin an options menu.
 *
 * @author Anders Mikkelsen
 * @version 14.03.15
 */
public class ServerOptionController extends Application implements Runnable {
    private final ApplicationServer applicationServer;

    /**
     * Initializes the options with a parent server, to bind shutdown.
     *
     * @param applicationServer ApplicationServer
     */
    public ServerOptionController(ApplicationServer applicationServer) {
        super("ServerOptions");

        this.applicationServer = applicationServer;
    }

    /**
     * Fires up the Runnable.
     */
    @Override
    public void run() {
        ApplicationUtils.addApplicationLog(this, "STARTED");

        doOptions();

        ApplicationUtils.addApplicationLog(this, "EXITED");
    }

    /**
     * Displays a menu with options, and awaits input.
     */
    private void doOptions() {
        try (BufferedReader system = new BufferedReader(new InputStreamReader(System.in))) {
            boolean active = true;

            while (active) {
                showMenu();

                while (!system.ready()) {
                    Thread.sleep(100);
                }

                String choice = system.readLine();

                active = handleMenu(choice);
            }
        } catch (IOException e) {
            ApplicationUtils.addApplicationLog(this, "SYSTEM IN CLOSED UNEXPECTEDLY");
        } catch (InterruptedException e) {
            ApplicationUtils.addApplicationLog(this, "INTERRUPTED UNEXPECTEDLY");
        }
    }

    /**
     * Takes care of menuoptions.
     *
     * @param choice java.lang.String
     * @return boolean
     */
    private boolean handleMenu(String choice) {
        if (choice.equalsIgnoreCase("1")) {
            System.out.println(Logger.getInstance().toString());
        } else if (choice.equalsIgnoreCase("2")) {
            applicationServer.killServer();

            return false;
        } else {
            System.out.println("Illegal choice.");
        }

        return true;
    }

    /**
     * Displays an optionsmenu to terminal.
     */
    private void showMenu() {
        System.out.println("\nWhat do you wish to do?");
        System.out.println("1: Show log");
        System.out.println("2: Shutdown\n");
    }
}
