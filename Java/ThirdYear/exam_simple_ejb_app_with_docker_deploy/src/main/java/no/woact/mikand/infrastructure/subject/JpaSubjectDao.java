package no.woact.mikand.infrastructure.subject;

import no.woact.mikand.models.Subject;
import no.woact.mikand.infrastructure.daotypes.JpaDAO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;

@Stateless
public class JpaSubjectDao extends JpaDAO<Subject> {
    JpaSubjectDao() {
        super(Subject.class);
    }

    public JpaSubjectDao(EntityManager entityManager) {
        super(Subject.class, entityManager);
    }
}
