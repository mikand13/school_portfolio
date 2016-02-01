package no.woact.mikand.models;

import no.woact.mikand.infrastructure.subject.JpaSubjectDao;
import no.woact.mikand.models.constraint.ValidDates;
import no.woact.mikand.models.constraint.ValidEventTypes;

import javax.faces.model.SelectItem;
import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class definss the event model. It is one custom validator for dates and splits its date between two tables.
 * Event and Event_details.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@Entity
@SecondaryTable(name = "EVENT_DETAILS")
@SequenceGenerator(name = "SEQ_EVENT", initialValue = 50)
@ValidDates
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EVENT")
    private int id;

    @NotNull
    @ValidEventTypes
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @NotNull
    @Size(min = 5, max = 25)
    private String title;

    @Size(max = 100)
    private String description;

    @NotNull
    @Valid
    @ManyToOne
    @JoinColumn(name = "FK_SUBJECT")
    private Subject subject;

    @NotNull
    @Column(table = "EVENT_DETAILS")
    private Date startTime;

    @NotNull
    @Column(table = "EVENT_DETAILS")
    private Date endTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public List<SelectItem> getSubjectsAsSelectItems(JpaSubjectDao subjectDao) {
        return subjectDao.getAll().stream()
                .map(Subject::buildSubjectSelectItem)
                .collect(Collectors.toList());
    }

    public List<SelectItem> getAllEventTypes() {
        return Arrays.asList(EventType.values()).stream()
                .map(EventType::extractEventType)
                .collect(Collectors.toList());
    }

    public Set<ConstraintViolation<Event>> isValid() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Event>> violations = validator.validate(this);

        return violations;
    }
}
