package ingsoftware.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Classe di utilità per la gestione dei messaggi di alert nell'interfaccia utente.
 * Fornisce metodi statici per mostrare diversi tipi di messaggi all'utente.
 */
public class AlertHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertHelper.class);
    
    /**
     * Mostra un alert di errore all'utente.
     * 
     * @param title Il titolo dell'alert
     * @param message Il messaggio da mostrare
     */
    public static void showErrorAlert(String title, String message) {
        logger.debug("Mostrando alert di errore: {} - {}", title, message);
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Mostra un alert di errore con titolo predefinito.
     * 
     * @param message Il messaggio da mostrare
     */
    public static void showErrorAlert(String message) {
        showErrorAlert("Errore", message);
    }
    
    /**
     * Mostra un alert di avvertimento all'utente.
     * 
     * @param title Il titolo dell'alert
     * @param message Il messaggio da mostrare
     */
    public static void showWarningAlert(String title, String message) {
        logger.debug("Mostrando alert di avvertimento: {} - {}", title, message);
        
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Mostra un alert di avvertimento con titolo predefinito.
     * 
     * @param message Il messaggio da mostrare
     */
    public static void showWarningAlert(String message) {
        showWarningAlert("Attenzione", message);
    }
    
    /**
     * Mostra un alert di successo all'utente.
     * 
     * @param title Il titolo dell'alert
     * @param message Il messaggio da mostrare
     */
    public static void showSuccessAlert(String title, String message) {
        logger.debug("Mostrando alert di successo: {} - {}", title, message);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Mostra un alert di successo con titolo predefinito.
     * 
     * @param message Il messaggio da mostrare
     */
    public static void showSuccessAlert(String message) {
        showSuccessAlert("Successo", message);
    }
    
    /**
     * Mostra un alert di informazione all'utente.
     * 
     * @param title Il titolo dell'alert
     * @param message Il messaggio da mostrare
     */
    public static void showInfoAlert(String title, String message) {
        logger.debug("Mostrando alert informativo: {} - {}", title, message);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Mostra un alert di conferma all'utente.
     * 
     * @param title Il titolo dell'alert
     * @param message Il messaggio da mostrare
     * @return true se l'utente ha confermato, false altrimenti
     */
    public static boolean showConfirmationAlert(String title, String message) {
        logger.debug("Mostrando alert di conferma: {} - {}", title, message);
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initStyle(StageStyle.UTILITY);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Mostra un alert di conferma con titolo predefinito.
     * 
     * @param message Il messaggio da mostrare
     * @return true se l'utente ha confermato, false altrimenti
     */
    public static boolean showConfirmationAlert(String message) {
        return showConfirmationAlert("Conferma", message);
    }
}