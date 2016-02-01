package no.woact.mikand.infrastructure.daotypes;

import no.woact.mikand.infrastructure.DAO;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * This class defines the ArrayListDAO implementation for a given type.
 *
 * @author anders
 * @version 03.12.15.
 *
 * @param <T>
 */
public abstract class ArrayListDAO<T> extends DAO<T> {
    protected final List<T> records = new ArrayList<>();

    /**
     * Sets DAO type.
     *
     * @param type Class of T
     */
    protected ArrayListDAO(Class<T> type) {
        super(type);
    }

    @Override
    public T findById(int id) {
        return records.get(id);
    }

    @Override
    public List<T> getAll() {
        return records;
    }
}
