package ingsoftware.service.mediator;

/**
 * Mediator per gestire le operazioni di avvio dell'applicazione.
 * Coordina le interazioni tra servizi di gamification, utente e UI.
 */
public interface StartupMediator {
    
    /**
     * Gestisce l'avvio dell'applicazione per un utente specifico.
     * 
     * @param userId ID dell'utente che sta avviando l'applicazione
     * @throws IllegalArgumentException se userId è null
     */
    void handleApplicationStartup(Long userId);
}