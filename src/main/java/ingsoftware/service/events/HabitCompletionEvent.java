package ingsoftware.service.events;

import org.springframework.context.ApplicationEvent;

public class HabitCompletionEvent extends ApplicationEvent {

    public HabitCompletionEvent(Object source) {
        super(source);
    }

}
