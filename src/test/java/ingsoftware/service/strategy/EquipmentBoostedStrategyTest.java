package ingsoftware.service.strategy;

import ingsoftware.model.Equipment;
import ingsoftware.model.EquipmentType;
import ingsoftware.model.User;
import ingsoftware.service.EquipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EquipmentBoostedStrategyTest {

    @Mock
    private GamificationStrategy baseStrategy;

    @Mock
    private EquipmentService equipmentService;

    private EquipmentBoostedStrategy equipmentBoostedStrategy;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        equipmentBoostedStrategy = new EquipmentBoostedStrategy(baseStrategy, equipmentService);
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setLevel(5);
    }

    @Test
    void calculateExperience_shouldReturnBaseResult_whenNoEquipmentActive() {
        // Arrange
        double baseExperience = 100.0;
        double expectedBaseResult = 150.0;
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(expectedBaseResult);
        when(equipmentService.getFullEquipmentSet(testUser.getID())).thenReturn(null);

        // Act
        double result = equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        assertThat(result).isEqualTo(expectedBaseResult);
        verify(baseStrategy).calculateExperience(baseExperience, testUser);
        verify(equipmentService).getFullEquipmentSet(testUser.getID());
    }

    @Test
    void calculateExperience_shouldReturnBaseResult_whenEquipmentMapIsEmpty() {
        // Arrange
        double baseExperience = 100.0;
        double expectedBaseResult = 150.0;
        Map<EquipmentType, Equipment> emptyEquipmentMap = new EnumMap<>(EquipmentType.class);
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(expectedBaseResult);
        when(equipmentService.getFullEquipmentSet(testUser.getID())).thenReturn(emptyEquipmentMap);

        // Act
        double result = equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        assertThat(result).isEqualTo(expectedBaseResult);
        verify(baseStrategy).calculateExperience(baseExperience, testUser);
        verify(equipmentService).getFullEquipmentSet(testUser.getID());
    }

    @Test
    void calculateExperience_shouldApplySingleEquipmentMultiplier() {
        // Arrange
        double baseExperience = 100.0;
        double baseResult = 150.0;
        double weaponMultiplier = 1.5;
        double expectedResult = baseResult * weaponMultiplier; // 150.0 * 1.5 = 225.0
        
        Equipment weapon = createTestEquipment(1L, "Test Sword", EquipmentType.WEAPON, weaponMultiplier);
        Map<EquipmentType, Equipment> equipmentMap = new EnumMap<>(EquipmentType.class);
        equipmentMap.put(EquipmentType.WEAPON, weapon);
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(baseResult);
        when(equipmentService.getFullEquipmentSet(testUser.getID())).thenReturn(equipmentMap);

        // Act
        double result = equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(baseStrategy).calculateExperience(baseExperience, testUser);
        verify(equipmentService).getFullEquipmentSet(testUser.getID());
    }

    @Test
    void calculateExperience_shouldApplyMultipleEquipmentMultipliers() {
        // Arrange
        double baseExperience = 100.0;
        double baseResult = 150.0;
        double weaponMultiplier = 1.5;
        double armorMultiplier = 1.2;
        double accessoryMultiplier = 1.1;
        double expectedResult = baseResult * weaponMultiplier * armorMultiplier * accessoryMultiplier; // 150.0 * 1.5 * 1.2 * 1.1 = 297.0
        
        Equipment weapon = createTestEquipment(1L, "Test Sword", EquipmentType.WEAPON, weaponMultiplier);
        Equipment armor = createTestEquipment(2L, "Test Armor", EquipmentType.ARMOR, armorMultiplier);
        Equipment accessory = createTestEquipment(3L, "Test Ring", EquipmentType.MISC, accessoryMultiplier);
        
        Map<EquipmentType, Equipment> equipmentMap = new EnumMap<>(EquipmentType.class);
        equipmentMap.put(EquipmentType.WEAPON, weapon);
        equipmentMap.put(EquipmentType.ARMOR, armor);
        equipmentMap.put(EquipmentType.MISC, accessory);
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(baseResult);
        when(equipmentService.getFullEquipmentSet(testUser.getID())).thenReturn(equipmentMap);

        // Act
        double result = equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(baseStrategy).calculateExperience(baseExperience, testUser);
        verify(equipmentService).getFullEquipmentSet(testUser.getID());
    }

    @Test
    void calculateExperience_shouldHandleEquipmentWithMultiplierOfOne() {
        // Arrange
        double baseExperience = 100.0;
        double baseResult = 150.0;
        double neutralMultiplier = 1.0;
        double expectedResult = baseResult * neutralMultiplier; // 150.0 * 1.0 = 150.0
        
        Equipment neutralEquipment = createTestEquipment(1L, "Neutral Item", EquipmentType.WEAPON, neutralMultiplier);
        Map<EquipmentType, Equipment> equipmentMap = new EnumMap<>(EquipmentType.class);
        equipmentMap.put(EquipmentType.WEAPON, neutralEquipment);
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(baseResult);
        when(equipmentService.getFullEquipmentSet(testUser.getID())).thenReturn(equipmentMap);

        // Act
        double result = equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(baseStrategy).calculateExperience(baseExperience, testUser);
        verify(equipmentService).getFullEquipmentSet(testUser.getID());
    }

    @Test
    void calculateExperience_shouldHandleEquipmentWithReducingMultiplier() {
        // Arrange
        double baseExperience = 100.0;
        double baseResult = 150.0;
        double reducingMultiplier = 0.8; // Equipment that reduces experience
        double expectedResult = baseResult * reducingMultiplier; // 150.0 * 0.8 = 120.0
        
        Equipment reducingEquipment = createTestEquipment(1L, "Cursed Item", EquipmentType.WEAPON, reducingMultiplier);
        Map<EquipmentType, Equipment> equipmentMap = new EnumMap<>(EquipmentType.class);
        equipmentMap.put(EquipmentType.WEAPON, reducingEquipment);
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(baseResult);
        when(equipmentService.getFullEquipmentSet(testUser.getID())).thenReturn(equipmentMap);

        // Act
        double result = equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        assertThat(result).isEqualTo(expectedResult);
        verify(baseStrategy).calculateExperience(baseExperience, testUser);
        verify(equipmentService).getFullEquipmentSet(testUser.getID());
    }

    @Test
    void calculateExperience_shouldVerifyEquipmentServiceIsCalledWithCorrectUserId() {
        // Arrange
        double baseExperience = 100.0;
        double baseResult = 150.0;
        Long expectedUserId = testUser.getID();
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(baseResult);
        when(equipmentService.getFullEquipmentSet(expectedUserId)).thenReturn(new EnumMap<>(EquipmentType.class));

        // Act
        equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        verify(equipmentService).getFullEquipmentSet(expectedUserId);
    }

    @Test
    void calculateExperience_shouldVerifyEquipmentMultipliersAreRetrievedCorrectly() {
        // Arrange
        double baseExperience = 100.0;
        double baseResult = 150.0;
        
        Equipment weapon = createTestEquipment(1L, "Test Sword", EquipmentType.WEAPON, 1.5);
        Equipment armor = createTestEquipment(2L, "Test Armor", EquipmentType.ARMOR, 1.2);
        
        Map<EquipmentType, Equipment> equipmentMap = new EnumMap<>(EquipmentType.class);
        equipmentMap.put(EquipmentType.WEAPON, weapon);
        equipmentMap.put(EquipmentType.ARMOR, armor);
        
        when(baseStrategy.calculateExperience(baseExperience, testUser)).thenReturn(baseResult);
        when(equipmentService.getFullEquipmentSet(testUser.getID())).thenReturn(equipmentMap);

        // Act
        double result = equipmentBoostedStrategy.calculateExperience(baseExperience, testUser);

        // Assert
        // Verify that the multipliers were correctly applied
        double expectedResult = baseResult * 1.5 * 1.2; // 150.0 * 1.5 * 1.2 = 270.0
        assertThat(result).isEqualTo(expectedResult);
        
        // Verify that equipment service was called
        verify(equipmentService).getFullEquipmentSet(testUser.getID());
        
        // Verify that the equipment objects returned have the correct multipliers
        assertThat(weapon.getExperienceMultiplier()).isEqualTo(1.5);
        assertThat(armor.getExperienceMultiplier()).isEqualTo(1.2);
    }

    /**
     * Helper method to create test equipment with specified properties
     */
    private Equipment createTestEquipment(Long id, String name, EquipmentType type, double experienceMultiplier) {
        Equipment equipment = new Equipment();
        equipment.setId(id);
        equipment.setName(name);
        equipment.setType(type);
        equipment.setExperienceMultiplier(experienceMultiplier);
        equipment.setState(Equipment.EquipmentState.ACTIVE);
        return equipment;
    }
}