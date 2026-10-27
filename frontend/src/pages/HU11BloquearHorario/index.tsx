import React, { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { hhmm, mensajeDeError, sprint2Service } from '../../services/sprint2.service';
import { Bloqueo } from '../../types/sprint2';

const FORM_VACIO = { fecha: '', horaInicio: '', horaFin: '', motivo: '' };

export const HU11BloquearHorario: React.FC = () => {
  const { usuario } = useAuth();
  const [form, setForm] = useState(FORM_VACIO);
  const [bloqueos, setBloqueos] = useState<Bloqueo[]>([]);
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
  const idEspecialista: number | undefined = usuario?.id;
  const esEspecialista = usuario?.rol === 'ESPECIALISTA';

  const cargar = (id: number) =>
    sprint2Service
      .listarBloqueos(id)
      .then(setBloqueos)
      .catch((err) =>
        setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudieron cargar los bloqueos.') })
      );

  useEffect(() => {
    if (idEspecialista && esEspecialista) {
      cargar(idEspecialista);
    }
  }, [idEspecialista, esEspecialista]);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  if (!esEspecialista) {
    return (
      <div className="container mt-4 col-md-6">
        <div className="alert alert-warning">
          Solo un especialista puede bloquear sus horarios. <Link to="/">Volver</Link>
        </div>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje({ tipo: '', texto: '' });
    try {
      const respuesta = await sprint2Service.bloquearHorario(usuario.id, {
        fecha: form.fecha,
        horaInicio: form.horaInicio,
        horaFin: form.horaFin,
        motivo: form.motivo.trim() === '' ? null : form.motivo.trim(),
      });
      setMensaje({ tipo: 'success', texto: respuesta.mensaje });
      setForm(FORM_VACIO);
      cargar(usuario.id);
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo bloquear el horario.') });
    }
  };

  const desbloquear = async (idBloqueo: number) => {
    setMensaje({ tipo: '', texto: '' });
    try {
      const respuesta = await sprint2Service.desbloquearHorario(idBloqueo);
      setMensaje({ tipo: 'success', texto: respuesta.mensaje });
      cargar(usuario.id);
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo desbloquear el horario.') });
    }
  };

  return (
    <div className="container mt-4 col-md-8">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Bloquear Horario</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        <form onSubmit={handleSubmit} className="row g-3 mb-4">
          <div className="col-md-4">
            <label className="form-label">Fecha</label>
            <input
              type="date"
              className="form-control"
              value={form.fecha}
              onChange={(e) => setForm({ ...form, fecha: e.target.value })}
              required
            />
          </div>
          <div className="col-md-4">
            <label className="form-label">Hora de inicio</label>
            <input
              type="time"
              className="form-control"
              value={form.horaInicio}
              onChange={(e) => setForm({ ...form, horaInicio: e.target.value })}
              required
            />
          </div>
          <div className="col-md-4">
            <label className="form-label">Hora de fin</label>
            <input
              type="time"
              className="form-control"
              value={form.horaFin}
              onChange={(e) => setForm({ ...form, horaFin: e.target.value })}
              required
            />
          </div>
          <div className="col-12">
            <label className="form-label">Motivo (opcional)</label>
            <input
              type="text"
              className="form-control"
              maxLength={200}
              value={form.motivo}
              onChange={(e) => setForm({ ...form, motivo: e.target.value })}
            />
          </div>
          <div className="col-12">
            <button type="submit" className="btn btn-primary">Bloquear horario</button>
          </div>
        </form>

        <h5>Bloqueos activos</h5>
        {bloqueos.length === 0 ? (
          <p className="text-muted">No tienes bloqueos activos.</p>
        ) : (
          <table className="table table-striped">
            <thead>
              <tr>
                <th>Fecha</th>
                <th>Inicio</th>
                <th>Fin</th>
                <th>Motivo</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {bloqueos.map((b) => (
                <tr key={b.idBloqueo}>
                  <td>{b.fecha}</td>
                  <td>{hhmm(b.horaInicio)}</td>
                  <td>{hhmm(b.horaFin)}</td>
                  <td>{b.motivo ?? '—'}</td>
                  <td>
                    <button className="btn btn-sm btn-outline-danger" onClick={() => desbloquear(b.idBloqueo)}>
                      Desbloquear
                    </button>
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

export default HU11BloquearHorario;
