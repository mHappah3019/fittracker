package ingsoftware.service;

import ingsoftware.dao.HabitCompletionDAO;
import ingsoftware.exception.*;
import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.model.Habit;
import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;
import ingsoftware.model.builder.HabitCompletionBuilder;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class HabitCompletionService {

    private final HabitService habitService;
    private final HabitCompletionDAO completionDAO;
    private final GamificationService gamificationService;
    private final UserService userService;

    public HabitCompletionService(HabitService habitService, HabitCompletionDAO completionDAO, GamificationService gamificationService, UserService userService) {
        this.habitService = habitService;
        this.completionDAO = completionDAO;
        this.gamificationService = gamificationService;
        this.userService = userService;
    }

    // Processes habit completion including streak updates, XP calculation, and level progression
    // Validates completion isn't duplicate and returns result data for Post-completion logic
    @Transactional
    public CompletionResultDTO completeHabit(Long habitId, Long userId) throws BusinessException {
        LocalDate today = LocalDate.now();

        Habit habit = habitService.findHabitOrThrow(habitId);
        User  user  = userService.findUserOrThrow(userId);

        verifyNotAlreadyCompletedToday(userId, habitId, today);

        int streak = updateStreak(habit, today);

        HabitCompletion completion = new HabitCompletionBuilder()
                .withUserId(userId)
                .withHabitId(habitId)
                .withCompletionDate(today)
                .withStreak(streak)
                .build();

        HabitCompletion savedCompletion = completionDAO.save(completion);

        double xpGained = calculateAndUpdateXP(habit, user);
        int newLevel = calculateAndUpdateLevel(user);

        return new CompletionResultDTO(savedCompletion, xpGained, newLevel);
    }

    /* ---------- SUPPORT METHODS ---------- */

    // Calculates if user leveled up and persists the level change
    // Returns new level achieved or 0 if no level up occurred
    private int calculateAndUpdateLevel(User user) {
        int newLevel = gamificationService.checkUpdateUserLevel(user);
        userService.saveUser(user);
        return newLevel;
    }

    // Calculates XP gained from habit completion and adds it to user's total
    // Persists the XP change and returns the amount gained
    private double calculateAndUpdateXP(Habit habit, User user) {
        double xpGained = gamificationService.calculateHabitXP(habit, user);
        user.addTotalXp(xpGained);
        userService.saveUser(user);
        return xpGained;
    }

    // Validates that the habit hasn't already been completed today
    // Throws HabitAlreadyCompletedException if duplicate completion is attempted
    private void verifyNotAlreadyCompletedToday(Long userId, Long habitId, LocalDate today) {
        boolean alreadyCompleted = completionDAO.existsByUserIdAndHabitIdAndCompletionDate(userId, habitId, today);
        if (alreadyCompleted) {
            throw new HabitAlreadyCompletedException("Abitudine già completata oggi");
        }
    }

    // Updates habit's streak counter based on completion frequency and timing
    // Resets streak to 1 if too much time has passed since last completion
    private int updateStreak(Habit habit, LocalDate today) {
        LocalDate lastCompleted = habit.getLastCompletedDate();
        int newStreak = habit.getCurrentStreak();

        if (lastCompleted == null) {
            newStreak = 1;
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastCompleted, today);

            switch (habit.getFrequency()) {
                case DAILY   -> newStreak = (daysBetween == 1) ? newStreak + 1 : 1;
                //case WEEKLY  -> newStreak = (daysBetween >= 7 && daysBetween <= 14) ? newStreak + 1 : 1;
                //case MONTHLY -> newStreak = (daysBetween >= 28 && daysBetween <= 35) ? newStreak + 1 : 1;
            }
        }

        habit.setCurrentStreak(newStreak);
        habit.setLastCompletedDate(today);
        habitService.saveHabit(habit);
        return newStreak;
    }
}
