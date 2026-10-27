# Guía de Despliegue en Render (Sprint 1)

## 1. Configurar Base de Datos PostgreSQL
1. Crea una base de datos PostgreSQL en **Render** o **Supabase**.
2. Copia la URL de conexión, usuario y contraseña.
3. Ejecuta el archivo `backend/database/schema_postgresql.sql` para crear la estructura de tablas inicial.

## 2. Desplegar Backend en Render (Web Service)
1. Crea un nuevo **Web Service** en Render conectando el repositorio.
2. Selecciona la carpeta `backend` como Root Directory.
3. Selecciona el runtime **Docker** (utilizará el `Dockerfile` incluido).
4. Configura las variables de entorno:
   - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://<HOST>:<PUERTO>/<DATABASE>`
   - `SPRING_DATASOURCE_USERNAME`: `<USUARIO>`
   - `SPRING_DATASOURCE_PASSWORD`: `<CONTRASEÑA>`
   - `JWT_SECRET`: `9a2f8c4e1b7d5e3f9a2f8c4e1b7d5e3f9a2f8c4e1b7d5e3f9a2f8c4e1b7d5e3f`
   - `FRONTEND_URL`: `https://tu-frontend.onrender.com`

## 3. Desplegar Frontend en Render (Static Site)
1. Crea un nuevo **Static Site** en Render.
2. Selecciona la carpeta `frontend` como Root Directory.
3. Configuración de Build:
   - **Build Command:** `npm run build`
   - **Publish Directory:** `dist`
4. Variable de Entorno:
   - `VITE_API_URL`: `https://tu-backend.onrender.com`

## 4. Notas para el Sprint 2 (leer antes de redesplegar)
1. **Base de datos:** no hay que hacer nada si el backend tiene `spring.jpa.hibernate.ddl-auto=update` (viene así): al arrancar crea solo las columnas y tablas nuevas. Si prefieres hacerlo a mano, ejecuta `backend/database/migracion_sprint2.sql` una vez (se puede repetir sin problema). No uses `schema_postgresql.sql` sobre una base que ya funciona: está desactualizado frente a las entidades y borra las tablas.
2. **Variables de entorno nuevas (opcionales):** `CITAS_CANCELACION_HORAS_MINIMAS` y `CITAS_REPROGRAMACION_HORAS_MINIMAS` (por defecto 24). Para una demo con citas de hoy o mañana, ponlas en `0`.
3. **Secreto JWT:** en Render define `JWT_SECRET` con un valor propio y largo (mínimo 32 caracteres). No uses el valor de ejemplo que aparece en esta guía ni el de `application.properties`: es público.
4. **Frontend (Static Site):** agrega una regla de reescritura en Render (*Redirects/Rewrites*): origen `/*`, destino `/index.html`, acción *Rewrite*. Sin ella, recargar páginas como `/mis-citas` o `/perfil` da 404.
5. **CORS:** `FRONTEND_URL` / `cors.allowed-origins` debe ser la URL exacta del frontend, sin `/` al final.
6. **Administrador:** el formulario de registro solo crea PACIENTE y ESPECIALISTA. Para probar HU-04 necesitas un ADMIN: regístrate normalmente y ejecuta en la base de datos `UPDATE usuarios SET rol = 'ADMIN' WHERE email = 'tu@correo.com';`, y vuelve a iniciar sesión.
