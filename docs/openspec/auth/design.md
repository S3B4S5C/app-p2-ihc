# Diseño del cambio: autenticación

## Contexto

El proyecto usa Angular standalone y Spring Boot. La autenticación debe ser suficiente para el proyecto académico, mantener una arquitectura limpia y no dificultar features futuras.

## Decisiones

### D1. JWT stateless

Se emitirá un JWT HS256 después de registro/login. Spring Security actuará como Resource Server para verificar cada petición privada.

**Motivo:** evita implementar sesiones de servidor y mantiene explícito el contrato frontend/backend.

### D2. BCrypt

Las contraseñas se almacenan únicamente como hash BCrypt.

### D3. Flyway como dueño del esquema

Hibernate usa `validate`; todos los cambios persistentes se versionan en `db/migration`.

### D4. Puerto/adaptador para persistencia y tokens

`AuthService` depende de `UserRepository` y `TokenService`. JPA y JWT quedan detrás de adaptadores.

### D5. Sesión frontend centralizada

`TokenStorageService` conserva token + usuario y expone estado reactivo. Guard e interceptor consumen ese mismo estado.

### D6. Componentes visuales pequeños

Se crean `Button`, `FormField` y `ThemeToggle` en lugar de componentes específicos para Login. Esto reduce duplicación al implementar mascotas/perfil.

### D7. Mobile-first

Los estilos base están optimizados para una columna y controles táctiles de al menos ~42–48 px. Los breakpoints únicamente amplían espacios en pantallas mayores.

## Contrato HTTP

### `POST /api/auth/register`

Request:

```json
{ "fullName": "Ana Pérez", "email": "ana@example.com", "password": "password123" }
```

Respuesta `201`: `AuthResponse`.

Errores principales: `400` validación, `409` correo duplicado.

### `POST /api/auth/login`

Request:

```json
{ "email": "ana@example.com", "password": "password123" }
```

Respuesta `200`: `AuthResponse`.

Error principal: `401` credenciales inválidas.

### `GET /api/auth/me`

Header: `Authorization: Bearer <jwt>`.

Respuesta `200`:

```json
{ "id": "uuid", "fullName": "Ana Pérez", "email": "ana@example.com" }
```

## Alternativas descartadas

- **Session cookies:** correctas para web tradicional, pero requerirían CSRF/session store y cambian el ejercicio planteado de JWT.
- **Guardar contraseña reversible:** descartado por seguridad.
- **Generar tablas con Hibernate:** descartado porque dificulta versionar y revisar cambios de esquema.
- **Componente único de auth muy grande:** descartado porque mezcla lógica y reduce reutilización.
