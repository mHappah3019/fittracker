-- Script per configurare il database MySQL per FitTracker
-- Eseguire questo script come utente root di MySQL

-- 1. Crea il database se non esiste
CREATE DATABASE IF NOT EXISTS fittracker 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. Crea un utente dedicato per l'applicazione (opzionale, per maggiore sicurezza)
-- CREATE USER IF NOT EXISTS 'fittracker_user'@'localhost' IDENTIFIED BY 'fittracker_password';
-- GRANT ALL PRIVILEGES ON fittracker.* TO 'fittracker_user'@'localhost';

-- 3. Usa il database
USE fittracker;

-- 4. Verifica che il database sia stato creato correttamente
SHOW TABLES;

-- 5. Verifica la configurazione del charset
SELECT DEFAULT_CHARACTER_SET_NAME, DEFAULT_COLLATION_NAME 
FROM information_schema.SCHEMATA 
WHERE SCHEMA_NAME = 'fittracker';

-- Note:
-- - Il database verrà popolato automaticamente da Hibernate al primo avvio dell'applicazione
-- - Le tabelle verranno create automaticamente grazie alla configurazione spring.jpa.hibernate.ddl-auto=update
-- - Per utilizzare un utente dedicato, decommentare le righe sopra e aggiornare application.properties