import React, { createContext, useContext, useState } from 'react';

interface AuthContextType {
  usuario: any | null;
  token: string | null;
  setAuthData: (data: { token: string; usuario: any }) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Lee la sesión guardada ANTES del primer render, para que al recargar la página
// (o escribir una URL) no se redirija a /login mientras se restaura la sesión.
const leerUsuarioGuardado = (): any | null => {
  try {
    const savedUser = localStorage.getItem('usuario');
    return localStorage.getItem('token') && savedUser ? JSON.parse(savedUser) : null;
  } catch {
    return null;
  }
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [usuario, setUsuario] = useState<any | null>(leerUsuarioGuardado);
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));

  const setAuthData = (data: { token: string; usuario: any }) => {
    setToken(data.token);
    setUsuario(data.usuario);
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    setToken(null);
    setUsuario(null);
  };

  return (
    <AuthContext.Provider value={{ usuario, token, setAuthData, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
};
