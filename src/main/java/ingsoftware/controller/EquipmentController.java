package ingsoftware.controller;

import ingsoftware.exception.DatabaseException;
import ingsoftware.exception.EquipmentNotFoundException;
import ingsoftware.exception.HabitNotFoundException;
import ingsoftware.model.Equipment;
import ingsoftware.model.EquipmentType;
import ingsoftware.service.EquipmentService;
import ingsoftware.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
    
    @FXML
    private VBox equipmentContainer;
    @FXML private Button saveButton;

    private final Map<EquipmentType, EquipmentRow> equipmentRows = new HashMap<>();

    private Long currentUserId; // Aggiungere campo per l'utente corrente

    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
    }

    @FXML
    private void initialize() {
        try {
            Map<EquipmentType, ObservableList<Equipment>> byType = equipmentService.getAllEquipmentGroupedByType();
            equipmentService.refreshCache();

            byType.forEach((type, list) -> {
                EquipmentRow row = new EquipmentRow(type, list);
                equipmentRows.put(type, row);
                equipmentContainer.getChildren().add(row.getNode());
            });

        } catch (HabitNotFoundException e) {
            showErrorMessage(e.getMessage());
        } catch (DatabaseException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore durante l'inizializzazione del controller equipaggiamento", e);
            showErrorMessage("Errore durante il caricamento dell'equipaggiamento. Riprova più tardi.");
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
            equipmentRows.forEach((type, row) -> {
                Equipment selected = row.getSelectedEquipment();
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


//TODO: refactor?
    private class EquipmentRow {
        private final HBox container;
        private final ChoiceBox<Equipment> choiceBox;
        private final Label slotLabel;
        private final ImageView preview;
        private final EquipmentType type;

        public EquipmentRow(EquipmentType type, ObservableList<Equipment> items) {
            this.type = type;
            this.slotLabel = new Label(type.getDisplayName());
            this.choiceBox = createChoiceBox(items);
            this.preview = new ImageView();
            this.container = new HBox(10, slotLabel, choiceBox, preview);

            //setupChoiceBox();
            loadCurrentEquipment();
            styleComponents();
        }

        private ChoiceBox<Equipment> createChoiceBox(ObservableList<Equipment> items) {
            ObservableList<Equipment> allItems = FXCollections.observableArrayList();
            allItems.addAll(items);

            ChoiceBox<Equipment> cb = new ChoiceBox<>(allItems);
            //cb.setConverter(new EquipmentStringConverter());
            return cb;
        }

        private void setupChoiceBox() {
            choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
                updatePreview(selected);
            });
        }


        private void loadCurrentEquipment() {
            try {
                Optional<Equipment> current = equipmentService.findEquippedByUserAndType(currentUserId, type);
                choiceBox.setValue(current.orElse(choiceBox.getItems().get(0)));
            } catch (EquipmentNotFoundException e) {
                showErrorMessage(e.getMessage());
            }
        }

        private void updatePreview(Equipment equipment) {
            if (equipment != null && !equipment.isNoneOption() && equipment.getIconPath() != null) {
                preview.setImage(new Image(equipment.getIconPath()));
            } else {
                preview.setImage(null);
            }
        }

        private void styleComponents() {
            slotLabel.setMinWidth(80);
            choiceBox.setMinWidth(200);
            preview.setFitWidth(48);
            preview.setFitHeight(48);
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new Insets(5));
        }

        public Node getNode() { return container; }
        public Equipment getSelectedEquipment() { return choiceBox.getValue(); }
    }
}

