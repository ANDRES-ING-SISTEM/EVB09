# Cambios del Sprint 2 sobre la integración del Sprint 1

El Sprint 2 se adaptó al modelo del Sprint 1 (tabla `usuarios` con rol, login con JWT, tablas `citas`, `especialidades`, `disponibilidades`). Lo que ya funcionaba **no se movió**: todos los endpoints del Sprint 1 siguen respondiendo igual. Esta carpeta se puede copiar encima de la de la integración local.

> **Aviso:** el código nuevo no se ha compilado ni ejecutado todavía (el entorno donde se preparó no tiene Maven ni acceso a internet). Se revisó a mano y con un chequeo de imports y tipos, pero hay que hacer la primera prueba en tu máquina (ver "Cómo probarlo").

## 1. Cómo se resolvió que los dos Sprints no se dañen

| Choque | Solución |
|---|---|
| Sprint 2 tenía su propia tabla de usuarios, con especialista como subclase | El especialista es un `Usuario` con rol `ESPECIALISTA` (como en el Sprint 1). Un solo login para todos. |
| `GET /api/citas/usuario/{id}` existía en los dos | Una sola ruta. Devuelve los campos del Sprint 1 y los del Sprint 2. Ya no incluye el objeto `usuario` anidado. |
| `GET /api/disponibilidades/especialista/{id}` existía en los dos con respuestas distintas | La del Sprint 1 no se toca. HU-10 usa `/disponibles` (ruta nueva). |
| Cancelar y reprogramar: el Sprint 1 ya tenía `DELETE /cancelar/{id}` y `PUT /actualizar/{id}` | Se dejan. HU-14 y HU-15 usan `PUT /{id}/cancelar` y `PUT /{id}/reprogramar`, que no chocan. |
| Estados de cita distintos (`PENDIENTE` vs `SOLICITADA`) | `PENDIENTE` es el de HU-12; `SOLICITADA` y `CONFIRMADA` también cuentan como pendientes. |
| El Sprint 2 usaba campos como `correo`, `contrasena`, `fechaCita`/`horaCita` | Se usan los del Sprint 1: `email`, `password`, `fechaHora`. |
| Bloqueos (HU-11) y citas (HU-12) no se conocían entre sí | HU-12, HU-10 y HU-15 consultan las mismas reglas (`ReglasAgenda`): un horario ocupado o bloqueado no se puede reservar. |
| `schema_postgresql.sql` no coincide con las entidades | No se tocó. Se agregó `migracion_sprint2.sql`, que parte del esquema real. |

## 2. Archivos del Sprint 1 que se modificaron (solo se agregó, no se quitó lógica)
- `model/Usuario.java`: campos `telefono` y `registroMedico`; la contraseña ya no sale en ningún JSON (solo se acepta al recibirla).
- `model/Especialidad.java`: campo `duracionMinutos`.
- `model/Cita.java`: campos opcionales `especialista`, `especialidad`, `motivoCancelacion`, `fechaCancelacion`, `fechaCreacion`.
- `repository/CitaRepository.java`, `EspecialidadRepository.java`, `UsuarioRepository.java`: métodos de consulta nuevos.
- `service/CitaService.java`: si la cita trae especialista, valida horario futuro, ocupado y bloqueado. Sin especialista, igual que antes. HU-13 ordena y valida el usuario.
- `controller/CitaController.java`: solo `GET /usuario/{id}` (devuelve `CitaResponse`).
- `config/SecurityConfig.java`: tres rutas nuevas con `permitAll`, igual que las del Sprint 1.
- `application.properties`: dos propiedades nuevas (horas mínimas para cancelar/reprogramar).
- Frontend: `App.tsx` (rutas), `Home` (menú por rol), `HU12SolicitarCita` (especialista y especialidad opcionales).
- `README.md` y `GUIA_DESPLIEGUE_RENDER.md`: se agregaron secciones.

## 3. Archivos nuevos
- Backend: 38 clases Java (modelos, repositorios, servicios, controladores, DTOs, `GlobalExceptionHandler`, `Reloj`, `ReglasAgenda`), `database/migracion_sprint2.sql`, `sprint_2/` (documentación y Postman).
- Frontend: 8 pantallas (`HU03`, `HU04`, `HU07`, `HU10`, `HU11`, `HU13`, `HU14`, `HU15`), `types/sprint2.ts`, `services/sprint2.service.ts`, `sprint_2/`.

## 4. Cómo probarlo (primera vez)
1. Con el backend detenido: `mvn clean package` (o `./mvnw clean package`). Si hay errores de compilación, son los que hay que corregir primero.
2. Arrancar el backend. Con `ddl-auto=update` crea solo las columnas y tablas nuevas. Revisa en el log que no aparezcan errores de Hibernate.
3. `npm install` y `npm run build` en `frontend/` (el build corre `tsc`, que valida los tipos).
4. Seguir el flujo de `frontend/sprint_2/HISTORIAS_DE_USUARIO_FRONTEND.md`, o importar `backend/sprint_2/postman/Sprint2.postman_collection.json`.
5. Para probar cancelar/reprogramar el mismo día pon `CITAS_CANCELACION_HORAS_MINIMAS=0` y `CITAS_REPROGRAMACION_HORAS_MINIMAS=0`.

## 5. Pendiente y advertencias (no se resolvieron a propósito)
1. **El backend sigue sin validar el token.** Existe el JWT, pero ningún filtro lo lee, y las rutas están en `permitAll` (como en el Sprint 1). Los permisos por rol de las pantallas solo ocultan opciones; cualquiera que llame a la API puede, por ejemplo, registrar un especialista. Arreglarlo cambia el comportamiento del Sprint 1, por eso se deja como tarea aparte: filtro JWT + reglas por rol.
2. **`DELETE /api/citas/cancelar/{id}` (Sprint 1) cancela sin validar** dueño, estado ni anticipación. Es un atajo a HU-14; conviene retirarlo cuando el frontend ya no lo use.
3. **Secreto JWT público** en `application.properties` y en la guía de Render: define `JWT_SECRET` propio en Render.
4. **`ddl-auto=update` y `show-sql=true`** siguen activos (valores del Sprint 1). Para producción conviene migraciones controladas y apagar `show-sql`.
5. **Cambiar el correo (HU-03) obliga a volver a iniciar sesión**, porque el token usa el correo como identificador.
6. **El `Dockerfile` copia `CitasBack-1.0.0-SPRINT1.jar`.** Si cambias la versión del `pom.xml`, actualiza también el Dockerfile.
