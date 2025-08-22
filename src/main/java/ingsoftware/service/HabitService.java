package ingsoftware.service;


import ingsoftware.exception.*;
import ingsoftware.model.Habit;
import ingsoftware.model.builder.HabitBuilder;
import ingsoftware.repository.HabitRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HabitService {
    
    private static final Logger logger = LoggerFactory.getLogger(HabitService.class);

    @Autowired
    private HabitRepository habitRepository;

    /**
     * Salva un'abitudine (crea nuova o aggiorna esistente)
     * @param habitId ID dell'abitudine da aggiornare, o null per creazione
     * @param builder Builder contenente i dati dell'abitudine
     * @return L'abitudine salvata
     * @throws IllegalArgumentException se i dati non sono validi
     * @throws HabitNotFoundException se l'ID specificato non esiste
     * @throws DuplicateHabitException se esiste già un'abitudine con lo stesso nome per l'utente
     */
    public Habit saveHabit(Long habitId, HabitBuilder builder) throws RuntimeException {
        Habit tempHabit = builder.build(); // Build once to avoid creating multiple instances
        
        if (habitId == null) {
            // CREAZIONE
            logger.info("Tentativo di creazione di una nuova abitudine con nome: {}", tempHabit.getName());
            return createHabit(builder);
        } else {
            // MODIFICA
            logger.info("Tentativo di aggiornamento dell'abitudine con ID: {}", habitId);
            return updateHabit(habitId, builder);
        }
    }


    /**
     * Crea una nuova abitudine
     */
    public Habit createHabit(HabitBuilder builder) throws BusinessException {
        Habit habit = builder.build();
        logger.debug("Creazione abitudine: nome={}, userId={}", habit.getName(), habit.getUserId());

        // Controllo duplicati per nome utente
        Long userId = habit.getUserId();
        String name = habit.getName();
        
        logger.debug("Verifica duplicati: userId={}, name={}", userId, name);
        
        Optional<Habit> existing = habitRepository.findByUserIdAndName(userId, name);
        if (existing.isPresent()) {
            logger.warn("Tentativo di creare un'abitudine duplicata: nome={}, userId={}", name, userId);
            throw new DuplicateHabitException("Abitudine con questo nome già esistente");
        }

        Habit savedHabit = habitRepository.save(habit);
        logger.info("Abitudine creata con successo: id={}, nome={}", 
                savedHabit != null ? savedHabit.getId() : "null", 
                savedHabit != null ? savedHabit.getName() : "null");
        return savedHabit;
    }

    /**
     * Aggiorna un'abitudine esistente utilizzando HabitBuilder
     */
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

            Optional<Habit> duplicateHabit = habitRepository.findByUserIdAndName(userId, newName);

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
        return habitRepository.save(existingHabit);
    }

    public List<Habit> findAllByUserId(Long id) {
        return habitRepository.findAllByUserId(id);
    }
    
    /**
     * Trova un'abitudine per ID o lancia un'eccezione se non esiste
     * @param habitId ID dell'abitudine da trovare
     * @return L'abitudine trovata
     * @throws HabitNotFoundException se l'ID specificato non esiste
     */
    public Habit findHabitOrThrow(Long habitId) {
        return habitRepository.findById(habitId)
                .orElseThrow(() -> new HabitNotFoundException(habitId));
    }
    
    /**
     * Salva un'abitudine esistente
     * @param habit L'abitudine da salvare
     * @return L'abitudine salvata
     */
    public Habit saveHabit(Habit habit) {
        return habitRepository.save(habit);
    }


    public void deleteHabit(Long id) {
        habitRepository.deleteById(id);
    }
}


