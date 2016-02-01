package no.woact.mikand.infrastructure.daotypes;


import no.woact.mikand.infrastructure.DAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 *
 * This defines all normal DAO behaviours for a given entity JPA DAO for a given T.
 *
 * @author Anders Mikkelsen
 * @version 29.11.2015
 *
 * @param <T>
 */
public abstract class JpaDAO<T> extends DAO<T> {
    @PersistenceContext(unitName = "pg5100-lms")
    private EntityManager entityManager;

    protected JpaDAO(Class<T> type) {
        super(type);
    }

    protected JpaDAO(Class<T> type, EntityManager entityManager) {
        super(type);
        this.entityManager = entityManager;
    }

    @Override
    public T persist(T record) {
        if (entityManager == null) return null;

        entityManager.persist(record);
        return record;
    }

    @Override
    public boolean update(T record) {
        if (entityManager == null) return false;

        if (!entityManager.contains(record)) {
            entityManager.merge(record);
        }

        return true;
    }

    @Override
    public T findById(int id) {
        if (entityManager == null) return null;

        return entityManager.find(type, id);
    }

    @Override
    public List<T> getAll() {
        if (entityManager == null) return null;

        TypedQuery<T> query =
                entityManager.createQuery("Select t from " + type.getName() + " t", type);
        return query.getResultList();
    }

    @Override
    public boolean remove(T record) {
        if (entityManager == null) return false;

        entityManager.remove(record);
        return true;
    }
}
