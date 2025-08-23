package ingsoftware.service.strategy;

import ingsoftware.model.User;
import ingsoftware.service.EventService;

public class EventBonusStrategy implements GamificationStrategy {
    private final GamificationStrategy baseStrategy;
    private final EventService eventService;
    private final double eventMultiplier;

    public EventBonusStrategy(GamificationStrategy baseStrategy, EventService eventService) {
        this.baseStrategy = baseStrategy;
        this.eventService = eventService;
        this.eventMultiplier = 2.0; // Inizializza il moltiplicatore
    }

    @Override
    public double calculateExperience(double baseExperience, User user) {
        double baseResult = baseStrategy.calculateExperience(baseExperience, user);
        return baseResult * eventMultiplier;
    }
}
