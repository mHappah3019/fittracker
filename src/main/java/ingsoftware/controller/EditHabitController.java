package ingsoftware.controller;

import ingsoftware.exception.BusinessException;
import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.service.HabitService;
import ingsoftware.util.AlertHelper;
import javafx.fxml.FXML;
import net.rgielen.fxweaver.core.FxmlView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@FxmlView("/ingsoftware/HabitEditView.fxml")
public class EditHabitController extends AbstractHabitFormController {

    private static final Logger logger = LoggerFactory.getLogger(EditHabitController.class);

    // Constructor injection
    public EditHabitController(HabitService habitService) {
        super(habitService);
    }

    // Override of AbstractHabitFormController
    @FXML
    public void initialize() {
        super.initialize();
    }

    // Override method called when the user clicks on the Save button.
    // Calls createHabitBuilderFromForm() to get a HabitBuilder from the form fields,
    // then calls habitService.createHabit() to "update" the habit and save it in the database.
    @Override
    protected void handleSave() {
        try {

            HabitBuilder formBuilder = createHabitBuilderFromForm();
            habitService.updateHabit(habitToEdit.getId(), formBuilder);

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


    // Override method called when the user clicks on the Delete button.
    @FXML
    protected void handleDelete() {
        if (habitToEdit == null) {
            logger.warn("Tentativo di eliminare un'abitudine null");
            showFormError("Nessuna abitudine selezionata per l'eliminazione");
            return;
        }
        
        // Asks for confirmation
        boolean confirmed = AlertHelper.showConfirmationAlert(
            "Conferma eliminazione", 
            "Sei sicuro di voler eliminare l'abitudine '" + habitToEdit.getName() + "'?\nQuesta azione non può essere annullata."
        );
        
        if (!confirmed) {
            return;
        }
        
        try {
            logger.info("Eliminazione abitudine con ID: {}", habitToEdit.getId());
            habitService.deleteHabit(habitToEdit.getId());

            // Esegui il callback per aggiornare la lista
            if (onSaveCallback != null) {
                onSaveCallback.run();
            }

            AlertHelper.showSuccessAlert("Abitudine eliminata con successo!");
            closeWindow();

        } catch (BusinessException e) {
            showFormError(e.getMessage());
        } catch (Exception e) {
            logger.error("Errore durante l'eliminazione dell'abitudine con ID: {}", habitToEdit.getId(), e);
            showFormError("Errore durante l'eliminazione dell'abitudine. Riprova più tardi.");
        }
    }
}