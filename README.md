# Proyecto Citas Médicas Vitalis - EBV09

Proyecto integral para la gestión de citas médicas con arquitectura separada (Frontend y Backend) estructurado por Sprints e Historias de Usuario.

## Estructura General del Proyecto
```
.
├── backend/                  # Código y documentación del Backend (Spring Boot + PostgreSQL)
│   ├── sprint_1/             # Entregables y documentación del Sprint 1 (HU-01, HU-02, HU-06, HU-08, HU-09, HU-12)
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
    ├── src/                  # Código fuente Frontend (Componentes, Páginas, Servicios)
    └── package.json
```

## Requisitos y Base de Datos
- **Base de Datos:** PostgreSQL 14+ (Compatible con Supabase, Render PostgreSQL o instalación local).
- **Backend:** Java 17+, Maven 3.8+.
- **Frontend:** Node.js 18+, npm 9+.
