package ingsoftware.dao.impl;

import ingsoftware.dao.AbstractJpaDAO;
import ingsoftware.dao.HabitDAO;
import ingsoftware.model.Habit;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class HabitDAOImpl extends AbstractJpaDAO<Habit, Long> implements HabitDAO {

    @Override
    protected boolean isNew(Habit entity) {
        return entity.getId() == null;
    }

    @Override
    public List<Habit> findAllByUserId(Long userId) {
        // Query JPQL esplicita
        TypedQuery<Habit> query = entityManager.createQuery(
                "SELECT h FROM Habit h WHERE h.userId = :userId",
                Habit.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public Optional<Habit> findByUserIdAndName(Long userId, String name) {
        // Query JPQL esplicita
        TypedQuery<Habit> query = entityManager.createQuery(
                "SELECT h FROM Habit h WHERE h.userId = :userId AND h.name = :name",
                Habit.class);
        query.setParameter("userId", userId);
        query.setParameter("name", name);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByIdAndUserIdAndLastCompletedDate(Long habitId, Long userId, LocalDate today) {
        // Query JPQL esplicita con COUNT
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(h) FROM Habit h " +
                        "WHERE h.id = :habitId AND h.userId = :userId AND h.lastCompletedDate = :today",
                Long.class);
        query.setParameter("habitId", habitId);
        query.setParameter("userId", userId);
        query.setParameter("today", today);
        return query.getSingleResult() > 0;
    }
}