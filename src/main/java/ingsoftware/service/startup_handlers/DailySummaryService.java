package ingsoftware.service.startup_handlers;


import ingsoftware.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Example handler that generates a daily summary for the user on their first access of the day.
 * This demonstrates how the Mediator pattern allows for easy extension of functionality.
 */
@Component
public class DailySummaryService {


    public DailySummaryService() {}

    public void onFirstAccessOfDay(User user, LocalDate previousAccessDate) {
        // In a real implementation, this could:
        // - Update a dashboard with statistics
        // - Generate achievement badges
    }

    public void onAccess(User user, LocalDate now) {}
}