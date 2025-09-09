package ingsoftware.controller;

import ingsoftware.exception.BusinessException;
import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.service.HabitService;
import net.rgielen.fxweaver.core.FxmlView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@FxmlView("/ingsoftware/HabitCreateView.fxml")
public class CreateHabitController extends AbstractHabitFormController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractHabitFormController.class);

    // Constructor injection
    public CreateHabitController(HabitService habitService) {
        super(habitService);
    }

    // Override method called when the user clicks on the Save button.
    // Calls createHabitBuilderFromForm() to get a HabitBuilder from the form fields,
    // then calls habitService.createHabit() to create the habit and save it in the database.
    @Override
    protected void handleSave() {
        try {

            HabitBuilder formBuilder = createHabitBuilderFromForm();
            habitService.createHabit(formBuilder);

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
}
