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

/**
 * Controller for managing user equipment selection and configuration.
 * Handles the equipment view where users can equip/unequip different types of gear.
 */
@Controller
@FxmlView("/ingsoftware/EquipmentView.fxml")
public class EquipmentController {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);

    // Service dependencies
    private final EquipmentService equipmentService;

    // UI components
    @FXML 
    private VBox equipmentContainer;

    // Constructor injection
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    private final Map<EquipmentType, EquipmentRowManager> equipmentRows = new HashMap<>();
    private Long currentUserId;


    @FXML
    private void initialize() {
        logger.debug("EquipmentController initialized");
    }

    /**
     * Sets the current user ID and initializes the equipment view.
     */
    public void setCurrentUserId(Long userId) {
        this.currentUserId = userId;
        initializeView();
    }

    /**
     * Initializes the equipment view by loading all equipment types and creating UI rows.
     * This method populates the equipment container with selectable equipment options.
     */
    @FXML
    private void initializeView() {
        try {
            Map<EquipmentType, ObservableList<Equipment>> equipmentByType = 
                equipmentService.getAllEquipmentGroupedByType();

            // Use the custom order defined in the enum
            for (EquipmentType type : EquipmentType.getOrderedValues()) {
                if (equipmentByType.containsKey(type)) {
                    createEquipmentRow(type, equipmentByType.get(type));
                }
            }

        } catch (EquipmentNotFoundException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error during equipment controller initialization", e);
            showErrorMessage("Error loading equipment. Please try again later.");
        }
    }

    /**
     * Creates and adds an equipment row for the specified type.
     */
    private void createEquipmentRow(EquipmentType type, ObservableList<Equipment> equipmentList) {
        EquipmentRowManager rowManager = new EquipmentRowManager(type, equipmentList);
        equipmentRows.put(type, rowManager);
        equipmentContainer.getChildren().add(rowManager.getNode());
        
        // Load the currently equipped item for this type
        loadCurrentEquipment(rowManager, type);
    }

    /**
     * Sets the row manager's selection to the user's current equipment or keeps the default.
     */
    private void loadCurrentEquipment(EquipmentRowManager rowManager, EquipmentType type) {
        try {
            Optional<Equipment> currentEquipment = equipmentService.findEquippedByUserIdAndType(currentUserId, type);
            logger.debug("Loading current equipment for type: {} - User ID: {}", type, currentUserId);
            
            Equipment selectedEquipment = currentEquipment.orElse(rowManager.getSelectedEquipment());
            rowManager.setSelectedEquipment(selectedEquipment);
            
        } catch (EquipmentNotFoundException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error loading current equipment for type {}: {}", type, e.getMessage(), e);
        }
    }

    /**
     *  Processes each equipment type and either equips or unequips items based on user selection.
     *  Called when the user clicks 'Save'.
     */
    @FXML
    private void onSaveEquipment() {
        if (currentUserId == null) {
            logger.warn("Attempted to save equipment without a current user set");
            showWarningMessage("No user selected. Please log in again.");
            return;
        }

        try {
            processEquipmentSelections();
            showSuccessMessage("Equipment saved successfully!");
            close();

        } catch (EquipmentNotFoundException e) {
            showErrorMessage(e.getMessage());
        } catch (Exception e) {
            logger.error("Error saving equipment for user {}", currentUserId, e);
            showErrorMessage(e.getMessage());
        }
    }

    /**
     * Processes all equipment selections and applies equip/unequip operations.
     */
    private void processEquipmentSelections() {
        equipmentRows.forEach((type, rowManager) -> {
            Equipment selectedEquipment = rowManager.getSelectedEquipment();
            
            if (selectedEquipment != null) {
                if (selectedEquipment.isNoneOption()) {
                    // User selected "None" - unequip current equipment of this type
                    equipmentService.unequip(currentUserId, type);
                } else {
                    // User selected actual equipment - equip it
                    equipmentService.equip(currentUserId, selectedEquipment.getId());
                }
            }
        });
    }

    /**
     * Handles the cancel button click - closes the dialog without saving.
     */
    @FXML
    public void onCancel() {
        close();
    }

    /**
     * Closes the equipment dialog window.
     */
    @FXML
    public void close() {
        Stage stage = (Stage) equipmentContainer.getScene().getWindow();
        stage.close();
    }

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
