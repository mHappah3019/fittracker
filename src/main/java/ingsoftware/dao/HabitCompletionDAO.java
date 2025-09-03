package ingsoftware.dao;

import ingsoftware.model.HabitCompletion;

import java.time.LocalDate;

/**
 * DAO interface for HabitCompletion entity operations.
 * Manages habit completion records and prevents duplicate entries.
 */
public interface HabitCompletionDAO extends BaseDAO<HabitCompletion, Long> {
    // Checks if a habit completion already exists for a user on a specific date
    boolean existsByUserIdAndHabitIdAndCompletionDate(Long userId, Long habitId, LocalDate completionDate);
}
