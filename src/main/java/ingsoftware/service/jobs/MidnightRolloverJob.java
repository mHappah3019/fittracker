// Java
package ingsoftware.service.jobs;

import ingsoftware.repository.UserRepository;
import ingsoftware.service.StartupMediator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MidnightRolloverJob {

    private static final int PAGE_SIZE = 500;

    private final UserRepository userRepository;
    private final StartupMediator startupMediator;

    public MidnightRolloverJob(UserRepository userRepository, StartupMediator startupMediator) {
        this.userRepository = userRepository;
        this.startupMediator = startupMediator;
    }


    // Esegue ogni giorno a mezzanotte, fuso orario Italia
    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Rome")
    public void runDailyRollover() {
        processAllActiveUsers();
    }

    private void processAllActiveUsers() {
        int page = 0;
        while (true) {
            Pageable pageable = PageRequest.of(page, PAGE_SIZE);
            List<Long> ids = userRepository.findAllActiveUserIds(pageable);
            if (ids.isEmpty()) break;

            for (Long userId : ids) {
                try {
                    // Deve essere idempotente: aggiorna solo se è davvero il primo accesso del giorno
                    startupMediator.handleApplicationStartup(userId);
                } catch (Exception ex) {
                    System.out.println("Error during application startup for user " + userId + ": " + ex.getMessage());
                }
            }
            page++;
        }
    }
}