package ingsoftware.service;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    private static final boolean EVENT_BONUS_ACTIVE = true;
    private static final double DEFAULT_EVENT_MULTIPLIER = 2.0;

    // Checks if special event bonuses are currently active
    // Used to determine if XP multipliers should be applied
    public boolean isEventBonusActive() {
        return EVENT_BONUS_ACTIVE;
    }

    // Returns the current event multiplier for XP calculations
    // Returns default multiplier if event is active, otherwise 1.0 (no bonus)
    public double getEventMultiplier() {
        return isEventBonusActive() ? DEFAULT_EVENT_MULTIPLIER : 1.0;
    }
}