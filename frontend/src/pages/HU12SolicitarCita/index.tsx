import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export const HU12SolicitarCita: React.FC = () => {
  const { usuario } = useAuth();
  const navigate = useNavigate();
  const [solicitud, setSolicitud] = useState({ servicio: '', fechaHora: '' });
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!usuario) {
      navigate('/login');
      return;
    }
    try {
      await api.post(`/api/citas/agendar/${usuario.id}`, solicitud);
      setMensaje({ tipo: 'success', texto: '¡Cita solicitada correctamente!' });
      setSolicitud({ servicio: '', fechaHora: '' });
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: 'Error al enviar la solicitud de cita.' });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2>HU-12 Solicitar Cita</h2>
        <hr />
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        <form onSubmit={handleSubmit}>
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
