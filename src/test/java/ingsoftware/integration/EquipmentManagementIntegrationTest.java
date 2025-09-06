package ingsoftware.integration;

import ingsoftware.dao.EquipmentDAO;
import ingsoftware.dao.UserDAO;
import ingsoftware.dao.UserEquipmentDAO;
import ingsoftware.model.Equipment;
import ingsoftware.model.User;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import ingsoftware.service.EquipmentService;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Test per il flusso completo di gestione equipaggiamenti.
 * Testa l'integrazione tra EquipmentService e i rispettivi DAO.
 */
@DisplayName("Integration Test - Gestione Equipaggiamenti")
class EquipmentManagementIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserEquipmentDAO userEquipmentDAO;

    private User testUser;
    private Equipment testWeapon;
    private Equipment testArmor;
    private Equipment testAccessory;

    @BeforeEach
    void setUp() {
        
        // Crea un utente di test
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setLevel(1);
        testUser.setTotalXp(0.0);
        testUser = userDAO.save(testUser);

        // Crea equipaggiamenti di test
        testWeapon = new Equipment();
        testWeapon.setName("Spada di Ferro");
        testWeapon.setDescription("Una spada robusta per principianti");
        testWeapon.setType(EquipmentType.WEAPON);
        testWeapon.setAvailable(true);
        testWeapon.setExperienceMultiplier(1.2);
        testWeapon = equipmentDAO.save(testWeapon);

        testArmor = new Equipment();
        testArmor.setName("Armatura di Cuoio");
        testArmor.setDescription("Protezione base");
        testArmor.setType(EquipmentType.ARMOR);
        testArmor.setAvailable(true);
        testArmor.setExperienceMultiplier(1.1);
        testArmor = equipmentDAO.save(testArmor);

        testAccessory = new Equipment();
        testAccessory.setName("Anello della Fortuna");
        testAccessory.setDescription("Aumenta la fortuna");
        testAccessory.setType(EquipmentType.MISC);
        testAccessory.setAvailable(true);
        testAccessory.setExperienceMultiplier(1.5);
        testAccessory = equipmentDAO.save(testAccessory);
    }

    @Test
    @DisplayName("Dovrebbe inizializzare correttamente l'inventario dell'utente")
    @Rollback
    void shouldInitializeUserEquipmentCorrectly() {
        // Act - Inizializza l'equipaggiamento dell'utente
        equipmentService.initializeUserEquipment(testUser.getId());

        // Assert - Verifica che l'utente abbia accesso a tutti gli equipaggiamenti
        List<UserEquipment> userEquipments = userEquipmentDAO.findByUserId(testUser.getId());
        
        // Dovrebbe avere almeno gli equipaggiamenti che abbiamo creato
        assertTrue(userEquipments.size() >= 3, "L'utente dovrebbe avere accesso agli equipaggiamenti");

        // Verifica che gli equipaggiamenti specifici siano presenti
        boolean hasWeapon = userEquipments.stream()
                .anyMatch(ue -> ue.getEquipmentId().equals(testWeapon.getId()));
        boolean hasArmor = userEquipments.stream()
                .anyMatch(ue -> ue.getEquipmentId().equals(testArmor.getId()));
        boolean hasAccessory = userEquipments.stream()
                .anyMatch(ue -> ue.getEquipmentId().equals(testAccessory.getId()));

        assertTrue(hasWeapon, "L'utente dovrebbe avere accesso alla spada");
        assertTrue(hasArmor, "L'utente dovrebbe avere accesso all'armatura");
        assertTrue(hasAccessory, "L'utente dovrebbe avere accesso all'accessorio");

        // Verifica che inizialmente nessun equipaggiamento sia equipaggiato
        List<UserEquipment> equippedItems = userEquipmentDAO.findByUserIdAndEquippedTrue(testUser.getId());
        assertTrue(equippedItems.isEmpty(), "Inizialmente nessun equipaggiamento dovrebbe essere equipaggiato");
    }

    @Test
    @DisplayName("Dovrebbe sostituire automaticamente equipaggiamento dello stesso tipo")
    @Rollback
    void shouldReplaceEquipmentOfSameType() throws Exception {
        // Arrange - Crea una seconda arma
        Equipment betterWeapon = new Equipment();
        betterWeapon.setName("Spada Magica");
        betterWeapon.setDescription("Una spada più potente");
        betterWeapon.setType(EquipmentType.WEAPON);
        betterWeapon.setAvailable(true);
        betterWeapon.setExperienceMultiplier(2.0);
        betterWeapon = equipmentDAO.save(betterWeapon);

        equipmentService.initializeUserEquipment(testUser.getId());

        // Act - Equipaggia prima la spada normale, poi quella magica
        equipmentService.equip(testUser.getId(), testWeapon.getId());
        equipmentService.equip(testUser.getId(), betterWeapon.getId());

        // Assert - Solo la spada magica dovrebbe essere equipaggiata
        Optional<Equipment> equippedWeapon = equipmentService.findEquippedByUserIdAndType(
                testUser.getId(), EquipmentType.WEAPON);
        assertTrue(equippedWeapon.isPresent());
        assertEquals(betterWeapon.getId(), equippedWeapon.get().getId());

        // Verifica che la spada normale sia stata disequipaggiata
        Optional<UserEquipment> oldWeaponEquipment = userEquipmentDAO.findByUserIdAndEquipmentId(
                testUser.getId(), testWeapon.getId());
        assertTrue(oldWeaponEquipment.isPresent());
        assertFalse(oldWeaponEquipment.get().isEquipped());

        // Verifica che la spada magica sia equipaggiata
        Optional<UserEquipment> newWeaponEquipment = userEquipmentDAO.findByUserIdAndEquipmentId(
                testUser.getId(), betterWeapon.getId());
        assertTrue(newWeaponEquipment.isPresent());
        assertTrue(newWeaponEquipment.get().isEquipped());
    }

    @Test
    @DisplayName("Dovrebbe recuperare correttamente tutti gli equipaggiamenti raggruppati per tipo")
    @Rollback
    void shouldRetrieveAllEquipmentGroupedByType() {
        // Act
        Map<EquipmentType, ObservableList<Equipment>> groupedEquipment = 
                equipmentService.getAllEquipmentGroupedByType();

        // Assert - Verifica che ci siano equipaggiamenti per ogni tipo
        assertTrue(groupedEquipment.containsKey(EquipmentType.WEAPON));
        assertTrue(groupedEquipment.containsKey(EquipmentType.ARMOR));
        assertTrue(groupedEquipment.containsKey(EquipmentType.MISC));

        // Verifica che i nostri equipaggiamenti di test siano presenti
        ObservableList<Equipment> weapons = groupedEquipment.get(EquipmentType.WEAPON);
        ObservableList<Equipment> armors = groupedEquipment.get(EquipmentType.ARMOR);
        ObservableList<Equipment> accessories = groupedEquipment.get(EquipmentType.MISC);

        assertTrue(weapons.stream().anyMatch(e -> e.getId().equals(testWeapon.getId())));
        assertTrue(armors.stream().anyMatch(e -> e.getId().equals(testArmor.getId())));
        assertTrue(accessories.stream().anyMatch(e -> e.getId().equals(testAccessory.getId())));

        // Verifica che ogni tipo abbia anche l'opzione "Nessun X"
        assertTrue(weapons.stream().anyMatch(e -> e.getName().contains("Nessun")));
        assertTrue(armors.stream().anyMatch(e -> e.getName().contains("Nessun")));
        assertTrue(accessories.stream().anyMatch(e -> e.getName().contains("Nessun")));
    }
}