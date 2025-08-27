package ingsoftware.dao.impl;

import ingsoftware.dao.AbstractJpaDAO;
import ingsoftware.dao.UserEquipmentDAO;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class UserEquipmentDAOImpl extends AbstractJpaDAO<UserEquipment, Long> implements UserEquipmentDAO {

    @Override
    protected boolean isNew(UserEquipment entity) {
        return entity.getId() == null;
    }

    @Override
    public List<UserEquipment> findByUserId(Long userId) {
        // Query JPQL esplicita
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId",
                UserEquipment.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<UserEquipment> findByUserIdAndEquippedTrue(Long userId) {
        // Query JPQL esplicita
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId AND ue.equipped = true",
                UserEquipment.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<UserEquipment> findByUserIdAndEquippedFalse(Long userId) {
        // Query JPQL esplicita
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId AND ue.equipped = false",
                UserEquipment.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public Optional<UserEquipment> findByUserIdAndEquipmentId(Long userId, Long equipmentId) {
        // Query JPQL esplicita
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId AND ue.equipmentId = :equipmentId",
                UserEquipment.class);
        query.setParameter("userId", userId);
        query.setParameter("equipmentId", equipmentId);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserEquipment> findEquippedByUserIdAndType(Long userId, EquipmentType type) {
        // Query JPQL esplicita con JOIN
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue " +
                        "JOIN Equipment e ON ue.equipmentId = e.id " +
                        "WHERE ue.userId = :userId AND ue.equipped = true AND e.type = :type",
                UserEquipment.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<UserEquipment> findByUserIdAndEquipmentType(Long userId, EquipmentType type) {
        // Query JPQL esplicita con JOIN
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue " +
                        "JOIN Equipment e ON ue.equipmentId = e.id " +
                        "WHERE ue.userId = :userId AND e.type = :type",
                UserEquipment.class);
        query.setParameter("userId", userId);
        query.setParameter("type", type);
        return query.getResultList();
    }

    @Override
    public boolean existsByUserIdAndEquipmentId(Long userId, Long equipmentId) {
        // Query JPQL esplicita con COUNT
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ue) FROM UserEquipment ue " +
                        "WHERE ue.userId = :userId AND ue.equipmentId = :equipmentId",
                Long.class);
        query.setParameter("userId", userId);
        query.setParameter("equipmentId", equipmentId);
        return query.getSingleResult() > 0;
    }
}