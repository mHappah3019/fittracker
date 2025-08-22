package ingsoftware.service.strategy;

import ingsoftware.model.User;
import ingsoftware.service.EventService;

public class EventBonusStrategy implements GamificationStrategy {
    private final GamificationStrategy baseStrategy;
    private final EventService eventService;
    private double eventMultiplier;

    public EventBonusStrategy(GamificationStrategy baseStrategy) {
        this.baseStrategy = baseStrategy;
        this.eventService = null;
        this.eventMultiplier = 2.0;
    }

    public EventBonusStrategy(GamificationStrategy baseStrategy, EventService eventService) {
        this.baseStrategy = baseStrategy;
        this.eventService = eventService;
    }

    @Override
    public double calculateExperience(double baseExperience, User user) {
        double baseResult = baseStrategy.calculateExperience(baseExperience, user);
        return baseResult * eventMultiplier;
    }
}
