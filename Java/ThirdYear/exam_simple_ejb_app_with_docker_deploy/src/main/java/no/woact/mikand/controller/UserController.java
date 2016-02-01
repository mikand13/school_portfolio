package no.woact.mikand.controller;

import no.woact.mikand.models.User;
import no.woact.mikand.models.UserType;
import no.woact.mikand.infrastructure.user.JpaUser;
import no.woact.mikand.infrastructure.user.UserDao;
import no.woact.mikand.utils.FacesUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import java.util.List;

/**
 * This class defines the usercontroller which handles events in a restlike manor.
 *
 * @author Anders Mikkelsen
 * @version 03.12.15.
 */
@Model
public class UserController {
    @Inject @JpaUser
    private UserDao<User> userDao;

    private int userId;
    private User user;

    @PostConstruct
    public void init() {
        user = new User();
    }

    public void create() {
        if (userDao.persist(user) != null) {
            FacesUtil.doRedirect("User", "/LMS/users/show.jsf?id=" + user.getId());
        }
    }

    public List<User> index() {
        return userDao.getAll();
    }

    public void show() {
        user = userDao.findById(userId);
    }

    public List<SelectItem> getUserTypes() {
        return UserType.getAllUserTypesAsSelectItem();
    }

    public List<String> getSelectedSubjects() {
        return user.getAllSubjectsAsStringList();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
