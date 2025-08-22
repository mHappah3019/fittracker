package ingsoftware.controller;

import ingsoftware.exception.BusinessException;
import ingsoftware.model.Habit;
import ingsoftware.model.HabitDifficulty;
import ingsoftware.model.HabitFrequencyType;
import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.service.HabitService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// NOTA: Questa classe è astratta e non ha l'annotazione @Controller o @FxmlView
public abstract class AbstractHabitFormController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHabitFormController.class);

    // --- Dependencies ---
    protected final HabitService habitService;

    // Constructor injection
    protected AbstractHabitFormController(HabitService habitService) {
        this.habitService = habitService;
    }

    // --- Componenti FXML Condivisi ---
    @FXML protected Label titleLabel;
    @FXML protected TextField nameField;
    @FXML protected TextArea descriptionArea;
    @FXML protected ComboBox<HabitDifficulty> difficultyComboBox;
    @FXML protected ComboBox<HabitFrequencyType> frequencyComboBox;
    @FXML protected Button saveButton;
    @FXML protected Label errorLabel;

    // --- Stato Interno ---
    protected Long currentUserId;
    protected Habit habitToEdit;
    protected Runnable onSaveCallback;

    @FXML
    public void initialize() {
        difficultyComboBox.getItems().setAll(HabitDifficulty.values());
        frequencyComboBox.getItems().setAll(HabitFrequencyType.values());
        errorLabel.setVisible(false);
    }

    public void setData(Long userId, Habit habit, Runnable onSaveCallback) {
        this.currentUserId = userId;
        this.habitToEdit = habit;
        this.onSaveCallback = onSaveCallback;

        if (habitToEdit != null) {
            titleLabel.setText("Modifica Abitudine");
            populateForm();
        } else {
            titleLabel.setText("Crea Nuova Abitudine");
        }
    }


    @FXML
    protected void handleSave() {
        try {

            HabitBuilder formBuilder = createHabitBuilderFromForm();
            habitService.saveHabit(habitToEdit != null ? habitToEdit.getId() : null, formBuilder);

            if (onSaveCallback != null) {
                onSaveCallback.run();
            }
            closeWindow();

        } catch (BusinessException e) {
            showFormError(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore imprevisto durante il salvataggio", e);
            showFormError("Errore imprevisto. Riprova.");
        }
    }

    @FXML
    protected void handleCancel() {
        closeWindow();
    }

    protected void populateForm() {
        nameField.setText(habitToEdit.getName());
        descriptionArea.setText(habitToEdit.getDescription());
        difficultyComboBox.setValue(habitToEdit.getDifficulty());
        frequencyComboBox.setValue(habitToEdit.getFrequency());
    }

    protected HabitBuilder createHabitBuilderFromForm() {
        return new HabitBuilder()
                .withUserId(currentUserId)
                .withName(nameField.getText())
                .withDescription(descriptionArea.getText())
                .withDifficulty(difficultyComboBox.getValue())
                .withFrequency(frequencyComboBox.getValue());
    }

    protected void showFormError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    protected void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}