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
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId",
                UserEquipment.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public List<UserEquipment> findByUserIdAndEquippedTrue(Long userId) {
        TypedQuery<UserEquipment> query = entityManager.createQuery(
                "SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId AND ue.equipped = true",
                UserEquipment.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }


    @Override
    public Optional<UserEquipment> findByUserIdAndEquipmentId(Long userId, Long equipmentId) {
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
    public boolean existsByUserIdAndEquipmentId(Long userId, Long equipmentId) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ue) FROM UserEquipment ue " +
                        "WHERE ue.userId = :userId AND ue.equipmentId = :equipmentId",
                Long.class);
        query.setParameter("userId", userId);
        query.setParameter("equipmentId", equipmentId);
        return query.getSingleResult() > 0;
    }
}