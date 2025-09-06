package ingsoftware.service;

import ingsoftware.config.PopupConfig;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.stage.Stage;


public abstract class PopupUIService {
    public PopupConfig config;

    public PopupUIService(PopupConfig config){
        this.config=config;
    }

    // Displays a popup with fade-in, hold, and fade-out animations
    // Uses configuration settings for timing and automatically closes when done
    public void animateAndShow(Stage stage) {

        FadeTransition fadeIn = new FadeTransition(config.getFadeIn(), stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition hold = new PauseTransition(config.getDisplay()); // Show longer for first access

        FadeTransition fadeOut = new FadeTransition(config.getFadeOut(), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(_ -> stage.close());

        SequentialTransition seq = new SequentialTransition(fadeIn, hold, fadeOut);
        stage.show();
        seq.play();
    }
}
