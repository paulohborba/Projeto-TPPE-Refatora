import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import ListItemCard from '../components/common/ListItemCard';
import Modal from '../components/common/Modal';
import InputGroup from '../components/common/InputGroup'; // Para os campos de detalhes
import { getEstacionamentoById, deleteEstacionamento } from '../api/estacionamentos';

function EstacionamentoDetails() {
    const { id } = useParams(); // Pega o ID do estacionamento da URL
    const navigate = useNavigate();

    const [estacionamento, setEstacionamento] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        const fetchEstacionamento = async () => {
            try {
                const data = await getEstacionamentoById(id);
                setEstacionamento(data);
            } catch (err) {
                setError('Erro ao carregar detalhes do estacionamento: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchEstacionamento();
        } else {
            setError('ID do estacionamento não fornecido.');
            setLoading(false);
        }
    }, [id]);

    const handleDeleteClick = () => {
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        try {
            await deleteEstacionamento(id);
            alert('Estacionamento apagado com sucesso!');
            navigate('/'); // Volta para o dashboard após apagar
        } catch (err) {
            setError('Erro ao apagar estacionamento: ' + (err.message || 'Erro desconhecido'));
            alert('Erro ao apagar estacionamento.');
        } finally {
            setShowDeleteModal(false);
        }
    };

    const cancelDelete = () => {
        setShowDeleteModal(false);
    };

    if (loading) {
        return <Card>Carregando detalhes do estacionamento...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    if (!estacionamento) {
        return <Card>Estacionamento não encontrado.</Card>;
    }

    return (
        <>
            <Card title={`Estacionamento ${estacionamento.nome || 'X'}`}>
                {/* Campos de Detalhes do Estacionamento - Apenas Leitura */}
                <InputGroup label="ID:" id="id" value={estacionamento.id} readOnly />
                <InputGroup label="Nome:" id="nome" value={estacionamento.nome} readOnly />
                <InputGroup label="Endereço:" id="endereco" value={estacionamento.endereco} readOnly />
                <InputGroup label="Complemento:" id="complemento" value={estacionamento.complemento} readOnly />
                <InputGroup label="Cidade:" id="cidade" value={estacionamento.cidade} readOnly />
                <InputGroup label="CEP:" id="cep" value={estacionamento.cep} readOnly />
                <InputGroup label="Telefone:" id="telefone" value={estacionamento.telefone} readOnly />
                <InputGroup label="Capacidade:" id="capacidade" value={estacionamento.capacidade} readOnly />

                <div className="button-group">
                    <Button variant="secondary" onClick={() => navigate(`/estacionamentos/${estacionamento.id}/edit`)}>
                        Editar
                    </Button>
                    <Button variant="danger" onClick={handleDeleteClick}>
                        Apagar
                    </Button>
                </div>
            </Card>

            {/* Seções de Listas (Veículos, Mensalistas, Contratantes) */}
            <Card title="Lista de Veículos">
                {/* Aqui você faria uma chamada API para buscar veículos associados a este estacionamento */}
                {/* E renderizaria com ListItemCard */}
                {/* Por enquanto, dados de exemplo */}
                <ListItemCard
                    title="ABC-1234"
                    description="Marca: Fiat, Modelo: Palio"
                    info="Entrada: 10:00"
                    onEdit={() => navigate(`/veiculos/1/edit`)} // Exemplo de navegação
                    onDelete={() => console.log('Apagar veículo 1')}
                />
                <ListItemCard
                    title="XYZ-5678"
                    description="Marca: VW, Modelo: Gol"
                    info="Saída: 15:30"
                    onEdit={() => navigate(`/veiculos/2/edit`)}
                    onDelete={() => console.log('Apagar veículo 2')}
                />
                <Button onClick={() => navigate(`/estacionamentos/${estacionamento.id}/veiculos/add`)}>
                    Adicionar veículo
                </Button>
            </Card>

            <Card title="Lista de Mensalistas">
                {/* Chamada API para mensalistas */}
                {/* Dados de exemplo */}
                <ListItemCard
                    title="João da Silva"
                    description="Placa: ABC-1234"
                    info="Valor: R$ 450"
                    onEdit={() => navigate(`/mensalistas/1/edit`)}
                    onDelete={() => console.log('Apagar mensalista 1')}
                />
                 <Button onClick={() => navigate(`/estacionamentos/${estacionamento.id}/mensalistas/add`)}>
                    Adicionar Mensalista
                </Button>
            </Card>

            <Card title="Lista de Contratantes">
                {/* Chamada API para contratantes */}
                {/* Dados de exemplo */}
                <ListItemCard
                    title="Empresa X S.A."
                    description="CPF/CNPJ: 12.345.678/0001-90"
                    info="Eventos: 3"
                    onEdit={() => navigate(`/contratantes/1/edit`)}
                    onDelete={() => console.log('Apagar contratante 1')}
                />
                <Button onClick={() => navigate(`/estacionamentos/${estacionamento.id}/contratantes/add`)}>
                    Adicionar Contratante
                </Button>
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
                <p>Tem certeza que deseja APAGAR o Estacionamento **{estacionamento.nome} (ID: {estacionamento.id})**?</p>
                <p>Esta ação é irreversível e removerá todos os dados relacionados a ele.</p>
            </Modal>
        </>
    );
}

export default EstacionamentoDetails;