package ingsoftware.service.startup_handlers;

import ingsoftware.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public DailyStreakNotificationService() {

    }

    /**
     * Called when user accesses the application.
     * Checks for streak milestones.
     */
    public void onAccess(User user, LocalDate previousAccessDate) {
        // TODO: implementare logica che mostra non quanto è lunga la streak attuale, ma il countdown
        //  alla fine della targetStreak (se presente).
    }
}