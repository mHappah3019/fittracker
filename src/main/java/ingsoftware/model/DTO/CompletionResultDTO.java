// src/main/java/ingsoftware/model/CompletionResult.java
package ingsoftware.model.DTO;

import ingsoftware.model.HabitCompletion;
import ingsoftware.model.User;


//TODO: dovrei mantenere questo DTO o inserire tutte le informazioni in HabitCompletion e quindi utilizzare HabitCompletionBuilder in maniera più estesa?
/**
 * Contiene l'HabitCompletion e altri risultati immediati dell'operazione di completamento.
 */
public class CompletionResultDTO {
    private final HabitCompletion completion;
    private final int newLevelAchieved;
    private final double xpGained;
    // Potrebbe includere anche List<Badge> unlockedBadges, ecc.

    public CompletionResultDTO(HabitCompletion completion, double xpGained, int newLevelAchieved) {
        this.completion = completion;
        this.xpGained = xpGained;
        this.newLevelAchieved = newLevelAchieved;
    }

    public HabitCompletion getCompletion() {
        return completion;
    }

    public int getNewLevelAchieved() {
        return newLevelAchieved;
    }

    public double getGainedXP() {
        return xpGained;
    }

    public User getUser() {
        return null;
    }

    public int getStreak() {
        return completion.getStreak();
    }

    public int getLevel() {
        return 0;
    }
}
