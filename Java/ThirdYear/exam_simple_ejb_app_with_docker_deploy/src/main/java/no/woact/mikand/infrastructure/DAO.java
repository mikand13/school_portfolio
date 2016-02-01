package no.woact.mikand.infrastructure;

/**
 *
 * This defines all normal DAO behaviours for a given entity DAO for a given T.
 *
 * @author Anders Mikkelsen
 * @version 29.11.2015
 *
 * @param <T>
 */
public abstract class DAO<T> implements EntityDao<T> {
    /**
     * Type used for persistence.
     */
    protected final Class<T> type;

    /**
     * Sets DAO type.
     *
     * @param type Class of T
     */
    protected DAO(Class<T> type) {
        this.type = type;
    }
}
