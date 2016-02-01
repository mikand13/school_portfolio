package no.woact.mikand.infrastructure.user;

import no.woact.mikand.models.User;
import no.woact.mikand.infrastructure.daotypes.JpaDAO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@JpaUser
@Stateless
public class JpaUserDao extends JpaDAO<User> implements UserDao<User> {
    JpaUserDao() {
        super(User.class);
    }

    public JpaUserDao(EntityManager entityManager) {
        super(User.class, entityManager);
    }
}
