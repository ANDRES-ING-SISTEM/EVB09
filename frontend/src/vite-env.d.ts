/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** URL base del backend en despliegue. Vacia en desarrollo (proxy de Vite). */
  readonly VITE_API_BASE_URL?: string;
  /** Solo desarrollo: "true" usa datos simulados en memoria en lugar del backend. */
  readonly VITE_MODO_SIMULADO?: string;
  /** "true" al construir: el sitio desplegado usa datos simulados (demo sin backend). */
  readonly VITE_MODO_DEMO?: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
