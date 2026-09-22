import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../../services/api';
import { useAuth } from '../../context/AuthContext';

export const HU02InicioDeSesion: React.FC = () => {
  const navigate = useNavigate();
  const { setAuthData } = useAuth();
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setCargando(true);
    setError('');

    try {
      const response = await api.post('/api/auth/login', credentials);
      const { token, usuario } = response.data;
      setAuthData({ token, usuario });
      localStorage.setItem('token', token);
      localStorage.setItem('usuario', JSON.stringify(usuario));

      if (usuario.rol === 'ESPECIALISTA') {
        navigate('/registrar-especialidad');
      } else if (usuario.rol === 'ADMIN') {
        navigate('/registrar-especialidad');
      } else {
        navigate('/solicitar-cita');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || err.response?.data?.error || 'Credenciales inválidas.');
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="container mt-5 col-md-5">
      <div className="card shadow p-4">
        <h2 className="mb-4 text-center">HU-02 Inicio de Sesión</h2>
        {error && <div className="alert alert-danger">{error}</div>}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Correo Electrónico</label>
            <input
              type="email"
              className="form-control"
              value={credentials.email}
              onChange={(e) => setCredentials({ ...credentials, email: e.target.value })}
              required
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Contraseña</label>
            <input
              type="password"
              className="form-control"
              value={credentials.password}
              onChange={(e) => setCredentials({ ...credentials, password: e.target.value })}
              required
            />
          </div>
          <button type="submit" className="btn btn-success w-100" disabled={cargando}>
            {cargando ? 'Ingresando...' : 'Iniciar Sesión'}
          </button>
        </form>
        <div className="mt-3 text-center">
          <small>¿No tienes cuenta? <Link to="/registro">Regístrate aquí</Link></small>
        </div>
      </div>
    </div>
  );
};

export default HU02InicioDeSesion;