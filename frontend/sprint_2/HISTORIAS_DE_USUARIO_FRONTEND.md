# Frontend — Sprint 2

Misma base que el Sprint 1: React 18 + Vite + TypeScript, `axios` (`src/services/api.ts`, que ya agrega el token), sesión en `AuthContext` y estilos Bootstrap. Las pantallas nuevas están en `src/pages/`, los tipos en `src/types/sprint2.ts` y las llamadas al backend en `src/services/sprint2.service.ts`.

| HU | Carpeta | Ruta | Quién la usa |
|---|---|---|---|
| HU-03 | `HU03GestionDePerfil` | `/perfil` | cualquier usuario con sesión |
| HU-04 | `HU04RegistroDeEspecialistas` | `/registrar-especialista` | ADMIN |
| HU-07 | `HU07EditarEspecialidad` | `/editar-especialidad` | ADMIN, ESPECIALISTA |
| HU-10 | `HU10ConsultarDisponibilidad` | `/disponibilidad` | cualquier usuario con sesión |
| HU-11 | `HU11BloquearHorario` | `/bloquear-horario` | ESPECIALISTA |
| HU-13 | `HU13ConsultarCitas` | `/mis-citas` | PACIENTE |
| HU-14 | `HU14CancelarCita` | `/cancelar-cita?id=` | PACIENTE (desde Mis citas) |
| HU-15 | `HU15ReprogramarCita` | `/reprogramar-cita?id=` | PACIENTE (desde Mis citas) |

## Cambios en pantallas del Sprint 1
- **Home:** el texto "las demás historias aún no están implementadas" se reemplazó por un menú con los enlaces según el rol.
- **HU-12 Solicitar cita:** el formulario sigue igual; se agregaron dos listas opcionales (especialista y especialidad) y lee `?especialista=` y `?fechaHora=` cuando llega desde HU-10. Sin elegir especialista funciona como antes. Ahora muestra el mensaje real del backend cuando el horario está ocupado o bloqueado.
- **App.tsx:** solo se agregaron las rutas nuevas.

## Flujo para probar todo
1. Registrar un ESPECIALISTA (HU-01) → definir horarios (HU-09) → bloquear uno (HU-11).
2. Registrar un PACIENTE → consultar disponibilidad (HU-10) → "Solicitar cita" (HU-12) → ver Mis citas (HU-13) → reprogramar (HU-15) o cancelar (HU-14).
3. Con un ADMIN (ver `GUIA_DESPLIEGUE_RENDER.md`, sección 4): registrar otro especialista (HU-04) y editar especialidades (HU-07).

Los permisos por rol de estas pantallas solo ocultan opciones en la interfaz; el backend todavía no las exige (ver `CAMBIOS_SPRINT_2.md`).
