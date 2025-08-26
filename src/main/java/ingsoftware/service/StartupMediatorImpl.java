package ingsoftware.service;

import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.model.User;
import ingsoftware.service.mediator.StartupMediator;
import ingsoftware.service.startup_handlers.DailySummaryService;
import ingsoftware.service.startup_handlers.StartupPopupUIService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class StartupMediatorImpl implements StartupMediator {
    
    private final GamificationService gamificationService;
    private final UserService userService;
    private final StartupPopupUIService popupUIService;
    private final DailySummaryService dailySummaryService;

    public StartupMediatorImpl(GamificationService gamificationService,
                               UserService userService,
                               StartupPopupUIService popupUIService, DailySummaryService dailySummaryService) {
        this.gamificationService = gamificationService;
        this.userService = userService;
        this.popupUIService = popupUIService;
        this.dailySummaryService = dailySummaryService;
    }

    public void handleApplicationStartup(Long userId) {
        User user = userService.findUserOrThrow(userId);

        //TODO:

        if (userService.isFirstAccessOfDay(user, LocalDate.now())) {
            LifePointsDTO result = updatesForNewDay(user);
            popupUIService.showfirstAccessPopup(result);
            dailySummaryService.onFirstAccessOfDay(user, user.getLastAccessDate());
        }

        LifePointsDTO result = new LifePointsDTO(false, 20, 30);
        popupUIService.showfirstAccessPopup(result);

        dailySummaryService.onAccess(user, LocalDate.now());
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