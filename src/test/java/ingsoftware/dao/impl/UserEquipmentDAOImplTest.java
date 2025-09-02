package ingsoftware.dao.impl;

import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test per UserEquipmentDAOImpl - Tests semplici e comprensibili
 * Copre i casi principali delle query JPQL personalizzate
 */
@ExtendWith(MockitoExtension.class)
class UserEquipmentDAOImplTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<UserEquipment> userEquipmentTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @InjectMocks
    private UserEquipmentDAOImpl userEquipmentDAO;

    private UserEquipment equippedWeapon;
    private UserEquipment unequippedArmor;
    private UserEquipment equippedShield;

    @BeforeEach
    void setUp() {
        // Crea equipaggiamenti utente di test
        equippedWeapon = new UserEquipment();
        equippedWeapon.setId(1L);
        equippedWeapon.setUserId(100L);
        equippedWeapon.setEquipmentId(10L);
        equippedWeapon.setEquipped(true);

        unequippedArmor = new UserEquipment();
        unequippedArmor.setId(2L);
        unequippedArmor.setUserId(100L);
        unequippedArmor.setEquipmentId(20L);
        unequippedArmor.setEquipped(false);

        equippedShield = new UserEquipment();
        equippedShield.setId(3L);
        equippedShield.setUserId(200L);
        equippedShield.setEquipmentId(30L);
        equippedShield.setEquipped(true);
    }


    // ===========================================
    // TEST PER findByUserId()
    // ===========================================

    @Test
    void testFindByUserId_WhenUserEquipmentExists_ReturnsUserEquipment() {
        // Arrange
        Long userId = 100L;
        List<UserEquipment> userEquipments = Arrays.asList(equippedWeapon, unequippedArmor);
        
        when(entityManager.createQuery(eq("SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId"), eq(UserEquipment.class)))
                .thenReturn(userEquipmentTypedQuery);
        when(userEquipmentTypedQuery.getResultList()).thenReturn(userEquipments);

        // Act
        List<UserEquipment> result = userEquipmentDAO.findByUserId(userId);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(2, result.size(), "Dovrebbe restituire 2 equipaggiamenti dell'utente");
        assertTrue(result.contains(equippedWeapon), "Dovrebbe contenere l'arma equipaggiata");
        assertTrue(result.contains(unequippedArmor), "Dovrebbe contenere l'armatura non equipaggiata");
        
        verify(entityManager).createQuery("SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId", UserEquipment.class);
        verify(userEquipmentTypedQuery).setParameter("userId", userId);
        verify(userEquipmentTypedQuery).getResultList();
    }

    @Test
    void testFindByUserId_WhenNoUserEquipmentExists_ReturnsEmptyList() {
        // Arrange
        Long userId = 999L;
        
        when(entityManager.createQuery(eq("SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId"), eq(UserEquipment.class)))
                .thenReturn(userEquipmentTypedQuery);
        when(userEquipmentTypedQuery.getResultList()).thenReturn(Collections.emptyList());

        // Act
        List<UserEquipment> result = userEquipmentDAO.findByUserId(userId);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.isEmpty(), "Dovrebbe restituire una lista vuota quando l'utente non ha equipaggiamenti");
    }

    // ===========================================
    // TEST PER findByUserIdAndEquipmentId()
    // ===========================================

    @Test
    void testFindByUserIdAndEquipmentId_WhenExists_ReturnsUserEquipment() {
        // Arrange
        Long userId = 100L;
        Long equipmentId = 10L;
        
        when(entityManager.createQuery(eq("SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId AND ue.equipmentId = :equipmentId"), eq(UserEquipment.class)))
                .thenReturn(userEquipmentTypedQuery);
        when(userEquipmentTypedQuery.getSingleResult()).thenReturn(equippedWeapon);

        // Act
        Optional<UserEquipment> result = userEquipmentDAO.findByUserIdAndEquipmentId(userId, equipmentId);

        // Assert
        assertTrue(result.isPresent(), "Dovrebbe trovare l'equipaggiamento utente");
        assertEquals(equippedWeapon, result.get(), "Dovrebbe restituire l'equipaggiamento corretto");
        
        verify(userEquipmentTypedQuery).setParameter("userId", userId);
        verify(userEquipmentTypedQuery).setParameter("equipmentId", equipmentId);
    }

    @Test
    void testFindByUserIdAndEquipmentId_WhenDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        Long userId = 100L;
        Long equipmentId = 999L;
        
        when(entityManager.createQuery(eq("SELECT ue FROM UserEquipment ue WHERE ue.userId = :userId AND ue.equipmentId = :equipmentId"), eq(UserEquipment.class)))
                .thenReturn(userEquipmentTypedQuery);
        when(userEquipmentTypedQuery.getSingleResult()).thenThrow(new NoResultException("Nessun risultato trovato"));

        // Act
        Optional<UserEquipment> result = userEquipmentDAO.findByUserIdAndEquipmentId(userId, equipmentId);

        // Assert
        assertFalse(result.isPresent(), "Dovrebbe restituire Optional vuoto quando non esiste");
    }

    // ===========================================
    // TEST PER findEquippedByUserIdAndType()
    // ===========================================

    @Test
    void testFindEquippedByUserIdAndType_WhenExists_ReturnsEquippedItem() {
        // Arrange
        Long userId = 100L;
        EquipmentType type = EquipmentType.WEAPON;
        
        when(entityManager.createQuery(eq("SELECT ue FROM UserEquipment ue JOIN Equipment e ON ue.equipmentId = e.id WHERE ue.userId = :userId AND ue.equipped = true AND e.type = :type"), eq(UserEquipment.class)))
                .thenReturn(userEquipmentTypedQuery);
        when(userEquipmentTypedQuery.getSingleResult()).thenReturn(equippedWeapon);

        // Act
        Optional<UserEquipment> result = userEquipmentDAO.findEquippedByUserIdAndType(userId, type);

        // Assert
        assertTrue(result.isPresent(), "Dovrebbe trovare l'equipaggiamento equipaggiato del tipo specificato");
        assertEquals(equippedWeapon, result.get(), "Dovrebbe restituire l'arma equipaggiata");
        
        verify(userEquipmentTypedQuery).setParameter("userId", userId);
        verify(userEquipmentTypedQuery).setParameter("type", type);
    }

    @Test
    void testFindEquippedByUserIdAndType_WhenDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        Long userId = 100L;
        EquipmentType type = EquipmentType.SHIELD;
        
        when(entityManager.createQuery(eq("SELECT ue FROM UserEquipment ue JOIN Equipment e ON ue.equipmentId = e.id WHERE ue.userId = :userId AND ue.equipped = true AND e.type = :type"), eq(UserEquipment.class)))
                .thenReturn(userEquipmentTypedQuery);
        when(userEquipmentTypedQuery.getSingleResult()).thenThrow(new NoResultException("Nessun risultato trovato"));

        // Act
        Optional<UserEquipment> result = userEquipmentDAO.findEquippedByUserIdAndType(userId, type);

        // Assert
        assertFalse(result.isPresent(), "Dovrebbe restituire Optional vuoto quando non c'è equipaggiamento equipaggiato del tipo specificato");
    }

}