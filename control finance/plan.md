# Plan de trabajo - Capa de datos SQLite nativa

## Objetivo
Implementar la base de datos local de la app de finanzas personales usando SQLite integrado de Android (sin Room/DAO), con CRUD completo para ingresos, gastos y categorias.

## Alcance funcional
- Registrar transacciones de tipo ingreso y gasto.
- Calcular balance global con base en transacciones almacenadas.
- Consultar historial mensual filtrado por fecha.
- Preparar datos para dashboard y reportes mensuales.

## Diseno de datos
- Tabla `categories`:
  - `id` INTEGER PRIMARY KEY AUTOINCREMENT
  - `name` TEXT NOT NULL
  - `type` TEXT NOT NULL CHECK(type IN ('income','expense'))
  - `created_at` INTEGER NOT NULL
- Tabla `transactions`:
  - `id` INTEGER PRIMARY KEY AUTOINCREMENT
  - `amount` REAL NOT NULL CHECK(amount > 0)
  - `type` TEXT NOT NULL CHECK(type IN ('income','expense'))
  - `description` TEXT
  - `category_id` INTEGER NOT NULL
  - `transaction_date` INTEGER NOT NULL
  - `created_at` INTEGER NOT NULL
  - FOREIGN KEY(`category_id`) REFERENCES `categories`(`id`) ON DELETE RESTRICT

## Arquitectura (MVVM)
- `data/model`: entidades Kotlin (`Category`, `Transaction`) y tipo `TransactionType`.
- `data/local`: `FinanceDatabaseHelper` (extiende `SQLiteOpenHelper`).
- `data/repository`: repositorios que encapsulan CRUD y consultas (`CategoryRepository`, `TransactionRepository`).
- `viewmodel`: consumira repositorios para exponer estado a UI Compose.

## Pasos de implementacion
1. Crear modelos de dominio para categorias y transacciones.
2. Implementar `FinanceDatabaseHelper` con:
   - creacion de tablas,
   - migraciones basicas (`onUpgrade`),
   - habilitacion de foreign keys.
3. Implementar CRUD de `categories`:
   - crear,
   - obtener por id,
   - listar por tipo,
   - actualizar,
   - eliminar validando integridad referencial.
4. Implementar CRUD de `transactions`:
   - crear (fecha automatica del dispositivo),
   - obtener por id,
   - actualizar,
   - eliminar,
   - listar por rango mensual.
5. Implementar consultas agregadas:
   - total ingresos,
   - total gastos,
   - balance global,
   - resumen mensual para reportes.
6. Verificar compilacion y consistencia de consultas SQL.

## Reglas de calidad y seguridad basica
- Usar consultas parametrizadas (`?`) para evitar SQL injection.
- Validar entrada de datos (monto positivo, tipo valido, categoria existente).
- No registrar logs con informacion financiera sensible.
- Encapsular acceso a DB en repositorios para mantener modularidad.

## Verificacion
- Build: `./gradlew assembleDebug`
- Unit tests: `./gradlew test`
- Lint: `./gradlew lint`
