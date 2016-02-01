package no.woact.mikand.controller;

import no.woact.mikand.models.Location;
import no.woact.mikand.models.Subject;
import no.woact.mikand.models.User;
import no.woact.mikand.infrastructure.location.JpaLocationDao;
import no.woact.mikand.infrastructure.subject.JpaSubjectDao;
import no.woact.mikand.infrastructure.user.JpaUser;
import no.woact.mikand.infrastructure.user.UserDao;
import no.woact.mikand.utils.FacesUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class defines the subjectcontroller which handles events in a restlike manor.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@Model
public class SubjectController {
    @Inject
    private JpaSubjectDao subjectDao;

    @Inject
    private JpaLocationDao locationDao;

    @Inject @JpaUser
    private UserDao<User> userDao;

    private int subjectId;
    private Subject subject;

    private int locationId;
    private List<String> userIds;

    @PostConstruct
    public void init() {
        subject = new Subject();
    }

    public List<Subject> index() {
        return subjectDao.getAll();
    }

    public void create() {
        subject.setLocation(locationDao.findById(locationId));
        subject.setUsers(userIds.stream()
                .map(Integer::parseInt)
                .map(userDao::findById)
                .collect(Collectors.toList()));

        if (subjectDao.persist(subject) != null) {
            FacesUtil.doRedirect("Subject", "/LMS/subjects/show.jsf?id=" + subject.getId());
        }
    }

    public void show() {
        subject = subjectDao.findById(subjectId);
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public String getSelectedLocation() {
        return Location.buildLocationDescription(subject.getLocation());
    }

    public List<String> getSelectedUsers() {
        return subject.getSelectedUsersAsStringList();
    }

    public List<SelectItem> getLocations() {
        return subject.getLocationsAsSelectItems(locationDao);
    }

    public List<SelectItem> getUsers() {
        return subject.getAllUsersAsSelectItem(userDao);
    }
}
