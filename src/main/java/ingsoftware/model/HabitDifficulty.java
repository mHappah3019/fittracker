package ingsoftware.model;

public enum HabitDifficulty {
    EASY(10),
    MEDIUM(15),
    HARD(20);

    private final int baseXP;

    HabitDifficulty(int baseXP) {
        this.baseXP = baseXP;
    }

    public int getBaseXP() {
        return baseXP;
    }
}