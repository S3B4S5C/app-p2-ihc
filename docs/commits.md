# Secuencia sugerida de commits

La implementación puede presentarse como desarrollo incremental con estos commits:

1. `chore(backend): preparar persistencia, migraciones y seguridad JWT`
   - Renombra el backend al dominio Mascotas al Día.
   - Configura PostgreSQL/Flyway/JPA/Spring Security.
   - Mantiene el health check y prepara JWT/CORS.

2. `feat(backend): implementar registro, login y proteccion de rutas`
   - Agrega dominio, puertos, adaptadores y endpoints de auth.
   - Agrega manejo de errores y pruebas unitarias/integración.

3. `feat(frontend): crear nucleo de auth, tema y componentes reutilizables`
   - Agrega modelos, servicio, storage, interceptor y guards.
   - Agrega tokens visuales, tema y componentes UI compartidos.

4. `feat(frontend): agregar pantallas de auth, shell y Mis Mascotas`
   - Implementa login/registro mobile-first.
   - Agrega shell desplegable, logout y ruta privada.
   - Agrega pruebas básicas de frontend.

5. `docs: documentar arquitectura y cambio de autenticacion`
   - Actualiza READMEs.
   - Agrega arquitectura.
   - Agrega propuesta/diseño/spec/tareas estilo OpenSpec.
