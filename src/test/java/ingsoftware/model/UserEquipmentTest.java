package ingsoftware.model;

import ingsoftware.model.enum_helpers.EquipmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserEquipmentTest {

    private User user;
    private Equipment equipment;
    private UserEquipment userEquipment;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        
        equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("Test Equipment");
        equipment.setType(EquipmentType.WEAPON);
        equipment.setExperienceMultiplier(1.5);
        
        userEquipment = new UserEquipment(user, equipment);
    }

    @Test
    void testUserEquipmentCreation() {
        assertNotNull(userEquipment);
        assertEquals(user, userEquipment.getUser());
        assertEquals(equipment, userEquipment.getEquipment());
        assertFalse(userEquipment.isEquipped());
        assertNotNull(userEquipment.getAcquiredDate());
        assertNull(userEquipment.getEquippedDate());
    }

    @Test
    void testEquipMethod() {
        LocalDateTime beforeEquip = LocalDateTime.now();
        
        userEquipment.equip();
        
        assertTrue(userEquipment.isEquipped());
        assertNotNull(userEquipment.getEquippedDate());
        assertTrue(userEquipment.getEquippedDate().isAfter(beforeEquip) || 
                  userEquipment.getEquippedDate().isEqual(beforeEquip));
    }

    @Test
    void testUnequipMethod() {
        // Prima equipaggia
        userEquipment.equip();
        assertTrue(userEquipment.isEquipped());
        assertNotNull(userEquipment.getEquippedDate());
        
        // Poi disequipaggia
        userEquipment.unequip();
        assertFalse(userEquipment.isEquipped());
        assertNull(userEquipment.getEquippedDate());
    }

    @Test
    void testSetEquippedTrue() {
        userEquipment.setEquipped(true);
        
        assertTrue(userEquipment.isEquipped());
        assertNotNull(userEquipment.getEquippedDate());
    }

    @Test
    void testSetEquippedFalse() {
        // Prima equipaggia
        userEquipment.setEquipped(true);
        assertTrue(userEquipment.isEquipped());
        
        // Poi disequipaggia
        userEquipment.setEquipped(false);
        assertFalse(userEquipment.isEquipped());
        assertNull(userEquipment.getEquippedDate());
    }

    @Test
    void testEqualsAndHashCode() {
        UserEquipment userEquipment2 = new UserEquipment(user, equipment);
        
        assertEquals(userEquipment, userEquipment2);
        assertEquals(userEquipment.hashCode(), userEquipment2.hashCode());
    }

    @Test
    void testToString() {
        String toString = userEquipment.toString();
        
        assertNotNull(toString);
        assertTrue(toString.contains("UserEquipment"));
        assertTrue(toString.contains("user=1"));
        assertTrue(toString.contains("equipment=Test Equipment"));
        assertTrue(toString.contains("equipped=false"));
    }

    @Test
    void testConstructorWithEquippedFlag() {
        UserEquipment equippedUserEquipment = new UserEquipment(user, equipment, true);
        
        assertTrue(equippedUserEquipment.isEquipped());
        assertNotNull(equippedUserEquipment.getEquippedDate());
        assertNotNull(equippedUserEquipment.getAcquiredDate());
    }
}