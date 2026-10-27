import React, { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { estadoPendiente, hhmm, mensajeDeError, sprint2Service } from '../../services/sprint2.service';
import { CitaDetalle } from '../../types/sprint2';

export const HU13ConsultarCitas: React.FC = () => {
  const { usuario } = useAuth();
  const [citas, setCitas] = useState<CitaDetalle[] | null>(null);
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
  const idUsuario: number | undefined = usuario?.id;

  useEffect(() => {
    if (!idUsuario) return;
    sprint2Service
      .consultarCitas(idUsuario)
      .then(setCitas)
      .catch((err) =>
        setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudieron cargar tus citas.') })
      );
  }, [idUsuario]);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="container mt-4 col-md-10">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Mis Citas</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        {citas === null && !mensaje.texto && <p>Cargando citas...</p>}
        {citas !== null && citas.length === 0 && (
          <div className="alert alert-info">
            Aún no tienes citas. <Link to="/solicitar-cita">Solicita una</Link>.
          </div>
        )}
        {citas !== null && citas.length > 0 && (
          <table className="table table-striped align-middle">
            <thead>
              <tr>
                <th>Especialidad</th>
                <th>Especialista</th>
                <th>Fecha</th>
                <th>Hora</th>
                <th>Estado</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {citas.map((c) => (
                <tr key={c.id}>
                  <td>{c.especialidad}</td>
                  <td>{c.especialista ?? '—'}</td>
                  <td>{c.fechaCita}</td>
                  <td>{hhmm(c.horaCita)}</td>
                  <td>{c.estado}</td>
                  <td>
                    {estadoPendiente(c.estado) && (
                      <>
                        <Link className="btn btn-sm btn-outline-primary" to={`/reprogramar-cita?id=${c.id}`}>
                          Reprogramar
                        </Link>{' '}
                        <Link className="btn btn-sm btn-outline-danger" to={`/cancelar-cita?id=${c.id}`}>
                          Cancelar
                        </Link>
                      </>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
        <div className="mt-2">
          <Link to="/" className="btn btn-outline-secondary">Volver</Link>
        </div>
      </div>
    </div>
  );
};

export default HU13ConsultarCitas;
