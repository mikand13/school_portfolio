package no.wsact.mikand.pg4600.assignment2.MapApp.utils.ruter;

/**
 * Project: MapApp
 * Package: no.wsact.mikand.pg4600.assignment2.MapApp.utils
 *
 * This class is a VO for stops in the Ruter Network
 *
 * @author Anders Mikkelsen
 * @version 11.05.15
 */
public class RuterStop {
    private final int id;
    private final String name;
    private final String shortName;
    private final int latitude;
    private final int longitude;
    private final String zone;

    public RuterStop(
            int id, String name, String shortName, int latitude, int longitude, String zone) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zone = zone;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public String getZone() {
        return zone;
    }
}
