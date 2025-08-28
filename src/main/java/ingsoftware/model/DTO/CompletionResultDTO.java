// src/main/java/ingsoftware/model/CompletionResult.java
package ingsoftware.model.DTO;

import ingsoftware.model.HabitCompletion;

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


    public int getStreak() {
        return completion.getStreak();
    }

}
