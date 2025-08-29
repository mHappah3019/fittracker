package ingsoftware.model.DTO;

public class LifePointsDTO {
    private final boolean isLevelDecreased;
    private final int newlifePoints;
    private final int oldLifePoints;

    public LifePointsDTO(boolean isLevelDecreased, int newlifePoints, int oldLifePoints) {
        this.isLevelDecreased = isLevelDecreased;
        this.newlifePoints = newlifePoints;
        this.oldLifePoints = oldLifePoints;
    }

    public boolean isLevelDecreased() {
        return isLevelDecreased;
    }

    public int getNewlifePoints() {
        return newlifePoints;
    }

    public int getOldLifePoints() {
        return oldLifePoints;
    }

    public int getNewLifePoints() {
        return newlifePoints;
    }
}
