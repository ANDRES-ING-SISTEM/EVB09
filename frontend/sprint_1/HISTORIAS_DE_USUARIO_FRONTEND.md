# Sprint 1 - Historias de Usuario (Frontend)

Este Sprint comprende las vistas, formularios, validaciones y servicios integrados con la API Backend en React y TypeScript.

## Vistas / Historias de Usuario Desarrolladas:
1. **HU01: Registro de Cliente (`src/pages/HU01RegistroDeCliente/`)**
   - Formularios de captura de datos (Nombre, Apellido, Email, Password, Rol).
   - Validaciones de formato e integración con `auth.service.ts`.

2. **HU02: Inicio de Sesión (`src/pages/HU02InicioDeSesion/`)**
   - Formulario de Autenticación.
   - Almacenamiento de Token JWT y Estado Global en `AuthContext.tsx`.

3. **HU06: Registrar Especialidad (`src/pages/HU06RegistrarEspecialidad/`)**
   - Formulario administrativo para catálogo de especialidades.

4. **HU08: Consultar Especialidades (`src/pages/HU08ConsultarEspecialidades/`)**
   - Listado público / administrativo de especialidades registradas.

5. **HU09: Definir Horarios Disponibles (`src/pages/HU09DefinirHorariosDisponibles/`)**
   - Panel para que el especialista cree sus franjas horarias.

6. **HU12: Solicitar Cita (`src/pages/HU12SolicitarCita/`)**
   - Interfaz de reserva de citas médica para los pacientes.
