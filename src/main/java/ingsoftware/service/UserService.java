package ingsoftware.service;

import ingsoftware.exception.UserNotFoundException;
import ingsoftware.model.User;
import ingsoftware.repository.UserRepository;
import ingsoftware.service.startup_handlers.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DataLoaderService dataLoaderService;

    @Autowired
    public UserService(UserRepository userRepository, DataLoaderService dataLoaderService) {
        this.userRepository = userRepository;
        this.dataLoaderService = dataLoaderService;
    }

    public boolean isFirstAccessOfDay(User user, LocalDate date) {
        LocalDate lastAccess = user.getLastAccessDate();
        if (lastAccess == null) {
            return true; // Primo accesso in assoluto
        }
        return lastAccess.isBefore(date);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public boolean checkDefaultUser() {
        return userRepository.count() == 0;
    }

    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }
    
    /**
     * Ottiene l'utente di default (il primo utente nel database)
     */
    public User getDefaultUser() {
        return dataLoaderService.getDefaultUser();
    }
    
    /**
     * Ottiene l'ID dell'utente di default
     */
    public Long getDefaultUserId() {
        return dataLoaderService.getDefaultUserId();
    }
}