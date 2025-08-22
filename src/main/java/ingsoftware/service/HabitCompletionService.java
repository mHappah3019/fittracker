package ingsoftware.service;

import ingsoftware.exception.*;
import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.model.Habit;
import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;
import ingsoftware.model.builder.HabitCompletionBuilder;
import ingsoftware.repository.HabitCompletionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class HabitCompletionService {

    @Autowired
    private HabitService habitService;
    @Autowired
    private HabitCompletionRepository completionRepository;
    @Autowired
    private GamificationService gamificationService;
    @Autowired
    private UserService userService;




    /**
     * Implementazione effettiva del completamento dell'abitudine.
     * Questo metodo è privato e viene chiamato da completeHabit che gestisce le eccezioni.
     */
    public CompletionResultDTO completeHabit(Long habitId, Long userId) throws BusinessException {
        LocalDate today = LocalDate.now();

        Habit habit = habitService.findHabitOrThrow(habitId);
        User  user  = userService.findUserOrThrow(userId);

        verifyNotAlreadyCompletedToday(user, habit, today);

        int streak = updateStreak(habit, today);

        HabitCompletion completion = new HabitCompletionBuilder()
                .withUser(user)
                .withHabit(habit)
                .withCompletionDate(today)
                .withStreak(streak)
                .build();

        HabitCompletion savedCompletion = completionRepository.save(completion);

        double xpGained = calculateAndUpdateXP(habit, user);
        int newLevel = calculateAndUpdateLevel(user);

        return new CompletionResultDTO(savedCompletion, xpGained, newLevel);
    }

    /* ---------- METODI DI SUPPORTO ---------- */

    private int calculateAndUpdateLevel(User user) {
        try {

            int newLevel = gamificationService.checkUpdateUserLevel(user);
            userService.saveUser(user);

            return newLevel;
        } catch (Exception e) {
            throw e;
        }
    }

    private double calculateAndUpdateXP(Habit habit, User user) {
        try {
            double xpGained = gamificationService.calculateHabitXP(habit, user);

            user.addTotalXp(xpGained);
            userService.saveUser(user);

            return xpGained;
        } catch (Exception e) {
            throw e;
        }
    }


    private void verifyNotAlreadyCompletedToday(User user, Habit habit, LocalDate today) {
        boolean alreadyCompleted = completionRepository.existsByUserAndHabitAndCompletionDate(user, habit, today);
        if (alreadyCompleted) {
            throw new HabitAlreadyCompletedException("Abitudine già completata oggi");
        }
    }

    private int updateStreak(Habit habit, LocalDate today) {
        try {
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
        } catch (Exception e) {
            throw e;
        }
    }
}
