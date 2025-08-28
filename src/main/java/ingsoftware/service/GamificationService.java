package ingsoftware.service;

import ingsoftware.model.*;
import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.service.strategy.ExperienceStrategyFactory;
import ingsoftware.service.strategy.GamificationStrategy;
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

    public double calculateHabitXP(Habit habit, User user) {
        GamificationStrategy strategy = strategyFactory.createStrategy();

        int baseXP = habit.getDifficulty().getBaseXP();

        return strategy.calculateExperience(baseXP, user);
    }

    public LifePointsDTO updateUserLifePoints(User user, LocalDate today) {
        // Salva i punti vita iniziali
        int oldLifePoints = user.getLifePoints();
        int totalLifePointsDelta = 0;
        boolean isLevelDecreased = false;
        
        LocalDate lastAccess = user.getLastAccessDate();

        // Recupera tutte le abitudini dell'utente una sola volta
        List<Habit> allUserHabits = habitService.findAllByUserId(user.getId());

        if (lastAccess != null) {
            long daysSinceLastAccess = ChronoUnit.DAYS.between(lastAccess, today);

            // Calcola delta per abitudini completate
            totalLifePointsDelta += calculateCompletedHabitsPoints(allUserHabits, lastAccess);

            // Calcola penalità per inattività
            if (daysSinceLastAccess > 1) {
                long inactiveDays = daysSinceLastAccess - 1;
                int inactivityPenalty = (int) (inactiveDays * INACTIVITY_PENALTY_PER_DAY);
                totalLifePointsDelta += inactivityPenalty;
            }
        }

        // Applica il moltiplicatore basato sulla difficoltà media delle abitudini
        if (totalLifePointsDelta != 0) {
            double difficultyMultiplier = calculateDifficultyMultiplier(allUserHabits);
            totalLifePointsDelta = (int) Math.round(totalLifePointsDelta * difficultyMultiplier);
        }

        // Applica tutti i cambiamenti insieme
        if (totalLifePointsDelta != 0) {
            user.addLifePoints(totalLifePointsDelta);
            isLevelDecreased = checkAndHandleLifePointsDepletion(user);
        }

        // Crea e restituisce il DTO con i valori vecchi e nuovi
        int newLifePoints = user.getLifePoints();
        return new LifePointsDTO(isLevelDecreased, newLifePoints, oldLifePoints);
    }


    /**
     * Calcola un moltiplicatore basato sulla difficoltà media delle abitudini dell'utente.
     * Utilizza il penaltyMultiplier di ogni difficoltà per calcolare un moltiplicatore medio.
     * 
     * @param habits Lista delle abitudini dell'utente
     * @return Il moltiplicatore da applicare ai punti vita
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
     * Calcola i punti per le abitudini che l'utente ha completato in una data specifica.
     * Non modifica direttamente l'utente, restituisce solo il delta dei punti.
     * 
     * @param allUserHabits Lista di tutte le abitudini dell'utente
     * @param completedDate La data per cui calcolare i punti delle abitudini completate
     * @return Il delta dei punti vita da aggiungere (può essere positivo o negativo)
     */

    private int calculateCompletedHabitsPoints(List<Habit> allUserHabits, LocalDate completedDate) {
        List<Habit> completedHabits = allUserHabits.stream()
                .filter(habit -> habit.getLastCompletedDate() != null &&
                        habit.getLastCompletedDate().equals(completedDate))
                .toList();

        if (completedHabits.isEmpty()) {
            return INACTIVITY_PENALTY_PER_DAY;
        }

        return calculator.compute(completedHabits, 75.0);
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