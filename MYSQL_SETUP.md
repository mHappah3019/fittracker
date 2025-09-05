# Configurazione MySQL per FitTracker

## Prerequisiti

1. **MySQL Server** installato e in esecuzione
   - Versione consigliata: MySQL 8.0 o superiore
   - Download: https://dev.mysql.com/downloads/mysql/

2. **Maven Daemon (mvnd)** per esecuzione test ottimizzata
   - Download: https://github.com/apache/maven-mvnd/releases
   - Estrarre e aggiungere al PATH

## Configurazione Database

### 1. Configurazione Automatica
L'applicazione è configurata per creare automaticamente il database `fittracker` al primo avvio.

### 2. Configurazione Manuale (Opzionale)
Se preferisci configurare manualmente il database:

```sql
-- Eseguire come root MySQL
mysql -u root -p < mysql_setup.sql
```

### 3. Configurazione Credenziali
Modifica il file `src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=tuapassword
```

**Importante**: Cambia la password predefinita con quella del tuo MySQL!

## Esecuzione Applicazione

### Compilazione
```bash
mvn clean compile
```

### Esecuzione
```bash
mvn spring-boot:run
```

oppure

```bash
mvn javafx:run
```

## Esecuzione Test

### Con Maven standard
```bash
mvn test
```

### Con Maven Daemon (Consigliato)
```bash
# Windows
run-tests-mvnd.bat

# Manuale
mvnd clean test -Pmvnd-test -Dmvnd=true
```

## Vantaggi MySQL vs SQLite

### Performance
- **Connessioni concorrenti**: MySQL gestisce meglio più connessioni simultanee
- **Transazioni**: Supporto ACID completo con isolamento configurabile
- **Indicizzazione**: Algoritmi di indicizzazione più avanzati

### Scalabilità
- **Dimensioni database**: Nessun limite pratico (SQLite limitato a ~281 TB)
- **Utenti concorrenti**: Supporto nativo per migliaia di connessioni
- **Replicazione**: Supporto master-slave per backup e distribuzione

### Funzionalità Enterprise
- **Backup**: Strumenti di backup incrementale e point-in-time recovery
- **Monitoraggio**: MySQL Workbench e strumenti di monitoring avanzati
- **Sicurezza**: Autenticazione avanzata, SSL, crittografia a riposo

## Configurazioni Avanzate

### Pool di Connessioni (HikariCP)
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
```

### Ottimizzazioni JPA
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

## Troubleshooting

### Errore di Connessione
1. Verificare che MySQL sia in esecuzione
2. Controllare username/password in application.properties
3. Verificare che la porta 3306 sia aperta

### Errore "Public Key Retrieval"
Aggiungere `allowPublicKeyRetrieval=true` all'URL di connessione (già configurato).

### Errore Timezone
L'URL include già `serverTimezone=UTC` per evitare problemi di timezone.

## Migrazione da SQLite

Se stai migrando da SQLite:
1. I dati esistenti in SQLite non verranno migrati automaticamente
2. Le tabelle verranno ricreate in MySQL al primo avvio
3. Considera l'esportazione/importazione dei dati se necessario