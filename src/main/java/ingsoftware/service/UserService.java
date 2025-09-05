package ingsoftware.service;

import ingsoftware.dao.UserDAO;
import ingsoftware.exception.UserNotFoundException;
import ingsoftware.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class UserService {

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // Checks if this is the user's first access of the day by comparing last access date
    // Returns true for first-ever access or if last access was before the given date
    public boolean isFirstAccessOfDay(User user, LocalDate date) {
        LocalDate lastAccess = user.getLastAccessDate();
        if (lastAccess == null) {
            return true; // Primo accesso in assoluto
        }
        return lastAccess.isBefore(date);
    }

    // Persists user changes to the database within a transaction
    // Ensures data consistency when updating user information
    @Transactional
    public void saveUser(User user) {
        userDAO.save(user);
    }

    // Retrieves a user by ID or throws UserNotFoundException if not found
    // Handle exception in controller layer
    public User findUserOrThrow(Long userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // Checks if the database has no users (empty state)
    // Used to determine if default user setup is needed
    public boolean checkDefaultUser() {
        return userDAO.findAll().isEmpty();
    }

    @Transactional
    public void resetAutoIncrement() {
        userDAO.resetAutoIncrement();
    }
}