# Proyecto Citas Médicas Vitalis - EBV09

Proyecto integral para la gestión de citas médicas con arquitectura separada (Frontend y Backend) estructurado por Sprints e Historias de Usuario.

## Estructura General del Proyecto
```
.
├── backend/                  # Código y documentación del Backend (Spring Boot + PostgreSQL)
│   ├── sprint_1/             # Entregables y documentación del Sprint 1 (HU-01, HU-02, HU-06, HU-08, HU-09, HU-12)
│   ├── sprint_2/             # Documentación y Postman del Sprint 2 (HU-03, HU-04, HU-07, HU-10, HU-11, HU-13, HU-14, HU-15)
│   ├── src/                  # Código fuente Java
│   │   ├── main/
│   │   │   ├── java/com/citas/app/CitasBack/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile            # Configuración para despliegue en Render / Cloud
│   ├── pom.xml               # Dependencias de Maven
│   └── database/             # Scripts SQL para PostgreSQL
└── frontend/                 # Código y documentación del Frontend (React + Vite + TypeScript)
    ├── sprint_1/             # Entregables y documentación del Sprint 1 (HU-01, HU-02, HU-06, HU-08, HU-09, HU-12)
    ├── sprint_2/             # Documentación del Sprint 2 (HU-03, HU-04, HU-07, HU-10, HU-11, HU-13, HU-14, HU-15)
    ├── src/                  # Código fuente Frontend (Componentes, Páginas, Servicios)
    └── package.json
```

## Requisitos y Base de Datos
- **Base de Datos:** PostgreSQL 14+ (Compatible con Supabase, Render PostgreSQL o instalación local).
- **Backend:** Java 17+, Maven 3.8+.
- **Frontend:** Node.js 18+, npm 9+.

## Sprint 2 (integrado sobre el Sprint 1)
Este mismo proyecto contiene ahora el Sprint 1 **y** el Sprint 2. Lo del Sprint 1 sigue funcionando igual; el Sprint 2 se agregó encima, adaptado al mismo modelo de datos (usuarios con rol, login con JWT).

| HU | Qué hace | Endpoint principal | Pantalla |
|---|---|---|---|
| HU-03 | Gestión de perfil | `GET/PUT /api/usuarios/{id}` | `/perfil` |
| HU-04 | Registrar especialista | `POST /api/especialistas` | `/registrar-especialista` |
| HU-07 | Editar especialidad | `PUT /api/especialidades/{id}` | `/editar-especialidad` |
| HU-10 | Consultar disponibilidad | `GET /api/disponibilidades/especialista/{id}/disponibles` | `/disponibilidad` |
| HU-11 | Bloquear horario | `POST /api/especialistas/{id}/bloqueos` | `/bloquear-horario` |
| HU-13 | Consultar citas | `GET /api/citas/usuario/{id}` | `/mis-citas` |
| HU-14 | Cancelar cita | `PUT /api/citas/{id}/cancelar` | `/cancelar-cita?id=` |
| HU-15 | Reprogramar cita | `PUT /api/citas/{id}/reprogramar` | `/reprogramar-cita?id=` |

Detalle de cada endpoint en `backend/sprint_2/` y `frontend/sprint_2/`. Lista completa de archivos tocados y notas importantes en **`CAMBIOS_SPRINT_2.md`**.
