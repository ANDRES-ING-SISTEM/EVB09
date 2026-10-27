import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import HU01RegistroDeCliente from './pages/HU01RegistroDeCliente';
import HU02InicioDeSesion from './pages/HU02InicioDeSesion';
import HU06RegistrarEspecialidad from './pages/HU06RegistrarEspecialidad';
import HU09DefinirHorarios from './pages/HU09DefinirHorarios';
import HU12SolicitarCita from './pages/HU12SolicitarCita';
import HU03GestionDePerfil from './pages/HU03GestionDePerfil';
import HU04RegistroDeEspecialistas from './pages/HU04RegistroDeEspecialistas';
import HU07EditarEspecialidad from './pages/HU07EditarEspecialidad';
import HU10ConsultarDisponibilidad from './pages/HU10ConsultarDisponibilidad';
import HU11BloquearHorario from './pages/HU11BloquearHorario';
import HU13ConsultarCitas from './pages/HU13ConsultarCitas';
import HU14CancelarCita from './pages/HU14CancelarCita';
import HU15ReprogramarCita from './pages/HU15ReprogramarCita';
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
          {/* Sprint 2 */}
          <Route path="/perfil" element={<HU03GestionDePerfil />} />
          <Route path="/registrar-especialista" element={<HU04RegistroDeEspecialistas />} />
          <Route path="/editar-especialidad" element={<HU07EditarEspecialidad />} />
          <Route path="/disponibilidad" element={<HU10ConsultarDisponibilidad />} />
          <Route path="/bloquear-horario" element={<HU11BloquearHorario />} />
          <Route path="/mis-citas" element={<HU13ConsultarCitas />} />
          <Route path="/cancelar-cita" element={<HU14CancelarCita />} />
          <Route path="/reprogramar-cita" element={<HU15ReprogramarCita />} />
          <Route path="/" element={<Home />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
