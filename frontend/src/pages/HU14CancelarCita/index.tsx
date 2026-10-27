import React, { useEffect, useState } from 'react';
import { Link, Navigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { estadoPendiente, hhmm, mensajeDeError, sprint2Service } from '../../services/sprint2.service';
import { CitaDetalle } from '../../types/sprint2';

export const HU14CancelarCita: React.FC = () => {
  const { usuario } = useAuth();
  const [params] = useSearchParams();
  const idCita = Number(params.get('id'));
  const [cita, setCita] = useState<CitaDetalle | null>(null);
  const [cargado, setCargado] = useState(false);
  const [motivo, setMotivo] = useState('');
  const [hecho, setHecho] = useState(false);
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
  const idUsuario: number | undefined = usuario?.id;

  useEffect(() => {
    if (!idUsuario) return;
    sprint2Service
      .consultarCitas(idUsuario)
      .then((citas) => setCita(citas.find((c) => c.id === idCita) ?? null))
      .catch((err) =>
        setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo cargar la cita.') })
      )
      .finally(() => setCargado(true));
  }, [idUsuario, idCita]);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  const confirmar = async () => {
    setMensaje({ tipo: '', texto: '' });
    try {
      const respuesta = await sprint2Service.cancelarCita(
        idCita,
        usuario.id,
        motivo.trim() === '' ? null : motivo.trim()
      );
      setMensaje({ tipo: 'success', texto: respuesta.mensaje });
      setHecho(true);
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo cancelar la cita.') });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Cancelar Cita</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        {!cargado && <p>Cargando cita...</p>}
        {cargado && !cita && !mensaje.texto && (
          <div className="alert alert-warning">No se encontró esa cita entre tus citas.</div>
        )}
        {cita && (
          <>
            <p>
              <strong>{cita.especialidad}</strong>
              {cita.especialista ? ` con ${cita.especialista}` : ''}
              <br />
              {cita.fechaCita} a las {hhmm(cita.horaCita)} — estado actual: {cita.estado}
            </p>
            {!hecho && estadoPendiente(cita.estado) && (
              <>
                <div className="mb-3">
                  <label className="form-label">Motivo de la cancelación (opcional)</label>
                  <textarea
                    className="form-control"
                    rows={3}
                    maxLength={200}
                    value={motivo}
                    onChange={(e) => setMotivo(e.target.value)}
                  />
                </div>
                <button className="btn btn-danger" onClick={confirmar}>Confirmar cancelación</button>
              </>
            )}
            {!hecho && !estadoPendiente(cita.estado) && (
              <div className="alert alert-info">Esta cita ya no se puede cancelar (estado {cita.estado}).</div>
            )}
          </>
        )}
        <div className="mt-3">
          <Link to="/mis-citas" className="btn btn-outline-secondary">Volver a mis citas</Link>
        </div>
      </div>
    </div>
  );
};

export default HU14CancelarCita;
