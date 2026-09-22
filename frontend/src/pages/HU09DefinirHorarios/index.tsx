import React, { useState } from 'react';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export const HU09DefinirHorarios: React.FC = () => {
  const { usuario } = useAuth();
  const [horario, setHorario] = useState({ fecha: '', horaInicio: '', horaFin: '' });
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!usuario) {
      setMensaje({ tipo: 'danger', texto: 'Debes iniciar sesión como especialista para publicar horarios.' });
      return;
    }

    try {
      await api.post(`/api/disponibilidades/especialista/${usuario.id}`, horario);
      setMensaje({ tipo: 'success', texto: '¡Horario publicado con éxito!' });
      setHorario({ fecha: '', horaInicio: '', horaFin: '' });
    } catch (err: any) {
      setMensaje({
        tipo: 'danger',
        texto: err.response?.data?.message || err.response?.data?.error || 'Error al publicar el horario.'
      });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2>HU-09 Definir Horarios Disponibles</h2>
        <hr />
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Fecha</label>
            <input
              type="date"
              className="form-control"
              value={horario.fecha}
              onChange={(e) => setHorario({ ...horario, fecha: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Hora de Inicio</label>
            <input
              type="time"
              className="form-control"
              value={horario.horaInicio}
              onChange={(e) => setHorario({ ...horario, horaInicio: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Hora de Finalización</label>
            <input
              type="time"
              className="form-control"
              value={horario.horaFin}
              onChange={(e) => setHorario({ ...horario, horaFin: e.target.value })}
              required
            />
          </div>
          <button type="submit" className="btn btn-warning text-white">Publicar Disponibilidad</button>
        </form>
      </div>
    </div>
  );
};

export default HU09DefinirHorarios;
