# Propuesta de cambio: autenticación y área privada

## Por qué

Mascotas al Día necesita identificar al tutor antes de permitirle administrar sus mascotas. La aplicación todavía no dispone de cuentas, control de acceso ni una estructura visual común para el área autenticada.

## Qué cambia

- Se agrega registro con nombre completo, correo y contraseña.
- Se agrega inicio/cierre de sesión mediante JWT.
- Se protege por defecto la API privada.
- Se agrega persistencia de usuarios con migración Flyway.
- Se agregan guards e interceptor en Angular.
- Se agregan componentes UI reutilizables para formularios de autenticación.
- Se agrega tema claro/oscuro y shell autenticado mobile-first.
- Se agrega la primera página privada: Mis Mascotas.
- Se agregan pruebas de backend y pruebas básicas de frontend.

## Impacto

### Frontend

El placeholder inicial de Angular pasa a una aplicación navegable con rutas públicas y privadas. No se agregan dependencias npm adicionales.

### Backend

La configuración pasa de un health check con JDBC a una API que además utiliza Spring Security, JPA y Flyway. La base local por defecto cambia de `despensa` a `mascotas`.

### Datos

Se crea `app_user`. No existe migración de datos previa porque la aplicación todavía no tenía usuarios.

## Fuera de alcance

- CRUD real de mascotas.
- Recuperación/cambio de contraseña.
- Refresh tokens.
- Verificación de correo.
- Login social.
- Roles administrativos.
