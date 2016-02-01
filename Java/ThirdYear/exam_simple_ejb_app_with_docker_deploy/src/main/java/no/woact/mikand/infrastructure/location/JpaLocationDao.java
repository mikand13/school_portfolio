package no.woact.mikand.infrastructure.location;

import no.woact.mikand.models.Location;
import no.woact.mikand.infrastructure.daotypes.JpaDAO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class JpaLocationDao extends JpaDAO<Location> {
    JpaLocationDao() {
        super(Location.class);
    }

    public JpaLocationDao(EntityManager entityManager) {
        super(Location.class, entityManager);
    }
}
