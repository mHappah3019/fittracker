// Java
package ingsoftware.service.jobs;

import ingsoftware.dao.UserDAO;
import ingsoftware.service.mediator.StartupMediatorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MidnightRolloverJob {

    private static final int PAGE_SIZE = 500;
    private static final Logger log = LoggerFactory.getLogger(MidnightRolloverJob.class);

    private final UserDAO userDAO;
    private final StartupMediatorImpl startupMediator;

    public MidnightRolloverJob(UserDAO userDAO, StartupMediatorImpl startupMediator) {
        this.userDAO = userDAO;
        this.startupMediator = startupMediator;
    }


    // Esegue ogni giorno a mezzanotte, fuso orario Italia
    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Rome")
    public void runDailyRollover() {
        processAllActiveUsers();
    }

    private void processAllActiveUsers() {
        int offset = 0;
        while (true) {
            List<Long> ids = userDAO.findAllActiveUserIds(offset, PAGE_SIZE);
            if (ids.isEmpty()) break;

            for (Long userId : ids) {
                try {
                    // Deve essere idempotente: aggiorna solo se è davvero il primo accesso del giorno
                    startupMediator.handleApplicationStartup(userId);
                } catch (Exception ex) {
                    log.info("Error during application startup for user {}: {}", userId, ex.getMessage());
                }
            }
            offset += PAGE_SIZE;
        }
    }
}