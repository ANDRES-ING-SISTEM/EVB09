import React from 'react';
import AgendarCita from './components/AgendarCita';

function App() {
  const usuarioId = 1;

  return (
    <div className="App">
      <header className="App-header">
        <h1 style={{ textAlign: 'center' }}>Sistema de Citas</h1>
      </header>
      <main>
        <AgendarCita usuarioId={usuarioId} />
      </main>
    </div>
  );
}

export default App;
