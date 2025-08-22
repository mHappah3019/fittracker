package ingsoftware.service.post_completion;


import ingsoftware.config.PopupConfig;
import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.service.PopupUIService;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;


@Service
public class CompletionPopupUIService implements PopupUIService {

    @Autowired
    private PopupConfig config;

    @Override
    public void showPopup(CompletionResultDTO result) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    public void showCompletionPopup(CompletionResultDTO completion) {
        Platform.runLater(() -> {
            // Il servizio popup interpreta autonomamente i dati
            double gainedXP = completion.getGainedXP();
            int newLevel = completion.getNewLevelAchieved();
            boolean streakTriggered = config.getStreakMilestones().contains(completion.getStreak());
            int streakDays = completion.getStreak();

            Stage popup = buildStage(gainedXP, newLevel, streakTriggered, streakDays);
            animateAndShow(popup);
        });

    }

    private Stage buildStage(double xp,
                             int newLevel,
                             boolean streak,
                             int days) {

        VBox root = new VBox(12);
        root.setPadding(new Insets(18));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("popup-root");

        // Esperienza (sempre presente)
        Label xpLabel = new Label("+" + xp + " XP");
        xpLabel.getStyleClass().add("xp-label");
        root.getChildren().add(xpLabel);

        // Level-up opzionale
        if (newLevel > 0 && newLevel != 1) {
            Label lvl = new Label("Level Up! \u2192 " + newLevel); // Simbolo freccia unicode
            lvl.getStyleClass().add("level-label");
            root.getChildren().add(lvl);
        }

        // Streak opzionale
        if (streak) {
            Label streakLbl = new Label("\uD83D\uDD25 Streak: " + days + " giorni"); // Emoji fuoco unicode
            streakLbl.getStyleClass().add("streak-label");
            root.getChildren().add(streakLbl);
        }

        Scene scene = new Scene(root);
        // Assumendo che popup.css si trovi nella cartella 'resources/css'
        java.net.URL cssResource = getClass().getResource("/css/popup.css");
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
        } else {
            // System.err.println("Errore: Il file CSS 'popup.css' non è stato trovato nel classpath. Il popup potrebbe non essere stilizzato correttamente.");
            // Potresti anche considerare di loggare l'errore utilizzando un framework di logging come SLF4J/Logback
            Logger.getLogger(CompletionPopupUIService.class.getName()).log(Level.WARNING, "Il file CSS 'popup.css' non è stato trovato.");
        }

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.initModality(Modality.NONE);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);


        // Posiziona nell’angolo in basso a destra
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX(screen.getMaxX() - 320); // Regola per la posizione desiderata
        stage.setY(screen.getMaxY() - 180); // Regola per la posizione desiderata

        System.out.println("CompletionPopupUIService: buildStage completato, stage creato");
        return stage;
    }

    private void animateAndShow(Stage stage) {
        System.out.println("CompletionPopupUIService: animateAndShow iniziato");
        FadeTransition fadeIn = new FadeTransition(config.getFadeIn(), stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition hold = new PauseTransition(config.getDisplay());

        FadeTransition fadeOut = new FadeTransition(config.getFadeOut(), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> stage.close());

        SequentialTransition seq = new SequentialTransition(fadeIn, hold, fadeOut);
        stage.show();
        seq.play();

        System.out.println("CompletionPopupUIService: animateAndShow completato (animazioni in corso)");
    }
}
