import React, { useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { mensajeDeError, sprint2Service } from '../../services/sprint2.service';

const FORM_VACIO = {
  nombre: '',
  apellido: '',
  email: '',
  telefono: '',
  registroMedico: '',
  password: '',
  confirmarPassword: '',
};

export const HU04RegistroDeEspecialistas: React.FC = () => {
  const { usuario } = useAuth();
  const [form, setForm] = useState(FORM_VACIO);
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  if (usuario.rol !== 'ADMIN') {
    return (
      <div className="container mt-4 col-md-6">
        <div className="alert alert-warning">
          Solo un administrador puede registrar especialistas. <Link to="/">Volver</Link>
        </div>
      </div>
    );
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje({ tipo: '', texto: '' });
    if (form.password !== form.confirmarPassword) {
      setMensaje({ tipo: 'danger', texto: 'Las contraseñas no coinciden.' });
      return;
    }
    try {
      const creado = await sprint2Service.registrarEspecialista({
        nombre: form.nombre.trim(),
        apellido: form.apellido.trim(),
        email: form.email.trim(),
        password: form.password,
        confirmarPassword: form.confirmarPassword,
        telefono: form.telefono.trim() === '' ? null : form.telefono.trim(),
        registroMedico: form.registroMedico.trim(),
      });
      setMensaje({
        tipo: 'success',
        texto: `Especialista ${creado.nombre} ${creado.apellido} registrado correctamente.`,
      });
      setForm(FORM_VACIO);
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo registrar al especialista.') });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Registrar Especialista</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

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
            <label className="form-label">Apellido</label>
            <input
              type="text"
              className="form-control"
              value={form.apellido}
              onChange={(e) => setForm({ ...form, apellido: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Correo electrónico</label>
            <input
              type="email"
              className="form-control"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Teléfono (opcional)</label>
            <input
              type="text"
              className="form-control"
              value={form.telefono}
              onChange={(e) => setForm({ ...form, telefono: e.target.value })}
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Registro médico</label>
            <input
              type="text"
              className="form-control"
              value={form.registroMedico}
              onChange={(e) => setForm({ ...form, registroMedico: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Contraseña (mínimo 8 caracteres)</label>
            <input
              type="password"
              className="form-control"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
              minLength={8}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Confirmar contraseña</label>
            <input
              type="password"
              className="form-control"
              value={form.confirmarPassword}
              onChange={(e) => setForm({ ...form, confirmarPassword: e.target.value })}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary">Registrar especialista</button>{' '}
          <Link to="/" className="btn btn-outline-secondary">Volver</Link>
        </form>
      </div>
    </div>
  );
};

export default HU04RegistroDeEspecialistas;
