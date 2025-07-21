import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Card from '../components/common/Card';
import Button from '../components/common/Button';
import InputGroup from '../components/common/InputGroup';
import ListItemCard from '../components/common/ListItemCard';
import Modal from '../components/common/Modal';
import { getContratanteById, deleteContratante } from '../api/contratantes';

function ContratanteDetails() {
    const { id } = useParams();
    const navigate = useNavigate();

    const [contratante, setContratante] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    useEffect(() => {
        const fetchContratante = async () => {
            try {
                const data = await getContratanteById(id);
                setContratante(data);
            } catch (err) {
                setError('Erro ao carregar detalhes do contratante: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };

        if (id) {
            fetchContratante();
        } else {
            setError('ID do contratante não fornecido.');
            setLoading(false);
        }
    }, [id]);

    const handleDeleteClick = () => {
        setShowDeleteModal(true);
    };

    const confirmDelete = async () => {
        try {
            await deleteContratante(id);
            alert('Contratante apagado com sucesso!');
            navigate(-1);
        } catch (err) {
            setError('Erro ao apagar contratante: ' + (err.message || 'Erro desconhecido'));
            alert('Erro ao apagar contratante.');
        } finally {
            setShowDeleteModal(false);
        }
    };

    const cancelDelete = () => {
        setShowDeleteModal(false);
    };

    if (loading) {
        return <Card>Carregando detalhes do contratante...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    if (!contratante) {
        return <Card>Contratante não encontrado.</Card>;
    }

    return (
        <>
            <Card title={`Contratante: ${contratante.nome}`}>
                <InputGroup label="ID:" id="id" value={contratante.id} readOnly />
                {contratante.estacionamento && (
                    <InputGroup label="Nome do Estacionamento:" id="estacionamentoNome" value={contratante.estacionamento.nome} readOnly />
                )}
                <InputGroup label="Nome/Razão Social:" id="nome" value={contratante.nome} readOnly />
                <InputGroup label="CNPJ:" id="cnpj" value={contratante.cnpj} readOnly />
                <InputGroup label="Telefone:" id="telefone" value={contratante.telefone} readOnly />
                <InputGroup label="Email:" id="email" value={contratante.email} readOnly />
                <InputGroup label="Logradouro:" id="logradouro" value={contratante.logradouro} readOnly />
                <InputGroup label="Número:" id="numero" value={contratante.numero} readOnly />
                <InputGroup label="Bairro:" id="bairro" value={contratante.bairro} readOnly />
                <InputGroup label="Cidade:" id="cidade" value={contratante.cidade} readOnly />
                <InputGroup label="CEP:" id="cep" value={contratante.cep} readOnly />

                <div className="button-group">
                    <Button variant="secondary" onClick={() => navigate(`/contratantes/${contratante.id}/edit`)}>
                        Editar
                    </Button>
                    <Button variant="danger" onClick={handleDeleteClick}>
                        Apagar
                    </Button>
                </div>
            </Card>

            <Card title="Eventos do Contratante">
                <ListItemCard
                    title="Conferência Anual Tech"
                    description="De 20/08/2025 a 22/08/2025"
                    info="Local: Estacionamento Central"
                    onEdit={() => navigate(`/eventos/1/edit`)}
                    onDelete={() => console.log('Apagar evento 1')}
                />
                <ListItemCard
                    title="Feira de Negócios 2025"
                    description="De 05/09/2025 a 07/09/2025"
                    info="Local: Park Fácil"
                    onEdit={() => navigate(`/eventos/2/edit`)}
                    onDelete={() => console.log('Apagar evento 2')}
                />
                 <Button onClick={() => navigate(`/contratantes/${contratante.id}/eventos/add`)}>
                    Adicionar Evento
                </Button>
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
                <p>Tem certeza que deseja APAGAR o Contratante **{contratante.nome} (ID: {contratante.id})**?</p>
                <p>Esta ação é irreversível.</p>
            </Modal>
        </>
    );
}

export default ContratanteDetails;