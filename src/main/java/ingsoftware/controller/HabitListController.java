package ingsoftware.controller;

import ingsoftware.controller.strictly_view.HabitListViewManager;
import ingsoftware.exception.BusinessException;
import ingsoftware.model.DTO.CompletionResultDTO;
import ingsoftware.model.Habit;
import ingsoftware.service.*;
import ingsoftware.service.events.HabitCompletionEvent;
import ingsoftware.service.mediator.PostCompletionMediatorImpl;
import ingsoftware.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HabitListController {

    private static final Logger logger = LoggerFactory.getLogger(HabitListController.class);

    // Dependencies
    private final ApplicationEventPublisher eventPublisher;
    private final HabitCompletionService habitCompletionService;
    private final HabitService habitService;
    private final PostCompletionMediatorImpl postMediator;
    private final FxWeaver fxWeaver;

    // Constructor injection
    public HabitListController(ApplicationEventPublisher eventPublisher,
                              HabitCompletionService habitCompletionService,
                              HabitService habitService,
                              PostCompletionMediatorImpl postMediator,
                              FxWeaver fxWeaver) {
        this.eventPublisher = eventPublisher;
        this.habitCompletionService = habitCompletionService;
        this.habitService = habitService;
        this.postMediator = postMediator;
        this.fxWeaver = fxWeaver;
    }

    // Componenti FXML
    @FXML private ListView<Habit> habitListView;
    @FXML private Button completeHabitButton;
    @FXML private Button editHabitButton;

    private Long currentUserId;
    private HabitListViewManager listViewManager;


    @FXML
    public void initialize() {
        // Configurazione iniziale della vista
        listViewManager = new HabitListViewManager(habitListView, editHabitButton, completeHabitButton);

        listViewManager.setupDoubleClickHandler(this::handleCompleteHabit);

        //refreshHabitList();
    }


    // --- GESTIONE AZIONI UTENTE ---

    @FXML
    private void handleAddHabit() {
        // Lancia la finestra di creazione, passando un "callback" per l'aggiornamento
        openHabitWindow(CreateHabitController.class, null, this::refreshHabitList);
    }
    
    @FXML
    private void handleEditHabit() {
        Habit selected = listViewManager.getSelectedHabit();
        if (selected == null) {
            showWarningMessage("Seleziona un'abitudine da modificare.");
            return;
        }
        // Lancia la finestra di modifica. La logica di eliminazione, se esiste,
        // dovrà trovarsi nel controller di questa nuova finestra.
        openHabitWindow(EditHabitController.class, selected, this::refreshHabitList);
    }


    @FXML
    private void handleCompleteHabit() {
        Habit selected = listViewManager.getSelectedHabit();
        if (selected == null) {
            showWarningMessage("Seleziona un'abitudine da completare.");
            return;
        }

        completeHabitButton.setDisable(true);

        try {
            // Esegue il completamento dell'abitudine
            CompletionResultDTO completion = habitCompletionService.completeHabit(selected.getId(), currentUserId);
            
            // Aggiorna UI con i dati freschi
            refreshHabitList();
            
            // Elaborazioni aggiuntive (rewards, statistiche, etc.)
            postMediator.handlePostCompletion(completion);
            
            // Notifica altri componenti
            eventPublisher.publishEvent(new HabitCompletionEvent(this));
            
        } catch (BusinessException e) {
            showErrorMessage("Errore nel completamento: " + e.getMessage());
            logger.warn("Errore business durante completamento abitudine: {}", e.getMessage());
        } catch (Exception e) {
            showErrorMessage("Si è verificato un errore imprevisto durante il completamento dell'abitudine.");
            logger.error("Errore imprevisto durante completamento abitudine", e);
        } finally {
            // Riabilita sempre il pulsante
            completeHabitButton.setDisable(false);
        }
    }
    
    // --- METODI DI SUPPORTO E UI ---


    private void refreshHabitList() {
        try {
            List<Habit> habits = habitService.findAllByUserId(currentUserId);
            listViewManager.updateHabitList(habits);
        } catch (Exception e) {
            logger.error("Errore durante il caricamento delle abitudini per l'utente {}", currentUserId, e);
            showErrorMessage("Impossibile caricare le abitudini. Riprova più tardi.");
        }
    }


    //Metodo generico che accetta la CLASSE del controller da caricare
    private <T extends AbstractHabitFormController> void openHabitWindow(Class<T> controllerClass, Habit habit, Runnable onSaveCallback) {
        try {
            Parent root = fxWeaver.loadView(controllerClass);

            // Ottieni l'istanza del bean specifico (Create o Edit)
            T controller = fxWeaver.getBean(controllerClass);
            controller.setData(currentUserId, habit, onSaveCallback);

            Stage stage = new Stage();
            stage.setTitle(habit == null ? "Crea Nuova Abitudine" : "Modifica Abitudine");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            logger.error("Errore durante l'apertura della finestra del form per {}", controllerClass.getSimpleName(), e);
            showErrorMessage("Impossibile aprire la finestra. Riprova più tardi.");
        }
    }

    public void setCurrentUser(long l) {
        this.currentUserId = l;
        refreshHabitList();
    }
    
    // Metodi per mostrare messaggi all'utente
    private void showErrorMessage(String message) {
        AlertHelper.showErrorAlert(message);
    }
    
    private void showWarningMessage(String message) {
        AlertHelper.showWarningAlert(message);
    }
}