package no.woact.mikand.infrastructure.user;

import no.woact.mikand.models.User;
import no.woact.mikand.infrastructure.daotypes.ArrayListDAO;

import javax.inject.Inject;

public class ArrayListUserDao extends ArrayListDAO<User> implements UserDao<User> {
    @Inject ArrayListUserDao() {
        super(User.class);
    }

    @Override
    public User persist(User user) {
        user.setId(records.size());
        records.add(user);
        return user;
    }

    @Override
    public boolean update(User user) {
        records.set(user.getId(), user);
        return true;
    }

    @Override
    public boolean remove(User user) {
        records.set(user.getId(), null);
        return true;
    }
}
