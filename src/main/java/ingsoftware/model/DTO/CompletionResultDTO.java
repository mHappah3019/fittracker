// src/main/java/ingsoftware/model/CompletionResult.java
package ingsoftware.model.DTO;

import ingsoftware.model.HabitCompletion;

/**
 * DTO for representing the result of completing a habit.
 *
 * This DTO encapsulates information about a completed habit,
 * including details such as the completion itself and any associated rewards or achievements.
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

    public Long getHabitId() {
        return completion.getHabitId();
    }
}
