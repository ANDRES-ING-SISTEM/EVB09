import React, { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { mensajeDeError, sprint2Service } from '../../services/sprint2.service';
import { Especialidad } from '../../types/sprint2';

export const HU07EditarEspecialidad: React.FC = () => {
  const { usuario } = useAuth();
  const [especialidades, setEspecialidades] = useState<Especialidad[]>([]);
  const [idSeleccionada, setIdSeleccionada] = useState('');
  const [form, setForm] = useState({ nombre: '', descripcion: '', duracionMinutos: '' });
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

  const cargar = () =>
    sprint2Service
      .listarEspecialidades()
      .then(setEspecialidades)
      .catch((err) =>
        setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudieron cargar las especialidades.') })
      );

  useEffect(() => {
    cargar();
  }, []);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  if (usuario.rol !== 'ADMIN' && usuario.rol !== 'ESPECIALISTA') {
    return (
      <div className="container mt-4 col-md-6">
        <div className="alert alert-warning">
          Solo administradores y especialistas pueden editar especialidades. <Link to="/">Volver</Link>
        </div>
      </div>
    );
  }

  const seleccionar = (id: string) => {
    setIdSeleccionada(id);
    setMensaje({ tipo: '', texto: '' });
    const e = especialidades.find((x) => String(x.id) === id);
    setForm(
      e
        ? {
            nombre: e.nombre,
            descripcion: e.descripcion ?? '',
            duracionMinutos: e.duracionMinutos ? String(e.duracionMinutos) : '',
          }
        : { nombre: '', descripcion: '', duracionMinutos: '' }
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje({ tipo: '', texto: '' });
    try {
      await sprint2Service.editarEspecialidad(Number(idSeleccionada), {
        nombre: form.nombre.trim(),
        descripcion: form.descripcion.trim(),
        duracionMinutos: Number(form.duracionMinutos),
      });
      setMensaje({ tipo: 'success', texto: 'Especialidad actualizada correctamente.' });
      cargar();
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo actualizar la especialidad.') });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Editar Especialidad</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        <div className="mb-3">
          <label className="form-label">Especialidad</label>
          <select
            className="form-control"
            value={idSeleccionada}
            onChange={(e) => seleccionar(e.target.value)}
          >
            <option value="">Selecciona una especialidad...</option>
            {especialidades.map((e) => (
              <option key={e.id} value={e.id}>
                {e.nombre}
              </option>
            ))}
          </select>
        </div>

        {idSeleccionada && (
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Nombre</label>
              <input
                type="text"
                className="form-control"
                value={form.nombre}
                onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Descripción</label>
              <textarea
                className="form-control"
                rows={3}
                value={form.descripcion}
                onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Duración de la consulta (minutos, mínimo 15)</label>
              <input
                type="number"
                min={15}
                className="form-control"
                value={form.duracionMinutos}
                onChange={(e) => setForm({ ...form, duracionMinutos: e.target.value })}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary">Guardar cambios</button>
          </form>
        )}
        <div className="mt-3">
          <Link to="/" className="btn btn-outline-secondary">Volver</Link>
        </div>
      </div>
    </div>
  );
};

export default HU07EditarEspecialidad;
