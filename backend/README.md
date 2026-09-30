# Backend IHC — App de despensa

Proyecto **Java 25 + Spring Boot 4.1.1 + Gradle 9.3.0 + PostgreSQL 17 (Docker)**.
Se coloca en `app-p2-ihc/backend/`, sin modificar `app-p2-ihc/frontend/`.

## Requisitos

- JDK **25** (`java -version` debe indicar 25; configurar `JAVA_HOME`).
- Docker Desktop con Docker Compose v2.
- Conexión a Internet para descargar Gradle y las dependencias la primera vez.

## Arranque en Windows PowerShell

Desde `F:\Docs\uni\ihc\app-p2-ihc\backend`:

```powershell
Copy-Item .env.example .env
docker compose up -d
powershell -ExecutionPolicy Bypass -File .\setup-gradle.ps1 # Solo la primera vez
.\gradlew.bat bootRun
```

El script `setup-gradle.ps1` obtiene **Gradle 9.3.0**, verifica su SHA-256 y genera el
**Gradle Wrapper oficial** (`gradlew.bat`, `gradlew` y el JAR). Si ya tienes Gradle
9.1 o superior, puedes sustituir ese paso por `gradle wrapper --gradle-version 9.3.0`.
El instalador local `.gradle-local/` se puede borrar después del bootstrap.

Si utilizas el frontend Angular con `npm start` o `ng serve`, podrá acceder al
backend en `http://localhost:8080` (origen CORS por defecto: `http://localhost:4200`).

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/health` | Comprueba `SELECT 1` contra PostgreSQL; devuelve `200` o `503`. |
| GET | `/actuator/health` | Health check estándar de Spring Boot (incluye indicador de BD). |

Cuando PostgreSQL está disponible:

```powershell
Invoke-RestMethod http://localhost:8080/api/health
# { "status": "UP", "database": "UP" }
```

Si PostgreSQL deja de responder, `/api/health` devuelve HTTP 503 con
`{ "status": "DOWN", "database": "DOWN" }`.

## Configuración

`compose.yaml` expone PostgreSQL **solo en 127.0.0.1** en el puerto `DB_PORT`
(por defecto 5432). La base de datos se llama `despensa` y sus datos persisten
en el volumen `postgres_data` de Docker.

`application.yaml` importa opcionalmente el mismo `.env` cuando ejecutas el
backend **desde esta carpeta**, para mantener sincronizados el usuario, la
contraseña, el nombre de BD y el puerto. Las variables reales del sistema tienen
prioridad. No subas `.env` al repositorio ni uses las credenciales de ejemplo en producción.

Variables soportadas: `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `DB_PORT`,
`SERVER_PORT` y `FRONTEND_ORIGIN`.

## Pruebas

```powershell
.\gradlew.bat test
.\gradlew.bat build
```

Las pruebas unitarias simulan la BD: verifican las respuestas HTTP 200 y 503 sin
necesitar Docker. Para comprobar la integración real, inicia PostgreSQL y luego
consulta `/api/health` o `/actuator/health`.

## Detener

```powershell
docker compose down        # Conserva los datos
docker compose down -v     # Elimina también la base de datos local
```

> Si cambias `DB_NAME`, `DB_USER` o `DB_PASSWORD` después de crear el volumen,
> PostgreSQL no reconfigura automáticamente la BD ya inicializada.
