# App Música

Reproductor de música local para Android con interfaz estilo streaming moderno, construido 100% con Kotlin y Jetpack Compose.  
Lee las canciones almacenadas en el dispositivo, permite reproducirlas con controles completos (play/pause, anterior, siguiente, barra de progreso) y ofrece gestión de playlists personalizadas.

---

## ¿Qué hace?

| Sección | Descripción |
|---|---|
| **Splash** | Animación de entrada (2 s) con ícono de nota musical pulsante. |
| **Inicio (Home)** | Lista todas las canciones del dispositivo ordenadas por título. Muestra el encabezado "¿Qué quieres escuchar hoy?". |
| **Búsqueda** | Búsqueda en tiempo real por título, artista, álbum o género. Muestra categorías de géneros cuando no hay consulta activa. |
| **Biblioteca** | Organiza la colección en tres pestañas: Playlists, Canciones y Artistas. Permite crear, ver, gestionar y eliminar playlists. |
| **Reproductor** | Vista de pantalla completa con arte de álbum simulado (efecto vinilo), barra de progreso deslizable, controles de reproducción, modo shuffle, modo repeat y botón de favorito. |
| **Mini-reproductor** | Barra persistente en la parte inferior que muestra la canción activa sin abandonar la pantalla actual. |

Las canciones se obtienen desde el `MediaStore` de Android (almacenamiento externo del dispositivo). No descarga ni transmite música desde internet.

---

## Requisitos

| Herramienta | Versión mínima |
|---|---|
| Android Studio | Hedgehog (2023.1.1) o superior |
| JDK | 11 |
| Kotlin | Incluido con Android Studio |
| Gradle | 8.x (wrapper incluido en el proyecto) |
| Android SDK — compileSdk | 36 |
| Android SDK — minSdk | 24 (Android 7.0) |
| Android SDK — targetSdk | 36 |

---

## Cómo ejecutar

### Desde Android Studio (recomendado)

1. Abre Android Studio.
2. **File → Open…** y selecciona la carpeta `app musica/`.
3. Espera a que Gradle sincronice las dependencias.
4. Conecta un dispositivo físico (Android 7.0+) o crea un AVD **con archivos de audio cargados**.
5. Pulsa **Run ▶** (o `Shift+F10`).

> **Nota sobre el emulador:** los AVD de Android Studio no incluyen archivos de audio por defecto. Para probar con música real, usa un dispositivo físico o carga archivos `.mp3`/`.flac` en el emulador mediante `adb push`.

```bash
# Ejemplo: copiar un archivo MP3 al almacenamiento del emulador
adb push cancion.mp3 /sdcard/Music/cancion.mp3
```

### Desde la terminal

```bash
# Entrar a la carpeta del proyecto (con comillas por el espacio en el nombre)
cd "app musica"

# Compilar y desplegar en un dispositivo/emulador conectado
./gradlew installDebug

# Solo compilar (genera el APK en app/build/outputs/apk/debug/)
./gradlew assembleDebug
```

---

## Permisos Android

La app solicita permisos en tiempo de ejecución al iniciar:

| Permiso | Android | Descripción |
|---|---|---|
| `READ_EXTERNAL_STORAGE` | ≤ Android 12 (API 32) | Leer archivos de audio del almacenamiento. |
| `READ_MEDIA_AUDIO` | ≥ Android 13 (API 33) | Leer archivos de audio (permiso granular). |

Si el usuario deniega el permiso, la app no cargará ninguna canción.

---

## Estructura del proyecto

```
app musica/
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/example/app_music/
│               ├── MainActivity.kt          # Splash + punto de entrada, solicita permisos
│               ├── MusicViewModel.kt        # Lógica de negocio: carga, reproducción y playlists
│               ├── data/
│               │   └── MusicData.kt         # Data classes: Song y Playlist
│               ├── screen/
│               │   ├── MusicApp.kt          # Navegación principal (BottomNav + PlayerScreen)
│               │   └── pantallas/
│               │       ├── HomeScreen.kt    # Lista principal de canciones
│               │       ├── SearchScreen.kt  # Búsqueda y exploración por categorías
│               │       ├── LibraryScreen.kt # Biblioteca, playlists y artistas
│               │       └── PlayerScreen.kt  # Reproductor a pantalla completa
│               └── ui/theme/
│                   ├── Color.kt             # Paleta oscura (DeepBlack, AccentPink, AccentBlue…)
│                   ├── Theme.kt
│                   └── Type.kt
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## Configuración

### Sin configuración adicional

Esta app **no requiere API keys, archivos `.properties` especiales ni variables de entorno**.  
Todo funciona de forma local usando `MediaStore` de Android.

### Personalización del tema

Los colores de la interfaz se definen en `app/src/main/java/com/example/app_music/ui/theme/Color.kt`:

```kotlin
val DeepBlack  = Color(0xFF0A0A0A)   // Fondo principal
val AccentPink = Color(0xFFFF3E7E)   // Color de acento primario (shuffle, repeat, like)
val AccentBlue = Color(0xFF3E7EFF)   // Color de acento secundario
val CardDark   = Color(0xFF1E1E2E)   // Fondo de tarjetas
val SurfaceDark = Color(0xFF16213E)  // Fondo de superficies elevadas
val TextPrimary = Color.White
val TextMuted   = Color(0xFF9E9E9E)
val Separator   = Color(0xFF2A2A3E)
```

---

## Dependencias principales

| Librería | Uso |
|---|---|
| Jetpack Compose + Material 3 | UI declarativa y componentes de diseño |
| AndroidX Media3 — ExoPlayer | Motor de reproducción de audio |
| AndroidX Media3 — Session | Integración con el sistema de medios de Android |
| AndroidX ViewModel + Compose | Gestión del estado (canciones, reproducción, playlists) |
| Material Icons Extended | Íconos adicionales (shuffle, repeat, queue, etc.) |

---

## Notas y limitaciones

- **Sin persistencia de playlists**: las listas de reproducción creadas se almacenan solo en memoria (`MutableStateFlow`). Al cerrar la app se pierden. Para persistirlas sería necesario agregar Room o DataStore.
- **Sin portadas de álbum reales**: el arte del álbum se simula con un degradado de colores y un efecto de vinilo. La obtención de portadas desde el MediaStore no está implementada.
- **Eliminación/renombrado en Android 11+**: en dispositivos con Android 11 (API 30) o superior, borrar o renombrar canciones requiere que el usuario confirme un diálogo de permiso del sistema (`MediaStore.createDeleteRequest` / `createWriteRequest`).
- **Categorías de búsqueda decorativas**: las tarjetas de género en la pantalla de búsqueda son visuales; el filtrado por género solo funciona si el archivo de audio tiene el metadato `genre` correctamente configurado.
- **minSdk 24**: no compatible con dispositivos Android 6.0 (Marshmallow) o anteriores.
- **Sin acceso a internet**: la app funciona completamente offline.
