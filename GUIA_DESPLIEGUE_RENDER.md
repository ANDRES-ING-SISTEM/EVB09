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
