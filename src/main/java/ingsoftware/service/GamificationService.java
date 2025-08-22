package ingsoftware.service;

import ingsoftware.model.*;
import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.repository.UserRepository;
import ingsoftware.repository.EquipmentRepository;
import ingsoftware.repository.HabitCompletionRepository;
import ingsoftware.service.strategy.ExperienceStrategyFactory;
import ingsoftware.service.strategy.GamificationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class GamificationService {


    @Autowired
    private HabitService habitService;

    @Autowired
    private LifePointCalculator calculator;

    @Autowired
    private ExperienceStrategyFactory strategyFactory;


    private static final double XP_PER_LEVEL = 100.0;
    final int INACTIVITY_PENALTY_PER_DAY = -10;

    public double calculateHabitXP(Habit habit, User user) {
        GamificationStrategy strategy = strategyFactory.createStrategy();

        int baseXP = habit.getDifficulty().getBaseXP();

        return strategy.calculateExperience(baseXP, user);
    }

    public LifePointsDTO updateUserLifePoints(User user, LocalDate today) {
        // Salva i punti vita iniziali
        int oldLifePoints = user.getLifePoints();
        boolean isLevelDecreased = false;
        
        LocalDate lastAccess = user.getLastAccessDate();

        if (lastAccess != null) {
            long daysSinceLastAccess = ChronoUnit.DAYS.between(lastAccess, today);

            processCompletedHabitsForDate(user, lastAccess);

            if (daysSinceLastAccess > 1) {
                long inactiveDays = daysSinceLastAccess - 1;
                int totalPenalty = (int) (inactiveDays * INACTIVITY_PENALTY_PER_DAY);
                user.addLifePoints(totalPenalty);
                isLevelDecreased = checkAndHandleLifePointsDepletion(user);
            }
        }

        // Crea e restituisce il DTO con i valori vecchi e nuovi
        int newLifePoints = user.getLifePoints();
        return new LifePointsDTO(isLevelDecreased, newLifePoints, oldLifePoints);
    }


    /**
     * Calcola e assegna punti per le abitudini che l'utente ha completato in una data specifica.
     */
    private void processCompletedHabitsForDate(User user, LocalDate completedDate) {
        List<Habit> allUserHabits = habitService.findAllByUserId(user.getID());
        List<Habit> completedHabits = allUserHabits.stream()
                .filter(habit -> habit.getLastCompletedDate() != null &&
                        habit.getLastCompletedDate().equals(completedDate))
                .toList();

        if (!completedHabits.isEmpty()) {
            int delta = calculator.compute(completedHabits, 75.0);
            user.addLifePoints(delta);
            checkAndHandleLifePointsDepletion(user);
        }
    }


    public int checkUpdateUserLevel(User user) {
        int newLevel = calculateLevel(user.getTotalXp());
        if (newLevel > user.getLevel()) {
            user.setLevel(newLevel);
            return newLevel;
        }
        return 0;
    }


    private int calculateLevel(double totalXp) {
        return (int) Math.floor(totalXp / XP_PER_LEVEL) + 1;
    }

    /**
     * Checks if user's lifePoints are <= 0 and decreases level if necessary.
     * When a user loses a level, lifePoints are reset to 50.
     * 
     * @param user The user to check
     * @return true if level was decreased, false otherwise
     */

    public boolean checkAndHandleLifePointsDepletion(User user) {
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