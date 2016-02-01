package no.woact.mikand.controller;

import no.woact.mikand.models.Subject;
import no.woact.mikand.models.User;
import no.woact.mikand.models.UserType;
import org.junit.Before;
import org.junit.Test;

import javax.faces.model.SelectItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class UserControllerTest {
    private UserController userController;
    private Subject subject;

    @Before
    public void setUp() throws Exception {
        userController = new UserController();
        User user = new User();
        ArrayList<Subject> subjects = new ArrayList<>();
        subject = new Subject();
        subject.setName("testsubject");
        subjects.add(subject);
        user.setSubjects(subjects);
        userController.setUser(user);
    }

    @Test
    public void getUserTypes() throws Exception {
        List<SelectItem> userTypes = userController.getUserTypes();
        SelectItem lecture = userTypes.get(0);
        SelectItem exercise = userTypes.get(1);

        assertEquals(lecture.getValue(), UserType.STUDENT);
        assertEquals(exercise.getValue(), UserType.TEACHER);
    }

    @Test
    public void getSelectedSubjects() throws Exception {
        List<String> subjects = userController.getSelectedSubjects();

        assertEquals(subjects.get(0), subject.getName());
    }
}