package ingsoftware.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

/**
 * Classe base per tutti gli Integration Test.
 * Configura l'ambiente di test con database H2 in-memory e profilo di test.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public abstract class BaseIntegrationTest {

    /**
     * Setup comune per tutti i test di integrazione.
     * Può essere sovrascritto dalle classi figlie per setup specifici.
     */
    @BeforeEach
    void baseSetUp() {
        // Setup comune per tutti i test
        // Le classi figlie possono aggiungere setup specifici
    }

    /**
     * Metodo di utilità per creare dati di test comuni.
     * Può essere utilizzato dalle classi figlie.
     */
    protected void setupCommonTestData() {
        // Implementazione per setup dati comuni se necessario
    }
}