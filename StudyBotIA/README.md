# StudyBotIA — AprendeBot

Aplicación Android de asistente de estudio impulsada por Inteligencia Artificial.  
Permite a estudiantes y profesores iniciar sesión, registrarse y chatear con una IA especializada en aprendizaje, con historial de conversación persistente en el dispositivo.

---

## ¿Qué hace?

| Pantalla | Descripción |
|---|---|
| **Splash** | Animación de entrada (2,5 s) con el logo de AprendeBot. |
| **Login** | Autenticación mediante usuario/email y contraseña contra el backend. |
| **Registro** | Creación de cuenta con nombre de usuario, correo, tipo de usuario (estudiante / profesor) y contraseña. |
| **Chat** | Conversación en tiempo real con la IA. Soporta Markdown en las respuestas, historial persistente en SQLite y posibilidad de copiar mensajes con pulsación larga. |
| **Reporte** | Formulario para enviar reportes de problemas por correo electrónico con información técnica del dispositivo incluida automáticamente. |

El historial de chat se guarda en una base de datos SQLite local (`studybot.db`) para que persista entre sesiones.

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
2. **File → Open…** y selecciona la carpeta `StudyBotIA/`.
3. Espera a que Gradle sincronice las dependencias.
4. Conecta un dispositivo físico (Android 7.0+) o crea un AVD.
5. Pulsa **Run ▶** (o `Shift+F10`).

### Desde la terminal

```bash
# Entrar a la carpeta del proyecto
cd StudyBotIA

# Compilar y desplegar en un dispositivo/emulador conectado
./gradlew installDebug

# Solo compilar (genera el APK en app/build/outputs/apk/debug/)
./gradlew assembleDebug
```

---

## Estructura del proyecto

```
StudyBotIA/
├── app/
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           └── java/com/example/studybotia/
│               ├── MainActivity.kt          # Splash Screen y punto de entrada
│               ├── data/
│               │   ├── AppDatabase.kt       # SQLiteOpenHelper (base de datos local)
│               │   ├── ChatRepository.kt    # Repositorio de mensajes
│               │   └── MessageDao.kt        # DAO para la tabla messages
│               ├── model/
│               │   ├── IARequest.kt         # Payload para el endpoint de chat IA
│               │   ├── IAResponse.kt        # Respuesta del chat IA
│               │   ├── LoginRequest.kt      # Payload de login
│               │   ├── LoginResponse.kt     # Respuesta de login
│               │   ├── Message.kt           # Modelo de mensaje en pantalla
│               │   ├── RegisterRequest.kt   # Payload de registro
│               │   └── RegisterResponse.kt  # Respuesta de registro
│               ├── network/
│               │   ├── ApiService.kt        # Interfaz Retrofit (login, registro, chat IA)
│               │   └── RetrofitClient.kt    # Singleton de Retrofit
│               ├── pantalla/
│               │   ├── login.kt             # LoginActivity + LoginScreen
│               │   ├── registro.kt          # RegistroActivity + RegistroScreen
│               │   ├── chat.kt              # ChatActivity + ChatScreen
│               │   └── reporte.kt           # ReporteActivity + ReporteScreen
│               └── ui/theme/
│                   ├── Color.kt
│                   ├── Theme.kt
│                   └── Type.kt
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

---

## Configuración

### URL del backend

La URL base de la API se define directamente en `RetrofitClient.kt`:

```kotlin
// app/src/main/java/com/example/studybotia/network/RetrofitClient.kt
private const val BASE_URL = "https://app-study-bot.onrender.com/"
```

Si necesitas apuntar a un entorno diferente (local o de pruebas), cambia este valor.

### Endpoints utilizados

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/user/login` | Inicia sesión. Devuelve `status: "ok"` si las credenciales son correctas. |
| POST | `/user/register` | Registra un nuevo usuario con `username`, `email`, `tipo` y `password`. |
| POST | `/AI/chat` | Envía una pregunta a la IA y recibe la respuesta. |

### Permisos Android

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

Solo se requiere acceso a internet.

### SharedPreferences

La sesión del usuario se guarda en las preferencias de la app bajo la clave `"studybot"`:

| Clave | Descripción |
|---|---|
| `username` | Nombre de usuario autenticado. Se usa en el reporte de problemas. |

---

## Dependencias principales

| Librería | Versión | Uso |
|---|---|---|
| Jetpack Compose + Material 3 | BOM incluido | UI declarativa |
| Retrofit 2 | 2.9.0 | Cliente HTTP REST |
| Gson Converter | 2.9.0 | Serialización JSON |
| compose-richtext (Halilibo) | 0.17.0 | Renderizado de Markdown en el chat |
| SQLite (Android SDK) | — | Historial de mensajes local |

---

## Notas y limitaciones

- **Sin API key en el cliente**: la autenticación con la IA se delega al backend (`app-study-bot.onrender.com`). No hay ninguna clave expuesta en el código cliente.
- **Backend en Render (free tier)**: el servidor puede tardar unos segundos en responder si lleva tiempo inactivo (cold start).
- **Sin cifrado de contraseñas en cliente**: las contraseñas se envían en texto plano sobre HTTPS; el cifrado debe realizarse en el backend.
- **Historial solo en SQLite local**: no se sincroniza con el servidor. Si se desinstala la app o se limpia el almacenamiento, se pierde el historial.
- **minSdk 24**: no es compatible con dispositivos Android 6.0 (Marshmallow) o anteriores.
- **Correo de soporte**: `reporte-bot-study@studybot.com` (definido en `reporte.kt`).
