package ingsoftware.controller;

import ingsoftware.model.User;
import ingsoftware.service.EquipmentService;
import ingsoftware.service.mediator.StartupMediatorImpl;
import ingsoftware.service.UserService;
import ingsoftware.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@FxmlView("/ingsoftware/MainDashboardView.fxml")
public class MainDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(MainDashboardController.class);

    // Dependencies
    private final FxWeaver fxWeaver;
    private final UserService userService;
    private final EquipmentService equipmentService;
    private final StartupMediatorImpl startupMediator;

    // Constructor injection
    public MainDashboardController(StartupMediatorImpl startupMediator, UserService userService, EquipmentService equipmentService, FxWeaver fxWeaver) {
        this.startupMediator = startupMediator;
        this.userService = userService;
        this.equipmentService = equipmentService;
        this.fxWeaver = fxWeaver;
    }

    public VBox userStats;
    public BorderPane habitList;

    @FXML
    private UserStatsController userStatsController;

    @FXML
    private HabitListController habitListController;

    private Long currentUserId;

    @FXML
    public void initialize() {
        logger.info("🔧 Inizializzazione MainDashboardController...");
        // L'inizializzazione vera e propria avverrà in setCurrentUser
        // quando l'ID utente sarà disponibile
    }

    public void setCurrentUser(Long userId) {
        logger.info("👤 Impostazione utente corrente: {}", userId);
        this.currentUserId = userId;
        
        // Verifica e crea l'utente di default se necessario
        checkAndCreateDefaultUser(userId);
        
        // Imposta l'utente nei controller figli se sono disponibili
        if (userStatsController != null) {
            userStatsController.setCurrentUser(userId);
        } else {
            logger.warn("⚠️ userStatsController non ancora inizializzato");
        }
        
        if (habitListController != null) {
            habitListController.setCurrentUser(userId);
        } else {
            logger.warn("⚠️ habitListController non ancora inizializzato");
        }
        
        // Avvia il mediator di startup
        Platform.runLater(() -> {
            try {
                startupMediator.handleApplicationStartup(userId);
                logger.info("✅ Startup mediator eseguito con successo");
            } catch (Exception e) {
                logger.error("❌ Errore durante l'esecuzione del startup mediator", e);
            }
        });
    }

    @FXML
    private void handleOpenEquipment() {
        try {
            Parent root = fxWeaver.loadView(EquipmentController.class);
            
            // Ottieni l'istanza del controller
            EquipmentController controller = fxWeaver.getBean(EquipmentController.class);
            
            // Imposta l'utente corrente nel controller dell'equipaggiamento
            controller.setCurrentUserId(currentUserId);
            
            Stage stage = new Stage();
            stage.setTitle("⚔️ Equipaggiamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch (Exception e) {
            logger.error("Errore durante l'apertura della finestra dell'equipaggiamento", e);
            showErrorMessage(e.getMessage());
        }
    }

    // Metodi per mostrare messaggi all'utente
    private void showErrorMessage(String message) {
        AlertHelper.showErrorAlert(message);
    }


    private void checkAndCreateDefaultUser(Long userId) {
        try {
            if (userService.checkDefaultUser()) {
                logger.info("🆕 Creazione utente di default con ID: {}", userId);
                User defaultUser = new User();
                defaultUser.setLevel(1);
                defaultUser.setId(userId);
                userService.saveUser(defaultUser);
                equipmentService.initializeUserEquipment(userId);
                logger.info("✅ Utente di default creato con successo");
            } else {
                logger.info("👤 Utente esistente trovato");
            }
        } catch (Exception e) {
            logger.error("❌ Errore durante la verifica/creazione dell'utente di default", e);
            showErrorMessage("Errore durante l'inizializzazione dell'utente: " + e.getMessage());
        }
    }
}

