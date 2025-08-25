package ingsoftware.repository;

import ingsoftware.model.Equipment;
import ingsoftware.model.User;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserEquipmentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserEquipmentRepository userEquipmentRepository;

    private User user;
    private Equipment weapon;
    private Equipment armor;
    private UserEquipment userWeapon;
    private UserEquipment userArmor;

    @BeforeEach
    void setUp() {
        // Crea utente
        user = new User();
        user.setId(1L);
        entityManager.persistAndFlush(user);

        // Crea equipaggiamenti
        weapon = new Equipment();
        weapon.setId(1L);
        weapon.setName("Test Weapon");
        weapon.setType(EquipmentType.WEAPON);
        weapon.setExperienceMultiplier(1.5);
        entityManager.persistAndFlush(weapon);

        armor = new Equipment();
        armor.setId(2L);
        armor.setName("Test Armor");
        armor.setType(EquipmentType.ARMOR);
        armor.setExperienceMultiplier(1.2);
        entityManager.persistAndFlush(armor);

        // Crea associazioni utente-equipaggiamento
        userWeapon = new UserEquipment(user.getId(), weapon.getId(), true); // equipaggiato
        userArmor = new UserEquipment(user.getId(), armor.getId(), false);  // non equipaggiato
        
        entityManager.persistAndFlush(userWeapon);
        entityManager.persistAndFlush(userArmor);
    }

    @Test
    void testFindByUserId() {
        List<UserEquipment> userEquipments = userEquipmentRepository.findByUserId(user.getId());
        
        assertEquals(2, userEquipments.size());
    }

    @Test
    void testFindByUserIdAndEquippedTrue() {
        List<UserEquipment> equippedItems = userEquipmentRepository.findByUserIdAndEquippedTrue(user.getId());
        
        assertEquals(1, equippedItems.size());
        assertEquals(weapon.getId(), equippedItems.get(0).getEquipmentId());
    }

    @Test
    void testFindByUserIdAndEquippedFalse() {
        List<UserEquipment> unequippedItems = userEquipmentRepository.findByUserIdAndEquippedFalse(user.getId());
        
        assertEquals(1, unequippedItems.size());
        assertEquals(armor.getId(), unequippedItems.get(0).getEquipmentId());
    }

    @Test
    void testFindByUserIdAndEquipmentId() {
        Optional<UserEquipment> found = userEquipmentRepository.findByUserIdAndEquipmentId(user.getId(), weapon.getId());
        
        assertTrue(found.isPresent());
        assertEquals(userWeapon.getId(), found.get().getId());
    }

    @Test
    void testFindEquippedByUserIdAndType() {
        Optional<UserEquipment> found = userEquipmentRepository.findEquippedByUserIdAndType(user.getId(), EquipmentType.WEAPON);
        
        assertTrue(found.isPresent());
        assertEquals(weapon.getId(), found.get().getEquipmentId());
        
        // Test per tipo non equipaggiato
        Optional<UserEquipment> notFound = userEquipmentRepository.findEquippedByUserIdAndType(user.getId(), EquipmentType.ARMOR);
        assertFalse(notFound.isPresent());
    }

    @Test
    void testFindByUserIdAndEquipmentType() {
        List<UserEquipment> weaponItems = userEquipmentRepository.findByUserIdAndEquipmentType(user.getId(), EquipmentType.WEAPON);
        
        assertEquals(1, weaponItems.size());
        assertEquals(weapon.getId(), weaponItems.get(0).getEquipmentId());
    }

    @Test
    void testExistsByUserIdAndEquipmentId() {
        assertTrue(userEquipmentRepository.existsByUserIdAndEquipmentId(user.getId(), weapon.getId()));
        assertTrue(userEquipmentRepository.existsByUserIdAndEquipmentId(user.getId(), armor.getId()));
        assertFalse(userEquipmentRepository.existsByUserIdAndEquipmentId(user.getId(), 999L));
    }
}