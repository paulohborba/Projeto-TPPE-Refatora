import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Card from '../components/common/Card';
import InputGroup from '../components/common/InputGroup';
import Button from '../components/common/Button';
import { createVeiculo, getVeiculoById, updateVeiculo } from '../api/veiculos';
import { getEstacionamentoById } from '../api/estacionamentos'; // Para obter o nome do estacionamento

function VeiculoForm({ isEditing = false }) {
    const navigate = useNavigate();
    const { id, estacionamentoId } = useParams(); // Pega ID do veículo e, se houver, estacionamentoId da URL

    const [veiculo, setVeiculo] = useState({
        id: '', // ID manual
        placa: '',
        marca: '',
        modelo: '',
        cor: '',
        tipoAcesso: '', // Ex: 'HORISTA', 'EVENTO', 'MENSALISTA'
        horaEntrada: '',
        dataEntrada: '',
        horaSaida: '',
        dataSaida: '',
        valorCobrado: '',
        estacionamento: { id: estacionamentoId || '', nome: '' } // Associado ao Estacionamento
    });
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                if (isEditing && id) {
                    const data = await getVeiculoById(id);
                    setVeiculo({
                        ...data,
                        id: String(data.id), // Garante que o ID é string para o input
                        valorCobrado: data.valorCobrado ? String(data.valorCobrado) : '',
                        // Formatar datas e horas para os campos de input type="date" e type="time"
                        dataEntrada: data.dataEntrada ? data.dataEntrada.split('T')[0] : '', // "YYYY-MM-DD"
                        horaEntrada: data.dataEntrada ? data.dataEntrada.split('T')[1]?.substring(0, 5) : '', // "HH:MM"
                        dataSaida: data.dataSaida ? data.dataSaida.split('T')[0] : '',
                        horaSaida: data.dataSaida ? data.dataSaida.split('T')[1]?.substring(0, 5) : '',
                    });
                } else if (estacionamentoId) {
                    // Se estamos adicionando e temos um estacionamentoId na URL, busca o nome
                    const estacionamentoData = await getEstacionamentoById(estacionamentoId);
                    setVeiculo(prev => ({
                        ...prev,
                        estacionamento: { id: estacionamentoId, nome: estacionamentoData.nome }
                    }));
                }
            } catch (err) {
                setError('Erro ao carregar dados do veículo ou estacionamento: ' + (err.message || 'Erro desconhecido'));
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, [isEditing, id, estacionamentoId]);

    const handleChange = (e) => {
        const { id, value } = e.target;
        setVeiculo(prev => ({
            ...prev,
            [id]: value
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setError(null);

        // Combina data e hora para os campos de entrada/saída (se existirem)
        // O backend espera um LocalDateTime ou String no formato ISO 8601
        const dataToSend = {
            ...veiculo,
            id: veiculo.id ? Number(veiculo.id) : null, // Converte ID para número
            valorCobrado: veiculo.valorCobrado ? parseFloat(veiculo.valorCobrado) : null,
            // Construir LocalDateTime strings no formato esperado pelo backend (ISO 8601)
            dataEntrada: veiculo.dataEntrada && veiculo.horaEntrada ? `${veiculo.dataEntrada}T${veiculo.horaEntrada}:00` : null,
            dataSaida: veiculo.dataSaida && veiculo.horaSaida ? `${veiculo.dataSaida}T${veiculo.horaSaida}:00` : null,
            // Envia apenas o ID do estacionamento, não o objeto completo, se o backend espera isso
            estacionamento: veiculo.estacionamento.id ? { id: Number(veiculo.estacionamento.id) } : null
        };

        // Remover os campos temporários de data/hora que não são enviados ao backend
        delete dataToSend.horaEntrada;
        delete dataToSend.dataSaida;
        delete dataToSend.horaSaida;


        try {
            if (isEditing) {
                await updateVeiculo(dataToSend.id, dataToSend);
                alert('Veículo atualizado com sucesso!');
            } else {
                await createVeiculo(dataToSend);
                alert('Veículo registrado com sucesso!');
            }
            // Navega de volta para os detalhes do estacionamento ou lista de veículos
            if (estacionamentoId) {
                navigate(`/estacionamentos/${estacionamentoId}`);
            } else {
                navigate('/'); // Ou para uma lista geral de veículos se houver
            }
        } catch (err) {
            console.error("Erro na operação:", err.response ? err.response.data : err.message);
            setError('Erro ao salvar veículo: ' + (err.response?.data?.message || err.message || 'Erro desconhecido'));
        }
    };

    if (loading) {
        return <Card>Carregando formulário...</Card>;
    }

    if (error) {
        return <Card><p style={{ color: 'red' }}>{error}</p></Card>;
    }

    return (
        <Card title={isEditing ? `Veículo ${veiculo.placa || ''} - Editar` : "Registrar Veículo"}>
            <form onSubmit={handleSubmit}>
                <InputGroup
                    label="ID:"
                    id="id"
                    type="text"
                    value={veiculo.id}
                    onChange={handleChange}
                    placeholder="Insira o ID do veículo/acesso"
                    readOnly={isEditing} // ID editável apenas na criação
                    required
                />
                {veiculo.estacionamento.nome && (
                    <InputGroup
                        label="Nome do Estacionamento:"
                        id="estacionamentoNome"
                        type="text"
                        value={veiculo.estacionamento.nome}
                        readOnly
                    />
                )}
                <InputGroup
                    label="Placa:"
                    id="placa"
                    type="text"
                    value={veiculo.placa}
                    onChange={handleChange}
                    placeholder="Insira a placa do veículo"
                    maxLength="7" // Placas comuns (Mercosul tem 7, antigas 7)
                    required
                />
                <InputGroup
                    label="Marca:"
                    id="marca"
                    type="text"
                    value={veiculo.marca}
                    onChange={handleChange}
                    placeholder="Insira a marca"
                    required
                />
                <InputGroup
                    label="Modelo:"
                    id="modelo"
                    type="text"
                    value={veiculo.modelo}
                    onChange={handleChange}
                    placeholder="Insira o modelo"
                    required
                />
                <InputGroup
                    label="Cor:"
                    id="cor"
                    type="text"
                    value={veiculo.cor}
                    onChange={handleChange}
                    placeholder="Insira a cor"
                    required
                />

                <InputGroup
                    label="Tipo de Acesso:"
                    id="tipoAcesso"
                    type="text" // Poderia ser um select com opções HORISTA, MENSALISTA, EVENTO
                    value={veiculo.tipoAcesso}
                    onChange={handleChange}
                    placeholder="Ex: HORISTA, MENSALISTA, EVENTO"
                    required
                />

                <div className="form-row-date-time">
                    <InputGroup
                        label="Data de Entrada:"
                        id="dataEntrada"
                        type="date"
                        value={veiculo.dataEntrada}
                        onChange={handleChange}
                        required
                    />
                    <InputGroup
                        label="Hora de Entrada:"
                        id="horaEntrada"
                        type="time"
                        value={veiculo.horaEntrada}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-row-date-time">
                    <InputGroup
                        label="Data de Saída:"
                        id="dataSaida"
                        type="date"
                        value={veiculo.dataSaida}
                        onChange={handleChange}
                        required
                    />
                    <InputGroup
                        label="Hora de Saída:"
                        id="horaSaida"
                        type="time"
                        value={veiculo.horaSaida}
                        onChange={handleChange}
                        required
                    />
                </div>
                
                <InputGroup
                    label="Valor Cobrado:"
                    id="valorCobrado"
                    type="number" // Tipo number para valores monetários
                    value={veiculo.valorCobrado}
                    onChange={handleChange}
                    placeholder="0.00"
                    step="0.01" // Permite valores decimais
                    required
                />

                <div className="button-group">
                    <Button type="submit" variant="primary">
                        {isEditing ? 'Confirmar Edição' : 'Registrar Veículo'}
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => navigate(-1)}>
                        Cancelar
                    </Button>
                </div>
            </form>
        </Card>
    );
}

export default VeiculoForm;