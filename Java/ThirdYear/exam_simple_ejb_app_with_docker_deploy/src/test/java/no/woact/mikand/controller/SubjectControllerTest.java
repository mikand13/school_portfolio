package no.woact.mikand.controller;

import no.woact.mikand.models.Location;
import no.woact.mikand.models.Subject;
import no.woact.mikand.models.User;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class SubjectControllerTest {
    private SubjectController subjectController;
    private User user;

    @Before
    public void setUp() throws Exception {
        subjectController = new SubjectController();
        Subject subject = new Subject();
        user = new User();
        user.setEmail("test@test.com");
        ArrayList<User> users = new ArrayList<>();
        users.add(user);
        subject.setUsers(users);
        Location location = new Location();
        location.setBuilding("testbuilding");
        location.setRoom("testroom");
        subject.setLocation(location);

        subjectController.setSubject(subject);
    }

    @Test
    public void getSelectedUsers() throws Exception {
        List<String> users = subjectController.getSelectedUsers();

        assertEquals(users.get(0), user.getEmail());
    }

    @Test
    public void getSelectedLocation() throws Exception {
        String locationDescription = subjectController.getSelectedLocation();

        assertEquals(locationDescription, "testbuilding - testroom");
    }
}