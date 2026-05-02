# Control Finance Android App Guidelines

This document outlines key conventions, architecture, and commands for the Control Finance Android application.

## Project Overview
- **Type:** Personal Finance Management Application
- **Platform:** Android
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose
- **Persistence:** SQLite (via Room Persistence Library)

## Core Features
1.  **Income & Expense Registration:** Record financial transactions.
2.  **Global Balance Calculation:** Calculate overall income vs. expenses.
3.  **Visual Dashboard:** Display financial overview with good visualization.
4.  **Monthly Transaction History:** View all entries for a given month, with automatic date capture from the device.
5.  **Monthly Reports:** Generate summaries of monthly financial activity.

## Architecture: MVVM (Model-View-ViewModel)
The application follows the MVVM architectural pattern for clean, modular, and scalable code.

-   **View (Jetpack Compose):**
    -   `MainActivity.kt` as the entry point.
    -   Feature-specific Composables located within `com.example.controlfinance.ui` and its sub-packages (e.g., `ui.home`, `ui.transactions`, `ui.reports`).
    -   Observes `ViewModel` for UI state updates.
-   **ViewModel:**
    -   Manages UI-related data and business logic.
    -   Communicates with the `Repository`.
    -   Located in `com.example.controlfinance.viewmodel` or `com.example.controlfinance.presentation.viewmodel`.
-   **Repository:**
    -   Abstracts data sources (e.g., Room Database, API).
    -   Provides data to `ViewModel`s.
    -   Handles data fetching, caching, and conflict resolution.
    -   Located in `com.example.controlfinance.data.repository`.
-   **Model:**
    -   POJOs/data classes representing financial entities (e.g., `Transaction`, `Account`, `Category`).
    -   Defined as Room `Entity` classes for database persistence.
    -   Located in `com.example.controlfinance.data.model`.

## Data Persistence: Room Persistence Library
-   **Entities:** Data classes annotated with `@Entity` (e.g., `TransactionEntity`).
-   **DAOs (Data Access Objects):** Interfaces with `@Dao` annotations for defining database operations (e.g., `TransactionDao`).
-   **RoomDatabase:** Abstract class inheriting from `RoomDatabase` to hold the database and DAOs (e.g., `AppDatabase`).

## Project Conventions & Rules
-   **Code Quality:** Adhere to Kotlin coding conventions, clean code principles, and modular design.
-   **Basic Data Security:** Implement basic security measures for sensitive financial data (e.g., input validation, avoiding logging sensitive data, considering encrypted storage if needed for future enhancements).
-   **Automatic Date Capture:** When recording transactions, automatically capture the current date from the device.

## Developer Commands
-   **Build Application:** `./gradlew assembleDebug`
-   **Run All Unit/Instrumentation Tests:** `./gradlew test` (for unit tests) and `./gradlew connectedAndroidTest` (for instrumentation tests).
-   **Run Specific Test:** `./gradlew :app:testDebugUnitTest --tests "com.example.controlfinance.data.repository.TransactionRepositoryTest"` (example for a unit test)
-   **Clean Project:** `./gradlew clean`
-   **Check Lint Issues:** `./gradlew lint`
