package ingsoftware.controller;

import ingsoftware.exception.BusinessException;
import ingsoftware.model.User;
import ingsoftware.service.UserService;
import ingsoftware.service.events.HabitCompletionEvent;
import ingsoftware.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class UserStatsController {

    private static final Logger logger = LoggerFactory.getLogger(UserStatsController.class);

    // Dependencies
    private final UserService userService;

    // Constructor injection
    public UserStatsController(UserService userService) {
        this.userService = userService;
    }

    // Componenti FXML
    @FXML private Label levelLabel;
    @FXML private ProgressBar experienceBar;
    @FXML private Label experienceLabel;
    @FXML private ProgressBar healthBar;
    @FXML private Label healthLabel;

    private Long currentUserId;

    @FXML
    public void initialize() {
        // Inizializzazione vuota, aspetta setCurrentUser
    }

    public void setCurrentUser(Long userId) {
        this.currentUserId = userId;
        updateUserStats();
    }

    @EventListener(HabitCompletionEvent.class)
    public void handleHabitCompletionEvent() {
        Platform.runLater(
                this::updateUserStats);
    // Aggiorna automaticamente quando riceve un evento di modifica delle statistiche utente
    }



    public void updateUserStats() {
        if (currentUserId == null) {
            logger.warn("Tentativo di aggiornare le statistiche senza un utente corrente impostato");
            return;
        }

        try {

            User user = userService.findUserOrThrow(currentUserId);
            updateUIWithUserData(user);

        } catch (BusinessException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore imprevisto durante l'aggiornamento delle statistiche per l'utente {}", currentUserId, e);
            showErrorMessage("Impossibile caricare le statistiche dell'utente. Riprova più tardi.");
        }
    }
    
    private void updateUIWithUserData(User user) {
        // Aggiorna il livello
        levelLabel.setText(String.valueOf(user.getLevel()));

        // Aggiorna i punti vita
        int lifePoints = user.getLifePoints();
        int maxLifePoints = 100; // Valore massimo dei punti vita
        healthBar.setProgress((double) lifePoints / maxLifePoints);
        healthLabel.setText(lifePoints + "/" + maxLifePoints);

        // Aggiorna l'esperienza
        double xp = user.getTotalXp();
        double xpForNextLevel = calculateXpForNextLevel(user.getLevel());
        experienceBar.setProgress(xp / xpForNextLevel);
        experienceLabel.setText((int)xp + "/" + (int)xpForNextLevel);
    }

    // Metodo per calcolare l'esperienza necessaria per il prossimo livello
    private double calculateXpForNextLevel(int currentLevel) {
        return 100 * currentLevel;
    }
    
    // Metodi per mostrare messaggi all'utente
    private void showErrorMessage(String message) {
        AlertHelper.showErrorAlert(message);
    }
}

