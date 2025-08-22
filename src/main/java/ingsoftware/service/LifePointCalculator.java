package ingsoftware.service;

import ingsoftware.model.Habit;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LifePointCalculator {
    
    public int compute(List<Habit> habits, double thresholdPercent) {
        return compute(habits, thresholdPercent, 10, -5);
    }
    
    public int compute(List<Habit> habits, 
                       double thresholdPercent,
                       int bonus,
                       int malus) {
        
        if (habits.isEmpty()) {
            return 0; // nessun cambiamento se non ci sono abitudini
        }
        
        long completed = habits.stream()
                              .filter(Habit::isCompletedToday)
                              .count();
        
        double completionRate = (completed * 100.0) / habits.size();
        
        return completionRate >= thresholdPercent ? bonus : malus;
    }
}