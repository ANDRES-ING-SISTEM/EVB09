import React, { useEffect, useState } from 'react';
import { Link, Navigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { estadoPendiente, hhmm, mensajeDeError, sprint2Service } from '../../services/sprint2.service';
import { CitaDetalle, HorarioLibre } from '../../types/sprint2';

export const HU15ReprogramarCita: React.FC = () => {
  const { usuario } = useAuth();
  const [params] = useSearchParams();
  const idCita = Number(params.get('id'));
  const [cita, setCita] = useState<CitaDetalle | null>(null);
  const [cargado, setCargado] = useState(false);
  const [horarios, setHorarios] = useState<HorarioLibre[]>([]);
  const [form, setForm] = useState({ fecha: '', hora: '', motivo: '' });
  const [hecho, setHecho] = useState(false);
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
  const idUsuario: number | undefined = usuario?.id;

  useEffect(() => {
    if (!idUsuario) return;
    sprint2Service
      .consultarCitas(idUsuario)
      .then((citas) => {
        const encontrada = citas.find((c) => c.id === idCita) ?? null;
        setCita(encontrada);
        // HU-10: se ofrecen los horarios realmente libres del especialista de la cita.
        if (encontrada && encontrada.idEspecialista) {
          sprint2Service
            .consultarDisponibles(encontrada.idEspecialista)
            .then((respuesta) => setHorarios(respuesta.data))
            .catch(() => setHorarios([]));
        }
      })
      .catch((err) =>
        setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo cargar la cita.') })
      )
      .finally(() => setCargado(true));
  }, [idUsuario, idCita]);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje({ tipo: '', texto: '' });
    try {
      const respuesta = await sprint2Service.reprogramarCita(idCita, {
        idUsuario: usuario.id,
        nuevaFechaCita: form.fecha,
        nuevaHoraCita: form.hora,
        motivo: form.motivo.trim() === '' ? null : form.motivo.trim(),
      });
      setMensaje({ tipo: 'success', texto: respuesta.mensaje });
      setHecho(true);
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo reprogramar la cita.') });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Reprogramar Cita</h2>
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
              Horario actual: {cita.fechaCita} a las {hhmm(cita.horaCita)} — estado: {cita.estado}
            </p>
            {!hecho && estadoPendiente(cita.estado) && (
              <form onSubmit={handleSubmit}>
                {horarios.length > 0 && (
                  <div className="mb-3">
                    <label className="form-label">Horarios disponibles del especialista</label>
                    <div className="d-flex flex-wrap gap-2">
                      {horarios.slice(0, 12).map((h) => (
                        <button
                          type="button"
                          key={h.id}
                          className="btn btn-sm btn-outline-primary"
                          onClick={() => setForm({ ...form, fecha: h.fecha, hora: hhmm(h.horaInicio) })}
                        >
                          {h.fecha} {hhmm(h.horaInicio)}
                        </button>
                      ))}
                    </div>
                  </div>
                )}
                <div className="mb-3">
                  <label className="form-label">Nueva fecha</label>
                  <input
                    type="date"
                    className="form-control"
                    value={form.fecha}
                    onChange={(e) => setForm({ ...form, fecha: e.target.value })}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Nueva hora</label>
                  <input
                    type="time"
                    className="form-control"
                    value={form.hora}
                    onChange={(e) => setForm({ ...form, hora: e.target.value })}
                    required
                  />
                </div>
                <div className="mb-3">
                  <label className="form-label">Motivo (opcional)</label>
                  <input
                    type="text"
                    className="form-control"
                    maxLength={200}
                    value={form.motivo}
                    onChange={(e) => setForm({ ...form, motivo: e.target.value })}
                  />
                </div>
                <button type="submit" className="btn btn-primary">Confirmar reprogramación</button>
              </form>
            )}
            {!hecho && !estadoPendiente(cita.estado) && (
              <div className="alert alert-info">Esta cita ya no se puede reprogramar (estado {cita.estado}).</div>
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

export default HU15ReprogramarCita;
