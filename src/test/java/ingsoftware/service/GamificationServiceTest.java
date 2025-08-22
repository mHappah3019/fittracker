package ingsoftware.service;

import ingsoftware.model.Habit;
import ingsoftware.model.User;
import ingsoftware.repository.UserRepository;
import ingsoftware.service.strategy.ExperienceStrategyFactory;
import ingsoftware.service.strategy.GamificationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

class GamificationServiceTest {

    @InjectMocks
    private GamificationService gamificationService;

    @Mock
    private HabitService habitService;

    @Mock
    private LifePointCalculator calculator;

    @Mock
    private ExperienceStrategyFactory strategyFactory;

    @Mock
    private GamificationStrategy strategy;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(strategyFactory.createStrategy()).thenReturn(strategy);
        
        // Create a test user
        user = new User();
        user.setId(1L);
        user.setLevel(3);
        user.setTotalXp(250);
        user.setLastAccessDate(LocalDate.now().minusDays(1));
    }

    @Test
    void testCheckAndHandleLifePointsDepletion_WhenLifePointsZero_ShouldDecreaseLevel() {
        // Arrange
        user.addLifePoints(-100); // Set life points to 0
        
        // Act
        boolean result = gamificationService.checkAndHandleLifePointsDepletion(user);
        
        // Assert
        assertTrue(result);
        assertEquals(2, user.getLevel());
        assertEquals(50, user.getLifePoints());
    }
    
    @Test
    void testCheckAndHandleLifePointsDepletion_WhenLifePointsNegative_ShouldDecreaseLevel() {
        // Arrange
        user.addLifePoints(-110); // Set life points to -10, but User class limits to 0
        
        // Act
        boolean result = gamificationService.checkAndHandleLifePointsDepletion(user);
        
        // Assert
        assertTrue(result);
        assertEquals(2, user.getLevel());
        assertEquals(50, user.getLifePoints());
    }
    
    @Test
    void testCheckAndHandleLifePointsDepletion_WhenLifePointsPositive_ShouldNotDecreaseLevel() {
        // Arrange
        user.addLifePoints(0); // Life points remain at 100
        
        // Act
        boolean result = gamificationService.checkAndHandleLifePointsDepletion(user);
        
        // Assert
        assertFalse(result);
        assertEquals(3, user.getLevel());
        assertEquals(100, user.getLifePoints());
    }
    
    @Test
    void testCheckAndHandleLifePointsDepletion_WhenLevelOne_ShouldNotDecreaseLevel() {
        // Arrange
        user.setLevel(1);
        user.addLifePoints(-100); // Set life points to 0
        
        // Act
        boolean result = gamificationService.checkAndHandleLifePointsDepletion(user);
        
        // Assert
        assertFalse(result);
        assertEquals(1, user.getLevel()); // Level should remain at 1
        assertEquals(0, user.getLifePoints()); // Life points should remain at 0
    }
}