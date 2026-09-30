# Especificación: autenticación

## ADDED Requirements

### Requirement: Registro de tutor

El sistema SHALL permitir crear una cuenta con nombre completo, correo válido y contraseña de al menos 8 caracteres.

#### Scenario: Registro exitoso

- **WHEN** una persona envía datos válidos y un correo no registrado
- **THEN** el backend crea el usuario con contraseña hasheada
- **AND** responde `201` con JWT y datos públicos del usuario
- **AND** el frontend inicia la sesión y navega a Mis Mascotas

#### Scenario: Correo duplicado

- **WHEN** se intenta registrar un correo ya existente
- **THEN** el backend responde `409`
- **AND** no crea un segundo usuario

### Requirement: Inicio de sesión

El sistema SHALL autenticar por correo y contraseña.

#### Scenario: Credenciales válidas

- **WHEN** correo y contraseña coinciden
- **THEN** el backend responde `200` con JWT y usuario
- **AND** el frontend persiste la sesión

#### Scenario: Credenciales inválidas

- **WHEN** el correo no existe o la contraseña no coincide
- **THEN** el backend responde `401`
- **AND** usa el mismo mensaje genérico en ambos casos

### Requirement: Protección de rutas

El sistema SHALL exigir JWT válido en la API privada y en la navegación privada del frontend.

#### Scenario: Usuario anónimo abre Mis Mascotas

- **WHEN** no existe una sesión activa
- **THEN** el guard redirige a `/login`
- **AND** conserva la URL destino en `redirect`

#### Scenario: Request API sin JWT

- **WHEN** se llama una ruta privada bajo `/api/**` sin Bearer token válido
- **THEN** Spring Security rechaza la petición

### Requirement: Cierre de sesión

El sistema SHALL permitir eliminar la sesión local desde el shell.

#### Scenario: Cerrar sesión

- **WHEN** el usuario selecciona Cerrar sesión
- **THEN** se elimina token y usuario del almacenamiento local
- **AND** se navega a `/login`

### Requirement: Tema visual

El sistema SHALL disponer de temas claro y oscuro persistentes.

#### Scenario: Cambiar tema

- **WHEN** el usuario activa el selector de tema
- **THEN** cambian los tokens visuales globales
- **AND** la preferencia se conserva tras recargar

### Requirement: Área Mis Mascotas

El sistema SHALL incluir una ruta autenticada `/mis-mascotas`.

#### Scenario: Usuario autenticado entra a Mis Mascotas

- **WHEN** la sesión contiene un JWT no expirado
- **THEN** se muestra el shell
- **AND** se muestra el título Mis Mascotas
- **AND** existe un contenedor reservado para el futuro listado
