package ingsoftware.service.strategy;

import ingsoftware.model.User;

public class ExponentialExperienceStrategy implements GamificationStrategy {
    private final GamificationStrategy baseStrategy;

    public ExponentialExperienceStrategy(GamificationStrategy baseStrategy) {
        this.baseStrategy = baseStrategy;
    }


    @Override
    public double calculateExperience(double baseExperience, User user) {
        double baseResult = baseStrategy.calculateExperience(baseExperience, user);
        int userLevel = user.getLevel();

        // Esperienza esponenziale basata sul livello
        double exponentialFactor = Math.pow(1.1, userLevel);
        return baseResult * exponentialFactor;
    }
}
