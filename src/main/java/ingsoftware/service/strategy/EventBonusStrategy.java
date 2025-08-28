package ingsoftware.service.strategy;

import ingsoftware.model.User;
import ingsoftware.service.EventService;

public class EventBonusStrategy implements GamificationStrategy {
    private final GamificationStrategy baseStrategy;
    private final EventService eventService;

    public EventBonusStrategy(GamificationStrategy baseStrategy, EventService eventService) {
        this.baseStrategy = baseStrategy;
        this.eventService = eventService;
    }

    @Override
    public double calculateExperience(double baseExperience, User user) {
        double baseResult = baseStrategy.calculateExperience(baseExperience, user);
        double eventMultiplier = eventService.getEventMultiplier();
        return baseResult * eventMultiplier;
    }
}
