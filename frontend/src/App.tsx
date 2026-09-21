import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Link } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { HU01RegistroDeCliente } from './pages/HU01RegistroDeCliente';
import { HU02InicioDeSesion } from './pages/HU02InicioDeSesion';
import { Home } from './pages/Home';

const NavBar: React.FC = () => {
  const { usuario } = useAuth();
  return (
    <nav className="flex justify-center gap-4 mb-4 text-sm">
      <Link to="/" className="text-blue-600 hover:underline">Inicio</Link>
      {!usuario && (
        <>
          <Link to="/login" className="text-blue-600 hover:underline">Iniciar sesión</Link>
          <Link to="/registro" className="text-blue-600 hover:underline">Registrarse</Link>
        </>
      )}
    </nav>
  );
};

const AppRoutes: React.FC = () => (
  <>
    <NavBar />
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<HU02InicioDeSesion />} />
      <Route path="/registro" element={<HU01RegistroDeCliente />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  </>
);

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-100 p-8">
          <header className="mb-8 text-center">
            <h1 className="text-3xl font-bold text-blue-700">Sistema Citas Médicas Vitalis - Sprint 1</h1>
            <p className="text-gray-600 mt-2">Frontend y Backend en PostgreSQL conectados exitosamente</p>
          </header>
          <AppRoutes />
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;
