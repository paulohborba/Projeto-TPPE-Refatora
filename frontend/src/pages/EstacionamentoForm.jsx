import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/common/Card';
import InputGroup from '../components/common/InputGroup';
import Button from '../components/common/Button';
import { createEstacionamento, getEstacionamentoById, updateEstacionamento } from '../api/estacionamentos';

function EstacionamentoForm({ isEditing = false }) {
    const navigate = useNavigate();
    const { id } = useParams(); // Pega o ID da URL se estiver em modo de edição

    const [estacionamento, setEstacionamento] = useState({
        id: '', // Campo para o ID (manual)
        nome: '',
        endereco: '',
        complemento: '',
        cidade: '',
        cep: '',
        telefone: '',
        capacidade: ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (isEditing && id) {
            const fetchEstacionamento = async () => {
                try {
                    const data = await getEstacionamentoById(id);
                    // Certifique-se de que a capacidade é um número ou string vazia
                    setEstacionamento({
                        ...data,
                        capacidade: data.capacidade ? String(data.capacidade) : ''
                    });
                } catch (err) {
                    setError('Erro ao carregar dados do estacionamento: ' + (err.message || 'Erro desconhecido'));
                } finally {
                    setLoading(false);
                }
            };
            fetchEstacionamento();
        } else {
            setLoading(false); // Não está editando, então não há carregamento inicial
        }
    }, [isEditing, id]);

    const handleChange = (e) => {
        const { id, value } = e.target;
        setEstacionamento(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null); // Limpa erros anteriores

        // Converte capacidade para número antes de enviar
        const dataToSend = {
            ...estacionamento,
            capacidade: parseInt(estacionamento.capacidade, 10)
        };

        try {
            if (isEditing) {
                await updateEstacionamento(dataToSend.id, dataToSend); // Envia o ID manual para edição
                alert('Estacionamento atualizado com sucesso!');
            } else {
                await createEstacionamento(dataToSend); // Envia o ID manual para criação
                alert('Estacionamento cadastrado com sucesso!');
            }
            navigate('/'); // Volta para o dashboard de estacionamentos
        } catch (err) {
            console.error("Erro na operação:", err);
            setError('Erro ao salvar estacionamento: ' + (err.response?.data || err.message));
        }
    };

    if (loading) {
        return <Card>Carregando formulário...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    return (
        <Card title={isEditing ? `Estacionamento ${estacionamento.nome} - Editar` : "Registrar Estacionamento"}>
            <form onSubmit={handleSubmit}>
                <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={estacionamento.id}
                    onChange={handleChange}
                    placeholder="Insira o ID do estacionamento"
                    readOnly={isEditing} // Em edição, o ID pode ser lido mas não alterado diretamente
                                          // Se você REALMENTE precisa que o ID seja editável na URL ou manual, remova o readOnly
                                          // Mas geralmente, o ID de um item existente não é mudado pelo usuário
                />
                <InputGroup
                    label="Nome:"
                    id="nome"
                    type="text"
                    value={estacionamento.nome}
                    onChange={handleChange}
                    placeholder="Insira o nome"
                    required
                />
                <InputGroup
                    label="Endereço:"
                    id="endereco"
                    type="text"
                    value={estacionamento.endereco}
                    onChange={handleChange}
                    placeholder="Insira o endereço"
                    required
                />
                <InputGroup
                    label="Complemento:"
                    id="complemento"
                    type="text"
                    value={estacionamento.complemento}
                    onChange={handleChange}
                    placeholder="Insira o complemento (opcional)"
                />
                <InputGroup
                    label="Cidade:"
                    id="cidade"
                    type="text"
                    value={estacionamento.cidade}
                    onChange={handleChange}
                    placeholder="Insira a cidade"
                    required
                />
                <InputGroup
                    label="CEP:"
                    id="cep"
                    type="text"
                    value={estacionamento.cep}
                    onChange={handleChange}
                    placeholder="Insira o CEP"
                    required
                />
                <InputGroup
                    label="Telefone:"
                    id="telefone"
                    type="text"
                    value={estacionamento.telefone}
                    onChange={handleChange}
                    placeholder="Insira o telefone"
                    required
                />
                <InputGroup
                    label="Capacidade:"
                    id="capacidade"
                    type="number" // Tipo number para capacidade
                    value={estacionamento.capacidade}
                    onChange={handleChange}
                    placeholder="Insira a capacidade"
                    required
                />

                <div className="button-group">
                    <Button type="submit" variant="primary">
                        {isEditing ? 'Confirmar Edição' : 'Enviar'}
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                        Cancelar
                    </Button>
                </div>
            </form>
        </Card>
    );
}

export default EstacionamentoForm;