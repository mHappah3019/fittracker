package ingsoftware.service;

import ingsoftware.model.Equipment;
import ingsoftware.model.EquipmentType;
import ingsoftware.model.User;
import ingsoftware.repository.EquipmentRepository;
import ingsoftware.repository.UserRepository;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private Equipment testEquipment;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testEquipment = new Equipment();
        testEquipment.setId(1L);
        testEquipment.setName("Test Equipment");
        testEquipment.setType(EquipmentType.WEAPON);
        testEquipment.setState(Equipment.EquipmentState.INACTIVE);
        
        testUser = new User();
        testUser.setId(1L);
    }

    @Test
    void getAllEquipmentGroupedByType_shouldReturnEquipmentGroupedByType_includingNoneOptions() {
        // Arrange
        Equipment weapon = new Equipment();
        weapon.setId(1L);
        weapon.setName("Sword");
        weapon.setType(EquipmentType.WEAPON);

        Equipment armor = new Equipment();
        armor.setId(2L);
        armor.setName("Shield");
        armor.setType(EquipmentType.ARMOR);

        when(equipmentRepository.findAll()).thenReturn(Arrays.asList(weapon, armor));

        // Act
        Map<EquipmentType, ObservableList<Equipment>> result = equipmentService.getAllEquipmentGroupedByType();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(EquipmentType.values().length);
        
        // Check that each type has at least the "None" option
        for (EquipmentType type : EquipmentType.values()) {
            assertThat(result.get(type)).isNotNull();
            assertThat(result.get(type)).isNotEmpty();
            
            // Check for "None" option
            boolean hasNoneOption = result.get(type).stream()
                    .anyMatch(eq -> eq.getName().startsWith("Nessun"));
            assertThat(hasNoneOption).isTrue();
        }
    }

    @Test
    void findAllEquippedByUser_shouldReturnActiveEquipmentByType() {
        // Arrange
        Long userId = 1L;
        
        Equipment activeWeapon = new Equipment();
        activeWeapon.setId(1L);
        activeWeapon.setType(EquipmentType.WEAPON);
        activeWeapon.setState(Equipment.EquipmentState.ACTIVE);

        Equipment activeArmor = new Equipment();
        activeArmor.setId(2L);
        activeArmor.setType(EquipmentType.ARMOR);
        activeArmor.setState(Equipment.EquipmentState.ACTIVE);

        when(equipmentRepository.findByStateEquals(Equipment.EquipmentState.ACTIVE))
                .thenReturn(Arrays.asList(activeWeapon, activeArmor));

        // Act
        Map<EquipmentType, Equipment> result = equipmentService.findAllEquippedByUser(userId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(EquipmentType.WEAPON)).isEqualTo(activeWeapon);
        assertThat(result.get(EquipmentType.ARMOR)).isEqualTo(activeArmor);
    }

    @Test
    void findAllEquippedByUser_shouldHandleEmptyResult() {
        // Arrange
        Long userId = 1L;
        when(equipmentRepository.findByStateEquals(Equipment.EquipmentState.ACTIVE))
                .thenReturn(Collections.emptyList());

        // Act
        Map<EquipmentType, Equipment> result = equipmentService.findAllEquippedByUser(userId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findAllEquippedByUser_shouldFilterEquipmentWithoutType() {
        // Arrange
        Long userId = 1L;
        
        Equipment equipmentWithType = new Equipment();
        equipmentWithType.setId(1L);
        equipmentWithType.setType(EquipmentType.WEAPON);
        equipmentWithType.setState(Equipment.EquipmentState.ACTIVE);

        Equipment equipmentWithoutType = new Equipment();
        equipmentWithoutType.setId(2L);
        equipmentWithoutType.setState(Equipment.EquipmentState.ACTIVE);
        // type is not set (Optional.empty())

        when(equipmentRepository.findByStateEquals(Equipment.EquipmentState.ACTIVE))
                .thenReturn(Arrays.asList(equipmentWithType, equipmentWithoutType));

        // Act
        Map<EquipmentType, Equipment> result = equipmentService.findAllEquippedByUser(userId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(EquipmentType.WEAPON)).isEqualTo(equipmentWithType);
    }

    @Test
    void equip_shouldEquipItem_andUnequipSameType() {
        // Arrange
        Long userId = 1L;
        Long equipmentId = 1L;
        
        Equipment newEquipment = new Equipment();
        newEquipment.setId(equipmentId);
        newEquipment.setType(EquipmentType.WEAPON);
        newEquipment.setState(Equipment.EquipmentState.INACTIVE);

        Equipment oldEquipment = new Equipment();
        oldEquipment.setId(2L);
        oldEquipment.setType(EquipmentType.WEAPON);
        oldEquipment.setState(Equipment.EquipmentState.ACTIVE);

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(newEquipment));
        when(equipmentRepository.findByTypeAndState(EquipmentType.WEAPON, Equipment.EquipmentState.ACTIVE))
                .thenReturn(Optional.of(oldEquipment));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Equipment result = equipmentService.equip(userId, equipmentId);

        // Assert
        assertThat(result.getState()).isEqualTo(Equipment.EquipmentState.ACTIVE);
        verify(equipmentRepository).save(oldEquipment); // Should save unequipped item
        verify(equipmentRepository).save(newEquipment); // Should save equipped item
        assertThat(oldEquipment.getState()).isEqualTo(Equipment.EquipmentState.INACTIVE);
    }

    @Test
    void equip_shouldThrowException_whenEquipmentNotFound() {
        // Arrange
        Long userId = 1L;
        Long equipmentId = 999L;
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> equipmentService.equip(userId, equipmentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Equipaggiamento non trovato");
    }

    @Test
    void equip_shouldThrowException_whenEquipmentHasNoType() {
        // Arrange
        Long userId = 1L;
        Long equipmentId = 1L;
        
        Equipment equipmentWithoutType = new Equipment();
        equipmentWithoutType.setId(equipmentId);
        equipmentWithoutType.setState(Equipment.EquipmentState.INACTIVE);
        // type is not set

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipmentWithoutType));

        // Act & Assert
        assertThatThrownBy(() -> equipmentService.equip(userId, equipmentId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("L'equipaggiamento deve avere un tipo per essere equipaggiato");
    }

    @Test
    void unequip_shouldUnequipActiveEquipment() {
        // Arrange
        Long userId = 1L;
        EquipmentType type = EquipmentType.WEAPON;
        
        Equipment activeEquipment = new Equipment();
        activeEquipment.setId(1L);
        activeEquipment.setType(type);
        activeEquipment.setState(Equipment.EquipmentState.ACTIVE);

        when(equipmentRepository.findByTypeAndState(type, Equipment.EquipmentState.ACTIVE))
                .thenReturn(Optional.of(activeEquipment));
        when(equipmentRepository.save(any(Equipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        equipmentService.unequip(userId, type);

        // Assert
        assertThat(activeEquipment.getState()).isEqualTo(Equipment.EquipmentState.INACTIVE);
        verify(equipmentRepository).save(activeEquipment);
    }

    @Test
    void unequip_shouldDoNothing_whenNoActiveEquipmentOfType() {
        // Arrange
        Long userId = 1L;
        EquipmentType type = EquipmentType.WEAPON;
        
        when(equipmentRepository.findByTypeAndState(type, Equipment.EquipmentState.ACTIVE))
                .thenReturn(Optional.empty());

        // Act
        equipmentService.unequip(userId, type);

        // Assert
        verify(equipmentRepository, never()).save(any(Equipment.class));
    }

    @Test
    void refreshCache_shouldClearCacheAndReloadFromRepository() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(equipmentRepository.findByStateEquals(Equipment.EquipmentState.ACTIVE))
                .thenReturn(Collections.emptyList());

        // Act
        equipmentService.refreshCache();

        // Assert
        verify(userRepository).findById(1L);
        verify(equipmentRepository).findByStateEquals(Equipment.EquipmentState.ACTIVE);
    }

    @Test
    void findEquippedByUserAndType_shouldReturnEquipmentFromCache() {
        // This test verifies the cache functionality, but since the cache is private,
        // we need to setup the cache first through refreshCache
        
        // Arrange
        Long userId = 1L;
        EquipmentType type = EquipmentType.WEAPON;
        
        Equipment cachedEquipment = new Equipment();
        cachedEquipment.setId(1L);
        cachedEquipment.setType(type);
        cachedEquipment.setState(Equipment.EquipmentState.ACTIVE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(equipmentRepository.findByStateEquals(Equipment.EquipmentState.ACTIVE))
                .thenReturn(Arrays.asList(cachedEquipment));

        // Setup cache
        equipmentService.refreshCache();
        
        // Act
        Optional<Equipment> result = equipmentService.findEquippedByUserAndType(userId, type);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(cachedEquipment);
    }

    @Test
    void findEquippedByUserAndType_shouldReturnEmpty_whenNotInCache() {
        // Arrange
        Long userId = 1L;
        EquipmentType type = EquipmentType.WEAPON;
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(equipmentRepository.findByStateEquals(Equipment.EquipmentState.ACTIVE))
                .thenReturn(Collections.emptyList());

        // Setup empty cache
        equipmentService.refreshCache();
        
        // Act
        Optional<Equipment> result = equipmentService.findEquippedByUserAndType(userId, type);

        // Assert
        assertThat(result).isEmpty();
    }
}