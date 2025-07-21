import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import InputGroup from '../components/common/InputGroup';
import ListItemCard from '../components/common/ListItemCard';
import Modal from '../components/common/Modal';
import { getMensalistaById, deleteMensalista } from '../api/mensalistas';

function MensalistaDetails() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [mensalista, setMensalista] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        const fetchMensalista = async () => {
            try {
                const data = await getMensalistaById(id);
                setMensalista(data);
            } catch (err) {
                setError('Erro ao carregar detalhes do mensalista: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchMensalista();
        } else {
            setError('ID do mensalista não fornecido.');
            setLoading(false);
        }
    }, [id]);

    const handleDeleteClick = () => {
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        try {
            await deleteMensalista(id);
            alert('Mensalista apagado com sucesso!');
            navigate(-1);
        } catch (err) {
            setError('Erro ao apagar mensalista: ' + (err.message || 'Erro desconhecido'));
            alert('Erro ao apagar mensalista.');
        } finally {
            setShowDeleteModal(false);
        }
    };

    const cancelDelete = () => {
        setShowDeleteModal(false);
    };

    if (loading) {
        return <Card>Carregando detalhes do mensalista...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    if (!mensalista) {
        return <Card>Mensalista não encontrado.</Card>;
    }

    return (
        <>
            <Card title={`Mensalista: ${mensalista.nome}`}>
                <InputGroup label="ID:" id="id" value={mensalista.id} readOnly />
                {mensalista.estacionamento && (
                    <InputGroup label="Nome do Estacionamento:" id="estacionamentoNome" value={mensalista.estacionamento.nome} readOnly />
                )}
                <InputGroup label="Nome:" id="nome" value={mensalista.nome} readOnly />
                <InputGroup label="CPF:" id="cpf" value={mensalista.cpf} readOnly />
                <InputGroup label="Telefone:" id="telefone" value={mensalista.telefone} readOnly />
                <InputGroup label="Placa do Veículo:" id="placaVeiculo" value={mensalista.placaVeiculo} readOnly />
                <InputGroup label="Vencimento Contrato:" id="vencimentoContrato" value={mensalista.vencimentoContrato} readOnly />
                <InputGroup label="Valor Mensal:" id="valorMensal" value={`R$ ${mensalista.valorMensal.toFixed(2)}`} readOnly />

                <div className="button-group">
                    <Button variant="secondary" onClick={() => navigate(`/mensalistas/${mensalista.id}/edit`)}>
                        Editar
                    </Button>
                    <Button variant="danger" onClick={handleDeleteClick}>
                        Apagar
                    </Button>
                </div>
            </Card>

            <Card title="Acessos do Mensalista">
                <ListItemCard
                    title="Acesso em 14/07/2025"
                    description="Entrada: 08:00 - Saída: 18:00"
                    info="Placa: ABC1234"
                    hasActions={false}
                />
                 <ListItemCard
                    title="Acesso em 13/07/2025"
                    description="Entrada: 09:00 - Saída: 17:00"
                    info="Placa: ABC1234"
                    hasActions={false}
                />
            </Card>

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
                <p>Tem certeza que deseja APAGAR o Mensalista **{mensalista.nome} (ID: {mensalista.id})**?</p>
                <p>Esta ação é irreversível.</p>
            </Modal>
        </>
    );
}

export default MensalistaDetails;