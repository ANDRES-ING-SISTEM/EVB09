import React, { useState } from 'react';
import api from '../../services/api';

export const HU06RegistrarEspecialidad: React.FC = () => {
  const [especialidad, setEspecialidad] = useState({ nombre: '', descripcion: '' });
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/especialidades', especialidad);
      setMensaje({ tipo: 'success', texto: '¡Especialidad registrada exitosamente!' });
      setEspecialidad({ nombre: '', descripcion: '' });
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: 'Error al registrar especialidad.' });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2>HU-06 Registrar Especialidad</h2>
        <hr />
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label">Nombre de la Especialidad</label>
            <input 
              type="text" 
              className="form-control" 
              value={especialidad.nombre} 
              onChange={(e) => setEspecialidad({ ...especialidad, nombre: e.target.value })} 
              required 
            />
          </div>
          <div className="mb-3">
            <label className="form-label">Descripción</label>
            <textarea 
              className="form-control" 
              rows={3} 
              value={especialidad.descripcion} 
              onChange={(e) => setEspecialidad({ ...especialidad, descripcion: e.target.value })} 
            />
          </div>
          <button type="submit" className="btn btn-primary">Guardar Especialidad</button>
        </form>
      </div>
    </div>
  );
};

export default HU06RegistrarEspecialidad;
