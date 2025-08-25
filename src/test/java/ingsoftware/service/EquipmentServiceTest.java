package ingsoftware.service;

import ingsoftware.model.Equipment;
import ingsoftware.model.UserEquipment;
import ingsoftware.model.enum_helpers.EquipmentType;
import ingsoftware.model.User;
import ingsoftware.repository.EquipmentRepository;
import ingsoftware.repository.UserRepository;
import ingsoftware.repository.UserEquipmentRepository;
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
    
    @Mock
    private UserEquipmentRepository userEquipmentRepository;

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
        weapon.setAvailable(true);

        Equipment armor = new Equipment();
        armor.setId(2L);
        armor.setName("Shield");
        armor.setType(EquipmentType.ARMOR);
        armor.setAvailable(true);

        when(equipmentRepository.findByAvailableTrue()).thenReturn(Arrays.asList(weapon, armor));

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

        Equipment activeArmor = new Equipment();
        activeArmor.setId(2L);
        activeArmor.setType(EquipmentType.ARMOR);

        UserEquipment userWeapon = new UserEquipment(userId, 1L, true);
        UserEquipment userArmor = new UserEquipment(userId, 2L, true);

        when(userEquipmentRepository.findByUserIdAndEquippedTrue(userId))
                .thenReturn(Arrays.asList(userWeapon, userArmor));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(activeWeapon));
        when(equipmentRepository.findById(2L)).thenReturn(Optional.of(activeArmor));

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
        when(userEquipmentRepository.findByUserIdAndEquippedTrue(userId))
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

        Equipment equipmentWithoutType = new Equipment();
        equipmentWithoutType.setId(2L);
        // type is not set (Optional.empty())

        UserEquipment userEquipment1 = new UserEquipment(userId, 1L, true);
        UserEquipment userEquipment2 = new UserEquipment(userId, 2L, true);

        when(userEquipmentRepository.findByUserIdAndEquippedTrue(userId))
                .thenReturn(Arrays.asList(userEquipment1, userEquipment2));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipmentWithType));
        when(equipmentRepository.findById(2L)).thenReturn(Optional.of(equipmentWithoutType));

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

        UserEquipment userEquipment = new UserEquipment(userId, equipmentId, false);
        UserEquipment oldUserEquipment = new UserEquipment(userId, 2L, true);

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(newEquipment));
        when(userEquipmentRepository.findByUserIdAndEquipmentId(userId, equipmentId))
                .thenReturn(Optional.of(userEquipment));
        when(userEquipmentRepository.findEquippedByUserIdAndType(userId, EquipmentType.WEAPON))
                .thenReturn(Optional.of(oldUserEquipment));
        when(userEquipmentRepository.save(any(UserEquipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Equipment result = equipmentService.equip(userId, equipmentId);

        // Assert
        assertThat(result).isEqualTo(newEquipment);
        verify(userEquipmentRepository).save(oldUserEquipment); // Should save unequipped item
        verify(userEquipmentRepository).save(userEquipment); // Should save equipped item
        assertThat(userEquipment.isEquipped()).isTrue();
        assertThat(oldUserEquipment.isEquipped()).isFalse();
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
    void equip_shouldThrowException_whenUserDoesNotOwnEquipment() {
        // Arrange
        Long userId = 1L;
        Long equipmentId = 1L;
        
        Equipment equipment = new Equipment();
        equipment.setId(equipmentId);
        equipment.setType(EquipmentType.WEAPON);
        
        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipment));
        when(userEquipmentRepository.findByUserIdAndEquipmentId(userId, equipmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> equipmentService.equip(userId, equipmentId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("L'utente non possiede questo equipaggiamento");
    }

    @Test
    void equip_shouldThrowException_whenEquipmentHasNoType() {
        // Arrange
        Long userId = 1L;
        Long equipmentId = 1L;
        
        Equipment equipmentWithoutType = new Equipment();
        equipmentWithoutType.setId(equipmentId);
        // type is not set

        UserEquipment userEquipment = new UserEquipment(userId, equipmentId, false);

        when(equipmentRepository.findById(equipmentId)).thenReturn(Optional.of(equipmentWithoutType));
        when(userEquipmentRepository.findByUserIdAndEquipmentId(userId, equipmentId))
                .thenReturn(Optional.of(userEquipment));

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
        
        UserEquipment equippedUserEquipment = new UserEquipment(userId, 1L, true);

        when(userEquipmentRepository.findEquippedByUserIdAndType(userId, type))
                .thenReturn(Optional.of(equippedUserEquipment));
        when(userEquipmentRepository.save(any(UserEquipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        equipmentService.unequip(userId, type);

        // Assert
        assertThat(equippedUserEquipment.isEquipped()).isFalse();
        verify(userEquipmentRepository).save(equippedUserEquipment);
    }

    @Test
    void unequip_shouldDoNothing_whenNoActiveEquipmentOfType() {
        // Arrange
        Long userId = 1L;
        EquipmentType type = EquipmentType.WEAPON;
        
        when(userEquipmentRepository.findEquippedByUserIdAndType(userId, type))
                .thenReturn(Optional.empty());

        // Act
        equipmentService.unequip(userId, type);

        // Assert
        verify(userEquipmentRepository, never()).save(any(UserEquipment.class));
    }

    @Test
    void refreshCache_shouldClearCacheAndReloadFromRepository() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userEquipmentRepository.findByUserIdAndEquippedTrue(1L))
                .thenReturn(Collections.emptyList());

        // Act
        equipmentService.refreshCache();

        // Assert
        verify(userRepository).findById(1L);
        verify(userEquipmentRepository).findByUserIdAndEquippedTrue(1L);
    }

    @Test
    void findEquippedByUserAndType_shouldReturnEquipment() {
        // Arrange
        Long userId = 1L;
        EquipmentType type = EquipmentType.WEAPON;
        
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setType(type);

        UserEquipment userEquipment = new UserEquipment(userId, 1L, true);

        when(userEquipmentRepository.findEquippedByUserIdAndType(userId, type))
                .thenReturn(Optional.of(userEquipment));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        
        // Act
        Optional<Equipment> result = equipmentService.findEquippedByUserAndType(userId, type);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(equipment);
    }

    @Test
    void findEquippedByUserAndType_shouldReturnEmpty_whenNotEquipped() {
        // Arrange
        Long userId = 1L;
        EquipmentType type = EquipmentType.WEAPON;
        
        when(userEquipmentRepository.findEquippedByUserIdAndType(userId, type))
                .thenReturn(Optional.empty());
        
        // Act
        Optional<Equipment> result = equipmentService.findEquippedByUserAndType(userId, type);

        // Assert
        assertThat(result).isEmpty();
    }
}