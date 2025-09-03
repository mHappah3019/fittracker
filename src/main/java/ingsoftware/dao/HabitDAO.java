package ingsoftware.dao;

import ingsoftware.model.Habit;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for Habit entity operations.
 * Handles habit-specific queries
 */
public interface HabitDAO extends BaseDAO<Habit, Long> {
    // Finds all habits belonging to a specific user
    List<Habit> findAllByUserId(Long userId);
    
    // Finds a habit by user ID and habit name, used for duplicate checking
    Optional<Habit> findByUserIdAndName(Long userId, String name);
}
