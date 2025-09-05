@echo off
REM Script per eseguire i test con Maven Daemon (mvnd)
REM Assicurarsi che mvnd sia installato e disponibile nel PATH

echo ========================================
echo   FitTracker - Esecuzione Test con mvnd
echo ========================================
echo.

REM Verifica se mvnd è disponibile
where mvnd >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERRORE: mvnd non trovato nel PATH!
    echo.
    echo Per installare mvnd:
    echo 1. Scaricare da: https://github.com/apache/maven-mvnd/releases
    echo 2. Estrarre in una cartella
    echo 3. Aggiungere la cartella bin al PATH
    echo.
    pause
    exit /b 1
)

echo mvnd trovato, esecuzione test in corso...
echo.

REM Esegui i test con mvnd utilizzando il profilo specifico
mvnd clean test -Pmvnd-test -Dmvnd=true

REM Verifica il risultato
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo   Test completati con successo!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo   Test falliti! Codice errore: %ERRORLEVEL%
    echo ========================================
)

echo.
pause