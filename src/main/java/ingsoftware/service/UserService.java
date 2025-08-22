package ingsoftware.service;

import ingsoftware.exception.UserNotFoundException;
import ingsoftware.model.Habit;
import ingsoftware.model.User;
import ingsoftware.repository.HabitRepository;
import ingsoftware.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Ho aggiunto HabitRepository e LifePointCalculator al costruttore
    // in quanto sono utilizzati nei metodi ma non erano presenti.
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}