package no.woact.mikand.controller;

import no.woact.mikand.models.Location;
import no.woact.mikand.infrastructure.location.JpaLocationDao;
import no.woact.mikand.utils.FacesUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import java.util.List;

/**
 * This class defines the locationcontroller which handles events in a restlike manor.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@Model
public class LocationController {
    @Inject
    private JpaLocationDao locationDao;

    private int locationId;
    private Location location;

    @PostConstruct
    public void init() {
        location = new Location();
    }

    public List<Location> index() {
        return locationDao.getAll();
    }

    public void create() {
        if(locationDao.persist(location) != null) {
            FacesUtil.doRedirect("Location", "/LMS/locations/show.jsf?id=" + location.getId());
        }
    }

    public void show() {
        location = locationDao.findById(locationId);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public int getLocationId() {
        return locationId;
    }
}
