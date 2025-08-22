package ingsoftware.service.strategy;

import ingsoftware.model.User;

public class BaseGamificationStrategy implements GamificationStrategy {
    @Override
    public double calculateExperience(double baseExperience, User user) {
        return baseExperience;
    }
}
