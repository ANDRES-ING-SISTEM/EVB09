# Backend — Sprint 2

Historias: HU-03, HU-04, HU-07, HU-10, HU-11, HU-13, HU-14 y HU-15. Todas conviven con el Sprint 1 y usan su modelo: un especialista es un `Usuario` con rol `ESPECIALISTA`; el login y el JWT son los de HU-02.

Todos los errores de las HU del Sprint 2 responden JSON con la clave `message` (la misma que lee el frontend del Sprint 1): `400` regla de negocio o campos inválidos, `404` recurso inexistente.

> Mientras no exista un filtro JWT, el backend no sabe quién hace cada petición. Por eso HU-14 y HU-15 reciben `idUsuario` en el body y comprueban que la cita sea suya.

## HU-03 Gestión de perfil
- `GET /api/usuarios/{id}` → `{ id, nombre, apellido, email, telefono, rol }` (nunca la contraseña).
- `PUT /api/usuarios/{id}` body `{ nombre, apellido, email, telefono }` → perfil actualizado. `telefono` opcional (7–15 dígitos). `400` si el correo ya lo usa otra cuenta.
- Nota: el JWT lleva el correo como identificador; si el usuario cambia su correo debe volver a iniciar sesión.

## HU-04 Registrar especialista
- `POST /api/especialistas` body `{ nombre, apellido, email, password, confirmarPassword, telefono?, registroMedico }` → `201` con el especialista. Contraseña mínimo 8 caracteres, cifrada con BCrypt. El especialista queda con rol `ESPECIALISTA` y entra por `/api/auth/login`.
- `GET /api/especialistas` → lista de especialistas (la usan HU-10 y HU-12 para elegir uno).

## HU-07 Editar especialidad
- `PUT /api/especialidades/{id}` body `{ nombre, descripcion, duracionMinutos }` (mínimo 15). `404` si no existe; `400` si el nombre ya lo usa otra especialidad, o si se cambia la duración y la especialidad tiene citas pendientes.
- `HU-06` (`POST`/`GET /api/especialidades`) no cambia; `duracionMinutos` es un campo opcional nuevo.

## HU-10 Consultar disponibilidad
- `GET /api/disponibilidades/especialista/{id}/disponibles?fecha=AAAA-MM-DD` (`fecha` opcional) → `{ mensaje, data: [{ id, fecha, horaInicio, horaFin }] }`.
- Parte de las franjas de HU-09 y descarta las pasadas, las que tienen una cita activa y las que se cruzan con un bloqueo de HU-11. Si no queda ninguna: `200` con `data: []` y un mensaje.
- `GET /api/disponibilidades/especialista/{id}` (HU-09) sigue devolviendo todas las franjas, sin filtrar.

## HU-11 Bloquear horario
- `POST /api/especialistas/{id}/bloqueos` body `{ fecha, horaInicio, horaFin, motivo? }` → `201`. `400` si la hora de inicio no es anterior a la de fin, si la fecha/hora ya pasó, si hay citas activas en ese rango o si se cruza con otro bloqueo.
- `GET /api/especialistas/{id}/bloqueos?fecha=` → bloqueos activos.
- `DELETE /api/bloqueos/{idBloqueo}` → libera el bloqueo (queda como historial).
- Un horario bloqueado ya no se puede pedir en HU-12 ni usar en HU-15.

## HU-13 Consultar citas
- `GET /api/citas/usuario/{id}` → lista (más recientes primero) con `id, servicio, fechaHora, estado` (campos del Sprint 1) y además `fechaCita, horaCita, especialidad, idEspecialista, especialista, motivoCancelacion, fechaCancelacion`. `404` si el usuario no existe.
- Cambio respecto al Sprint 1: ya no devuelve el objeto `usuario` anidado (traía datos del usuario que no deben salir en esta respuesta).

## HU-14 Cancelar cita
- `PUT /api/citas/{id}/cancelar` body `{ idUsuario, motivo? }`. La cita pasa a `CANCELADA` (no se borra). `400` si no es del usuario, ya estaba cancelada, no está pendiente o faltan menos de 24 h (`citas.cancelacion.horas-minimas`).
- El `DELETE /api/citas/cancelar/{id}` del Sprint 1 sigue existiendo y funciona igual (sin validaciones).

## HU-15 Reprogramar cita
- `PUT /api/citas/{id}/reprogramar` body `{ idUsuario, nuevaFechaCita, nuevaHoraCita, motivo? }`. La cita conserva su id y estado; se guarda el historial en `reprogramaciones_cita`. `400` si no es del usuario, no está pendiente, faltan menos de 24 h (`citas.reprogramacion.horas-minimas`), el nuevo horario ya pasó, está ocupado o bloqueado.
- El `PUT /api/citas/actualizar/{id}` del Sprint 1 sigue existiendo.

## Cambio en HU-12 (Sprint 1), sin romper nada
`POST /api/citas/agendar/{usuarioId}` acepta ahora, opcionalmente, `especialista: { id }` y `especialidad: { id }`. Si se envía especialista se valida que exista, que el horario sea futuro y que no esté ocupado ni bloqueado. Si no se envía, funciona exactamente igual que en el Sprint 1.

## Estados de una cita
`PENDIENTE` (la deja HU-12), `SOLICITADA` y `CONFIRMADA` (se aceptan por compatibilidad), `CANCELADA` y `COMPLETADA`. "Pendiente" para HU-07/14/15 = `PENDIENTE`, `SOLICITADA` o `CONFIRMADA`.
