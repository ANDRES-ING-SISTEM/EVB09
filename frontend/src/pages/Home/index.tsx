import React from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const Home: React.FC = () => {
  const { usuario, logout } = useAuth();

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  return (
    <div className="p-6 max-w-md mx-auto bg-white rounded-xl shadow-md text-center">
      <h2 className="text-2xl font-bold mb-2 text-blue-600">
        Bienvenido/a, {usuario.nombre}
      </h2>
      <p className="text-gray-600 mb-1">Correo: {usuario.email}</p>
      <p className="text-gray-600 mb-6">Rol: {usuario.rol}</p>
      <button
        onClick={logout}
        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-4 py-2 rounded-lg transition-colors"
      >
        Cerrar sesión
      </button>
      <p className="text-xs text-gray-400 mt-6">
        Las demás historias de usuario (especialidades, horarios, citas) aún no
        están implementadas en este Sprint.{' '}
        <Link to="/login" className="text-blue-600 hover:underline">
          Volver
        </Link>
      </p>
    </div>
  );
};

export default Home;

