// src/pages/DashboardEstacionamentos.jsx
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import ListItemCard from '../components/common/ListItemCard';
import Modal from '../components/common/Modal'; // Para o modal de exclusão
import { getAllEstacionamentos, deleteEstacionamento } from '../api/estacionamentos'; // Importar API mockada

function DashboardEstacionamentos() {
    const [estacionamentos, setEstacionamentos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [estacionamentoToDelete, setEstacionamentoToDelete] = useState(null);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchEstacionamentos = async () => {
            try {
                const data = await getAllEstacionamentos();
                setEstacionamentos(data);
            } catch (err) {
                setError('Erro ao carregar estacionamentos: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };

        fetchEstacionamentos();
    }, []);

    const handleDeleteClick = (estacionamento) => {
        setEstacionamentoToDelete(estacionamento);
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        if (estacionamentoToDelete) {
            try {
                await deleteEstacionamento(estacionamentoToDelete.id);
                setEstacionamentos(estacionamentos.filter(e => e.id !== estacionamentoToDelete.id));
                alert('Estacionamento apagado com sucesso!');
            } catch (err) {
                setError('Erro ao apagar estacionamento: ' + (err.message || 'Erro desconhecido'));
                alert('Erro ao apagar estacionamento.');
            } finally {
                setShowDeleteModal(false);
                setEstacionamentoToDelete(null);
            }
        }
    };

    const cancelDelete = () => {
        setShowDeleteModal(false);
        setEstacionamentoToDelete(null);
    };

    if (loading) {
        return <Card>Carregando estacionamentos...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    return (
        <>
            <Card title="Estacionamentos Cadastrados">
                <div className="button-group start">
                    <Button onClick={() => navigate('/estacionamentos/add')}>
                        Adicionar Estacionamento
                    </Button>
                </div>
                {estacionamentos.length === 0 ? (
                    <p>Nenhum estacionamento cadastrado ainda.</p>
                ) : (
                    estacionamentos.map(estacionamento => (
                        <ListItemCard
                            key={estacionamento.id}
                            title={estacionamento.nome}
                            description={`Capacidade: ${estacionamento.capacidade} vagas`}
                            info={`Ocupadas: ${estacionamento.vagasOcupadas} / ${estacionamento.capacidade}`}
                            onDetails={() => navigate(`/estacionamentos/${estacionamento.id}`)}
                            onEdit={() => navigate(`/estacionamentos/${estacionamento.id}/edit`)}
                            onDelete={() => handleDeleteClick(estacionamento)}
                        />
                    ))
                )}
            </Card>

            {/* Modal de Confirmação de Exclusão */}
            <Modal
                show={showDeleteModal}
                onClose={cancelDelete}
                title="Confirmar Exclusão"
                actions={
                    <div className="button-group">
                        <Button variant="danger" onClick={confirmDelete}>Confirmar</Button>
                        <Button variant="secondary" onClick={cancelDelete}>Cancelar</Button>
                    </div>
                }
            >
                <p>Tem certeza que deseja APAGAR o Estacionamento **{estacionamentoToDelete?.nome} (ID: {estacionamentoToDelete?.id})**?</p>
                <p>Esta ação é irreversível e removerá todos os dados relacionados a ele (veículos, mensalistas, contratantes, eventos).</p>
            </Modal>
        </>
    );
}

export default DashboardEstacionamentos;