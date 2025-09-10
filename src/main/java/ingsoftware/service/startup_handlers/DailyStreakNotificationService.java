package ingsoftware.service.startup_handlers;

import ingsoftware.controller.strictly_view.HabitListViewManager;

import ingsoftware.model.User;
import ingsoftware.service.events.HabitCompletionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Handler that notifies users about their habit streaks.
 * Shows streak milestones and provides encouragement to maintain streaks.
 */
@Component
public class DailyStreakNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(DailyStreakNotificationService.class);
    
    // Milestone streak values that trigger notifications
    private static final int[] STREAK_MILESTONES = {7, 30, 100, 365};

    private final ApplicationEventPublisher eventPublisher;

    public DailyStreakNotificationService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Called when user accesses the application.
     * Checks for streak milestones.
     */
    public void onAccess(User user, LocalDate previousAccessDate) {
        // TODO: implementare logica che mostra non quanto è lunga la streak attuale, ma il countdown
        //  alla fine della targetStreak (se presente).
    }

    /**
     * Aggiorna la visualizzazione di un'abitudine usando solo l'ID.
     * 
     * @param habitId L'ID dell'abitudine da aggiornare
     * @param displayMode Il modo di visualizzazione desiderato
     */
    public void updateHabitDisplay(Long habitId, HabitListViewManager.HabitListCell.DisplayMode displayMode) {
        if (habitId == null) {
            logger.warn("Tentativo di aggiornare visualizzazione con habitId null");
            return;
        }

        logger.debug("Richiesta aggiornamento visualizzazione per habitId: {} con mode: {}", 
                    habitId, displayMode);

        // Pubblica un HabitCompletionEvent che trasporta anche le info per il refresh UI
        HabitCompletionEvent event = new HabitCompletionEvent(this, habitId, displayMode);
        eventPublisher.publishEvent(event);
    }
}