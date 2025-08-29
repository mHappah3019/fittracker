package ingsoftware.model.enum_helpers;

public enum HabitDifficulty {
    EASY( 10.0, 1.2),
    MEDIUM(15.0, 1.0),
    HARD(20.0, 0.8);

    private final double baseXP;
    private final double penaltyMultiplier;

    HabitDifficulty(double baseXP, double penaltyMultiplier) {
        this.baseXP = baseXP;
        this.penaltyMultiplier = penaltyMultiplier;
    }

    public double getBaseXP() {
        return baseXP;
    }

    public double getPenaltyMultiplier() {
        return penaltyMultiplier;
    }
}