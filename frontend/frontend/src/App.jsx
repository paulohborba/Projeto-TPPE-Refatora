import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './components/layouts/MainLayout';

// Importar as páginas
import DashboardEstacionamentos from './pages/DashboardEstacionamentos';
import EstacionamentoForm from './pages/EstacionamentoForm';
import EstacionamentoDetails from './pages/EstacionamentoDetails';

import VeiculoForm from './pages/VeiculoForm'; // Agora sim, este é o formulário de Veículo/Acesso
import VeiculoDetails from './pages/VeiculoDetails'; // Detalhes de Veículo/Acesso

import MensalistaForm from './pages/MensalistaForm';
import MensalistaDetails from './pages/MensalistaDetails';

import ContratanteForm from './pages/ContratanteForm';
import ContratanteDetails from './pages/ContratanteDetails';

import EventoForm from './pages/EventoForm';
import EventoDetails from './pages/EventoDetails';

import NotFoundPage from './pages/NotFoundPage'; // Uma página simples 404

function App() {
    return (
        <Router>
            <Routes>
                {/* Rota para o Dashboard de Estacionamentos */}
                <Route path="/" element={<MainLayout pageTitle="Estacionamentos"><DashboardEstacionamentos /></MainLayout>} />

                {/* Rotas para Estacionamentos */}
                <Route path="/estacionamentos/add" element={<MainLayout pageTitle="Adicionar Estacionamento"><EstacionamentoForm /></MainLayout>} />
                <Route path="/estacionamentos/:id" element={<MainLayout><EstacionamentoDetails /></MainLayout>} />
                <Route path="/estacionamentos/:id/edit" element={<MainLayout pageTitle="Editar Estacionamento"><EstacionamentoForm isEditing /></MainLayout>} />

                {/* Rotas para VEÍCULOS (que incluem dados de acesso) */}
                {/* A tela "Add Veículo" no Figma está ligada a um Estacionamento */}
                <Route path="/estacionamentos/:estacionamentoId/veiculos/add" element={<MainLayout pageTitle="Registrar Veículo"><VeiculoForm /></MainLayout>} />
                <Route path="/veiculos/:id" element={<MainLayout><VeiculoDetails /></MainLayout>} />
                <Route path="/veiculos/:id/edit" element={<MainLayout pageTitle="Editar Veículo"><VeiculoForm isEditing /></MainLayout>} />

                {/* Rotas para Mensalistas */}
                <Route path="/estacionamentos/:estacionamentoId/mensalistas/add" element={<MainLayout pageTitle="Adicionar Mensalista"><MensalistaForm /></MainLayout>} />
                <Route path="/mensalistas/:id" element={<MainLayout><MensalistaDetails /></MainLayout>} />
                <Route path="/mensalistas/:id/edit" element={<MainLayout pageTitle="Editar Mensalista"><MensalistaForm isEditing /></MainLayout>} />

                {/* Rotas para Contratantes */}
                <Route path="/estacionamentos/:estacionamentoId/contratantes/add" element={<MainLayout pageTitle="Adicionar Contratante"><ContratanteForm /></MainLayout>} />
                <Route path="/contratantes/:id" element={<MainLayout><ContratanteDetails /></MainLayout>} />
                <Route path="/contratantes/:id/edit" element={<MainLayout pageTitle="Editar Contratante"><ContratanteForm isEditing /></MainLayout>} />

                {/* Rotas para Eventos */}
                <Route path="/estacionamentos/:estacionamentoId/eventos/add" element={<MainLayout pageTitle="Adicionar Evento"><EventoForm /></MainLayout>} />
                <Route path="/contratantes/:contratanteId/eventos/add" element={<MainLayout pageTitle="Adicionar Evento ao Contratante"><EventoForm /></MainLayout>} />
                <Route path="/eventos/:id" element={<MainLayout><EventoDetails /></MainLayout>} />
                <Route path="/eventos/:id/edit" element={<MainLayout pageTitle="Editar Evento"><EventoForm isEditing /></MainLayout>} />

                {/* Rota Catch-all para 404 */}
                <Route path="*" element={<MainLayout><NotFoundPage /></MainLayout>} />
            </Routes>
        </Router>
    );
}

export default App;