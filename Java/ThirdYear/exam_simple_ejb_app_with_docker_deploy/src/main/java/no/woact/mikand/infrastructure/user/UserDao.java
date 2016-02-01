package no.woact.mikand.infrastructure.user;

import no.woact.mikand.infrastructure.EntityDao;

/**
 *
 * Qualifier for user daos for a given T.
 *
 * @author Anders Mikkelsen
 * @version 29.11.2015
 *
 * @param <T>
 */
public interface UserDao<T> extends EntityDao<T> {}
