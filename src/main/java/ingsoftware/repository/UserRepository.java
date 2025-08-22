package ingsoftware.repository;
import ingsoftware.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find all active user IDs with pagination support
     * @param pageable pagination parameters
     * @return list of user IDs
     */
    @Query("SELECT u.id FROM User u WHERE u.lastAccessDate IS NOT NULL ORDER BY u.id")
    List<Long> findAllActiveUserIds(Pageable pageable);
    
    /**
     * Count total number of active users
     * @return count of active users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.lastAccessDate IS NOT NULL")
    long countActiveUsers();
}
