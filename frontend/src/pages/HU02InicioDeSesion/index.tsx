import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../../services/auth.service';
import { useAuth } from '../../context/AuthContext';

export const HU02InicioDeSesion: React.FC = () => {
  const navigate = useNavigate();
  const { setAuthData } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [cargando, setCargando] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setCargando(true);
    try {
      const data = await authService.login({ email, password });
      setAuthData({ token: data.token, usuario: data.usuario });
      navigate('/');
    } catch (err: any) {
      const mensaje =
        err?.response?.data?.message ||
        err?.response?.data?.error ||
        'No fue posible iniciar sesión. Verifica tus credenciales.';
      setError(mensaje);
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="p-6 max-w-md mx-auto bg-white rounded-xl shadow-md">
      <h2 className="text-2xl font-bold mb-4 text-blue-600">HU02 - Inicio de Sesión</h2>
      <p className="text-gray-600 mb-6">
        Formulario de autenticación con generación de token JWT.
      </p>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
            Correo electrónico
          </label>
          <input
            id="email"
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="correo@ejemplo.com"
          />
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
            Contraseña
          </label>
          <input
            id="password"
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="••••••••"
          />
        </div>

        {error && (
          <div className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg px-3 py-2">
            {error}
          </div>
        )}

        <button
          type="submit"
          disabled={cargando}
          className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white font-semibold py-2 rounded-lg transition-colors"
        >
          {cargando ? 'Ingresando...' : 'Iniciar sesión'}
        </button>
      </form>

      <p className="text-sm text-gray-500 mt-4 text-center">
        ¿No tienes cuenta?{' '}
        <Link to="/registro" className="text-blue-600 hover:underline">
          Regístrate
        </Link>
      </p>
    </div>
  );
};

export default HU02InicioDeSesion;
