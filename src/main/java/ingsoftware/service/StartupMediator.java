package ingsoftware.service;

import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.model.Habit;
import ingsoftware.model.User;
import ingsoftware.repository.HabitRepository;
import ingsoftware.repository.UserRepository;
import ingsoftware.service.startup_handlers.StartupPopupUIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class StartupMediator {
    
    private final GamificationService gamificationService;
    private final UserService userService;
    private final StartupPopupUIService popupUIService;

    public StartupMediator(GamificationService gamificationService, 
                          UserService userService, 
                          StartupPopupUIService popupUIService) {
        this.gamificationService = gamificationService;
        this.userService = userService;
        this.popupUIService = popupUIService;
    }

    public void handleApplicationStartup(Long userId) {
        User user = userService.findUserOrThrow(userId);

        if (userService.isFirstAccessOfDay(user, LocalDate.now())) {
            LifePointsDTO result = updatesForNewDay(user);
            popupUIService.showfirstAccessPopup(result);
        }
    }


    private LifePointsDTO updatesForNewDay(User user) {
        // Update lifePoints based on habit completion and inactivity
        LifePointsDTO result = gamificationService.updateUserLifePoints(user, LocalDate.now());

        // Update lastAccessDate to current date
        user.setLastAccessDate(LocalDate.now());

        // Persist the updated user
        userService.saveUser(user);

        return result;
    }


}