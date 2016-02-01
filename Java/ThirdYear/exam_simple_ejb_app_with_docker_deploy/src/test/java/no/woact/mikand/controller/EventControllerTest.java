package no.woact.mikand.controller;

import no.woact.mikand.models.Event;
import no.woact.mikand.models.EventType;
import org.junit.Before;
import org.junit.Test;

import javax.faces.model.SelectItem;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class EventControllerTest {
    EventController eventController;

    @Before
    public void setUp() throws Exception {
        eventController = new EventController();
        eventController.setEvent(new Event());
    }

    @Test
    public void getEventTypes() throws Exception {
        List<SelectItem> eventTypes = eventController.getEventTypes();
        SelectItem lecture = eventTypes.get(0);
        SelectItem exercise = eventTypes.get(1);

        assertEquals(lecture.getValue(), EventType.LECTURE);
        assertEquals(exercise.getValue(), EventType.EXERCISE);
    }
}