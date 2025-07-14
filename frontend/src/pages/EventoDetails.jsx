// src/pages/EventoDetails.jsx
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import InputGroup from '../components/common/InputGroup';
import Modal from '../components/common/Modal';
import { getEventoById, deleteEvento } from '../api/eventos';
// import { getVeiculosByEventoId } from '../api/veiculos'; // Para listar veículos em eventos

function EventoDetails() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [evento, setEvento] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    // const [veiculosNoEvento, setVeiculosNoEvento] = useState([]); // Para listar veículos

    useEffect(() => {
        const fetchEvento = async () => {
            try {
                const data = await getEventoById(id);
                setEvento(data);
                // Exemplo: Se houvesse uma API para buscar veículos associados a um evento
                // const veiculosData = await getVeiculosByEventoId(id);
                // setVeiculosNoEvento(veiculosData);
            } catch (err) {
                setError('Erro ao carregar detalhes do evento: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchEvento();
        } else {
            setError('ID do evento não fornecido.');
            setLoading(false);
        }
    }, [id]);

    const handleDeleteClick = () => {
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        try {
            await deleteEvento(id);
            alert('Evento apagado com sucesso!');
            navigate(-1); // Volta para a página anterior (ex: detalhes do contratante ou estacionamento)
        } catch (err) {
            setError('Erro ao apagar evento: ' + (err.message || 'Erro desconhecido'));
            alert('Erro ao apagar evento.');
        } finally {
            setShowDeleteModal(false);
        }
    };

    const cancelDelete = () => {
        setShowDeleteModal(false);
    };

    if (loading) {
        return <Card>Carregando detalhes do evento...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    if (!evento) {
        return <Card>Evento não encontrado.</Card>;
    }

    return (
        <>
            <Card title={`Evento: ${evento.nome}`}>
                <InputGroup label="ID:" id="id" value={evento.id} readOnly />
                {evento.contratante && (
                    <InputGroup label="Contratante:" id="contratanteNome" value={evento.contratante.nome} readOnly />
                )}
                {evento.estacionamento && (
                    <InputGroup label="Estacionamento:" id="estacionamentoNome" value={evento.estacionamento.nome} readOnly />
                )}
                <InputGroup label="Nome do Evento:" id="nome" value={evento.nome} readOnly />
                <InputGroup label="Início:" id="dataInicio" value={evento.dataInicio ? evento.dataInicio.replace('T', ' ').substring(0, 16) : ''} readOnly />
                <InputGroup label="Fim:" id="dataFim" value={evento.dataFim ? evento.dataFim.replace('T', ' ').substring(0, 16) : ''} readOnly />
                <InputGroup label="Valor Diária:" id="valorDiaria" value={`R$ ${evento.valorDiaria ? evento.valorDiaria.toFixed(2) : '0.00'}`} readOnly />
                <InputGroup label="Vagas Contratadas:" id="qtdVagasContratadas" value={evento.qtdVagasContratadas} readOnly />

                <div className="button-group">
                    <Button variant="secondary" onClick={() => navigate(`/eventos/${evento.id}/edit`)}>
                        Editar
                    </Button>
                    <Button variant="danger" onClick={handleDeleteClick}>
                        Apagar
                    </Button>
                </div>
            </Card>

            {/* Não temos uma tela específica no Figma para "veículos em um evento"
                mas se fosse necessário, seria aqui:
            <Card title="Veículos no Evento">
                <ListItemCard
                    title="Placa: ABC-1234"
                    description="Entrada: 10:00 - Saída: 18:00"
                    info="Valor cobrado: R$ 50,00"
                    hasActions={false}
                />
            </Card>
            */}

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
                <p>Tem certeza que deseja APAGAR o Evento **{evento.nome} (ID: {evento.id})**?</p>
                <p>Esta ação é irreversível.</p>
            </Modal>
        </>
    );
}

export default EventoDetails;