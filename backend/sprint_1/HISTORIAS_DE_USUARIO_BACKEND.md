# Sprint 1 - Historias de Usuario (Backend)

Este Sprint abarca la infraestructura del servidor, la seguridad JWT y la lógica de negocio en PostgreSQL.

## Historias de Usuario Soportadas:
1. **HU-01: Registro de Usuarios (Pacientes y Especialistas)**
   - Endpoints: `POST /api/auth/registro`
   - Cifrado de contraseñas con BCrypt.
   - Validación de duplicados en la tabla `usuarios` en PostgreSQL.

2. **HU-02: Inicio de Sesión y Autenticación**
   - Endpoints: `POST /api/auth/login`
   - Generación de Token JWT firmado.
   - Retorno de información del usuario y rol asignado.

3. **HU-06: Registrar Especialidad Médica**
   - Endpoints: `POST /api/especialidades`
   - Control de acceso para administradores.

4. **HU-08: Consultar Especialidades**
   - Endpoints: `GET /api/especialidades`
   - Lista pública de especialidades registradas en la base de datos.

5. **HU-09: Definir Horarios Disponibles**
   - Endpoints: `POST /api/disponibilidades` y `GET /api/disponibilidades/especialista/{id}`
   - Registro de franjas horarias por especialista.

6. **HU-12: Solicitar / Reservar Cita**
   - Endpoints: `POST /api/citas` y `GET /api/citas/paciente/{id}`
   - Persistencia de la reserva y actualización de disponibilidad.
