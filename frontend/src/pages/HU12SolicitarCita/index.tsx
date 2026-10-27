import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import { mensajeDeError, sprint2Service } from '../../services/sprint2.service';
import { Especialidad, Especialista } from '../../types/sprint2';

export const HU12SolicitarCita: React.FC = () => {
  const { usuario } = useAuth();
  const navigate = useNavigate();
  const [params] = useSearchParams();
  // HU-10 puede mandar al paciente aquí con el especialista y el horario ya elegidos.
  const [solicitud, setSolicitud] = useState({
    servicio: '',
    fechaHora: params.get('fechaHora') ?? '',
  });
  const [idEspecialista, setIdEspecialista] = useState(params.get('especialista') ?? '');
  const [idEspecialidad, setIdEspecialidad] = useState('');
  const [especialistas, setEspecialistas] = useState<Especialista[]>([]);
  const [especialidades, setEspecialidades] = useState<Especialidad[]>([]);
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

  // Sprint 2: listas opcionales. Si fallan, el formulario sigue funcionando como en el Sprint 1.
  useEffect(() => {
    sprint2Service.listarEspecialistas().then(setEspecialistas).catch(() => setEspecialistas([]));
    sprint2Service.listarEspecialidades().then(setEspecialidades).catch(() => setEspecialidades([]));
  }, []);

  const elegirEspecialidad = (id: string) => {
    setIdEspecialidad(id);
    const e = especialidades.find((x) => String(x.id) === id);
    if (e && solicitud.servicio.trim() === '') {
      setSolicitud({ ...solicitud, servicio: e.nombre });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!usuario) {
      navigate('/login');
      return;
    }
    try {
      const cuerpo: Record<string, unknown> = { ...solicitud };
      if (idEspecialista) cuerpo.especialista = { id: Number(idEspecialista) };
      if (idEspecialidad) cuerpo.especialidad = { id: Number(idEspecialidad) };
      await api.post(`/api/citas/agendar/${usuario.id}`, cuerpo);
      setMensaje({ tipo: 'success', texto: '¡Cita solicitada correctamente!' });
      setSolicitud({ servicio: '', fechaHora: '' });
      setIdEspecialista('');
      setIdEspecialidad('');
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'Error al enviar la solicitud de cita.') });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Solicitar Cita</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Especialista (opcional)</label>
            <select
              className="form-control"
              value={idEspecialista}
              onChange={(e) => setIdEspecialista(e.target.value)}
            >
              <option value="">Sin especialista asignado</option>
              {especialistas.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.nombre} {s.apellido}
                </option>
              ))}
            </select>
          </div>
          <div className="mb-3">
            <label className="form-label">Especialidad (opcional)</label>
            <select
              className="form-control"
              value={idEspecialidad}
              onChange={(e) => elegirEspecialidad(e.target.value)}
            >
              <option value="">Sin especialidad</option>
              {especialidades.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.nombre}
                </option>
              ))}
            </select>
          </div>
          <div className="mb-3">
            <label className="form-label">Servicio</label>
            <input
              type="text"
              className="form-control"
              value={solicitud.servicio}
              onChange={(e) => setSolicitud({ ...solicitud, servicio: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Fecha y Hora</label>
            <input
              type="datetime-local"
              className="form-control"
              value={solicitud.fechaHora}
              onChange={(e) => setSolicitud({ ...solicitud, fechaHora: e.target.value })}
              required
            />
          </div>
          <button type="submit" className="btn btn-success">Confirmar Solicitud</button>
        </form>
      </div>
    </div>
  );
};

export default HU12SolicitarCita;
