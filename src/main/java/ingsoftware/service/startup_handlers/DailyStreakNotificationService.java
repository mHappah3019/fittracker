package ingsoftware.service.startup_handlers;

import ingsoftware.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Handler that notifies users about their habit streaks on first access of the day.
 * This is another example of how the Mediator pattern allows for easy extension.
 */
@Component
public class DailyStreakNotificationService {

    public DailyStreakNotificationService() {
    }

    public void onFirstAccessOfDay(User user, LocalDate previousAccessDate) {
        // In a real implementation, this could:
        // - Send a push notification
        // - Award bonus points or badges
        // - Provide encouragement to maintain the streak
    }
}