package ingsoftware.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void delete(T entity);
    void deleteById(ID id);
    boolean existsById(ID id);
    long count();
}
