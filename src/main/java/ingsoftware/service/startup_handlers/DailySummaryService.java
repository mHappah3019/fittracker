package ingsoftware.service.startup_handlers;


import ingsoftware.model.Habit;
import ingsoftware.model.User;
import ingsoftware.service.HabitService;
import ingsoftware.service.strategy.LifePointCalculator;
import ingsoftware.util.AlertHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Example handler that generates a daily summary for the user on their first access of the day.
 * This demonstrates how the Mediator pattern allows for easy extension of functionality.
 */
@Component
public class DailySummaryService {

    private final HabitService habitService;

    public DailySummaryService(HabitService habitService) {
        this.habitService = habitService;
    }

    public void onFirstAccessOfDay(User user, LocalDate previousAccessDate) {
        // In a real implementation, this could:
        // - Update a dashboard with statistics
        // - Generate achievement badges
    }

    public void onAccess(User user, LocalDate now) {
        checkHabitCompletionProgress(user);
    }

    /**
     * Verifica il progresso di completamento delle abitudini dell'utente per oggi
     * e mostra un avviso se non ha completato almeno il 70% delle abitudini attive.
     */
    private void checkHabitCompletionProgress(User user) {
        List<Habit> userHabits = habitService.findAllByUserId(user.getId());

        // Filtra solo le abitudini attive
        List<Habit> activeHabits = userHabits.stream()
                .filter(Habit::isActive)
                .toList();

        if (activeHabits.isEmpty()) {
            return; // Nessuna abitudine attiva, non c'è nulla da controllare
        }

        // Conta quante abitudini sono state completate oggi
        long completedToday = activeHabits.stream()
                .mapToLong(habit -> habit.isCompletedToday() ? 1 : 0)
                .sum();

        // Calcola la percentuale di completamento
        double completionPercentage = (double) completedToday / activeHabits.size() * 100;

        // Se la percentuale è inferiore al 70% (thresholdPercecnt), mostra un avviso
        if (completionPercentage < LifePointCalculator.thresholdPercent) {
            String message = String.format(
                "Hai completato solo %d su %d abitudini oggi (%.1f%%).\n" +
                "Cerca di completare almeno il 70%% delle tue abitudini per mantenere i tuoi progressi!",
                completedToday, activeHabits.size(), completionPercentage
            );

            AlertHelper.showWarningAlert("Promemoria Abitudini", message);
        }
    }
}