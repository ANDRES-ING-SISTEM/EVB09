import api from './api';
import {
  Bloqueo,
  CitaDetalle,
  Especialidad,
  Especialista,
  HorarioLibre,
  PerfilUsuario,
} from '../types/sprint2';

/**
 * Saca el mensaje de error de una respuesta del backend. Los controladores del Sprint 2 devuelven
 * { "message": "..." }; si el servidor no responde o no manda mensaje se usa el texto por defecto.
 */
export const mensajeDeError = (err: any, porDefecto: string): string => {
  const data = err?.response?.data;
  if (typeof data === 'string' && data.trim() !== '') return data;
  if (data?.message) return data.message;
  if (data?.error) return data.error;
  return porDefecto;
};

/** "08:00:00" -> "08:00" */
export const hhmm = (hora: string): string => hora.slice(0, 5);

/** Estados en los que una cita todavía se puede cancelar o reprogramar. */
export const estadoPendiente = (estado: string): boolean =>
  ['PENDIENTE', 'SOLICITADA', 'CONFIRMADA'].includes(estado);

export const sprint2Service = {
  // HU-03
  consultarPerfil: async (idUsuario: number) =>
    (await api.get<PerfilUsuario>(`/api/usuarios/${idUsuario}`)).data,

  actualizarPerfil: async (
    idUsuario: number,
    datos: { nombre: string; apellido: string; email: string; telefono: string | null }
  ) => (await api.put<PerfilUsuario>(`/api/usuarios/${idUsuario}`, datos)).data,

  // HU-04
  registrarEspecialista: async (datos: {
    nombre: string;
    apellido: string;
    email: string;
    password: string;
    confirmarPassword: string;
    telefono: string | null;
    registroMedico: string;
  }) => (await api.post<Especialista>('/api/especialistas', datos)).data,

  listarEspecialistas: async () => (await api.get<Especialista[]>('/api/especialistas')).data,

  // HU-06 / HU-07
  listarEspecialidades: async () => (await api.get<Especialidad[]>('/api/especialidades')).data,

  editarEspecialidad: async (
    idEspecialidad: number,
    datos: { nombre: string; descripcion: string; duracionMinutos: number }
  ) => (await api.put<Especialidad>(`/api/especialidades/${idEspecialidad}`, datos)).data,

  // HU-10
  consultarDisponibles: async (idEspecialista: number, fecha?: string) =>
    (
      await api.get<{ mensaje: string; data: HorarioLibre[] }>(
        `/api/disponibilidades/especialista/${idEspecialista}/disponibles`,
        { params: fecha ? { fecha } : undefined }
      )
    ).data,

  // HU-11
  bloquearHorario: async (
    idEspecialista: number,
    datos: { fecha: string; horaInicio: string; horaFin: string; motivo: string | null }
  ) =>
    (
      await api.post<{ mensaje: string; bloqueo: Bloqueo }>(
        `/api/especialistas/${idEspecialista}/bloqueos`,
        datos
      )
    ).data,

  listarBloqueos: async (idEspecialista: number) =>
    (await api.get<Bloqueo[]>(`/api/especialistas/${idEspecialista}/bloqueos`)).data,

  desbloquearHorario: async (idBloqueo: number) =>
    (await api.delete<{ mensaje: string; bloqueo: Bloqueo }>(`/api/bloqueos/${idBloqueo}`)).data,

  // HU-13
  consultarCitas: async (idUsuario: number) =>
    (await api.get<CitaDetalle[]>(`/api/citas/usuario/${idUsuario}`)).data,

  // HU-14
  cancelarCita: async (idCita: number, idUsuario: number, motivo: string | null) =>
    (await api.put<{ mensaje: string }>(`/api/citas/${idCita}/cancelar`, { idUsuario, motivo })).data,

  // HU-15
  reprogramarCita: async (
    idCita: number,
    datos: { idUsuario: number; nuevaFechaCita: string; nuevaHoraCita: string; motivo: string | null }
  ) => (await api.put<{ mensaje: string }>(`/api/citas/${idCita}/reprogramar`, datos)).data,
};
