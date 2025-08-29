package ingsoftware.service;

import ingsoftware.model.Habit;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class LifePointCalculator {

    private static final int BONUS = 10;
    private static final int MALUS = -5;
    private static final double thresholdPercent = 70.0;


    // Metodo completo con tutti i parametri
    public int compute(List<Habit> completedHabits,
                       List<Habit> allUserHabits)
    {

        if (allUserHabits.isEmpty()) {
            return 0; // nessun cambiamento se non ci sono abitudini
        }

        double completionRate = (completedHabits.size() * 100.0) / allUserHabits.size();

        return completionRate >= thresholdPercent ? BONUS : MALUS;
    }
}
