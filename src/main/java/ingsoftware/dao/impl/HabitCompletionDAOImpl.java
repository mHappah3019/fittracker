package ingsoftware.dao.impl;

import ingsoftware.dao.AbstractJpaDAO;
import ingsoftware.dao.HabitCompletionDAO;
import ingsoftware.model.HabitCompletion;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
@Transactional
public class HabitCompletionDAOImpl extends AbstractJpaDAO<HabitCompletion, Long> implements HabitCompletionDAO {

    @Override
    protected boolean isNew(HabitCompletion entity) {
        return entity.getId() == null;
    }

    @Override
    public boolean existsByUserIdAndHabitIdAndCompletionDate(Long userId, Long habitId, LocalDate completionDate) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(hc) FROM HabitCompletion hc " +
                        "WHERE hc.userId = :userId AND hc.habitId = :habitId AND hc.completionDate = :completionDate",
                Long.class);
        query.setParameter("userId", userId);
        query.setParameter("habitId", habitId);
        query.setParameter("completionDate", completionDate);
        return query.getSingleResult() > 0;
    }
}