# Mascotas al Día

Aplicación mobile-first para organizar el cuidado de mascotas.

## Stack

- Frontend: Angular 21 standalone + TypeScript + Vitest.
- Backend: Java 25 + Spring Boot 4.1.1 + Gradle 9.3.0.
- Base de datos: PostgreSQL 17 en Docker.
- Autenticación: JWT firmado con HMAC-SHA256 y contraseñas BCrypt.
- Migraciones: Flyway.

## Arranque local

### 1. Base de datos y backend

```powershell
cd backend
Copy-Item .env.example .env   # solo si no existe

docker compose up -d
.\gradlew.bat bootRun
```

Backend: `http://localhost:8080`.

### 2. Frontend

En otra terminal:

```powershell
cd frontend
npm ci
npm start
```

Frontend: `http://localhost:4200`.

## Flujo implementado

1. Registro de usuario.
2. Inicio de sesión.
3. Persistencia local de sesión JWT.
4. Guard de rutas privadas.
5. Shell autenticado con menú desplegable.
6. Página privada **Mis Mascotas** preparada para el siguiente incremento.
7. Cierre de sesión.
8. Tema claro/oscuro persistente.

La documentación de arquitectura está en [`docs/architecture.md`](docs/architecture.md) y el cambio está descrito con estructura inspirada en OpenSpec dentro de [`docs/openspec/auth`](docs/openspec/auth).
