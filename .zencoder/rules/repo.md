---
description: Repository Information Overview
alwaysApply: true
---

# Habit Tracker Information

## Summary
A gamified habit tracking application built with JavaFX and Spring Boot. The application helps users track their habits with a gamification approach, including features like equipment, life points, and habit completion tracking.

## Structure
- **src/main/java/ingsoftware**: Core application code
  - **model**: Domain entities (User, Habit, Equipment, etc.)
  - **service**: Business logic and services
  - **controller**: JavaFX controllers for UI
  - **repository**: Data access layer
  - **config**: Application configuration
  - **exception**: Custom exceptions
- **src/main/resources**: Application resources and FXML files
- **src/test**: Test classes mirroring the main structure

## Language & Runtime
**Language**: Java
**Version**: Java 21 (with preview features enabled)
**Build System**: Maven
**Package Manager**: Maven
**Framework**: Spring Boot 3.2.0 with JavaFX 21.0.7

## Dependencies
**Main Dependencies**:
- Spring Boot Starter (core, data-jpa, web, validation)
- JavaFX (controls, fxml) 21.0.7
- SQLite JDBC 3.45.1.0
- Hibernate Community Dialects 6.3.1.Final
- JavaFX Weaver Spring Boot Starter 2.0.1
- Jakarta Persistence API 3.1.0

**Development Dependencies**:
- Spring Boot Starter Test
- TestFX JUnit5 4.0.18
- H2 Database (runtime scope)

## Build & Installation
```bash
# Compile the project
mvn clean compile

# Package the application
mvn package

# Run the application
mvn spring-boot:run

# Run with JavaFX plugin
mvn javafx:run
```

## Database
**Type**: SQLite
**Configuration**: 
- URL: jdbc:sqlite:./fittracker.db
- Hibernate DDL: update
- Dialect: SQLiteDialect

## Testing
**Framework**: JUnit 5 with Spring Boot Test
**Test Location**: src/test/java/ingsoftware
**Configuration**: Maven Surefire Plugin with JavaFX-specific arguments
**Run Command**:
```bash
mvn test
```

## Application Architecture
**Pattern**: MVC with Spring Boot
**Main Components**:
- **Models**: Domain entities (User, Habit, Equipment)
- **Services**: Business logic implementation
- **Controllers**: JavaFX UI controllers
- **Repositories**: Data access interfaces
- **Main Application**: HabitTrackerApplication (JavaFX + Spring Boot)