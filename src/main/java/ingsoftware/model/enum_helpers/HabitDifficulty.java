package ingsoftware.model.enum_helpers;

public enum HabitDifficulty {
    EASY(10, 1.2),
    MEDIUM(15, 1.0),
    HARD(20, 0.8);

    private final int baseXP;
    private final double penaltyMultiplier;

    HabitDifficulty(int baseXP, double penaltyMultiplier) {
        this.baseXP = baseXP;
        this.penaltyMultiplier = penaltyMultiplier;
    }

    public int getBaseXP() {
        return baseXP;
    }

    public double getPenaltyMultiplier() {
        return penaltyMultiplier;
    }
}