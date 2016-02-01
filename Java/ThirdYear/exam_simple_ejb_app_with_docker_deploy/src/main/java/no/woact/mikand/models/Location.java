package no.woact.mikand.models;

import javax.faces.model.SelectItem;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.function.Function;

/**
 * This class defines the location model.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@Entity
@SequenceGenerator(name = "SEQ_LOCATION", initialValue = 50)
public class Location {
    private static final Function<Location, String> roomDesc = l -> l.getBuilding() + " - " + l.getRoom();
    private static final Function<Location, SelectItem> locBuild = l -> new SelectItem(l.getId(), roomDesc.apply(l));

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_LOCATION")
    private int id;

    @NotNull
    private String room;
    @NotNull
    private String building;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public static SelectItem buildLocationSelectItem(Location location) {
        return locBuild.apply(location);
    }

    public static String buildLocationDescription(Location location) {
        return roomDesc.apply(location);
    }

    public String getLocationDescription() {
        return Location.buildLocationDescription(this);
    }
}
