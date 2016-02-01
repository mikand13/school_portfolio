package no.woact.mikand.infrastructure.user;

import no.woact.mikand.models.User;
import no.woact.mikand.models.UserType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * This class tests the ArrayList Dao.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
public class ArrayListUserDaoIT {
    private ArrayListUserDao userDao;

    @Before
    public void setUp() throws Exception {
        userDao = new ArrayListUserDao();

        User user = new User();
        user.setEmail("one@one.com");
        user.setPassword("oNe143");
        user.setUserType(UserType.STUDENT);

        userDao.persist(user);
    }

    @After
    public void tearDown() throws Exception {
        userDao = null;
    }

    @Test
    public void persist() throws Exception {
        User user = new User();
        user.setEmail("per@test.com");
        user.setPassword("abC123");
        user.setUserType(UserType.STUDENT);

        User result = userDao.persist(user);

        assertTrue(result.getId() > 0);
    }

    @Test
    public void findById() throws Exception {
        User user = userDao.findById(0);

        assertEquals("one@one.com", user.getEmail());
        assertEquals(UserType.STUDENT, user.getUserType());
    }

    @Test
    public void getAll() throws Exception {
        List<User> users = userDao.getAll();

        assertEquals(1, users.size());
    }

    @Test
    public void findByIdAndUpdate() throws Exception {
        User user = userDao.findById(0);
        user.setEmail("test@test.com");

        boolean updated = userDao.update(user);

        User result = userDao.findById(0);

        assertTrue(updated);
        assertEquals("test@test.com", result.getEmail());
    }

    @Test
    public void remove() throws Exception {
        User user = userDao.findById(0);

        boolean removed = userDao.remove(user);

        User result = userDao.findById(0);

        assertTrue(removed);
        assertNull(result);
    }
}