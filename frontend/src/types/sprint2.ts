// Tipos de las respuestas del backend para las historias del Sprint 2.

export interface PerfilUsuario {
  id: number;
  nombre: string;
  apellido: string;
  email: string;
  telefono: string | null;
  rol: string;
}

export interface Especialista extends PerfilUsuario {
  registroMedico: string | null;
}

export interface Especialidad {
  id: number;
  nombre: string;
  descripcion: string | null;
  duracionMinutos: number | null;
  activo: boolean;
}

export interface HorarioLibre {
  id: number;
  fecha: string;
  horaInicio: string;
  horaFin: string;
}

export interface Bloqueo {
  idBloqueo: number;
  idEspecialista: number;
  fecha: string;
  horaInicio: string;
  horaFin: string;
  motivo: string | null;
  estado: string;
}

export interface CitaDetalle {
  id: number;
  servicio: string;
  fechaHora: string;
  estado: string;
  fechaCita: string;
  horaCita: string;
  especialidad: string;
  idEspecialista: number | null;
  especialista: string | null;
  motivoCancelacion: string | null;
  fechaCancelacion: string | null;
}
