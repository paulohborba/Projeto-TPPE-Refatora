import React from 'react';
import Header from '../common/Header'; // Importa o Header
import '../../styles/layout.css'; // Estilos do layout

function MainLayout({ children }) {
    return (
        <div className="main-layout">
            <Header /> {/* O cabeçalho fixo */}
            <main className="main-content">
                {children} {/* Conteúdo dinâmico de cada página */}
            </main>
        </div>
    );
}

export default MainLayout;
