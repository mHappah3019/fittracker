package ingsoftware.repository;

import ingsoftware.model.HabitCompletion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    boolean existsByUserIdAndHabitIdAndCompletionDate(Long userId, Long habitId, LocalDate completionDate);
}
