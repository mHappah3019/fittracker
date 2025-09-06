package ingsoftware.service;

import ingsoftware.model.*;
import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.service.strategy.ExperienceStrategyFactory;
import ingsoftware.service.strategy.GamificationStrategy;
import ingsoftware.service.strategy.LifePointCalculator;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GamificationService {

    private final HabitService habitService;
    private final LifePointCalculator calculator;
    private final ExperienceStrategyFactory strategyFactory;

    private static final double XP_PER_LEVEL = 100.0;
    final int INACTIVITY_PENALTY_PER_DAY = -10;

    public GamificationService(HabitService habitService,
                               LifePointCalculator calculator,
                               ExperienceStrategyFactory strategyFactory) {
        this.habitService = habitService;
        this.calculator = calculator;
        this.strategyFactory = strategyFactory;
    }

    // Calculates XP gained from completing a habit using the current gamification strategy
    // Applies base XP from difficulty level and any active multipliers or bonuses
    public double calculateHabitXP(Habit habit, User user) {
        GamificationStrategy strategy = strategyFactory.createStrategy();

        double baseXP = habit.getDifficulty().getBaseXP();

        return strategy.calculateExperience(baseXP, user);
    }

    // Updates user's life points based on completed habits and inactivity penalties
    // Returns DTO containing old/new values and whether level decreased
    public LifePointsDTO updateUserLifePoints(User user, LocalDate today) {
        // Salva i punti vita iniziali
        int oldLifePoints = user.getLifePoints();

        // Calcola il delta dei punti vita
        int totalLifePointsDelta = calculateLifePointsDelta(user, today);

        // Applica i cambiamenti e gestisci la diminuzione di livello se necessario
        boolean isLevelDecreased = applyLifePointsChanges(user, totalLifePointsDelta);

        // Crea e restituisce il DTO con i valori vecchi e nuovi
        return new LifePointsDTO(isLevelDecreased, user.getLifePoints(), oldLifePoints);
    }

    /**
     * Calculate the life point changes for a given user based on completed habits and inactivity.
     * Composed of Completed Habits Points and Inactivity Penalty
     */
    private int calculateLifePointsDelta(User user, LocalDate today) {
        int totalLifePointsDelta = 0;
        LocalDate lastAccess = user.getLastAccessDate();

        // Recupera tutte le abitudini dell'utente una sola volta
        List<Habit> allUserHabits = habitService.findAllByUserId(user.getId());

        if (lastAccess != null) {
            // Aggiungi punti per abitudini completate
            totalLifePointsDelta += calculateCompletedHabitsPoints(allUserHabits, lastAccess);

            // Sottrai punti per inattività
            totalLifePointsDelta += calculateInactivityPenalty(lastAccess, today);
        }

        // Applica il moltiplicatore di difficoltà se necessario
        if (totalLifePointsDelta != 0) {
            totalLifePointsDelta = applyDifficultyMultiplier(totalLifePointsDelta, allUserHabits);
        }

        return totalLifePointsDelta;
    }

    /**
     * Calculate the penalty for inactivity between two dates
     */
    private int calculateInactivityPenalty(LocalDate lastAccess, LocalDate today) {
        long daysSinceLastAccess = ChronoUnit.DAYS.between(lastAccess, today);

        if (daysSinceLastAccess > 1) {
            long inactiveDays = daysSinceLastAccess - 1;
            return (int) (inactiveDays * INACTIVITY_PENALTY_PER_DAY);
        }

        return 0;
    }

    /**
     * Apply difficulty multiplier based on the average difficulty of the user's habits
     */
    private int applyDifficultyMultiplier(int pointsDelta, List<Habit> habits) {
        double difficultyMultiplier = calculateDifficultyMultiplier(habits);
        return (int) Math.round(pointsDelta * difficultyMultiplier);
    }

    /**
     * Apply life points changes to the user and handle potential level decrease
     */
    private boolean applyLifePointsChanges(User user, int totalLifePointsDelta) {
        if (totalLifePointsDelta == 0) {
            return false;
        }

        user.addLifePoints(totalLifePointsDelta);
        return checkAndHandleLevelDecrease(user);
    }



    /**
     * Calculate the difficulty multiplier based on the average difficulty of the user's habits
     */
    private double calculateDifficultyMultiplier(List<Habit> habits) {
        if (habits.isEmpty()) {
            return 1.0; // Moltiplicatore neutro se non ci sono abitudini
        }

        // Calcola il moltiplicatore medio basato sui penaltyMultiplier delle difficoltà
        double averageMultiplier = habits.stream()
                .mapToDouble(habit -> habit.getDifficulty().getPenaltyMultiplier())
                .average()
                .orElse(1.0); // Default a MEDIUM (1.0) se non ci sono abitudini

        return averageMultiplier;
    }

    /**
     * Calculate the life points earned by completing habits on a specific date
     */

    private int calculateCompletedHabitsPoints(List<Habit> allUserHabits, LocalDate completedDate) {
        List<Habit> completedHabits = allUserHabits.stream()
                .filter(habit -> habit.getLastCompletedDate() != null &&
                        habit.getLastCompletedDate().equals(completedDate))
                .toList();


        return calculator.compute(completedHabits, allUserHabits);
    }


    // Checks if user should level up based on total XP and updates their level
    // Returns new level if leveled up, 0 if no level change occurred
    public int checkUpdateUserLevel(User user) {
        int newLevel = calculateLevel(user.getTotalXp());
        if (newLevel > user.getLevel()) {
            user.setLevel(newLevel);
            return newLevel;
        }
        return 0;
    }

    // Calculates user level based on total XP using the standard XP per level formula
    // Level 1 starts at 0 XP, higher level requires 100 more XP
    private int calculateLevel(double totalXp) {
        return (int) Math.floor(totalXp / XP_PER_LEVEL) + 1;
    }

    // Checks if user's life points dropped to zero and handles level decrease
    // Resets life points to 50 when level decreases, prevents going below level 1
    public boolean checkAndHandleLevelDecrease(User user) {
        if (user.getLifePoints() <= 0) {
            int currentLevel = user.getLevel();
            if (currentLevel > 1) {
                user.setLevel(currentLevel - 1);
                // Reset lifePoints to 50 after level decrease
                user.addLifePoints(50 - user.getLifePoints());
                return true;
            }
        }
        return false;
    }

}