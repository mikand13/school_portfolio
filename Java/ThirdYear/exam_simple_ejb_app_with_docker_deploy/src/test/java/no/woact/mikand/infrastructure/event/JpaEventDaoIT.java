package no.woact.mikand.infrastructure.event;

import no.woact.mikand.models.Event;
import no.woact.mikand.models.EventType;
import no.woact.mikand.models.Subject;
import no.woact.mikand.infrastructure.subject.JpaSubjectDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class
 *
 * @author anders
 * @version 03.12.15.
 */
public class JpaEventDaoIT {
    private EntityManagerFactory factory;
    private EntityManager entityManager;
    private JpaEventDao eventDao;
    private JpaSubjectDao subjectDao;

    @Before
    public void setUp() throws Exception {
        factory = Persistence.createEntityManagerFactory("pg5100-lms");
        entityManager = factory.createEntityManager();
        eventDao = new JpaEventDao(entityManager);
        subjectDao = new JpaSubjectDao(entityManager);
    }

    @After
    public void tearDown() throws Exception {
        entityManager.close();
        factory.close();
    }

    @Test
    public void persist() throws Exception {
        Event event = new Event();
        Subject subject = new Subject();
        subject.setName("PG5200");
        subject.setUsers(new ArrayList<>());

        event.setDescription("some desc");
        event.setTitle("some title");
        event.setStartTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
        event.setEndTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
        event.setSubject(subject);
        event.setEventType(EventType.EXERCISE);

        entityManager.getTransaction().begin();
        subjectDao.persist(subject);
        Event result = eventDao.persist(event);
        entityManager.getTransaction().commit();

        assertTrue(result.getId() > 0);
    }

    @Test
    public void findById() throws Exception {
        Event event = eventDao.findById(1);

        assertEquals("Lecture 1", event.getTitle());
    }

    @Test
    public void getAll() throws Exception {
        List<Event> events = eventDao.getAll();

        assertEquals(2, events.size());
    }
}
