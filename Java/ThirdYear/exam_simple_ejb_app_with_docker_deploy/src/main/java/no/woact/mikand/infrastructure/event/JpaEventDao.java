package no.woact.mikand.infrastructure.event;

import no.woact.mikand.models.Event;
import no.woact.mikand.infrastructure.daotypes.JpaDAO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class JpaEventDao extends JpaDAO<Event> {
    JpaEventDao() {
        super(Event.class);
    }

    public JpaEventDao(EntityManager entityManager) {
        super(Event.class, entityManager);
    }
}
