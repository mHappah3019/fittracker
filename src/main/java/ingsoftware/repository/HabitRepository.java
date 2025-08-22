package ingsoftware.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ingsoftware.model.Habit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Habit Repository Interface
@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findAllByUserId(Long userId);
    Optional<Habit> findByUserIdAndName(Long userId, String name);

    boolean existsByIdAndUserIdAndLastCompletedDate(Long habitId, Long userId, LocalDate today);
}
