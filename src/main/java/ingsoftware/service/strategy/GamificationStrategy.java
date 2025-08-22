package ingsoftware.service.strategy;

import ingsoftware.model.User;

public interface GamificationStrategy {
    double calculateExperience(double baseExperience, User user);
}
