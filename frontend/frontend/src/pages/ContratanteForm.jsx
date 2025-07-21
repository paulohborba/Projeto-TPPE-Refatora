import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/common/Card';
import InputGroup from '../components/common/InputGroup';
import Button from '../components/common/Button';
import { createContratante, getContratanteById, updateContratante } from '../api/contratantes';
import { getEstacionamentoById } from '../api/estacionamentos';

function ContratanteForm({ isEditing = false }) {
    const navigate = useNavigate();
    const { id, estacionamentoId } = useParams();

    const [contratante, setContratante] = useState({
        id: '',
        nome: '',
        cnpj: '',
        telefone: '',
        email: '',
        logradouro: '',
        numero: '',
        bairro: '',
        cidade: '',
        cep: '',
        estacionamento: { id: estacionamentoId || '', nome: '' }
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (isEditing && id) {
                    const data = await getContratanteById(id);
                    setContratante({ ...data, id: String(data.id) });
                } else if (estacionamentoId) {
                    const estacionamentoData = await getEstacionamentoById(estacionamentoId);
                    setContratante(prev => ({
                        ...prev,
                        estacionamento: { id: estacionamentoId, nome: estacionamentoData.nome }
                    }));
                }
            } catch (err) {
                setError('Erro ao carregar dados do contratante ou estacionamento: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [isEditing, id, estacionamentoId]);

    const handleChange = (e) => {
        const { id, value } = e.target;
        setContratante(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        const dataToSend = {
            ...contratante,
            id: contratante.id ? Number(contratante.id) : null,
            estacionamento: contratante.estacionamento.id ? { id: Number(contratante.estacionamento.id) } : null
        };

        try {
            if (isEditing) {
                await updateContratante(dataToSend.id, dataToSend);
                alert('Contratante atualizado com sucesso!');
            } else {
                await createContratante(dataToSend);
                alert('Contratante cadastrado com sucesso!');
            }
            if (estacionamentoId) {
                navigate(`/estacionamentos/${estacionamentoId}`);
            } else {
                navigate('/');
            }
        } catch (err) {
            console.error("Erro na operação:", err.response ? err.response.data : err.message);
            setError('Erro ao salvar contratante: ' + (err.response?.data?.message || err.message || 'Erro desconhecido'));
        }
    };

    if (loading) {
        return <Card>Carregando formulário...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    return (
        <Card title={isEditing ? `Contratante ${contratante.nome || ''} - Editar` : "Registrar Contratante"}>
            <form onSubmit={handleSubmit}>
                <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={contratante.id}
                    onChange={handleChange}
                    placeholder="Insira o ID do contratante"
                    readOnly={isEditing}
                    required
                />
                 {contratante.estacionamento.nome && (
                    <InputGroup
                        label="Nome do Estacionamento:"
                        id="estacionamentoNome"
                        type="text"
                        value={contratante.estacionamento.nome}
                        readOnly
                    />
                )}
                <InputGroup
                    label="Nome/Razão Social:"
                    id="nome"
                    type="text"
                    value={contratante.nome}
                    onChange={handleChange}
                    placeholder="Nome ou Razão Social"
                    required
                />
                <InputGroup
                    label="CNPJ:"
                    id="cnpj"
                    type="text"
                    value={contratante.cnpj}
                    onChange={handleChange}
                    placeholder="00.000.000/0000-00"
                    required
                />
                <InputGroup
                    label="Telefone:"
                    id="telefone"
                    type="text"
                    value={contratante.telefone}
                    onChange={handleChange}
                    placeholder="(00) 0000-0000"
                    required
                />
                <InputGroup
                    label="Email:"
                    id="email"
                    type="email"
                    value={contratante.email}
                    onChange={handleChange}
                    placeholder="contato@exemplo.com"
                    required
                />
                <InputGroup
                    label="Logradouro:"
                    id="logradouro"
                    type="text"
                    value={contratante.logradouro}
                    onChange={handleChange}
                    placeholder="Rua, Avenida, etc."
                    required
                />
                <InputGroup
                    label="Número:"
                    id="numero"
                    type="text"
                    value={contratante.numero}
                    onChange={handleChange}
                    placeholder="Número do endereço"
                    required
                />
                <InputGroup
                    label="Bairro:"
                    id="bairro"
                    type="text"
                    value={contratante.bairro}
                    onChange={handleChange}
                    placeholder="Bairro"
                    required
                />
                <InputGroup
                    label="Cidade:"
                    id="cidade"
                    type="text"
                    value={contratante.cidade}
                    onChange={handleChange}
                    placeholder="Cidade"
                    required
                />
                <InputGroup
                    label="CEP:"
                    id="cep"
                    type="text"
                    value={contratante.cep}
                    onChange={handleChange}
                    placeholder="00000-000"
                    required
                />

                <div className="button-group">
                    <Button type="submit" variant="primary">
                        {isEditing ? 'Confirmar Edição' : 'Registrar Contratante'}
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                        Cancelar
                    </Button>
                </div>
            </form>
        </Card>
    );
}

export default ContratanteForm;