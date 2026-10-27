import React, { useEffect, useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { mensajeDeError, sprint2Service } from '../../services/sprint2.service';

export const HU03GestionDePerfil: React.FC = () => {
  const { usuario, token, setAuthData } = useAuth();
  const [form, setForm] = useState({ nombre: '', apellido: '', email: '', telefono: '' });
  const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
  const [cargando, setCargando] = useState(true);
  const idUsuario: number | undefined = usuario?.id;

  useEffect(() => {
    if (!idUsuario) return;
    sprint2Service
      .consultarPerfil(idUsuario)
      .then((perfil) =>
        setForm({
          nombre: perfil.nombre,
          apellido: perfil.apellido,
          email: perfil.email,
          telefono: perfil.telefono ?? '',
        })
      )
      .catch((err) =>
        setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo cargar el perfil.') })
      )
      .finally(() => setCargando(false));
  }, [idUsuario]);

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMensaje({ tipo: '', texto: '' });
    try {
      const perfil = await sprint2Service.actualizarPerfil(usuario.id, {
        nombre: form.nombre.trim(),
        apellido: form.apellido.trim(),
        email: form.email.trim(),
        telefono: form.telefono.trim() === '' ? null : form.telefono.trim(),
      });
      // Se actualiza la sesión local para que el saludo de Home muestre el nombre nuevo.
      const actualizado = {
        ...usuario,
        nombre: perfil.nombre,
        apellido: perfil.apellido,
        email: perfil.email,
      };
      localStorage.setItem('usuario', JSON.stringify(actualizado));
      setAuthData({ token: token ?? '', usuario: actualizado });
      const cambioCorreo = perfil.email !== usuario.email;
      setMensaje({
        tipo: 'success',
        texto: cambioCorreo
          ? 'Perfil actualizado. Como cambiaste el correo, vuelve a iniciar sesión para renovar tu sesión.'
          : 'Perfil actualizado correctamente.',
      });
    } catch (err: any) {
      setMensaje({ tipo: 'danger', texto: mensajeDeError(err, 'No se pudo actualizar el perfil.') });
    }
  };

  return (
    <div className="container mt-4 col-md-6">
      <div className="card shadow p-4">
        <h2 className="h4 fw-bold text-center mb-4">Gestión de Perfil</h2>
        {mensaje.texto && <div className={`alert alert-${mensaje.tipo}`}>{mensaje.texto}</div>}
        {cargando ? (
          <p>Cargando perfil...</p>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Nombre</label>
              <input
                type="text"
                className="form-control"
                value={form.nombre}
                onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Apellido</label>
              <input
                type="text"
                className="form-control"
                value={form.apellido}
                onChange={(e) => setForm({ ...form, apellido: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Correo electrónico</label>
              <input
                type="email"
                className="form-control"
                value={form.email}
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Teléfono (7 a 15 dígitos, opcional)</label>
              <input
                type="text"
                className="form-control"
                value={form.telefono}
                onChange={(e) => setForm({ ...form, telefono: e.target.value })}
              />
            </div>
            <button type="submit" className="btn btn-primary">Guardar cambios</button>{' '}
            <Link to="/" className="btn btn-outline-secondary">Volver</Link>
          </form>
        )}
      </div>
    </div>
  );
};

export default HU03GestionDePerfil;
