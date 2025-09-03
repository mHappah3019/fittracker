package ingsoftware.dao;

import java.util.List;
import java.util.Optional;

/**
 * Base DAO interface providing common CRUD operations for all entities.
 * Generic interface that can be extended by specific entity DAOs.
 */
public interface BaseDAO<T, ID> {
    // Saves or updates an entity and returns the persisted instance
    T save(T entity);
    
    // Finds an entity by its ID, returns empty Optional if not found
    Optional<T> findById(ID id);
    
    // Retrieves all entities of this type from the database
    List<T> findAll();
    
    // Deletes the given entity from the database
    void delete(T entity);
    
    // Deletes an entity by its ID
    void deleteById(ID id);
    
    // Checks if an entity with the given ID exists
    boolean existsById(ID id);
    
    // Returns the total count of entities in the table
    long count();
}
