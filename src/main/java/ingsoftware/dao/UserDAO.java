package ingsoftware.dao;

import ingsoftware.model.User;

import java.util.List;

/**
 * DAO interface for User entity operations.
 * Extends BaseDAO to provide user-specific database queries.
 */
public interface UserDAO extends BaseDAO<User, Long> {
    // Retrieves paginated list of active user IDs for efficient batch processing
    List<Long> findAllActiveUserIds(int offset, int limit);
}
