package no.woact.mikand.models;

import no.woact.mikand.infrastructure.location.JpaLocationDao;
import no.woact.mikand.infrastructure.user.UserDao;

import javax.faces.model.SelectItem;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class defines the subject model.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@SequenceGenerator(name = "SEQ_SUBJECT", initialValue = 50)
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_SUBJECT")
    private int id;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @Size(min = 0, max = 100)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USR_SUB")
    private List<User> users;

    @ManyToOne
    @JoinColumn(name = "FK_LOCATION")
    @Valid
    private Location location;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "subject")
    private List<Event> events;

    private static final Function<Subject, SelectItem> subjectDesc = s -> new SelectItem(s.getId(), s.getName());

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public static SelectItem buildSubjectSelectItem(Subject subject) {
        return subjectDesc.apply(subject);
    }

    public List<String> getSelectedUsersAsStringList() {
        return getUsers().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
    }

    public List<SelectItem> getLocationsAsSelectItems(JpaLocationDao locationDao) {
        return locationDao.getAll().stream()
                .map(Location::buildLocationSelectItem)
                .collect(Collectors.toList());
    }

    public List<SelectItem> getAllUsersAsSelectItem(UserDao<User> userDao) {
        return userDao.getAll().stream()
                .map(User::buildUserSelectItem)
                .collect(Collectors.toList());
    }
}
