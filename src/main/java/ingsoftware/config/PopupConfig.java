package ingsoftware.config;


import javafx.util.Duration;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PopupConfig {
    private final Duration fadeIn  = Duration.millis(250);
    private final Duration display = Duration.seconds(2.5);
    private final Duration fadeOut = Duration.millis(350);

    private final List<Integer> streakMilestones = List.of(7, 15, 30);

    // Getter se necessario
    public Duration getFadeIn() {
        return fadeIn;
    }

    public Duration getDisplay() {
        return display;
    }

    public Duration getFadeOut() {
        return fadeOut;
    }

    public List<Integer> getStreakMilestones() {
        return streakMilestones;
    }
}