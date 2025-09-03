package ingsoftware.service;


import ingsoftware.dao.HabitDAO;
import ingsoftware.exception.*;
import ingsoftware.model.Habit;
import ingsoftware.model.builder.HabitBuilder;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HabitService {
    
    private static final Logger logger = LoggerFactory.getLogger(HabitService.class);

    private final HabitDAO habitDAO;

    public HabitService(HabitDAO habitDAO) {
        this.habitDAO = habitDAO;
    }


    // Creates a new habit using the builder pattern with duplicate name validation
    // Throws DuplicateHabitException if a habit with the same name already exists for the user
    @Transactional
    public Habit createHabit(HabitBuilder builder) throws BusinessException {
        Habit habit = builder.build();
        logger.debug("Creazione abitudine: nome={}, userId={}", habit.getName(), habit.getUserId());

        // Controllo duplicati per nome utente
        Long userId = habit.getUserId();
        String name = habit.getName();
        
        logger.debug("Verifica duplicati: userId={}, name={}", userId, name);
        
        Optional<Habit> existing = habitDAO.findByUserIdAndName(userId, name);
        if (existing.isPresent()) {
            logger.warn("Tentativo di creare un'abitudine duplicata: nome={}, userId={}", name, userId);
            throw new DuplicateHabitException("Abitudine con questo nome già esistente");
        }

        Habit savedHabit = habitDAO.save(habit);
        logger.info("Abitudine creata con successo: id={}, nome={}", 
                savedHabit != null ? savedHabit.getId() : "null", 
                savedHabit != null ? savedHabit.getName() : "null");
        return savedHabit;
    }

    // Updates an existing habit using HabitBuilder with duplicate name checking
    // Validates that the new name doesn't conflict with other user habits
    @Transactional
    public Habit updateHabit(Long habitId, HabitBuilder builder) throws BusinessException {
        logger.debug("Aggiornamento abitudine: id={}", habitId);

        // 1. Retrieve the existing habit
        Habit existingHabit = findHabitOrThrow(habitId);

        // 2. Create a temporary habit with the new values
        Habit formData = builder.withId(habitId).build();

        // 3. Check for duplicate names
        if (!existingHabit.getName().equals(formData.getName())) {
            // Existing duplicate name check code...
            logger.debug("Nome abitudine cambiato da '{}' a '{}'", existingHabit.getName(), formData.getName());

            // Controlla se esiste già un'abitudine con il nuovo nome
            Long userId = formData.getUserId();
            String newName = formData.getName();

            logger.debug("Verifica duplicati per aggiornamento: userId={}, newName={}", userId, newName);

            Optional<Habit> duplicateHabit = habitDAO.findByUserIdAndName(userId, newName);

            if (duplicateHabit.isPresent() && !duplicateHabit.get().getId().equals(habitId)) {
                // Se un'abitudine duplicata è stata trovata E il suo ID è diverso dall'abitudine che si sta aggiornando,
                // allora è un conflitto di duplicazione reale.
                logger.warn("Tentativo di aggiornare un'abitudine con un nome duplicato: id={}, nome={}",
                        habitId, newName);
                throw new DuplicateHabitException("Abitudine con questo nome già esistente.");
            }
        }

        // 4. Update only the fields that should be changed
        existingHabit.setName(formData.getName());
        existingHabit.setDescription(formData.getDescription());
        existingHabit.setDifficulty(formData.getDifficulty());
        existingHabit.setFrequency(formData.getFrequency());
        existingHabit.setUpdatedAt(LocalDateTime.now());

        // 5. Save the updated entity
        return habitDAO.save(existingHabit);
    }

    // Retrieves all habits belonging to a specific user
    // Returns empty list if user has no habits
    public List<Habit> findAllByUserId(Long id) {
        return habitDAO.findAllByUserId(id);
    }
    
    // Finds a habit by ID or throws HabitNotFoundException if not found
    // Handle exception handling in controller layer
    public Habit findHabitOrThrow(Long habitId) {
        return habitDAO.findById(habitId)
                .orElseThrow(() -> new HabitNotFoundException(habitId));
    }
    
    // Persists an existing habit to the database
    // Used for updating habit state like completion dates and streaks
    public Habit saveHabit(Habit habit) {
        return habitDAO.save(habit);
    }

    // Permanently removes a habit from the database by ID
    public void deleteHabit(Long id) {
        habitDAO.deleteById(id);
    }
}


