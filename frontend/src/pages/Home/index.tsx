import React from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

interface Opcion {
  ruta: string;
  texto: string;
  roles?: string[];
}

const OPCIONES: Opcion[] = [
  { ruta: '/perfil', texto: 'Mi perfil' },
  { ruta: '/disponibilidad', texto: 'Consultar disponibilidad' },
  { ruta: '/solicitar-cita', texto: 'Solicitar cita', roles: ['PACIENTE'] },
  { ruta: '/mis-citas', texto: 'Mis citas (cancelar / reprogramar)', roles: ['PACIENTE'] },
  { ruta: '/definir-horarios', texto: 'Definir horarios', roles: ['ESPECIALISTA'] },
  { ruta: '/bloquear-horario', texto: 'Bloquear horario', roles: ['ESPECIALISTA'] },
  { ruta: '/registrar-especialidad', texto: 'Registrar especialidad', roles: ['ESPECIALISTA', 'ADMIN'] },
  { ruta: '/editar-especialidad', texto: 'Editar especialidad', roles: ['ESPECIALISTA', 'ADMIN'] },
  { ruta: '/registrar-especialista', texto: 'Registrar especialista', roles: ['ADMIN'] },
];

export const Home: React.FC = () => {
  const { usuario, logout } = useAuth();

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  const visibles = OPCIONES.filter((o) => !o.roles || o.roles.includes(usuario.rol));

  return (
    <div className="p-6 max-w-md mx-auto bg-white rounded-xl shadow-md text-center">
      <h2 className="text-2xl font-bold mb-2 text-blue-600">
        Bienvenido/a, {usuario.nombre}
      </h2>
      <p className="text-gray-600 mb-1">Correo: {usuario.email}</p>
      <p className="text-gray-600 mb-4">Rol: {usuario.rol}</p>

      <ul className="text-left mb-6 list-none p-0">
        {visibles.map((o) => (
          <li key={o.ruta} className="mb-1">
            <Link to={o.ruta} className="text-blue-600 hover:underline">
              {o.texto}
            </Link>
          </li>
        ))}
      </ul>

      <button
        onClick={logout}
        className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-semibold px-4 py-2 rounded-lg transition-colors"
      >
        Cerrar sesión
      </button>
    </div>
  );
};

export default Home;
