// src/components/common/Header.jsx
import React from 'react';
import { Link } from 'react-router-dom';
import '../../styles/layout.css'; // Reutiliza os estilos de layout para o header

function Header() {
    return (
        <header className="header">
            <div className="header-content">
                <Link to="/" className="app-title">
                    Sistema de Estacionamento
                </Link>
                <nav className="main-nav">
                    <ul>
                        <li><Link to="/">Estacionamentos</Link></li>
                        {/* Se você quiser uma lista geral para Veículos, Mensalistas, Contratantes, Eventos, adicione aqui */}
                        {/* <li><Link to="/veiculos">Veículos</Link></li> */}
                        {/* <li><Link to="/mensalistas">Mensalistas</Link></li> */}
                        {/* <li><Link to="/contratantes">Contratantes</Link></li> */}
                        {/* <li><Link to="/eventos">Eventos</Link></li> */}
                    </ul>
                </nav>
            </div>
        </header>
    );
}

export default Header;