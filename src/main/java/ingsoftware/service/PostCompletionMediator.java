package ingsoftware.service;

import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.service.post_completion.*;
import javafx.application.Platform;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PostCompletionMediator {

    //– Dipendenze opzionali (iniettate via costruttore o setter)
    private final CompletionPopupUIService popupUISvc;

    private final AchievementService achievementSvc;
    private final DailySummaryService summarySvc;
    private final AnalyticsLoggerService analyticsSvc;

    public PostCompletionMediator(
            CompletionPopupUIService popupUI,
            AchievementService achievementSvc,
            DailySummaryService summarySvc,
            AnalyticsLoggerService analyticsSvc) {
        this.popupUISvc = popupUI;
        this.achievementSvc = achievementSvc;
        this.summarySvc = summarySvc;
        this.analyticsSvc = analyticsSvc;
    }

    /** Metodo unico chiamato dal controller dopo la business-logic */
    public void handlePostCompletion(CompletionResultDTO completion) {
        // 1. Aggiornamenti UI immediati (non bloccanti)
        Platform.runLater(() -> {
            try {
                popupUISvc.showCompletionPopup(completion);
            } catch(Exception e) {
                System.err.println("Errore aggiornamento UI: " + e.getMessage());
            }
        });

        // 2. Business logic asincrona raggruppata
        CompletableFuture.runAsync(() -> {
            try {
                // Esegui tutti i servizi in sequenza o parallelo interno
                achievementSvc.checkForNewBadges(completion);
                summarySvc.updateSummary(completion);
                analyticsSvc.logEvent("HabitCompleted", completion);
            } catch(Exception e) {
                System.err.println("Errore nei servizi post-completion: " + e.getMessage());
            }
        });
    }

}

