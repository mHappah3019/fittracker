package ingsoftware.service;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    private static final boolean EVENT_BONUS_ACTIVE = true;
    private static final double DEFAULT_EVENT_MULTIPLIER = 2.0;

    public boolean isEventBonusActive() {
        return EVENT_BONUS_ACTIVE;
    }

    /**
     * Restituisce il moltiplicatore dell'evento se attivo, altrimenti 1.0 (nessun bonus)
     * @return il moltiplicatore da applicare all'esperienza
     */
    public double getEventMultiplier() {
        return isEventBonusActive() ? DEFAULT_EVENT_MULTIPLIER : 1.0;
    }
}