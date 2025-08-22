package ingsoftware.service.mediator;

import ingsoftware.model.DTO.CompletionResultDTO;

/**
 * Mediator per gestire le operazioni post-completamento di un'abitudine.
 * Coordina UI updates, achievement checking, analytics e summary updates.
 */
public interface PostCompletionMediator {
    
    /**
     * Gestisce tutte le operazioni che devono essere eseguite dopo 
     * il completamento di un'abitudine.
     * 
     * @param completion DTO contenente i risultati del completamento
     * @throws IllegalArgumentException se completion è null
     */
    void handlePostCompletion(CompletionResultDTO completion);
}