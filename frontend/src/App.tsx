import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import HU01RegistroDeCliente from './pages/HU01RegistroDeCliente';
import HU02InicioDeSesion from './pages/HU02InicioDeSesion';
import HU06RegistrarEspecialidad from './pages/HU06RegistrarEspecialidad';
import HU09DefinirHorarios from './pages/HU09DefinirHorarios';
import HU12SolicitarCita from './pages/HU12SolicitarCita';
import Home from './pages/Home';

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/registro" element={<HU01RegistroDeCliente />} />
          <Route path="/login" element={<HU02InicioDeSesion />} />
          <Route path="/registrar-especialidad" element={<HU06RegistrarEspecialidad />} />
          <Route path="/definir-horarios" element={<HU09DefinirHorarios />} />
          <Route path="/solicitar-cita" element={<HU12SolicitarCita />} />
          <Route path="/" element={<Home />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
