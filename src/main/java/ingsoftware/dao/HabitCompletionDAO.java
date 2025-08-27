package ingsoftware.dao;

import ingsoftware.model.HabitCompletion;

import java.time.LocalDate;

public interface HabitCompletionDAO extends BaseDAO<HabitCompletion, Long> {
    boolean existsByUserIdAndHabitIdAndCompletionDate(Long userId, Long habitId, LocalDate completionDate);
}
