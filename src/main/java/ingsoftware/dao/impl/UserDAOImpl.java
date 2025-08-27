package ingsoftware.dao.impl;

import ingsoftware.dao.AbstractJpaDAO;
import ingsoftware.dao.UserDAO;
import ingsoftware.model.User;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class UserDAOImpl extends AbstractJpaDAO<User, Long> implements UserDAO {

    @Override
    protected boolean isNew(User entity) {
        return entity.getId() == null;
    }

    @Override
    public List<Long> findAllActiveUserIds(int offset, int limit) {
        // Query JPQL esplicita con paginazione
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT u.id FROM User u WHERE u.lastAccessDate IS NOT NULL ORDER BY u.id",
                Long.class);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}