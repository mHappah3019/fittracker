package ingsoftware.service;

import ingsoftware.dao.UserDAO;
import ingsoftware.exception.UserNotFoundException;
import ingsoftware.model.User;
import ingsoftware.service.startup_handlers.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final DataLoaderService dataLoaderService;

    @Autowired
    public UserService(UserDAO userDAO, DataLoaderService dataLoaderService) {
        this.userDAO = userDAO;
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
        userDAO.save(user);
    }

    public User findUserOrThrow(Long userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public boolean checkDefaultUser() {
        return userDAO.findAll().isEmpty();
    }

    public boolean existsById(Long id) {
        return userDAO.existsById(id);
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