package ingsoftware.controller;

import ingsoftware.service.HabitService;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Controller;

@Controller
@FxmlView("/ingsoftware/HabitCreateView.fxml") // <-- Collegato alla sua vista specifica
public class CreateHabitController extends AbstractHabitFormController {
    
    // Constructor injection
    public CreateHabitController(HabitService habitService) {
        super(habitService);
    }
}
