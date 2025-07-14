import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import InputGroup from '../components/common/InputGroup';
import Modal from '../components/common/Modal';
import { getVeiculoById, deleteVeiculo } from '../api/veiculos';

function VeiculoDetails() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [veiculo, setVeiculo] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        const fetchVeiculo = async () => {
            try {
                const data = await getVeiculoById(id);
                setVeiculo(data);
            } catch (err) {
                setError('Erro ao carregar detalhes do veículo: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchVeiculo();
        } else {
            setError('ID do veículo não fornecido.');
            setLoading(false);
        }
    }, [id]);

    const handleDeleteClick = () => {
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        try {
            await deleteVeiculo(id);
            alert('Veículo apagado com sucesso!');
            // Redireciona para o dashboard ou para a tela de detalhes do estacionamento
            // (se este veículo pertence a um estacionamento específico e você quer voltar para lá)
            // Por simplicidade, vou para o dashboard. Ajuste conforme seu fluxo.
            navigate('/');
        } catch (err) {
            setError('Erro ao apagar veículo: ' + (err.message || 'Erro desconhecido'));
            alert('Erro ao apagar veículo.');
        } finally {
            setShowDeleteModal(false);
        }
    };

    const cancelDelete = () => {
        setShowDeleteModal(false);
    };

    if (loading) {
        return <Card>Carregando detalhes do veículo...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    if (!veiculo) {
        return <Card>Veículo não encontrado.</Card>;
    }

    return (
        <>
            <Card title={`Detalhes do Veículo (ID: ${veiculo.id})`}>
                <InputGroup label="ID:" id="id" value={veiculo.id} readOnly />
                {veiculo.estacionamento && (
                    <InputGroup label="Nome do Estacionamento:" id="estacionamentoNome" value={veiculo.estacionamento.nome} readOnly />
                )}
                <InputGroup label="Placa:" id="placa" value={veiculo.placa} readOnly />
                <InputGroup label="Marca:" id="marca" value={veiculo.marca} readOnly />
                <InputGroup label="Modelo:" id="modelo" value={veiculo.modelo} readOnly />
                <InputGroup label="Cor:" id="cor" value={veiculo.cor} readOnly />
                <InputGroup label="Tipo de Acesso:" id="tipoAcesso" value={veiculo.tipoAcesso} readOnly />
                <InputGroup label="Hora de Entrada:" id="dataEntrada" value={veiculo.dataEntrada ? veiculo.dataEntrada.replace('T', ' ').substring(0, 16) : ''} readOnly />
                <InputGroup label="Hora de Saída:" id="dataSaida" value={veiculo.dataSaida ? veiculo.dataSaida.replace('T', ' ').substring(0, 16) : ''} readOnly />
                <InputGroup label="Valor Cobrado:" id="valorCobrado" value={`R$ ${veiculo.valorCobrado ? veiculo.valorCobrado.toFixed(2) : '0.00'}`} readOnly />

                <div className="button-group">
                    <Button variant="secondary" onClick={() => navigate(`/veiculos/${veiculo.id}/edit`)}>
                        Editar
                    </Button>
                    <Button variant="danger" onClick={handleDeleteClick}>
                        Apagar
                    </Button>
                </div>
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
                <p>Tem certeza que deseja APAGAR o Veículo com placa **{veiculo.placa} (ID: {veiculo.id})**?</p>
                <p>Esta ação é irreversível.</p>
            </Modal>
        </>
    );
}

export default VeiculoDetails;