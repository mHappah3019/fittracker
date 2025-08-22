package ingsoftware.service.startup_handlers;

import ingsoftware.model.Habit;
import ingsoftware.model.User;
import ingsoftware.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handler that notifies users about their habit streaks on first access of the day.
 * This is another example of how the Mediator pattern allows for easy extension.
 */
@Component
public class DailyStreakNotificationHandler {

    private final HabitRepository habitRepository;
    
    @Autowired
    public DailyStreakNotificationHandler(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
        // Register this handler with the mediator
        //mediator.registerStartupHandler(this);
    }

    public void onFirstAccessOfDay(User user, LocalDate previousAccessDate) {
        // Get all habits for the user
        List<Habit> userHabits = habitRepository.findAllByUserId(user.getId());
        
        // Find habits with significant streaks (e.g., 7+ days)
        List<Habit> significantStreakHabits = userHabits.stream()
                .filter(habit -> habit.getCurrentStreak() >= 7)
                .collect(Collectors.toList());
        
        if (!significantStreakHabits.isEmpty()) {
            System.out.println("Streak Notification for user: " + user.getId());
            System.out.println("You have " + significantStreakHabits.size() + " habits with significant streaks!");
            
            // Display each significant streak
            for (Habit habit : significantStreakHabits) {
                System.out.println("- " + habit.getName() + ": " + habit.getCurrentStreak() + " day streak!");
                
                // Check if they're close to their target streak
                if (habit.getTargetStreak() != null && 
                    habit.getCurrentStreak() >= habit.getTargetStreak() * 0.8) {
                    System.out.println("  You're close to reaching your target streak of " + 
                                      habit.getTargetStreak() + " days!");
                }
            }
            
            // In a real implementation, this could:
            // - Send a push notification
            // - Display a special UI element
            // - Award bonus points or badges
            // - Provide encouragement to maintain the streak
        }
        
        // Find habits at risk of breaking streaks (not completed yesterday)
        List<Habit> atRiskHabits = userHabits.stream()
                .filter(habit -> habit.getCurrentStreak() > 0)
                .filter(habit -> habit.getLastCompletedDate() == null || 
                               !habit.getLastCompletedDate().equals(previousAccessDate))
                .collect(Collectors.toList());
        
        if (!atRiskHabits.isEmpty()) {
            System.out.println("\nStreak Alert for user: " + user.getId());
            System.out.println("You have " + atRiskHabits.size() + " habits at risk of breaking their streak!");
            
            for (Habit habit : atRiskHabits) {
                System.out.println("- " + habit.getName() + ": " + habit.getCurrentStreak() + 
                                  " day streak at risk! Don't break the chain!");
            }
        }
    }
}