package no.woact.mikand.infrastructure;

import java.util.List;

/**
 *
 * This defines a DAO interface for all entities of a given E.
 *
 * @author Anders Mikkelsen
 * @version 29.11.2015
 *
 * @param <E>
 */
public interface EntityDao<E> {
    /**
     * Persists a single record to the DB.
     *
     * @param record E
     * @return E
     */
    E persist(E record);

    /**
     * Updates a single record.
     *
     * @param record E
     * @return boolean
     */
    boolean update(E record);

    /**
     * Find a single record by id.
     *
     * @param id int
     * @return E
     */
    E findById(int id);

    /**
     * Finds and returns all records if the model has set a index namedquery.
     *
     * @return List of E
     */
    List<E> getAll();

    /**
     * Removes a single record from persistence.
     *
     * @param record E
     * @return boolean
     */
    boolean remove(E record);
}
