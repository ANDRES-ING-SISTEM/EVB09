import React, { useState, useEffect } from 'react';

const AgendarCita = ({ usuarioId }) => {
  const [citas, setCitas] = useState([]);
  const [servicio, setServicio] = useState('');
  const [fechaHora, setFechaHora] = useState('');
  const [editandoId, setEditandoId] = useState(null);
  const [mensaje, setMensaje] = useState('');

  const API_URL = 'http://localhost:8080/api/citas';

  useEffect(() => {
    if (usuarioId) {
      cargarCitas();
    }
  }, [usuarioId]);

  const cargarCitas = async () => {
    try {
      const response = await fetch(`${API_URL}/usuario/${usuarioId}`);
      if (response.ok) {
        const data = await response.json();
        setCitas(data);
      }
    } catch (error) {
      console.error('Error al cargar las citas:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!servicio || !fechaHora) {
      setMensaje('Por favor completa todos los campos.');
      return;
    }

    try {
      if (editandoId) {
        const response = await fetch(`${API_URL}/actualizar/${editandoId}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ servicio, fechaHora }),
        });

        if (response.ok) {
          setMensaje('Cita actualizada exitosamente.');
          setEditandoId(null);
        }
      } else {
        const response = await fetch(`${API_URL}/agendar/${usuarioId}`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ servicio, fechaHora }),
        });

        if (response.ok) {
          setMensaje('Cita agendada exitosamente.');
        }
      }

      setServicio('');
      setFechaHora('');
      cargarCitas();
    } catch (error) {
      console.error('Error al agendar o actualizar:', error);
      setMensaje('Ocurrió un error.');
    }
  };

  const handleEditar = (cita) => {
    setEditandoId(cita.id);
    setServicio(cita.servicio);
    setFechaHora(cita.fechaHora);
  };

  const handleCancelar = async (citaId) => {
    try {
      const response = await fetch(`${API_URL}/cancelar/${citaId}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        setMensaje('Cita cancelada.');
        cargarCitas();
      }
    } catch (error) {
      console.error('Error al cancelar la cita:', error);
    }
  };

  return (
    <div style={{ maxWidth: '600px', margin: '20px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h2>{editandoId ? 'Editar Cita' : 'Agendar Nueva Cita'}</h2>
      
      {mensaje && <p style={{ color: 'blue' }}>{mensaje}</p>}

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '10px' }}>
          <label>Servicio: </label>
          <input
            type="text"
            value={servicio}
            onChange={(e) => setServicio(e.target.value)}
            placeholder="Ej: Consulta Médica, Corte de cabello"
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Fecha y Hora: </label>
          <input
            type="datetime-local"
            value={fechaHora}
            onChange={(e) => setFechaHora(e.target.value)}
            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
          />
        </div>

        <button type="submit" style={{ padding: '10px 15px', backgroundColor: '#28a745', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
          {editandoId ? 'Actualizar' : 'Agendar'}
        </button>
        {editandoId && (
          <button
            type="button"
            onClick={() => { setEditandoId(null); setServicio(''); setFechaHora(''); }}
            style={{ marginLeft: '10px', padding: '10px 15px', backgroundColor: '#6c757d', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
          >
            Cancelar Edición
          </button>
        )}
      </form>

      <hr style={{ margin: '20px 0' }} />

      <h3>Tus Citas</h3>
      {citas.length === 0 ? (
        <p>No tienes citas agendadas.</p>
      ) : (
        <ul style={{ listStyleType: 'none', padding: 0 }}>
          {citas.map((cita) => (
            <li key={cita.id} style={{ padding: '10px', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <strong>{cita.servicio}</strong> - {new Date(cita.fechaHora).toLocaleString()}
                <br />
                <small>Estado: <span style={{ fontWeight: 'bold', color: cita.estado === 'CANCELADA' ? 'red' : 'green' }}>{cita.estado}</span></small>
              </div>
              <div>
                {cita.estado !== 'CANCELADA' && (
                  <>
                    <button onClick={() => handleEditar(cita)} style={{ marginRight: '5px', padding: '5px 10px', backgroundColor: '#ffc107', border: 'none', borderRadius: '3px', cursor: 'pointer' }}>
                      Editar
                    </button>
                    <button onClick={() => handleCancelar(cita.id)} style={{ padding: '5px 10px', backgroundColor: '#dc3545', color: 'white', border: 'none', borderRadius: '3px', cursor: 'pointer' }}>
                      Cancelar
                    </button>
                  </>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default AgendarCita;
