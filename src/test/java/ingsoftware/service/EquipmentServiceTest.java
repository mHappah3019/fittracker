package ingsoftware.service;

import ingsoftware.dao.EquipmentDAO;
import ingsoftware.dao.UserDAO;
import ingsoftware.dao.UserEquipmentDAO;
import ingsoftware.model.Equipment;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test per EquipmentService - Tests semplici e comprensibili
 * Copre i casi principali di gestione dell'equipaggiamento
 */
@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentDAO equipmentDAO;
    
    @Mock
    private UserDAO userDAO;
    
    @Mock
    private UserEquipmentDAO userEquipmentDAO;

    @InjectMocks
    private EquipmentService equipmentService;

    private Equipment weaponEquipment;
    private Equipment armorEquipment;
    private UserEquipment userEquipment;
    private Long testUserId = 1L;
    
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

        // UserEquipment di test
        userEquipment = new UserEquipment(testUserId, weaponEquipment.getId());
        userEquipment.equip();
    }

    // ===========================================
    // TEST PER getAllEquipmentGroupedByType()
    // ===========================================

    @Test
    void testGetAllEquipmentGroupedByType_ReturnsGroupedEquipment() {
        // Arrange
        List<Equipment> availableEquipment = Arrays.asList(weaponEquipment, armorEquipment);
        when(equipmentDAO.findByAvailableTrue()).thenReturn(availableEquipment);

        // Act
        Map<EquipmentType, ObservableList<Equipment>> result = 
            equipmentService.getAllEquipmentGroupedByType();

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.containsKey(EquipmentType.WEAPON), "Dovrebbe contenere equipaggiamento di tipo WEAPON");
        assertTrue(result.containsKey(EquipmentType.ARMOR), "Dovrebbe contenere equipaggiamento di tipo ARMOR");
        
        // Verifica che ci siano le opzioni "Nessuno" per ogni tipo
        ObservableList<Equipment> weapons = result.get(EquipmentType.WEAPON);
        assertTrue(weapons.size() >= 2, "Dovrebbe includere almeno l'arma + opzione 'Nessuno'");
        
        verify(equipmentDAO).findByAvailableTrue();
    }

    // ===========================================
    // TEST PER findAllEquippedByUser()
    // ===========================================

    @Test
    void testFindAllEquippedByUser_UserHasEquipment_ReturnsMap() {
        // Arrange
        List<UserEquipment> userEquipments = Arrays.asList(userEquipment);
        when(userEquipmentDAO.findByUserIdAndEquippedTrue(testUserId)).thenReturn(userEquipments);
        when(equipmentDAO.findById(weaponEquipment.getId())).thenReturn(Optional.of(weaponEquipment));

        // Act
        Map<EquipmentType, Equipment> result = equipmentService.findAllEquippedByUser(testUserId);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertEquals(1, result.size(), "Dovrebbe contenere un equipaggiamento");
        assertTrue(result.containsKey(EquipmentType.WEAPON), "Dovrebbe contenere un'arma");
        assertEquals(weaponEquipment, result.get(EquipmentType.WEAPON));
        
        verify(userEquipmentDAO).findByUserIdAndEquippedTrue(testUserId);
        verify(equipmentDAO).findById(weaponEquipment.getId());
    }

    @Test
    void testFindAllEquippedByUser_UserHasNoEquipment_ReturnsEmptyMap() {
        // Arrange
        when(userEquipmentDAO.findByUserIdAndEquippedTrue(testUserId)).thenReturn(Collections.emptyList());

        // Act
        Map<EquipmentType, Equipment> result = equipmentService.findAllEquippedByUser(testUserId);

        // Assert
        assertNotNull(result, "Il risultato non dovrebbe essere null");
        assertTrue(result.isEmpty(), "La mappa dovrebbe essere vuota");
    }

    // ===========================================
    // TEST PER findEquippedByUserIdAndType()
    // ===========================================

    @Test
    void testFindEquippedByUserIdAndType_EquipmentExists_ReturnsEquipment() {
        // Arrange
        when(userEquipmentDAO.findEquippedByUserIdAndType(testUserId, EquipmentType.WEAPON))
            .thenReturn(Optional.of(userEquipment));
        when(equipmentDAO.findById(weaponEquipment.getId())).thenReturn(Optional.of(weaponEquipment));

        // Act
        Optional<Equipment> result = equipmentService.findEquippedByUserIdAndType(testUserId, EquipmentType.WEAPON);

        // Assert
        assertTrue(result.isPresent(), "Dovrebbe trovare l'equipaggiamento");
        assertEquals(weaponEquipment, result.get(), "Dovrebbe restituire l'arma corretta");
    }

    // ===========================================
    // TEST PER equip()
    // ===========================================

    @Test
    void testEquip_ValidEquipment_EquipsSuccessfully() {
        // Arrange
        when(equipmentDAO.findById(weaponEquipment.getId())).thenReturn(Optional.of(weaponEquipment));
        when(userEquipmentDAO.findByUserIdAndEquipmentId(testUserId, weaponEquipment.getId()))
            .thenReturn(Optional.of(userEquipment));

        // Act
        Equipment result = equipmentService.equip(testUserId, weaponEquipment.getId());

        // Assert
        assertNotNull(result, "Dovrebbe restituire l'equipaggiamento equipaggiato");
        assertEquals(weaponEquipment, result, "Dovrebbe restituire l'equipaggiamento corretto");
        verify(userEquipmentDAO).save(userEquipment);
        
        // Verifica che sia stato chiamato unequip per rimuovere equipaggiamento dello stesso tipo
        verify(userEquipmentDAO).findEquippedByUserIdAndType(testUserId, EquipmentType.WEAPON);
    }

    @Test
    void testEquip_EquipmentNotFound_ThrowsException() {
        // Arrange
        Long nonExistentId = 999L;
        when(equipmentDAO.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> equipmentService.equip(testUserId, nonExistentId),
                "Dovrebbe lanciare RuntimeException quando l'equipaggiamento non esiste");
        
        assertTrue(exception.getMessage().contains("non trovato"));
    }

}