import React, { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { hhmm, mensajeDeError, sprint2Service } from '../../services/sprint2.service';
import { Especialista, HorarioLibre } from '../../types/sprint2';

export const HU10ConsultarDisponibilidad: React.FC = () => {
  const { usuario } = useAuth();
  const [especialistas, setEspecialistas] = useState<Especialista[]>([]);
  const [idEspecialista, setIdEspecialista] = useState('');
  const [fecha, setFecha] = useState('');
  const [horarios, setHorarios] = useState<HorarioLibre[] | null>(null);
  const [aviso, setAviso] = useState('');
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

  useEffect(() => {
    sprint2Service
      .listarEspecialistas()
      .then(setEspecialistas)
      .catch((err) =>
        setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudieron cargar los especialistas.') })
      );
  }, []);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje({ tipo: '', texto: '' });
    setHorarios(null);
    try {
      const respuesta = await sprint2Service.consultarDisponibles(Number(idEspecialista), fecha || undefined);
      setHorarios(respuesta.data);
      setAviso(respuesta.mensaje);
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo consultar la disponibilidad.') });
    }
  };

  return (
    <div className="container mt-4 col-md-8">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Consultar Disponibilidad</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        <form onSubmit={handleSubmit} className="row g-3 mb-3">
          <div className="col-md-6">
            <label className="form-label">Especialista</label>
            <select
              className="form-control"
              value={idEspecialista}
              onChange={(e) => setIdEspecialista(e.target.value)}
              required
            >
              <option value="">Selecciona un especialista...</option>
              {especialistas.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.nombre} {s.apellido}
                </option>
              ))}
            </select>
          </div>
          <div className="col-md-4">
            <label className="form-label">Fecha (opcional)</label>
            <input
              type="date"
              className="form-control"
              value={fecha}
              onChange={(e) => setFecha(e.target.value)}
            />
          </div>
          <div className="col-md-2 d-flex align-items-end">
            <button type="submit" className="btn btn-primary w-100">Consultar</button>
          </div>
        </form>

        {horarios !== null && (
          <>
            <div className={`alert alert-${horarios.length > 0 ? 'success' : 'info'}`}>{aviso}</div>
            {horarios.length > 0 && (
              <table className="table table-striped">
                <thead>
                  <tr>
                    <th>Fecha</th>
                    <th>Inicio</th>
                    <th>Fin</th>
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {horarios.map((h) => (
                    <tr key={h.id}>
                      <td>{h.fecha}</td>
                      <td>{hhmm(h.horaInicio)}</td>
                      <td>{hhmm(h.horaFin)}</td>
                      <td>
                        {usuario.rol === 'PACIENTE' && (
                          <Link
                            className="btn btn-sm btn-outline-success"
                            to={`/solicitar-cita?especialista=${idEspecialista}&fechaHora=${h.fecha}T${hhmm(h.horaInicio)}`}
                          >
                            Solicitar cita
                          </Link>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </>
        )}
        <div className="mt-2">
          <Link to="/" className="btn btn-outline-secondary">Volver</Link>
        </div>
      </div>
    </div>
  );
};

export default HU10ConsultarDisponibilidad;
