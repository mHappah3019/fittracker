package ingsoftware.service;

import ingsoftware.model.DTO.LifePointsDTO;
import ingsoftware.model.User;
import ingsoftware.service.mediator.StartupMediator;
import ingsoftware.service.startup_handlers.DailyStreakNotificationService;
import ingsoftware.service.startup_handlers.DailySummaryService;
import ingsoftware.service.startup_handlers.StartupPopupUIService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class StartupMediatorImpl implements StartupMediator {

    private static final Logger logger = LoggerFactory.getLogger(PostCompletionMediatorImpl.class);

    private final GamificationService gamificationService;
    private final UserService userService;
    private final StartupPopupUIService popupUIService;
    private final DailySummaryService dailySummaryService;
    private final DailyStreakNotificationService dailyStreakNotificationService;

    public StartupMediatorImpl(GamificationService gamificationService,
                               UserService userService,
                               StartupPopupUIService popupUIService, DailySummaryService dailySummaryService, DailyStreakNotificationService dailyStreakNotificationService) {
        this.gamificationService = gamificationService;
        this.userService = userService;
        this.popupUIService = popupUIService;
        this.dailySummaryService = dailySummaryService;
        this.dailyStreakNotificationService = dailyStreakNotificationService;
    }

    @Transactional
    public void handleApplicationStartup(Long userId) {
        try {
            User user = userService.findUserOrThrow(userId);

            if (userService.isFirstAccessOfDay(user, LocalDate.now())) {
                LifePointsDTO result = updatesForNewDay(user);
                popupUIService.showfirstAccessPopup(result);
                dailySummaryService.onFirstAccessOfDay(user, user.getLastAccessDate());
            }

            dailyStreakNotificationService.onAccess(user, LocalDate.now());
            dailySummaryService.onAccess(user, LocalDate.now());
        } catch (Exception e) {
            logger.error("Errore durante l'avvio dell'applicazione");
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