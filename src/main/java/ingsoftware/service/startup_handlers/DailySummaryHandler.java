package ingsoftware.service.startup_handlers;

import ingsoftware.model.Habit;
import ingsoftware.model.User;
import ingsoftware.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Example handler that generates a daily summary for the user on their first access of the day.
 * This demonstrates how the Mediator pattern allows for easy extension of functionality.
 */
@Component
public class DailySummaryHandler {

    private final HabitRepository habitRepository;
    
    @Autowired
    public DailySummaryHandler(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
        // Register this handler with the mediator
        //mediator.registerStartupHandler(this);
    }

    public void onFirstAccessOfDay(User user, LocalDate previousAccessDate) {
        // Generate a daily summary for the user
        System.out.println("Generating daily summary for user: " + user.getId());
        
        // Get habits that were due yesterday
        List<Habit> yesterdayHabits = habitRepository.findAllByUserId(user.getId());
        
        // Filter to those that were completed
        long completedCount = yesterdayHabits.stream()
                .filter(habit -> habit.getLastCompletedDate() != null && 
                       habit.getLastCompletedDate().equals(previousAccessDate))
                .count();
        
        // Generate summary statistics
        System.out.println("Daily Summary for " + previousAccessDate);
        System.out.println("Total habits: " + yesterdayHabits.size());
        System.out.println("Completed habits: " + completedCount);
        System.out.println("Completion rate: " + 
                (yesterdayHabits.isEmpty() ? 0 : (completedCount * 100.0 / yesterdayHabits.size())) + "%");
        System.out.println("Current life points: " + user.getLifePoints());
        
        // In a real implementation, this could:
        // - Send a notification to the user
        // - Update a dashboard with statistics
        // - Generate achievement badges
        // - Provide personalized recommendations
        // - etc.
    }
}