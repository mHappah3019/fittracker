package ingsoftware.service;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    private static final boolean EVENT_BONUS_ACTIVE = true;

    public boolean isEventBonusActive() {
        return EVENT_BONUS_ACTIVE;
    }
}