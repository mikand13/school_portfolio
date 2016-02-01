package no.woact.mikand.controller;

import no.woact.mikand.models.Event;
import no.woact.mikand.infrastructure.event.JpaEventDao;
import no.woact.mikand.infrastructure.subject.JpaSubjectDao;
import no.woact.mikand.utils.FacesUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import java.util.*;

/**
 * This class defines the eventcontroller which handles events in a restlike manor. All logic is modelside except class
 * level constraints.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@Model
public class EventController {
    @Inject
    private JpaEventDao eventDao;

    @Inject
    private JpaSubjectDao subjectDao;

    private int eventId;
    private Event event;

    private int subjectId;
    private List<Event> allEvents;

    @PostConstruct
    public void init() {
        event = new Event();

        Date now = Calendar.getInstance(Locale.getDefault()).getTime();

        event.setStartTime(now);
        event.setEndTime(now);
    }

    public List<Event> index() {
        return allEvents == null ? allEvents = eventDao.getAll() : allEvents;
    }

    /**
     * Manually validates dates on type level constraint as it is not supported in jsf.
     */
    public void create() {
        event.setSubject(subjectDao.findById(subjectId));

        Set<ConstraintViolation<Event>> violations = event.isValid();

        if (violations.isEmpty()) {
            if (eventDao.persist(event) != null) {
                FacesUtil.doRedirect("Event", "/LMS/events/show.jsf?id=" + event.getId());
            }
        } else {
            FacesUtil.addViolationsToFacesMessages(violations);
        }
    }

    public void show() {
        event = eventDao.findById(eventId);
    }

    public Event getLocation() {
        return event;
    }

    public void setLocation(Event location) {
        this.event = location;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public List<SelectItem> getSubjects() {
        return event.getSubjectsAsSelectItems(subjectDao);
    }

    public List<SelectItem> getEventTypes() {
        return event.getAllEventTypes();
    }
}
