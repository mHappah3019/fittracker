package ingsoftware.controller;

import ingsoftware.controller.strictly_view.EquipmentRowManager;
import ingsoftware.exception.EquipmentNotFoundException;
import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import ingsoftware.service.EquipmentService;
import ingsoftware.util.AlertHelper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import net.rgielen.fxweaver.core.FxmlView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@FxmlView("/ingsoftware/EquipmentView.fxml")
public class EquipmentController {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);

    // Dependencies
    private final EquipmentService equipmentService;

    // Constructor injection
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @FXML private VBox equipmentContainer;

    private final Map<EquipmentType, EquipmentRowManager> equipmentRows = new HashMap<>();

    private Long currentUserId;

    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
    }

    @FXML
    private void initialize() {
        try {
            Map<EquipmentType, ObservableList<Equipment>> byType = equipmentService.getAllEquipmentGroupedByType();
            equipmentService.refreshCache();

            byType.forEach((type, list) -> {
                EquipmentRowManager rowManager = new EquipmentRowManager(type, list);
                equipmentRows.put(type, rowManager);
                equipmentContainer.getChildren().add(rowManager.getNode());

                // Carica l'equipaggiamento corrente
                loadCurrentEquipment(rowManager, type);
            });

        } catch (EquipmentNotFoundException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore durante l'inizializzazione del controller equipaggiamento", e);
            showErrorMessage("Errore durante il caricamento dell'equipaggiamento. Riprova più tardi.");
        }
    }

    /**
     * Carica l'equipaggiamento corrente per un determinato tipo.
     *
     * @param rowManager Il manager della riga di equipaggiamento
     * @param type Il tipo di equipaggiamento
     */
    private void loadCurrentEquipment(EquipmentRowManager rowManager, EquipmentType type) {
        try {
            Optional<Equipment> current = equipmentService.findEquippedByUserAndType(currentUserId, type);
            rowManager.setSelectedEquipment(current.orElse(rowManager.getSelectedEquipment()));
        } catch (EquipmentNotFoundException e) {
            showErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void onSaveEquipment() {
        if (currentUserId == null) {
            logger.warn("Tentativo di salvare equipaggiamento senza un utente corrente impostato");
            showWarningMessage("Nessun utente selezionato. Effettua nuovamente il login.");
            return;
        }

        try {
            equipmentRows.forEach((type, rowManager) -> {
                Equipment selected = rowManager.getSelectedEquipment();
                // Se l'utente ha selezionato "Nessuno", disattiva l'equipaggiamento corrente di quel tipo
                if (selected != null && selected.isNoneOption()) {
                    // Chiama il servizio per disequipaggiare l'equipaggiamento corrente di quel tipo
                    equipmentService.unequip(currentUserId, type);

                } else if (selected != null && !selected.isNoneOption()) {
                    equipmentService.equip(currentUserId, selected.getId());
                }
            });

            equipmentService.refreshCache();
            showSuccessMessage("Equipaggiamento salvato con successo!");
            close();

        } catch (EquipmentNotFoundException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore durante il salvataggio dell'equipaggiamento per l'utente {}", currentUserId, e);
            showErrorMessage("Errore durante il salvataggio dell'equipaggiamento. Riprova più tardi.");
        }
    }

    @FXML
    public void onCancel() {
        close();
    }

    @FXML
    public void close() {
        Stage stage = (Stage) equipmentContainer.getScene().getWindow();
        stage.close();
    }

    // Metodi per mostrare messaggi all'utente
    private void showSuccessMessage(String message) {
        AlertHelper.showSuccessAlert(message);
    }

    private void showErrorMessage(String message) {
        AlertHelper.showErrorAlert(message);
    }

    private void showWarningMessage(String message) {
        AlertHelper.showWarningAlert(message);
    }
}
