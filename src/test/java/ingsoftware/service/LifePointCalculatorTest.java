package ingsoftware.service;

import ingsoftware.model.Habit;
import ingsoftware.model.HabitDifficulty;
import ingsoftware.model.HabitFrequencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LifePointCalculatorTest {

    private LifePointCalculator lifePointCalculator;
    private List<Habit> habits;

    @BeforeEach
    void setUp() {
        lifePointCalculator = new LifePointCalculator();
        habits = new ArrayList<>();
    }

    @Test
    void compute_shouldReturnZero_whenHabitsListIsEmpty() {
        // Arrange
        double thresholdPercent = 80.0;

        // Act
        int result = lifePointCalculator.compute(Collections.emptyList(), thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(0);
    }

    @Test
    void compute_shouldReturnBonus_whenCompletionRateAboveThreshold() {
        // Arrange
        Habit completedHabit1 = createCompletedHabit("Habit 1");
        Habit completedHabit2 = createCompletedHabit("Habit 2");
        Habit incompleteHabit = createIncompleteHabit("Habit 3");
        
        habits = Arrays.asList(completedHabit1, completedHabit2, incompleteHabit);
        double thresholdPercent = 60.0; // 2/3 = 66.67% > 60%

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(10); // default bonus
    }

    @Test
    void compute_shouldReturnMalus_whenCompletionRateBelowThreshold() {
        // Arrange
        Habit completedHabit = createCompletedHabit("Habit 1");
        Habit incompleteHabit1 = createIncompleteHabit("Habit 2");
        Habit incompleteHabit2 = createIncompleteHabit("Habit 3");
        
        habits = Arrays.asList(completedHabit, incompleteHabit1, incompleteHabit2);
        double thresholdPercent = 50.0; // 1/3 = 33.33% < 50%

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(-5); // default malus
    }

    @Test
    void compute_shouldReturnBonus_whenCompletionRateEqualsThreshold() {
        // Arrange
        Habit completedHabit = createCompletedHabit("Habit 1");
        Habit incompleteHabit = createIncompleteHabit("Habit 2");
        
        habits = Arrays.asList(completedHabit, incompleteHabit);
        double thresholdPercent = 50.0; // 1/2 = 50% == 50%

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(10); // should return bonus when equal
    }

    @Test
    void compute_shouldReturnBonus_whenAllHabitsCompleted() {
        // Arrange
        Habit completedHabit1 = createCompletedHabit("Habit 1");
        Habit completedHabit2 = createCompletedHabit("Habit 2");
        Habit completedHabit3 = createCompletedHabit("Habit 3");
        
        habits = Arrays.asList(completedHabit1, completedHabit2, completedHabit3);
        double thresholdPercent = 80.0; // 3/3 = 100% > 80%

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(10);
    }

    @Test
    void compute_shouldReturnMalus_whenNoHabitsCompleted() {
        // Arrange
        Habit incompleteHabit1 = createIncompleteHabit("Habit 1");
        Habit incompleteHabit2 = createIncompleteHabit("Habit 2");
        
        habits = Arrays.asList(incompleteHabit1, incompleteHabit2);
        double thresholdPercent = 10.0; // 0/2 = 0% < 10%

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(-5);
    }

    @Test
    void compute_shouldUseCustomBonusAndMalus() {
        // Arrange
        Habit completedHabit = createCompletedHabit("Habit 1");
        Habit incompleteHabit = createIncompleteHabit("Habit 2");
        
        habits = Arrays.asList(completedHabit, incompleteHabit);
        double thresholdPercent = 60.0; // 1/2 = 50% < 60%
        int customBonus = 20;
        int customMalus = -10;

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent, customBonus, customMalus);

        // Assert
        assertThat(result).isEqualTo(customMalus);
    }

    @Test
    void compute_shouldReturnCustomBonus_whenThresholdMet() {
        // Arrange
        Habit completedHabit = createCompletedHabit("Habit 1");
        Habit incompleteHabit = createIncompleteHabit("Habit 2");
        
        habits = Arrays.asList(completedHabit, incompleteHabit);
        double thresholdPercent = 40.0; // 1/2 = 50% > 40%
        int customBonus = 15;
        int customMalus = -8;

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent, customBonus, customMalus);

        // Assert
        assertThat(result).isEqualTo(customBonus);
    }

    @Test
    void compute_shouldHandleHighThreshold() {
        // Arrange
        Habit completedHabit1 = createCompletedHabit("Habit 1");
        Habit completedHabit2 = createCompletedHabit("Habit 2");
        Habit incompleteHabit = createIncompleteHabit("Habit 3");
        
        habits = Arrays.asList(completedHabit1, completedHabit2, incompleteHabit);
        double thresholdPercent = 70.0; // 2/3 = 66.67% < 70%

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(-5); // should return malus
    }

    @Test
    void compute_shouldHandleLowThreshold() {
        // Arrange
        Habit completedHabit = createCompletedHabit("Habit 1");
        Habit incompleteHabit1 = createIncompleteHabit("Habit 2");
        Habit incompleteHabit2 = createIncompleteHabit("Habit 3");
        Habit incompleteHabit3 = createIncompleteHabit("Habit 4");
        
        habits = Arrays.asList(completedHabit, incompleteHabit1, incompleteHabit2, incompleteHabit3);
        double thresholdPercent = 20.0; // 1/4 = 25% > 20%

        // Act
        int result = lifePointCalculator.compute(habits, thresholdPercent);

        // Assert
        assertThat(result).isEqualTo(10); // should return bonus
    }

    private Habit createCompletedHabit(String name) {
        Habit habit = new Habit();
        habit.setName(name);
        habit.setFrequency(HabitFrequencyType.DAILY);
        habit.setDifficulty(HabitDifficulty.MEDIUM);
        habit.setLastCompletedDate(LocalDate.now()); // completed today
        return habit;
    }

    private Habit createIncompleteHabit(String name) {
        Habit habit = new Habit();
        habit.setName(name);
        habit.setFrequency(HabitFrequencyType.DAILY);
        habit.setDifficulty(HabitDifficulty.MEDIUM);
        habit.setLastCompletedDate(LocalDate.now().minusDays(1)); // not completed today
        return habit;
    }
}