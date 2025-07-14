import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/common/Card';
import InputGroup from '../components/common/InputGroup';
import Button from '../components/common/Button';
import { createMensalista, getMensalistaById, updateMensalista } from '../api/mensalistas';
import { getEstacionamentoById } from '../api/estacionamentos'; // Para obter o nome do estacionamento

function MensalistaForm({ isEditing = false }) {
    const navigate = useNavigate();
    const { id, estacionamentoId } = useParams();

    const [mensalista, setMensalista] = useState({
        id: '', // ID manual
        nome: '',
        cpf: '',
        telefone: '',
        placaVeiculo: '',
        vencimentoContrato: '', // YYYY-MM-DD
        valorMensal: '',
        estacionamento: { id: estacionamentoId || '', nome: '' }
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (isEditing && id) {
                    const data = await getMensalistaById(id);
                    setMensalista({
                        ...data,
                        id: String(data.id),
                        valorMensal: data.valorMensal ? String(data.valorMensal) : '',
                        vencimentoContrato: data.vencimentoContrato ? data.vencimentoContrato.split('T')[0] : '' // Formato YYYY-MM-DD
                    });
                } else if (estacionamentoId) {
                    const estacionamentoData = await getEstacionamentoById(estacionamentoId);
                    setMensalista(prev => ({
                        ...prev,
                        estacionamento: { id: estacionamentoId, nome: estacionamentoData.nome }
                    }));
                }
            } catch (err) {
                setError('Erro ao carregar dados do mensalista ou estacionamento: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [isEditing, id, estacionamentoId]);

    const handleChange = (e) => {
        const { id, value } = e.target;
        setMensalista(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        const dataToSend = {
            ...mensalista,
            id: mensalista.id ? Number(mensalista.id) : null,
            valorMensal: mensalista.valorMensal ? parseFloat(mensalista.valorMensal) : null,
            estacionamento: mensalista.estacionamento.id ? { id: Number(mensalista.estacionamento.id) } : null
        };

        try {
            if (isEditing) {
                await updateMensalista(dataToSend.id, dataToSend);
                alert('Mensalista atualizado com sucesso!');
            } else {
                await createMensalista(dataToSend);
                alert('Mensalista cadastrado com sucesso!');
            }
            if (estacionamentoId) {
                navigate(`/estacionamentos/${estacionamentoId}`);
            } else {
                navigate('/'); // Ou para uma lista geral de mensalistas
            }
        } catch (err) {
            console.error("Erro na operação:", err.response ? err.response.data : err.message);
            setError('Erro ao salvar mensalista: ' + (err.response?.data?.message || err.message || 'Erro desconhecido'));
        }
    };

    if (loading) {
        return <Card>Carregando formulário...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    return (
        <Card title={isEditing ? `Mensalista ${mensalista.nome || ''} - Editar` : "Registrar Mensalista"}>
            <form onSubmit={handleSubmit}>
                <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={mensalista.id}
                    onChange={handleChange}
                    placeholder="Insira o ID do mensalista"
                    readOnly={isEditing}
                    required
                />
                 {mensalista.estacionamento.nome && (
                    <InputGroup
                        label="Nome do Estacionamento:"
                        id="estacionamentoNome"
                        type="text"
                        value={mensalista.estacionamento.nome}
                        readOnly
                    />
                )}
                <InputGroup
                    label="Nome:"
                    id="nome"
                    type="text"
                    value={mensalista.nome}
                    onChange={handleChange}
                    placeholder="Nome completo do mensalista"
                    required
                />
                <InputGroup
                    label="CPF:"
                    id="cpf"
                    type="text"
                    value={mensalista.cpf}
                    onChange={handleChange}
                    placeholder="000.000.000-00"
                    required
                />
                <InputGroup
                    label="Telefone:"
                    id="telefone"
                    type="text"
                    value={mensalista.telefone}
                    onChange={handleChange}
                    placeholder="(00) 00000-0000"
                    required
                />
                <InputGroup
                    label="Placa do Veículo:"
                    id="placaVeiculo"
                    type="text"
                    value={mensalista.placaVeiculo}
                    onChange={handleChange}
                    placeholder="ABC1234"
                    maxLength="7"
                    required
                />
                <InputGroup
                    label="Vencimento do Contrato:"
                    id="vencimentoContrato"
                    type="date"
                    value={mensalista.vencimentoContrato}
                    onChange={handleChange}
                    required
                />
                <InputGroup
                    label="Valor Mensal:"
                    id="valorMensal"
                    type="number"
                    value={mensalista.valorMensal}
                    onChange={handleChange}
                    placeholder="0.00"
                    step="0.01"
                    required
                />

                <div className="button-group">
                    <Button type="submit" variant="primary">
                        {isEditing ? 'Confirmar Edição' : 'Registrar Mensalista'}
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                        Cancelar
                    </Button>
                </div>
            </form>
        </Card>
    );
}

export default MensalistaForm;