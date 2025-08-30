package ingsoftware.dao.impl;

import ingsoftware.model.Equipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test per EquipmentDAOImpl - Tests semplici e comprensibili
 * Copre i casi principali delle query JPQL personalizzate
 */
@ExtendWith(MockitoExtension.class)
class EquipmentDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Equipment> typedQuery;

    @InjectMocks
    private EquipmentDAOImpl equipmentDAO;

    private Equipment weaponEquipment;
    private Equipment armorEquipment;
    private Equipment unavailableEquipment;

    @BeforeEach
    void setUp() {
        // Crea equipaggiamenti di test
        weaponEquipment = new Equipment();
        weaponEquipment.setId(1L);
        weaponEquipment.setName("Spada Magica");
        weaponEquipment.setType(EquipmentType.WEAPON);
        weaponEquipment.setAvailable(true);

        armorEquipment = new Equipment();
        armorEquipment.setId(2L);
        armorEquipment.setName("Armatura Dorata");
        armorEquipment.setType(EquipmentType.ARMOR);
        armorEquipment.setAvailable(true);

        unavailableEquipment = new Equipment();
        unavailableEquipment.setId(3L);
        unavailableEquipment.setName("Spada Rotta");
        unavailableEquipment.setType(EquipmentType.WEAPON);
        unavailableEquipment.setAvailable(false);
    }

    // ===========================================
    // TEST PER isNew()
    // ===========================================

    @Test
    void testIsNew_WhenIdIsNull_ReturnsTrue() {
        // Arrange
        Equipment newEquipment = new Equipment();
        newEquipment.setId(null);

        // Act
        boolean result = equipmentDAO.isNew(newEquipment);

        // Assert
        assertTrue(result, "Un equipaggiamento con id null dovrebbe essere considerato nuovo");
    }

    @Test
    void testIsNew_WhenIdIsNotNull_ReturnsFalse() {
        // Arrange
        Equipment existingEquipment = new Equipment();
        existingEquipment.setId(1L);

        // Act
        boolean result = equipmentDAO.isNew(existingEquipment);

        // Assert
        assertFalse(result, "Un equipaggiamento con id non null non dovrebbe essere considerato nuovo");
    }

    // ===========================================
    // TEST PER findByAvailableTrue()
    // ===========================================

    @Test
    void testFindByAvailableTrue_WhenEquipmentExists_ReturnsAvailableEquipment() {
        // Arrange
        List<Equipment> availableEquipment = Arrays.asList(weaponEquipment, armorEquipment);
        when(entityManager.createQuery(eq("SELECT e FROM Equipment e WHERE e.available = true"), eq(Equipment.class)))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(availableEquipment);

        // Act
        List<Equipment> result = equipmentDAO.findByAvailableTrue();

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(2, result.size(), "Dovrebbe restituire 2 equipaggiamenti disponibili");
        assertTrue(result.contains(weaponEquipment), "Dovrebbe contenere l'arma disponibile");
        assertTrue(result.contains(armorEquipment), "Dovrebbe contenere l'armatura disponibile");
        assertFalse(result.contains(unavailableEquipment), "Non dovrebbe contenere equipaggiamento non disponibile");
        
        // Verifica che la query sia stata eseguita correttamente
        verify(entityManager).createQuery("SELECT e FROM Equipment e WHERE e.available = true", Equipment.class);
        verify(typedQuery).getResultList();
    }

    @Test
    void testFindByAvailableTrue_WhenNoAvailableEquipment_ReturnsEmptyList() {
        // Arrange
        when(entityManager.createQuery(eq("SELECT e FROM Equipment e WHERE e.available = true"), eq(Equipment.class)))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<Equipment> result = equipmentDAO.findByAvailableTrue();

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.isEmpty(), "Dovrebbe restituire una lista vuota quando non ci sono equipaggiamenti disponibili");
        
        verify(entityManager).createQuery("SELECT e FROM Equipment e WHERE e.available = true", Equipment.class);
        verify(typedQuery).getResultList();
    }

    // ===========================================
    // TEST PER findByTypeAndAvailableTrue()
    // ===========================================

    @Test
    void testFindByTypeAndAvailableTrue_WhenEquipmentOfTypeExists_ReturnsFilteredEquipment() {
        // Arrange
        List<Equipment> weaponEquipmentList = Arrays.asList(this.weaponEquipment);
        when(entityManager.createQuery(eq("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true"), eq(Equipment.class)))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(weaponEquipmentList);

        // Act
        List<Equipment> result = equipmentDAO.findByTypeAndAvailableTrue(EquipmentType.WEAPON);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(1, result.size(), "Dovrebbe restituire 1 arma disponibile");
        assertTrue(result.contains(this.weaponEquipment), "Dovrebbe contenere l'arma del tipo richiesto");
        
        // Verifica che la query sia stata eseguita correttamente con il parametro
        verify(entityManager).createQuery("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true", Equipment.class);
        verify(typedQuery).setParameter("type", EquipmentType.WEAPON);
        verify(typedQuery).getResultList();
    }

    @Test
    void testFindByTypeAndAvailableTrue_WhenNoEquipmentOfType_ReturnsEmptyList() {
        // Arrange
        when(entityManager.createQuery(eq("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true"), eq(Equipment.class)))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<Equipment> result = equipmentDAO.findByTypeAndAvailableTrue(EquipmentType.SHIELD);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.isEmpty(), "Dovrebbe restituire una lista vuota quando non ci sono equipaggiamenti del tipo richiesto");
        
        verify(entityManager).createQuery("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true", Equipment.class);
        verify(typedQuery).setParameter("type", EquipmentType.SHIELD);
        verify(typedQuery).getResultList();
    }

    @Test
    void testFindByTypeAndAvailableTrue_WithArmorType_ReturnsArmorEquipment() {
        // Arrange
        List<Equipment> armorEquipmentList = Arrays.asList(armorEquipment);
        when(entityManager.createQuery(eq("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true"), eq(Equipment.class)))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(armorEquipmentList);

        // Act
        List<Equipment> result = equipmentDAO.findByTypeAndAvailableTrue(EquipmentType.ARMOR);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(1, result.size(), "Dovrebbe restituire 1 armatura disponibile");
        assertTrue(result.contains(armorEquipment), "Dovrebbe contenere l'armatura del tipo richiesto");
        
        // Verifica che la query sia stata eseguita correttamente
        verify(entityManager).createQuery("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true", Equipment.class);
        verify(typedQuery).setParameter("type", EquipmentType.ARMOR);
        verify(typedQuery).getResultList();
    }

    @Test
    void testFindByTypeAndAvailableTrue_WithNullType_ExecutesQueryWithNullParameter() {
        // Arrange
        when(entityManager.createQuery(eq("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true"), eq(Equipment.class)))
                .thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<Equipment> result = equipmentDAO.findByTypeAndAvailableTrue(null);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.isEmpty(), "Dovrebbe restituire una lista vuota per tipo null");
        
        // Verifica che la query sia stata eseguita con parametro null
        verify(entityManager).createQuery("SELECT e FROM Equipment e WHERE e.type = :type AND e.available = true", Equipment.class);
        verify(typedQuery).setParameter("type", null);
        verify(typedQuery).getResultList();
    }
}