package ingsoftware.repository;

import ingsoftware.model.Habit;
import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    boolean existsByUserAndHabitAndCompletionDate(User user, Habit habit, LocalDate completionDate);
    
    Optional<HabitCompletion> findByUserAndHabitAndCompletionDate(User user, Habit habit, LocalDate completionDate);
}
