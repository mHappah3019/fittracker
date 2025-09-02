package ingsoftware.dao;

import ingsoftware.model.Habit;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitDAO extends BaseDAO<Habit, Long> {
    List<Habit> findAllByUserId(Long userId);
    Optional<Habit> findByUserIdAndName(Long userId, String name);
}
