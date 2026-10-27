// Datos simulados en memoria (ver `services/simulado/`) en lugar del backend.
// Se activan de dos formas:
//  - Desarrollo: VITE_MODO_SIMULADO=true (solo con `npm run dev`).
//  - Demo desplegada: VITE_MODO_DEMO=true al construir (`vite build`), para publicar el sitio
//    mostrando las pantallas con datos de ejemplo mientras el backend no esté conectado.
// Sin ninguna de las dos, el sitio usa el backend real.
const SIMULADO_EN_DESARROLLO = import.meta.env.DEV && import.meta.env.VITE_MODO_SIMULADO === 'true';
const DEMO_DESPLEGADA = import.meta.env.VITE_MODO_DEMO === 'true';

export const MODO_SIMULADO = SIMULADO_EN_DESARROLLO || DEMO_DESPLEGADA;
