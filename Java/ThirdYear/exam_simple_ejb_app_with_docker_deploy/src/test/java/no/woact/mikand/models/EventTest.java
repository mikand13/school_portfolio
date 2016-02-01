package no.woact.mikand.models;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class EventTest {
    private Validator validator;
    private Event event;

    @Before
    public void setUp() throws Exception {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        Subject subject = new Subject();
        subject.setName("PG5200");
        subject.setUsers(new ArrayList<>());

        event = new Event();
        event.setDescription("some desc");
        event.setTitle("some title");
        event.setSubject(subject);
        event.setEventType(EventType.EXERCISE);
    }

    @Test
    public void invalidEvent() throws Exception {
        Event invalidEvent = new Event();

        Set<ConstraintViolation<Event>> violations = validator.validate(invalidEvent);

        assertEquals(7, violations.size());
    }

    @Test
    public void invalidDate() throws Exception {
        event.setEndTime(new Timestamp(Calendar.getInstance().getTime().getTime()));

        Thread.sleep(10);

        event.setStartTime(new Timestamp(Calendar.getInstance().getTime().getTime()));

        Set<ConstraintViolation<Event>> violations = validator.validate(event);

        assertEquals(1, violations.size());
    }

    @Test
    public void validEvent() throws Exception {
        event.setStartTime(new Timestamp(Calendar.getInstance().getTime().getTime()));
        event.setEndTime(new Timestamp(Calendar.getInstance().getTime().getTime()));

        Set<ConstraintViolation<Event>> violations = validator.validate(event);

        assertTrue(violations.isEmpty());
    }
}