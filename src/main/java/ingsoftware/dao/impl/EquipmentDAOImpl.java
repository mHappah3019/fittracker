package ingsoftware.dao.impl;

import ingsoftware.dao.AbstractJpaDAO;
import ingsoftware.dao.EquipmentDAO;
import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class EquipmentDAOImpl extends AbstractJpaDAO<Equipment, Long> implements EquipmentDAO {

    @Override
    protected boolean isNew(Equipment entity) {
        return entity.getId() == null;
    }

    @Override
    public List<Equipment> findByAvailableTrue() {
        // Query JPQL esplicita
        TypedQuery<Equipment> query = entityManager.createQuery(
                "SELECT e FROM Equipment e WHERE e.available = true",
                Equipment.class);
        return query.getResultList();
    }

    @Override
    public List<Equipment> findByTypeAndAvailableTrue(EquipmentType type) {
        // Query JPQL esplicita
        TypedQuery<Equipment> query = entityManager.createQuery(
                "SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true",
                Equipment.class);
        query.setParameter("type", type);
        return query.getResultList();
    }
}
