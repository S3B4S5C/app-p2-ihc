# Arquitectura — Mascotas al Día

## Objetivos

La arquitectura separa reglas de negocio, detalles de infraestructura y presentación para que la autenticación pueda evolucionar sin acoplar el resto de la aplicación a JWT, PostgreSQL o componentes visuales específicos.

## Vista general

```text
Angular mobile-first
  pages/layouts
       ↓
  core/auth + shared/ui
       ↓ HTTP + Bearer JWT
Spring Boot API
  presentation
       ↓
  application
       ↓
  domain ports
       ↑
  infrastructure adapters
       ↓
PostgreSQL
```

## Frontend

### `core/`

Servicios transversales que existen una sola vez durante la aplicación.

- `auth/AuthService`: operaciones de login, registro, usuario actual y logout.
- `auth/TokenStorageService`: única fuente reactiva de la sesión persistida.
- `auth/auth.interceptor.ts`: agrega el JWT solo a URLs del API y limpia sesión ante 401.
- `auth/auth.guard.ts`: protege rutas privadas y evita mostrar auth a usuarios ya autenticados.
- `theme/ThemeService`: persiste y aplica el tema.

### `shared/ui/`

Componentes sin conocimiento de páginas concretas:

- botón,
- campo de formulario,
- selector de tema.

Todos consumen variables CSS semánticas, por lo que podrán reutilizarse en formularios de mascotas, perfil, citas o recordatorios.

### `pages/`

Orquestan formularios y navegación. `LoginPage` y `RegisterPage` no implementan HTTP directamente: delegan en `AuthService`.

### `layouts/`

`AppShellComponent` envuelve el área privada y concentra header/navegación/cierre de sesión.

### `models/`

Contratos TypeScript que reflejan el contrato HTTP del backend.

## Backend

El backend está organizado por feature (`auth`) y dentro de ella por capas limpias.

### Dominio

`auth/domain` contiene:

- `User`: modelo de dominio.
- `UserRepository`: puerto de persistencia.

No depende de JPA ni de HTTP.

### Aplicación

`auth/application` contiene:

- `AuthService`: casos de uso registro/login/usuario actual.
- `TokenService`: puerto para emitir tokens.

La capa conoce interfaces, no implementaciones de PostgreSQL/JWT.

### Infraestructura

- `infrastructure/persistence`: entidad JPA, repositorio Spring Data y adaptador del puerto `UserRepository`.
- `infrastructure/security`: implementación JWT de `TokenService`.

### Presentación

`auth/presentation/AuthController` define DTOs HTTP y validaciones de entrada. Los errores se normalizan mediante `GlobalExceptionHandler`.

### Configuración transversal

- `SecurityConfig`: API stateless y Resource Server JWT.
- `CorsConfig`: origen permitido del frontend.
- `Flyway`: dueño del esquema de base de datos.
- Hibernate solo valida el esquema (`ddl-auto: validate`).

## Flujo de login

```text
LoginPage
  → AuthService.login()
  → POST /api/auth/login
  → AuthController
  → AuthService (backend)
  → UserRepository
  → BCrypt.matches
  → TokenService.issue
  ← JWT + usuario
  ← TokenStorageService.save
  → /mis-mascotas
```

## Decisiones de seguridad

- Contraseñas nunca se almacenan ni retornan en texto plano.
- JWT con HS256 y secreto mínimo de 32 bytes.
- Expiración configurable (`JWT_TTL`, 8 h por defecto).
- `sub` contiene el correo normalizado; `uid` el UUID.
- La firma, expiración e issuer se validan al autenticar cada request.
- No se guardan sesiones en servidor.
- El cliente no considera el JWT una fuente de permisos; solo usa `exp` para evitar reutilizar un token evidentemente vencido.
- Los endpoints públicos están enumerados explícitamente; `/api/**` queda protegido por defecto.

## Evolución prevista

Para el siguiente incremento, la feature `pets` puede repetir el mismo patrón `domain/application/infrastructure/presentation` en backend y crear `pages/pets`, `core/pets` y componentes compartidos en frontend sin modificar la infraestructura de autenticación.
