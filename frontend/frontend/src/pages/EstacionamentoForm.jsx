import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/common/Card';
import InputGroup from '../components/common/InputGroup';
import Button from '../components/common/Button';
import { createEstacionamento, getEstacionamentoById, updateEstacionamento } from '../api/estacionamentos';

function EstacionamentoForm({ isEditing = false }) {
    const navigate = useNavigate();
    const { id } = useParams();

    const [estacionamento, setEstacionamento] = useState({
        id: '',
        nome: '',
        endereco: '',
        complemento: '',
        cidade: '',
        cep: '',
        telefone: '',
        capacidade: '',
        horaAbertura: '',
        horaFechamento: ''
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (isEditing && id) {
            const fetchEstacionamento = async () => {
                try {
                    const data = await getEstacionamentoById(id);
                    setEstacionamento({
                        ...data,
                        capacidade: data.capacidade ? String(data.capacidade) : '',
                        horaAbertura: data.horaAbertura || '',
                        horaFechamento: data.horaFechamento || ''
                    });
                } catch (err) {
                    setError('Erro ao carregar dados do estacionamento: ' + (err.message || 'Erro desconhecido'));
                } finally {
                    setLoading(false);
                }
            };
            fetchEstacionamento();
        } else {
            setLoading(false);
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
        setError(null);

        // Crie um objeto com os dados base
        let dataToSend = {
            ...estacionamento,
            capacidade: parseInt(estacionamento.capacidade, 10)
        };

        try {
            if (isEditing) {
                // No modo de edição, o ID é necessário
                await updateEstacionamento(dataToSend.id, dataToSend);
                alert('Estacionamento atualizado com sucesso!');
            } else {
                // NO MODO DE CRIAÇÃO, REMOVA O ID DO OBJETO A SER ENVIADO
                // Isso permite que o banco de dados gere um ID automaticamente.
                const { id, ...newData } = dataToSend;
                await createEstacionamento(newData);
                alert('Estacionamento cadastrado com sucesso!');
            }
            navigate('/'); // Volta para o dashboard de estacionamentos
        } catch (err) {
            console.error("Erro na operação:", err);
            // Melhora a mensagem de erro para o usuário
            setError('Erro ao salvar estacionamento: ' + (err.response?.data?.message || err.message || 'Erro desconhecido.'));
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
                {/* CAMPO ID: Condicionalmente visível e desabilitado */}
                <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={estacionamento.id}
                    onChange={handleChange}
                    placeholder={isEditing ? "ID do estacionamento" : "Gerado automaticamente"}
                    readOnly={!isEditing} // Somente leitura se NÃO estiver editando
                    disabled={!isEditing} // Desabilita o input se NÃO estiver editando
                    style={{ display: isEditing ? 'block' : 'none' }} // Esconde o campo ID se não estiver editando
                />
                {/* Você pode preferir não esconder, apenas desabilitar, removendo a linha acima 'style' */}
                {/* Se optar por não esconder, apenas desabilitar, a linha ficaria assim: */}
                {/* <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={estacionamento.id}
                    onChange={handleChange}
                    placeholder={isEditing ? "ID do estacionamento" : "Gerado automaticamente"}
                    readOnly={true} // Sempre somente leitura
                    disabled={!isEditing} // Desabilita apenas na criação
                /> */}


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
                    type="number"
                    value={estacionamento.capacidade}
                    onChange={handleChange}
                    placeholder="Insira a capacidade"
                    required
                />
                <InputGroup
                    label="Hora de Abertura:"
                    id="horaAbertura"
                    type="time"
                    value={estacionamento.horaAbertura}
                    onChange={handleChange}
                    required
                />
                <InputGroup
                    label="Hora de Fechamento:"
                    id="horaFechamento"
                    type="time"
                    value={estacionamento.horaFechamento}
                    onChange={handleChange}
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