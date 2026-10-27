import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../services/api';

export const HU01RegistroDeCliente: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    nombre: '',
    apellido: '',
    email: '',
    password: '',
    rol: 'PACIENTE'
  });
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
  const [cargando, setCargando] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setCargando(true);
    setMensaje({ tipo: '', texto: '' });

    try {
      await api.post('/api/auth/registro', formData);
      setMensaje({ tipo: 'success', texto: '¡Registro exitoso! Redirigiendo al inicio de sesión...' });
      setTimeout(() => navigate('/login'), 2000);
    } catch (err: any) {
      setMensaje({
        tipo: 'danger',
        texto: err.response?.data?.message || err.response?.data?.error || 'Error al registrar el usuario. Revisa los datos.'
      });
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="container mt-5 col-md-5">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Registro de Usuario</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Nombre</label>
            <input
              type="text"
              className="form-control"
              value={formData.nombre}
              onChange={(e) => setFormData({ ...formData, nombre: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Apellido</label>
            <input
              type="text"
              className="form-control"
              value={formData.apellido}
              onChange={(e) => setFormData({ ...formData, apellido: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Correo Electrónico</label>
            <input
              type="email"
              className="form-control"
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Contraseña</label>
            <input
              type="password"
              className="form-control"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              required
              minLength={6}
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Tipo de usuario</label>
            <select
              className="form-control"
              value={formData.rol}
              onChange={(e) => setFormData({ ...formData, rol: e.target.value })}
            >
              <option value="PACIENTE">Paciente</option>
              <option value="ESPECIALISTA">Especialista</option>
            </select>
          </div>
          <button type="submit" className="btn btn-primary w-100" disabled={cargando}>
            {cargando ? 'Registrando...' : 'Registrarse'}
          </button>
        </form>
        <div className="mt-3 text-center">
          <small>¿Ya tienes cuenta? <Link to="/login">Inicia sesión aquí</Link></small>
        </div>
      </div>
    </div>
  );
};

export default HU01RegistroDeCliente;
